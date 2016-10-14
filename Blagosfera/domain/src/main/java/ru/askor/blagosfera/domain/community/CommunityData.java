package ru.askor.blagosfera.domain.community;

import lombok.Data;
import ru.askor.blagosfera.domain.Address;
import ru.askor.blagosfera.domain.field.Field;
import ru.askor.blagosfera.domain.field.FieldsGroup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс с полями объединения
 * Created by vgusev on 08.03.2016.
 */
@Data
public class CommunityData implements Serializable {

    /**
     * Список полей объединения
     */
    private List<FieldsGroup> fieldGroups;

    /**
     * Полное описание целей объединения
     */
    private String description;

    /**
     * ИНН
     */
    private String inn;

    /**
     * Фактический адрес юр лица
     */
    private Address actualAddress;

    /**
     * Юридический адрес юр лица
     */
    private Address registrationAddress;

    public Field getFieldByInternalName(String internalName) {
        Field result = null;
        if (fieldGroups != null) {
            for (FieldsGroup fieldGroup : fieldGroups) {
                List<Field> fields = fieldGroup.getFields();
                if (fields != null) {
                    for (Field field : fields) {
                        if (field.getInternalName().equals(internalName)) {
                            result = field;
                            break;
                        }
                    }
                }
                if (result != null) {
                    break;
                }
            }
        }
        return result;
    }

    public String getFieldValueByInternalName(String internalName) {
        Field fieldDomain = getFieldByInternalName(internalName);
        String value = null;
        if (fieldDomain != null) {
            value = fieldDomain.getValue();
        }
        return value;
    }

    public List<Field> getFields() {
        List<Field> result = new ArrayList<>();
        if (fieldGroups != null) {
            for (FieldsGroup fieldGroup : fieldGroups) {
                List<Field> fields = fieldGroup.getFields();
                if (fields != null) {
                    result.addAll(fields.stream().collect(Collectors.toList()));
                }
            }
        }
        return result;
    }

    public void addField(Field field) {
        if (fieldGroups != null && fieldGroups.size() > 0) {
            List<Field> fields = fieldGroups.get(0).getFields();
            if (fields != null) {
                fields.add(field);
            }
        }
    }

    /**
     * Создать данные объединения по набору полей
     * @param fieldValues
     * @param factCountry
     * @param registrationCountry
     */
    /*public CommunityData(List<FieldValue> fieldValues, String factCountry, String registrationCountry) {
        this();
        initFromFields(fieldValues, factCountry, registrationCountry);
    }*/


    /*public String getDescription() {
        return getFieldValueByFieldName("COMMUNITY_DESCRIPTION");
    }*/

    /*public String getAnnouncement() {
        return getFieldValueByFieldName("COMMUNITY_BRIEF_DESCRIPTION");
    }*/



    /*public String getSeoLink() {
        return getFieldValueByFieldName("COMMUNITY_SHORT_LINK_NAME");
    }*/

    // TODO ????
    /*public String getEditableSeoLink() {
        int slashIndex = getSeoLink().lastIndexOf('/');
        return slashIndex == -1 ? getSeoLink() : getSeoLink().substring(slashIndex + 1);
    }*/

    /*public String getInn() {

        return getFieldValueByFieldName("COMMUNITY_INN");

    }*/

    /**
     * Тип объединения
     */
    /*public String getTypeCommunity() {
        String type = "---";
        String communityType = getFieldValueByFieldName(COMMUNITY_TYPE);
        if (COMMUNITY_WITH_ORGANIZATION.equals(communityType)) {
            type = "Объединение в рамках юридического лица";
        } else if (COMMUNITY_WITHOUT_ORGANIZATION.equals(communityType)) {
            type = "Объединение вне рамок юридического лица";
        } else if (COMMUNITY_IP.equals(communityType)) {
            type = "Объединение ИП";
        }
        return type;
    }*/

    // Фактический адрес юр лица
    /*public Address getActualAddress() {
        return new Address(
                country,
                getFieldValueByFieldName("COMMUNITY_LEGAL_F_REGION"),
                getFieldValueByFieldName("COMMUNITY_LEGAL_F_AREA"),
                getFieldValueByFieldName("COMMUNITY_LEGAL_F_LOCALITY"),
                getFieldValueByFieldName("COMMUNITY_LEGAL_F_STREET"),
                getFieldValueByFieldName("COMMUNITY_LEGAL_F_HOUSE"),
                getFieldValueByFieldName("COMMUNITY_LEGAL_F_GEO_POSITION"),
                getFieldValueByFieldName("COMMUNITY_LEGAL_F_GEO_LOCATION"),
                getFieldValueByFieldName("COMMUNITY_LEGAL_F_OFFICE"), "офис"
        );
    }

    // Юридический адрес юр лица
    public Address getRegistrationAddress() {
        return new Address(
                registrationCountry,
                getFieldValueByFieldName("COMMUNITY_LEGAL_REGION"),
                getFieldValueByFieldName("COMMUNITY_LEGAL_AREA"),
                getFieldValueByFieldName("COMMUNITY_LEGAL_LOCALITY"),
                getFieldValueByFieldName("COMMUNITY_LEGAL_STREET"),
                getFieldValueByFieldName("COMMUNITY_LEGAL_HOUSE"),
                getFieldValueByFieldName("COMMUNITY_LEGAL_GEO_POSITION"),
                getFieldValueByFieldName("COMMUNITY_LEGAL_GEO_LOCATION"),
                getFieldValueByFieldName("COMMUNITY_LEGAL_OFFICE"), "офис"
        );
    }*/
}
