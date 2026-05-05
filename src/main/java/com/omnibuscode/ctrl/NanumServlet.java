package com.omnibuscode.ctrl;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.omnibuscode.auth.AuthInfo;
import com.omnibuscode.auth.AuthManager;
import com.omnibuscode.base.EnvSYS;
import com.omnibuscode.base.OpenUserInfo;
import com.omnibuscode.base.UserSession;
import com.omnibuscode.dao.FcmTokenDataAccessObject;
import com.omnibuscode.dao.InviteDataAccessObject;
import com.omnibuscode.dao.NanumDataAccessObject;
import com.omnibuscode.dao.NanumfavoriteDataAccessObject;
import com.omnibuscode.dao.NanumitemDataAccessObject;
import com.omnibuscode.dao.ShareDataAccessObject;
import com.omnibuscode.dao.StoreDataAccessObject;
import com.omnibuscode.dao.UserDataAccessObject;
import com.omnibuscode.dao.ZZimDataAccessObject;
import com.omnibuscode.logic.NanumWebService;
import com.omnibuscode.logic.ShareService;
import com.omnibuscode.util.FcmNotificationUtil;
import com.omnibuscode.util.JSONObjectExt;
import com.omnibuscode.util.JinieboxUtil;
import com.omnibuscode.utils.ExceptionUtil;
import com.omnibuscode.utils.PropertiesUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author KIUNSEA
 *
 */
@WebServlet("/nanum")
@MultipartConfig(
		fileSizeThreshold = 1024 * 1024, // 1MB
		maxFileSize = 1024 * 1024 * 10, // 10MB
		maxRequestSize = 1024 * 1024 * 50 // 50MB
)
public class NanumServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
	 */
	private Logger log = LogManager.getLogger(NanumServlet.class);
    
    public void init() {
        PropertiesUtil.USER_PROPERTIES_PATH = this.getServletContext().getRealPath("/")
                + "WEB-INF/classes/res/JINIEBOX.PROPERTIES";
    }
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doGet(req, res);
	}
	
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        
        JSONObject resJson = null;
        JSONObjectExt reqJson = null;
        try {
            reqJson = JinieboxUtil.parseRequest(req);
        } catch (Exception e) {
            log.error(ExceptionUtil.getExceptionInfo(e));
        }

        String cmdHttp = reqJson.getString("cmd");
        try {
            if (cmdHttp != null) {
                if (AuthManager.getInstance().hasUserAuthed(req)) {
                    try {
                        UserSession us = AuthManager.getInstance().getUserSession(req);
                        reqJson.put("seq_session_user", us.getSeq());
                        
                        String seqStore = us.getSeqDefstore();
                        if (seqStore == null) { //세션에서 기본저장소를 찾지 못한 경우의 처리
                            String buid = us.getBuid();
                            UserDataAccessObject udao = new UserDataAccessObject();
                            JSONObject userJson = udao.getUserByBuid(buid);
                            seqStore = userJson.get("seq_defstore").toString();
                        }
                        reqJson.put("seq_defstore", seqStore);
                        
                        if (seqStore != null && Integer.parseInt(seqStore) > 0) {
                            if ("add".equals(cmdHttp)) {
                                resJson = this.addNanum(req);
                            } else if ("delete".equals(cmdHttp)) {
                                resJson = this.delete(req);
// XXX 삭제 예정
//                            } else if ("deleteNanums".equals(cmdHttp)) {
//                                resJson = this.deleteNanums(req);
                            } else if ("update".equals(cmdHttp)) {
                                resJson = this.updateNanum(req);
                            } else if ("registItems".equals(cmdHttp)) {
                                resJson = this.registNanumItems(reqJson);
                            } else if ("releaseItem".equals(cmdHttp)) {
                                resJson = this.releaseNanumItem(reqJson);
                            } else if ("nanums".equals(cmdHttp)) {
                                resJson = this.getNanums(req);
// XXX 미사용으로 삭제 예정
//                            } else if ("favorites".equals(cmdHttp)) {
//                                resJson = this.favorites(req);
                            } else if ("toggle".equals(cmdHttp)) {
                                resJson = this.favoritesToggle(req);
                            } else if ("getInfo".equals(cmdHttp)) {
                                resJson = this.getNanumInfo(req);
                            } else if ("share".equals(cmdHttp)) {
                                resJson = this.shareNanum(req);
                            } else if ("newsc".equals(cmdHttp)) {
                                resJson = this.regenSharecode(req);
                            } else if ("itemZZim".equals(cmdHttp)) {
                                resJson = this.itemZZim(reqJson);
                            } else if ("getZZims".equals(cmdHttp)) {
                                resJson = this.getZZims(reqJson);
                            }
                        } else {
                            resJson = new JSONObject();
                            resJson.put("code", EnvSYS.RESCODE_FAIL);
                            resJson.put("msg", "등록하지 않은 사용자입니다");
                        }
                    } catch (Exception e) {
                        log.error(ExceptionUtil.getExceptionInfo(e));
                    }
                } else {
                    resJson = new JSONObject();
                    AuthInfo uai = AuthManager.getInstance().getUserAuthInfo(req);
                    if (uai != null) {
                        resJson.put("code", uai.getValidcode());
                        resJson.put("msg", uai.getValidmsg());
                    } else {
                        resJson.put("code", EnvSYS.RESCODE_FAIL);
                        resJson.put("msg", EnvSYS.RESMSG_FAIL);
                    }
                }
            } else {
                String scdHttp = reqJson.getString("scd");
                if (scdHttp != null) {
                    resJson = this.showNanumView(req, res);
                } else {
                	resJson = new JSONObject();
                    resJson.put("code", EnvSYS.RESCODE_FAIL);
                    resJson.put("msg", EnvSYS.RESMSG_INVREQ);
                }
            }
        } catch (Exception e) {
            log.error(ExceptionUtil.getExceptionInfo(e));
        }
        
        if (resJson == null) resJson = new JSONObject();
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
    
    /**
     * 나눔박스 등록 요청 처리
     * 
     * @param req
     * @return
     * @throws Exception
     */
    private JSONObject addNanum(HttpServletRequest req) throws Exception {
        
        JSONObject resObj = new JSONObject();
        String shareType = req.getParameter("box_share");
        String accessCode = req.getParameter("box_acd");
        String name = req.getParameter("box_name");
        String details = req.getParameter("box_details");
        String seqStore = req.getParameter("seq_store");
        
        if (JinieboxUtil.isEmpty(seqStore)) {
            UserSession us = AuthManager.getInstance().getUserSession(req);
            seqStore = us.getSeqDefstore();
        }

        NanumDataAccessObject nanumDao = new NanumDataAccessObject();
        int seqBox = nanumDao.insert(name, details, seqStore, shareType, this.generateShareCode(), accessCode);

        if (seqBox > -1) {
            resObj.put("nanums", nanumDao.getStoreNanums(seqStore));
            resObj.put("code", EnvSYS.RESCODE_SUCC);
            resObj.put("msg", "나눔함 등록이 성공하였습니다");
        }

        return resObj;
    }
    
    /**
     * 랜덤 share code 반환
     * @param code
     * @return
     */
    private String generateShareCode() {

        String sharecode = JinieboxUtil.generateRandomAlphabet(20);
        NanumDataAccessObject ndao = new NanumDataAccessObject();
        try {
            while (ndao.usedShareCode(sharecode)) {
                sharecode = JinieboxUtil.generateRandomAlphabet(20);
            }
        } catch (Exception e) {
            log.error(ExceptionUtil.getExceptionInfo(e));
        }
        
        return sharecode;
    }
    
    /**
     * 박스를 삭제한다
     * @param req
     * @return
     * @throws NumberFormatException
     * @throws Exception
     */
    private JSONObject delete(HttpServletRequest req) throws Exception {

        JSONObject resObj = new JSONObject();
        String code = EnvSYS.RESCODE_FAIL;
        String msg = "박스(나눔함) 삭제에 실패하였습니다 ";

        String seqNanum = req.getParameter("seq_box");
        if (seqNanum != null && Integer.parseInt(seqNanum) > -1) {
            ShareDataAccessObject sdao = new ShareDataAccessObject();
            NanumitemDataAccessObject nidao = new NanumitemDataAccessObject();
            NanumfavoriteDataAccessObject nfdao = new NanumfavoriteDataAccessObject();
            NanumDataAccessObject ndao = new NanumDataAccessObject();

            // 나눔함 공유 정보 삭제
            sdao.delete(EnvSYS.CLASS_TYPE_NANUM, seqNanum);

            // 나눔 아이템 정보 삭제
            nidao.deleteSeqNanum(seqNanum);

            // 나눔 즐겨찾기 정보 삭제
            nfdao.delete(seqNanum);

            // 나눔 박스 삭제
            ndao.delete(seqNanum);

            UserSession userSS = AuthManager.getInstance().getUserSession(req);
            String seqStore = userSS.getSeqDefstore();
            resObj.put("nanums", ndao.getStoreNanums(seqStore));
            
            code = EnvSYS.RESCODE_SUCC;
            msg = "박스(나눔함)의 모든 정보(박스, 아이템, 즐겨찾기, 공유)를 삭제하였습니다.";
        }

        resObj.put("code", code);
        resObj.put("msg", msg);
        return resObj;
    }
    
  //XXX 삭제 예정
//    /**
//     * 박스들을 일괄 삭제한다
//     * 
//     * @param req
//     * @return
//     * @throws Exception
//     */
//    private JSONObject deleteNanums(HttpServletRequest req) throws Exception {
//
//        JSONObject resObj = new JSONObject();
//        String code = EnvSYS.RESCODE_FAIL;
//        String msg = "박스(나눔함) 삭제에 실패하였습니다 ";
//        
//        ShareDataAccessObject sdao = new ShareDataAccessObject();
//        NanumitemDataAccessObject nidao = new NanumitemDataAccessObject();
//        NanumfavoriteDataAccessObject nfdao = new NanumfavoriteDataAccessObject();
//        NanumDataAccessObject ndao = new NanumDataAccessObject();
//        
//        String nanumsStr = req.getParameter("nanums");
//        String[] nanumArr = nanumsStr.split(",");
//        if (nanumArr.length > 0) {
//            for (int i = 0; i < nanumArr.length; i++) {
//
//                String seqNanum = nanumArr[i];
//
//                // 나눔함 공유 정보 삭제
//                sdao.delete(null, null, seqNanum);
//
//                // 나눔 아이템 정보 삭제
//                nidao.delete(seqNanum);
//
//                // 나눔 즐겨찾기 정보 삭제
//                nfdao.delete(seqNanum);
//
//                // 나눔 박스 삭제
//                ndao.delete(seqNanum);
//            }
//            code = EnvSYS.RESCODE_SUCC;
//            msg = "박스(나눔함)의 모든 정보(박스, 아이템, 즐겨찾기, 공유)를 삭제하였습니다.";
//            
//            UserSession userSS = AuthManager.getInstance().getUserSession(req);
//            String seqStore = userSS.getSeqDefstore();
//            List sNanums = ndao.getStoreNanums(seqStore);
////            resObj.put("nanums", sNanums);
//            resObj.put("nanums", JinieboxUtil.listToMap(sNanums));
//        } else {
//            msg = "박스 목록이 없습니다.";
//        }
//        
//        resObj.put("code", code);
//        resObj.put("msg", msg);
//        return resObj;
//    }
    
    private JSONObject updateNanum(HttpServletRequest req) throws Exception {

        JSONObject resObj = new JSONObject();
        
        String buid = AuthManager.getInstance().getUserSession(req).getBuid();
        String seq = req.getParameter("seq_box");
        String seqStore = req.getParameter("seq_store");
        String shareType = req.getParameter("box_share");
        String accessCode = req.getParameter("box_acd");
        String name = req.getParameter("box_name");
        String details = req.getParameter("box_details");
        
        NanumDataAccessObject nanumDao = new NanumDataAccessObject();

        if (seq != null) {
        	nanumDao.update(seq, name, details, seqStore, shareType, null, accessCode);
        	
        	resObj.put("seqNanum", seq);
        	resObj.put("nanumName", name);
        	resObj.put("nanumDetails", details);
        	resObj.put("seqStore", seqStore);
        	
        	resObj.put("code", EnvSYS.RESCODE_SUCC);
            resObj.put("msg", "박스 정보를 수정하였습니다");
        } else {
            resObj.put("code", EnvSYS.RESCODE_FAIL);
            resObj.put("msg", name + " 정보 수정이 실패하였습니다");
        }
        
        return resObj;
    }
    
    /**
     * share code 다시 생성
     * @param req
     * @return
     * @throws Exception
     */
    private JSONObject regenSharecode(HttpServletRequest req) throws Exception {

        JSONObject resObj = new JSONObject();
        
        String seq = req.getParameter("seq_box");
        NanumDataAccessObject nanumDao = new NanumDataAccessObject();

        if (seq != null) {
            String newsc = this.generateShareCode();
            nanumDao.update(seq, null, null, null, null, newsc, null);
            resObj.put("newsc", newsc);
            
            resObj.put("code", EnvSYS.RESCODE_SUCC);
            resObj.put("msg", "공유 주소를 변경하였습니다.");
        } else {
            resObj.put("code", EnvSYS.RESCODE_FAIL);
            resObj.put("msg", " 정보 수정이 실패하였습니다");
        }
        
        return resObj;
    }
    
    /**
     * 복수개의 아이템을 나눔함에 일괄 등록
     * 
     * @param req
     * @return
     * @throws Exception
     */
    private JSONObject registNanumItems(JSONObjectExt reqJson) throws Exception {
        
        NanumWebService nanumSvc = new NanumWebService();
        JSONObject resObj = nanumSvc.registNanumItems(reqJson);
        
        return resObj;
        
    }
    
    /**
     * 아이템을 나눔함에서 해제
     * 
     * @param req
     * @return
     * @throws Exception
     */
    private JSONObject releaseNanumItem(JSONObjectExt reqJson) throws Exception {
        
        JSONObject resObj = new JSONObject();
        String code = EnvSYS.RESCODE_FAIL;
        String msg = "아이템 등록해제에 실패하였습니다 ";

        String seqNanum = reqJson.getString("seq_nanum");
        String seqItem = reqJson.getString("seq_item");

        NanumitemDataAccessObject niDao = new NanumitemDataAccessObject();
        niDao.delete(seqNanum, seqItem);
        
        resObj.put("seq_nanum", seqNanum);
        resObj.put("seq_item", seqItem);
        
        code = EnvSYS.RESCODE_SUCC;
        msg = "아이템 등록해제에 성공하였습니다";
        
        resObj.put("code", code);
        resObj.put("msg", msg);
        
        return resObj;
        
    }
    
    /**
     * 나눔함 목록 반환 (즐겨찾기, 자신의, 공유 받은)
     * 
     * @param req
     * @return
     * @throws Exception
     */
    private JSONObject getNanums(HttpServletRequest req) throws Exception {
        
        JSONObject resObj = new JSONObject();
        String code = EnvSYS.RESCODE_FAIL;
        String msg = "보관함 조회에 실패하였습니다 ";
        
        UserSession usrSession = AuthManager.getInstance().getUserSession(req);
        String seqUser = usrSession.getSeq();
        String seqStore = usrSession.getSeqDefstore();
        
        StoreDataAccessObject storeDao = new StoreDataAccessObject();
        NanumfavoriteDataAccessObject nfDao = new NanumfavoriteDataAccessObject();
        NanumitemDataAccessObject niDao = new NanumitemDataAccessObject();
        NanumDataAccessObject nDao = new NanumDataAccessObject();
        ShareDataAccessObject shDao = new ShareDataAccessObject();
        
        //즐겨찾기로 지정된 나눔박스 목록
        List<JSONObject> nanumsFav = nfDao.getUserFavorites(seqUser);
        JSONObject nanumfavJson, nanumJson = null;
        String seqNanum, seqOwner, authority = null;
        Iterator<JSONObject> nanumIter = nanumsFav.iterator();
        while (nanumIter.hasNext()) {
            nanumfavJson = (JSONObject) nanumIter.next();
            
            seqNanum = nanumfavJson.get("seq").toString();
            nanumJson = nDao.getNanum(seqNanum);
            
            int itemcnt = niDao.getItemCount(seqNanum);
            nanumfavJson.put("itemcnt",  itemcnt);
            seqOwner = storeDao.getStore(nanumfavJson.get("seq_store").toString()).get("seq_owner").toString();
            String accessLevel = nanumJson.get("access_level").toString();
            if (seqUser.equals(seqOwner)) {
                nanumfavJson.put("is_nanum_owner",  "true");
                if ("M".equals(accessLevel) || "F".equals(accessLevel)) { // 소유자의 나눔함에서 접근 레벨이 M 또는 F인 경우 버튼 출력
                    nanumfavJson.put("show_btn_addshare",  "true");
                }    
            }
            authority = shDao.getAuthority(seqUser, EnvSYS.CLASS_TYPE_NANUM, nanumfavJson.get("seq").toString());
            if ("M".equals(authority)) { //공유된 권한이 modify 인 경우
                nanumfavJson.put("show_btn_additem",  "true");
            }
        }
        resObj.put("nanums_fav", nanumsFav);
        
        //자신의 저장소에 있는 나눔박스 목록
        List<JSONObject> nanumsOwn = nDao.getStoreNanums(seqStore);
        this.restoreCheckFavorite(nanumsOwn, seqUser);
        resObj.put("nanums_own", nanumsOwn);
        
        //친구 또는 회원에게 공유받은 나눔박스 목록
        List<JSONObject> nanumsNearby = nDao.getNearbyNanums(seqUser);
        this.restoreCheckFavorite(nanumsNearby, seqUser);
        resObj.put("nanums_nearby", nanumsNearby);
        
        code = EnvSYS.RESCODE_SUCC;
        msg = "정보 조회가 성공하였습니다";

        resObj.put("code", code);
        resObj.put("msg", msg);
        return resObj;
    }
    
    /**
     * 나눔박스가 즐겨찾기로 지정되었는지의 여부를 목록에 적용
     * 
     * @param nanums
     * @param seqUser
     * @throws Exception
     */
    private void restoreCheckFavorite(List<JSONObject> nanums, String seqUser) throws Exception {
        NanumfavoriteDataAccessObject nfDao = new NanumfavoriteDataAccessObject();
        
        JSONObject nanumJson = null;
        Iterator<JSONObject> nanumIter = nanums.iterator();
        while (nanumIter.hasNext()) {
            nanumJson = (JSONObject) nanumIter.next();
            if (nfDao.hasAlready(nanumJson.get("seq").toString(), seqUser)) {
                nanumJson.put("favorite", "1");
            } else {
                nanumJson.put("favorite", "0");
            }
        }
    }
    
 // XXX 미사용으로 삭제 예정
//    /**
//     * 즐겨찾기 목록 반환
//     * @param req
//     * @return
//     * @throws Exception
//     */
//    private JSONObject favorites(HttpServletRequest req) throws Exception {
//        
//        JSONObject resObj = new JSONObject();
//        String code = EnvSYS.RESCODE_FAIL;
//        String msg = "즐겨찾기 조회에 실패하였습니다 ";
//        
//        UserSession userSS = AuthManager.getInstance().getUserSession(req);
//        String seqUser = userSS.getSeq();
//        String seqStore = userSS.getSeqDefstore();
//        
//        NanumfavoriteDataAccessObject nfDao = new NanumfavoriteDataAccessObject();
//        NanumitemDataAccessObject niDao = new NanumitemDataAccessObject();
//        
//        List<JSONObject> nanumsFav = nfDao.getUserFavorites(seqUser);
//        JSONObject nanumJson = null;
//        Iterator<JSONObject> nanumIter = nanumsFav.iterator();
//        while (nanumIter.hasNext()) {
//            nanumJson = (JSONObject) nanumIter.next();
//            int itemcnt = niDao.getItemCount(nanumJson.get("seq").toString());
//            nanumJson.put("itemcnt",  itemcnt);
//        }
//        resObj.put("nanums_fav", nanumsFav);
//        
//        code = EnvSYS.RESCODE_SUCC;
//        msg = "정보 조회가 성공하였습니다";
//
//        resObj.put("code", code);
//        resObj.put("msg", msg);
//        return resObj;
//    }
    
    /**
     * 나눔박스를 즐겨찾기 테이블 (nanum_favorite)에서 toggle(추가/삭제) 한다
     * 
     * @param req
     * @return
     * @throws Exception
     */
    private JSONObject favoritesToggle(HttpServletRequest req) throws Exception {

        JSONObject resObj = new JSONObject();
        
        String seqUser = AuthManager.getInstance().getUserSession(req).getSeq();
        String seqNanum = req.getParameter("seq_nanum");

        if (seqNanum != null) {
            
            NanumfavoriteDataAccessObject nfDao = new NanumfavoriteDataAccessObject();
            NanumDataAccessObject nDao = new NanumDataAccessObject();
            
            JSONObject nJson = nDao.getNanum(seqNanum);
            if (!nfDao.hasAlready(seqNanum, seqUser)) {
                nfDao.insert(seqNanum, seqUser);
                nJson.put("favorite", "1");
            } else {
                nfDao.delete(seqNanum, seqUser);
                nJson.put("favorite", "0");
            }
            resObj.put("nanum", nJson);

            resObj.put("code", EnvSYS.RESCODE_SUCC);
            resObj.put("msg", "나눔 정보를 수정하였습니다");
        } else {
            resObj.put("code", EnvSYS.RESCODE_FAIL);
            resObj.put("msg", "나눔 정보가 누락되었습니다");
        }

        return resObj;
    }
    
    /**
     * 나눔아이템 목록 반환
     * @param req
     * @return
     * @throws Exception
     */
    private JSONObject getNanumInfo(HttpServletRequest req) throws Exception {
        
        UserSession us = AuthManager.getInstance().getUserSession(req);
        String contextPath = this.getServletContext().getRealPath("/");
        String seqUser = us.getSeq();
        
        JSONObject resObj = new JSONObject();
        String code = EnvSYS.RESCODE_FAIL;
        String msg = "아이템 조회에 실패하였습니다 ";
        String seqNanum = req.getParameter("seq_nanum");
        
        NanumDataAccessObject nDao = new NanumDataAccessObject();
        JSONObject nanumJson = nDao.getNanum(seqNanum);
        if (nanumJson != null) {
            StoreDataAccessObject storeDao = new StoreDataAccessObject();
            String seqOwner = storeDao.getStore(nanumJson.get("seq_store").toString()).get("seq_owner").toString();
            if (seqUser.equals(seqOwner)) {
                resObj.put("is_nanum_owner",  "true");
            }
        }
        
        NanumWebService nanumSvc = new NanumWebService();
        List<JSONObject> items = null;
        if ("0".equals(seqNanum)) {
            items = nanumSvc.getOpenNanumItems(contextPath, us);
        } else {
            items = nanumSvc.getNanumItems(contextPath, us, seqNanum);
        }
        resObj.put("nanum_items", JinieboxUtil.listToMap(items));
        
        InviteDataAccessObject invDao = new InviteDataAccessObject();
        JSONObject invites = invDao.getInvitesJson(seqUser, EnvSYS.CLASS_TYPE_NANUM, seqNanum);
        resObj.put("nanum_invites", invites);
        
        ShareDataAccessObject shareDao = new ShareDataAccessObject();
        JSONObject shares = shareDao.list(EnvSYS.CLASS_TYPE_NANUM, seqNanum);
        resObj.put("nanum_shares", shares);
        
        String authority = shareDao.getAuthority(seqUser, EnvSYS.CLASS_TYPE_NANUM, seqNanum);
        resObj.put("nanum_authority", authority);
        
        resObj.put("seq_nanum", seqNanum);
        
        code = EnvSYS.RESCODE_SUCC;
        msg = "정보 조회가 성공하였습니다";

        resObj.put("code", code);
        resObj.put("msg", msg);
        return resObj;
    }
    
    /**
     * 나눔박스의 공유 정보를 등록<br/>
     * URL을 공유받아 최초 접속 성공시 공유정보로 자동 등록
     * 
     * @param req
     * @return
     * @throws Exception
     */
    private JSONObject shareNanum(HttpServletRequest req) throws Exception {

        UserSession usrSession = AuthManager.getInstance().getUserSession(req);
        String seqUser = usrSession.getSeq();
        String shareCode = req.getParameter("scd");
        String accessCodeUser = req.getParameter("acd");
        
        NanumWebService nwsvc = new NanumWebService();
        return nwsvc.addShareNanum(seqUser, shareCode, accessCodeUser);
        
    }
    
    /**
     * 세션과 관계없이 나눔함의 접근 레벨에 따라 나눔 리스트 페이지를 선택적으로 출력하기 위한 함수이다.<br/>
     * 공유코드(share code)로 나눔박스의 아이템 목록을 조회하여 페이지로 출력한다<br/>
     *   - scd : share_code<br/>
     *   - acd : access_code
     * 
     * @param req
     * @param res
     * @return
     * @throws Exception
     */
    private JSONObject showNanumView(HttpServletRequest req, HttpServletResponse res) throws Exception {
        
        String contextPath = this.getServletContext().getRealPath("/");
        UserSession us = AuthManager.getInstance().getUserSession(req);
        
        JSONObject resObj = new JSONObject();
        String code = EnvSYS.RESCODE_FAIL;
        String msg = "아이템 조회에 실패하였습니다 ";
        
        String shareCode = req.getParameter("scd");
        String accessCode = req.getParameter("acd");
        
        NanumDataAccessObject ndao = new NanumDataAccessObject();
        JSONObjectExt nanum = ndao.getNanumBySharecode(shareCode);
        ShareDataAccessObject shareDao = new ShareDataAccessObject();
        InviteDataAccessObject invDao = new InviteDataAccessObject();
        NanumfavoriteDataAccessObject nfDao = new NanumfavoriteDataAccessObject();
        
        if (nanum != null) {
            
            req.setAttribute("share_code", shareCode);
            
            boolean requireAcd = false; //사용자의 접속코드 입력이 필요한지 여부
            String acdSaved = nanum.getString("access_code");
            if (acdSaved != null && acdSaved.trim().length() > 0) {
                requireAcd = true;
                req.setAttribute("require_acd", requireAcd);
            }
            String accessLevel = nanum.getString("access_level");
            String seqNanum = nanum.getString("seq");
            
            if (accessLevel.equals("O")) {
                /**
                 * OPEN - 회원, 비회원 모두 열람 가능 (jsp페이지를 출력하기 전에 servlet에서 이미 데이터 적재 여부가 결정됨)
                 */
                
                req.setAttribute("seq_nanum", seqNanum);
                
                if (us == null) {
                    //로그인하지 않은 상태로 데이터가 요청된 경우
                    String seqStore = nanum.getString("seq_store");
                    us = new OpenUserInfo(seqStore);
                }
                
                NanumWebService nanumSvc = new NanumWebService();
                List<JSONObject> items = nanumSvc.getNanumItems(contextPath, us, nanum.getString("seq"));
                
                if (accessCode != null) {
                    // 출력페이지 jsp에서 share code 와 access code 로 데이터를 요청한 경우 처리
                    //TODO access code 검사
                    resObj.put("nanum_items", items);
                } else {
                    // share code 로만 요청한 경우 출력할 페이지(nanumpage.jsp)로 응답
                    req.setAttribute("nanum_name", nanum.getString("name"));

                    if (!requireAcd) { // 접속코드가 필요 없는 경우에만 정보를 추가
                        req.setAttribute("nanum_items", items);
                    }
                    getServletContext().getRequestDispatcher("/nanumpage.jsp").forward(req, res);
                }
                
            } else if (accessLevel.equals("M")) {
                /**
                 * MEMBER - 회원이어야만 열람 가능 (이웃나눔과 즐겨찾기 자동 처리)
                 */
                
                if (us == null) {
                    //로그인 화면으로 포워딩
                    req.setAttribute("forward_url", req.getContextPath() + "/nanum?scd=" + shareCode);
                    getServletContext().getRequestDispatcher("/signin.jsp").forward(req, res);
                } else {
                    
                    // 이웃나눔으로 등록
                    String valAuthority = shareDao.getAuthority(us.getSeq(), nanum.getType(), seqNanum);
                    if (valAuthority == null) {
                        ShareService shareSvc = new ShareService();
                        shareSvc.addShare(us.getSeq(), nanum.getType(), seqNanum, "R");
                    }

                    // 즐겨찾기에 등록
                    if (!nfDao.hasAlready(seqNanum, us.getSeq())) {
                        nfDao.insert(seqNanum, us.getSeq());
                    }
                    
                    req.setAttribute("init_show", "nanum-tab");
                    req.setAttribute("seq_nanum", seqNanum);
                    getServletContext().getRequestDispatcher("/list_box.jsp").forward(req, res);
                    
                    code = EnvSYS.RESCODE_SUCC;
                    msg = "정보 조회가 성공하였습니다";
                }
                
            } else if (accessLevel.equals("F")) {
                /**
                 * FRIEND - 공유 허가된 사용자만 열람 가능 (이웃나눔과 즐겨찾기 자동 처리)
                 */
                
                if (us == null) {
                    //로그인 화면으로 포워딩
                    req.setAttribute("forward_url", req.getContextPath() + "/nanum?scd=" + shareCode);
                    getServletContext().getRequestDispatcher("/signin.jsp").forward(req, res);
                } else {
                    req.setAttribute("init_show", "nanum-tab");
                    
                    // 이웃나눔 확인
                    String valAuthority = shareDao.getAuthority(us.getSeq(), EnvSYS.CLASS_TYPE_NANUM, seqNanum);
                    
                    if (valAuthority == null) {
                        JSONObject invObj = invDao.getInvite(nanum.getString("seq_owner"), EnvSYS.CLASS_TYPE_NANUM, seqNanum);
                        
                        // 공유 초대 처리
                        if (invObj != null) {
                            if (shareDao.add(us.getSeq(), invObj.get("type_object").toString(), invObj.get("seq_object").toString(), invObj.get("authority").toString())) {
                                // 즐겨찾기에 등록
                                if (!nfDao.hasAlready(seqNanum, us.getSeq())) {
                                    nfDao.insert(seqNanum, us.getSeq());
                                }
                                invDao.delete(invObj.get("seq").toString());
                            } else {
                                log.debug("공유 저장에 실패하였습니다.");
                            }
                        }

                        req.setAttribute("seq_nanum", seqNanum);
                    }
                    
                    getServletContext().getRequestDispatcher("/list_box.jsp").forward(req, res);
                    
                    code = EnvSYS.RESCODE_SUCC;
                    msg = "정보 조회가 성공하였습니다";
                }
                
            } else {
                // TODO 올바르지 않은 값
            }
        } else {
            msg = "(" + shareCode + ") 나눔 정보가 없습니다";
        }
        
        resObj.put("code", code);
        resObj.put("msg", msg);
        return resObj;
    }

    /**
     * 나눔아이템을 찜 요청한다.
     * @param reqJson
     * @return
     * @throws Exception
     */
    private JSONObject itemZZim(JSONObjectExt reqJson) throws Exception {
        
        String seqNitem = reqJson.getString("seq_nitem");
        String seqUser = reqJson.getString("seq_session_user");
        String zzimQty = reqJson.getString("zzim_qty");
        
        ZZimDataAccessObject zzimDao = new ZZimDataAccessObject();
        
        JSONObject resObj = new JSONObject();
        if (zzimDao.hasAlready(seqNitem, seqUser)) {
            resObj.put("code", EnvSYS.RESCODE_FAIL);
            resObj.put("msg", "이미 요청한 정보가 있습니다.");
        } else {
            int seqZZim = zzimDao.addZZim(seqNitem, seqUser, zzimQty);
            if (seqZZim > -1) {
                
                List<JSONObject> zzimInfos = zzimDao.getZZimInfo(seqNitem);
                if (zzimInfos != null) {
                    String titleStr = "나눔찜 요청이 접수되었습니다.";
                    JSONObject zzimInfo = null;
                    Iterator<JSONObject> zzimInfoIter = zzimInfos.iterator();
                    while (zzimInfoIter.hasNext()) {
                        zzimInfo = (JSONObject) zzimInfoIter.next();

                        String bodyStr = "'" + zzimInfo.get("uname") + "' 사용자가 '" 
                                + zzimInfo.get("nname") + "' 나눔함의 '"
                                + zzimInfo.get("iname") + "' 아이템을 '" 
                                + zzimQty + "' 개 찜하였습니다.";

                        FcmTokenDataAccessObject ftDao = new FcmTokenDataAccessObject();
                        List<String> usrTokens = ftDao.getTokens(zzimInfo.get("iowner").toString());

                        if (usrTokens != null && usrTokens.size() > 0) {
                            FcmNotificationUtil fcmNoti = FcmNotificationUtil.getInstance();
                            fcmNoti.sendNotifications(usrTokens, titleStr, bodyStr);
                        }
                    }
                }
                
                resObj.put("code", EnvSYS.RESCODE_SUCC);
                resObj.put("msg", "요청을 전달하였습니다.");
            } else {
                resObj.put("code", EnvSYS.RESCODE_FAIL);
                resObj.put("msg", "요청에 실패하였습니다.");
            }
        }
        
        return resObj;
    }
    
    /**
     * 나눔아이템 목록 반환
     * @param req
     * @return
     * @throws Exception
     */
    private JSONObject getZZims(JSONObjectExt reqJson) throws Exception {
        
        JSONObject resObj = new JSONObject();
        
        String code = EnvSYS.RESCODE_SUCC;
        String msg = "정보 조회가 성공하였습니다";
        
        String seqNanum = reqJson.getString("seq_nanum");
        String seqNitem = reqJson.getString("seq_nitem");
        String seqUser = reqJson.getString("seq_session_user");
        
        NanumDataAccessObject nDao = new NanumDataAccessObject();
        JSONObject nanumJson = nDao.getNanum(seqNanum);
        if (nanumJson != null) {
            StoreDataAccessObject storeDao = new StoreDataAccessObject();
            String seqOwner = storeDao.getStore(nanumJson.get("seq_store").toString()).get("seq_owner").toString();
            if (seqUser.equals(seqOwner)) {
                // 나눔 소유자만 조회 가능
                ZZimDataAccessObject zzimDao = new ZZimDataAccessObject();
                List<JSONObject> zzims = zzimDao.getZZims(seqNitem);
                resObj.put("zzims", zzims);
            } else {
                code = EnvSYS.RESCODE_FAIL;
                msg = "아이템 소유자만 조회 가능합니다.";
            }
        } else {
            code = EnvSYS.RESCODE_FAIL;
            msg = "나눔 정보가 없습니다.";
        }
        
        resObj.put("code", code);
        resObj.put("msg", msg);
        return resObj;
    }
}
