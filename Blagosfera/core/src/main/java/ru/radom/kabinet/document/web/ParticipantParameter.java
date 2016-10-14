package ru.radom.kabinet.document.web;

import java.util.Map;

/**
 * Created by vgusev on 16.06.2015.
 * Класс обёртка параметров параметров участника документа.
 */
public class ParticipantParameter {

    /**
     * Наименование типа участника документа.
     */
    private String participantTypeName;

    /**
     * Наименование типа участника шаблона.
     */
    private String participantTypeTemplateName;

    /**
     * ИД участника в встраиваемой системе.
     */
    private long participantId;

    /**
     * ИД-ры участников в встраиваемой системе.
     */
    private Map<String, Long> participantIds;

    public String getParticipantTypeName() {
        return participantTypeName;
    }

    public void setParticipantTypeName(String participantTypeName) {
        this.participantTypeName = participantTypeName;
    }

    public String getParticipantTypeTemplateName() {
        return participantTypeTemplateName;
    }

    public void setParticipantTypeTemplateName(String participantTypeTemplateName) {
        this.participantTypeTemplateName = participantTypeTemplateName;
    }

    public long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(long participantId) {
        this.participantId = participantId;
    }

    public Map<String, Long> getParticipantIds() {
        return participantIds;
    }

    public void setParticipantIds(Map<String, Long> participantIds) {
        this.participantIds = participantIds;
    }
}
