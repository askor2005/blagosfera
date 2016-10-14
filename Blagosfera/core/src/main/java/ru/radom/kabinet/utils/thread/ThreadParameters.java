package ru.radom.kabinet.utils.thread;

import java.util.HashMap;
import java.util.Map;

/**
 * Параметры которые можно кешировать в рамках треда
 * Переменная создаётся в TimeFilter и там же очищается
 * !!! Использовать аккуратно
 * Created by vgusev on 30.10.2015.
 */
public class ThreadParameters {

    private static final ThreadLocal<Map<String, Object>> THREAD_LOCAL = new ThreadLocal<>();

    public static final void init(){
        THREAD_LOCAL.set(new HashMap<>());
    }

    public static final void clear() {
        THREAD_LOCAL.remove();
    }

    public static final void setParameter(String name, Object value) {
        if (THREAD_LOCAL.get() != null) {
            THREAD_LOCAL.get().put(name, value);
        }
    }

    public static final <T> T getParameter(String parameterName) {
        return (T)THREAD_LOCAL.get().get(parameterName);
    }

    public static final boolean exists(String parameterName) {
        if (THREAD_LOCAL.get() != null) {
            return THREAD_LOCAL.get().containsKey(parameterName);
        } else {
            return false;
        }
    }
}
