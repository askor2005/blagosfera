package ru.askor.blagosfera.logging.data.jpa.entities;

import org.hibernate.annotations.Type;
import ru.askor.blagosfera.logging.domain.ExecutionFlowResult;

import javax.persistence.*;

/**
 * Created by Maxim Nikitin on 15.03.2016.
 */
@Entity
@Table(name = "execution_flow_result")
public class ExecutionFlowResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "execution_flow_result_id_generator")
    @SequenceGenerator(name = "execution_flow_result_id_generator", sequenceName = "execution_flow_result_id", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "value", unique = false, nullable = true)
    @Type(type="text")
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "execution_flow_id", nullable = false)
    private ExecutionFlowEntity executionFlow;

    public ExecutionFlowResultEntity() {
    }

    public ExecutionFlowResultEntity(String value) {
        this.value = value;
    }

    public ExecutionFlowResult toDomain() {
        ExecutionFlowResult executionFlowResult = new ExecutionFlowResult();
        executionFlowResult.setId(getId());
        executionFlowResult.setValue(getValue());
        return executionFlowResult;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExecutionFlowResultEntity)) return false;

        ExecutionFlowResultEntity that = (ExecutionFlowResultEntity) o;

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

    public ExecutionFlowResultEntity setExecutionFlow(ExecutionFlowEntity executionFlow) {
        this.executionFlow = executionFlow;
        return this;
    }
}
