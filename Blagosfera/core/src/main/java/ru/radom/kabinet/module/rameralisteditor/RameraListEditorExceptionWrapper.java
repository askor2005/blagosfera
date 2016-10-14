package ru.radom.kabinet.module.rameralisteditor;

/**
 * Created by vgusev on 02.06.2015.
 */
public class RameraListEditorExceptionWrapper {

    private String message;

    private String stack;

    public RameraListEditorExceptionWrapper(String message, String stack) {
        this.message = message;
        this.stack = stack;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStack() {
        return stack;
    }

    public void setStack(String stack) {
        this.stack = stack;
    }
}
