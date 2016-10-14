-- // add_signed_protocol_column_in_batch_voting_template
-- Migration SQL that makes the change goes here.

CREATE SEQUENCE seq_voters_allowed_templates
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE seq_voters_allowed_templates
  OWNER TO kabinet;

ALTER TABLE voters_allowed_templates ADD COLUMN id bigint;
ALTER TABLE voters_allowed_templates ADD COLUMN sign_protocol boolean;
update voters_allowed_templates set id = nextval('seq_voters_allowed_templates');
ALTER TABLE voters_allowed_templates ALTER COLUMN id SET NOT NULL;
ALTER TABLE voters_allowed_templates ADD CONSTRAINT voters_allowed_templates_pkey PRIMARY KEY(id);

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
	"secretaryVotingParticipantName":"Секретарь собрания"
}',
'Настройки для создания протокола собраний которые созданы конструктором собраний';

-- //@UNDO
-- SQL to undo the change goes here.


