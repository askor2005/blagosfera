package ru.radom.kabinet.utils.exception;

import ru.radom.kabinet.utils.thread.ThreadParameters;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class ExceptionUtils {

    public static final String EXCEPTION_MESSAGE = "EXCEPTION_MESSAGE";

    private ExceptionUtils() {
    }

    public static String getStackTrace(Throwable e) {
        Writer result = new StringWriter();
        PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        return result.toString();
    }

    /**
     *
     * @param condition
     * @param message
     */
    public static void check(boolean condition, String message) {
        if (condition) {
            ThreadParameters.setParameter(EXCEPTION_MESSAGE, message);
            throw new RuntimeException(message);
        }
    }

    public static void check(boolean condition, String message, boolean checkedException) throws Throwable {
        Throwable exception;
        if (checkedException) {
            exception = new Exception(message);
        } else {
            exception = new RuntimeException(message);
        }
        ThreadParameters.setParameter(EXCEPTION_MESSAGE, message);
        if (condition) throw exception;
    }

    public static void setErrorMessage(boolean condition, String message){
        ThreadParameters.setParameter(EXCEPTION_MESSAGE, message);
    }
}
