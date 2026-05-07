package com.omnibuscode.base;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.omnibuscode.utils.PropertiesUtil;

/**
 * Null/blank-safe, type-safe wrapper around {@link PropertiesUtil}.
 *
 * <p>Treats {@code null}, empty string, and whitespace-only values as
 * "unset" and never throws {@link NullPointerException} or
 * {@link NumberFormatException} for a missing key — callers always get
 * a usable default.</p>
 *
 * <p>Each missing key is logged at most once to keep startup output
 * readable when many integrations are intentionally not configured
 * (e.g. running without Google/Kakao/Naver/FCM in standalone mode).</p>
 *
 * <p>Use {@link #isSet(String)} to gate optional integrations and
 * {@link #getRequired(String)} only for keys whose absence really should
 * abort startup.</p>
 */
public final class SafeProps {

    private static final Logger log = LogManager.getLogger(SafeProps.class);
    private static final Set<String> warnedKeys = ConcurrentHashMap.newKeySet();

    private SafeProps() {}

    /**
     * Resolves the raw value for a key with this priority (12-factor friendly):
     * <ol>
     *   <li>System property {@code jiniebox.<key.toLowerCase>} (e.g. {@code jiniebox.localdb_url})</li>
     *   <li>Environment variable with the same key name (e.g. {@code LOCALDB_URL})</li>
     *   <li>Value from {@link PropertiesUtil} (JINIEBOX.PROPERTIES)</li>
     * </ol>
     * Returns {@code null} if the value is missing/blank in all sources.
     */
    private static String resolve(String key) {
        if (key == null) return null;
        // 1. -D system property (소문자 prefix 'jiniebox.' 권장)
        String v = blankToNull(System.getProperty("jiniebox." + key.toLowerCase()));
        if (v != null) return v;
        // 2. environment variable (예: KAKAO_REST_API_KEY)
        v = blankToNull(System.getenv(key));
        if (v != null) return v;
        // 3. PROPERTIES 파일
        return blankToNull(PropertiesUtil.get(key));
    }

    /** True if the key has a non-blank value (system property / env var / PROPERTIES 중 어디든). */
    public static boolean isSet(String key) {
        return resolve(key) != null;
    }

    /** Trimmed value, or {@code null} if missing/blank. Logs missing once. */
    public static String getString(String key) {
        return getString(key, null);
    }

    public static String getString(String key, String defaultValue) {
        String v = resolve(key);
        if (v == null) {
            warnMissing(key);
            return defaultValue;
        }
        return v;
    }

    /** Throws {@link IllegalStateException} if the key is missing in all sources. */
    public static String getRequired(String key) {
        String v = resolve(key);
        if (v == null) {
            throw new IllegalStateException("Required property is missing: " + key);
        }
        return v;
    }

    public static int getInt(String key, int defaultValue) {
        String v = resolve(key);
        if (v == null) {
            warnMissing(key);
            return defaultValue;
        }
        try {
            return Integer.parseInt(v);
        } catch (NumberFormatException e) {
            log.warn("Property '" + key + "' is not a valid int: '" + v + "', using default " + defaultValue);
            return defaultValue;
        }
    }

    public static long getLong(String key, long defaultValue) {
        String v = resolve(key);
        if (v == null) {
            warnMissing(key);
            return defaultValue;
        }
        try {
            return Long.parseLong(v);
        } catch (NumberFormatException e) {
            log.warn("Property '" + key + "' is not a valid long: '" + v + "', using default " + defaultValue);
            return defaultValue;
        }
    }

    public static boolean getBool(String key, boolean defaultValue) {
        String v = resolve(key);
        if (v == null) {
            warnMissing(key);
            return defaultValue;
        }
        return "true".equalsIgnoreCase(v) || "1".equals(v) || "yes".equalsIgnoreCase(v);
    }

    private static String blankToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static void warnMissing(String key) {
        if (warnedKeys.add(key)) {
            log.warn("Property not set: " + key);
        }
    }
}
