package ru.radom.kabinet.document.web;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vgusev on 16.06.2015.
 * Класс обёртка параметров шаблона документа.
 */
public class TemplateParameters {

    private long templateId;

    private List<ParticipantParameter> participantParameters = new ArrayList<>();

    public long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(long templateId) {
        this.templateId = templateId;
    }

    public List<ParticipantParameter> getParticipantParameters() {
        return participantParameters;
    }

    public void setParticipantParameters(List<ParticipantParameter> participantParameters) {
        this.participantParameters = participantParameters;
    }
}
