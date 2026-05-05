package com.omnibuscode.logic;

import java.io.File;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.omnibuscode.auth.AuthManager;
import com.omnibuscode.base.EnvSYS;
import com.omnibuscode.base.LocalDBConnection;
import com.omnibuscode.base.UserSession;
import com.omnibuscode.dao.BoxDataAccessObject;
import com.omnibuscode.dao.ItemDataAccessObject;
import com.omnibuscode.logic.file.FileManager;
import com.omnibuscode.util.JSONObjectExt;
import com.omnibuscode.util.JinieboxUtil;
import com.omnibuscode.utils.ExceptionUtil;
import com.omnibuscode.utils.NumberUtil;
import com.omnibuscode.utils.PropertiesUtil;

/**
 * 아이템 비즈니스 로직 어시스트 클래스
 * @author KIUNSEA
 *
 */
public class ItemWebService extends ItemService {
    
    private Logger log = LogManager.getLogger(ItemWebService.class);

    /**
     * 아이템 목록 링크 반환
     */
    public JSONObject list(JSONObjectExt reqJson) throws Exception {
        JSONObject resObj = new JSONObject();
        String seqStore = reqJson.getString("seq_store");
        String boxName = reqJson.getString("box_name");
        
        ItemDataAccessObject itemDao = new ItemDataAccessObject();
        List<JSONObject> items = itemDao.getStoreItems(seqStore, boxName, null);

        if (items != null && items.size() > 0) {
            
            //기간 만료 체크
            JSONObject itemJson = null;
            Iterator<JSONObject> itemIter = items.iterator();
            while (itemIter.hasNext()) {
                itemJson = itemIter.next();
                itemJson.put("check_expired", this.checkExpired(itemJson.get("expiry_date").toString()));
            }
            
            resObj.put("code", EnvSYS.RESCODE_SUCC);
            resObj.put("items", items);
        } else {
            resObj.put("code", EnvSYS.RESCODE_FAIL);
            if (boxName != null) {
                resObj.put("msg", boxName + "에는 저장된 아이템이 없습니다");
            } else {
                resObj.put("msg", "저장된 아이템이 없습니다");
            }
        }
        
        return resObj;
    }

    /**
     * 아이템을 저장하고 응답 데이터를 반환한다.
     * 
     * @param ctxRoot : image file 정보가 없으면 null 로 전달
     * @param us
     * @param reqJson
     * @return
     * @throws Exception
     */
    public JSONObject store(String ctxRoot, UserSession us,  JSONObjectExt reqJson) throws Exception {

        JSONObject resObj = new JSONObject();
        
        String seqStore = reqJson.getString("seq_store");
        String seqBox = reqJson.getString("seq_box");
        String seqItem = reqJson.getString("seq_item");
        String boxName = reqJson.getString("box_name");
        String itemName = reqJson.getString("item_name");
        String itemQty = reqJson.getString("item_qty");
        String itemInsd = reqJson.getString("item_insd");
        String itemExpd = reqJson.getString("item_expd");
        String buid = reqJson.getString("buid");
        
        BoxDataAccessObject boxDao = new BoxDataAccessObject();
        ItemDataAccessObject itemDao = new ItemDataAccessObject();
        
        Object stObj = us.getDefStoreInfo().get("storage_type");
        int storageType = stObj != null ? Integer.parseInt(stObj.toString()) : -1;
        
        JSONObject box = null;
        if (seqBox != null) {
            if (Integer.parseInt(seqBox) == -2) { // 신규등록
                int seqIntBox = boxDao.insert(boxName, EnvSYS.RESMSG_CREATEBOXSYS, null, seqStore);
                if (seqIntBox > -1) {
                    seqBox = Integer.toString(seqIntBox);
                    box = boxDao.getBox(seqBox);
                }
            } else {
                box = boxDao.getBox(seqBox);
            }
        } else {
            box = boxDao.getStoreBox(seqStore, boxName);
        }

        if (seqItem != null) {
            /**
             * 기존 아이템의 정보를 갱신
             */
            itemDao.updateItem(seqItem, itemName, itemQty, itemInsd, itemExpd, seqBox, 0);
            JSONObject itemJson = itemDao.getItem(seqItem);
            
            resObj.put("seq_box", itemJson.get("seq_box"));
            resObj.put("seq_item", seqItem = itemJson.get("seq").toString());
            resObj.put("item_name", itemJson.get("name"));
            resObj.put("item_qty", itemJson.get("qty"));
            resObj.put("item_expd", itemJson.get("expiry_date"));
            
            resObj.put("code", EnvSYS.RESCODE_SUCC);
            resObj.put("msg", "아이템 정보를 수정하였습니다");
            
        } else {
            /**
             * 기존 아이템과 비교
             */
            if (box != null) { // 지정한 박스에 저장
                int seqUserInt = Integer.parseInt(us.getSeq());
                int seqBoxInt = Integer.parseInt(box.get("seq").toString());
                int itemQtyInt = 1;
                if (NumberUtil.isNumber(itemQty)) {
                    itemQtyInt = Integer.parseInt(itemQty);
                }
                int itemExpdInt = 0;
                if (NumberUtil.isNumber(itemExpd)) {
                    itemExpdInt = Integer.parseInt(itemExpd);
                }
                
                /**
                 * box 내에서 아이템 이름으로 검색하여 item 수량을 증감시킨다 (당일 추가된 만료일이 동일한 아이템이 없는 경우 새롭게 insert 함)
                 */
                JSONObject itemJson = itemDao.getSomedayItem(seqBoxInt, itemName, JinieboxUtil.getTodayString(), itemExpd);
                int seqItemInt = -1;
                if (itemJson != null) { // 기존 아이템에 반영
                    String addedQty = (Integer.parseInt(itemJson.get("qty").toString()) + itemQtyInt) + "";
                    itemDao.updateItem(itemJson.get("seq").toString(), null, addedQty, null, itemExpd, null, 0);
                    seqItem = itemJson.get("seq").toString();
                } else { // 새로운 아이템으로 등록
                    int todayInt = Integer.parseInt(JinieboxUtil.getTodayString());
                    seqItemInt = itemDao.insertItem(seqUserInt, seqBoxInt, itemName, itemQtyInt, itemExpdInt, todayInt, 0);
                    seqItem = Integer.toString(seqItemInt);
                }
                
                resObj.put("seq_item", seqItem);
                resObj.put("code", EnvSYS.RESCODE_SUCC);
                resObj.put("msg", box.get("name") + "에 " + itemName + " "+ itemQtyInt + "개을(를) 추가하였습니다");
            } else { // 박스를 지정하지 않은 경우 에러 발생
                resObj.put("code", EnvSYS.RESCODE_NOBOX);
                resObj.put("msg", boxName + "은(는) 등록하지 않은 박스입니다\n보관함 목록은 링크를 확인하여 주세요");
                
                resObj.put("authkey", AuthManager.getInstance().getRandomAuthKey(buid));
                resObj.put("label", "박스목록");
                String resUrl = PropertiesUtil.get("SERVICE_URL") + "/list_box.jsp";
                resUrl += "?seq_store=" + seqStore;
                resObj.put("url", resUrl);
            }
        }
        
        FileManager fm = ctxRoot != null ? new FileManager(ctxRoot, PropertiesUtil.get("ITEM_IMG_REPO"), us.getGoogleAccessToken()) : null;
        
        /**
         * 이미지 저장
         */
        if (fm != null && resObj.get("code").toString().equals(EnvSYS.RESCODE_SUCC)) {
            
            List<File> imgs = reqJson.getFiles();
            fm.restoreFile(us.getSeq(), seqItem, EnvSYS.CLASS_TYPE_ITEM, imgs);
            
            // 이미지 목록 저장
            JSONObject imageInfo = fm.getImageInfo(us.getSeq(), storageType, seqItem, EnvSYS.CLASS_TYPE_ITEM);
            if (imageInfo != null) {
                resObj.put("image_info", imageInfo);
            }
        }
        
        /**
         * box 의 items 목록
         */
        List<JSONObject> items = itemDao.getBoxItems(seqBox, null);
		if (fm != null) {
			JSONObject item = null;
			JSONObject imageInfo = null;
			Iterator<JSONObject> itemIter = items.iterator();
			while (itemIter.hasNext()) {
				item = (JSONObject) itemIter.next();
				imageInfo = fm.getImageInfo(us.getSeq(), storageType, item.get("seq").toString(), EnvSYS.CLASS_TYPE_ITEM);
				item.put("image_info", imageInfo);
			}
		}
        resObj.put("items", JinieboxUtil.listToMap(items));
        
        return resObj;
    }
    
    /**
     * Box 의 Items 정보를 반환
     * @param ctxRoot
     * @param seqBox
     * @return
     * @throws Exception
     */
    public JSONObject getBoxItems(String ctxRoot, UserSession us, String seqBox) throws Exception {
        ItemDataAccessObject itemDao = new ItemDataAccessObject();
        JSONObject items = JinieboxUtil.listToMap(itemDao.getBoxItems(seqBox, null));

        FileManager fm = new FileManager(ctxRoot, PropertiesUtil.get("ITEM_IMG_REPO"), us.getGoogleAccessToken());
        JSONObject imageInfo = null;
        JSONObject itemJson = null;
        String seqItem = null;
        Iterator<Object> keyIter = items.keySet().iterator();
        while (keyIter.hasNext()) {
            seqItem = keyIter.next().toString();
            itemJson = (JSONObject) items.get(seqItem);
            JSONObject defStoreInfo = us.getDefStoreInfo();
            String storeOwner = defStoreInfo.get("seq_owner").toString();
            Object stObj = defStoreInfo.get("storage_type");
            int storageType = stObj != null ? Integer.parseInt(stObj.toString()) : -1;
            imageInfo = fm.getImageInfo(storeOwner, storageType, seqItem, EnvSYS.CLASS_TYPE_ITEM);
            if (imageInfo != null) {
                itemJson.put("image_info", imageInfo);
            }
            // 기간 만료 체크
            itemJson.put("check_expired", this.checkExpired(itemJson.get("expiry_date").toString()));
        }

        return items;
    }
    
    /**
     * 사용자 시퀀스가 같고 이름에 검색어를 포함한 아이템을 조회하여 정보를 반환한다
     * @param seqUser
     * @param itemName
     * @return
     * @throws Exception
     */
    public List<JSONObject> getUserItems(String seqUser, String itemName) throws Exception {
        
        List<JSONObject> items = null;
        LocalDBConnection conn = null;
        try {
            conn = new LocalDBConnection();
            StringBuffer querySb = new StringBuffer(
                    "SELECT b.seq b_seq, b.name b_name, i.name i_name, i.qty i_qty, i.insert_date i_insert_date, i.expiry_date i_expiry_date");
            querySb.append(" FROM box b, item i");
            querySb.append(" WHERE i.seq_box = b.seq");
            querySb.append(" AND i.seq_user = " + seqUser);
            querySb.append(" AND i.name like '%" + itemName + "%'");
            log.debug("LOCALDB-QUERY------------------------------------------------------------------------------");
            log.debug(querySb);
            ResultSet rset = conn.executeQuery(querySb.toString());

            JSONObject jsonObj = null;
            if (rset != null) {
                items = new ArrayList<JSONObject>();
                while (rset.next()) {
                    jsonObj = new JSONObject();
                    jsonObj.put("b_seq", rset.getInt("b_seq"));
                    jsonObj.put("b_name", rset.getString("b_name"));
                    jsonObj.put("i_name", rset.getString("i_name"));
                    jsonObj.put("i_qty", rset.getInt("i_qty"));
                    jsonObj.put("i_insert_date", rset.getInt("i_insert_date"));
                    jsonObj.put("i_expiry_date", rset.getInt("i_expiry_date"));
                    items.add(jsonObj);
                }
                return items;
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("* 프로그램 수행중 에러 발생");
            log.error(ExceptionUtil.getExceptionInfo(e));
            throw e;
        } finally {
            conn.close();
        }
        
    }
    
    /**
     * 만료일자 체크하여 결과 반환<br/>
     * > 1 : 마감임박 (countdown)<br/>
     * > 0 : 기한종료 (expired)<br/>
     * > -1 : 만료일자가 없거나 기한이 7일 이상인 경우
     * @param expiryDate
     * @return
     */
    private static int checkExpired(String expiryDate) {
        
        SimpleDateFormat format = new SimpleDateFormat ( "yyyyMMdd");
        String current_time = format.format (System.currentTimeMillis());
        
        int edint = Integer.parseInt(expiryDate);
        int ctint = Integer.parseInt(current_time);
        
        if (edint > 0) {
            int interval = edint - ctint;
            if (interval > 0) {
                if (interval <= 7) {
                    // 만료일이 1주일 미만이면
                    return 1;
                } else {
                    // 만료기일이 1주일 이상 남아있다면
                    return -1;
                }
            } else {
                // 만료일이 지났다면
                return 0;
            }
        } else {
            // 만료일자를 설정하지 않은 경우
            return -1;
        }
        
    }

}
