package ru.radom.kabinet.document.generator;

import java.util.List;

/**
 *
 * Created by vgusev on 06.07.2015.
 */
public class CreateDocumentParameter {

    private ParticipantCreateDocumentParameter participantParameter;

    private List<UserFieldValue> userFieldValueList;

    public CreateDocumentParameter() {}

    public CreateDocumentParameter(ParticipantCreateDocumentParameter participantParameter, List<UserFieldValue> userFieldValueList) {
        this.participantParameter = participantParameter;
        this.userFieldValueList = userFieldValueList;
    }

    public ParticipantCreateDocumentParameter getParticipantParameter() {
        return participantParameter;
    }

    public void setParticipantParameter(ParticipantCreateDocumentParameter participantParameter) {
        this.participantParameter = participantParameter;
    }

    public List<UserFieldValue> getUserFieldValueList() {
        return userFieldValueList;
    }

    public void setUserFieldValueList(List<UserFieldValue> userFieldValueList) {
        this.userFieldValueList = userFieldValueList;
    }
}
