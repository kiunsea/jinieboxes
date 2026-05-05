package com.omnibuscode.util;

import java.io.File;
import java.util.List;

import org.json.simple.JSONObject;

/**
 * JSONObject 에 기본 저장할 사용자 파라미터를(SAVED_USERFILES, object type) 위한 확장 클래스
 * 
 * @author KIUNSEA
 *
 */
public class JSONObjectExt extends JSONObject {
    
    private static final long serialVersionUID = 1L;
    public static String SAVED_USERFILES = "JINIEBOX_SAVED_USERFILES";
    private String typeObject = null;
    
    public JSONObjectExt() {;}
    
    /**
     * JSONObject 를 JSONObjectExt 로 형변환
     * @param jsonObj
     */
    public JSONObjectExt(JSONObject jsonObj) {
    	this.putAll(jsonObj);
    }
    
    /**
     * 생성하는 객체 타입에 대한 이니셜을 지정하면 객체 관리에 편하다<br/>
     * Store(S), Box(B), Nanum(N) 등등 EnvSYS.java 에 이니셜을 저장함
     * @param typeObject
     */
    public JSONObjectExt(String typeObject) {
        this.typeObject = typeObject;
    }

    /**
     * null 체크를 위한 변환 유틸 함수
     * @param param
     * @return
     */
    public int getInt(String name) {
        Object value = super.get(name);
        return value != null ? Integer.parseInt(value.toString()) : -1;
    }
    
    /**
     * null 체크를 위한 변환 유틸 함수
     * @param param
     * @return
     */
    public String getString(String name) {
        Object value = super.get(name);
        return value != null ? value.toString() : null;
    }
    
    /**
     * boolean으로 변환하여 반환 (에러는 호출하는 클래스에서 처리)
     * @param name
     * @return
     */
    public boolean getBoolean(String name) {
        Object value = super.get(name);
        return Boolean.parseBoolean(value.toString());
    }
    
    /**
     * null 체크를 위한 변환 유틸 함수
     * @param param
     * @return
     */
    public List<File> getFiles() {
        Object value = super.get(SAVED_USERFILES);
        return value != null ? (List<File>) value : null;
    }
    
    /**
     * 현재 저장된 객체에 대해 사용자가 지정한 타입을 반환 
     * @return
     */
    public String getType() {
        return this.typeObject;
    }
}
