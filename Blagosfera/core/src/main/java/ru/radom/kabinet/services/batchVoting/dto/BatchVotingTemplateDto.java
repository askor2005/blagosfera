package ru.radom.kabinet.services.batchVoting.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import org.apache.commons.lang3.BooleanUtils;
import ru.askor.voting.domain.BatchVotingMode;
import ru.radom.kabinet.json.ShortDateTimeDeserializer;
import ru.radom.kabinet.json.ShortDateTimeSerializer;
import ru.radom.kabinet.json.TimeStampDateSerializer;
import ru.radom.kabinet.model.votingtemplate.BatchVotingAttributeTemplate;
import ru.radom.kabinet.model.votingtemplate.BatchVotingTemplateEntity;
import ru.radom.kabinet.model.votingtemplate.VotingTemplateEntity;
import ru.radom.kabinet.web.user.dto.UserDataDto;

import java.util.*;

/**
 *
 * Created by vgusev on 09.10.2015.
 */
@Getter
public class BatchVotingTemplateDto {

    // ИД шаблона
    public Long id;

    // Наименование собрания
    public String subject;
    // Поведение собрания
    public String behavior;
    // Создвать чат для собрания
    public boolean isNeedCreateChat;
    // Кворум собрания
    public long quorum;
    // Дата начала собрания
    @JsonSerialize(using = ShortDateTimeSerializer.class)
    @JsonDeserialize(using = ShortDateTimeDeserializer.class)
    public Date startDate;
    // дата окончания голосования
    @JsonSerialize(using = ShortDateTimeSerializer.class)
    @JsonDeserialize(using = ShortDateTimeDeserializer.class)
    public Date endDate;
    // Флаг - может ли собрание закончится до окончания времени собрания
    public boolean isCanFinishBeforeEndDate;
    // Список тех кто голосует
    //public Set<Long> votersAllowed = new HashSet<>();
    public List<VoterAllowedTemplateDto> votersAllowed = new ArrayList<>();

    // Вид проведения собрания
    public BatchVotingMode mode;
    // дата окончания регистрации
    @JsonSerialize(using = ShortDateTimeSerializer.class)
    @JsonDeserialize(using = ShortDateTimeDeserializer.class)
    public Date registrationEndDate;
    // Колисество рестартов голосования
    public int votingRestartCount;
    // Дополнительные атрибуты
    public List<BatchVotingAttributeTemplateDto> attributes = new ArrayList<>();
    // Вид голосования
    public boolean secretVoting;
    // Список голосований
    public List<VotingTemplateDto> votings = new ArrayList<>();
    // Флаг - нужно ли создавать голосования за председателя, секретаря и повестку дня
    public boolean isNeedAddAdditionalVotings = true;
    // Описание собрания
    public String description;

    // ИДы созданных собраний по данному шаблону
    public Set<Long> batchVotingIds;

    @JsonSerialize(using = TimeStampDateSerializer.class)
    public Date lastBatchVotingDate;

    // Создатель шаблона
    public UserDataDto creator;

    public boolean useBiometricIdentificationInAdditionalVotings;
    public boolean useBiometricIdentificationInRegistration;
    public boolean addChatToProtocol;

    public boolean testBatchVotingMode;

    public BatchVotingTemplateDto() {
    }

    public BatchVotingTemplateDto(BatchVotingTemplateEntity batchVotingTemplate) {
        try {
            id = batchVotingTemplate.getId();
            subject = batchVotingTemplate.getSubject();
            description = batchVotingTemplate.getDescription();
            isNeedCreateChat = BooleanUtils.toBooleanDefaultIfNull(batchVotingTemplate.getIsNeedCreateChat(), false);
            behavior = batchVotingTemplate.getBehavior();
            quorum = batchVotingTemplate.getQuorum();

            startDate = batchVotingTemplate.getStartDate();
            endDate = new Date(batchVotingTemplate.getStartDate().getTime() +
                    (batchVotingTemplate.getBatchVotingHoursCount() == null ? 1 :
                    batchVotingTemplate.getBatchVotingHoursCount()) * 60l * 60l * 1000l);
            /*endDate = DateUtils.formatDate(new Date(batchVotingTemplate.getStartDate().getTime()+
                    (batchVotingTemplate.getBatchVotingHoursCount() == null ? 1 :
                    batchVotingTemplate.getBatchVotingHoursCount())*60*60*1000),DateUtils.Format.DATE_TIME_SHORT);*/
            isCanFinishBeforeEndDate = BooleanUtils.toBooleanDefaultIfNull(batchVotingTemplate.getIsCanFinishBeforeEndDate(), false);

            votersAllowed.addAll(VoterAllowedTemplateDto.toListDto(batchVotingTemplate.getVotersAllowed()));

            //votersAllowed = batchVotingTemplate.getVotersAllowed();
            mode = batchVotingTemplate.getMode();
            /*registrationEndDate = DateUtils.formatDate(new Date(batchVotingTemplate.getStartDate().getTime() + (batchVotingTemplate.getRegistrationHoursCount() == null ? 1 :
                    batchVotingTemplate.getRegistrationHoursCount()) * 60 * 60 * 1000), DateUtils.Format.DATE_TIME_SHORT);*/
            registrationEndDate = new Date(batchVotingTemplate.getStartDate().getTime() +
                    (batchVotingTemplate.getRegistrationHoursCount() == null ? 1 :
                    batchVotingTemplate.getRegistrationHoursCount()) * 60l * 60l * 1000l);

            votingRestartCount = batchVotingTemplate.getVotingRestartCount();
            secretVoting = BooleanUtils.toBooleanDefaultIfNull( batchVotingTemplate.getSecretVoting(), false);
            isNeedAddAdditionalVotings = BooleanUtils.toBooleanDefaultIfNull(batchVotingTemplate.getIsNeedAddAdditionalVotings(), false);

            for (BatchVotingAttributeTemplate batchVotingAttributeTemplate : batchVotingTemplate.getAttributes()) {
                BatchVotingAttributeTemplateDto batchVotingAttributeTemplateDto = new BatchVotingAttributeTemplateDto();
                batchVotingAttributeTemplateDto.id = batchVotingAttributeTemplate.getId();
                batchVotingAttributeTemplateDto.name = batchVotingAttributeTemplate.getName();
                batchVotingAttributeTemplateDto.value = batchVotingAttributeTemplate.getValue();
                attributes.add(batchVotingAttributeTemplateDto);
            }

            // Создаём голосования
            for (VotingTemplateEntity votingTemplate : batchVotingTemplate.getVotings()) {
                votings.add(new VotingTemplateDto(votingTemplate));
            }

            batchVotingIds = batchVotingTemplate.getBatchVotings();
            lastBatchVotingDate = batchVotingTemplate.getLastBatchVotingDate();

            if (batchVotingTemplate.getCreator() != null) {
                creator = new UserDataDto(batchVotingTemplate.getCreator().toDomain());
            }

            useBiometricIdentificationInAdditionalVotings = batchVotingTemplate.getUseBiometricIdentificationInAdditionalVotings();
            useBiometricIdentificationInRegistration = batchVotingTemplate.getUseBiometricIdentificationInRegistration();
            addChatToProtocol = batchVotingTemplate.getAddChatToProtocol();

            testBatchVotingMode = batchVotingTemplate.isTestBatchVotingMode();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
