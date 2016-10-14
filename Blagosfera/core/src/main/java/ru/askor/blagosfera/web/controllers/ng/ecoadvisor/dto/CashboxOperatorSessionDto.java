package ru.askor.blagosfera.web.controllers.ng.ecoadvisor.dto;

import ru.radom.kabinet.model.cashbox.CashboxOperatorSessionEntity;

import java.util.Date;

public class CashboxOperatorSessionDto {

    public long id;
    public boolean active;
    public Date createdDate;
    public Date endDate;
    public String workplace;
    public CashboxUserDto operator;

    public CashboxOperatorSessionDto() {
    }

    public CashboxOperatorSessionDto(CashboxOperatorSessionEntity session) {
        id = session.getId();
        active = session.isActive();
        createdDate = session.getCreatedDate();
        endDate = session.getEndDate();
        workplace = session.getWorkplaceId();
        operator = new CashboxUserDto(session.getOperator());
    }
}
