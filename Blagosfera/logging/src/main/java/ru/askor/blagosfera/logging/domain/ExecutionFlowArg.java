package ru.askor.blagosfera.logging.domain;

/**
 * Created by Maxim Nikitin on 15.03.2016.
 */
public class ExecutionFlowArg {

    private Long id;
    private String value;

    public ExecutionFlowArg() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
