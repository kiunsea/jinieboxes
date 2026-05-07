package com.omnibuscode.base;

import java.io.IOException;
import java.util.Locale;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 요청별 locale 을 결정해 {@code req.setAttribute("i18n.locale", Locale)} 로 저장한다.
 * JSP 의 {@code <fmt:setLocale value="${i18n.locale}"/>} 또는
 * Java 의 {@link I18n#localeOf(HttpServletRequest)} 가 이 값을 사용한다.
 *
 * <p>우선순위:</p>
 * <ol>
 *   <li>쿼리 파라미터 {@code ?lang=ko|en|ja} (사용자 언어 변경 트리거; 쿠키에 저장)</li>
 *   <li>쿠키 {@code jiniebox_lang}</li>
 *   <li>{@code Accept-Language} 헤더 (browser 기본)</li>
 *   <li>한국어({@link Locale#KOREAN})</li>
 * </ol>
 */
@WebFilter(filterName = "LocaleFilter", urlPatterns = {"/*"})
public class LocaleFilter implements Filter {

    public static final String COOKIE_NAME = "jiniebox_lang";
    public static final String REQUEST_ATTR = "i18n.locale";
    private static final int COOKIE_MAX_AGE_DAYS = 365;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest) {
            HttpServletRequest req = (HttpServletRequest) request;
            Locale locale = resolve(req, response);
            req.setAttribute(REQUEST_ATTR, locale);
        }
        chain.doFilter(request, response);
    }

    private Locale resolve(HttpServletRequest req, ServletResponse res) {
        // 1) ?lang=...
        String langParam = req.getParameter("lang");
        if (isSupported(langParam)) {
            Locale l = Locale.forLanguageTag(langParam);
            saveCookie(res, langParam);
            return l;
        }
        // 2) 쿠키
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (COOKIE_NAME.equals(c.getName()) && isSupported(c.getValue())) {
                    return Locale.forLanguageTag(c.getValue());
                }
            }
        }
        // 3) Accept-Language
        Locale browser = req.getLocale();
        if (browser != null && isSupported(browser.getLanguage())) {
            return browser;
        }
        // 4) 기본: 한국어
        return Locale.KOREAN;
    }

    private static boolean isSupported(String lang) {
        if (lang == null) return false;
        String l = lang.toLowerCase();
        return "ko".equals(l) || "en".equals(l) || "ja".equals(l);
    }

    private static void saveCookie(ServletResponse res, String lang) {
        if (!(res instanceof jakarta.servlet.http.HttpServletResponse)) return;
        Cookie c = new Cookie(COOKIE_NAME, lang);
        c.setPath("/");
        c.setMaxAge(60 * 60 * 24 * COOKIE_MAX_AGE_DAYS);
        c.setHttpOnly(true);
        ((jakarta.servlet.http.HttpServletResponse) res).addCookie(c);
    }
}
