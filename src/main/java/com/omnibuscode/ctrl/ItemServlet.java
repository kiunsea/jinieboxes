package com.omnibuscode.ctrl;



import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.omnibuscode.auth.AuthInfo;
import com.omnibuscode.auth.AuthManager;
import com.omnibuscode.base.EnvSYS;
import com.omnibuscode.base.UserSession;
import com.omnibuscode.dao.BoxDataAccessObject;
import com.omnibuscode.dao.ItemDataAccessObject;
import com.omnibuscode.dao.NanumitemDataAccessObject;
import com.omnibuscode.logic.ItemBixbyService;
import com.omnibuscode.logic.ItemWebService;
import com.omnibuscode.logic.NanumWebService;
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
@WebServlet("/item")
@MultipartConfig(
		fileSizeThreshold = 1024 * 1024, // 1MB
		maxFileSize = 1024 * 1024 * 10, // 10MB
		maxRequestSize = 1024 * 1024 * 50 // 50MB
)
public class ItemServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 서버 환경 변경시 log4j.properties 설정 정보도 함께 변경 필요
	 */
	private Logger log = LogManager.getLogger(ItemServlet.class);
    
    public void init() {
        /* InitializeEnv 가 부팅 시 standalone/WAR 모드에 맞게 이미 설정 — 덮어쓰지 않음 */
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
        	if (AuthManager.getInstance().hasUserAuthed(req)) {
                if (cmdHttp != null) {
                    
                    reqJson.put("buid", AuthManager.getInstance().getUserSession(req).getBuid());
                    reqJson.put("seq_store", AuthManager.getInstance().getUserSession(req).getSeqDefstore());
                    
                    if ("store".equals(cmdHttp)) { // 아이템을 저장한다
                        resJson = this.store(req, reqJson);
                    } else if ("take".equals(cmdHttp)) { // 아이템을 꺼낸다
                        resJson = this.take(req);
                    } else if ("check".equals(cmdHttp)) { // 지정한 아이템이 있는지 여부를 확인한다
                        resJson = this.check(req);
                    } else if ("where".equals(cmdHttp)) { // 아이템들이 어느 저장소에 저장되어 있는지 확인한다
                        resJson = this.where(req);
                    } else if ("when".equals(cmdHttp)) { // 저장된 특정 아이템들이 언제 입력되었는지 확인한다
                        resJson = this.when(req);
                    } else if ("listLink".equals(cmdHttp)) { // 아이템 목록 링크 반환
                        resJson = this.listLink(req);
                    } else if ("list".equals(cmdHttp)) { // 아이템 목록을 반환
                        String clientType = AuthManager.getInstance().getUserAuthInfo(req).getClientType();
                        if (clientType.equals(EnvSYS.KEY_CLIENT_TYPE_BIXBY)) {
                            resJson = new ItemBixbyService().list(reqJson);
                        } else if (clientType.equals(EnvSYS.KEY_CLIENT_TYPE_WEB)) {
                            resJson = new ItemWebService().list(reqJson);
                        }
                    } else if ("addImg".equals(cmdHttp)) { // 이미지 추가
                        resJson = this.addImg(req, reqJson);
                    } else if ("delImg".equals(cmdHttp)) { // 이미지 삭제
                        resJson = this.delImg(req);
                    } else if ("qty".equals(cmdHttp)) { // 지정한 아이템의 갯수를 반환
                        resJson = this.quantity(req);
                    } else if ("truncate".equals(cmdHttp)) { // 지정한 보관함(박스)의 아이템을 모두 삭제한다
                    	resJson = this.truncate(req);
                    } else if ("batch".equals(cmdHttp)) { // 아이템 목록의 속성을 일괄 변경한다
                    	resJson = this.batch(req);
                    } else if ("takeItems".equals(cmdHttp)) { // 아이템들을 일괄 삭제한다
                    	resJson = this.takeItems(req);
                    }
                } else {
                    resJson = new JSONObject();
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
            e.printStackTrace();
            log.error(ExceptionUtil.getExceptionInfo(e));
        }

        if (resJson == null) resJson = new JSONObject();
        resJson.put("buid", req.getParameter("buid")); //(Bixby)html 페이지간의 redirect 이동을 위한 parameter 전달용
//        log.debug("res - " + resObj);
        
        res.setCharacterEncoding("UTF-8");
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Cache-Control", "no-cache");
        PrintWriter out = res.getWriter();
        out.println(resJson);
        out.flush();
        out.close();
    }
    
    /**
     * 아이템을 저장한다
     * 
     * @param reqJson
     * @return
     * @throws Exception
     */
    private JSONObject store(HttpServletRequest req, JSONObjectExt reqJson) throws Exception {

    	JSONObject resObj = null;
    	UserSession us = AuthManager.getInstance().getUserSession(req);
    	String addMode = reqJson.getString("add_mode");
    	
		// 아이템을 박스에 등록
		ItemWebService itemSvc = new ItemWebService();
		if (addMode == null || "barcode".equals(addMode)) {
			resObj = itemSvc.store(this.getServletContext().getRealPath("/"), us, reqJson);

			if (resObj.get("code").toString().equals(EnvSYS.RESCODE_SUCC)) {
				String seqItem = resObj.get("seq_item").toString();

				// 나눔함 요청 확인 및 처리
				String seqNanum = reqJson.getString("seq_nanum");
				if (!(JinieboxUtil.isEmpty(seqItem) || JinieboxUtil.isEmpty(seqNanum))) {
					NanumWebService nanumSvc = new NanumWebService();
					String itemDetail = reqJson.getString("item_nanum_detail");
					resObj = nanumSvc.registNaumItem(seqItem, seqNanum, itemDetail);
				}
			}

			return resObj;
		} else if ("receipt".equals(addMode)) {
			BoxDataAccessObject boxDao = new BoxDataAccessObject();
			JSONObject pendboxJson = boxDao.getPendBox(us.getSeqDefstore());
	        String seqPendBox = pendboxJson.get("seq").toString();
			
			JSONArray items = (JSONArray) reqJson.get("items");
			
			JSONObject itemJson = null;
			Iterator<JSONObject> itemIter = items.iterator();
			while(itemIter.hasNext()) {
				itemJson = (JSONObject) itemIter.next();
				itemJson.put("seq_box", seqPendBox); //등록대기 보관함
				itemSvc.store(null, us, new JSONObjectExt(itemJson));
			}
			
			resObj = new JSONObject();
			resObj.put("code", EnvSYS.RESCODE_SUCC);
	        resObj.put("msg", "저장 성공적~~~");
	        return resObj;
		}
		
		return null;
    }
    
    /**
     * 아이템을 꺼내어 삭제한다
     * 
     * @param req
     * @return
     * @throws NumberFormatException
     * @throws Exception
     */
    private JSONObject take(HttpServletRequest req) throws NumberFormatException, Exception {

        JSONObject resObj = new JSONObject();
        
        String seqItem = req.getParameter("seq_item");
        String seqBox = req.getParameter("seq_box");
        String buid = req.getParameter("buid");
        String boxName = req.getParameter("box_name");
        String itemName = req.getParameter("item_name");
        String itemQty = req.getParameter("item_qty");
        
        String seqStore = AuthManager.getInstance().getUserSession(req).getSeqDefstore();

        //수량이 없는 경우 1로 초기화
        if (itemQty == null || itemQty.length() < 1)
            itemQty = "1";
        
        BoxDataAccessObject boxDao = new BoxDataAccessObject();
        ItemDataAccessObject itemDao = new ItemDataAccessObject();

        String code = EnvSYS.RESCODE_FAIL;
        String msg = "아이템 업데이트에 실패하였습니다 ";
		if (seqBox != null && seqItem != null) { // 특정 박스의 특정 아이템을 삭제
			if (itemDao.deleteItem(seqItem)) {
			    NanumitemDataAccessObject nidao = new NanumitemDataAccessObject();
			    nidao.deleteSeqItem(seqItem);
			    
				code = EnvSYS.RESCODE_SUCC;
                msg = "보관함에서 아이템을(를) 삭제하였습니다";
                List<JSONObject> items = itemDao.getBoxItems(seqBox, null);
                resObj.put("items", JinieboxUtil.listToMap(items));
			}
        } else if ("ALL".equals(itemQty.toUpperCase())) { // 저장소내에 있는 지정된 아이템 모두를 삭제
            if (boxName == null || "전부".equals(boxName) || "모두".equals(boxName) || "모든상자".equals(boxName) || "모든박스".equals(boxName)) {
            	/** 저장소내의 모든 박스에서 해당 아이템을 삭제 */
                int[] seqBoxes = boxDao.getAllSeq(seqStore);
                for (int i = 0; i < seqBoxes.length; i++) {
                    itemDao.deleteItem(Integer.toString(seqBoxes[i]), itemName);
                    code = EnvSYS.RESCODE_SUCC;
                    msg = "모든 보관함에서 " + itemName + "을(를) 삭제하였습니다";
                }
            } else {
            	/** 특정 박스에서 특정 아이템을 모두 삭제 */
                JSONObject box = boxDao.getStoreBox(seqStore, boxName);
                if (box != null) {
                    itemDao.deleteItem(box.get("seq").toString(), itemName);
                    code = EnvSYS.RESCODE_SUCC;
                    msg = box.get("name") + "에서 모든 " + itemName + "을 삭제하였습니다";
                }
            }
        } else { // 아이템의 수량을 변경
        	/** 특정 박스에서 특정 아이템의 수량을 변경 */
            JSONObject box = boxDao.getStoreBox(seqStore, boxName);
            if (box != null) {
                int qtyReq = 1;
                if (NumberUtil.isNumber(itemQty)) {
                    qtyReq = Integer.parseInt(itemQty);
                }

                List<JSONObject> items = itemDao.getBoxItems(box.get("seq").toString(), itemName);
                if (items != null && items.size() > 0) {
                    JSONObject item = null;
                    Iterator<JSONObject> itemIter = items.iterator();
                    while (itemIter.hasNext()) {
                        item = itemIter.next();
                        int qtyStored = Integer.parseInt(item.get("qty").toString());
                        if (qtyStored < qtyReq) {
                            itemDao.deleteItem(item.get("seq").toString());
                            qtyReq = qtyReq - qtyStored; // 다음 아이템 수량에 적용
                        } else if (qtyStored == qtyReq) { // 저장된 수량이 동일하다면 아이템 삭제후 중지
                            itemDao.deleteItem(item.get("seq").toString());
                            msg = boxName + " 보관함에서 " + itemName + "을(를) 삭제하였습니다";
                            break;
                        } else if (qtyStored > qtyReq) {
                            itemDao.updateQty(item.get("seq").toString(), -qtyReq);
                            msg = boxName + " " + itemName + "의 수량을 " + (qtyStored - qtyReq) + "개로 변경 하였습니다";
                            break;
                        }
                    }
                    code = EnvSYS.RESCODE_SUCC;                    
                            
                } else {
                    msg += itemName + "이(가) " + boxName + "에 없습니다";
                }

            } else {
                msg += boxName + " 보관함을 찾을 수 없습니다";
            }
        }
        
        resObj.put("code", code);
        resObj.put("msg", msg);
        return resObj;
    }
    
    /**
     *  아이템들을 일괄 삭제한다
     * @param req
     * @return
     * @throws NumberFormatException
     * @throws Exception
     */
    private JSONObject takeItems(HttpServletRequest req) throws NumberFormatException, Exception {

        JSONObject resObj = new JSONObject();
        
        String seqBox = req.getParameter("seq_box");
        String buid = req.getParameter("buid");
        
        String seqStore = AuthManager.getInstance().getUserSession(req).getSeqDefstore();

        String code = EnvSYS.RESCODE_FAIL;
        String msg = "아이템 업데이트에 실패하였습니다 ";
        
        BoxDataAccessObject boxDao = new BoxDataAccessObject();
		if (seqBox != null) { // 특정 박스의 특정 아이템을 삭제
			String itemsStr = req.getParameter("items");
			String[] itemArr = itemsStr.split(",");
			
			NanumitemDataAccessObject nidao = new NanumitemDataAccessObject();
			ItemDataAccessObject itemDao = new ItemDataAccessObject();
			for (int i = 0; i < itemArr.length; i++) {
			    nidao.deleteSeqItem(itemArr[i]);
				itemDao.deleteItem(itemArr[i]);
			}
			code = EnvSYS.RESCODE_SUCC;
            msg = "박스(보관함)에서 아이템을(를) 삭제하였습니다";
			
            List<JSONObject> items = itemDao.getBoxItems(seqBox, null);
            resObj.put("items", items);
        }
        
        resObj.put("code", code);
        resObj.put("msg", msg);
        return resObj;
    }
    
    /**
     * 지정한 아이템이 있는지 여부를 확인한다
     * @param req
     * @param seqStore
     * @return
     * @throws Exception
     */
    private JSONObject check(HttpServletRequest req) throws Exception {

        String boxName = req.getParameter("box_name");
        String itemName = req.getParameter("item_name");

        String seqStore = AuthManager.getInstance().getUserSession(req).getSeqDefstore();
        
        BoxDataAccessObject boxDao = new BoxDataAccessObject();
        ItemDataAccessObject itemDao = new ItemDataAccessObject();

        JSONObject box = boxDao.getStoreBox(seqStore, boxName);
        List<JSONObject> items = itemDao.getBoxItems(box.get("seq").toString(), itemName);

        JSONObject resObj = new JSONObject();
        resObj.put("code", EnvSYS.RESCODE_SUCC);
        StringBuffer msgSb = new StringBuffer();
        if (items.size() > 0) {
            int itemCnt = 0;
            Iterator<JSONObject> itemIter = items.iterator();
            JSONObject item = null;
            while (itemIter.hasNext()) {
                item = itemIter.next();
                itemCnt += Integer.parseInt(item.get("qty").toString());
            }
            msgSb.append(itemName + "은(는) " + itemCnt + "개가 있습니다");            
        } else {
            if (boxName != null)
                msgSb.append(boxName+"에 ");
            msgSb.append(itemName + "은(는) 없습니다");
        }
        resObj.put("msg", msgSb.toString());
        
        return resObj;
    }
    
    /**
     * 아이템들이 어느 저장소에 저장되어 있는지 확인한다 
     * @param req
     * @param seqStore
     * @return
     * @throws Exception
     */
    private JSONObject where(HttpServletRequest req) throws Exception {

        JSONObject resObj = new JSONObject();

        String seqStore = AuthManager.getInstance().getUserSession(req).getSeqDefstore();
        String boxName = req.getParameter("box_name");
        String itemName = req.getParameter("item_name");
        
        ItemDataAccessObject itemDao = new ItemDataAccessObject();
        List<JSONObject> items = itemDao.getStoreItems(seqStore, boxName, itemName);

        StringBuffer msgSb = new StringBuffer();
        if (items.size() > 0) {
            msgSb.append(itemName + "은(는) ");

            Map<Object, Integer> imap = new HashMap<Object, Integer>();
            JSONObject item = null;
            Iterator<JSONObject> itemIter = items.iterator();
            while (itemIter.hasNext()) {
                item = itemIter.next();
                if (item.get("name").toString().equals(itemName)) {
                    int qty = 0;
                    if (imap.containsKey(item.get("bname"))) {
                        qty = Integer.parseInt(imap.get(item.get("bname")).toString());
                        qty += Integer.parseInt(item.get("qty").toString());
                    } else {
                        qty = Integer.parseInt(item.get("qty").toString());
                    }
                    imap.put(item.get("bname"), qty);
                }
            }

            String bname, iqty = null;
            Iterator<Object> iMapKeyIter = imap.keySet().iterator();
            while (iMapKeyIter.hasNext()) {
                bname = iMapKeyIter.next().toString();
                iqty = imap.get(bname).toString();
                msgSb.append(" " + bname + "에 " + iqty + "개,");
            }
            if (imap.size() > 0)
                msgSb.deleteCharAt(msgSb.length() - 1);
            msgSb.append(" 있습니다");

            resObj.put("code", EnvSYS.RESCODE_SUCC);
            resObj.put("msg", msgSb.toString());
        } else {
            if (boxName != null)
                msgSb.append(boxName + "에 ");
            msgSb.append(itemName + "은(는) 없습니다");
            resObj.put("code", EnvSYS.RESCODE_SUCC);
            resObj.put("msg", msgSb.toString());
        }
        return resObj;
    }
    
    /**
     * 저장된 특정 아이템들이 언제 입력되었는지 확인한다
     * @param req
     * @param seqStore
     * @return
     * @throws Exception
     */
    private JSONObject when(HttpServletRequest req) throws Exception {

        JSONObject resObj = new JSONObject();
        
        String seqStore = AuthManager.getInstance().getUserSession(req).getSeqDefstore();
        String boxName = req.getParameter("box_name");
        String itemName = req.getParameter("item_name");

        ItemDataAccessObject itemDao = new ItemDataAccessObject();
        List<JSONObject> items = itemDao.getStoreItems(seqStore, boxName, itemName);

        StringBuffer msgSb = new StringBuffer();
        if (items.size() > 0) {
            if (items.size() == 1) {
                JSONObject item = items.get(0);
                String insdate = item.get("insdate").toString();                
                msgSb.append(item.get("bname") + "의 " + itemName + "은(는) " + insdate.substring(0, 4) + "년"
                        + insdate.substring(4, 6) + "월" + insdate.substring(6) + "일에 저장 되었습니다");
                resObj.put("msg", msgSb.toString());
            } else {
                resObj = this.where(req);
                String tmpMsg = resObj.get("msg").toString();
                resObj.put("msg", tmpMsg+" 자세한 내용은 전체 목록을 조회해 주세요");
            }
            resObj.put("code", EnvSYS.RESCODE_SUCC);           
        } else {
            if (boxName != null)
                msgSb.append(boxName+"에 ");
            msgSb.append(itemName + "은(는) 없습니다");
            resObj.put("code", EnvSYS.RESCODE_SUCC);
            resObj.put("msg", msgSb.toString());
        }

        return resObj;
    }
    
    
    /**
     * 아이템 목록 링크 반환
     * @param req
     * @return
     * @throws Exception
     */
    private JSONObject listLink(HttpServletRequest req) throws Exception {

        JSONObject resObj = new JSONObject();
        
        String buid = AuthManager.getInstance().getUserSession(req).getBuid();
		String seqStore = AuthManager.getInstance().getUserSession(req).getSeqDefstore();
		
        ItemDataAccessObject itemDao = new ItemDataAccessObject();
        String boxName = req.getParameter("box_name");
        List<JSONObject> items = itemDao.getStoreItems(seqStore, boxName, null);
        
        String resUrl = PropertiesUtil.get("SERVICE_URL") + "/item_list.jsp";
		resUrl += "?seq_store=" + seqStore;
		if (boxName != null)
			resUrl += "&box_name=" + boxName;
        resObj = new JSONObject();
        resObj.put("authkey", AuthManager.getInstance().getRandomAuthKey(buid));
        resObj.put("code", EnvSYS.RESCODE_SELECT);
        resObj.put("msg", "아이템의 종류는 " + items.size() + "가지 입니다 자세한 내용은 링크를 확인해주세요");
        resObj.put("label", "아이템목록");
        resObj.put("url", resUrl);

        return resObj;
    }
    
    /**
     * 아이템 목록을 반환
     * @param req
     * @param seqStore
     * @return
     * @throws Exception
     */
//    private JSONObject list1(HttpServletRequest req) throws Exception {
//
//        JSONObject resObj = new JSONObject();
//        
//    	String buid = AuthManager.getInstance().getUserSession(req).getBuid();
//		String seqStore = AuthManager.getInstance().getUserSession(req).getSeqDefstore();
//        String boxName = req.getParameter("box_name");
//        String what = req.getParameter("what");
//
//        ItemDataAccessObject itemDao = new ItemDataAccessObject();
//        List<JSONObject> items = itemDao.getStoreItems(seqStore, boxName, null);
//
//        if (items != null && items.size() > 0) {
//            if (what == null) {
//                resObj.put("msg", "총 " + items.size() + "의 아이템이 있습니다");
//            } else {
//                StringBuffer msgSb = new StringBuffer(boxName + "에는 ");
//                JSONObject item = null;
//                Iterator<JSONObject> itemIter = items.iterator();
//                while (itemIter.hasNext()) {
//                    item = (JSONObject) itemIter.next();
//                    msgSb.append(item.get("name") + ",");
//                }
//                msgSb.deleteCharAt(msgSb.length() - 1);
//                msgSb.append("이(가) 있습니다 자세한 내용은 링크를 확인해주세요");
//                resObj.put("msg", msgSb.toString());
//            }
//            resObj.put("code", EnvSYS.RESCODE_SUCC);
//            resObj.put("items", items);
//        } else {
//            resObj.put("code", EnvSYS.RESCODE_FAIL);
//            if (boxName != null) {
//            	resObj.put("msg", boxName + "에는 저장된 아이템이 없습니다");
//            } else {
//            	resObj.put("msg", "저장된 아이템이 없습니다");
//            }
//        }
//        
//        String resUrl = PropertiesUtil.get("SERVICE_URL") + "/item_list.jsp";
//        resUrl += "?seq_store=" + seqStore;
//        if (boxName != null)
//        	resUrl += "&box_name=" + boxName;
//        resObj.put("authkey", AuthManager.getInstance().getRandomAuthKey(buid));
//        resObj.put("label", "아이템목록");
//        resObj.put("url", resUrl);
//        
//        return resObj;
//    }

    /**
     * 아이템 사진 추가
     * @param reqJson
     * @return
     * @throws Exception
     */
    private JSONObject addImg(HttpServletRequest req, JSONObjectExt reqJson) throws Exception {

        JSONObject resObj = new JSONObject();
        
        UserSession us = AuthManager.getInstance().getUserSession(req);
        String seqUser = us.getSeq();
        String seqItem = reqJson.getString("seq_item");
        Object stObj = us.getDefStoreInfo().get("storage_type");
        int storageType = stObj != null ? Integer.parseInt(stObj.toString()) : -1;
        
        FileManager fm = new FileManager(this.getServletContext().getRealPath("/"), PropertiesUtil.get("ITEM_IMG_REPO"), us.getGoogleAccessToken());
        // 사진이미지 저장
        List<File> imgs = reqJson.getFiles();
        fm.restoreFile(seqUser, seqItem, EnvSYS.CLASS_TYPE_ITEM, imgs);
        // 이미지 목록 반환
        JSONObject imageInfo = fm.getImageInfo(seqUser, storageType, seqItem, EnvSYS.CLASS_TYPE_ITEM);
        if (imageInfo != null) {
            resObj.put("seqItem", seqItem);
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
     * 아이템 사진 삭제
     * @param reqJson
     * @return
     * @throws Exception
     */
    private JSONObject delImg(HttpServletRequest req) throws Exception {

        JSONObject resObj = new JSONObject();
        
        UserSession us = AuthManager.getInstance().getUserSession(req);
        String seqUser = us.getSeq();
        String seqItem = req.getParameter("seq_item");
        String imgsrc = req.getParameter("imgsrc");
        String imgid = req.getParameter("imgid");
        Object stObj = us.getDefStoreInfo().get("storage_type");
        int storageType = stObj != null ? Integer.parseInt(stObj.toString()) : -1;
        
        FileManager fm = new FileManager(this.getServletContext().getRealPath("/"), PropertiesUtil.get("ITEM_IMG_REPO"), us.getGoogleAccessToken());
//        String pathImage = this.getServletContext().getRealPath("/") + PropertiesUtil.get("ITEM_IMG_REPO") + seqItem + "/"
//                + FileUtil.getFullName(imgsrc);
//        boolean result = FileUtil.deleteFile(new File(pathImage));
        JSONObjectExt resultInfo = fm.deleteImage(seqUser, seqItem, EnvSYS.CLASS_TYPE_ITEM, imgid);
        boolean result = resultInfo.getBoolean("result");
        
        // 이미지 목록 반환
        JSONObject imageInfo = fm.getImageInfo(seqUser, storageType, seqItem, EnvSYS.CLASS_TYPE_ITEM);
        if (result) {
            resObj.put("seqItem", seqItem);
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
     * 아이템 갯수를 반환
     * @param req
     * @param seqStore
     * @return
     * @throws Exception
     */
    private JSONObject quantity(HttpServletRequest req) throws Exception {

        JSONObject resObj = new JSONObject();
        resObj.put("code", EnvSYS.RESCODE_FAIL);

        String seqStore = AuthManager.getInstance().getUserSession(req).getSeqDefstore();
        String boxName = req.getParameter("box_name");
        String itemName = req.getParameter("item_name");
        String wordItem = req.getParameter("word_item");

        BoxDataAccessObject boxDao = new BoxDataAccessObject();
        ItemDataAccessObject itemDao = new ItemDataAccessObject();
        JSONObject boxObj = null;
        List<JSONObject> items = null;
        
		if (boxName != null) {
			boxObj = boxDao.getStoreBox(seqStore, boxName);
			if (boxObj != null) {
				items = itemDao.getBoxItems(boxObj.get("seq").toString(), itemName);
			} else {
				resObj.put("code", EnvSYS.RESCODE_FAIL);
				resObj.put("msg", boxName + " 보관함이 없습니다");
				return resObj;
			}
		} else {
			items = new ArrayList<JSONObject>();
			List<JSONObject> boxes = boxDao.getStoreBoxes(seqStore);
			Iterator<JSONObject> boxIter = boxes.iterator();
			while (boxIter.hasNext()) {
				boxObj = (JSONObject) boxIter.next();
				items.addAll(itemDao.getBoxItems(boxObj.get("seq").toString(), itemName));
			}
		}
        
        if (items != null && items.size() > 0) {
            JSONObject itemObj = null;
            int itemQty = 0;
            Iterator itemIter = items.iterator();
            while (itemIter.hasNext()) {
                itemObj = (JSONObject) itemIter.next();
                if (itemName.equals(itemObj.get("name").toString())) {
                    itemQty += Integer.parseInt(itemObj.get("qty").toString());
                }
            }
            resObj.put("code", EnvSYS.RESCODE_SUCC);
			resObj.put("msg", (boxName != null ? (boxName + " 보관함에는 ") : "") + itemName + "이(가) " + itemQty + "개 있습니다");
        } else {
            resObj.put("code", EnvSYS.RESCODE_FAIL);
            resObj.put("msg", (boxName != null ? (boxName + " 보관함에 ") : "") + "아이템이 없습니다");
        }

        return resObj;
    }
    
    private JSONObject truncate(HttpServletRequest req) throws NumberFormatException, Exception {

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
    
	private JSONObject batch(HttpServletRequest req) throws Exception {

		JSONObject resObj = new JSONObject();

		String itemsStr = req.getParameter("items");
		String[] itemArr = itemsStr.split(",");
		String seqBox = req.getParameter("seq_box");
		String toBox = req.getParameter("eimd_to_box");
		String inputQty = req.getParameter("eimd_input_qty");
		String inputInsd = req.getParameter("eimd_input_insd");
		String inputExpd = req.getParameter("eimd_input_expd");

		ItemDataAccessObject itemDao = new ItemDataAccessObject();
		for (int i = 0; i < itemArr.length; i++) {
			itemDao.updateItem(itemArr[i], null, inputQty, inputInsd, inputExpd, toBox, 0);
		}
		
        resObj.put("items", itemDao.getBoxItems(seqBox, null));
		resObj.put("code", EnvSYS.RESCODE_SUCC);
		resObj.put("msg", "아이템 저장에 성공하였습니다");
		return resObj;

	}
	
	//XXX 삭제 예정
//	/**
//	 * List 
//	 * @param items
//	 * @return
//	 */
//	private JSONObject itemsListToJson(List<JSONObject> items) {
//		JSONObject itemsJson = new JSONObject();
//		Iterator<JSONObject> itemsIter = items.iterator();
//		JSONObject item = null;
//		while (itemsIter.hasNext()) {
//			item = (JSONObject) itemsIter.next();
//			itemsJson.put(item.get("seq"), item);
//		}
//		return itemsJson;
//	}
}
