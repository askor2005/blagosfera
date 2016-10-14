package ru.radom.kabinet.services.communities;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.field.Field;
import ru.askor.blagosfera.domain.field.FieldType;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.communities.dto.CommunityFillingDto;
import ru.radom.kabinet.utils.exception.ExceptionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 03.11.2015.
 */
@Service
public class CommunityFillingService {

    @Autowired
    private SettingsManager settingsManager;

    /**
     * Получить данные по заполненности юр лица
     * @param community
     * @return
     */
    public CommunityFillingDto getCommunityFilling(Community community) {
        List<Field> filledFields = new ArrayList<>();
        List<Field> notFilledFields = new ArrayList<>();
        List<Field> requiredFields = new ArrayList<>();
        boolean avatarLoaded = !CommunityEntity.DEFAULT_AVATAR_URL.equals(community.getAvatar());
        boolean allRequiredFilled = true;
        int filledPoints = 0;
        int totalPoints = 0;
        //Collection<FieldValueEntity> fieldValues = community.getFieldValues();

        ExceptionUtils.check(community.getCommunityData() == null && community.getCommunityData().getFields() == null,
                "Необходимо загрузить поля объединения");

        List<Field> fields = community.getCommunityData().getFields();
        for (Field field : fields) {
            if (field.getType() == FieldType.SYSTEM || field.getType() == FieldType.SYSTEM_IMAGE) {
                continue;
            }
            if (StringUtils.isEmpty(field.getValue())) {
                notFilledFields.add(field);
                if (field.isRequired()) {
                    requiredFields.add(field);
                    allRequiredFilled = false;
                }
            } else {
                filledFields.add(field);
                filledPoints += field.getPoints();
            }
            totalPoints += field.getPoints();
        }

        int threshold = settingsManager.getSystemSettingAsInt("community.filling-threshold", 70);

        int percent = 0;
        if (totalPoints != 0) {
            percent = (!allRequiredFilled || !avatarLoaded) ? 0 : filledPoints * 100 / totalPoints;
        }

        return new CommunityFillingDto(percent, filledPoints, totalPoints, avatarLoaded, requiredFields, filledFields, notFilledFields, threshold);
    }
}
