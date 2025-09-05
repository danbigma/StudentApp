package com.studentapp.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;

public class AppConfig {

    private static final String PROPS_RESOURCE = "/app.properties";
    private static volatile AppConfig INSTANCE;

    private final Properties props = new Properties();

    private AppConfig() {
        // Load defaults from classpath resource
        try (InputStream is = AppConfig.class.getResourceAsStream(PROPS_RESOURCE)) {
            if (is != null) {
                props.load(is);
            }
        } catch (IOException ignored) {
        }
    }

    public static AppConfig get() {
        if (INSTANCE == null) {
            synchronized (AppConfig.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AppConfig();
                }
            }
        }
        return INSTANCE;
    }

    private String envName(String key) {
        // Convert dot/ dash to underscore and upper case
        return key.replace('.', '_').replace('-', '_').toUpperCase(Locale.ROOT);
    }

    private String getRaw(String key, String def) {
        // Order: System property -> Env var -> properties -> default
        String sys = System.getProperty(key);
        if (sys != null) return sys;
        String env = System.getenv(envName(key));
        if (env != null) return env;
        String val = props.getProperty(key);
        return val != null ? val : def;
    }

    public String getString(String key, String def) {
        return Objects.toString(getRaw(key, def), def);
    }

    public int getInt(String key, int def) {
        String v = getRaw(key, String.valueOf(def));
        try { return Integer.parseInt(v.trim()); } catch (Exception e) { return def; }
    }

    public boolean getBoolean(String key, boolean def) {
        String v = getRaw(key, String.valueOf(def));
        return "1".equals(v) || Boolean.parseBoolean(v);
    }

    // Specific getters
    public String getAdminUser() { return getString("admin.user", "admin"); }
    public String getAdminPassword() { return getString("admin.password", "admin"); }

    public int getSessionTimeoutShortSeconds() { return getInt("session.timeout.short.seconds", 60); }
    public int getSessionTimeoutLongSeconds() { return getInt("session.timeout.long.seconds", 1800); }

    public boolean isCorsEnabled() { return getBoolean("cors.enabled", false); }
    public String getCorsAllowedOrigins() { return getString("cors.allowed.origins", "*"); }
    public String getCorsAllowedMethods() { return getString("cors.allowed.methods", "GET,POST,PUT,DELETE,OPTIONS"); }
    public String getCorsAllowedHeaders() { return getString("cors.allowed.headers", "Origin,Content-Type,Accept,Authorization,X-Requested-With"); }
    public boolean isCorsAllowCredentials() { return getBoolean("cors.allow.credentials", true); }
    public int getCorsMaxAgeSeconds() { return getInt("cors.max.age.seconds", 1800); }

    public boolean isSecurityHeadersEnabled() { return getBoolean("security.headers.enabled", true); }
    public String getContentSecurityPolicy() { return getString("security.csp", "default-src 'self' 'unsafe-inline'" ); }
    public String getFrameOptions() { return getString("security.frame.options", "SAMEORIGIN"); }
    public String getReferrerPolicy() { return getString("security.referrer.policy", "no-referrer"); }

    // CSRF and Login protection
    public boolean isCsrfEnabled() { return getBoolean("csrf.enabled", true); }
    public int getLoginMaxAttempts() { return getInt("login.max.attempts", 5); }
    public int getLoginLockoutSeconds() { return getInt("login.lockout.seconds", 600); }

    // Cookies
    public boolean isCookieForceSecure() { return getBoolean("cookie.force.secure", false); }
}
