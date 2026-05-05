package com.omnibuscode.auth;

import com.omnibuscode.base.EnvSYS;

public class AuthWebInfo extends AuthInfo {

	public AuthWebInfo() {
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
