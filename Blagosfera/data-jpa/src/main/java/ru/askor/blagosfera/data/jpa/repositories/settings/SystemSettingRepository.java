package ru.askor.blagosfera.data.jpa.repositories.settings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.askor.blagosfera.data.jpa.entities.settings.SystemSettingEntity;

import java.util.List;

public interface SystemSettingRepository extends JpaRepository<SystemSettingEntity, Long>, JpaSpecificationExecutor<SystemSettingEntity> {

    SystemSettingEntity findOneByKey(String key);

    List<SystemSettingEntity> findAllByKeyIn(List<String> keys);
}
