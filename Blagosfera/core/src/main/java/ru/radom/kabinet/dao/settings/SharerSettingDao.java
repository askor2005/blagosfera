package ru.radom.kabinet.dao.settings;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.settings.SharerSetting;
import ru.radom.kabinet.utils.VarUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("sharerSettingDao")
public class SharerSettingDao extends Dao<SharerSetting> {

	private SharerSetting getSetting(Long userId, String key) {
		return findFirst(Restrictions.eq("key", key), Restrictions.eq("user.id", userId));
	}

	public String get(Long userId, String key) {
		SharerSetting setting = getSetting(userId, key);
		return setting != null ? setting.getValue() : null;
	}

	public String get(Long userId, String key, String defaultValue) {
		String value = get(userId, key);
		return value != null ? value : defaultValue;
	}

	public SharerSetting getByKey(String key, Long userId) {
		return findFirst(Restrictions.eq("key", key), Restrictions.eq("user.id", userId));
	}

	/**
	 * Найти все настройки, которые начинаются с keyPreffix
	 * @param keyPreffix
	 * @param user
	 * @return
	 */
	public List<SharerSetting> findByKeyPreffix(String keyPreffix, UserEntity user) {
		return find(Restrictions.ilike("key", keyPreffix, MatchMode.START), Restrictions.eq("user", user));
	}

	public long getLong(Long userId, String key, long defaultValue) {
		String value = get(userId, key);
		return value != null ? Long.parseLong(value) : defaultValue;
	}

	public int getInteger(Long userId, String key, int defaultValue) {
		String value = get(userId, key);
		return value != null ? Integer.parseInt(value) : defaultValue;
	}

	public boolean getBoolean(Long userId, String key, boolean defaultValue) {
		String value = get(userId, key);
		return value != null ? Boolean.parseBoolean(value) : defaultValue;
	}

	public <T extends Enum> T getEnum(Long userId, final Class<T> clazz, final String key) {
		String value = get(userId, key);
		return (T) Enum.valueOf(clazz, value);
	}

	public SharerSetting set(UserEntity userEntity, String key, String value) {
		SharerSetting setting = getSetting(userEntity.getId(), key);
		if (setting == null) {
			setting = new SharerSetting();
			setting.setKey(key);
			setting.setUser(userEntity);
		}
		setting.setValue(value);
		saveOrUpdate(setting);
		return setting;
	}

	public List<Long> getLongsList(UserEntity userEntity, String key, List<Long> defaultValue) {
		String value = get(userEntity.getId(), key);
		List<Long> list = new ArrayList<>();
		try {
			for (String part : value.split(",")) {
				list.add(Long.parseLong(part));
			}
			return list;
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	public List<Integer> getIntegersList(UserEntity userEntity, String key, List<Integer> defaultValue) {
		String value = get(userEntity.getId(), key);
		List<Integer> list = new ArrayList<Integer>();
		try {
			for (String part : value.split(",")) {
				list.add(Integer.parseInt(part));
			}
			return list;
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * Получить список целочисленных настроек пользователей
	 * @param users
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public Map<UserEntity, Long> getLongsBySharerList(List<UserEntity> users, String key, Long defaultValue) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("key", key));
		criteria.add(Restrictions.in("user", users));
		List<SharerSetting> sharerSettings = find(criteria);
		Map<UserEntity, Long> result = new HashMap<>();
		for (SharerSetting sharerSetting : sharerSettings) {
			result.put(sharerSetting.getUser(), VarUtils.getLong(sharerSetting.getValue(), defaultValue));
		}
		return result;
	}
}
