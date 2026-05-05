package com.omnibuscode.ctrl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Iterator;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.databind.JsonNode;
import com.omnibuscode.auth.AuthInfo;
import com.omnibuscode.auth.AuthManager;
import com.omnibuscode.base.EnvSYS;
import com.omnibuscode.utils.HttpURLConnectionUtil;
import com.omnibuscode.utils.JSONUtil;
import com.omnibuscode.utils.PropertiesUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 외부 서비스(REST API)에 접근하여 데이터를 가져오기 위한 클래스
 * > 바코드 상품 조회
 * @author KIUNSEA
 *
 */
@WebServlet("/extsvc")
public class ExtsvcServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
     */
    private Logger log = LogManager.getLogger(ExtsvcServlet.class);

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // request parameter 로깅
        String pName = null;
        Enumeration<String> enums = req.getParameterNames();
        while (enums.hasMoreElements()) {
            pName = enums.nextElement().toString();
            log.debug("(param) " + pName + " - " + req.getParameter(pName));
        }

        doGet(req, res);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        JSONObject resObj = null;

        String cmdHttp = req.getParameter("cmd");

        try {
            if (AuthManager.getInstance().hasUserAuthed(req)) {
                if (cmdHttp != null) {
                    if ("getProductdName".equals(cmdHttp)) { // 바코드로 상품 조회
                        resObj = this.getProductdName(req);
                    }
                } else {
                    resObj.put("code", EnvSYS.RESCODE_FAIL);
                    resObj.put("msg", EnvSYS.RESMSG_INVREQ);
                }
            } else {
                resObj = new JSONObject();
                AuthInfo ai = AuthManager.getInstance().getUserAuthInfo(req);
                if (ai != null) {
                    resObj.put("code", ai.getValidcode());
                    resObj.put("msg", ai.getValidmsg());
                } else {
                    resObj.put("code", EnvSYS.RESCODE_FAIL);
                    resObj.put("msg", EnvSYS.RESMSG_FAIL);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        if (resObj == null)
            resObj = new JSONObject();

        log.debug("res - " + resObj);

        res.setCharacterEncoding("UTF-8");
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Cache-Control", "no-cache");
        PrintWriter out = res.getWriter();
        out.println(resObj);
        out.flush();
        out.close();
    }

    /**
     * [식품의약품안전처 유통바코드 OpenAPI] 와 [대한상공회의소 유통물류진흥원에서 상품조회] 를 이용하여 상품 정보를 추출한다.
     * @param req
     * @return
     * @throws IOException
     * @throws ParseException
     */
    private JSONObject getProductdName(HttpServletRequest req) throws IOException, ParseException {

        JSONObject resObj = new JSONObject();
        String productName = null;

        String brcd = req.getParameter("barcode"); // ex> 8809360172172 :
        
        String resStr = null;
        Object rstCd = null;
        JsonNode root = null;
        
        try {
            resStr = HttpURLConnectionUtil
                    .requestPost(PropertiesUtil.get("EXT_API_FOODSAFETY").replace("[BARCODE]", brcd), null, null);

            root = JSONUtil.parseJsonNode(resStr);
            rstCd = root.get("I2570").get("RESULT").get("CODE");
            log.debug("foodsafetykorea search result : " + root.get("I2570").get("RESULT").get("MSG").toString());
        } catch (Exception e) {
            log.error("foodsafetykorea search failed : " + e);
        }

        if (rstCd != null && rstCd.toString().indexOf("INFO-000") > -1) {
            /**
             * 식품의약품안전처 조회
             */
            JsonNode jnRow = root.get("I2570").get("row");
            if (jnRow.isArray() && jnRow.size() == 1) {
                productName = jnRow.get(0).get("PRDT_NM").asText();
            } else if (jnRow.size() > 1) {
                // TODO 조회 결과가 너무 많음 에러
            } else {
                // TODO 조회 결과 없음 에러
            }
        } else {
            /**
             * 대한상공회의소 유통물류진흥원 조회
             */
            String url_dlpa = PropertiesUtil.get("EXT_API_KOREANNET").replace("[BARCODE]", brcd);

            Document doc = Jsoup.connect(url_dlpa).get();
            Elements elems = doc.select("body>div>div.pv_title>h3");
            Iterator<Element> itr = elems.iterator();
            while (itr.hasNext()) {
                Element elem = itr.next();
                if (elem.hasText()) {
                    productName = elem.text();
                } else {
                    // TODO 조회 결과 없음 에러
                }
            }
        }

        if (productName == null) {
            resObj.put("code", EnvSYS.RESCODE_FAIL);
            resObj.put("msg", "상품 검색에 실패하였습니다");
        } else {
            resObj.put("item_name", productName);
            resObj.put("code", EnvSYS.RESCODE_SUCC);
        }

        return resObj;
    }
}
