package com.omnibuscode.ctrl;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omnibuscode.auth.AuthInfo;
import com.omnibuscode.auth.AuthManager;
import com.omnibuscode.auth.AuthNaverInfo;
import com.omnibuscode.base.EnvSYS;
import com.omnibuscode.base.IntegrationGate;
import com.omnibuscode.base.SafeProps;
import com.omnibuscode.dao.AccountClovaDataAccessObject;
import com.omnibuscode.dao.UserDataAccessObject;
import com.omnibuscode.logic.ItemWebService;
import com.omnibuscode.util.NaverAccountUtil;
import com.omnibuscode.utils.ExceptionUtil;
import com.omnibuscode.utils.HttpURLConnectionUtil;
import com.omnibuscode.utils.JSONUtil;
import com.omnibuscode.utils.PropertiesUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 지니박스와 클로바를 연동하기 위한 클래스이다
 * @author KIUNSEA
 */
@WebServlet("/clova")
public class ClovaServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
     */
    private Logger log = LogManager.getLogger(ClovaServlet.class);

    public void init() {
        /* InitializeEnv 가 부팅 시 standalone/WAR 모드에 맞게 이미 설정 — 덮어쓰지 않음 */
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        doGet(req, res);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        log.debug("Clova Call~~~");

        if (!IntegrationGate.isClovaEnabled()) {
            IntegrationGate.writeDisabled(res, "Clova");
            return;
        }

        //클라이언트 정보 추적용 로깅(아이피 정보만 취할 수 있음)
        log.debug("getRemoteHost -- "+req.getRemoteHost());
        log.debug("getRemoteAddr -- "+req.getRemoteAddr());
        log.debug("getRemoteUser -- "+req.getRemoteUser());
        log.debug("getServerName -- "+req.getServerName());
        String userAgent = req.getHeader("User-Agent");
        log.debug("User-Agent: " + userAgent);
        
        String signatureStr = req.getHeader("SignatureCEK");
        byte[] body = this.getBody(req);
        
        String strJson = new String(body, "UTF-8");
        log.debug("Received JSON data: " + strJson);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(strJson);
        
        //요청 내용 확인용
        log.debug("SignatureCEK: " + signatureStr);
        log.debug("userId: " + root.get("context").get("System").get("user").get("userId"));
        log.debug("intent: " + root.get("request").get("intent").get("name"));
        log.debug("intent slots : ");
        JsonNode slotsTmp = root.get("request").get("intent").get("slots");
        Iterator<JsonNode> slotTmpIter = slotsTmp.iterator();
        JsonNode slotTmp = null;
        while (slotTmpIter.hasNext()) {
            slotTmp = (JsonNode) slotTmpIter.next();
            log.debug("  >> slot : " + slotTmp.get("name") + " - " + slotTmp.get("value"));
        }

//        // json 데이터로 요청하기 때문에 파라미터는 없다.
//        // request parameter 로깅
//        String pName = null;
//        Enumeration<String> enums = req.getParameterNames();
//        while (enums.hasMoreElements()) {
//            pName = enums.nextElement().toString();
//            log.debug("(param) " + pName + " - " + req.getParameter(pName));
//        }
        
        JSONObject resJson = new JSONObject();
        resJson.put("code", EnvSYS.RESCODE_FAIL);
        resJson.put("msg", "잘못된 요청입니다");
        
        try {
            if (this.validClovaPublicKey(signatureStr, body) && this.checkClovaRequest(root)) {
                JsonNode userIdJn = root.path("context").path("System").path("user").path("userId");
                if (userIdJn.isMissingNode()) {
                    resJson.put("code", EnvSYS.RESCODE_FAIL);
                    resJson.put("msg", "요청 정보에 클로바 사용자 정보가 없습니다");
                } else {
                    String clovaUserId = userIdJn.asText();
                    String seqUser = null;
                    UserDataAccessObject userDao = new UserDataAccessObject();
                    AccountClovaDataAccessObject acDao = new AccountClovaDataAccessObject();
                    try {
                        seqUser = acDao.getUserSeq(clovaUserId);
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error(ExceptionUtil.getExceptionInfo(e));
                    }
                    if (seqUser == null) {
                        // clova id 를 jiniebox 에 등록하기 위해 네이버 로그인 폼을 출력
                        try {
                            
                            String resJsonStr = this.getResTemplateForClova(
                                    "클로바를 지니박스에 연결합니다. 아래 로그인 링크를 클릭해 주세요.",
                                    this.getContentJboxRegForm(clovaUserId));
                            resJson = JSONUtil.parseJSONObject(resJsonStr);
                        } catch (ParseException e) {
                            log.error(ExceptionUtil.getExceptionInfo(e));
                        }
                    } else {
                        log.debug("클로바 요청 처리");
                      
                        //이미 인증된 사용자인 경우 세션 생성하여 로그인
                        JSONObject userJson = acDao.getUserByClovaid(clovaUserId);
                        AuthInfo authUserInfo = new AuthNaverInfo();
                        authUserInfo.setValidcode(EnvSYS.RESCODE_SUCC);
                        authUserInfo.setValidmsg("인증에 성공하였습니다");
                        AuthManager.getInstance().createUserSession(req, authUserInfo, userJson);
                        userDao.setVisited(userJson.get("seq").toString());
                        
                        String clovaIntent = root.path("request").path("intent").path("name").asText();
                        if ("IsItem".equals(clovaIntent)) {
                            JsonNode slots = root.get("request").get("intent").get("slots");
                            
                            List<JsonNode> itemNameList = new ArrayList<JsonNode>();
                            String itemName = null;
                            Iterator<JsonNode> slotIter = slots.iterator();
                            JsonNode slot = null;
                            if (slotIter.hasNext()) {
                                slot = (JsonNode) slotIter.next();
                                if ("ItemName".equals(slot.get("name").asText())) {
                                    itemNameList.add(slot.get("value"));

                                    itemName = slot.get("value").asText();
                                    String contentFindResult = this.getContentFindItems(seqUser, itemName);
                                    String resJsonStr = this.getResTemplateForClova(
											"'" + itemName + "' 는 총 다음의 보관함에 있습니다.", contentFindResult);
                                    resJson = JSONUtil.parseJSONObject(resJsonStr);
                                }
                            }
                        }
                    }
                }
            }
        } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | SignatureException
                | IOException e) {
            e.printStackTrace();
            log.debug(ExceptionUtil.getExceptionInfo(e));
        } catch (Exception e1) {
            e1.printStackTrace();
            log.debug(ExceptionUtil.getExceptionInfo(e1));
        }
        
        log.debug("res - " + resJson);

        res.setCharacterEncoding("UTF-8");
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Cache-Control", "no-cache");
        PrintWriter out = res.getWriter();
        out.println(resJson);
        out.flush();
        out.close();
    }
    
    /**
     * 아이템 조회 결과 Content Template 반환
     * @param seqUser
     * @param itemName
     * @return
     * @throws Exception
     */
    private String getContentFindItems(String seqUser, String itemName) throws Exception {
        
        JSONObject resContent = new JSONObject();
        resContent.put("subType", "Type4");
        resContent.put("type", "CardList");
        JSONObject noticeText = new JSONObject();
        noticeText.put("type", "string");
        noticeText.put("value", "각 보관함을 조회한 결과입니다.");
        resContent.put("noticeText", noticeText);
        resContent.put("cardList", new ArrayList());

        Map<String, JSONObject> boxSeqMap = new HashMap<String, JSONObject>(); //박스 목록 체크용
        JSONObject boxInfo = null;
        String seqBox = null;
        JSONObject item = null;
        
        List<JSONObject> items = new ItemWebService().getUserItems(seqUser, itemName);
        Iterator<JSONObject> itemIter = items.iterator();
        while (itemIter.hasNext()) {
            item = (JSONObject) itemIter.next();
            seqBox = item.get("b_seq").toString();
            
            if (!boxSeqMap.keySet().contains(seqBox)) {
                //새로운 박스 정보를 생성하여 저장
                boxInfo = new JSONObject();
                
                JSONObject contentProviderText = new JSONObject();
                contentProviderText.put("type", "string");
                contentProviderText.put("value", "지니박스");
                boxInfo.put("contentProviderText", contentProviderText);
                
                JSONObject title = new JSONObject();
                title.put("type", "string");
                title.put("value", item.get("b_name").toString());
                boxInfo.put("title", title);
                
                JSONObject linkUrl = new JSONObject();
                linkUrl.put("type", "url");
                linkUrl.put("value", PropertiesUtil.get("SERVICE_URL") + "/box_info.jsp?seq_box=" + seqBox);
                boxInfo.put("linkUrl", linkUrl);
                
                boxInfo.put("description", new ArrayList());
                
                List<JSONObject> boxList = (List<JSONObject>)resContent.get("cardList");
                boxList.add(boxInfo);
                boxSeqMap.put(seqBox, boxInfo);
            } else {
                boxInfo = boxSeqMap.get(seqBox);
            }
            
            JSONObject boxItem = new JSONObject();
            boxItem.put("type", "string");
            String itemInfo = "> "+item.get("i_name") 
                    + "\n    - 갯수:" + item.get("i_qty") 
                    + "\n    - 등록일:" + item.get("i_insert_date") 
                    + "\n    - 만료일:" + item.get("i_expiry_date");
            boxItem.put("value", itemInfo);
            
            ((List) boxInfo.get("description")).add(boxItem);
        }
        
        return resContent.toJSONString();
    }

    /**
     * clova id 를 jiniebox 에 등록하기 위한 content (regist card)
     * @return
     */
    private String getContentJboxRegForm(String clovaUserId) {
        
        String authCode = NaverAccountUtil.getInstance().createTempAuthCode(null, clovaUserId, null);
        // 계정 연결을 위해 네이버 아이디 로그인폼 링크가 있는 카드를 출력
        String serviceUrl = PropertiesUtil.get("SERVICE_URL");
        
        // response.card 이용 (성공)
        // https://developers.naver.com/console/clova/custom_ext/Develop/Guides/Build_Custom_Extension.md#ReturnCustomExtensionResponse
        StringBuffer contentTempate = new StringBuffer();
        contentTempate.append("    {");
        contentTempate.append("      \"type\": \"ImageText\",");
        contentTempate.append("      \"imageUrl\": {");
        contentTempate.append("        \"type\": \"url\",");
        contentTempate.append("        \"value\": \"\"");
        contentTempate.append("      },");
        contentTempate.append("      \"mainText\": {");
        contentTempate.append("        \"type\": \"string\",");
        contentTempate.append("        \"value\": \"클로바를 지니박스에 연결\"");
        contentTempate.append("      },");
        contentTempate.append("      \"referenceText\": {");
        contentTempate.append("        \"type\": \"string\",");
        contentTempate.append("        \"value\": \"네이버 아이디 로그인\"");
        contentTempate.append("      },");
        contentTempate.append("      \"referenceUrl\": {");
        contentTempate.append("        \"type\": \"url\",");
        contentTempate.append("        \"value\": \"" + serviceUrl + "/naver_login.jsp?cuid="+clovaUserId+"&ac=" + authCode + "\"");
        contentTempate.append("      },");
        contentTempate.append("      \"subTextList\": [");
        contentTempate.append("        {");
        contentTempate.append("          \"type\": \"string\",");
        contentTempate.append("          \"value\": \"클로바를 지니박스에 연결합니다.\n아래 로그인 링크를 클릭해 주세요.\"");
        contentTempate.append("        }");
        contentTempate.append("      ],");
        contentTempate.append("      \"thumbImageType\": {");
        contentTempate.append("        \"type\": \"string\",");
        contentTempate.append("        \"value\": \"지니박스\"");
        contentTempate.append("      },");
        contentTempate.append("      \"thumbImageUrl\": {");
        contentTempate.append("        \"type\": \"url\",");
        contentTempate.append("        \"value\": \"" + serviceUrl + "/res/web/images/jiniebox-baby-round.png\"");
        contentTempate.append("      }");
        contentTempate.append("    }");

        return contentTempate.toString();
    }
    
    /**
     * clova 에 응답할 데이터 템플릿
     * @param outputSpeech
     * @param contentTempate
     * @return
     */
    private String getResTemplateForClova(String outputSpeech, String contentTempate) {
        StringBuffer resJsonSb = new StringBuffer();
        resJsonSb.append("{");
        resJsonSb.append("  \"version\": \"0.1.0\",");
        resJsonSb.append("  \"sessionAttributes\": {},");
        resJsonSb.append("  \"response\": {");
        resJsonSb.append("    \"outputSpeech\": {");
        resJsonSb.append("      \"type\": \"SimpleSpeech\",");
        resJsonSb.append("      \"values\": {");
        resJsonSb.append("          \"type\": \"PlainText\",");
        resJsonSb.append("          \"lang\": \"ko\",");
        resJsonSb.append("          \"value\": \"" + outputSpeech + "\"");
        resJsonSb.append("      }");
        resJsonSb.append("    },");
		resJsonSb.append("    \"card\": " + contentTempate + ",");
        resJsonSb.append("    \"directives\": [],");
        resJsonSb.append("    \"shouldEndSession\": true");
        resJsonSb.append("  }");
        resJsonSb.append("}");

        return resJsonSb.toString();
    }
    
    /**
     * Clova 요청 메시지 검증<br/>
     * https://developers.naver.com/console/clova/custom_ext/Develop/Guides/Build_Custom_Extension.md#RequestMessageValidation
     * @param signatureStr
     * @param body
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    private boolean validClovaPublicKey(String signatureStr, byte[] body) throws IOException, NoSuchAlgorithmException,
            InvalidKeySpecException, InvalidKeyException, SignatureException {

        String publicKeyStr = HttpURLConnectionUtil.readUrlTextFile(SafeProps.getString("CLOVA_SIG_PUBKEY_URI", "https://clova.ai/.well-known/signature-public-key.pem"));
        publicKeyStr = publicKeyStr.replaceAll("\\n", "").replaceAll("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll("-----END PUBLIC KEY-----", "");
        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyStr));

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(pubKey);
        sig.update(body);

        byte[] signature = Base64.getDecoder().decode(signatureStr);
        return sig.verify(signature);
    }
    
    /**
     * 클로바의 정상적인 요청인지 간이 검증<br/>
     * valid 와 다르게 요청 본문 내용을 검사한다
     * @param rootReqData
     * @return
     */
    private boolean checkClovaRequest(JsonNode rootReqData) {

        String clovaId = SafeProps.getString("CLOVA_EXTENSION_ID");
        
        JsonNode appIdJn = rootReqData.path("context")
                .path("System")
                .path("application")
                .path("applicationId");
        String appId = appIdJn.isMissingNode() ? null : appIdJn.asText();
        
        JsonNode extIdJn = rootReqData.path("request")
                .path("extensionId");
        String extId = extIdJn.isMissingNode() ? null : extIdJn.asText();

        if (appId != null && extId != null && clovaId.equals(appId) && clovaId.equals(extId)) {
            return true;
        }

        return false;
    }

    /**
     * POST 요청이나 PUT 요청 등에서 본문 데이터를 반환
     * @param request
     * @return
     */
    private byte[] getBody(HttpServletRequest request) {
        try {
            // 요청 본문을 InputStream으로 읽기
            InputStream inputStream = request.getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            // 버퍼 크기 설정
            byte[] data = new byte[1024];
            int bytesRead;

            // InputStream에서 데이터를 읽고 ByteArrayOutputStream에 저장
            while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, bytesRead);
            }

            // ByteArrayOutputStream을 byte 배열로 변환
            return buffer.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null; // 에러 발생 시 null 반환
        }
    }
    
    /**
     * text 문서의 url 에서 문서 내용을 읽어 반환한다.
     * @param urlString
     * @return 문서내용 String
     * @throws IOException
     */
    private String readUrlTextFile(String urlString) throws IOException {

        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);

            // URL 연결을 열고 HTTP GET 요청 수행
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // 응답 코드 확인
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // InputStreamReader를 통해 URL에서 데이터를 읽음
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder content = new StringBuilder();
                String inputLine;

                // 한 줄씩 읽어서 StringBuilder에 추가
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine).append("\n");
                }

                // 스트림 닫기
                in.close();

                // 읽어온 내용을 String에 저장
                String fileContent = content.toString();

                // 출력 (읽어온 내용)
                return fileContent;

            } else {
                log.debug("HTTP 응답 코드: " + responseCode);
                return null;
            }

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
