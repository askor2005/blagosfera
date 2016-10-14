-- // add_check_validation_in_votings
-- Migration SQL that makes the change goes here.

insert into system_settings (id, key, val, description)
select nextval('system_settings_id'), 'voting.voters.need.verified', 'true', 'Должен ли быть участник собрания идентифицирован в системе'
where not exists(select id from system_settings where key = 'voting.voters.need.verified');

insert into system_settings (id, key, val, description)
select nextval('system_settings_id'), 'document.signer.need.verified', 'true', 'Должен ли быть подписываютщий участник документа идентифицирован в системе'
where not exists(select id from system_settings where key = 'document.signer.need.verified');

-- //@UNDO
-- SQL to undo the change goes here.


