package com.omnibuscode.logic;

import org.json.simple.JSONObject;

import com.omnibuscode.dao.CommonDataAccessObject;

/**
 * 아이템 비즈니스 로직 어시스트 인터페이스
 * @author KIUNSEA
 *
 */
public abstract class ZZimService extends CommonDataAccessObject {

    /**
     * CHECK 쿼리로 작성하지 못하는 프로세스를 위한 로직으로 getZZimInfo() 쿼리가 있으므로 일단 보류한다.
     * @return
     */
    public JSONObject getZZimInfo() {
        return null;
    }
    
}
