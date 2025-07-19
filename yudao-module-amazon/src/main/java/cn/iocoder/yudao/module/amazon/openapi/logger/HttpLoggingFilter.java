package cn.iocoder.yudao.module.amazon.openapi.logger;

public final class HttpLoggingFilter {

    private HttpLoggingFilter() { }

    public static void setHttpLogging(boolean isEnabled) {
        System.getProperties().setProperty(HttpLoggingFilter.class.getName(), String.valueOf(isEnabled));
    }

    public static boolean isLoggingEnabled() {
        return Boolean.getBoolean(HttpLoggingFilter.class.getName());
    }
}
