package ru.radom.kabinet.services.communities;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.field.Field;
import ru.askor.blagosfera.domain.field.FieldType;
import ru.askor.blagosfera.domain.user.User;
import ru.askor.voting.domain.BatchVoting;
import ru.askor.voting.domain.BatchVotingMode;
import ru.askor.voting.domain.RegisteredVoter;
import ru.askor.voting.domain.Voting;
import ru.askor.voting.domain.exception.VotingSystemException;
import ru.radom.kabinet.services.communities.kuch.CreateKuchFirstMeetingSettings;
import ru.radom.kabinet.utils.exception.ExceptionUtils;
import ru.radom.kabinet.utils.VarUtils;
import ru.radom.kabinet.voting.BatchVotingConstants;
import ru.radom.kabinet.voting.CommonVotingService;
import ru.radom.kabinet.voting.CooperativeFirstPlotBatchVoting;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Сервис создания собрания для создания КУч 1го этапа
 * Created by vgusev on 24.08.2015.
 */
@Service
@Transactional
public class CooperativeFirstMeetingService {

    @Autowired
    private CommonVotingService commonVotingService;

    /**
     * Создать пачку голосований за создание КУЧ.
     * @param name Наименование КУЧ
     * @param meetingTargets Цели КУЧ
     * @param dateStartValueStr Дата начала собрания
     * @param dateEndValueStr Дата окончания собрания
     * @param votersRegistrationEndDateStr Дата окончания регистрации в собрании
     * @param votersStr Участники собрания
     * @param addressFields Поля с аддресом кодированные urlencoding
     * @param votingType тип голосования
     * @return созданное собрание
     * @throws Exception
     */
    public BatchVoting createCooperativeMeeting(String name, String meetingTargets, String dateStartValueStr,
                                                String dateEndValueStr, String votersRegistrationEndDateStr, String votersStr,
                                                String addressFields, String votingType, Community currentCommunity, User currentUser) {
        BatchVoting result = null;
        try {
            ExceptionUtils.check(currentCommunity == null, "Не определёно текущее ПО");
            ExceptionUtils.check(currentUser == null, "Не определён текущий пользователь");

            Long currentCommunityId = currentCommunity.getId();
            Long currentUserId = currentUser.getId();

            ExceptionUtils.check(!BatchVotingConstants.VOTING_TYPES.containsKey(votingType), "Не выбран тип голосования");

            String[] voters = votersStr.split(",");
            Set<RegisteredVoter> voterAllowed = new HashSet<>();
            Set<Long> votersIds = new HashSet<>();
            for (String voterId : voters) {
                Long sharerId = VarUtils.getLong(voterId, -1l);
                if (sharerId > -1) {
                    voterAllowed.add(new RegisteredVoter(sharerId));
                    votersIds.add(sharerId);
                }
            }
            Community subGroup = searchRecursiveSubGroupByName(currentCommunity, name);
            ExceptionUtils.check(subGroup != null, "Подгруппа с таким именем уже существует!");

            // Собрание группы пайщиков Потребительского Общества Развития Общественных Систем "РА-ДОМ" по созданию Кооперативного участка "Москва" - ПО РОС "РА-ДОМ"
            String batchVotingName = CreateKuchFirstMeetingSettings.getStringFromSettings(CreateKuchFirstMeetingSettings.getInstance().getMeetingName(), currentCommunity, commonVotingService.getShortCooperativePlotName(name, currentCommunity), currentUser);


            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");

            // Период
            Date startDate = dateFormatter.parse(dateStartValueStr);
            Date endDate = dateFormatter.parse(dateEndValueStr);

            // Дата регистрации
            Date votersRegistrationEndDate = dateFormatter.parse(votersRegistrationEndDateStr);

            // Делаем поправку времени с учетом таймзоны
            TimeZone timeZone = TimeZone.getDefault();

            int timeZoneOffset = timeZone.getOffset(new Date().getTime()); // милисекунды
            startDate.setTime(startDate.getTime() + timeZoneOffset);
            endDate.setTime(endDate.getTime() + timeZoneOffset);
            votersRegistrationEndDate.setTime(votersRegistrationEndDate.getTime() + timeZoneOffset);

            // Описание регистрации
            String registrationDescription =
                    CreateKuchFirstMeetingSettings.getStringFromSettings(
                            CreateKuchFirstMeetingSettings.getInstance().getMeetingRegistrationDescription(),
                            currentCommunity,
                            commonVotingService.getShortCooperativePlotName(name, currentCommunity),
                            currentUser);

            Map<String, String> additionalData = new HashMap<>();

            //"communityId" : "ИД объединения с типом Потребительское Общество",
            additionalData.put(BatchVotingConstants.COMMUNITY_ID_ATTR_NAME, String.valueOf(currentCommunityId));
            //"plotName" : "Название КУЧ",
            additionalData.put(BatchVotingConstants.COOPERATIVE_PLOT_NAME_ATTR_NAME, name);
            // Поля с адресом
            additionalData.put(BatchVotingConstants.ADDRESS_FIELDS_ATTR_NAME, addressFields);

            // Декодинг адреса КУч для отображения в подробностях
            List<Field> addressFieldList = commonVotingService.decodePlotAddress(addressFields);
            List<String> addressParts = new ArrayList<>();
            for (Field field : addressFieldList) {
                String fieldValue = field.getValue();
                if (field.getType() != FieldType.GEO_LOCATION && field.getType() != FieldType.GEO_POSITION) {
                    addressParts.add(field.getName() + ": " + fieldValue);
                }
            }
            String addressString = "";
            if (addressParts.size() > 0) {
                addressString = "Адрес Кооперативного участка:<br/>" + StringUtils.join(addressParts, "<br/>");
            }

            List<Voting> votings = Arrays.asList(
                    // Голосование за председателя собрания
                    createVotingForPresidentMeeting(voterAllowed, name, currentCommunity, currentUser),
                    // Голосование за секретаря собрания
                    createVotingForSecretaryMeeting(voterAllowed, name, currentCommunity, currentUser),
                    // Голосование за повестку дня
                    createVotingForMeetingAgenta(name, currentCommunity, currentUser),
                    // Голосование за создание КУЧ
                    createVotingForCreateCooperative(name, currentCommunity, currentUser)
            );

            result = commonVotingService.createBatchVoting(
                    currentUserId, batchVotingName, meetingTargets, addressString,
                    registrationDescription,
                    CooperativeFirstPlotBatchVoting.NAME, 51,
                    votersIds,
                    startDate, endDate, votersRegistrationEndDate,
                    true, BatchVotingMode.SEQUENTIAL,
                    3, votingType.equals(BatchVotingConstants.CLOSED_VOTING_TYPE_KEY), false,
                    true, "Цели и задачи создаваемого КУч", additionalData, votings
            );
        } catch (Exception e) {
            ExceptionUtils.check(true, e.getMessage());
        }
        return result;
    }

    // Создание голосования на выбор председателя собрания
    private Voting createVotingForPresidentMeeting(Set<RegisteredVoter> voterAllowed, String cooperativeName, Community parentCommunity, User currentUser) throws VotingSystemException {
        String shortKuchName = commonVotingService.getShortCooperativePlotName(cooperativeName, parentCommunity);
        String subject = CreateKuchFirstMeetingSettings.getInstance().getVotingSubjects().get(CooperativeFirstPlotBatchVoting.VOTING_FOR_PRESIDENT_OF_MEETING_INDEX);
        String description = CreateKuchFirstMeetingSettings.getInstance().getVotingDescriptions().get(CooperativeFirstPlotBatchVoting.VOTING_FOR_PRESIDENT_OF_MEETING_INDEX);

        subject = CreateKuchFirstMeetingSettings.getStringFromSettings(subject, parentCommunity, shortKuchName, currentUser);
        description = CreateKuchFirstMeetingSettings.getStringFromSettings(description, parentCommunity, shortKuchName, currentUser);
        // TODO Добавить постановления
        String successDecree = "";
        String failDecree = "";
        String sentence = "";
        return commonVotingService.createVotingCandidate(
                commonVotingService.getCandidatesFromRegisteredVoters(voterAllowed), subject, description, true, true,
                CooperativeFirstPlotBatchVoting.VOTING_FOR_PRESIDENT_OF_MEETING_INDEX, false, true, true, 1, 1, 1L, 1L, false,new HashMap<>(),
                successDecree, failDecree, sentence, false, 51
        );
    }

    // Создание голосования на выбор секретаря собрания
    private Voting createVotingForSecretaryMeeting(Set<RegisteredVoter> voterAllowed, String cooperativeName, Community parentCommunity, User currentUser) throws VotingSystemException {
        String shortKuchName = commonVotingService.getShortCooperativePlotName(cooperativeName, parentCommunity);
        String subject = CreateKuchFirstMeetingSettings.getInstance().getVotingSubjects().get(CooperativeFirstPlotBatchVoting.VOTING_FOR_SECRETARY_OF_MEETING_INDEX);
        String description = CreateKuchFirstMeetingSettings.getInstance().getVotingDescriptions().get(CooperativeFirstPlotBatchVoting.VOTING_FOR_SECRETARY_OF_MEETING_INDEX);

        subject = CreateKuchFirstMeetingSettings.getStringFromSettings(subject, parentCommunity, shortKuchName, currentUser);
        description = CreateKuchFirstMeetingSettings.getStringFromSettings(description, parentCommunity, shortKuchName, currentUser);
        // TODO Добавить постановления
        String successDecree = "";
        String failDecree = "";
        String sentence = "";
        return commonVotingService.createVotingCandidate(
                commonVotingService.getCandidatesFromRegisteredVoters(voterAllowed), subject, description, true, true,
                CooperativeFirstPlotBatchVoting.VOTING_FOR_SECRETARY_OF_MEETING_INDEX, false, true, true, 1, 1, 1L, 1L, false,new HashMap<>(),
                successDecree, failDecree, sentence, false, 51);
    }

    // Создание голосования за повестку дня
    private Voting createVotingForMeetingAgenta(String cooperativeName, Community parentCommunity, User currentUser) throws VotingSystemException {
        String shortKuchName = commonVotingService.getShortCooperativePlotName(cooperativeName, parentCommunity);
        String subject = CreateKuchFirstMeetingSettings.getInstance().getVotingSubjects().get(CooperativeFirstPlotBatchVoting.VOTING_FOR_AGENDA_OF_MEETING_INDEX);
        String description = CreateKuchFirstMeetingSettings.getInstance().getVotingDescriptions().get(CooperativeFirstPlotBatchVoting.VOTING_FOR_AGENDA_OF_MEETING_INDEX);

        subject = CreateKuchFirstMeetingSettings.getStringFromSettings(subject, parentCommunity, shortKuchName, currentUser);
        description = CreateKuchFirstMeetingSettings.getStringFromSettings(description, parentCommunity, shortKuchName, currentUser);
        // TODO Добавить постановления
        String successDecree = "";
        String failDecree = "";
        String sentence = "";
        return commonVotingService.createVotingProContraAbstain(
                subject, description, true, true, CooperativeFirstPlotBatchVoting.VOTING_FOR_AGENDA_OF_MEETING_INDEX,
                true, true, false, new HashMap<>(), successDecree, failDecree, sentence, true, 51);
    }

    // Создание голосования за создание КУЧ
    private Voting createVotingForCreateCooperative(String cooperativeName, Community parentCommunity, User currentUser) throws VotingSystemException {
        String shortKuchName = commonVotingService.getShortCooperativePlotName(cooperativeName, parentCommunity);
        String subject = CreateKuchFirstMeetingSettings.getInstance().getVotingSubjects().get(CooperativeFirstPlotBatchVoting.VOTING_FOR_CREATING_SOCIAL_COMMUNITY_INDEX);
        String description = CreateKuchFirstMeetingSettings.getInstance().getVotingDescriptions().get(CooperativeFirstPlotBatchVoting.VOTING_FOR_CREATING_SOCIAL_COMMUNITY_INDEX);

        subject = CreateKuchFirstMeetingSettings.getStringFromSettings(subject, parentCommunity, shortKuchName, currentUser);
        description = CreateKuchFirstMeetingSettings.getStringFromSettings(description, parentCommunity, shortKuchName, currentUser);
        // TODO Добавить постановления
        String successDecree = "";
        String failDecree = "";
        String sentence = "";
        return commonVotingService.createVotingProContraAbstain(
                subject, description, true, true, CooperativeFirstPlotBatchVoting.VOTING_FOR_CREATING_SOCIAL_COMMUNITY_INDEX,
                false, true, false, new HashMap<>(), successDecree, failDecree, sentence, true, 51);
    }

    private Community searchRecursiveSubGroupByName(Community parentCommunity, String name){
        Community result = null;
        for (Community child : parentCommunity.getChildren()) {
            if (child.getName().equals(name)) {
                result = child;
                break;
            } else {
                result = searchRecursiveSubGroupByName(child, name);
                if (result != null) {
                    break;
                }
            }
        }
        return result;
    }
}
