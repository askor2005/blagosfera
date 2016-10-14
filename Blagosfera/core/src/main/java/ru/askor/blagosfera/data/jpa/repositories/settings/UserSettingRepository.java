package ru.askor.blagosfera.data.jpa.repositories.settings;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.radom.kabinet.model.settings.SharerSetting;

public interface UserSettingRepository extends JpaRepository<SharerSetting, Long> {

    SharerSetting findOneByUser_IdAndKey(Long userId, String key);
}
