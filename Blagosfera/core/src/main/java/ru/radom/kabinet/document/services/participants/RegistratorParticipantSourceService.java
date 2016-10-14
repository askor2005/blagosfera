package ru.radom.kabinet.document.services.participants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.core.services.registrator.RegistratorDataService;
import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.askor.blagosfera.domain.document.DocumentClassDataSource;
import ru.askor.blagosfera.domain.document.IDocumentParticipant;
import ru.askor.blagosfera.domain.document.ParticipantField;
import ru.askor.blagosfera.domain.field.FieldType;
import ru.askor.blagosfera.domain.registrator.RegistratorDomain;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.document.dto.DocumentParticipantSourceDto;
import ru.radom.kabinet.document.dto.PossibleSourceParticipantDto;
import ru.radom.kabinet.services.registration.RegistratorService;
import ru.radom.kabinet.services.sharer.UserDataService;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 07.04.2016.
 */
@Service
@Transactional
public class RegistratorParticipantSourceService extends UserParticipantSourceService implements DocumentParticipantSourceService {

    // Иия поля - тип регистратора
    private static final String REGISTRATOR_TYPE_FIELD_NAME = "REGISTRATOR_TYPE";

    @Autowired
    private RegistratorService registratorService;

    @Autowired
    private RegistratorDataService registratorDataService;

    @Autowired
    private UserDataService userDataService;

    @Override
    public ParticipantsTypes getType() {
        return ParticipantsTypes.REGISTRATOR;
    }

    @Override
    public DocumentParticipantSourceDto getParticipantSource(
            IDocumentParticipant documentParticipant, String participantName, List<Long> filteredFieldIds,
            List<FieldType> excludedFieldTypes, List<FieldType> includedFieldTypes, boolean needFillSystemFields, int index) {
        User user = (User)documentParticipant;
        DocumentParticipantSourceDto result = super.getParticipantSource(documentParticipant, participantName, filteredFieldIds, excludedFieldTypes, includedFieldTypes, needFillSystemFields, index);
        if (needFillSystemFields) {
            addSystemFieldsToRegistrator(user, result, filteredFieldIds);
        }
        return result;
    }

    @Override
    public DocumentParticipantSourceDto getParticipantSource(Long participantId, String participantName, List<Long> filteredFieldIds, List<FieldType> excludedFieldTypes, List<FieldType> includedFieldTypes, boolean needFillSystemFields, int index) {
        User user = registratorDataService.getByRegistratorId(participantId);
        return getParticipantSource(
                user, participantName, filteredFieldIds, excludedFieldTypes, includedFieldTypes, needFillSystemFields, index
        );
    }

    @Override
    public List<PossibleSourceParticipantDto> getPossibleSourceParticipants(DocumentClassDataSource dataSource) {
        List<PossibleSourceParticipantDto> result = new ArrayList<>();
        List<RegistratorDomain> registrators = registratorService.page(null, 0, 1000, null, null, null, null, null, false,null, false);
        List<User> users = userDataService.getUsersFromRegistratorsMinData(registrators);
        if (users != null) {
            for (User user : users) {
                PossibleSourceParticipantDto possibleSourceParticipant = new PossibleSourceParticipantDto();
                possibleSourceParticipant.setId(user.getId());
                possibleSourceParticipant.setName(user.getName());
                result.add(possibleSourceParticipant);
            }
        }
        return result;
    }

    /**
     * Добавить значения системных полей регистратору.
     *
     * @param user - пользователь системы
     * @param documentParticipantSource - участник документа
     * @param filteredFieldIds - поля участника документа
     */
    private void addSystemFieldsToRegistrator(User user, DocumentParticipantSourceDto documentParticipantSource, List<Long> filteredFieldIds/*, List<FieldsGroupEntity> fieldsGroups*/) {
        // Загружаем системные поля по группам полей
        /*List<FieldEntity> systemFields = fieldDao.getListByGroupsAndType(fieldsGroups, FieldType.SYSTEM);
        for (FieldEntity field : systemFields) {
            documentParticipant.getParticipantFields().add(new ParticipantField(field.getId(), "", field.getName(), field.getInternalName(), "", field.getType()));
        }*/

        for (ParticipantField participantField : documentParticipantSource.getParticipantFields()) {
            boolean needLoadValue = true;
            if (filteredFieldIds != null && filteredFieldIds.size() > 0) {
                needLoadValue = filteredFieldIds.contains(participantField.getId());
            }
            if (participantField.getInternalName().equalsIgnoreCase(REGISTRATOR_TYPE_FIELD_NAME)) { // тип регистратора
                if (user != null && needLoadValue) {
                    RegistratorDomain registratorDomain = registratorDataService.getRegistratorDtoById(user.getId());
                    if (registratorDomain != null) {
                        participantField.setValue(registratorDomain.getLevel().getName());
                    } else {
                        participantField.setValue("");
                    }
                } else {
                    participantField.setValue("");
                }
            }
        }
    }
}
