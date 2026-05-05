package com.omnibuscode.ctrl;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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
import com.omnibuscode.dao.ShareDataAccessObject;
import com.omnibuscode.dao.StoreDataAccessObject;
import com.omnibuscode.dao.UserDataAccessObject;
import com.omnibuscode.logic.ItemWebService;
import com.omnibuscode.logic.file.FileManager;
import com.omnibuscode.util.JSONObjectExt;
import com.omnibuscode.util.JinieboxUtil;
import com.omnibuscode.utils.ExceptionUtil;
import com.omnibuscode.utils.NumberUtil;
import com.omnibuscode.utils.PropertiesUtil;

/**
 * @author KIUNSEA
 *
 */
@WebServlet("/box")
@MultipartConfig(
		fileSizeThreshold = 1024 * 1024, // 1MB
		maxFileSize = 1024 * 1024 * 10, // 10MB
		maxRequestSize = 1024 * 1024 * 50 // 50MB
)
public class BoxServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
	 */
//	private Log log = LogUtil.getLog(BoxServlet.class);
	private Logger log = LogManager.getLogger(BoxServlet.class);
    
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
            UserSession us = AuthManager.getInstance().getUserSession(req);
            if (AuthManager.getInstance().hasUserAuthed(req)) {
                if (cmdHttp != null) {
                    
                    try {
                        if ("getAddboxLink".equals(cmdHttp)) {
                            resJson = this.getAddboxLink(req);
                        } else if ("add".equals(cmdHttp)) {
                            resJson = this.add(req);
                        } else if ("delete".equals(cmdHttp)) {
                            resJson = this.delete(req);
                        } else if ("update".equals(cmdHttp)) { // 박스 수정
                            resJson = this.update(req);
                        } else if ("addImg".equals(cmdHttp)) { // 이미지 추가
                            resJson = this.addImg(us, reqJson);
                        } else if ("delImg".equals(cmdHttp)) { // 이미지 삭제
                            resJson = this.delImg(req);
                        } else if ("getBoxListLink".equals(cmdHttp)) {
                            resJson = this.getBoxListLink(req);
                        } else if ("list".equals(cmdHttp)) {
                            resJson = this.getBoxList(req);
                        } else if ("info".equals(cmdHttp)) {
                            resJson = this.getBoxInfo(req);
                        } else if ("truncate".equals(cmdHttp)) {
                            resJson = this.truncateBox(req);
                        } else if ("batch".equals(cmdHttp)) {
                            resJson = this.batch(req);
                        } else if ("setDelAfter".equals(cmdHttp)) {
                            resJson = this.setDelAfter(req);
                        }
                    } catch (Exception e) {
                        log.error(ExceptionUtil.getExceptionInfo(e));
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
     * 상자 등록 링크 반환 (by bixby user id)
     * 
     * @param req
     * @return
     * @throws Exception
     */
    private JSONObject getAddboxLink(HttpServletRequest req) throws Exception {
        
        JSONObject resObj = new JSONObject();
		String buid = AuthManager.getInstance().getUserSession(req).getBuid();
		String seqStore = AuthManager.getInstance().getUserSession(req).getSeqDefstore();
        
        String resUrl = PropertiesUtil.get("SERVICE_URL") + "/box_add.html";
        resUrl += "?seq_store=" + seqStore;
        resObj.put("authkey", AuthManager.getInstance().getRandomAuthKey(buid));
        resObj.put("code", EnvSYS.RESCODE_SUCC);
        resObj.put("msg", "보관함을 등록합니다");
        resObj.put("label", "박스추가");
        resObj.put("url", resUrl);
        
        return resObj;
    }
    
    /**
     * 상자 등록 요청 처리
     * 
     * @param req
     * @return
     * @throws Exception
     */
    private JSONObject add(HttpServletRequest req) throws Exception {

        UserSession us = AuthManager.getInstance().getUserSession(req);
        
        JSONObject resObj = new JSONObject();
        String boxName = req.getParameter("box_name");
        String boxDetails = req.getParameter("box_details");
        String seqStore = req.getParameter("seq_store");
        
        if (!NumberUtil.isNumber(seqStore)) {
            seqStore = us.getSeqDefstore();
        }

        BoxDataAccessObject boxDao = new BoxDataAccessObject();
        int seqBox = boxDao.insert(boxName, boxDetails, null, seqStore);

        if (seqBox > -1) {
            JSONObject boxes = JinieboxUtil.listToMap(boxDao.getStoreBoxes(seqStore));
            this.addExtInfo(req, boxes);
            
            resObj.put("boxes", boxes);
            resObj.put("code", EnvSYS.RESCODE_SUCC);
            resObj.put("msg", "보관함 등록이 성공하였습니다");
        }

        return resObj;
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
        
        String seqStore = req.getParameter("seq_store");
        if (JinieboxUtil.isEmpty(seqStore)) {
            UserSession us = AuthManager.getInstance().getUserSession(req);
            seqStore = us.getSeqDefstore();
        }
        String buid = req.getParameter("buid");
        
        String code = EnvSYS.RESCODE_FAIL;
        String msg = "박스(보관함) 업데이트에 실패하였습니다 ";
        
        
		String seqBox = req.getParameter("seq_box");
        if (NumberUtil.isNumber(seqBox) && Integer.parseInt(seqBox) > -1) {
            
            ShareDataAccessObject sdao = new ShareDataAccessObject();
            BoxDataAccessObject boxDao = new BoxDataAccessObject();
            ItemDataAccessObject itemDao = new ItemDataAccessObject();
            
            // 삭제할 box내의 모든 item 을 기본박스로 이관한다
            List<JSONObject> itemList = itemDao.getBoxItems(seqBox, null);
            JSONObject defBoxJson = boxDao.getStoreBox(seqStore, EnvSYS.RESERVED_WAITINGBOX);
            JSONObject itemJson = null;
            Iterator<JSONObject> itemIter = itemList.iterator();
            while(itemIter.hasNext()) {
                itemJson = (JSONObject) itemIter.next();
                itemDao.updateItem(itemJson.get("seq").toString(), null, null, null, null, defBoxJson.get("seq").toString(), 0);
            }
            
            // 공유 정보 삭제
            sdao.delete(EnvSYS.CLASS_TYPE_BOX, seqBox);
            
            // 박스 삭제
            boxDao.delete(seqBox);
            List<JSONObject> boxes = boxDao.getStoreBoxes(seqStore);
            resObj.put("boxes", JinieboxUtil.listToMap(boxes));
            
            code = EnvSYS.RESCODE_SUCC;
            msg = "박스(보관함)를 삭제하였습니다\n모든 아이템은 '" + EnvSYS.RESERVED_WAITINGBOX + "'로 이동하였습니다";
        } else {
            code = EnvSYS.RESMSG_FAIL;
            msg = "박스(보관함)를 삭제에 실패하였습니다";
        }
        
        resObj.put("code", code);
        resObj.put("msg", msg);
        return resObj;
    }
    
  //XXX 삭제 예정
//    /**
//     * 박스들을 일괄 삭제한다
//     * @param req
//     * @return
//     * @throws NumberFormatException
//     * @throws Exception
//     */
//    private JSONObject deleteBoxes(HttpServletRequest req) throws Exception {
//
//        JSONObject resObj = new JSONObject();
//        
//        String seqStore = req.getParameter("seq_store");
//        String buid = req.getParameter("buid");
//        
//        String code = EnvSYS.RESCODE_FAIL;
//        String msg = "박스(보관함) 업데이트에 실패하였습니다 ";
//        
//        ShareDataAccessObject shareDao = new ShareDataAccessObject();
//        BoxDataAccessObject boxDao = new BoxDataAccessObject();
//        ItemDataAccessObject itemDao = new ItemDataAccessObject();
//        
//        String boxesStr = req.getParameter("boxes");
//        String[] boxArr = boxesStr.split(",");
//        if (boxArr.length > 0) {
//            for (int i = 0; i < boxArr.length; i++) {
//
//                String seqBox = boxArr[i];
//
//                // 삭제할 box내의 모든 item 을 기본박스로 이관한다
//                List<JSONObject> itemList = itemDao.getBoxItems(seqBox, null);
//                JSONObject defBoxJson = boxDao.getStoreBox(seqStore, EnvSYS.RESERVED_DEFAULTBOX);
//                JSONObject itemJson = null;
//                Iterator<JSONObject> itemIter = itemList.iterator();
//                while (itemIter.hasNext()) {
//                    itemJson = (JSONObject) itemIter.next();
//                    itemDao.updateItem(itemJson.get("seq").toString(), null, null, null, null, defBoxJson.get("seq").toString());
//                }
//
//                // 공유 정보 삭제
//                shareDao.delete(null, seqBox, null);
//
//                // 박스 삭제
//                boxDao.delete(seqBox);
//
//            }
//            code = EnvSYS.RESCODE_SUCC;
//            msg = "박스(보관함)를 삭제하였습니다\n모든 아이템은 '" + EnvSYS.RESERVED_DEFAULTBOX + "'로 이동하였습니다";
//
//            List<JSONObject> boxes = boxDao.getStoreBoxes(seqStore);
//            resObj.put("boxes", JinieboxUtil.listToMap(boxes));
////            resObj.put("boxes", boxes);
//        } else {
//            msg = "박스 목록이 없습니다.";
//        }
//        
//        
//        
//        resObj.put("code", code);
//        resObj.put("msg", msg);
//        return resObj;
//    }
    
    private JSONObject update(HttpServletRequest req) throws Exception {

        JSONObject resObj = new JSONObject();
        
        String buid = AuthManager.getInstance().getUserSession(req).getBuid();
        String seqBox = req.getParameter("seq_box");
        String seqStore = req.getParameter("seq_store");
        String boxName = req.getParameter("box_name");
        String boxDetails = req.getParameter("box_details");
        
        BoxDataAccessObject boxDao = new BoxDataAccessObject();

        if (seqBox != null) {
        	boxDao.update(seqBox, boxName, boxDetails, null, seqStore);
        	
        	resObj.put("seqBox", seqBox);
        	resObj.put("boxName", boxName);
        	resObj.put("boxDetails", boxDetails);
        	resObj.put("seqStore", seqStore);
        	
        	resObj.put("code", EnvSYS.RESCODE_SUCC);
            resObj.put("msg", "박스 정보를 수정하였습니다");
        } else {
            resObj.put("code", EnvSYS.RESCODE_FAIL);
            resObj.put("msg", boxName + " 정보 수정이 실패하였습니다");
        }
        
        return resObj;
    }
    
    /**
     * 박스 사진 추가
     * @param reqJson
     * @return
     * @throws Exception
     */
    private JSONObject addImg(UserSession us,  JSONObjectExt reqJson) throws Exception {

        JSONObject resObj = new JSONObject();
        
        String seqUser = us.getSeq();
        String seqBox = reqJson.getString("seq_box");

        FileManager fm = new FileManager(this.getServletContext().getRealPath("/"), PropertiesUtil.get("BOX_IMG_REPO"), us.getGoogleAccessToken());
        // 사진이미지 저장
        List<File> imgs = reqJson.getFiles();
        fm.restoreFile(seqUser, seqBox, EnvSYS.CLASS_TYPE_BOX, imgs);
        
        // 이미지 목록 반환
        Object stObj = us.getDefStoreInfo().get("storage_type");
        int storageType = stObj != null ? Integer.parseInt(stObj.toString()) : -1;
        JSONObject imageInfo = fm.getImageInfo(seqUser, storageType, seqBox, EnvSYS.CLASS_TYPE_BOX);
        if (imageInfo != null) {
            resObj.put("image_info", imageInfo);
            resObj.put("code", EnvSYS.RESCODE_SUCC);
            resObj.put("msg", "사진을 저장하였습니다.");
        } else {
            resObj.put("code", EnvSYS.RESCODE_FAIL);
            resObj.put("msg", "사진 저장에 실패하였습니다.");
        }
        
        return resObj;
    }
    
    /**
     * 박스 사진 삭제
     * @param reqJson
     * @return
     * @throws Exception
     */
    private JSONObject delImg(HttpServletRequest req) throws Exception {

        JSONObject resObj = new JSONObject();
        
        UserSession us = AuthManager.getInstance().getUserSession(req);
        String seqUser = us.getSeq();
        String seqBox = req.getParameter("seq_box");
        String imgsrc = req.getParameter("imgsrc");
        String imgid = req.getParameter("imgid");
        
        FileManager fm = new FileManager(this.getServletContext().getRealPath("/"), PropertiesUtil.get("BOX_IMG_REPO"), us.getGoogleAccessToken());
        JSONObjectExt resultInfo = fm.deleteImage(seqUser, seqBox, EnvSYS.CLASS_TYPE_BOX, imgid);
        boolean result = resultInfo.getBoolean("result");
        
        // 이미지 목록 반환
        Object stObj = us.getDefStoreInfo().get("storage_type");
        int storageType = stObj != null ? Integer.parseInt(stObj.toString()) : -1;
        JSONObject imageInfo = fm.getImageInfo(seqUser, storageType, seqBox, EnvSYS.CLASS_TYPE_BOX);
        if (result) {
            resObj.put("image_info", imageInfo);
            resObj.put("code", EnvSYS.RESCODE_SUCC);
            resObj.put("msg", "사진을 삭제하였습니다.");
        } else {
            resObj.put("code", EnvSYS.RESCODE_FAIL);
            resObj.put("msg", "사진 삭제에 실패하였습니다.");
        }

        return resObj;
    }
    
    /**
     * 상자 목록 링크 반환 (for bixby)
     * 
     * @param req
     * @return
     * @throws Exception
     */
    private JSONObject getBoxListLink(HttpServletRequest req) throws Exception {

        JSONObject resObj = new JSONObject();
        String buid = AuthManager.getInstance().getUserSession(req).getBuid();
		String seqStore = AuthManager.getInstance().getUserSession(req).getSeqDefstore();
        
        BoxDataAccessObject boxDao = new BoxDataAccessObject();
        List<JSONObject> boxes = boxDao.getStoreBoxes(seqStore);

        String resUrl = PropertiesUtil.get("SERVICE_URL") + "/list_box.jsp";
        resUrl += "?seq_store=" + seqStore;
        resObj.put("authkey", AuthManager.getInstance().getRandomAuthKey(buid));
        resObj.put("code", EnvSYS.RESCODE_SUCC);
        resObj.put("msg", "보관함이 총 " + boxes.size() + "개가 있습니다 자세한 내용은 링크를 확인해주세요");
        resObj.put("label", "박스목록");
        resObj.put("url", resUrl);

        return resObj;
    }
    
    /**
     * 상자 목록 반환
     * @param req
     * @return
     * @throws Exception
     */
    private JSONObject getBoxList(HttpServletRequest req) throws Exception {
        
        JSONObject resObj = new JSONObject();
        
        UserSession us = AuthManager.getInstance().getUserSession(req);
		String seqStore = us.getSeqDefstore();
		String seqUser = us.getSeq();
		
        Object stObj = us.getDefStoreInfo().get("storage_type");
        int storageType = stObj != null ? Integer.parseInt(stObj.toString()) : -1;
        
        resObj.put("first_visit", us.checkFirstVisit());
        UserDataAccessObject userDao = new UserDataAccessObject();
        userDao.setVisited(seqUser); //db반영
        us.setFirstVisit(0); //세션반영
        
        
        BoxDataAccessObject boxDao = new BoxDataAccessObject();
        JSONObject boxes = JinieboxUtil.listToMap(boxDao.getStoreBoxes(seqStore));
        this.addExtInfo(req, boxes);
        
        resObj.put("boxes", boxes);
        resObj.put("code", EnvSYS.RESCODE_SUCC);
        resObj.put("msg", "");
        return resObj;
    }
    
    private JSONObject getBoxInfo(HttpServletRequest req) throws Exception {

        JSONObject resObj = new JSONObject();
        
        UserSession us = AuthManager.getInstance().getUserSession(req);
        String seqBox = req.getParameter("seq_box");
        
        BoxDataAccessObject boxDao = new BoxDataAccessObject();
        JSONObject boxJson = boxDao.getBox(seqBox);
        String seqStore = boxJson.get("seq_store").toString();
        StoreDataAccessObject storeDao = new StoreDataAccessObject();
        JSONObject storeJson = storeDao.getStore(seqStore);
        Object stObj = us.getDefStoreInfo().get("storage_type");
        int storageType = stObj != null ? Integer.parseInt(stObj.toString()) : -1;

        HttpSession sess = req.getSession(true);
        Object usObj = sess.getAttribute(EnvSYS.KEY_USER_SESSION); // saved user session
        if (usObj != null) {
            String seqOwner = us.getSeq(); // 초대장 생성자 (저장소 소유자)

            BoxDataAccessObject bdao = new BoxDataAccessObject();
            JSONObject box = bdao.getBox(seqBox);

            JSONObject items = new ItemWebService().getBoxItems(this.getServletContext().getRealPath("/"), us, seqBox);
            box.put("items", items);
            
            InviteDataAccessObject idao = new InviteDataAccessObject();
            JSONObject invites = idao.getInvitesJson(seqOwner, EnvSYS.CLASS_TYPE_BOX, seqBox);
            box.put("invites", invites);
            box.put("seq_owner", seqOwner);
            
            FileManager fm = new FileManager(this.getServletContext().getRealPath("/"), PropertiesUtil.get("BOX_IMG_REPO"), us.getGoogleAccessToken());
            JSONObject imageInfo = fm.getImageInfo(seqOwner, storageType, seqBox, EnvSYS.CLASS_TYPE_BOX);
            box.put("image_info", imageInfo);
            
            box.put("standby_days", storeJson.get("standby_days"));
            
            resObj.put("box", box);
            
            List<JSONObject> boxes = bdao.getStoreBoxes(seqStore);
            resObj.put("boxes", boxes);
            
            NanumDataAccessObject nDao = new NanumDataAccessObject();
            List<JSONObject> nanums = nDao.getStoreNanums(seqStore);  //자신의 저장소에 있는 나눔박스 목록
            List<JSONObject> nanumsNearby = nDao.getNearbyNanums(us.getSeq()); //친구 또는 회원에게 공유받은 나눔박스 목록
            nanums.addAll(nanumsNearby);
            resObj.put("nanums", nanums);
        }
        
        resObj.put("code", EnvSYS.RESCODE_SUCC);
        resObj.put("msg", "");
        return resObj;
    }
    
    /**
     * 상자의 모든 아이템 삭제
     * 
     * @param req
     * @return
     * @throws Exception
     */
    private JSONObject truncateBox(HttpServletRequest req) throws Exception {

        JSONObject resObj = new JSONObject();
        
        String boxName = req.getParameter("box_name");
		String seqStore = AuthManager.getInstance().getUserSession(req).getSeqDefstore();
        
        BoxDataAccessObject boxDao = new BoxDataAccessObject();
        JSONObject boxObj = boxDao.getStoreBox(seqStore, boxName);

        if (boxObj != null) {
        	boolean bRtn = boxDao.truncate(boxObj.get("seq").toString());
            if (bRtn) {
                resObj.put("code", EnvSYS.RESCODE_SUCC);
                resObj.put("msg", boxName + " 보관함을 비웠습니다");
            } else {
            	resObj.put("code", EnvSYS.RESCODE_FAIL);
                resObj.put("msg", "동작에 실패하였습니다");
            }
        } else {
        	resObj.put("code", EnvSYS.RESCODE_NOBOX);
            resObj.put("msg", boxName + " 보관함이 없습니다");
        }
        
        return resObj;
    }
    
    /**
     * 복수개의 박스를 일괄 처리
     * 
     * @param req
     * @return
     * @throws Exception
     */
    private JSONObject batch(HttpServletRequest req) throws Exception {

        JSONObject resObj = new JSONObject();
        String code = EnvSYS.RESCODE_FAIL;
        String msg = "아이템 저장에 실패하였습니다 ";

        String boxesStr = req.getParameter("boxes");
        String[] boxArr = boxesStr.split(",");
        String seqStore = req.getParameter("seq_store");
        String toStore = req.getParameter("ebmd_to_store");
        String inputDetail = req.getParameter("ebmd_input_detail");

        BoxDataAccessObject boxDao = new BoxDataAccessObject();
        for (int i = 0; i < boxArr.length; i++) {
            boxDao.update(boxArr[i], null, inputDetail, null, toStore);
        }

        JSONObject boxes = JinieboxUtil.listToMap(boxDao.getStoreBoxes(seqStore));
        this.addExtInfo(req, boxes);
        resObj.put("boxes", boxes);

        code = EnvSYS.RESCODE_SUCC;
        msg = "아이템 저장에 성공하였습니다";

        resObj.put("code", code);
        resObj.put("msg", msg);
        return resObj;

    }
	
    /**
     * 박스 목록에 아이템 정보와 이미지 정보를 추가한다
     * 
     * @param boxes
     * @param seqUser
     * @param storageType
     * @throws Exception 
     */
    private void addExtInfo(HttpServletRequest req, JSONObject boxes) throws Exception {

        UserSession us = AuthManager.getInstance().getUserSession(req);
        
        String seqUser = us.getSeq();
        Object stObj = us.getDefStoreInfo().get("storage_type");
        int storageType = stObj != null ? Integer.parseInt(stObj.toString()) : -1;

        Iterator<String> seqBoxIter = boxes.keySet().iterator();
        String seqBox = null;
        JSONObject boxJo = null;

        while (seqBoxIter.hasNext()) {
            seqBox = seqBoxIter.next().toString();
            boxJo = (JSONObject) boxes.get(seqBox);

            JSONObject items = new ItemWebService().getBoxItems(this.getServletContext().getRealPath("/"), us, seqBox);
            boxJo.put("items", items);

            FileManager fm = new FileManager(this.getServletContext().getRealPath("/"),
                    PropertiesUtil.get("BOX_IMG_REPO"), us.getGoogleAccessToken());
            JSONObject imageInfo = fm.getImageInfo(seqUser, storageType, seqBox, EnvSYS.CLASS_TYPE_BOX);
            boxJo.put("image_info", imageInfo);
        }
    }

    /**
     * 등록대기 아이템 유효기간 설정
     * @param req
     * @return
     * @throws Exception
     */
    private JSONObject setDelAfter(HttpServletRequest req) throws Exception {

        JSONObject resObj = new JSONObject();
        String code = EnvSYS.RESCODE_FAIL;
        String msg = "아이템 저장에 실패하였습니다";
        
        String delAfter = req.getParameter("val");
        String seqBox = req.getParameter("seq_box");
        
        BoxDataAccessObject boxDao = new BoxDataAccessObject();
        boxDao.setItemDeleteAfterDays(seqBox, (delAfter != null && NumberUtil.isNumber(delAfter)) ? Integer.parseInt(delAfter) * 7 : 7);

        code = EnvSYS.RESCODE_SUCC;
        msg = "저장에 성공하였습니다";

        resObj.put("code", code);
        resObj.put("msg", msg);
        return resObj;

    }
    
//	/**
//	 * List 
//	 * @param boxes
//	 * @return
//	 */
//	private JSONObject boxesListToJson(List<JSONObject> boxes) {
//		JSONObject boxesJson = new JSONObject();
//		Iterator<JSONObject> boxesIter = boxes.iterator();
//		JSONObject box = null;
//		while (boxesIter.hasNext()) {
//			box = (JSONObject) boxesIter.next();
//			boxesJson.put(box.get("seq"), box);
//		}
//		return boxesJson;
//	}
}
