package com.omnibuscode.base;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 다국어 메시지 조회 유틸 (한국어 기본 + 영어/일본어 점진 도입).
 *
 * <p>Resource bundle: {@code i18n/messages_<lang>.properties} (classpath).
 * 키 누락 시 default bundle({@code messages.properties}, 한국어)로 자동 fallback.</p>
 *
 * <h3>사용 예</h3>
 * <pre>{@code
 *   String msg = I18n.t("integration.disabled", I18n.localeOf(request), "Google");
 *   // ko: "Google 연동이 설정되지 않았습니다"
 *   // en: "Google integration is not configured"
 *   // ja: "Google 連携が設定されていません"
 * }</pre>
 *
 * <h3>JSP 사용 예 (선호)</h3>
 * <pre>{@code
 *   <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
 *   <fmt:setBundle basename="i18n.messages"/>
 *   <fmt:message key="auth.failed"/>
 * }</pre>
 *
 * <p>요청별 locale 결정은 {@link com.omnibuscode.base.LocaleFilter} 참고.</p>
 */
public final class I18n {

    public static final String BUNDLE_BASE = "i18n.messages";

    private I18n() {}

    /** 키에 해당하는 메시지 반환. Locale 미지정 시 시스템 기본. */
    public static String t(String key, Locale locale, Object... args) {
        Locale l = (locale != null) ? locale : Locale.getDefault();
        String pattern;
        try {
            pattern = ResourceBundle.getBundle(BUNDLE_BASE, l).getString(key);
        } catch (MissingResourceException e) {
            // fallback to root bundle (Korean default)
            try {
                pattern = ResourceBundle.getBundle(BUNDLE_BASE, Locale.ROOT).getString(key);
            } catch (MissingResourceException e2) {
                return "??" + key + "??";
            }
        }
        return (args == null || args.length == 0) ? pattern : MessageFormat.format(pattern, args);
    }

    /** Locale 인자 없이 호출 — JVM 기본 locale 사용. */
    public static String t(String key, Object... args) {
        return t(key, Locale.getDefault(), args);
    }

    /**
     * 요청에 적용할 locale 결정. 우선순위:
     * <ol>
     *   <li>요청 attribute {@code i18n.locale} (LocaleFilter 가 설정)</li>
     *   <li>{@code req.getLocale()} (Accept-Language)</li>
     *   <li>{@link Locale#KOREAN} (기본값)</li>
     * </ol>
     */
    public static Locale localeOf(HttpServletRequest req) {
        if (req == null) return Locale.KOREAN;
        Object attr = req.getAttribute("i18n.locale");
        if (attr instanceof Locale) return (Locale) attr;
        Locale l = req.getLocale();
        return (l != null) ? l : Locale.KOREAN;
    }
}
