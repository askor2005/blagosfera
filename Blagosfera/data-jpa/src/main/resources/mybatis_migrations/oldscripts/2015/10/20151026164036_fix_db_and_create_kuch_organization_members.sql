-- // fix_db_and_create_kuch_organization_members
-- Migration SQL that makes the change goes here.

do $$
declare permissionId bigint;
declare associationFormId bigint;
begin
	-- Удаляем форму объединения из таблицы прав доступа к объединению
	ALTER TABLE community_permissions DROP COLUMN community_association_form_id RESTRICT;
	-- Удаляем форму объединения из таблицы с разделами объединения
	ALTER TABLE community_sections DROP COLUMN asoociation_form_id RESTRICT;
	-- Создаём таблицу с формами объединений в правах доступа к объединению
	CREATE TABLE community_permission_association_forms
	(
	  community_permission_id bigint NOT NULL,
	  association_form_id bigint NOT NULL,
	  CONSTRAINT community_permission_association_forms_pkey PRIMARY KEY (community_permission_id, association_form_id),
	  CONSTRAINT fk_3w4vle8x7q8kuoxkponitcadi FOREIGN KEY (association_form_id)
	      REFERENCES list_editor_item (id) MATCH SIMPLE
	      ON UPDATE NO ACTION ON DELETE NO ACTION,
	  CONSTRAINT fk_s86261cpve3aent2w4qg6jmpb FOREIGN KEY (community_permission_id)
	      REFERENCES community_permissions (id) MATCH SIMPLE
	      ON UPDATE NO ACTION ON DELETE NO ACTION
	)
	WITH (
	  OIDS=FALSE
	);
	ALTER TABLE community_permission_association_forms
	  OWNER TO kabinet;



	-- Создаём права доступа - пайщик в ПО
	insert into community_permissions (id, name, position, title, description, security_role)
	select nextval('seq_community_permissions'), 'PO_COMMUNITY_SHARE', 301, 'Роль пайщика', 'Даёт возможность доступа к страницам: Шаблоны собраний, Конструктор собраний, Создать КУч', false
	where not exists (select id from community_permissions where name = 'PO_COMMUNITY_SHARE');

	select id into permissionId from community_permissions where name = 'PO_COMMUNITY_SHARE';

	-- Мнемокод формы объединения - ПО
	--"community_cooperative_society";
	select id into associationFormId from list_editor_item where mnemo_code = 'community_cooperative_society';

	-- Добавляем для роли "Пайщик" форму объединения - ПО
	insert into community_permission_association_forms(community_permission_id, association_form_id)
	values (permissionId, associationFormId);


	-- Роль принятия и вывода пайщиков
	select id into permissionId from community_permissions where name = 'ROLE_APPROVE_SHARERS';

	-- Мнемокод формы объединения - ПО
	--"community_cooperative_society";
	select id into associationFormId from list_editor_item where mnemo_code = 'community_cooperative_society';

	insert into community_permission_association_forms(community_permission_id, association_form_id)
	values (permissionId, associationFormId);

	-- Мнемокод формы объединения - КУч
	--"cooperative_plot";
	select id into associationFormId from list_editor_item where mnemo_code = 'cooperative_plot';

	insert into community_permission_association_forms(community_permission_id, association_form_id)
	values (permissionId, associationFormId);

	-- Устанавливаем роль "Пайщик" для страниц "Создание КУч", "Создание собрания" и "Шаблоны собраний"
	update community_sections set permission = 'PO_COMMUNITY_SHARE' where name in ('CREATE_MEETING', 'BATCH_VOTING_TEMPLATES', 'BATCH_VOTING_CONSTRUCTOR');


	-- Создаём мнемокоды для форм объедиения: Регистраторы благосферы и редакторы благосферы
	update list_editor_item set mnemo_code = 'blagosfera_registrators' where id = 189;
	update list_editor_item set mnemo_code = 'blagosfera_editors' where id = 190;

	-- Удаляем таблицу с группами форм объединений
	drop table community_association_forms_groups cascade;
	-- Удаляем таблицу с формами объединений
	drop table community_association_forms cascade;
	-- Удаляем таблицу с устаревшими документами
	drop table documents cascade;
	-- Удаляем таблицу с устаревшими шаблонами документов
	drop table document_templates cascade;
	-- Удаляем таблицу с устаревшими типами документов
	drop table document_template_types cascade;

	-- Настройка документов для участников объединения - юр лиц в КУч ПО
	  insert into system_settings (id, key, val, description)
	  select nextval('seq_system_settings'), 'KuchOrganizationMemberSettings',
	  '{
		// Настройки по умолчанию для заявления о вступлении в КУч ПО
		statementJoinTemplateCode: "STATEMENT_JOIN_UR_LICO_TO_KUCH_PO",
		statementJoinCommunityParticipantName: "Кооперативный участок",
		statementJoinOrganizationParticipantName: "Кандидат в пайщики юр лицо",

		// Настройки по умполнчанию для протокола на принятие юр лиц в пайщики КУч ПО
		protocolJoinTemplateCode: "KUCH_PROTOCOL_JOIN_TEMPLATE_CODE",
		protocolJoinCommunityParticipantName: "Кооперативный участок",
		protocolJoinOrganizationsParticipantName: "Список юр лиц",
		protocolJoinOrganizationsDocumentsUserField: "Заявления юр лиц",

		// Настройки по умполнчанию для заявления о выходе из КУч ПО
		statementExcludeTemplateCode: "KUCH_STATEMENT_EXCLUDE_TEMPLATE_CODE",
		statementExcludeCommunityParticipantName: "Кооперативный участок",
		statementExcludeOrganizationParticipantName: "Кандидат на выход из КУч ПО",

		// Настройки по умполнчанию для протокола на выход юр лиц из КУч ПО
		protocolExcludeTemplateCode: "KUCH_PROTOCOL_EXCLUDE_TEMPLATE_CODE",
		protocolExcludeCommunityParticipantName: "Кооперативный участок",
		protocolExcludeOrganizationsParticipantName: "Список юр лиц",
		protocolExcludeOrganizationsDocumentsUserField: "Заявления юр лиц"
	}', 'Настройки для пайщиков юр лиц в КУч ПО'
	where not exists (select id from system_settings where key = 'KuchOrganizationMemberSettings');
end $$;

-- //@UNDO
-- SQL to undo the change goes here.


