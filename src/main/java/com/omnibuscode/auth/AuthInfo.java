package com.omnibuscode.auth;

import com.omnibuscode.base.EnvSYS;

/**
 * @author KIUNSEA 
 * 
 * 사용자 인증 정보를 가지고 있다 (아이디, 시스템 인증코드, 인증 수행 결과)
 */
public abstract class AuthInfo {
	protected String clientType = null; // default is WEB
	protected String buid = null; //사용자 아이디
	protected String authcode = null; //외부 시스템을 인증하기 위한 인증코드
	protected String juid = null; //인증 수행 결과
	
	protected boolean authed = false; //인증 검사 결과
	protected String validcode = null; //인증 검사 결과 코드
	protected String validmsg = null; //인증 검사 결과 메세지

	/**
	 * EnvSYS.KEY_CLIENT_TYPE_[PLATFORM] 으로 조회
	 * @return client type string
	 */
	public String getClientType() {
		return this.clientType;
	}
	
	/**
	 * 시스템 인증코드 반환<br/>
	 * 내부 시스템 사용자인 경우에는 null
	 * @return
	 */
	public String getAuthcode() {
		return this.authcode;
	}
	
	/**
	 * 사용자 인증 여부
	 * @param authed
	 */
	public void setAuthed(boolean authed) {
		this.authed = authed;
	}
	
	/**
	 * 사용자 인증 여부
	 * @return
	 */
	public boolean hasAuthed() {
		return this.authed;
	}

	public void setValidcode(String validcode) {
		this.validcode = validcode;
	}
	/**
	 * 사용자 검증 결과 코드
	 * @return
	 */
	public String getValidcode() {
		return this.validcode;
	}
	
	public void setValidmsg(String validmsg) {
		this.validmsg = validmsg;
	}
	/**
	 * 사용자 검증 결과 메세지
	 * @param validmsg
	 */
	public String getValidmsg() {
		return this.validmsg;
	}
	
	public abstract void setUserid(String userid);
	/**
	 * client system 에 따른 사용자 아이디를 반환
	 * @return
	 */
	public abstract String getUserid();

}
