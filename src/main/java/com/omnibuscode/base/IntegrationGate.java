package com.omnibuscode.base;

import java.io.IOException;
import java.io.PrintWriter;

import org.json.simple.JSONObject;

import jakarta.servlet.http.HttpServletResponse;

/**
 * 외부 연동(OAuth, FCM, OpenAI 등) 기능이 활성화되어 있는지 검사하고,
 * 비활성 상태일 때 표준 503 + JSON 응답을 작성하는 공통 헬퍼.
 *
 * <p>각 통합 기능은 필수 키들이 모두 설정된 경우에만 활성으로 간주한다.
 * 설정되지 않은 환경(특히 공개용 클론 또는 standalone)에서도 서버가 정상
 * 부팅되도록 보장하는 게 목적이다.</p>
 */
public final class IntegrationGate {

    public static final String FEATURE_DISABLED = "FEATURE_DISABLED";

    private IntegrationGate() {}

    public static boolean isGoogleOAuthEnabled() {
        return SafeProps.isSet("GOOGLE_CLIENT_ID")
                && SafeProps.isSet("GOOGLE_CLIENT_SECRET")
                && SafeProps.isSet("GOOGLE_REDIRECT_URI");
    }

    public static boolean isKakaoOAuthEnabled() {
        return SafeProps.isSet("KAKAO_REST_API_KEY")
                && SafeProps.isSet("KAKAO_CLIENT_SECRET")
                && SafeProps.isSet("KAKAO_REDIRECT_URI");
    }

    public static boolean isNaverOAuthEnabled() {
        return SafeProps.isSet("NAVER_CLIENT_ID")
                && SafeProps.isSet("NAVER_CLIENT_SECRET")
                && SafeProps.isSet("NAVER_REDIRECT_URI");
    }

    public static boolean isClovaEnabled() {
        return SafeProps.isSet("CLOVA_EXTENSION_ID");
    }

    public static boolean isOpenAIEnabled() {
        return SafeProps.isSet("OPENAI_API_KEY");
    }

    /**
     * 장보고(Jangbogo) 연동 기능 활성 여부.
     * 명시적 opt-in: {@code JANGBOGO_ENABLED=true} 설정이 있을 때만 동작한다.
     * 특허로 보호되는 로직이며, 공개 클론 기본 환경에서는 비활성 상태로 둔다.
     */
    public static boolean isJangbogoEnabled() {
        return SafeProps.getBool("JANGBOGO_ENABLED", false);
    }

    /**
     * 503 Service Unavailable + JSON 본문(코드/메시지)을 응답으로 쓴다.
     * 미설정된 외부 연동에 호출이 들어왔을 때 클라이언트가 명확히 인지할 수 있도록 한다.
     */
    @SuppressWarnings("unchecked")
    public static void writeDisabled(HttpServletResponse res, String integrationName) throws IOException {
        res.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        res.setCharacterEncoding("UTF-8");
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Cache-Control", "no-cache");
        JSONObject body = new JSONObject();
        body.put("code", FEATURE_DISABLED);
        body.put("msg", integrationName + " 연동이 설정되지 않았습니다");
        PrintWriter out = res.getWriter();
        out.println(body);
        out.flush();
        out.close();
    }
}
