package ru.radom.kabinet.model.votingtemplate;

import org.apache.commons.lang3.BooleanUtils;
import ru.askor.voting.domain.VotingState;
import ru.askor.voting.domain.VotingType;
import ru.radom.kabinet.model.LongIdentifiable;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * Created by vgusev on 13.10.2015.
 */
@Entity
@Table(name = "voting_templates")
public class VotingTemplateEntity extends LongIdentifiable {

    // Тип голосования
    @Column(name = "voting_type", nullable = false)
    private VotingType votingType;

    // С возможностью переголосовать
    @Column(name = "is_vote_cancellable", nullable = false)
    private Boolean isVoteCancellable;

    // Можно оставить комментарий к голосованию
    @Column(name = "is_vote_comments_allowed", nullable = false)
    private Boolean isVoteCommentsAllowed;

    // минимальное количество выбираемых вариантов
    @Column(name = "min_selection_count", nullable = false)
    private Long minSelectionCount;

    // максимальное количество выбираемых вариантов
    @Column(name = "max_selection_count", nullable = false)
    private Long maxSelectionCount;

    // Минимальное количество победителей
    @Column(name = "min_winners_count")
    private Long minWinnersCount;

    // Максимальное количество победителей
    @Column(name = "max_winners_count")
    private Long maxWinnersCount;

    // Завершать собрание при кривом голосовании
    //@Column(name = "is_fail_on_contra_result", nullable = false)
    @Column(name = "stop_batch_voting_on_fail_result", nullable = false)
    private Boolean stopBatchVotingOnFailResult;

    // Порядковый номер голосования
    @Column(name = "index", nullable = false)
    private Integer index;

    // Наименование голосования
    @Column(name = "subject", nullable = false, length = 1000)
    private String subject;

    // Описание голосовния
    @Column(name = "description", nullable = false, length = 10000)
    private String description;

    // Состояние голосования
    @Column(name = "voting_state", nullable = false)
    private VotingState votingState;

    // Видимость голосования
    @Column(name = "is_visible", nullable = false)
    private Boolean isVisible;

    // Варианты голосований
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "voting")
    private Set<VotingItemTemplate> votingItems = new HashSet<>();

    // Дополнительные атрибуты
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "voting",cascade = {CascadeType.REMOVE})
    private Set<VotingAttributeTemplate> attributes = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_voting_id")
    private BatchVotingTemplateEntity batchVoting;

    @Column(name = "multiple_winners", nullable = false)
    private Boolean multipleWinners;

    // Постановление в случае успешного завершения голосования
    @Column(name = "success_decree", length = 10000)
    private String successDecree;

    // Постановление в случае не успешного завершения голосования
    @Column(name = "fail_decree", length = 10000)
    private String failDecree;

    // Предложение по голосованию
    @Column(name = "sentence", length = 10000)
    private String sentence;

    // Процент для победы
    @Column(name = "percent_for_win")
    private Integer percentForWin;

    public VotingType getVotingType() {
        return votingType;
    }

    public void setVotingType(VotingType votingType) {
        this.votingType = votingType;
    }

    public Boolean getIsVoteCancellable() {
        return isVoteCancellable;
    }

    public void setIsVoteCancellable(Boolean isVoteCancellable) {
        this.isVoteCancellable = isVoteCancellable;
    }

    public Boolean getIsVoteCommentsAllowed() {
        return isVoteCommentsAllowed;
    }

    public void setIsVoteCommentsAllowed(Boolean isVoteCommentsAllowed) {
        this.isVoteCommentsAllowed = isVoteCommentsAllowed;
    }

    public Long getMinSelectionCount() {
        return minSelectionCount;
    }

    public void setMinSelectionCount(Long minSelectionCount) {
        this.minSelectionCount = minSelectionCount;
    }

    public Long getMaxSelectionCount() {
        return maxSelectionCount;
    }

    public void setMaxSelectionCount(Long maxSelectionCount) {
        this.maxSelectionCount = maxSelectionCount;
    }

    public Long getMaxWinnersCount() {
        return maxWinnersCount;
    }

    public void setMaxWinnersCount(Long maxWinnersCount) {
        this.maxWinnersCount = maxWinnersCount;
    }

    public Long getMinWinnersCount() {
        return minWinnersCount;
    }

    public void setMinWinnersCount(Long minWinnersCount) {
        this.minWinnersCount = minWinnersCount;
    }

    public boolean isStopBatchVotingOnFailResult() {
        return BooleanUtils.toBooleanDefaultIfNull(stopBatchVotingOnFailResult, true);
    }

    public void setStopBatchVotingOnFailResult(boolean stopBatchVotingOnFailResult) {
        this.stopBatchVotingOnFailResult = stopBatchVotingOnFailResult;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public VotingState getVotingState() {
        return votingState;
    }

    public void setVotingState(VotingState votingState) {
        this.votingState = votingState;
    }

    public Boolean getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(Boolean isVisible) {
        this.isVisible = isVisible;
    }

    public Set<VotingItemTemplate> getVotingItems() {
        return votingItems;
    }

    public Set<VotingAttributeTemplate> getAttributes() {
        return attributes;
    }

    public BatchVotingTemplateEntity getBatchVoting() {
        return batchVoting;
    }

    public void setBatchVoting(BatchVotingTemplateEntity batchVoting) {
        this.batchVoting = batchVoting;
    }

    public Boolean getMultipleWinners() {
        return multipleWinners;
    }

    public void setMultipleWinners(Boolean multipleWinners) {
        this.multipleWinners = multipleWinners;
    }

    public Boolean getUseBiometricIdentification() {
        return checkAttributeFlag(VotingAttributeTemplate.USE_BIOMETRIC_IDENTIFICATION);
    }

    public void setUseBiometricIdentification(boolean useBiometricIdentification) {
        setAttributeFlag(VotingAttributeTemplate.USE_BIOMETRIC_IDENTIFICATION, useBiometricIdentification);
    }

    public Boolean getSkipResults() {
        return checkAttributeFlag(VotingAttributeTemplate.SKIP_RESULTS);
    }

    public void setSkipResults(boolean skipResults) {
        setAttributeFlag(VotingAttributeTemplate.SKIP_RESULTS, skipResults);
    }

    public String getSuccessDecree() {
        return successDecree;
    }

    public void setSuccessDecree(String successDecree) {
        this.successDecree = successDecree;
    }

    public String getFailDecree() {
        return failDecree;
    }

    public void setFailDecree(String failDecree) {
        this.failDecree = failDecree;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public void setAddAbstain(boolean addAbstain) {
        setAttributeFlag(VotingAttributeTemplate.ADD_ABSTAIN, addAbstain);
    }

    public boolean isAddAbstain() {
        return checkAttributeFlag(VotingAttributeTemplate.ADD_ABSTAIN);
    }

    public void setNotRestartable(boolean restartable) {
        setAttributeFlag(VotingAttributeTemplate.NOT_RESTARTABLE, restartable);
    }

    public boolean isNotRestartable() {
        return checkAttributeFlag(VotingAttributeTemplate.NOT_RESTARTABLE);
    }

    public Integer getPercentForWin() {
        return percentForWin;
    }

    public void setPercentForWin(Integer percentForWin) {
        this.percentForWin = percentForWin;
    }

    private void setAttributeFlag(String attributeName, boolean value) {
        VotingAttributeTemplate existingAttribute = null;

        for(Iterator<VotingAttributeTemplate> it = attributes.iterator(); it.hasNext();) {
            VotingAttributeTemplate attribute = it.next();

            if (attribute.getName().equals(attributeName)) {
                if (!value) {
                    it.remove();
                } else {
                    existingAttribute = attribute;
                }

                break;
            }
        }

        if (value && (existingAttribute == null)) {
            existingAttribute = new VotingAttributeTemplate();
            existingAttribute.setVoting(this);
            existingAttribute.setName(attributeName);
            existingAttribute.setValue(attributeName);
            attributes.add(existingAttribute);
        }
    }

    private boolean checkAttributeFlag(String attributeName) {
        for(VotingAttributeTemplate attribute : attributes) {
            if (attribute.getName().equals(attributeName)) return true;
        }

        return false;
    }
}
