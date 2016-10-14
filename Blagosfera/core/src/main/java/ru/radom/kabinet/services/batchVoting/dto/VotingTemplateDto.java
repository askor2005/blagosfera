package ru.radom.kabinet.services.batchVoting.dto;

import ru.askor.voting.domain.VotingState;
import ru.askor.voting.domain.VotingType;
import ru.radom.kabinet.model.votingtemplate.VotingAttributeTemplate;
import ru.radom.kabinet.model.votingtemplate.VotingItemTemplate;
import ru.radom.kabinet.model.votingtemplate.VotingTemplateEntity;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 11.10.2015.
 */
public class VotingTemplateDto {

    public Long id;

    // Тип голосования
    public VotingType votingType;

    // С возможностью переголосовать
    public boolean isVoteCancellable;

    public boolean addAbstain;

    public boolean notRestartable;

    // Можно оставить комментарий к голосованию
    public boolean isVoteCommentsAllowed;

    // минимальное количество выбираемых вариантов
    public long minSelectionCount;

    // максимальное количество выбираемых вариантов
    public long maxSelectionCount;

    // минимальное количество победителей
    public Long minWinnersCount;

    // максимальное количествопобедителей
    public Long maxWinnersCount;

    // Завершать собрание при кривом голосовании
    public boolean stopBatchVotingOnFailResult;

    // Порядковый номер голосования
    public int index;

    // Наименование голосования
    public String subject;

    // Описание голосовния
    public String description;

    // Состояние голосования
    public VotingState votingState;

    // Видимость голосования
    public boolean isVisible;

    // разрешить несколько победителей
    public boolean multipleWinners;

    // Использовать биометрическую идентификацию
    public boolean useBiometricIdentification;
    //Пропускать страницу с результатами голосования
    public boolean skipResults;

    // Варианты голосований
    public List<VotingItemTemplateDto> votingItems = new ArrayList<>();

    // Дополнительные атрибуты
    public List<VotingAttributeTemplateDto> attributes = new ArrayList<>();

    // Постановление в случае успешного завершения голосования
    public String successDecree;

    // Постановление в случае не успешного завершения голосования
    public String failDecree;

    // Предложение по голосованию
    public String sentence;

    // Процент для победы
    public int percentForWin;

    public VotingTemplateDto() {
    }

    public VotingTemplateDto(VotingTemplateEntity votingTemplate) {
        id = votingTemplate.getId();
        subject = votingTemplate.getSubject();
        description = votingTemplate.getDescription();
        index = votingTemplate.getIndex();
        stopBatchVotingOnFailResult = votingTemplate.isStopBatchVotingOnFailResult();
        isVisible = votingTemplate.getIsVisible();
        isVoteCancellable = votingTemplate.getIsVoteCancellable();
        addAbstain = votingTemplate.isAddAbstain();
        notRestartable = votingTemplate.isNotRestartable();
        isVoteCommentsAllowed = votingTemplate.getIsVoteCommentsAllowed();
        minSelectionCount = votingTemplate.getMinSelectionCount();
        maxSelectionCount = votingTemplate.getMaxSelectionCount();
        minWinnersCount = votingTemplate.getMinWinnersCount();
        maxWinnersCount = votingTemplate.getMaxWinnersCount();
        votingState = votingTemplate.getVotingState();
        votingType = votingTemplate.getVotingType();
        multipleWinners = votingTemplate.getMultipleWinners();
        useBiometricIdentification = votingTemplate.getUseBiometricIdentification();
        skipResults = votingTemplate.getSkipResults();

        for (VotingAttributeTemplate votingAttributeTemplate : votingTemplate.getAttributes()) {
            VotingAttributeTemplateDto votingAttributeTemplateDto = new VotingAttributeTemplateDto();
            votingAttributeTemplateDto.id = votingAttributeTemplate.getId();
            votingAttributeTemplateDto.name = votingAttributeTemplate.getName();
            votingAttributeTemplateDto.value = votingAttributeTemplate.getValue();
            attributes.add(votingAttributeTemplateDto);
        }

        for (VotingItemTemplate votingItemTemplate : votingTemplate.getVotingItems()) {
            votingItems.add(new VotingItemTemplateDto(votingItemTemplate));
        }

        successDecree = votingTemplate.getSuccessDecree();
        failDecree = votingTemplate.getFailDecree();
        sentence = votingTemplate.getSentence();
        percentForWin = votingTemplate.getPercentForWin() == null ? 51 : votingTemplate.getPercentForWin();
    }

    public Long getId() {
        return id;
    }

    public VotingType getVotingType() {
        return votingType;
    }

    public boolean isVoteCancellable() {
        return isVoteCancellable;
    }

    public boolean isVoteCommentsAllowed() {
        return isVoteCommentsAllowed;
    }

    public long getMinSelectionCount() {
        return minSelectionCount;
    }

    public long getMaxSelectionCount() {
        return maxSelectionCount;
    }

    public boolean isStopBatchVotingOnFailResult() {
        return stopBatchVotingOnFailResult;
    }

    public int getIndex() {
        return index;
    }

    public String getSubject() {
        return subject;
    }

    public String getDescription() {
        return description;
    }

    public VotingState getVotingState() {
        return votingState;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public List<VotingItemTemplateDto> getVotingItems() {
        return votingItems;
    }

    public List<VotingAttributeTemplateDto> getAttributes() {
        return attributes;
    }

    public boolean isMultipleWinners() {
        return multipleWinners;
    }

    public void setMultipleWinners(boolean multipleWinners) {
        this.multipleWinners = multipleWinners;
    }

    public boolean isUseBiometricIdentification() {
        return useBiometricIdentification;
    }

    public void setUseBiometricIdentification(boolean useBiometricIdentification) {
        this.useBiometricIdentification = useBiometricIdentification;
    }

    public String getSuccessDecree() {
        return successDecree;
    }

    public String getFailDecree() {
        return failDecree;
    }

    public String getSentence() {
        return sentence;
    }

    public int getPercentForWin() {
        return percentForWin;
    }
}
