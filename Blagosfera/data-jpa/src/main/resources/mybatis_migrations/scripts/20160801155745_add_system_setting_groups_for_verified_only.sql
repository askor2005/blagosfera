-- // add_system_setting_groups_for_verified_only
-- Migration SQL that makes the change goes here.


INSERT INTO system_settings (id, key, val, description)
select nextval('system_settings_id'), 'groups_members_verified_only_seolink', 'reg',
        'Группы в которые можно приглашать только идентифицированных пользователей' where not exists (select * from system_settings where key ='groups_members_verified_only_seolink');

-- //@UNDO
-- SQL to undo the change goes here.


