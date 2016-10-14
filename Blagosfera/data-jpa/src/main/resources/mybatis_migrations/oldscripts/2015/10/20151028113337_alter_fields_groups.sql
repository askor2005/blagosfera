-- // alter_fields_groups
-- Migration SQL that makes the change goes here.

do $$
declare groupId bigint;
declare poAssociationFormId bigint;
declare kuchAssociationFormId bigint;
begin
	-- Создаём таблицу с формами объединения для групп полей
	CREATE TABLE fields_groups_association_forms
	(
	  fields_group_id bigint NOT NULL,
	  association_form_id bigint NOT NULL,
	  CONSTRAINT fields_groups_association_forms_pkey PRIMARY KEY (fields_group_id, association_form_id),
	  CONSTRAINT fk_6hwkffw9fhke7v5us9nro36e4 FOREIGN KEY (fields_group_id)
	      REFERENCES fields_groups (id) MATCH SIMPLE
	      ON UPDATE NO ACTION ON DELETE NO ACTION,
	  CONSTRAINT fk_b24outljkglik5yx29cfbetts FOREIGN KEY (association_form_id)
	      REFERENCES list_editor_item (id) MATCH SIMPLE
	      ON UPDATE NO ACTION ON DELETE NO ACTION
	)
	WITH (
	  OIDS=FALSE
	);
	ALTER TABLE fields_groups_association_forms
	  OWNER TO kabinet;


	select id into kuchAssociationFormId from list_editor_item where mnemo_code = 'cooperative_plot';
	select id into poAssociationFormId from list_editor_item where mnemo_code = 'community_cooperative_society';

	-- Паевые взносы
	select id into groupId from fields_groups where internal_name = 'COMMUNITY_ADDITIONAL_GROUP_SHARE_AND_MEMBERSHIP_FEES';
	insert into fields_groups_association_forms (fields_group_id, association_form_id) values (groupId, poAssociationFormId);
	insert into fields_groups_association_forms (fields_group_id, association_form_id) values (groupId, kuchAssociationFormId);

	-- Совет потребительского общества
	select id into groupId from fields_groups where internal_name = 'COMMUNITY_ADDITIONAL_GROUP_TIP_OF_THE_CONSUMER_SOCIETY';
	insert into fields_groups_association_forms (fields_group_id, association_form_id) values (groupId, poAssociationFormId);

	--Правление потребительского общества
	select id into groupId from fields_groups where internal_name = 'COMMUNITY_ADDITIONAL_GROUP_BOARD_CONSUMER_SOCIETY';
	insert into fields_groups_association_forms (fields_group_id, association_form_id) values (groupId, poAssociationFormId);

	--Руководство Кооперативного Участка
	select id into groupId from fields_groups where internal_name = 'COMMUNITY_ADDITIONAL_GROUP_COOPERATIVE_PLOT_MANAGERS';
	insert into fields_groups_association_forms (fields_group_id, association_form_id) values (groupId, kuchAssociationFormId);

	-- Удаляем устаревшее поле
	ALTER TABLE fields_groups DROP COLUMN list_editor_item_id RESTRICT;
end $$;


-- //@UNDO
-- SQL to undo the change goes here.


