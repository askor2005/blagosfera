package ru.radom.kabinet.document.web;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vgusev on 16.06.2015.
 * Класс - контеёнер параметров документа с клиента.
 */
public class DocumentParameters {

    private String content;

    private String name;

    private String shortName;

    private Long templateId;

    private List<ParticipantParameter> participantParameters = new ArrayList<>();

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public List<ParticipantParameter> getParticipantParameters() {
        return participantParameters;
    }

    public void setParticipantParameters(List<ParticipantParameter> participantParameters) {
        this.participantParameters = participantParameters;
    }
}
