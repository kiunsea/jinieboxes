package com.omnibuscode.base;

import org.json.simple.JSONObject;

import com.omnibuscode.dao.StoreDataAccessObject;

/**
 * 로그인하지 않은 사용자에게 서비스를 제공하기 위한 최소한의 사용자 정보 객체<br/>
 * 현재는 nanum service 에서 사용함
 * @author KIUNSEA
 *
 */
public class OpenUserInfo extends UserSession {

    public OpenUserInfo(String seqStore) throws Exception {
        
        StoreDataAccessObject storeDao = new StoreDataAccessObject();
        JSONObject storeJson = storeDao.getStore(seqStore);
        this.setDefStoreInfo(storeJson);
        
        String seqUser = storeJson.get("seq_owner").toString();
        this.setSeq(seqUser);
    }
    
}
