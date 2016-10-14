package ru.askor.blagosfera.logging.domain;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maxim Nikitin on 15.03.2016.
 */
public class ExecutionFlow {

    private Long id;
    private LocalDateTime date;
    private String threadName;
    private String requestId;
    private String sessionId;
    private String username;
    private String targetClassName;
    private String targetMethodName;
    private Long parentId;
    private Long duration;
    private Boolean exceptionThrown = false;
    private List<ExecutionFlowArg> args = new ArrayList<>();
    private ExecutionFlowResult result;

    public ExecutionFlow() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTargetClassName() {
        return targetClassName;
    }

    public void setTargetClassName(String targetClassName) {
        this.targetClassName = targetClassName;
    }

    public String getTargetMethodName() {
        return targetMethodName;
    }

    public void setTargetMethodName(String targetMethodName) {
        this.targetMethodName = targetMethodName;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Boolean getExceptionThrown() {
        return exceptionThrown;
    }

    public void setExceptionThrown(Boolean exceptionThrown) {
        this.exceptionThrown = exceptionThrown;
    }

    public List<ExecutionFlowArg> getArgs() {
        return args;
    }

    public ExecutionFlowResult getResult() {
        return result;
    }

    public void setResult(ExecutionFlowResult result) {
        this.result = result;
    }
}
