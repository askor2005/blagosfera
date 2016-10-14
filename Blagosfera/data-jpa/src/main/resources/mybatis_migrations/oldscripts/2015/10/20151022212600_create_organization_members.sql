-- // create_organization_members
-- Migration SQL that makes the change goes here.

CREATE SEQUENCE seq_organization_community_members START 1;
CREATE TABLE organization_community_members
(
  id bigint NOT NULL,
  status integer NOT NULL,
  community_id bigint NOT NULL,
  document_id bigint,
  organization_id bigint NOT NULL,
  CONSTRAINT organization_community_members_pkey PRIMARY KEY (id),
  CONSTRAINT fk_l7ro64soaq6s2tif36ke4fxgw FOREIGN KEY (document_id)
      REFERENCES flowofdocument (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_shkwscrpqm2ps2a3elu40plvt FOREIGN KEY (organization_id)
      REFERENCES communities (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_t0pckp5b1v9aobb0sw1keglj8 FOREIGN KEY (community_id)
      REFERENCES communities (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT uk_jn0oj9xlvrmpetwnrjgcc5vd8 UNIQUE (community_id, organization_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE organization_community_members
  OWNER TO kabinet;


CREATE SEQUENCE seq_organization_community_member_parameters START 1;
CREATE TABLE organization_community_member_parameters
(
  id bigint NOT NULL,
  param_name character varying(10000),
  param_value character varying(10000),
  rganization_community_member_id bigint NOT NULL,
  CONSTRAINT organization_community_member_parameters_pkey PRIMARY KEY (id),
  CONSTRAINT fk_5c22sjli62f1d3vgefltlgbhi FOREIGN KEY (rganization_community_member_id)
      REFERENCES organization_community_members (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE organization_community_member_parameters
  OWNER TO kabinet;

  -- Настройка документов для участников объединения - юр лиц
  insert into system_settings (id, key, val, description)
  select nextval('seq_system_settings'), 'POOrganizationMemberSettings',
  '{
	// Настройки по умолчанию для заявления о вступлении в ПО
	statementJoinTemplateCode: "STATEMENT_JOIN_UR_LICO_TO_PO",
	statementJoinCommunityParticipantName: "Потребительское общество",
	statementJoinOrganizationParticipantName: "Кандидат в пайщики юр лицо",

	// Настройки по умполнчанию для протокола на принятие юр лиц в пайщики ПО
	protocolJoinTemplateCode: "PROTOCOL_JOIN_TEMPLATE_CODE",
	protocolJoinCommunityParticipantName: "Потребительское общество",
	protocolJoinOrganizationsParticipantName: "Список юр лиц",
	protocolJoinOrganizationsDocumentsUserField: "Заявления юр лиц",

	// Настройки по умполнчанию для заявления о выходе из ПО
	statementExcludeTemplateCode: "STATEMENT_EXCLUDE_TEMPLATE_CODE",
	statementExcludeCommunityParticipantName: "Потребительское общество",
	statementExcludeOrganizationParticipantName: "Кандидат на выход из ПО",

	// Настройки по умполнчанию для протокола на выход юр лиц из ПО
	protocolExcludeTemplateCode: "PROTOCOL_EXCLUDE_TEMPLATE_CODE",
	protocolExcludeCommunityParticipantName: "Потребительское общество",
	protocolExcludeOrganizationsParticipantName: "Список юр лиц",
	protocolExcludeOrganizationsDocumentsUserField: "Заявления юр лиц"
}', 'Настройки для пайщиков юр лиц в ПО'
where not exists (select id from system_settings where key = 'POOrganizationMemberSettings');


-- //@UNDO
-- SQL to undo the change goes here.


