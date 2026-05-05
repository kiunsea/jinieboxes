package com.omnibuscode.logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONObject;

import com.omnibuscode.base.EnvSYS;
import com.omnibuscode.base.UserSession;
import com.omnibuscode.dao.BoxDataAccessObject;
import com.omnibuscode.dao.NanumDataAccessObject;
import com.omnibuscode.dao.NanumfavoriteDataAccessObject;
import com.omnibuscode.dao.NanumitemDataAccessObject;
import com.omnibuscode.dao.ShareDataAccessObject;
import com.omnibuscode.dao.StoreDataAccessObject;
import com.omnibuscode.dao.ZZimDataAccessObject;
import com.omnibuscode.logic.file.FileManager;
import com.omnibuscode.util.JSONObjectExt;
import com.omnibuscode.util.JinieboxUtil;
import com.omnibuscode.utils.PropertiesUtil;

/**
 * 나눔함 비즈니스 로직 어시스트 클래스
 * @author KIUNSEA
 *
 */
public class NanumWebService {

    /**
     * 복수개의 아이템을 나눔함에 일괄 등록
     * 
     * @param req
     * @return
     * @throws Exception
     */
    public JSONObject registNanumItems(JSONObjectExt reqJson) throws Exception {

        JSONObject resObj = new JSONObject();
        String code = EnvSYS.RESCODE_FAIL;
        String msg = "아이템 등록에 실패하였습니다 ";

        String itemsStr = reqJson.getString("items");
        String[] itemArr = itemsStr.split(",");
        String seqNanum = reqJson.getString("seq_nanum");

        if (!(JinieboxUtil.isEmpty(itemsStr) || JinieboxUtil.isEmpty(seqNanum))) {
            NanumitemDataAccessObject niDao = new NanumitemDataAccessObject();
            for (int i = 0; i < itemArr.length; i++) {
                if (!niDao.exist(seqNanum, itemArr[i])) {
                    niDao.insert(seqNanum, itemArr[i], null);
                }
            }

            code = EnvSYS.RESCODE_SUCC;
            msg = "아이템 저장에 성공하였습니다";
        }
        
        resObj.put("code", code);
        resObj.put("msg", msg);
        return resObj;
        
    }
    
    /**
     * 한개의 아이템을 나눔박스에 등록
     * 
     * @param req
     * @return
     * @throws Exception
     */
    public JSONObject registNaumItem(String seqItem, String seqNanum, String detail) throws Exception {

        JSONObject resObj = new JSONObject();
        String code = EnvSYS.RESCODE_FAIL;
        String msg = "아이템 등록에 실패하였습니다 ";

        NanumitemDataAccessObject niDao = new NanumitemDataAccessObject();
        if (!niDao.exist(seqNanum, seqItem)) {
            niDao.insert(seqNanum, seqItem, detail);
            
            code = EnvSYS.RESCODE_SUCC;
            msg = "아이템 저장에 성공하였습니다";
        }
        
        resObj.put("code", code);
        resObj.put("msg", msg);
        return resObj;
        
    }
    
    /**
     * 나눔박스의 공유 정보를 등록<br/>
     * 
     * @param seqUser
     * @param shareCode
     * @param accessCodeUser
     * @return
     * @throws Exception
     */
    public JSONObject addShareNanum(String seqUser, String shareCode, String accessCodeUser) throws Exception {

        JSONObject resObj = new JSONObject();
        String code = EnvSYS.RESCODE_FAIL;
        String msg = "아이템 조회에 실패하였습니다 ";
        
        NanumDataAccessObject ndao = new NanumDataAccessObject();
        JSONObjectExt nanum = ndao.getNanumBySharecode(shareCode);
        
        boolean passed = false;
        if (seqUser != null && nanum != null && nanum.containsKey("seq_owner")) {
            if (!seqUser.equals(nanum.getString("seq_owner"))) { //자신의 객체를 스스로에게 공유하지 못하게 한다.
                String acdSaved = nanum.getString("access_code");
                if (acdSaved != null && acdSaved.trim().length() > 0) {
                    if (accessCodeUser != null && accessCodeUser.trim().equals(acdSaved)) {
                        passed = true;
                    } else {
                        msg = "접속코드가 올바르지 않습니다";
                    }
                } else {
                    passed = true;
                }
            } else {
                msg = "자신의 나눔함은 자신에게 공유 할 수 없습니다";
            }
        }
        
        if (passed) {
            ShareDataAccessObject sdao = new ShareDataAccessObject();
            if (!sdao.hasAlready(seqUser, "N", nanum.getString("seq"))) {
                sdao.add(seqUser, nanum.getType(), nanum.getString("seq"), "R");
            }
            
            NanumfavoriteDataAccessObject nfDao = new NanumfavoriteDataAccessObject();
            if (!nfDao.hasAlready(nanum.getString("seq"), seqUser)) {
                nfDao.insert(nanum.getString("seq"), seqUser);
            }
            
            code = EnvSYS.RESCODE_SUCC;
            msg = "공유 정보가 저장되었습니다";
        }

        resObj.put("code", code);
        resObj.put("msg", msg);
        return resObj;
        
    }
    
    /**
     * 열린나눔 아이템 목록을 반환한다
     * @param pathItemRepo
     * @param seqBox
     * @return
     * @throws Exception 
     */
    public List<JSONObject> getOpenNanumItems(String contextPath, UserSession us) throws Exception {
        
        List<JSONObject> openNanumItems = new ArrayList<JSONObject>();
        Set<Object> itemSeqSets = new HashSet<Object>();
        
        BoxDataAccessObject boxDao = new BoxDataAccessObject();
        NanumDataAccessObject ndao = new NanumDataAccessObject();
        NanumitemDataAccessObject nidao = new NanumitemDataAccessObject();
        ZZimDataAccessObject zzimDao = new ZZimDataAccessObject();
        
        List<JSONObject> openNanums = ndao.getOpenNanums();
        Iterator<JSONObject> openNanumIter = openNanums.iterator();
        int seqUserInt = us != null ? Integer.parseInt(us.getSeq()) : -1;
        JSONObject openNanum = null;
        List<JSONObject> oItems = null;
        JSONObject oItemJson = null;
        while(openNanumIter.hasNext()) {
            openNanum = (JSONObject) openNanumIter.next();
            oItems = nidao.getNanumItems(openNanum.get("seq").toString());
            Iterator<JSONObject> oItemIter = oItems.iterator();
            while(oItemIter.hasNext()) {
                oItemJson = (JSONObject) oItemIter.next();
                String seqItem = oItemJson.get("seq_i").toString();
                if (!itemSeqSets.contains(seqItem)) {
                    openNanumItems.add(oItemJson);
                    itemSeqSets.add(seqItem);
                }
                
                String seqNitem = oItemJson.get("seq").toString();
                oItemJson.put("zzimcnt", zzimDao.getZZimCount(seqNitem));
                
                String seqBox = oItemJson.get("seq_box").toString();
                int seqOwner = boxDao.getSeqOwner(seqBox);
                if (seqUserInt == seqOwner) {
                    oItemJson.put("is_owner", true);
                }
            }
        }
        
        this.loadItemImages(contextPath, openNanumItems);
        
        return openNanumItems;
    }
    
    /**
     * 나눔박스내의 아이템 목록을 반환한다
     * 
     * @param thisP
     * @param seqNanum
     * @return
     * @throws Exception
     */
    public List<JSONObject> getNanumItems(String contextPath, UserSession us, String seqNanum) throws Exception {
        BoxDataAccessObject boxDao = new BoxDataAccessObject();
        NanumitemDataAccessObject niDao = new NanumitemDataAccessObject();
        ZZimDataAccessObject zzimDao = new ZZimDataAccessObject();
        
        List<JSONObject> items = niDao.getNanumItems(seqNanum);
        int seqUserInt = us != null ? Integer.parseInt(us.getSeq()) : -1;
        String seqBox = null;
        JSONObject itemJson = null;
        Iterator<JSONObject> itemIter = items.iterator();
        while (itemIter.hasNext()) {
            itemJson = (JSONObject) itemIter.next();
            seqBox = itemJson.get("seq_box").toString();
            if (Integer.parseInt(seqBox) == -1) {
                itemIter.remove();
            } else {
                int seqOwner = boxDao.getSeqOwner(seqBox);
                if (seqUserInt == seqOwner) {
                    itemJson.put("is_owner", true);
                }
            }
            
            String seqNitem = itemJson.get("seq").toString();
            itemJson.put("zzimcnt", zzimDao.getZZimCount(seqNitem));
        }
        
        this.loadItemImages(contextPath, items);
        return items;
    }
    
    /**
     * 전달받은 아이템 목록에 대해 아이템의 이미지를 적재한다.
     * 
     * @param thisP
     * @param items
     * @throws Exception 
     */
    private void loadItemImages(String contextPath, List<JSONObject> items) throws Exception {
        FileManager fm = new FileManager(contextPath, PropertiesUtil.get("ITEM_IMG_REPO"), null);
        ItemWebService itemSvc = new ItemWebService();
        JSONObject itemJson = null;
        JSONObject images = null;
        Iterator<JSONObject> itemsIter = items.iterator();
        while (itemsIter.hasNext()) {
            itemJson = (JSONObject) itemsIter.next();
            
            String seqOwner = itemJson.get("seq_user").toString();
            StoreDataAccessObject storeDao = new StoreDataAccessObject();
            int storageType = storeDao.getStorageType(seqOwner);
                    
            images = fm.getImageInfo(seqOwner, storageType, itemJson.get("seq_i").toString(), EnvSYS.CLASS_TYPE_ITEM);
            if (images != null) {
                itemJson.put("images", images);
            }
        }
    }
}
