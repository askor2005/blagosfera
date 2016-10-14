package ru.askor.blagosfera.logging.data.jpa.entities;

import org.hibernate.annotations.Type;
import java.time.LocalDateTime;
import ru.askor.blagosfera.logging.domain.ExecutionFlow;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maxim Nikitin on 15.03.2016.
 */
@Entity
@Table(name = "execution_flow")
public class ExecutionFlowEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "execution_flow_id_generator")
    @SequenceGenerator(name = "execution_flow_id_generator", sequenceName = "execution_flow_id", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name="date", unique = false, nullable = false)
    private LocalDateTime date;

    @Column(name = "thread_name", unique = false, nullable = false)
    @Type(type="text")
    private String threadName;

    @Column(name = "request_id", unique = false, nullable = true)
    @Type(type="text")
    private String requestId;

    @Column(name = "session_id", unique = false, nullable = true)
    @Type(type="text")
    private String sessionId;

    @Column(name = "username", unique = false, nullable = true)
    @Type(type="text")
    private String username;

    @Column(name = "target_class_name", unique = false, nullable = false)
    @Type(type="text")
    private String targetClassName;

    @Column(name = "target_method_name", unique = false, nullable = false)
    @Type(type="text")
    private String targetMethodName;

    @Column(name = "parent_id", unique = false, nullable = true)
    private Long parentId;

    @Column(name = "duration", unique = false, nullable = true)
    private Long duration;

    @Column(name = "exception_thrown", unique = false, nullable = false)
    private Boolean exceptionThrown = false;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "executionFlow", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExecutionFlowArgEntity> args = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "executionFlow", cascade = CascadeType.ALL, orphanRemoval = true)
    private ExecutionFlowResultEntity result;

    public ExecutionFlowEntity() {
    }

    public ExecutionFlow toDomain() {
        ExecutionFlow executionFlow = new ExecutionFlow();
        executionFlow.setId(getId());
        executionFlow.setDate(getDate());
        executionFlow.setThreadName(getThreadName());
        executionFlow.setRequestId(getRequestId());
        executionFlow.setSessionId(getSessionId());
        executionFlow.setUsername(getUsername());
        executionFlow.setTargetClassName(getTargetClassName());
        executionFlow.setTargetMethodName(getTargetMethodName());
        executionFlow.setParentId(getParentId());
        executionFlow.setDuration(getDuration());
        executionFlow.setExceptionThrown(getExceptionThrown());

        for (ExecutionFlowArgEntity arg : getArgs()) {
            executionFlow.getArgs().add(arg.toDomain());
        }

        executionFlow.setResult(getResult().toDomain());

        return executionFlow;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExecutionFlowEntity)) return false;

        ExecutionFlowEntity that = (ExecutionFlowEntity) o;

        //return !(getId() != null ? !getId().equals(that.getId()) : that.getId() != null);
        return (getId() != null) && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
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

    public List<ExecutionFlowArgEntity> getArgs() {
        return args;
    }

    public ExecutionFlowResultEntity getResult() {
        return result;
    }

    public void setResult(ExecutionFlowResultEntity result) {
        this.result = result;
    }
}
