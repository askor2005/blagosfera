-- // add_setting_show_groups_number_to_user
-- Migration SQL that makes the change goes here.

INSERT INTO system_settings(id, key, val, description)
    VALUES (nextval('system_settings_id'), 'sharer.right.communities.pagesize', '5', 'по скольку загружать объединений в правом меню');

-- //@UNDO
-- SQL to undo the change goes here.

delete from system_settings where key = 'sharer.right.communities.pagesize';
