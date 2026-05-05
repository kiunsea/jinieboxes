package com.omnibuscode.logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.checkerframework.checker.units.qual.s;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.databind.JsonNode;
import com.omnibuscode.base.EnvSYS;
import com.omnibuscode.base.SafeProps;
import com.omnibuscode.dao.GcpTokenDataAccessObject;
import com.omnibuscode.dao.UserDataAccessObject;
import com.omnibuscode.util.GoogleAuthUtil;
import com.omnibuscode.util.JSONObjectExt;
import com.omnibuscode.util.JinieboxUtil;
import com.omnibuscode.utils.JSONUtil;
import com.omnibuscode.utils.PropertiesUtil;

import jakarta.servlet.ServletException;

/**
 * Google Cloud Platform 에 접속하여 데이터를 제어하기 위한 클래스 (클래스 내부에서 REST API 이용)
 * @author KIUNSEA
 *
 */
public class GcpService {

    /**
     * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
     */
//  private Log log = LogUtil.getLog(BoxServlet.class);
    private Logger log = LogManager.getLogger(GcpService.class);

    /**
     *  구글 인증서버로부터 전달받은 code를 이용하여 access_token 과 refresh_token 을 발급 받고 DB에 저장한다.
     *  
     * @param seqUser
     * @param code
     * @return
     * @throws Exception
     */
    public JSONObjectExt restoreTokensWithCode(String seqUser, String code) throws Exception {
        
        GoogleAuthUtil ga = new GoogleAuthUtil();
        JsonNode jn = ga.receiveTokensWithCode(code);
        
        JSONObjectExt resJson = null;
        if (jn != null) {
            String accessToken = jn.get("access_token").asText();
            String refreshToken = jn.get("refresh_token").asText();
            resJson = this.saveTokens(seqUser, accessToken, refreshToken);
        } else {
            resJson = new JSONObjectExt();
            resJson.put("result", EnvSYS.RESCODE_FAIL);
            resJson.put("msg", "구글 토큰 발급에 실패하였습니다.");
        }

        return resJson;
    }
    
    /**
     * access token 과 refresh token 을 db에 저장하고 처리 결과 메세지를 반환
     * 
     * @param seqUser
     * @param accessToken
     * @param refreshToken
     * @return 결과메세지
     * @throws Exception
     */
    private JSONObjectExt saveTokens(String seqUser, String accessToken, String refreshToken) throws Exception {

        JSONObjectExt resJson = new JSONObjectExt();
        String resRst = EnvSYS.RESCODE_FAIL;
        String resMsg = EnvSYS.RESMSG_FAIL;
        
        UserDataAccessObject uDao = new UserDataAccessObject();
        
        if (accessToken != null) {

            GcpTokenDataAccessObject gcpTokenDao = new GcpTokenDataAccessObject();

            // access token 저장
            String expiryTime = null; // 유효시간
            long currTime = System.currentTimeMillis();
            long validTime = SafeProps.getLong("GOOGLE_ACCESS_VALID_TIME", 3500000L);
            expiryTime = Long.toString(currTime + validTime); // 유효시간
            gcpTokenDao.saveAccessToken(seqUser, accessToken, expiryTime);

            // 사용자의 구글계정 확인후 요청 처리
            String beforeGmail = uDao.getGoogleid(seqUser);
            String afterGmail = this.getProfile(accessToken);
            if (afterGmail != null) {
                resJson.put("gmail", afterGmail);
                String expiryDate = null;
                expiryDate = JinieboxUtil.getNextdayString(JinieboxUtil.getTodayString(), SafeProps.getInt("GOOGLE_REFRESH_VALID_DAY", 6));

                // google 계정(gmail) 저장
                uDao.setGoogleid(seqUser, afterGmail);
                // refresh token 저장
                gcpTokenDao.saveRefreshToken(seqUser, refreshToken, expiryDate);
                resMsg = "구글 계정과 토큰을 저장하였습니다.\\n이제 구글 서비스를 이용할 수 있습니다.";
                resRst = EnvSYS.RESCODE_SUCC;

                if (!beforeGmail.equals(afterGmail)) {
                    // TODO 구글 계정 변경에 대한 알림을 출력하여 사용자로부터 확인
                    // TODO 발급받은 refresh token 은 세션에 저장하여 사용자 확인후 재사용
                    resMsg = "구글 계정을 변경하여 토큰을 저장하였습니다.";
                }
            } else {
                // TODO 구글 계정 조회 오류 알림
            }
        }
        
        resJson.put("result", resRst);
        resJson.put("msg", resMsg);
        return resJson;
    }
    
    /**
     * 유효한 Access Token 을 반환한다.
     * 
     * @param seqUser
     * @return access token 이 null 인 경우엔 사용자로부터 토큰 갱신을 위한 사용자 인증 절차를 수행하게 해야 한다.
     * @throws Exception 
     */
    public String getAccessToken(String seqUser) throws Exception {
        
        JSONObject chkRst = this.checkTokensExpiration(seqUser);
        Object rstObj = chkRst != null ? chkRst.get("result") : null;
        Object tokensObj = chkRst != null ? chkRst.get("tokensJson") : null;
        JSONObject tokensJson = (tokensObj != null) ? (JSONObject) tokensObj : null;

        if (rstObj != null && tokensJson != null) {
            int rst = Integer.parseInt(rstObj.toString());
            if (rst <= 0) { // 0:유효함
                return tokensJson.get("access_token").toString();
            } else if (rst == 1) { // 1:access 갱신필요
                GoogleAuthUtil ga = new GoogleAuthUtil();
                JsonNode jn = ga.receiveAcTokenWithReToken(tokensJson.get("refresh_token").toString());
                if (jn != null) {
                    String accessToken = jn.get("access_token").asText();
                    String expiresIn = jn.get("expires_in").asText();
                    
                    //CHECK 구글에서 반환된 expires_in 은 3599초는 약 1시간후에 만료되는데 현재 시간을 기준으로 DB에 저장해야 하므로 다음으로 변환 처리한다
                    String expiryTime = null; // 유효시간
                    long currTime = System.currentTimeMillis();
                    long validTime = SafeProps.getLong("GOOGLE_ACCESS_VALID_TIME", 3500000L);
                    expiryTime = Long.toString(currTime + validTime); // 유효시간
                    
                    if (accessToken != null) {
                        GcpTokenDataAccessObject gcpTokenDao = new GcpTokenDataAccessObject();
                        gcpTokenDao.saveAccessToken(seqUser, accessToken, expiryTime);
                        return accessToken;
                    }
                }
            } else if (rst == 2) { // 2:refresh 갱신필요
                // TODO refresh token 갱신 필요를 알려야 한다 (이후 사용자 동의와 함께 구글 인증 절차 진행)
                log.error("refresh token 갱신 필요!!");
            }
        }
        
        //TODO null 을 반환하는 경우는 사용자로부터 토큰 갱신을 위한 사용자 인증 절차를 수행하게 하기 위해서이다.
        return null;
    }
    
    /**
     * 구글 인증서버로부터 사용자의 구글 계정 아이디(gmail)를 조회
     * @param access_Token
     * @return
     * @throws ServletException
     * @throws IOException
     * @throws ParseException
     */
    private String getProfile(String access_Token) throws ServletException, IOException, ParseException {
        URL url = new URL(SafeProps.getString("GOOGLE_APIS_PROFILE", "https://gmail.googleapis.com/gmail/v1/users/me/profile"));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        conn.setRequestProperty("Authorization", "Bearer " + access_Token);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        BufferedReader brin = null;
        try {
            int responseCode = conn.getResponseCode();

            // 응답 처리
            if (responseCode == 200) {
                // 성공 inputStream으로 응답 데이터 처리
                InputStream inputStream = conn.getInputStream();
                brin = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                StringBuffer resStrBuff = new StringBuffer();
                String strIn = null;
                while ((strIn = brin.readLine()) != null) {
                    resStrBuff.append(strIn + System.getProperty("line.separator"));
                }

                if (resStrBuff.length() > 0) {
                    JsonNode root = JSONUtil.parseJsonNode(resStrBuff.toString());
                    String emailAddr = root.get("emailAddress").asText();
                    log.debug("Google emailAddr - " + emailAddr);
                    if (emailAddr != null) {
                        return emailAddr;
                    }
                }
            } else {
                // TODO 실패 에러 처리
            }

        } catch (IOException ioe) {
            log.error("접속이 불가합니다 - " + ioe.toString());
        } finally {
            if (brin != null) {
                brin.close();
                brin = null;
            }
            if (conn != null) {
                conn.disconnect();
                conn = null;
            }
            url = null;
        }

        return null;
    }

    /**
     * refresh token 과 access token 의 만료 시간을 체크한다
     * 
     * @param seqUser
     * @return resultJson (json object)<br/>
     *   검사 결과 : resultJson.result -> int<=0:유효함, int=1:access갱신필요, int=2:refresh갱신필요<br/>
     *   검사한 값 : resultJson.tokensJson -> refresh_token, refresh_expiry, access_token, access_expiry
     * @throws Exception 
     */
    public JSONObjectExt checkTokensExpiration(String seqUser) throws Exception {
        
        JSONObjectExt resultJson = new JSONObjectExt();
        
        GcpTokenDataAccessObject gcpTokenDao = new GcpTokenDataAccessObject();
        JSONObject tokensJson = gcpTokenDao.getTokens(seqUser);

        if (tokensJson != null) {
            
            resultJson.put("tokensJson", tokensJson);
            
            Object refreshExpObj = tokensJson.get("refresh_expiry_date");
            if (refreshExpObj == null) {
                resultJson.put("result", 2);
                return resultJson;
            } else {
                int refreshExpInt = Integer.parseInt(refreshExpObj.toString());
                int todayInt = Integer.parseInt(JinieboxUtil.getTodayString());
                if (refreshExpInt <= todayInt) {
                    resultJson.put("result", 2);
                    return resultJson;
                } else {
                    Object accessExpObj = tokensJson.get("access_expiry_time");
                    if (accessExpObj == null) {
                        resultJson.put("result", 1);
                        return resultJson;
                    } else {
                        long accessExpLong = Long.parseLong(accessExpObj.toString());
                        long currTimeLong = System.currentTimeMillis();
                        if (accessExpLong <= currTimeLong) {
                            resultJson.put("result", 1);
                        } else {
                            resultJson.put("result", 0);
                        }
                        return resultJson;
                    }
                }
            }
        }
        
        return null;
    }
    
}
