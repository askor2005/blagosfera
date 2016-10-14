-- // create_letter_of_authority_settings
-- Migration SQL that makes the change goes here.

-- Настройки оповещения по доверенностям
insert into system_settings (id, key, val, description)
select nextval('seq_system_settings'), 'lettersofauthorities.expireddate.notify.days.before', '90,60,30,14,7', 'Дни оповещения участникков доверенности до её завершения'
where not exists (select id from system_settings where key = 'lettersofauthorities.expireddate.notify.days.before');

-- //@UNDO
-- SQL to undo the change goes here.


