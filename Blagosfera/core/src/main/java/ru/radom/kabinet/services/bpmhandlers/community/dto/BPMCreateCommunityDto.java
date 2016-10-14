package ru.radom.kabinet.services.bpmhandlers.community.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import ru.askor.blagosfera.domain.community.CommunityAccessType;
import ru.radom.kabinet.json.TimeStampDateDeserializer;
import ru.radom.kabinet.model.fields.FieldFileEntity;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Обёртка данных таска по созданию объединения
 * Created by vgusev on 20.01.2016.
 */
@Data
public class BPMCreateCommunityDto {

    /**
     * Полное название на русском языке
     */
    private String fullName;

    /**
     * Вид доступа к объединению
     */
    private CommunityAccessType accessType;

    /**
     * Видимость объединения
     */
    private boolean invisible;

    /**
     * ИД создателя объединения
     */
    private Long creatorId;

    /**
     * ИД участников системы - членов объединения
     */
    @JsonDeserialize(using = StringByCommaToSetDeserializer.class)
    private Set<Long> memberIds;

    /**
     * Ссылка на картинку объединения
     */
    private String avatarUrl;

    /**
     * Дата создания объединения в виде timespamp
     */
    @JsonDeserialize(using = TimeStampDateDeserializer.class)
    private Date createdAt;

    /**
     * ИД родительского объединения
     */
    private Long parentId;

    /**
     * ИД основного вида деятельности
     */
    private Long mainOkvedId;

    /**
     * ИДы видов деятельности
     */
    @JsonDeserialize(using = StringByCommaToSetDeserializer.class)
    private Set<Long> okvedIds;

    /**
     * ИДы сфер деятельности
     */
    @JsonDeserialize(using = StringByCommaToSetDeserializer.class)
    private Set<Long> activityScopeIds;

    /**
     * Значение полей объединения
     */
    private Map<String, String> fields;

    /**
     *
     */
    private Map<String, List<FieldFileEntity>> fieldFiles;

}
