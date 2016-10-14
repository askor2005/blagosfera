package ru.radom.kabinet.model.votingtemplate;

import org.apache.commons.lang3.BooleanUtils;
import ru.askor.voting.domain.BatchVotingMode;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;

import javax.persistence.*;
import java.util.*;

/**
 *
 * Created by vgusev on 13.10.2015.
 */
@Entity
@Table(name = "batch_voting_templates")
public class BatchVotingTemplateEntity extends LongIdentifiable {

    // Наименование собрания
    @Column(name = "subject", nullable = false)
    private String subject;

    // Поведение собрания
    @Column(name = "behavior", nullable = false)
    private String behavior;

    @Column(name = "is_need_create_chat")
    private Boolean isNeedCreateChat;

    // Кворум собрания
    @Column(name = "quorum", nullable = false)
    private Long quorum;

    // Дата начала собрания
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date", nullable = false)
    private Date startDate;

    // Количество часов выделенное на собрание
    @Column(name = "batch_voting_hours_count")
    private Integer batchVotingHoursCount;

    // Флаг - может ли собрание закончится до окончания времени собрания
    @Column(name = "is_can_finish_before_end_date", nullable = false)
    private Boolean isCanFinishBeforeEndDate;

    // Список тех кто голосует
    /*@ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name="voters_allowed_templates", joinColumns = @JoinColumn(name = "batch_voting_id"))
    @Column(name="voter_id")
    private Set<Long> votersAllowed = new HashSet<>();*/

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "batchVotingTemplate", cascade = CascadeType.REMOVE)
    private List<VoterAllowedTemplate> votersAllowed = new ArrayList<>();

    // Вид проведения собрания
    @Column(name="mode", nullable = false)
    private BatchVotingMode mode;

    // Количество часов на регистрацию
    @Column(name="registration_hours_count")
    private Integer registrationHoursCount;

    // Колисество рестартов голосования
    @Column(name="voting_restart_count", nullable = false)
    private Integer votingRestartCount;

    // Дополнительные атрибуты
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "batchVoting", cascade = CascadeType.ALL)
    private List<BatchVotingAttributeTemplate> attributes = new ArrayList<>();

    // Вид голосования
    @Column(name="secret_voting", nullable = false)
    private Boolean secretVoting;

    // Список голосований
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "batchVoting", cascade = CascadeType.ALL)
    private List<VotingTemplateEntity> votings = new ArrayList<>();

    // Флаг - нужно ли создавать голосования за председателя, секретаря и повестку дня
    @Column(name="is_need_add_additional_votings", nullable = false)
    private Boolean isNeedAddAdditionalVotings = true;

    // Объединение в котором создали шаблон
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private CommunityEntity community;

    // Создатель шаблона
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private UserEntity creator;

    // Описание шаблона собрания
    @Column(name = "description", length = 100000)
    private String description;

    // ИДы созданных собраний на основе шаблона
    @ElementCollection
    @CollectionTable(name="batch_voting_templates_to_batch_voting",
            joinColumns=@JoinColumn(name="batch_voting_template_id", nullable = false, updatable = false))
    @Column(name="batch_voting_id", nullable = false, updatable = false)
    private Set<Long> batchVotings = new HashSet<>();

    @Column(name = "last_batch_voting_date", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastBatchVotingDate;

    @Column(name="test_mode", nullable = true)
    private Boolean testBatchVotingMode;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBehavior() {
        return behavior;
    }

    public void setBehavior(String behavior) {
        this.behavior = behavior;
    }

    public Boolean getIsNeedCreateChat() {
        return isNeedCreateChat;
    }

    public void setIsNeedCreateChat(Boolean isNeedCreateChat) {
        this.isNeedCreateChat = isNeedCreateChat;
    }

    public Long getQuorum() {
        return quorum;
    }

    public void setQuorum(Long quorum) {
        this.quorum = quorum;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Integer getBatchVotingHoursCount() {
        return batchVotingHoursCount;
    }

    public void setBatchVotingHoursCount(Integer batchVotingHoursCount) {
        this.batchVotingHoursCount = batchVotingHoursCount;
    }

    public Boolean getIsCanFinishBeforeEndDate() {
        return isCanFinishBeforeEndDate;
    }

    public void setIsCanFinishBeforeEndDate(Boolean isCanFinishBeforeEndDate) {
        this.isCanFinishBeforeEndDate = isCanFinishBeforeEndDate;
    }

    /*public Set<Long> getVotersAllowed() {
        return votersAllowed;
    }*/

    public List<VoterAllowedTemplate> getVotersAllowed() {
        return votersAllowed;
    }

    public BatchVotingMode getMode() {
        return mode;
    }

    public void setMode(BatchVotingMode mode) {
        this.mode = mode;
    }

    public Integer getRegistrationHoursCount() {
        return registrationHoursCount;
    }

    public void setRegistrationHoursCount(Integer registrationHoursCount) {
        this.registrationHoursCount = registrationHoursCount;
    }

    public Integer getVotingRestartCount() {
        return votingRestartCount;
    }

    public void setVotingRestartCount(Integer votingRestartCount) {
        this.votingRestartCount = votingRestartCount;
    }

    public List<BatchVotingAttributeTemplate> getAttributes() {
        return attributes;
    }

    public Boolean getSecretVoting() {
        return secretVoting;
    }

    public void setSecretVoting(Boolean secretVoting) {
        this.secretVoting = secretVoting;
    }

    public List<VotingTemplateEntity> getVotings() {
        return votings;
    }

    public Boolean getIsNeedAddAdditionalVotings() {
        return isNeedAddAdditionalVotings;
    }

    public void setIsNeedAddAdditionalVotings(Boolean isNeedAddAdditionalVotings) {
        this.isNeedAddAdditionalVotings = isNeedAddAdditionalVotings;
    }

    public CommunityEntity getCommunity() {
        return community;
    }

    public void setCommunity(CommunityEntity community) {
        this.community = community;
    }

    public UserEntity getCreator() {
        return creator;
    }

    public void setCreator(UserEntity creator) {
        this.creator = creator;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Long> getBatchVotings() {
        return batchVotings;
    }

    public Date getLastBatchVotingDate() {
        return lastBatchVotingDate;
    }

    public void setLastBatchVotingDate(Date lastBatchVotingDate) {
        this.lastBatchVotingDate = lastBatchVotingDate;
    }

    public boolean isTestBatchVotingMode() {
        return BooleanUtils.toBooleanDefaultIfNull(testBatchVotingMode, false);
    }

    public void setTestBatchVotingMode(Boolean testBatchVotingMode) {
        this.testBatchVotingMode = testBatchVotingMode;
    }

    public Boolean getUseBiometricIdentificationInAdditionalVotings() {
        for(BatchVotingAttributeTemplate attribute : attributes) {
            if (attribute.getName().equals(VotingAttributeTemplate.USE_BIOMETRIC_IDENTIFICATION + "_IN_ADDITIONAL_VOTINGS")) return true;
        }

        return false;
    }
    public Boolean getAddChatToProtocol() {
        for(BatchVotingAttributeTemplate attribute : attributes) {
            if (attribute.getName().equals(VotingAttributeTemplate.ADD_CHAT_TO_PROTOCOL)) {
                return true;
            };
        }

        return false;
    }

    public void setUseBiometricIdentificationInAdditionalVotings(boolean useBiometricIdentificationInAdditionalVotings) {
        BatchVotingAttributeTemplate existingAttribute = null;

        for(Iterator<BatchVotingAttributeTemplate> it = attributes.iterator(); it.hasNext();) {
            BatchVotingAttributeTemplate attribute = it.next();

            if (attribute.getName().equals(VotingAttributeTemplate.USE_BIOMETRIC_IDENTIFICATION + "_IN_ADDITIONAL_VOTINGS")) {
                if (!useBiometricIdentificationInAdditionalVotings) {
                    it.remove();
                } else {
                    existingAttribute = attribute;
                }

                break;
            }
        }

        if (useBiometricIdentificationInAdditionalVotings && (existingAttribute == null)) {
            existingAttribute = new BatchVotingAttributeTemplate();
            existingAttribute.setBatchVoting(this);
            existingAttribute.setName(VotingAttributeTemplate.USE_BIOMETRIC_IDENTIFICATION + "_IN_ADDITIONAL_VOTINGS");
            existingAttribute.setValue(VotingAttributeTemplate.USE_BIOMETRIC_IDENTIFICATION);
            attributes.add(existingAttribute);
        }
    }
    public void setAddChatToProtocol(boolean addChatToProtocol) {
        BatchVotingAttributeTemplate existingAttribute = null;

        for(Iterator<BatchVotingAttributeTemplate> it = attributes.iterator(); it.hasNext();) {
            BatchVotingAttributeTemplate attribute = it.next();

            if (attribute.getName().equals(VotingAttributeTemplate.ADD_CHAT_TO_PROTOCOL)) {
                if (!addChatToProtocol) {
                    it.remove();
                } else {
                    existingAttribute = attribute;
                }

                break;
            }
        }

        if (addChatToProtocol && (existingAttribute == null)) {
            existingAttribute = new BatchVotingAttributeTemplate();
            existingAttribute.setBatchVoting(this);
            existingAttribute.setName(VotingAttributeTemplate.ADD_CHAT_TO_PROTOCOL);
            existingAttribute.setValue(VotingAttributeTemplate.ADD_CHAT_TO_PROTOCOL);
            attributes.add(existingAttribute);
        }
    }

    public Boolean getUseBiometricIdentificationInRegistration() {
        for(BatchVotingAttributeTemplate attribute : attributes) {
            if (attribute.getName().equals(VotingAttributeTemplate.USE_BIOMETRIC_IDENTIFICATION + "_IN_REGISTRATION")) return true;
        }

        return false;
    }

    public void setUseBiometricIdentificationInRegistration(boolean useBiometricIdentificationInRegistration) {
        BatchVotingAttributeTemplate existingAttribute = null;

        for(Iterator<BatchVotingAttributeTemplate> it = attributes.iterator(); it.hasNext();) {
            BatchVotingAttributeTemplate attribute = it.next();

            if (attribute.getName().equals(VotingAttributeTemplate.USE_BIOMETRIC_IDENTIFICATION + "_IN_REGISTRATION")) {
                if (!useBiometricIdentificationInRegistration) {
                    it.remove();
                } else {
                    existingAttribute = attribute;
                }

                break;
            }
        }

        if (useBiometricIdentificationInRegistration && (existingAttribute == null)) {
            existingAttribute = new BatchVotingAttributeTemplate();
            existingAttribute.setBatchVoting(this);
            existingAttribute.setName(VotingAttributeTemplate.USE_BIOMETRIC_IDENTIFICATION + "_IN_REGISTRATION");
            existingAttribute.setValue(VotingAttributeTemplate.USE_BIOMETRIC_IDENTIFICATION);
            attributes.add(existingAttribute);
        }
    }
}
