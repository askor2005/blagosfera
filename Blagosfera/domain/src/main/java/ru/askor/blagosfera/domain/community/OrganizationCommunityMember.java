package ru.askor.blagosfera.domain.community;

import lombok.Data;
import ru.askor.blagosfera.domain.document.Document;

/**
 *
 * Created by vgusev on 17.03.2016.
 */
@Data
public class OrganizationCommunityMember {

    private Long id;

    private Community community;

    // Член объединения - организация
    private Community organization;

    // Статус члена - организации
    private CommunityMemberStatus status;

    private Document document;

}
