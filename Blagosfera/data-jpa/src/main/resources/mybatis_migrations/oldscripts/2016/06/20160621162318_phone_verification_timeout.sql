-- // phone verification timeout
-- Migration SQL that makes the change goes here.

insert into system_settings (id, key, val, description)
select nextval('system_settings_id'), 'mobile_phone.verification_timeout', '300', 'Время, отведенное на подтверждение номера телефона, в секундах'
where not exists(select id from system_settings where key = 'mobile_phone.verification_timeout');

-- //@UNDO
-- SQL to undo the change goes here.


