package com.omnibuscode.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.omnibuscode.base.IntegrationGate;
import com.omnibuscode.base.SafeProps;
import com.omnibuscode.utils.HttpURLConnectionUtil;
import com.omnibuscode.utils.JSONUtil;

public class GoogleAuthUtil {

    private Logger log = LogManager.getLogger(GoogleAuthUtil.class);

    private static final String GOOGLE_OAUTH_TOKEN_DEFAULT = "https://oauth2.googleapis.com/token";

    /** {@link IntegrationGate#isGoogleOAuthEnabled()} 의 호환용 별칭. */
    public static boolean isEnabled() {
        return IntegrationGate.isGoogleOAuthEnabled();
    }

    /**
     * 구글 인증서버로부터 전달받은 code를 이용하여 access_token 과 refresh_token 을 발급 받는다
     *
     * @param code
     * @return {access_token, refresh_token}, Google OAuth 미설정 시 {@code null}
     * @throws Exception
     */
    public JsonNode receiveTokensWithCode(String code) throws Exception {
        if (!isEnabled()) {
            log.warn("Google OAuth 미설정 - receiveTokensWithCode 건너뜀");
            return null;
        }
        if (code != null) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("code", code);
            params.put("client_id", SafeProps.getString("GOOGLE_CLIENT_ID"));
            params.put("client_secret", SafeProps.getString("GOOGLE_CLIENT_SECRET"));
            params.put("redirect_uri", SafeProps.getString("GOOGLE_REDIRECT_URI"));
            params.put("grant_type", "authorization_code");

            String resJson = HttpURLConnectionUtil.requestPostData(SafeProps.getString("GOOGLE_OAUTH_TOKEN", GOOGLE_OAUTH_TOKEN_DEFAULT), null, params);
            log.debug("res json >>> " + resJson);

            String accessToken = null;
            String refreshToken = null;
            if (resJson != null) {
                JsonNode jn = JSONUtil.parseJsonNode(resJson);

                if (jn.get("access_token") != null) {
                    accessToken = jn.get("access_token").asText();
                    refreshToken = jn.get("refresh_token").asText();

                    log.debug("access_token : " + accessToken);
                    log.debug("refresh_token : " + refreshToken);
                    
                    return jn;
                } else {
                    log.debug("google auth failed - " + resJson);
                }
            }
        }
        
        return null;
    }
    
    /**
     * refresh_token 을 이용하여 구글 인증서버로부터 access_token 을 발급 받아 저장한다.
     * 
     * @param refreshToken
     * @return {access_token, expires_in}
     * @throws Exception
     */
    public JsonNode receiveAcTokenWithReToken(String refreshToken) throws Exception {
        if (!isEnabled()) {
            log.warn("Google OAuth 미설정 - receiveAcTokenWithReToken 건너뜀");
            return null;
        }
        if (refreshToken != null) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("refresh_token", refreshToken);
            params.put("client_id", SafeProps.getString("GOOGLE_CLIENT_ID"));
            params.put("client_secret", SafeProps.getString("GOOGLE_CLIENT_SECRET"));
            params.put("grant_type", "refresh_token");

            String resStr = HttpURLConnectionUtil.requestPostData(SafeProps.getString("GOOGLE_OAUTH_TOKEN", GOOGLE_OAUTH_TOKEN_DEFAULT), null, params);
            log.debug("http res string : " + resStr);
            
            if (resStr != null) {
                JsonNode jn = JSONUtil.parseJsonNode(resStr);
                return jn;
            }
        }
        return null;
    }
}
