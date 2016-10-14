package ru.radom.kabinet.services.communities;

/**
 *
 * Created by vgusev on 03.11.2015.
 */
public interface CommunitiesPermissions {

    // Дает возможность редактировать информацию об объединении, такую как название, описание, тип, форма и т.д.
    String SETTINGS_COMMON_PERMISSION = "SETTINGS_COMMON";

    // даёт возможность создавать/редактировать собрания и шаблоны собраний объединения
    String VOTINGS_ADMIN = "VOTINGS_ADMIN";

    // даёт возможность просматривать все собрания объединения, а не только те в которых участвует пользователь
    String VOTINGS_VIEW = "VOTINGS_VIEW";
}
