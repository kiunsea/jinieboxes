package com.omnibuscode.auth;

import com.omnibuscode.base.EnvSYS;

public class AuthNaverInfo extends AuthInfo {

	public AuthNaverInfo() {
		this.clientType = EnvSYS.KEY_CLIENT_TYPE_NAVER;
	}

	public void setAuthcode(String authcode) {
		this.authcode = authcode;
	}
	
	@Override
	public String getUserid() {
		return this.buid;
	}
	
	@Override
	public void setUserid(String userid) {
		this.buid = userid;
	}
	
}
