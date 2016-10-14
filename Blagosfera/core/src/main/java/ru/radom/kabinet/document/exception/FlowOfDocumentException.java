package ru.radom.kabinet.document.exception;

/**
 * Created by vgusev on 16.09.2015.
 */
public class FlowOfDocumentException extends RuntimeException {

    private FlowOfDocumentExceptionType exceptionType;

    public FlowOfDocumentException(String message, FlowOfDocumentExceptionType exceptionType) {
        super(message);
        this.exceptionType = exceptionType;
    }

    public FlowOfDocumentExceptionType getExceptionType() {
        return exceptionType;
    }
}
