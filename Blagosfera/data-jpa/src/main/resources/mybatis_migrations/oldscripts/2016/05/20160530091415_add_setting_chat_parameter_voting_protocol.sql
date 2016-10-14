-- // add_setting_chat_parameter_voting_protocol
-- Migration SQL that makes the change goes here.

update system_settings set val='{"templateCode":"batch_voting_protocol_constructor", "communityParticipantName":"Объединение", "votersParticipantName":"Список участников собрания", "protocolUserFieldName":"Протокол","chatUserFieldName" : "Чат"}' where key = 'constructor.batch.voting.settings';

-- //@UNDO
-- SQL to undo the change goes here.


