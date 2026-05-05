package com.omnibuscode.logic;

import org.json.simple.JSONObject;

import com.omnibuscode.dao.CommonDataAccessObject;
import com.omnibuscode.util.JSONObjectExt;

/**
 * 아이템 비즈니스 로직 어시스트 인터페이스
 * @author KIUNSEA
 *
 */
public abstract class ItemService extends CommonDataAccessObject {

    /**
     * 아이템 목록 링크 반환
     * @param reqJson
     * @return
     * @throws Exception
     */
    public abstract JSONObject list(JSONObjectExt reqJson) throws Exception;
    
}
