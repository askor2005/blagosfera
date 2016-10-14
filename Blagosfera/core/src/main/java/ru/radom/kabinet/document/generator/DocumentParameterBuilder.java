package ru.radom.kabinet.document.generator;

import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.askor.blagosfera.domain.document.IDocumentParticipant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * Created by vgusev on 15.09.2015.
 */
public class DocumentParameterBuilder {

    private ParticipantCreateDocumentParameter participantCreateDocumentParameter;

    private List<UserFieldValue> userFieldValues;

    private DocumentParameterBuilder(ParticipantsTypes participantType, Long participantId, String participantName){
        participantCreateDocumentParameter = new ParticipantCreateDocumentParameter(participantType.getName(), participantId, participantName);
        userFieldValues = new ArrayList<>();
    }

    private DocumentParameterBuilder(ParticipantsTypes participantType, List<Long> participantIds, String participantName){
        participantCreateDocumentParameter = new ParticipantCreateDocumentParameter(participantType.getName(), participantIds, participantName);
        userFieldValues = new ArrayList<>();
    }

    private DocumentParameterBuilder(List<IDocumentParticipant> documentParticipants, ParticipantsTypes participantType, String participantName) {
        participantCreateDocumentParameter = new ParticipantCreateDocumentParameter(documentParticipants, participantType.getName(), participantName);
        userFieldValues = new ArrayList<>();
    }

    // Создать билдер
    public static DocumentParameterBuilder create(ParticipantsTypes participantType, IDocumentParticipant documentParticipant, String participantName) {
        if (documentParticipant.getId() == null) {
            return  new DocumentParameterBuilder(Collections.singletonList(documentParticipant), participantType, participantName);
        }
        return new DocumentParameterBuilder(participantType, documentParticipant.getId(), participantName);
    }

    // Создать билдер
    public static DocumentParameterBuilder create(ParticipantsTypes participantType, List<IDocumentParticipant> documentParticipants, String participantName) {
        List<Long> ids = new ArrayList<>();
        for (IDocumentParticipant documentParticipant : documentParticipants) {
            ids.add(documentParticipant.getId());
        }
        return new DocumentParameterBuilder(participantType, ids, participantName);
    }

    // Добавить параметр - пользовательское поле
    public DocumentParameterBuilder add(UserFieldValue userFieldValue) {
        userFieldValues.add(userFieldValue);
        return this;
    }

    // Добавить параметр - пользовательские поля
    public DocumentParameterBuilder addAll(List<UserFieldValue> userFieldValueList) {
        userFieldValues.addAll(userFieldValueList);
        return this;
    }

    // Получить параметр для создания документа
    public CreateDocumentParameter get() {
        return new CreateDocumentParameter(participantCreateDocumentParameter, userFieldValues);
    }
}
