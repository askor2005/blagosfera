package ru.radom.kabinet.services.bpmhandlers.voting.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import ru.askor.voting.domain.VotingType;
import ru.radom.kabinet.json.objectmapper.BooleanDeserializer;
import ru.radom.kabinet.voting.VotingButtonContentDto;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * Created by vgusev on 10.12.2015.
 */
@Data
public class BPMVotingDto {

    /**
     *
     */
    private String subject;
    /**
     *
     */
    private String description;
    /**
     * Условие создания голосования. Голосование не создаётся если значение false
     */
    private String votingCreateCondition;
    /**
     *
     */
    private VotingType votingType;
    /**
     *
     */
    @JsonDeserialize(using = BooleanDeserializer.class)
    private boolean isVoteCommentsAllowed;
    /**
     *
     */
    private Set<BPMVotingItemDto> votingItems;
    /**
     * Возможность отменить голосование
     */
    @JsonDeserialize(using = BooleanDeserializer.class)
    private boolean isVoteCancellable;
    /**
     * Видимость голосования
     */
    @JsonDeserialize(using = BooleanDeserializer.class)
    private boolean isVisible;
    /**
     * Варианты голосований из переменной BPM
     */
    private Set<Object> votingItemsVar;
    /**
     * Добавить в голосование вариант "Воздержаться"
     */
    @JsonDeserialize(using = BooleanDeserializer.class)
    private boolean addAbstain;
    /**
     * Считать собрание провальным если в голосовании в За\Против победил вариант Против
     */
    @JsonDeserialize(using = BooleanDeserializer.class)
    private boolean isFailOnContraResult;
    /**
     * Минимальное количество выбираемых значений
     */
    private Long minSelectionCount;
    /**
     * Количество максимально выбираемых значений
     */
    private Long maxSelectionCount;

    private Long minWinnersCount;

    private Long maxWinnersCount;

    /**
     * Множество победителей
     */
    @JsonDeserialize(using = BooleanDeserializer.class)
    private boolean isMultipleWinners;
    /**
     * Минимальный процент для победы
     */
    private int percentForWin;
    /**
     * Индекс голосования
     */
    private int index;

    /**
     * Коллекция с наименованием кнопок и контент, который открывается при клике на кнопку
     */
    private Set<VotingButtonContentDto> votingButtons;
    /**
     * Текст который отображается в протоколе голосования при победе какого то варианта
     */
    private String votingWinnerText;

    private String sentence;

    private String successDecree;

    private String failDecree;

    private String selectFromType;

    private Integer sourceVotingIndex;

    public Set<Object> getVotingItems() {
        // Если есть данные из статичного списка вариантов, которые добавил пользователь
        // Либо установлены варианты из переменной
        Set<Object> result;
        if ((votingItems == null || votingItems.size() == 0) &&
                votingItemsVar != null && votingItemsVar.size() > 0 && "votingItemsFromVar".equals(selectFromType)) {
            result = votingItemsVar;
        } else if ((votingItemsVar == null || votingItemsVar.size() == 0) &&
                votingItems != null && votingItems.size() > 0 && ("addMine".equals(selectFromType) || selectFromType == null)) {
            result = new HashSet<>();
            for (BPMVotingItemDto votingItem : votingItems) {
                result.add(votingItem.getValue());
            }
        } else {
            result = new HashSet<>();
        }
        return result;
    }
}
