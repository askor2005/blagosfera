package ru.radom.kabinet.services.bpmhandlers.voting.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import ru.askor.voting.domain.BatchVotingMode;
import ru.radom.kabinet.json.objectmapper.BooleanDeserializer;
import ru.radom.kabinet.utils.DateUtils;

import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Обёртка данных для bpm таска - собрание
 * Created by vgusev on 10.12.2015.
 */
@Data
public class BPMBatchVotingDto {

    /**
     * Создатель собрания
     */
    private Long ownerId;
    /**
     * Тема собрания
     */
    private String subject;
    /**
     * Описание собрания
     */
    private String description;
    /**
     * Возможность звержить собрание до завершения даты окончания
     */
    @JsonDeserialize(using = BooleanDeserializer.class)
    private boolean isCanFinishBeforeEndDate;
    /**
     * кворум в процентах
     */
    private long quorum;
    /**
     * Дата начала
     */
    private String startDate;
    /**
     * Нужно ли создавать чат собрания
     */
    @JsonDeserialize(using = BooleanDeserializer.class)
    private boolean isNeedCreateChat;
    /**
     * Участники собрания
     */
    private Set<Long> votersAllowed;
    /**
     * Вид проведения голосований - последовательное или параллельное
     */
    private BatchVotingMode mode;
    /**
     * Дата окончания собрания
     */
    private String endDate;
    /**
     * Количество рестартов голосований
     */
    private int votingRestartCount;
    /**
     * Нужно ли создавать дополнительные голосования
     */
    @JsonDeserialize(using = BooleanDeserializer.class)
    private boolean isNeedAddAdditionalVotings;
    /**
     * Голосования собрания
     */
    private BPMVotingsDto votings;
    /**
     * Вид голосований - открытый протокол по окончании или закрытый
     */
    private boolean secretVoting;
    /**
     * Дата окончания регистрации
     */
    private String registrationEndDate;

    /**
     * Очередь web сокета, куда шлётся сообщение что собрание создано
     */
    private String createBatchVotingQueue;

    /**
     * Дополнительные параметры собрания
     */
    private Map<String, String> additionalParameters;

    public Date getStartDate() {
        return DateUtils.parseDate(startDate, new Date(), DateUtils.Format.DATE_TIME_SHORT);
    }

    public Date getEndDate() {
        return DateUtils.parseDate(endDate, new Date(), DateUtils.Format.DATE_TIME_SHORT);
    }

    public Date getRegistrationEndDate() {
        return DateUtils.parseDate(registrationEndDate, new Date(), DateUtils.Format.DATE_TIME_SHORT);
    }

}
