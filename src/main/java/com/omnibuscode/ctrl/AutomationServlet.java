package com.omnibuscode.ctrl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.omnibuscode.auth.AuthInfo;
import com.omnibuscode.auth.AuthManager;
import com.omnibuscode.base.EnvSYS;
import com.omnibuscode.base.UserSession;
import com.omnibuscode.dao.AutoBarcodeDataAccessObject;
import com.omnibuscode.dao.AutoKeywordDataAccessObject;
import com.omnibuscode.dao.ItemDataAccessObject;
import com.omnibuscode.logic.AutomationService;
import com.omnibuscode.logic.ItemWebService;

/**
 * @author KIUNSEA
 *
 */
@WebServlet("/auto")
public class AutomationServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
     */
    private Logger log = LogManager.getLogger(AutomationServlet.class);

    public void init() {
        ;
    }

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

        JSONObject resJson = null;

        String cmdHttp = req.getParameter("cmd");

        try {
            if (AuthManager.getInstance().hasUserAuthed(req)) {
                if (cmdHttp != null) {
                    if ("addRule".equals(cmdHttp)) { // 자동 분류 규칙을 추가
                        resJson = this.addRule(req);
                    } else if ("updateRule".equals(cmdHttp)) { // 자동 분류 규칙을 수정(신규 추가 포함)
                        resJson = this.updateRule(req);
                    } else if ("deleteRule".equals(cmdHttp)) { // 자동 분류 규칙을 삭제
                        resJson = this.deleteRule(req);
                    } else if ("runNow".equals(cmdHttp)) { // 자동 분류 작업을 지금 실행
                        resJson = this.runNow(req);
                    }
                } else {
                    resJson.put("code", EnvSYS.RESCODE_FAIL);
                    resJson.put("msg", EnvSYS.RESMSG_INVREQ);
                }
            } else {
                resJson = new JSONObject();
                AuthInfo uai = AuthManager.getInstance().getUserAuthInfo(req);
                if (uai != null) {
                    resJson.put("code", uai.getValidcode());
                    resJson.put("msg", uai.getValidmsg());
                } else {
                    resJson.put("code", EnvSYS.RESCODE_FAIL);
                    resJson.put("msg", EnvSYS.RESMSG_USERSESSION_FAIL);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (resJson == null)
            resJson = new JSONObject();
        /**
         * (Bixby)html 페이지간의 redirect 이동을 위한 parameter 전달용
         */
        resJson.put("buid", req.getParameter("buid"));
        resJson.put("authkey", AuthManager.getInstance().getRandomAuthKey(req.getParameter("buid")));

//        log.debug("res - " + resJson);

        res.setCharacterEncoding("UTF-8");
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Cache-Control", "no-cache");
        PrintWriter out = res.getWriter();
        out.println(resJson);
        out.flush();
        out.close();
    }

    private JSONObject addRule(HttpServletRequest req) throws Exception {
        JSONObject resObj = new JSONObject();

        Object ruletypeObj = req.getParameter("ruletype");
        if (ruletypeObj != null) {
            String ruletype = ruletypeObj.toString();
            if ("keyword".equals(ruletype)) {
                resObj = this.addKeywordRule(req);
            } else if ("barcode".equals(ruletype)) {
                resObj = this.addBarcodeRule(req);
            }
        } else {
            resObj.put("code", EnvSYS.RESCODE_FAIL);
            resObj.put("msg", "뷴류 규칙 형태가 지정되지 않았습니다.");
        }

        return resObj;
    }
    
    private JSONObject addKeywordRule(HttpServletRequest req) throws Exception {
        JSONObject resObj = new JSONObject();

        UserSession us = AuthManager.getInstance().getUserSession(req);
        AutoKeywordDataAccessObject autokeyDao = new AutoKeywordDataAccessObject();

        String keywords = req.getParameter("keywords"); // 키워드 목록
        String keyoper = req.getParameter("keyoper");
        String matchcnt = req.getParameter("matchcnt");
        String seqBox = req.getParameter("seq_box");
        String status = req.getParameter("status");

        int intKeyoper = autokeyDao.NUM_OR_OPERATION;
        if ("AND".equals(keyoper.toUpperCase())) {
            intKeyoper = autokeyDao.NUM_AND_OPERATION;
        }
        int intMatchcnt = (matchcnt != null) ? Integer.parseInt(matchcnt) : 0;
        int intStatus = (status != null) ? Integer.parseInt(status) : 1;

        int seqRule = autokeyDao.add(keywords, intKeyoper, intMatchcnt, seqBox, intStatus, us.getSeqDefstore());
        JSONObject ruleJson = autokeyDao.getRule(Integer.toString(seqRule));
        resObj.put("rule", ruleJson);

        resObj.put("code", EnvSYS.RESCODE_SUCC);
        resObj.put("msg", "분류 규칙을 저장하였습니다.");

        return resObj;
    }
    
    private JSONObject addBarcodeRule(HttpServletRequest req) throws Exception {
        JSONObject resObj = new JSONObject();

        UserSession us = AuthManager.getInstance().getUserSession(req);
        AutoBarcodeDataAccessObject autobarDao = new AutoBarcodeDataAccessObject();

        String barcode = req.getParameter("barcode");
        autobarDao.add(barcode, us.getSeqDefstore());

        resObj.put("code", EnvSYS.RESCODE_SUCC);
        resObj.put("msg", "분류 규칙을 저장하였습니다.");

        return resObj;
    }
    
    private JSONObject updateRule(HttpServletRequest req) throws Exception {
        JSONObject resObj = new JSONObject();
        
        String seqRule = req.getParameter("seq_rule");
        AutoKeywordDataAccessObject autokeyDao = new AutoKeywordDataAccessObject();
        
        boolean newRule = true;
        if (seqRule != null) {
            JSONObject rule = autokeyDao.getRule(seqRule);
            if (rule != null) {
                String keywords = req.getParameter("keywords"); //키워드 목록
                String keyoper = req.getParameter("keyoper");
                String matchcnt = req.getParameter("matchcnt");
                String seqBox = req.getParameter("seq_box");
                String status = req.getParameter("status");
                
                int intKeyoper = (keyoper != null) ? Integer.parseInt(keyoper) : -1;
                int intMatchcnt = (matchcnt != null) ? Integer.parseInt(matchcnt) : -1;
                int intStatus = (status != null) ? Integer.parseInt(status) : -1;
                
                autokeyDao.update(seqRule, keywords, intKeyoper, intMatchcnt, seqBox, intStatus);
                
                resObj.put("code", EnvSYS.RESCODE_SUCC);
                resObj.put("msg", "분류 규칙을 저장하였습니다.");
                newRule = false;
            }
        }
        
        if (newRule) { //새로 추가되어야 할 규칙이라면
            resObj = this.addKeywordRule(req);
        }
        
        return resObj;
    }
    
    private JSONObject deleteRule(HttpServletRequest req) throws Exception {
        JSONObject resObj = new JSONObject();

        String seqRule = req.getParameter("seq_rule");
        AutoKeywordDataAccessObject autokeyDao = new AutoKeywordDataAccessObject();
        autokeyDao.delete(seqRule);

        resObj.put("code", EnvSYS.RESCODE_SUCC);
        resObj.put("msg", "분류 규칙을 삭제하였습니다.");

        return resObj;
    }

    private JSONObject runNow(HttpServletRequest req) throws Exception {
        JSONObject resObj = new JSONObject();
        
        UserSession us = AuthManager.getInstance().getUserSession(req);
        AutomationService autoSvc = new AutomationService();
        ItemDataAccessObject itemDao = new ItemDataAccessObject();
        List<JSONObject> items = itemDao.getStandbyItems(us.getSeqDefstore());
        
        JSONObject itemJson = null;
        Iterator<JSONObject> itemIter = items.iterator();
        while(itemIter.hasNext()) {
            itemJson = (JSONObject)itemIter.next();
            
            String seqItem = itemJson.get("seq").toString();
            String itemName = itemJson.get("name").toString();
            String datetime = itemJson.get("insert_date").toString();
            String seqOrder = itemJson.get("seq_order").toString();
            int seqToBox = autoSvc.checkRules(itemName, us.getSeqDefstore());
            
            if (seqToBox > -1) { //분류 자동화에 적용되는 경우 지정한 보관함으로 이동
                int qty = Integer.parseInt(itemJson.get("qty").toString());
                JSONObject storedItem = itemDao.getSomedayItem(seqToBox, itemName, datetime, null);
                if (storedItem != null) { // 기존 아이템에 수량을 추가
                    String addedQty = (Integer.parseInt(storedItem.get("qty").toString()) + qty) + "";
                    itemDao.updateItem(storedItem.get("seq").toString(), null, addedQty, null, null, null, Integer.parseInt(seqOrder));
                } else { // 박스 시퀀스만 변경
                    itemDao.updateItem(seqItem, null, null, null, null, Integer.toString(seqToBox), Integer.parseInt(seqOrder));
                }
            }
        }
        
        resObj.put("code", EnvSYS.RESCODE_SUCC);
        resObj.put("msg", "아이템을 이동하였습니다.");
        
        return resObj;
    }
}
