package com.omnibuscode.auth;

import com.omnibuscode.base.EnvSYS;

/**
 * @author KIUNSEA
 * 사용자 인증 기본정보용 클래스
 */
public class AuthUserInfo extends AuthInfo {

	public AuthUserInfo() {
		this.clientType = EnvSYS.KEY_CLIENT_TYPE_WEB;
	}

	@Override
	public String getUserid() {
		return this.juid;
	}

	@Override
	public void setUserid(String userid) {
		this.juid = userid;
	}

}
