-- // add registrator system settings
-- Migration SQL that makes the change goes here.

insert into system_settings (id, key, val, description)
select nextval('seq_system_settings'), 'registrator.level3.needVerifiedSharers', '100',
'Необходимое количество приглашённых идентифицированных участников для того чтобы стать Регистратором 3-го Ранга'
where not exists(select id from system_settings where key = 'registrator.level3.needVerifiedSharers');

insert into system_settings (id, key, val, description)
select nextval('seq_system_settings'), 'registrator.level2.needVerifiedCommunities', '20',
'Необходимое количество сертифицированных объединений, которые должны создать приглашённые сертифицированных участники, для того чтобы стать Регистратором 2-го Ранга'
where not exists(select id from system_settings where key = 'registrator.level2.needVerifiedCommunities');

-- //@UNDO
-- SQL to undo the change goes here.


