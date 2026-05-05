package com.omnibuscode.base;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

// import com.omnibuscode.ai.ChatRoom; // TODO: AI 패키지 구현 필요
import com.omnibuscode.util.JinieboxUtil;

public class UserSession {

    private String seq, juid, buid, juname, insertTime = null;
    private JSONObject ownStoreInfo = null; 	//소유 저장소 정보
    private JSONObject defStoreInfo = null; 	//기본 저장소 정보
    private String googleAccessToken = null; 	//google cloud platform 서비스를 이용하기 위한 토큰
    private String instantMessage = null; 		//사용자에게 전달할 일회성 메세지
    private List<String> failedAuthMalls = new ArrayList<String>(); //로그인에 실패한 쇼핑몰 시퀀스 목록
    private JSONObject mallUsrid = null; 		//장보고 쇼핑몰 아이디 목록
    private int isPartner = 1; 					//테스트 사용자 여부
    private int firstVisit = 1;					//처음 사용자 여부
    private boolean reuseChatRoom = true;		//생성한 채팅방을 세션이 유지되는 동안 재사용 여부
    // private ChatRoom sessChatRoom = null;		//사용자 세션에서 유지하는 채팅방 // TODO: AI 패키지 구현 필요
    private Object sessChatRoom = null;		//사용자 세션에서 유지하는 채팅방 (임시: Object 타입으로 변경)

    public JSONObject getMallUsrid() {
        return mallUsrid;
    }

    public String getMallUsrid(String seqMall) {
        return (this.mallUsrid != null && this.mallUsrid.get(seqMall) != null) ? mallUsrid.get(seqMall).toString() : null;
    }

    public void addMallUsrid(String seqMall, String mallUsrid) {
        if (this.mallUsrid == null) {
            this.mallUsrid = new JSONObject();
        }
        this.mallUsrid.put(seqMall, mallUsrid);
    }
    
    public void setMallUsrid(JSONObject mallUsrid) {
        this.mallUsrid = mallUsrid;
    }

    /**
     * 인증에 실패한 쇼핑몰인지 확인
     * 
     * @param seq
     * @return
     */
    public boolean checkFailedAuthMall(String seq) {
        return this.failedAuthMalls.contains(seq);
    }
    
    /**
     * 로그인에 실패한 쇼핑몰 시퀀스 목록<br/>
     * 함수 호출이후 목록을 초기화 한다.
     * 
     * @return
     */
    public List<String> getFailedAuthMalls() {
        List<String> rtnList = new ArrayList<String>();
        rtnList.addAll(this.failedAuthMalls);
        this.failedAuthMalls.clear();
        return rtnList;
    }

    /**
     * 로그인에 실패한 쇼핑몰 시퀀스 목록에 추가 (아이디가 없거나 비번 오류등)
     * 
     * @param seqm
     */
    public boolean addFailedAuthMallSeq(String seqm) {
        return this.failedAuthMalls.add(seqm);
    }
    
    public boolean removeFailedAuthMallSeq(String seqm) {
        return this.failedAuthMalls.remove(seqm);
    }

    public boolean hasInstantMessage() {
        return this.instantMessage != null ? true : false;
    }
    
    /**
     * get & clear
     * 
     * @return instant message
     */
    public String getInstantMessage() {
        String instMsg = this.instantMessage;
        this.instantMessage = null;
        return instMsg;
    }

    public void setInstantMessage(String instantMessage) {
        this.instantMessage = instantMessage;
    }

    public String getGoogleAccessToken() throws Exception {
        return this.googleAccessToken;
    }

    public void setGoogleAccessToken(String googleAccessToken) {
        this.googleAccessToken = googleAccessToken;
    }

    /**
     * 사용자 시퀀스
     * 
     * @return user seq
     */
    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        if (JinieboxUtil.isEmpty(seq))
            this.seq = null;
        else
            this.seq = seq;
    }

    /**
     * 지니박스 사용자 아이디
     * 
     * @return user juid
     */
    public String getJuid() {
        return juid;
    }

    public void setJuid(String juid) {
        if (JinieboxUtil.isEmpty(juid))
            this.juid = null;
        else
            this.juid = juid;
    }

    /**
     * 빅스비 사용자 아이디
     * 
     * @return user buid
     */
    public String getBuid() {
        return buid;
    }

    public void setBuid(String buid) {
        if (JinieboxUtil.isEmpty(buid))
            this.buid = null;
        else
            this.buid = buid;
    }

    /**
     * 사용자 이름
     * 
     * @return user juname
     */
    public String getJuname() {
        return juname;
    }

    public void setJuname(String juname) {
        if (JinieboxUtil.isEmpty(juname))
            this.juname = null;
        else
            this.juname = juname;
    }

    /**
     * 등록시간
     * 
     * @return user inserttime
     */
    public String getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(String insertTime) {
        if (JinieboxUtil.isEmpty(insertTime))
            this.insertTime = null;
        else
            this.insertTime = insertTime;
    }
    
    public JSONObject getDefStoreInfo() {
        return defStoreInfo;
    }

    public void setDefStoreInfo(JSONObject defStoreInfo) {
        this.defStoreInfo = defStoreInfo;
    }
    
    public String getSeqDefstore() {
        return this.defStoreInfo != null ? this.defStoreInfo.get("seq").toString() : null;
    }

    public JSONObject getOwnStoreInfo() {
        return ownStoreInfo;
    }

    public void setOwnStoreInfo(JSONObject ownStoreInfo) {
        this.ownStoreInfo = ownStoreInfo;
    }
    
    public String getNameDefstore() {
        return this.defStoreInfo != null ? this.defStoreInfo.get("name").toString() : null;
    }
    
    public int getIsPartner() {
        return isPartner;
    }

    public void setIsParter(int isPartner) {
        this.isPartner = isPartner;
    }

    public boolean isPartner() {
        return (this.isPartner == 1);
    }
    
    /**
     * 저장소 시퀀스를 입력받아 본인 소유의 저장소의 시퀀스인지 여부 확인
     * @return
     */
    public boolean isOwner(String seqStore) {
        if (this.ownStoreInfo != null) {
            if (this.ownStoreInfo.get("seq").toString().equals(seqStore)) {
                return true;
            }
        }
        return false;
    }

    public int getFirstVisit() {
        return firstVisit;
    }

    public void setFirstVisit(int firstVisit) {
        this.firstVisit = firstVisit;
    }
    
    public boolean checkFirstVisit() {
        return (this.firstVisit == 1);
    }

	public boolean isReuseChatRoom() {
		return reuseChatRoom;
	}

	public void setReuseChatRoom(boolean reuseChatRoom) {
		this.reuseChatRoom = reuseChatRoom;
	}

	public Object getSessChatRoom() {
		return sessChatRoom;
	}

	public void setSessChatRoom(Object sessChatRoom) {
		this.sessChatRoom = sessChatRoom;
	}
	
}
