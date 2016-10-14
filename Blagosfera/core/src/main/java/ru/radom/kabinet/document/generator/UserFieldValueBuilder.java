package ru.radom.kabinet.document.generator;

import ru.askor.blagosfera.domain.document.userfields.DocumentUserFieldType;
import ru.askor.blagosfera.domain.document.userfields.fieldsgroups.UserFieldsGroup;
import ru.radom.kabinet.utils.DateUtils;

import java.util.*;

/**
 * Created by vgusev on 06.07.2015.
 */
public class UserFieldValueBuilder {

    /**
     * Создать значение поля - массив строк.
     * @param fieldName
     * @param values
     * @return
     */
    public static UserFieldValue createStringValue(String fieldName, List<String> values) {
        return new UserFieldValue(DocumentUserFieldType.STRING, fieldName, values);
    }

    /**
     * Создать значение поля - строка
     * @param fieldName
     * @param value
     * @return
     */
    public static UserFieldValue createStringValue(String fieldName, String value) {
        return createStringValue(fieldName, Arrays.asList(new String[]{value}));
    }

    /**
     * Создать значение поля - массив целых чисел
     * @param fieldName
     * @param values
     * @return
     */
    public static UserFieldValue createLongValue(String fieldName, List<Long> values) {
        return new UserFieldValue(DocumentUserFieldType.NUMBER, fieldName, values);
    }

    /**
     * Создать значение поля - целое число
     * @param fieldName
     * @param value
     * @return
     */
    public static UserFieldValue createLongValue(String fieldName, Long value) {
        return createLongValue(fieldName, Arrays.asList(new Long[]{value}));
    }

    /**
     * Создать значение поля - массив целых чисел
     * @param fieldName
     * @param values
     * @return
     */
    public static UserFieldValue createIntegerValue(String fieldName, List<Integer> values) {
        return new UserFieldValue(DocumentUserFieldType.NUMBER, fieldName, values);
    }

    /**
     * Создать значение поля - целое число
     * @param fieldName
     * @param value
     * @return
     */
    public static UserFieldValue createIntegerValue(String fieldName, Integer value) {
        return createIntegerValue(fieldName, Arrays.asList(new Integer[]{value}));
    }

    /**
     * Создать значение поля - массив дробных чисел
     * @param fieldName
     * @param values
     * @return
     */
    public static UserFieldValue createDoubleValue(String fieldName, List<Double> values) {
        return new UserFieldValue(DocumentUserFieldType.NUMBER, fieldName, values);
    }

    /**
     * Создать значение поля - дробное число
     * @param fieldName
     * @param value
     * @return
     */
    public static UserFieldValue createDoubleValue(String fieldName, Double value) {
        return createDoubleValue(fieldName, Arrays.asList(new Double[]{value}));
    }

    /**
     * Создать значение поля - массив дат
     * @param fieldName
     * @param values
     * @return
     */
    public static UserFieldValue createDateValue(String fieldName, List<Date> values) {
        return new UserFieldValue(DocumentUserFieldType.DATE, fieldName, values);
    }

    /**
     * Создать значение поля - дата
     * @param fieldName
     * @param value
     * @return
     */
    public static UserFieldValue createDateValue(String fieldName, Date value) {
        return createDateValue(fieldName, Arrays.asList(new Date[]{value}));
    }

    /**
     * Создать значение поля - массив участников документа
     * @param fieldName
     * @param participantIds
     * @return
     */
    public static UserFieldValue createParticipantValue(String fieldName, List<Long> participantIds) {
        return new UserFieldValue(DocumentUserFieldType.PARTICIPANT, fieldName, participantIds);
    }

    /**
     * Создать значение поля - участник документа
     * @param fieldName
     * @param participantId
     * @return
     */
    public static UserFieldValue createParticipantValue(String fieldName, Long participantId) {
        return createParticipantValue(fieldName, Arrays.asList(new Long[]{participantId}));
    }

    /**
     * Создать значение поля денежного типа
     * @param fieldName
     * @param values
     * @return
     */
    public static UserFieldValue createCurrencyValue(String fieldName, List<Double> values) {
        return new UserFieldValue(DocumentUserFieldType.CURRENCY, fieldName, values);
    }

    /**
     * Создать значение поля денежного типа
     * @param fieldName
     * @param value
     * @return
     */
    public static UserFieldValue createCurrencyValue(String fieldName, Double value) {
        return createCurrencyValue(fieldName, Arrays.asList(new Double[]{value}));
    }

    /**
     * Создать значение поля c типом группы полей
     * @param fieldName
     * @param values
     * @return
     */
    public static UserFieldValue createFieldsGroupsValueMultiRows(String fieldName, List<List<UserFieldsGroup>> values) {
        return new UserFieldValue(DocumentUserFieldType.FIELDS_GROUPS, fieldName, values);
    }

    /**
     * Создать значение поля c типом группы полей
     * @param fieldName
     * @param value
     * @return
     */
    public static UserFieldValue createFieldsGroupsValueOneRow(String fieldName, List<UserFieldsGroup> value) {
        List<List<UserFieldsGroup>> values = new ArrayList<>();
        values.add(value);
        return createFieldsGroupsValueMultiRows(fieldName, values);
    }

    /**
     * Создать значение поля c типом документ
     * @param fieldName
     * @param documentIds
     * @return
     */
    public static UserFieldValue createDocumentValue(String fieldName, List<Long> documentIds) {
        return new UserFieldValue(DocumentUserFieldType.DOCUMENT, fieldName, documentIds);
    }

    /**
     * Создать значение поля c типом документ
     * @param fieldName
     * @param documentId
     * @return
     */
    public static UserFieldValue createDocumentValue(String fieldName, Long documentId) {
        List<Long> values = new ArrayList<>();
        values.add(documentId);
        return createDocumentValue(fieldName, values);
    }

    /**
     * Создает значение в зависимости от типа
     */
    public static UserFieldValue createByType(String type, String fieldName, Object value) {
        DocumentUserFieldType fieldType = DocumentUserFieldType.getByType(type);
        if(value instanceof List) {
            if (fieldType.equals(DocumentUserFieldType.FIELDS_GROUPS)) {
                return createFieldsGroupsValueOneRow(fieldName, (List<UserFieldsGroup>) value);
            } else {
                return new UserFieldValue(fieldType, fieldName, (List<Object>)value);
            }
        }
        switch (fieldType) {
            case CURRENCY: {
                if (value instanceof Number) {
                    return createCurrencyValue(fieldName, ((Number) value).doubleValue());
                } else {
                    return createCurrencyValue(fieldName, Double.parseDouble(String.valueOf(value)));
                }
            }
            case DATE: {
                if (value instanceof String) {
                    return createDateValue(fieldName, DateUtils.parseDate((String) value, null));
                } else {
                    return createDateValue(fieldName, (Date) value);
                }
            }
            case PARTICIPANT:
            case DOCUMENT: {
                if (value instanceof Number) {
                    return new UserFieldValue(fieldType, fieldName, Collections.singletonList(((Number) value).longValue()));
                } else {
                    return new UserFieldValue(fieldType, fieldName, Collections.singletonList(Long.parseLong(String.valueOf(value))));
                }
            }
            case NUMBER: {
                if (value instanceof Long) {
                    return createLongValue(fieldName, (Long) value);
                } else if (value instanceof Integer) {
                    return createIntegerValue(fieldName, (Integer) value);
                } else if (value instanceof Float) {
                    return createDoubleValue(fieldName, ((Float) value).doubleValue());
                } else if (value instanceof Double) {
                    return createDoubleValue(fieldName, (Double) value);
                } else {
                    return createDoubleValue(fieldName, Double.parseDouble(String.valueOf(value)));
                }
            }
            default:
            case STRING:
                return createStringValue(fieldName, String.valueOf(value));
        }
    }
}
