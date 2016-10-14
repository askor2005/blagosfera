package ru.radom.kabinet.document.parser;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.data.jpa.repositories.ChatMessageRepository;
import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.askor.blagosfera.domain.community.CommunityPost;
import ru.askor.blagosfera.domain.document.ParticipantField;
import ru.askor.voting.business.services.BatchVotingService;
import ru.askor.voting.business.services.VotingService;
import ru.askor.voting.domain.*;
import ru.askor.voting.domain.exception.VotingSystemException;
import ru.radom.kabinet.document.dto.DocumentParticipantSourceDto;
import ru.radom.kabinet.model.chat.ChatMessage;
import ru.radom.kabinet.model.votingtemplate.VotingAttributeTemplate;
import ru.radom.kabinet.services.EmailTemplateContextFunctions;
import ru.radom.kabinet.services.communities.CommunityDataService;
import ru.radom.kabinet.services.communities.sharermember.CommunityMemberDomainService;
import ru.radom.kabinet.services.taxcode.TaxCodeService;
import ru.radom.kabinet.utils.DateUtils;
import ru.radom.kabinet.utils.FieldConstants;
import ru.radom.kabinet.utils.HumansStringUtils;
import ru.radom.kabinet.utils.VarUtils;
import ru.radom.kabinet.voting.BatchVotingConstants;
import ru.radom.kabinet.voting.CommonVotingService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Утилитный класс для обработки JEL в шаблонах документов
 * Created by vgusev on 10.02.2016.
 */
@Component
@Transactional
public class ParticipantsFieldParserUtils {

    private static SettingsManager settingsManager;

    private static TaxCodeService taxCodeService;

    private static VotingService votingService;

    private static CommonVotingService commonVotingService;

    private static BatchVotingService batchVotingService;

    private static ChatMessageRepository chatMessageRepository;

    private static CommunityMemberDomainService communityMemberDataService;

    private static CommunityDataService communityDataService;

    @Autowired
    private void setSettingsManager (SettingsManager settingsManager) {
        ParticipantsFieldParserUtils.settingsManager = settingsManager;
    }

    @Autowired
    private void setTaxCodeService(TaxCodeService taxCodeService) {
        ParticipantsFieldParserUtils.taxCodeService = taxCodeService;
    }

    @Autowired
    private void setVotingService(VotingService votingService) {
        ParticipantsFieldParserUtils.votingService = votingService;
    }

    @Autowired
    private void setCommonVotingService(CommonVotingService commonVotingService) {
        ParticipantsFieldParserUtils.commonVotingService = commonVotingService;
    }

    @Autowired
    private void setBatchVotingService(BatchVotingService batchVotingService) {
        ParticipantsFieldParserUtils.batchVotingService = batchVotingService;
    }

    @Autowired
    private void setChatMessageRepository(ChatMessageRepository chatMessageRepository) {
        ParticipantsFieldParserUtils.chatMessageRepository = chatMessageRepository;
    }

    @Autowired
    private void setCommunityDataService(CommunityMemberDomainService communityMemberDataService) {
        ParticipantsFieldParserUtils.communityMemberDataService = communityMemberDataService;
    }

    @Autowired
    private void setCommunityDataService(CommunityDataService communityDataService) {
        ParticipantsFieldParserUtils.communityDataService = communityDataService;
    }

    /**
     * Форматирование числа
     * @param num число
     * @param prependSymbol количество символов
     * @param countSymbols символ который заполнит до необходимой длины
     * @return
     */
    public String formatInt(int num, String prependSymbol, int countSymbols) {
        return String.format("%" + prependSymbol + countSymbols + "d", num);
    }

    public String formatLocalDateTime(LocalDateTime localDateTime, String format) {
        Date date = toDate(localDateTime);
        return date != null ? DateUtils.formatDate(date, format) : "";
    }

    private ParticipantField findParticipantField(DocumentParticipantSourceDto participant, String internalName) {
        ParticipantField field = null;
        if (participant != null && participant.getParticipantFields() != null) {
            for (ParticipantField participantField : participant.getParticipantFields()) {
                if (participantField.getInternalName().equals(internalName)) {
                    field = participantField;
                    break;
                }
            }
        }
        return field;
    }

    public boolean compareFieldValue(DocumentParticipantSourceDto participant, String fieldInternalName, String value) {
        ParticipantField field = findParticipantField(participant, fieldInternalName);
        boolean result = false;
        if (field != null && field.getValue() != null && value != null) {
            result = StringUtils.equalsIgnoreCase(field.getValue(), value);
        }
        return result;
    }

    private String getFieldValue(ParticipantField field) {
        String result = field.getValue();
        if (field.getInternalName().equals(FieldConstants.COMMUNITY_LEGAL_CITY_DESCRIPTION_SHORT) ||
                field.getInternalName().equals(FieldConstants.COMMUNITY_FACT_CITY_DESCRIPTION_SHORT) ||
                field.getInternalName().equals(FieldConstants.REGISTRATOR_OFFICE_CITY_DESCRIPTION_SHORT)) {
            if ("Город".equals(result)) {
                result = "г";
            }
        }
        return result;
    }

    public String conditionField(DocumentParticipantSourceDto participant, String conditionFieldInternalName, String fieldInternalName, String valuesSystemAttrName) {
        ParticipantField conditionField = findParticipantField(participant, conditionFieldInternalName);
        ParticipantField field = findParticipantField(participant, fieldInternalName);
        String result;
        if (conditionField != null && conditionField.getValue() != null && field != null) {
            String valuesRaw = settingsManager.getSystemSetting(valuesSystemAttrName, null);
            if (valuesRaw != null) {
                valuesRaw = valuesRaw.toLowerCase();
                String[] values = valuesRaw.split(",");
                List<String> valuesList = Arrays.asList(values);
                String conditionValue = getFieldValue(conditionField).toLowerCase();
                if (valuesList.contains(conditionValue)) {
                    result = getFieldValue(field);
                } else {
                    result = "";
                }
            } else {
                result = "";
            }
        } else {
            result = "";
        }
        return result;
    }

    public String[] splitField(DocumentParticipantSourceDto participant, String fieldInternalName, String split) {
        ParticipantField field = findParticipantField(participant, fieldInternalName);
        String[] result;
        if (field != null && field.getValue() != null) {
            split = StringEscapeUtils.unescapeHtml4(split);
            result = field.getValue().split(split);
        } else {
            result = null;
        }
        return result;
    }

    public String splitAndJoinField(DocumentParticipantSourceDto participant, String fieldInternalName, String split, String join) {
        String[] fieldValueParts = splitField(participant, fieldInternalName, split);
        String result;
        if (fieldValueParts != null) {
            join = StringEscapeUtils.unescapeHtml4(join);
            result = StringUtils.join(fieldValueParts, join);
        } else {
            result = "";
        }
        return result;
    }

    public String splitAndGetIndex(DocumentParticipantSourceDto participant, String fieldInternalName, String split, int index) {
        String[] fieldValueParts = splitField(participant, fieldInternalName, split);
        String result;
        if (fieldValueParts != null && fieldValueParts.length > index) {
            result = fieldValueParts[index];
        } else {
            result = "";
        }
        return result;
    }

    public String splitAndGetRange(DocumentParticipantSourceDto participant, String fieldInternalName, String split, String join, int skip) {
        String[] fieldValueParts = splitField(participant, fieldInternalName, split);
        String result;
        if (fieldValueParts != null) {
            join = StringEscapeUtils.unescapeHtml4(join);
            fieldValueParts = Arrays.copyOfRange(fieldValueParts, skip, fieldValueParts.length);
            result = StringUtils.join(fieldValueParts, join);
        } else {
            result = null;
        }
        return result;
    }

    /**
     * Получить код налогового органа по адресу участника
     * @param participant
     * @return
     */
    public String getTaxCode(DocumentParticipantSourceDto participant) {
        String result = "";
        try {
            if (ParticipantsTypes.INDIVIDUAL.equals(participant.getType()) ||
                    ParticipantsTypes.INDIVIDUAL_LIST.equals(participant.getType())) {
                result = taxCodeService.getCodeBySharerId(participant.getId());
            } else if (ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.equals(participant.getType()) ||
                    ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION_LIST.equals(participant.getType())) {
                result = taxCodeService.getCodeByCommunityId(participant.getId());
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Текущий год
     * @return
     */
    public String getCurrentYear() {
        Date date = new Date();
        return DateUtils.formatDate(date, "yyyy");
    }

    /**
     * Текущая дата
     * @return
     */
    public String getCurrentDate() {
        Date date = new Date();
        return DateUtils.formatDate(date, DateUtils.Format.DATE);
    }

    /**
     * Получить протокол голосования
     * @param votingId
     * @return
     */
    public String getVotingProtocol(Long votingId) {
        String result;
        try {
            Voting voting = votingService.getVoting(votingId, true, true);
            result = commonVotingService.getVotingProtocolString(voting);
        } catch (Exception e) {
            System.err.println("Произошла ошибка при полученнии протокола голосования с ИД " + votingId + ". Текст ошибки: " + e.getMessage());
            result = "";
        }
        return result;
    }

    public String votingsForEachFormatById(Long batchVotingId, boolean withAddtitionalVotings, String valueFormat, String interviewValueFormat, int startIndex, int count) {
        String result;
        try {
            result = votingsForEachFormat(batchVotingService.getBatchVoting(batchVotingId, true, true), withAddtitionalVotings, valueFormat, interviewValueFormat, startIndex, count);
        } catch (Exception e) {
            System.err.println("Произошла ошибка при полученнии повестки дня собрания с ИД " + batchVotingId + ". Текст ошибки: " + e.getMessage());
            result = "";
        }
        return result;
    }

    public String votingsForEachFormat(BatchVoting batchVoting, boolean withAddtitionalVotings, String valueFormat, String interviewValueFormat, int startIndex, int count) {
        return votingsForEachFormat(batchVoting, withAddtitionalVotings, valueFormat, interviewValueFormat, startIndex, count, 0);
    }

    public String votingsForEachFormat(BatchVoting batchVoting, boolean withAddtitionalVotings, String valueFormat, String interviewValueFormat, int startIndex, int count, int votesCount) {
        boolean needCreateFailProtocol = votesCount > 0;
        String result;
        try {
            if (batchVoting.getVotings().get(0).getVotingItems() == null || batchVoting.getVotings().get(0).getVotingItems().isEmpty()) {
                batchVoting = batchVotingService.getBatchVoting(batchVoting.getId(), true, true);
            }
            StringBuilder stringBuilder = new StringBuilder();
            List<Voting> votings = batchVoting.getVotings();
            int index = 1;
            count = count == -1 ? votings.size() : count + startIndex;
            for (int i = startIndex; i < count; i++) {
                String rowResult = StringEscapeUtils.unescapeHtml4(votingFormat(batchVoting, i, valueFormat, interviewValueFormat, null, null, index, needCreateFailProtocol));
                index++;
                stringBuilder.append(rowResult);
            }
            result = stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Произошла ошибка при полученнии повестки дня собрания с ИД " + batchVoting.getId() + ". Текст ошибки: " + e.getMessage());
            result = "";
        }
        return result;
    }

    public String votingFormat(BatchVoting batchVoting, int votingIndex, String valueFormat, String interviewValueFormat, String successDecree, String failDecree, int index) {
        return votingFormat(batchVoting, votingIndex, valueFormat, interviewValueFormat, successDecree, failDecree, index, false);
    }

    private String votingFormat(BatchVoting batchVoting, int votingIndex, String valueFormat, String interviewValueFormat, String successDecree, String failDecree, int index, boolean needCreateFailProtocol) {
        String rowResult;
        Voting voting = batchVoting.getVotings().get(votingIndex);
        if (voting.getResult().getVotesCount() == 0 && !needCreateFailProtocol) {
            rowResult = "";
        } else {
            if (VotingType.INTERVIEW.equals(voting.getParameters().getVotingType())) {
                rowResult = interviewValueFormat;
            } else {
                rowResult = valueFormat;
            }

            rowResult = rowResult.replaceAll("index", "" + index);
            rowResult = rowResult.replaceAll("value", voting.getSubject());
            if (voting.getAdditionalData().get(BatchVotingConstants.VOTING_DESCRIPTION) != null) {
                rowResult = rowResult.replaceAll("description", voting.getAdditionalData().get(BatchVotingConstants.VOTING_DESCRIPTION));
            }
            if (rowResult.contains("protocol")) {
                rowResult = rowResult.replaceAll("protocol", commonVotingService.getVotingProtocolString(batchVoting, voting, successDecree, failDecree));
            }
            String votingDecree = "";
            if (VotingResultType.VALID.equals(voting.getResult().getResultType()) && voting.getAdditionalData().containsKey(BatchVotingConstants.VOTING_SUCCESS_DECREE_ATTR_NAME)) {
                votingDecree = voting.getAdditionalData().get(BatchVotingConstants.VOTING_SUCCESS_DECREE_ATTR_NAME);
            }
            if (!VotingResultType.VALID.equals(voting.getResult().getResultType()) && voting.getAdditionalData().containsKey(BatchVotingConstants.VOTING_FAIL_DECREE_ATTR_NAME)) {
                votingDecree = voting.getAdditionalData().get(BatchVotingConstants.VOTING_FAIL_DECREE_ATTR_NAME);
            }
            rowResult = rowResult.replaceAll("votingDecree", votingDecree);
            String sentence = voting.getAdditionalData().get(BatchVotingConstants.VOTING_SENTENCE_ATTR_NAME);
            sentence = sentence == null ? "" : sentence;
            rowResult = rowResult.replaceAll("votingSentence", sentence);
            rowResult = StringEscapeUtils.unescapeHtml4(rowResult);
        }
        return rowResult;
    }

    public String dialogMessagesForEechFromBatchVoting(BatchVoting batchVoting, String valueFormat, String dateFormat, String oneRow, String twoRow) {
        String result = "";
        if ((batchVoting.getAdditionalData().containsKey(VotingAttributeTemplate.ADD_CHAT_TO_PROTOCOL)) &&
                ((batchVoting.getAdditionalData().get(BatchVotingConstants.BATCH_VOTING_DIALOG_ID_ATTR_NAME)) != null)){
            result = dialogMessagesForEachFormat(
                    VarUtils.getLong(batchVoting.getAdditionalData().get(BatchVotingConstants.BATCH_VOTING_DIALOG_ID_ATTR_NAME), null),
                    toDate(batchVoting.getParameters().getStartDate()),
                    createDate(),
                    valueFormat,
                    dateFormat,
                    oneRow,
                    twoRow
            );
        }
        return result;
    }

    public String getDialogHeader(BatchVoting batchVoting, String dialogHeader) {
        String result = "";
        if ((batchVoting.getAdditionalData().containsKey(VotingAttributeTemplate.ADD_CHAT_TO_PROTOCOL)) &&
                ((batchVoting.getAdditionalData().get(BatchVotingConstants.BATCH_VOTING_DIALOG_ID_ATTR_NAME)) != null)){

            Long dialogId = VarUtils.getLong(batchVoting.getAdditionalData().get(BatchVotingConstants.BATCH_VOTING_DIALOG_ID_ATTR_NAME), null);
            List<ChatMessage> messages = chatMessageRepository.findByDialog_IdAndDateGreaterThanAndDateIsLessThanOrderByDateAsc(dialogId, toDate(batchVoting.getParameters().getStartDate()), createDate());

            boolean hasTextMessages = false;
            for (ChatMessage message : messages) {
                if ((message.getText() != null) && (!ru.radom.kabinet.utils.StringUtils.isEmpty(message.getText()))) {
                    hasTextMessages = true;
                }
            }
            if (hasTextMessages) {
                result = dialogHeader;
            }
        }
        return result;
    }

    public String dialogMessagesForEachFormat(Long dialogId, Date startDate, Date endDate, String valueFormat, String dateFormat, String oneRow, String twoRow) {
        StringBuilder stringBuilder = new StringBuilder();
        if (dialogId != null) {
            if (StringUtils.isBlank(dateFormat)) {
                dateFormat = DateUtils.Format.DATE_TIME_SHORT;
            }
            List<ChatMessage> messages;
            if (startDate == null && endDate == null) {
                messages = chatMessageRepository.findByDialog_IdOrderByDateAsc(dialogId);
            } else {
                messages = chatMessageRepository.findByDialog_IdAndDateGreaterThanAndDateIsLessThanOrderByDateAsc(dialogId, startDate, endDate);
            }
            boolean hasTextMessages = false;
            for (ChatMessage message : messages) {
                if ((message.getText() != null) && (!ru.radom.kabinet.utils.StringUtils.isEmpty(message.getText()))) {
                    hasTextMessages = true;
                }
            }

            if (hasTextMessages) {
                int index = 0;

                for (ChatMessage message : messages) {
                    if ((message.getText() != null) && (!ru.radom.kabinet.utils.StringUtils.isEmpty(message.getText()))) {
                        String avatar = EmailTemplateContextFunctions.resizeImage(message.getSender().getAvatar(), "c28");
                        String rowResult = valueFormat;
                        rowResult = rowResult.replaceAll("index", "" + (index + 1));
                        if (index % 2 == 0) {
                            rowResult = rowResult.replaceAll("oneRow", oneRow);
                            rowResult = rowResult.replaceAll("twoRow", "");
                        } else {
                            rowResult = rowResult.replaceAll("oneRow", "");
                            rowResult = rowResult.replaceAll("twoRow", twoRow);
                        }
                        rowResult = rowResult.replaceAll("avatar", avatar);
                        rowResult = rowResult.replaceAll("shortName", message.getSender().getShortName());
                        rowResult = rowResult.replaceAll("fullName", message.getSender().getFullName());

                        rowResult = rowResult.replaceAll("date", DateUtils.formatDate(message.getDate(), dateFormat));
                        rowResult = rowResult.replaceAll("text", message.getText());
                        stringBuilder.append(rowResult);
                        index++;
                    }
                }
            } else {

            }
        }

        return StringEscapeUtils.unescapeHtml4(stringBuilder.toString());
    }

    public Date toDate(LocalDateTime localDateTime) {
        Date result = null;
        if (localDateTime != null) {
            Timestamp timestamp = Timestamp.valueOf(localDateTime);
            result = new Date(timestamp.getTime());
        }
        return result;
    }

    public Date createDate() {
        return new Date();
    }

    public Long getLong(String value) {
        return VarUtils.getLong(value, null);
    }

    /**
     * Получить наименование должностей участника объединения
     * @param communityId
     * @param userId
     * @param multiplePrefix
     * @param singlePrefix
     * @param joinStr
     * @return
     */
    public String getPostNamesOfMember(Long communityId, Long userId, String multiplePrefix, String singlePrefix, String joinStr) {
        if (joinStr == null) {
            joinStr = ", ";
        }
        String result = null;
        if (communityId != null && communityId > 0) {
            CommunityMember communityMember = communityMemberDataService.getByCommunityIdAndUserId(communityId, userId);
            if (communityMember != null && communityMember.getPosts() != null && !communityMember.getPosts().isEmpty()) {
                Community community = communityDataService.getByIdFullData(communityId);
                List<String> postNames = new ArrayList<>();
                for (CommunityPost communityPost : communityMember.getPosts()) {
                    postNames.add(communityPost.getName() + " " + community.getShortRuName());
                }
                result = StringUtils.join(postNames, joinStr);
                if (multiplePrefix != null && postNames.size() > 1) {
                    result = multiplePrefix + result;
                } else if (singlePrefix != null && postNames.size() == 1) {
                    result = singlePrefix + result;
                }
            }
        }
        return result == null ? "" : result;
    }

    public String getBatchVotingData(BatchVoting batchVoting, String template) {
        String result = template;
        if (batchVoting != null) {
            int registeredVotersCount = getRegisteredVotersCount(batchVoting);
            String commonVotersCountMorph = HumansStringUtils.morph(batchVoting.getVotersAllowed().size(), "человек", "человека", "человек");
            String registeredVotersCountMorph = HumansStringUtils.morph(registeredVotersCount, "человек", "человека", "человек");


            result = result.replaceAll("commonVotersCountMorph", commonVotersCountMorph);
            result = result.replaceAll("registeredVotersCountMorph", registeredVotersCountMorph);
            result = result.replaceAll("commonVotersCount", "" + batchVoting.getVotersAllowed().size());
            result = result.replaceAll("registeredVotersCount", "" + registeredVotersCount);
        }

        return result;
    }

    public int getRegisteredVotersCount(BatchVoting batchVoting) {
        int registeredVotersCount = 0;
        for (RegisteredVoter registeredVoter : batchVoting.getVotersAllowed()) {
            if (RegisteredVoterStatus.REGISTERED.equals(registeredVoter.getStatus())) {
                registeredVotersCount++;
            }
        }
        return registeredVotersCount;
    }

    public BatchVoting testBatchVoting() {
        try {
            return batchVotingService.getBatchVoting(221, true, true);
        } catch (VotingSystemException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String morphByCount(int count, String str1, String str2, String str3) {
        return HumansStringUtils.morph(count, str1, str2, str3);
    }



}
