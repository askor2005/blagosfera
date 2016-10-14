package ru.askor.blagosfera.domain.events.community;

import lombok.Getter;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.OrganizationCommunityMember;
import ru.askor.blagosfera.domain.events.BlagosferaEvent;

/**
 * Событие участников объединения - юр лиц
 * Created by vgusev on 23.10.2015.
 */
@Getter
public class OrganizationCommunityMemberEvent extends BlagosferaEvent {

    // Тип созбытия
    private OrganizationCommunityMemberEventType eventType;

    // Участник объединения - юр лицо
    private Community organization;

    // Объединение
    private Community community;

    // Участник объединения
    private OrganizationCommunityMember member;

    public OrganizationCommunityMemberEvent(Object source, OrganizationCommunityMemberEventType eventType, Community organization, Community community) {
        super(source);
        this.eventType = eventType;
        this.organization = organization;
        this.community = community;
    }

    public OrganizationCommunityMemberEvent(Object source, OrganizationCommunityMemberEventType eventType, Community organization, Community community, OrganizationCommunityMember member) {
        super(source);
        this.eventType = eventType;
        this.organization = organization;
        this.community = community;
        this.member = member;
    }
}
