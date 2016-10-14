-- // add sms service api key
-- Migration SQL that makes the change goes here.

insert into system_settings (id, key, val, description)
select nextval('system_settings_id'), 'sms_service.api_key', '', 'API-ключ для службы рассылки СМС'
where not exists(select id from system_settings where key = 'sms_service.api_key');

-- //@UNDO
-- SQL to undo the change goes here.


