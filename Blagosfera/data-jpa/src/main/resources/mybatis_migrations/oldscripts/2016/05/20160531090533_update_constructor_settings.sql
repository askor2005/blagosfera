-- // update_constructor_settings
-- Migration SQL that makes the change goes here.

delete from system_settings where key = 'constructor.batch.voting.settings';
insert into system_settings(id, key, val, description)
select nextval('system_settings_id'), 'constructor.batch.voting.settings',
'{
	"templateCode":"batch_voting_protocol_constructor",
	"templateCodeWithoutPresidentVoting":"batch_voting_protocol_constructor_without_president",
	"communityParticipantName":"Объединение",
	"voterWhoSignProtocolParticipantName" : "Подписанты протокола",
	"votersParticipantName":"Список участников собрания",
	"protocolUserFieldName":"Протокол",
	"presidentVotingParticipantName":"Председатель собрания",
	"secretaryVotingParticipantName":"Секретарь собрания",
	"chatUserFieldName" : "Чат"
}',
'Настройки для создания протокола собраний которые созданы конструктором собраний';



-- //@UNDO
-- SQL to undo the change goes here.


