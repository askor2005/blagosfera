package ru.radom.kabinet.document.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.askor.blagosfera.domain.document.ParticipantField;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Обёртка участника документооборота которая используется для заполнения полей при формировании документа
 * Created by vgusev on 07.04.2016.
 */
@Data
public class DocumentParticipantSourceDto {

    /**
     * ИД источника данных
     */
    private Long id;

    /**
     * Название участника документа (берётся из названия класса документа)
     */
    private String name;


    /**
     * Тип участника документа
     */
    private ParticipantsTypes type;

    /**
     * Дочерние участники документа
     */
    private Map<String, List<DocumentParticipantSourceDto>> childMap;

    /**
     * Набор полей участника для заполнения в шаблоне
     */
    private List<ParticipantField> participantFields = new ArrayList<>();

    private int index;
}
