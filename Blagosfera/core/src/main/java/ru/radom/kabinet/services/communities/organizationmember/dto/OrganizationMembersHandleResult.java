package ru.radom.kabinet.services.communities.organizationmember.dto;

import lombok.Getter;
import ru.askor.blagosfera.domain.document.Document;

/**
 * Результат приёма и вывода организации в качестве участника в объединение
 * Created by vgusev on 06.04.2016.
 */
@Getter
public class OrganizationMembersHandleResult {

    private Document document;

    public OrganizationMembersHandleResult(Document document) {
        this.document = document;
    }

    public OrganizationMembersHandleResult() {}
}
