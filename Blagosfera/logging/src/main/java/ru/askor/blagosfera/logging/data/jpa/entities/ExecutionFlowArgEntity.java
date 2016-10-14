package ru.askor.blagosfera.logging.data.jpa.entities;

import org.hibernate.annotations.Type;
import ru.askor.blagosfera.logging.domain.ExecutionFlowArg;

import javax.persistence.*;

/**
 * Created by Maxim Nikitin on 15.03.2016.
 */
@Entity
@Table(name = "execution_flow_arg")
public class ExecutionFlowArgEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "execution_flow_arg_id_generator")
    @SequenceGenerator(name = "execution_flow_arg_id_generator", sequenceName = "execution_flow_arg_id", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "value", unique = false, nullable = true)
    @Type(type="text")
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "execution_flow_id", nullable = false)
    private ExecutionFlowEntity executionFlow;

    public ExecutionFlowArgEntity() {
    }

    public ExecutionFlowArgEntity(String value) {
        this.value = value;
    }

    public ExecutionFlowArg toDomain() {
        ExecutionFlowArg executionFlowArg = new ExecutionFlowArg();
        executionFlowArg.setId(getId());
        executionFlowArg.setValue(getValue());
        return executionFlowArg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExecutionFlowArgEntity)) return false;

        ExecutionFlowArgEntity that = (ExecutionFlowArgEntity) o;

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ExecutionFlowEntity getExecutionFlow() {
        return executionFlow;
    }

    public ExecutionFlowArgEntity setExecutionFlow(ExecutionFlowEntity executionFlow) {
        this.executionFlow = executionFlow;
        return this;
    }
}
