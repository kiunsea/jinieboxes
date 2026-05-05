package com.omnibuscode.ctrl;



import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
import com.omnibuscode.dao.BoxDataAccessObject;
import com.omnibuscode.dao.InviteDataAccessObject;
import com.omnibuscode.dao.ItemDataAccessObject;
import com.omnibuscode.dao.NanumDataAccessObject;
import com.omnibuscode.dao.NanumfavoriteDataAccessObject;
import com.omnibuscode.dao.NanumitemDataAccessObject;
import com.omnibuscode.dao.ShareDataAccessObject;
import com.omnibuscode.dao.StoreDataAccessObject;
import com.omnibuscode.dao.UserDataAccessObject;
import com.omnibuscode.logic.GcpService;
import com.omnibuscode.util.JSONObjectExt;
import com.omnibuscode.util.JinieboxUtil;
import com.omnibuscode.utils.ExceptionUtil;
import com.omnibuscode.utils.NumberUtil;
import com.omnibuscode.utils.PropertiesUtil;

/**
 * @author KIUNSEA
 *
 */
@WebServlet("/store")
public class StoreServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
     */
    private Logger log = LogManager.getLogger(StoreServlet.class);

    public void init() {;}

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    	//request parameter 로깅
        String pName = null;
        Enumeration<String> enums = req.getParameterNames();
        while(enums.hasMoreElements()) {
        	pName = enums.nextElement().toString();
        	log.debug("(param) "+pName+" - "+req.getParameter(pName));
        }

		doGet(req, res);
	}

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        JSONObject resJson = null;

        String cmdHttp = req.getParameter("cmd");
        String buid = req.getParameter("buid");

        try {
        	if (AuthManager.getInstance().hasUserAuthed(req)) {
                if (cmdHttp != null) {
                    if ("getStoreListLink".equals(cmdHttp)) {
                        resJson = this.getStoreListLink(req);
                    } else if ("list".equals(cmdHttp)) {
                        resJson = this.list(req);
                    } else if ("getStoreInfoLink".equals(cmdHttp)) {
                        resJson = this.getStoreInfoLink(req);
                    } else if ("info".equals(cmdHttp)) {
                        resJson = this.info(req);
                    } else if ("deleteBoxes".equals(cmdHttp)) {
                        resJson = this.deleteBoxes(req);
                    } else if ("saveSetting".equals(cmdHttp)) {
                        resJson = this.saveSetting(req);
                    } else if ("setStandbyDays".equals(cmdHttp)) {
                        resJson = this.setStandbyDays(req);
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
                    resJson.put("msg", EnvSYS.RESMSG_FAIL);
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
     * 저장소 목록 링크 반환 (by bixby user id)
     * 
     * @param req
     * @return
     * @throws Exception
     */
    private JSONObject getStoreListLink(HttpServletRequest req) throws Exception {

        JSONObject resObj = new JSONObject();
        String buid = AuthManager.getInstance().getUserSession(req).getBuid();
        
        UserDataAccessObject userDao = new UserDataAccessObject();
        String seqUser = userDao.getSeqByBuid(buid);
        StoreDataAccessObject sdao = new StoreDataAccessObject();
        List<JSONObject> stores = sdao.getSharedStores(seqUser);
        JSONObject store = sdao.getOwnStore(seqUser);
        
        if (stores == null)
            stores = new ArrayList<JSONObject>();
        stores.add(store);
        
        String resUrl = PropertiesUtil.get("SERVICE_URL") + "/store_list.jsp";
        resUrl += "?seq_owner=" + seqUser;
        resObj.put("authkey", AuthManager.getInstance().getRandomAuthKey(buid));
        resObj.put("code", EnvSYS.RESCODE_SUCC);
        resObj.put("msg", "저장소가 총 " + stores.size() + "개가 있습니다 자세한 내용은 링크를 확인해주세요");
        resObj.put("label", "저장소목록");
        resObj.put("url", resUrl);

        return resObj;
    }
    
    /**
     * 저장소 목록 조회
     * 
     * @param req
     * @return
     * @throws Exception
     */
    private JSONObject list(HttpServletRequest req) throws Exception {

        JSONObject resObj = new JSONObject();
        
        UserSession us = AuthManager.getInstance().getUserSession(req);
        String seqUser = us.getSeq();

        StoreDataAccessObject sdao = new StoreDataAccessObject();
        List<JSONObject> stores = sdao.getSharedStores(seqUser);
        JSONObject store = sdao.getOwnStore(seqUser);
        store.put("my_store", true);

        if (stores == null) {
            stores = new ArrayList<JSONObject>();
        }
        stores.add(store);
        
        StoreDataAccessObject storeDao = new StoreDataAccessObject();
        List<JSONObject> shList = storeDao.getSharedStores(us.getSeq());
        resObj.put("sharedlist", shList);

        // def store 설정
        UserDataAccessObject userDao = new UserDataAccessObject();
        JSONObject userJson = userDao.getUser(seqUser);
        if (userJson != null) {
            Object seqDefstore = userJson.get("seq_defstore");
            if (seqDefstore != null) {
                String seqDefStore = seqDefstore.toString();
                Iterator<JSONObject> storeIter = stores.iterator();
                JSONObject storeJson = null;
                String seqStore = null;
                while (storeIter.hasNext()) {
                    storeJson = (JSONObject) storeIter.next();
                    seqStore = storeJson.get("seq").toString();
                    if (seqDefStore.equals(seqStore)) {
                        storeJson.put("def_check", true);
                    }
                }
                resObj.put("stores", stores);
            } else {
                resObj.put("msg", "기본 저장소가 없습니다");
            }
            resObj.put("code", EnvSYS.RESCODE_SUCC);
        } else {
            resObj.put("code", EnvSYS.RESCODE_FAIL);
            resObj.put("msg", "사용자 정보가 없습니다");
        }
        return resObj;
    }
    
    /**
     * 저장소 정보 조회<br/>
     * - 조회하려는 저장소명이 소유저장소와 일치한다면 공유저장소는 조회할 필요가 없다<br/>
     * - 소유저장소와 일치하지 않고 공유저장소에서 여러개가 발견되면 이름이 같은 저장소가 여러개라고 알려주는 메세지를 출력
     * - 소유저장소와 일치하지 않고 공유저장소에서 하나만 발견되었다면 공유 저장소 목록에서 일치 항목을 발견했다는 메세지를 출력
     * @param req
     * @return
     * @throws Exception
     */
    private JSONObject getStoreInfoLink(HttpServletRequest req) throws Exception {

        JSONObject resObj = new JSONObject();
        String buid = AuthManager.getInstance().getUserSession(req).getBuid();
        String storeName = req.getParameter("store_name");
        
        UserDataAccessObject udao = new UserDataAccessObject();
        StoreDataAccessObject sdao = new StoreDataAccessObject();
        String seqUser = udao.getSeqByBuid(buid);
        JSONObject store = sdao.getOwnStore(seqUser);
        
        List<JSONObject> boxes = null;
        int storeCnt = 0;
        if (storeName != null && !storeName.equals(store.get("name"))) {
            store = null;
            List<JSONObject> storesShared = sdao.getSharedStores(seqUser);
            JSONObject storeShared = null;
            Iterator<?> storesSharedIter = storesShared.iterator();
            while (storesSharedIter.hasNext()) {
                storeShared = (JSONObject) storesSharedIter.next();
                if (storeName.equals(storeShared.get("name"))) {
                    store = storeShared;
                    storeCnt++;
                }
            }
        }
        
        if (store != null && storeCnt < 2) {
            BoxDataAccessObject boxDao = new BoxDataAccessObject();
            boxes = boxDao.getStoreBoxes(store.get("seq").toString());
            store.put("boxes", boxes);
            resObj.put("store", store);
            
            String resUrl = PropertiesUtil.get("SERVICE_URL") + "/store_info.html";
            resUrl += "?seq_store=" + store.get("seq").toString();
            resObj.put("label", "저장소정보");
            resObj.put("url", resUrl);
            resObj.put("msg", store.get("name") + "저장소는 " + boxes.size() + "개의 보관함을 가지고 있습니다 자세한 내용은 링크를 확인해주세요");
            
        } else if (storeCnt > 1) {
            resObj.put("msg", store.get("name") + " 이름이 일치하는 저장소가 공유 저장소 목록에서 " + storeCnt + "개 발견되었습니다 저장소 목록을 확인하여 주세요");
            resObj.put("code", EnvSYS.RESCODE_SUCC);
        } else {
            resObj.put("msg", "일치하는 저장소가 없습니다");
            resObj.put("code", EnvSYS.RESCODE_FAIL);
        }
        
        resObj.put("authkey", AuthManager.getInstance().getRandomAuthKey(buid));

        return resObj;
    }
    
    /**
     * 저장소 조회
     * @param req
     * @return
     * @throws Exception
     */
    private JSONObject info(HttpServletRequest req) throws Exception {

        JSONObject resObj = new JSONObject();
        String seqStore = req.getParameter("seq_store");

        UserSession us = AuthManager.getInstance().getUserSession(req);
        if (us != null) {
            resObj.put("is_partner", us.isPartner());
            
            String seqUser = us.getSeq(); // 초대장 생성자 (저장소 소유자)

            StoreDataAccessObject sdao = new StoreDataAccessObject();
            JSONObject store = sdao.getStore(seqStore);
            
            UserDataAccessObject udao = new UserDataAccessObject();
            String gmail = udao.getGoogleid(seqUser);
            resObj.put("gmail", gmail);

            BoxDataAccessObject boxDao = new BoxDataAccessObject();
            List<JSONObject> boxes = boxDao.getStoreBoxes(seqStore);
            store.put("boxes", JinieboxUtil.listToMap(boxes));

            NanumDataAccessObject nanumDao = new NanumDataAccessObject();
            List<JSONObject> nanums = nanumDao.getStoreNanums(seqStore);
            store.put("nanums", JinieboxUtil.listToMap(nanums));

            InviteDataAccessObject invDao = new InviteDataAccessObject();
            JSONObject invites = invDao.getInvitesJson(seqUser, EnvSYS.CLASS_TYPE_STORE, seqStore);
            store.put("invites", invites);

            ShareDataAccessObject shareDao = new ShareDataAccessObject();
            JSONObject shares = shareDao.list(EnvSYS.CLASS_TYPE_STORE, seqStore);
            store.put("shares", shares);

            GcpService gcpSvc = new GcpService();
            JSONObjectExt chkToken = gcpSvc.checkTokensExpiration(seqUser);
            if (chkToken != null && chkToken.getInt("result") == 0) {
                store.put("gauthcd", EnvSYS.RESCODE_SUCC);
            } else {
                store.put("gauthcd", EnvSYS.RESCODE_FAIL);
            }

            String seqOwner = store.get("seq_owner").toString();
            if (seqUser.equals(seqOwner)) {
                store.put("is_owner", true);
            } else {
                store.put("is_owner", false);
            }
            
            resObj.put("store", store);
            
            List<JSONObject> stores = sdao.getSharedStores(seqUser);
            JSONObject storeOwn = sdao.getOwnStore(seqUser);

            if (stores == null) {
                stores = new ArrayList<JSONObject>();
            }
            stores.add(storeOwn);
            resObj.put("stores", stores);
        }

        resObj.put("code", EnvSYS.RESCODE_SUCC);
        return resObj;
    }
    
    /**
     * 저장소의 박스들을 일괄 삭제한다 (보관박스, 나눔박스)
     * 
     * @param req
     * @return
     * @throws NumberFormatException
     * @throws Exception
     */
    private JSONObject deleteBoxes(HttpServletRequest req) throws Exception {

        JSONObject resObj = new JSONObject();

        String seqStore = req.getParameter("seq_store");
        if (seqStore == null) {
            seqStore = AuthManager.getInstance().getUserSession(req).getSeqDefstore();
        }

        String code = EnvSYS.RESCODE_FAIL;
        String msg = "박스 삭제에 실패하였습니다";

        ShareDataAccessObject shareDao = new ShareDataAccessObject();
        BoxDataAccessObject boxDao = new BoxDataAccessObject();
        ItemDataAccessObject itemDao = new ItemDataAccessObject();

        // 보관박스 삭제
        String boxesStr = req.getParameter("boxes");
        if (boxesStr != null && boxesStr.indexOf(',') > -1) {
            
            String[] boxArr = boxesStr.split(",");
            for (int i = 0; i < boxArr.length; i++) {

                String seqBox = boxArr[i];

                // 삭제할 box내의 모든 item 을 기본박스로 이관한다
                List<JSONObject> itemList = itemDao.getBoxItems(seqBox, null);
                JSONObject defBoxJson = boxDao.getStoreBox(seqStore, EnvSYS.RESERVED_WAITINGBOX);
                JSONObject itemJson = null;
                Iterator<JSONObject> itemIter = itemList.iterator();
                while (itemIter.hasNext()) {
                    itemJson = (JSONObject) itemIter.next();
                    itemDao.updateItem(itemJson.get("seq").toString(), null, null, null, null,defBoxJson.get("seq").toString(), 0);
                }

                // 공유 정보 삭제
                shareDao.delete(EnvSYS.CLASS_TYPE_BOX, seqBox);

                // 박스 삭제
                boxDao.delete(seqBox);
            }

            List<JSONObject> boxes = boxDao.getStoreBoxes(seqStore);
            resObj.put("boxes", JinieboxUtil.listToMap(boxes));
        }

        // 나눔박스 삭제
        NanumitemDataAccessObject nidao = new NanumitemDataAccessObject();
        NanumfavoriteDataAccessObject nfdao = new NanumfavoriteDataAccessObject();
        NanumDataAccessObject ndao = new NanumDataAccessObject();

        String nanumsStr = req.getParameter("nanums");
        if (nanumsStr != null && nanumsStr.indexOf(',') > -1) {
            
            String[] nanumArr = nanumsStr.split(",");
            for (int i = 0; i < nanumArr.length; i++) {
                String seqNanum = nanumArr[i];

                // 나눔함 공유 정보 삭제
                shareDao.delete(EnvSYS.CLASS_TYPE_NANUM, seqNanum);

                // 나눔 아이템 정보 삭제
                nidao.deleteSeqNanum(seqNanum);

                // 나눔 즐겨찾기 정보 삭제
                nfdao.delete(seqNanum);

                // 나눔 박스 삭제
                ndao.delete(seqNanum);
            }

            List sNanums = ndao.getStoreNanums(seqStore);
            resObj.put("nanums", JinieboxUtil.listToMap(sNanums));
        }

        code = EnvSYS.RESCODE_SUCC;
        msg = "박스의 모든 정보(박스, 아이템, 즐겨찾기, 공유)를 삭제하였습니다.";
        
        resObj.put("code", code);
        resObj.put("msg", msg);
        return resObj;
    }
    
    private JSONObject saveSetting(HttpServletRequest req) throws Exception {
        
        String resCode = null, resMsg = null;
        
        UserSession us = AuthManager.getInstance().getUserSession(req);
        
        String seqUser = us.getSeq();
        String imgUseStr = req.getParameter("img_use");
        String storageTypeStr = req.getParameter("storage_type");
        
        if (imgUseStr != null) {
            StoreDataAccessObject sdao = new StoreDataAccessObject();
            
            int imgUse = -1;
            if ("TRUE".equals(imgUseStr.toUpperCase())) {
                imgUse = sdao.IMG_USE_TRUE;
                if (storageTypeStr != null) {

                    int storageType = -1;
                    if ("GOOGLE".equals(storageTypeStr.toUpperCase())) {
                        storageType = sdao.STORAGE_TYPE_GOOGLE;
                    } else if ("JBS".equals(storageTypeStr.toUpperCase())) {
                        storageType = sdao.STORAGE_TYPE_LOCAL;
                    }
                    sdao.setStorageType(seqUser, storageType); //db에 반영
                    us.getDefStoreInfo().put("storage_type", storageType); //세션에 반영
                }
            } else {
                imgUse = sdao.IMG_USE_FALSE;
            }
            sdao.setImguse(seqUser, imgUse); //db에 반영
            us.getDefStoreInfo().put("img_use", imgUse); //세션에 반영
            
            resCode = EnvSYS.RESCODE_SUCC;
            resMsg = "설정을 저장하였습니다";
        } else {
            resCode = EnvSYS.RESCODE_FAIL;
            resMsg = "파라미터값이 없습니다.";
        }
        
        JSONObject resObj = new JSONObject();
        resObj.put("code", resCode);
        resObj.put("msg", resMsg);
        return resObj;
    }
    
    /**
     * 등록대기 아이템 유효기간 설정
     * @param req
     * @return
     * @throws Exception
     */
    private JSONObject setStandbyDays(HttpServletRequest req) throws Exception {

        JSONObject resObj = new JSONObject();
        String code = EnvSYS.RESCODE_FAIL;
        String msg = "아이템 저장에 실패하였습니다";
        
        UserSession us = AuthManager.getInstance().getUserSession(req);
        String seqStore = us.getSeqDefstore();
        String standbyDays = req.getParameter("val");
        
        StoreDataAccessObject storeDao = new StoreDataAccessObject();
        storeDao.setItemStandbyDays(seqStore, (standbyDays != null && NumberUtil.isNumber(standbyDays)) ? Integer.parseInt(standbyDays) * 7 : 7);

        code = EnvSYS.RESCODE_SUCC;
        msg = "저장에 성공하였습니다";

        resObj.put("code", code);
        resObj.put("msg", msg);
        return resObj;

    }
    
}
