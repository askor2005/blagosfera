package ru.radom.kabinet.document.generator;

import lombok.Data;
import ru.askor.blagosfera.domain.document.IDocumentParticipant;

import java.util.List;

/**
 *
 * Created by vgusev on 06.07.2015.
 */
@Data
public class ParticipantCreateDocumentParameter {

    /**
     * Тип участника (пользователь, объединение)
     */
    private String type;

    /**
     * ИД участника
     */
    private Long id;

    /**
     * ИДы участников
     */
    private List<Long> ids;

    /**
     * Массив объектов - участников документа
     */
    private List<IDocumentParticipant> documentParticipants;

    /**
     * Наименование участника в шаблоно
     */
    private String name;

    public ParticipantCreateDocumentParameter() {
    }

    /**
     * Создать параметр для одного участника
     * @param type
     * @param id
     * @param name
     */
    public ParticipantCreateDocumentParameter(String type, Long id, String name) {
        this.type = type;
        this.id = id;
        this.name = name;
    }

    /**
     * Создать параметр для списка участников
     * @param type
     * @param ids
     * @param name
     */
    public ParticipantCreateDocumentParameter(String type, List<Long> ids, String name) {
        this.type = type;
        this.ids = ids;
        this.name = name;
    }

    /**
     *
     * @param documentParticipants
     * @param type
     * @param name
     */
    public ParticipantCreateDocumentParameter(List<IDocumentParticipant> documentParticipants, String type, String name) {
        this.type = type;
        this.documentParticipants = documentParticipants;
        this.name = name;
    }
}
