package ru.radom.kabinet.services.communities.sharermember.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.askor.blagosfera.domain.document.Document;
import ru.askor.blagosfera.domain.events.community.CommunityMemberEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by vgusev on 29.10.2015.
 */
public class CommunityMemberResponseDto {

    private Document document;

    private CommunityMember member;

    private Community community;

    @JsonIgnore
    private List<CommunityMemberEvent> events;

    private Map<String, Object> parameters = new HashMap<>();

    public CommunityMemberResponseDto(CommunityMember member) {
        this.member = member;
        this.community = member.getCommunity();
    }

    public CommunityMemberResponseDto(CommunityMember member, CommunityMemberEvent event) {
        this.member = member;
        this.community = member.getCommunity();
        this.events = new ArrayList<>();
        this.events.add(event);
    }

    public CommunityMemberResponseDto(CommunityMember member, Map<String, Object> parameters) {
        this.member = member;
        this.community = member.getCommunity();
        this.parameters.putAll(parameters);
    }

    public CommunityMemberResponseDto(Document document, CommunityMember member, Map<String, Object> parameters, CommunityMemberEvent event) {
        this.document = document;
        this.member = member;
        this.community = member.getCommunity();
        this.parameters.putAll(parameters);
        this.events = new ArrayList<>();
        this.events.add(event);
    }

    public CommunityMemberResponseDto(Document document, Map<String, Object> parameters) {
        this.document = document;
        this.parameters.putAll(parameters);
    }

    public CommunityMemberResponseDto(Map<String, Object> parameters, List<CommunityMemberEvent> events) {
        this.parameters.putAll(parameters);
        this.events = events;
    }

    public CommunityMemberResponseDto(List<CommunityMemberEvent> events) {
        this.events = events;
    }

    public Document getDocument() {
        return document;
    }

    public CommunityMember getMember() {
        return member;
    }

    public Community getCommunity() {
        return community;
    }

    public List<CommunityMemberEvent> getEvents() {
        return events;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }
}
