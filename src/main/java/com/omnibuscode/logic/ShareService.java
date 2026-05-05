package com.omnibuscode.logic;

import org.json.simple.JSONObject;

import com.omnibuscode.base.EnvSYS;
import com.omnibuscode.dao.BoxDataAccessObject;
import com.omnibuscode.dao.NanumDataAccessObject;
import com.omnibuscode.dao.ShareDataAccessObject;
import com.omnibuscode.dao.StoreDataAccessObject;

/**
 * 공유테이블 비즈니스 로직 어시스트 클래스
 * @author KIUNSEA
 *
 */
public class ShareService {

    /**
     * 공유테이블에 공유 정보를 등록
     * 
     * @param seqUser
     * @param typeObject
     * @param seqObject
     * @param authority
     * @return
     * @throws Exception
     */
    public JSONObject addShare(String seqUser, String typeObject, String seqObject, String authority) throws Exception {

        JSONObject resObj = new JSONObject();
        String code = EnvSYS.RESCODE_FAIL;
        String msg = "공유정보 등록에 실패하였습니다 ";

        StoreDataAccessObject storeDao = new StoreDataAccessObject();
        BoxDataAccessObject boxDao = new BoxDataAccessObject();
        NanumDataAccessObject nanumDao = new NanumDataAccessObject();
        
        JSONObject jsonObj = null;
        if (EnvSYS.CLASS_TYPE_BOX.equals(typeObject)) {
            jsonObj = boxDao.getBox(seqObject);
        } else if (EnvSYS.CLASS_TYPE_NANUM.equals(typeObject)) {
            jsonObj = nanumDao.getNanum(seqObject);
        } else if (EnvSYS.CLASS_TYPE_STORE.equals(typeObject)) {
            jsonObj = storeDao.getStore(seqObject);;
        }
        
        String seqOwner = null;
        if (EnvSYS.CLASS_TYPE_STORE.equals(typeObject)) {
            seqOwner = jsonObj.get("seq_owner").toString();
        } else if (jsonObj != null) {
            String seqStore = jsonObj.get("seq_store").toString();
            JSONObject storeJson = storeDao.getStore(seqStore);
            seqOwner = storeJson.get("seq_owner").toString();
        }
        
        if (!(seqOwner == null || seqUser.equals(seqOwner))) { // 자신의 객체를 스스로에게 공유하지 않게 한다.
            ShareDataAccessObject sdao = new ShareDataAccessObject();
            sdao.add(seqUser, typeObject, seqObject, authority);
        }

        code = EnvSYS.RESCODE_SUCC;
        msg = "공유정보 저장에 성공하였습니다";
        
        resObj.put("code", code);
        resObj.put("msg", msg);
        return resObj;
        
    }

}
