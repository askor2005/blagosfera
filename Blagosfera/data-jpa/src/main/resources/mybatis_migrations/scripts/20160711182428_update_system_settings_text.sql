-- // update_system_settings_text
-- Migration SQL that makes the change goes here.

update system_settings set description='Необходимое количество приглашённых идентифицированных участников для того чтобы стать Регистратором 3-го Ранга'  where key='registrator.level3.needVerifiedSharers';
update system_settings set description='Необходимое количество сертифицированных объединений, которые должны создать приглашённые идентифицированные участники, для того чтобы стать Регистратором 2-го Ранга' where key='registrator.level2.needVerifiedCommunities';	

update system_settings set description='Должен ли быть участник собрания идентифицирован в системе' where key='voting.voters.need.verified';
update system_settings set description='Должен ли быть подписывающий участник документа идентифицирован в системе' where key='document.signer.need.verified';

update system_settings set description='Максимальное время жизни сессии идентификации пользователя, в минутах.' where key='certification.session.lifetime';	




-- //@UNDO
-- SQL to undo the change goes here.


