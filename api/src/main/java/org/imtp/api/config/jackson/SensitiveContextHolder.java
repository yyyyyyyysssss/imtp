package org.imtp.api.config.jackson;

public class SensitiveContextHolder{

    private static final ThreadLocal<Boolean> SENSITIVE_THREAD_LOCAL = ThreadLocal.withInitial(() -> true);

    public static void enable() {
        SENSITIVE_THREAD_LOCAL.set(true);
    }

    public static void disable() {
        SENSITIVE_THREAD_LOCAL.set(false);
    }

    public static boolean isSensitive() {
        return SENSITIVE_THREAD_LOCAL.get();
    }

    public static void clear(){
        SENSITIVE_THREAD_LOCAL.remove();
    }
}
