-- // add_community_additional_fields
-- Migration SQL that makes the change goes here.

do $$
declare
  fieldGroupId bigint;
  associationFormId bigint;
  taxationSystemId bigint;
begin
	insert into list_editor (id, form_name, listeditortype, name)
	select nextval('seq_list_editor'), 'COMMUNITY_TAXATION_SYSTEM', 0, 'COMMUNITY_TAXATION_SYSTEM'
	where not exists(select id from list_editor where name = 'COMMUNITY_TAXATION_SYSTEM');

	select id into taxationSystemId from list_editor where name = 'COMMUNITY_TAXATION_SYSTEM';

	insert into list_editor_item (id, is_active, text, list_editor, is_selected_item, listeditoritemtype, item_order, mnemo_code)
	select nextval('seq_list_editor_item'), false, 'Простая Система Налогообложения', taxationSystemId, false, 0, 0, 'simple_taxation_system'
	where not exists (select id from list_editor_item where mnemo_code = 'simple_taxation_system');

	insert into list_editor_item (id, is_active, text, list_editor, is_selected_item, listeditoritemtype, item_order, mnemo_code)
	select nextval('seq_list_editor_item'), false, 'Упрощённая Система Налогообложения', taxationSystemId, false, 0, 1, 'usn_taxation_system'
	where not exists (select id from list_editor_item where mnemo_code = 'usn_taxation_system');

	insert into list_editor_item (id, is_active, text, list_editor, is_selected_item, listeditoritemtype, item_order, mnemo_code)
	select nextval('seq_list_editor_item'), false, 'Единый Налог на Вменённый доход', taxationSystemId, false, 0, 2, 'envd_taxation_system'
	where not exists (select id from list_editor_item where mnemo_code = 'envd_taxation_system');

	select id into fieldGroupId from fields_groups where internal_name = 'COMMUNITY_COMMON';

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), false, 'COMMUNITY_TAXATION_SYSTEM', 'Система Налогообложения', 13, fieldGroupId, 27, null, null, false, false, 0, false, false, null, null
	where not exists (select id from fields where internal_name = 'COMMUNITY_TAXATION_SYSTEM');


	insert into fields_groups (id, internal_name, name, position, object_type)
	select nextval('seq_fields_groups'), 'COMMUNITY_WITH_ORGANIZATION_OKVEDS', 'Виды деятельности', 701, 'COMMUNITY'
	where not exists(select id from fields_groups where internal_name = 'COMMUNITY_WITH_ORGANIZATION_OKVEDS');

	select id into fieldGroupId from fields_groups where internal_name = 'COMMUNITY_WITH_ORGANIZATION_OKVEDS';

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), true, 'COMMUNITY_FULL_OKVEDS', 'Виды деятельности с кодами', 0, fieldGroupId, 19, null, null, true, false, 0, false, false, null, null
	where not exists (select id from fields where internal_name = 'COMMUNITY_FULL_OKVEDS');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), true, 'COMMUNITY_SHORT_OKVEDS', 'Виды деятельности без кодов', 0, fieldGroupId, 19, null, null, true, false, 0, false, false, null, null
	where not exists (select id from fields where internal_name = 'COMMUNITY_SHORT_OKVEDS');

	insert into fields_groups (id, internal_name, name, position, object_type)
	select nextval('seq_fields_groups'), 'COMMUNITY_ADDITIONAL_GROUP_REVISOR_CONSUMER_SOCIETY', 'Ревизионная комиссия потребительского общества', 413, 'COMMUNITY'
	where not exists(select id from fields_groups where internal_name = 'COMMUNITY_ADDITIONAL_GROUP_REVISOR_CONSUMER_SOCIETY');

	select id into fieldGroupId from fields_groups where internal_name = 'COMMUNITY_ADDITIONAL_GROUP_REVISOR_CONSUMER_SOCIETY';

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), true, 'COMMUNITY_CHAIRMAN_REVISOR_COMMITTEE', 'Председатель ревизионной комиссии', 1, fieldGroupId, 18, null, 'Начните вводить имя председателя ревизионной комиссии', true, false, 1, false, false, null, null
	where not exists (select id from fields where internal_name = 'COMMUNITY_CHAIRMAN_REVISOR_COMMITTEE');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), true, 'COMMUNITY_CHAIRMAN_REVISOR_COMMITTEE_ID', 'Председатель ревизионной комиссии', 1, fieldGroupId, 0, null, null, true, false, 1, false, false, null, null
	where not exists (select id from fields where internal_name = 'COMMUNITY_CHAIRMAN_REVISOR_COMMITTEE_ID');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), true, 'COMMUNITY_MEMBERS_REVISOR_COMMITTEE', 'Члены ревизионной комиссии', 2, fieldGroupId, 20, null, 'Начните вводить имя члена совета', true, false, 1, false, false, null, null
	where not exists (select id from fields where internal_name = 'COMMUNITY_MEMBERS_REVISOR_COMMITTEE');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), true, 'COMMUNITY_PROTOCOL_MEMBERS_REVISOR_COMMITTEE', 'Протокол общего собрания ПО по выборам ревизионной комиссии', 3, fieldGroupId, 0, null, null, true, false, 1, false, false, null, null
	where not exists (select id from fields where internal_name = 'COMMUNITY_PROTOCOL_MEMBERS_REVISOR_COMMITTEE');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), true, 'COMMUNITY_BEGIN_DATE_MEMBERS_REVISOR_COMMITTEE', 'Дата начала полномочий членов ревизионной комиссии', 4, fieldGroupId, 1, null, null, true, false, 1, false, false, null, null
	where not exists (select id from fields where internal_name = 'COMMUNITY_BEGIN_DATE_MEMBERS_REVISOR_COMMITTEE');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), true, 'COMMUNITY_END_DATE_MEMBERS_REVISOR_COMMITTEE', 'Дата окончания полномочий членов ревизионной комиссии', 5, fieldGroupId, 1, null, null, true, false, 1, false, false, null, null
	where not exists (select id from fields where internal_name = 'COMMUNITY_END_DATE_MEMBERS_REVISOR_COMMITTEE');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), true, 'COMMUNITY_PROTOCOL_CHAIRMAN_REVISOR', 'Протокол собрания Совета ПО по выборам Председателя ревизионной комиссии', 6, fieldGroupId, 0, null, null, true, false, 1, false, false, null, null
	where not exists (select id from fields where internal_name = 'COMMUNITY_PROTOCOL_CHAIRMAN_REVISOR');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), true, 'COMMUNITY_BEGIN_DATE_REVISOR', 'Дата начала полномочий Председателя ревизионной комиссии', 7, fieldGroupId, 1, null, null, true, false, 1, false, false, null, null
	where not exists (select id from fields where internal_name = 'COMMUNITY_BEGIN_DATE_REVISOR');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), true, 'COMMUNITY_END_DATE_REVISOR', 'Дата окончания полномочий Председателя ревизионной комиссии', 8, fieldGroupId, 1, null, null, true, false, 1, false, false, null, null
	where not exists (select id from fields where internal_name = 'COMMUNITY_END_DATE_REVISOR');


	select id into associationFormId from list_editor_item where mnemo_code = 'community_cooperative_society';

	insert into fields_groups_association_forms(association_form_id, fields_group_id)
	select associationFormId, fieldGroupId
	where not exists(select 1 from fields_groups_association_forms where fields_group_id = fieldGroupId and association_form_id = associationFormId);
end $$;

-- //@UNDO
-- SQL to undo the change goes here.


