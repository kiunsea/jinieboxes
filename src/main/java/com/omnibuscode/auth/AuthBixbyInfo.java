package com.omnibuscode.auth;

import com.omnibuscode.base.EnvSYS;

public class AuthBixbyInfo extends AuthInfo {

	public AuthBixbyInfo() {
		this.clientType = EnvSYS.KEY_CLIENT_TYPE_BIXBY;
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
