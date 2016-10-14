package ru.radom.kabinet.model.cashbox;

import ru.askor.blagosfera.domain.cashbox.CashboxOperatorSession;
import ru.radom.kabinet.model.UserEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "cashbox_operator_session")
public class CashboxOperatorSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cashbox_operator_session_id_generator")
    @SequenceGenerator(name = "cashbox_operator_session_id_generator", sequenceName = "cashbox_operator_session_id", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_id", nullable = true)
    private UserEntity operator;

    @Column(name = "workplace_id", nullable = false)
    private String workplaceId;

    @Column(name = "created_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Column(name = "end_date", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    @Column(name = "active", nullable = false)
    private boolean active;

    public CashboxOperatorSessionEntity() {
    }

    public CashboxOperatorSessionEntity(CashboxOperatorSession cashboxOperatorSession) {
        setId(cashboxOperatorSession.getId());
        setOperator(cashboxOperatorSession.getOperator());
        setWorkplaceId(cashboxOperatorSession.getWorkplaceId());
        setCreatedDate(cashboxOperatorSession.getCreatedDate());
        setEndDate(cashboxOperatorSession.getEndDate());
        setActive(cashboxOperatorSession.isActive());
    }

    public CashboxOperatorSession toDomain() {
        CashboxOperatorSession cashboxOperatorSession = new CashboxOperatorSession();
        cashboxOperatorSession.setId(getId());
        cashboxOperatorSession.setOperator(getOperator());
        cashboxOperatorSession.setWorkplaceId(getWorkplaceId());
        cashboxOperatorSession.setCreatedDate(getCreatedDate());
        cashboxOperatorSession.setEndDate(getEndDate());
        cashboxOperatorSession.setActive(isActive());
        return cashboxOperatorSession;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CashboxOperatorSessionEntity)) return false;

        CashboxOperatorSessionEntity that = (CashboxOperatorSessionEntity) o;

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

    public UserEntity getOperator() {
        return operator;
    }

    public void setOperator(UserEntity operator) {
        this.operator = operator;
    }

    public String getWorkplaceId() {
        return workplaceId;
    }

    public void setWorkplaceId(String workplaceId) {
        this.workplaceId = workplaceId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
