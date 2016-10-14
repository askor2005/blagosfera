-- // add sms service enabled flag
-- Migration SQL that makes the change goes here.

insert into system_settings (id, key, val, description)
select nextval('system_settings_id'), 'sms_service.enabled', 'false', 'Разрешить рассылку СМС'
where not exists(select id from system_settings where key = 'sms_service.enabled');

-- //@UNDO
-- SQL to undo the change goes here.


