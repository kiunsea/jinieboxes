package com.omnibuscode.logic;

import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONObject;

import com.omnibuscode.auth.AuthManager;
import com.omnibuscode.base.EnvSYS;
import com.omnibuscode.dao.ItemDataAccessObject;
import com.omnibuscode.util.JSONObjectExt;
import com.omnibuscode.utils.PropertiesUtil;

/**
 * 아이템 비즈니스 로직 어시스트 클래스
 * @author KIUNSEA
 *
 */
public class ItemBixbyService extends ItemService {

    /**
     * 아이템 목록 링크 반환
     */
    public JSONObject list(JSONObjectExt reqJson) throws Exception {
        JSONObject resObj = new JSONObject();
        
        String buid = reqJson.getString("buid");
        String seqStore = reqJson.getString("seq_store");
        String boxName = reqJson.getString("box_name");
        String what = reqJson.getString("what");

        ItemDataAccessObject itemDao = new ItemDataAccessObject();
        List<JSONObject> items = itemDao.getStoreItems(seqStore, boxName, null);

        if (items != null && items.size() > 0) {
            if (what == null) {
                resObj.put("msg", "총 " + items.size() + "의 아이템이 있습니다");
            } else {
                StringBuffer msgSb = new StringBuffer(boxName + "에는 ");
                JSONObject item = null;
                Iterator<JSONObject> itemIter = items.iterator();
                while (itemIter.hasNext()) {
                    item = (JSONObject) itemIter.next();
                    msgSb.append(item.get("name") + ",");
                }
                msgSb.deleteCharAt(msgSb.length() - 1);
                msgSb.append("이(가) 있습니다 자세한 내용은 링크를 확인해주세요");
                resObj.put("msg", msgSb.toString());
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
        
        String resUrl = PropertiesUtil.get("SERVICE_URL") + "/item_list.jsp";
        resUrl += "?seq_store=" + seqStore;
        if (boxName != null)
            resUrl += "&box_name=" + boxName;
        resObj.put("authkey", AuthManager.getInstance().getRandomAuthKey(buid));
        resObj.put("label", "아이템목록");
        resObj.put("url", resUrl);
        
        return resObj;
    }
    
//XXX 당장은 bixby 에서의 요청에 대해 base 를 확장할 필요가 없어서 빈 클래스로 생성해두지만 추후를 위해 코드는 남겨둔다
//    @Override
//    public JSONObject list(JSONObjectExt reqJson) throws Exception {
//
//        JSONObject resObj = new JSONObject();
//        String seqStore = reqJson.get("seq_store").toString();
//        String boxName = reqJson.get("box_name").toString();
//        
//        ItemDataAccessObject itemDao = new ItemDataAccessObject();
//        List<JSONObject> items = itemDao.getStoreItems(seqStore, boxName, null);
//
//        if (items != null && items.size() > 0) {
//            resObj.put("code", EnvSYS.RESCODE_SUCC);
//            resObj.put("items", items);
//        } else {
//            resObj.put("code", EnvSYS.RESCODE_FAIL);
//            if (boxName != null) {
//                resObj.put("msg", boxName + "에는 저장된 아이템이 없습니다");
//            } else {
//                resObj.put("msg", "저장된 아이템이 없습니다");
//            }
//        }
//        
//        return resObj;
//    }
//    
//    /**
//     * 아이템을 저장한다
//     * 
//     * @param reqJson
//     * @return
//     * @throws Exception
//     */
//    public JSONObject store(String ctxRoot, JSONObjectExt reqJson) throws Exception {
//
//        JSONObject resObj = new JSONObject();
//        
//        String seqItem = reqJson.getString("seq_item");
//        String seqBox = reqJson.getString("seq_box");
//        String boxName = reqJson.getString("box_name");
//        String itemName = reqJson.getString("item_name");
//        String itemQty = reqJson.getString("item_qty");
//        String itemInsd = reqJson.getString("item_insd");
//        String itemExpd = reqJson.getString("item_expd");
//        
//        BoxDataAccessObject boxDao = new BoxDataAccessObject();
//        ItemDataAccessObject itemDao = new ItemDataAccessObject();
//        
//        JSONObject box = null;
//        if (seqBox != null) {
//            if (Integer.parseInt(seqBox) == -2) { // 신규등록)
//                int seqIntBox = boxDao.insert(boxName, EnvSYS.RESMSG_CREATEBOXSYS, null, reqJson.getString("seq_store"));
//                if (seqIntBox > -1) {
//                    seqBox = Integer.toString(seqIntBox);
//                    box = boxDao.getBox(seqBox);
//                }
//            } else {
//                box = boxDao.getBox(seqBox);
//            }
//        } else {
//            box = boxDao.getStoreBox(reqJson.getString("seq_store"), boxName);
//        }
//
//        if (seqItem != null) { //등록된 아이템은 갱신
//            itemDao.updateItem(seqItem, itemName, itemQty, itemInsd, itemExpd, seqBox);
//            JSONObject itemJson = itemDao.getItem(seqItem);
//            
//            resObj.put("seqBox", itemJson.get("seq_box"));
//            resObj.put("seqItem", itemJson.get("seq"));
//            resObj.put("itemName", itemJson.get("name"));
//            resObj.put("itemQty", itemJson.get("qty"));
//            resObj.put("itemExpd", itemJson.get("expiry_date"));
//            
//            resObj.put("code", EnvSYS.RESCODE_SUCC);
//            resObj.put("msg", "아이템 정보를 수정하였습니다");
//            
//        } else { // 아이템을 신규 등록
//    
//            if (box != null) {
//                int seqBoxInt = Integer.parseInt(box.get("seq").toString());
//                int itemQtyInt = 1;
//                if (NumberUtil.isNumber(itemQty)) {
//                    itemQtyInt = Integer.parseInt(itemQty);
//                }
//                int itemExpdInt = 0;
//                if (NumberUtil.isNumber(itemExpd)) {
//                    itemExpdInt = Integer.parseInt(itemExpd);
//                }
//                seqItem = itemDao.updateBoxItemQty(seqBoxInt, itemName, itemQtyInt, itemExpdInt);
//                resObj.put("seqItem", seqItem);
//                
//                List<JSONObject> items = itemDao.getBoxItems(seqBox, null);
//                resObj.put("items", JinieboxUtil.listToMap(items));
//                
//                resObj.put("code", EnvSYS.RESCODE_SUCC);
//                resObj.put("msg", box.get("name") + "에 " + reqJson.get("item_name") + " "+ itemQtyInt + "개을(를) 추가하였습니다");
//            } else {
//                resObj.put("code", EnvSYS.RESCODE_NOBOX);
//                resObj.put("msg", boxName + "은(는) 등록하지 않은 박스입니다\n보관함 목록은 링크를 확인하여 주세요");
//                
//                resObj.put("authkey", AuthManager.getInstance().getRandomAuthKey(reqJson.getString("buid")));
//                resObj.put("label", "박스목록");
//                String resUrl = PropertiesUtil.get("SERVICE_URL") + "/box_main.jsp";
//                resUrl += "?seq_store=" + reqJson.get("seq_store");
//                resObj.put("url", resUrl);
//            }
//        }
//        
//        // 사진이미지 처리
//        if (resObj.get("code").toString().equals(EnvSYS.RESCODE_SUCC)) {
//            // 이미지 저장
//            List<File> imgs = reqJson.getFiles();
//            if (imgs != null) {
//                Iterator imgIter = imgs.iterator();
//                File img = null;
//                while (imgIter.hasNext()) {
//                    img = (File) imgIter.next();
//                    String pathDest = ctxRoot + PropertiesUtil.get("ITEM_IMG_REPO") + seqItem;
//                    FileUtil.makeDirs(pathDest);
//                    FileUtil.moveFile(img, new File(pathDest + "/" + img.getName()));
//                }
//            }
//            // 이미지 목록 반환
//            String pathItemRepo = ctxRoot + PropertiesUtil.get("ITEM_IMG_REPO");
//            List<String> imgNameList = new ItemBixbyService().getItemImgNameList(pathItemRepo, seqItem);
//            if (imgNameList != null) {
//                resObj.put("itemImgs", imgNameList);
//            }
//        }
//        
//        return resObj;
//    }
//    
//    /**
//     * Box 의 Items 정보를 반환
//     * @param ctxRoot
//     * @param seqBox
//     * @return
//     * @throws Exception
//     */
//    public JSONObject getBoxItems(String ctxRoot, String seqBox) throws Exception {
//        ItemDataAccessObject itemDao = new ItemDataAccessObject();
//        JSONObject items = JinieboxUtil.listToMap(itemDao.getBoxItems(seqBox, null));
//
//        String pathItemRepo = ctxRoot + PropertiesUtil.get("ITEM_IMG_REPO");
//        File fileRepo = null;
//        File[] childsF = null;
//        List<String> imgNameList = null;
//        JSONObject itemJson = null;
//        String seqItem = null;
//        Iterator<Object> keyIter = items.keySet().iterator();
//        while (keyIter.hasNext()) {
//            seqItem = keyIter.next().toString();
//            itemJson = (JSONObject) items.get(seqItem);
//            imgNameList = this.getItemImgNameList(pathItemRepo, seqItem);
//            if (imgNameList != null) {
//                itemJson.put("imgs", imgNameList);
//            }
//        }
//
//        return items;
//    }
//    
//    /**
//     * 아이템의 사진 이름 목록을 반환
//     * @param pathItemRepo
//     * @param seqItem
//     * @return
//     */
//    public List<String> getItemImgNameList(String pathItemRepo, String seqItem) {
//        File fileRepo = new File(pathItemRepo + seqItem);
//        List<String> imgNameList = null;
//        File[] childsF = fileRepo.listFiles();
//        if (childsF != null) {
//            imgNameList = new ArrayList<String>();
//            for (int i = 0; i < childsF.length; i++) {
//                imgNameList.add(PropertiesUtil.get("ITEM_IMG_REPO") + seqItem + "/"
//                        + FileUtil.getFullName(childsF[i].getAbsolutePath()));
//            }
//            return imgNameList;
//        }
//        return null;
//    }
    
}
