package ru.radom.kabinet.services.communities;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.field.Field;
import ru.askor.blagosfera.domain.field.IFieldOwner;
import ru.radom.kabinet.dao.DisallowedWordDao;
import ru.radom.kabinet.model.DisallowedType;
import ru.radom.kabinet.services.field.FieldValidateResult;
import ru.radom.kabinet.services.field.FieldsService;
import ru.radom.kabinet.services.field.ICustomFieldValidator;
import ru.radom.kabinet.utils.FieldConstants;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Created by vgusev on 28.03.2016.
 */
@Service
public class CommunityCustomFieldValidator implements ICustomFieldValidator {

    @Autowired
    private FieldsService fieldsService;

    @Autowired
    private DisallowedWordDao disallowedWordDao;

    @Override
    public boolean isCustomValidate(Field field, IFieldOwner owner) {
        Community community = (Community)owner;
        boolean result = false;
        switch (field.getInternalName()) {
            case FieldConstants.COMMUNITY_FULL_RU_NAME:
            case FieldConstants.COMMUNITY_BRIEF_DESCRIPTION:
            case FieldConstants.COMMUNITY_SHORT_LINK_NAME:
                result = true;
                break;
            case FieldConstants.COMMUNITY_ASSOCIATION_FORM:
                if (ParticipantsTypes.COMMUNITY_WITH_ORGANIZATION.equals(community.getCommunityType())) {
                    result = true;
                }
                break;
        }
        return result;
    }

    @Override
    public FieldValidateResult validate(Field field, IFieldOwner owner) {
        //Community community = (Community)owner;
        FieldValidateResult result = null;


        /*if (StringUtils.isEmpty(community.getAnnouncement())) {
            map.put("announcement", "Краткое описание целей не задано");
        } else if (community.getAnnouncement().length() > 255) {
            map.put("announcement", "Допустимый размер краткого описания целей до 255 символов");
        }*/


        switch (field.getInternalName()) {
            case FieldConstants.COMMUNITY_FULL_RU_NAME:
                if (field.getValue() == null) {
                    result = new FieldValidateResult(false, field, "Название объединения не задано");
                }
                if (field.getValue() != null && (field.getValue().length() < 3 || field.getValue().length() > 1000)) {
                    result = new FieldValidateResult(false, field, "Допустимый размер названия от 3 до 1000 символов");
                }
                break;
            case FieldConstants.COMMUNITY_BRIEF_DESCRIPTION:
                if (StringUtils.isBlank(field.getValue())) {
                    result = new FieldValidateResult(false, field, "Краткое описание целей не задано");
                } else if (field.getValue().length() > 255) {
                    result = new FieldValidateResult(false, field, "Допустимый размер краткого описания целей до 255 символов");
                }
                break;
            case FieldConstants.COMMUNITY_ASSOCIATION_FORM:
                if (StringUtils.isBlank(field.getValue())) {
                    result = new FieldValidateResult(false, field, "Не выбрана форма объединения");
                }
                break;
            case FieldConstants.COMMUNITY_SHORT_LINK_NAME:
                if (!StringUtils.isBlank(field.getValue())) {
                    boolean fieldValueExists =
                            fieldsService.fieldValueExists(
                                    FieldConstants.COMMUNITY_SHORT_LINK_NAME,
                                    field.getValue(),
                                    owner.getId()
                            );
                    if (fieldValueExists) {
                        result = new FieldValidateResult(false, field, "Данное короткое имя объединения уже занято");
                    }

                    if (result == null && field.getValue().length() < 4) {
                        result = new FieldValidateResult(false, field, "Имя ссылки должно состоять минимум из четырёх символов");
                    }

                    /*if (result == null) {
                        Pattern p = Pattern.compile("^[0-9]+$");
                        Matcher m = p.matcher(field.getValue());
                        if (m.matches()) {
                            result = new FieldValidateResult(false, field, "В имени ссылки запрещается использовать только цифры");
                        }
                    }*/

                    if (result == null) {
                        Pattern p = Pattern.compile("[a-zа-я0-9]+");
                        Matcher m = p.matcher(field.getValue());
                        if (!m.matches()) {
                            result = new FieldValidateResult(false, field, "В имени ссылки допускаются только русские и латинские строчные символы и цифры без пробелов");
                        }
                    }

                    if (result == null) {
                        List<String> disallowedWords = disallowedWordDao.getStringsByType(DisallowedType.COMMUNITY_SHORT_LINK_NAME);
                        if (disallowedWords.contains(field.getValue())) {
                            result = new FieldValidateResult(false, field, "Недопустимое имя ссылки");
                        }
                    }
                }
                break;
        }
        if (result == null) {
            result = new FieldValidateResult(true, field);
        }
        return result;
    }
}
