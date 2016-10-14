-- // add_system_setting_jivosite_key
-- Migration SQL that makes the change goes here.

insert into system_settings (id, key, val, description)
select nextval('system_settings_id'), 'jivositeKey', 'WIA2qekGNZ', 'ключ от jivosite'
where not exists(select id from system_settings where key = 'jivositeKey');

-- //@UNDO
-- SQL to undo the change goes here.


