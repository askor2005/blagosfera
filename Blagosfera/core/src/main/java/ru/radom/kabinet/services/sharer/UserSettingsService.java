package ru.radom.kabinet.services.sharer;

import ru.askor.blagosfera.domain.user.User;

import java.util.List;

/**
 *
 * Created by vgusev on 18.03.2016.
 */
@Deprecated
public interface UserSettingsService {

    String get(User user, String key);

    Long get(User user, String key, long defaultValue);

    String get(User user, String key, String defaultValue);

    boolean getBoolean(User user, String key, boolean defaultValue);

    List<Long> getLongsList(User user, String key, List<Long> defaultValue);

    void set(User user, String key, String value);

    void delete(User user, String key);
}
