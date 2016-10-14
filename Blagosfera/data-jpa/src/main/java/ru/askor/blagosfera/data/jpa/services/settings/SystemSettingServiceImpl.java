package ru.askor.blagosfera.data.jpa.services.settings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.data.jpa.entities.settings.SystemSettingEntity;
import ru.askor.blagosfera.data.jpa.repositories.settings.SystemSettingRepository;
import ru.askor.blagosfera.data.jpa.specifications.settings.SystemSettingSpec;
import ru.askor.blagosfera.domain.settings.SystemSetting;
import ru.askor.blagosfera.domain.settings.SystemSettingsPage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maxim Nikitin on 10.03.2016.
 */
@Transactional
@Service("systemSettingService")
public class SystemSettingServiceImpl implements SystemSettingService {

    @Autowired
    private SystemSettingRepository systemSettingRepository;

    public SystemSettingServiceImpl() {
    }

    @Override
    public String getSystemSetting(String key) {
        SystemSettingEntity systemSettingEntity = systemSettingRepository.findOneByKey(key);
        if (systemSettingEntity == null) return null;
        return systemSettingEntity.getValue();
    }

    @Override
    public String setSystemSetting(String key, String value, String description) {
        SystemSettingEntity systemSettingEntity = systemSettingRepository.findOneByKey(key);

        if (systemSettingEntity == null) {
            systemSettingEntity = new SystemSettingEntity();
            systemSettingEntity.setKey(key);
        }

        systemSettingEntity.setValue(value);

        if (description != null) {
            systemSettingEntity.setDescription(description);
        }

        systemSettingRepository.save(systemSettingEntity);
        return value;
    }

    @Override
    public void deleteSystemSetting(Long settingId) {
        systemSettingRepository.delete(settingId);
    }

    @Override
    public List<SystemSetting> getSystemSettings(List<String> keys) {
        List<SystemSetting> result = new ArrayList<>();
        List<SystemSettingEntity> settings = systemSettingRepository.findAllByKeyIn(keys);

        for (SystemSettingEntity setting : settings) {
            result.add(setting.toDomain());
        }

        return result;
    }

    @Override
    public SystemSettingsPage getSystemSettings(int page, int size, String keyFilter, String descriptionFilter) {
        PageRequest pageRequest = new PageRequest(page, size);
        Specifications<SystemSettingEntity> filters = Specifications.where(SystemSettingSpec.keyLike(keyFilter)).and(SystemSettingSpec.descriptionLike(descriptionFilter));
        Page<SystemSettingEntity> settings = systemSettingRepository.findAll(filters, pageRequest);
        SystemSettingsPage result = new SystemSettingsPage();
        result.total = settings.getTotalElements();

        for (SystemSettingEntity setting : settings.getContent()) {
            result.items.add(setting.toDomain());
        }

        return result;
    }
}
