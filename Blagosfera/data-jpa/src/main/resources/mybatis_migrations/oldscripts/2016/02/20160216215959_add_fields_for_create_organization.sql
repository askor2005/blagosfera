-- // add_fields_for_create_organization
-- Migration SQL that makes the change goes here.

do $$
declare
  fieldGroupId bigint;
begin
	select id into fieldGroupId from fields_groups where internal_name = 'PERSON_COMMON';

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), true, 'PERSON_INDEX', 'Порядковый номер участника', 13, fieldGroupId, 19, null, null, true, false, 0, false, false, null, null
	where not exists (select id from fields where internal_name = 'PERSON_INDEX');



	select id into fieldGroupId from fields_groups where internal_name = 'COMMUNITY_WITH_ORGANIZATION_LEGAL_ADDRESS';

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), true, 'COMMUNITY_LEGAL_REGION_CODE', 'Код региона', 300, fieldGroupId, 29, null, null, true, false, 0, false, false, null, null
	where not exists (select id from fields where internal_name = 'COMMUNITY_LEGAL_REGION_CODE');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), true, 'COMMUNITY_LEGAL_STREET_DESCRIPTION_SHORT', 'Сокращённое описание улицы', 301, fieldGroupId, 29, null, null, true, false, 0, false, false, null, null
	where not exists (select id from fields where internal_name = 'COMMUNITY_LEGAL_STREET_DESCRIPTION_SHORT');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), true, 'COMMUNITY_LEGAL_DISTRICT_DESCRIPTION_SHORT', 'Сокращённое описание района', 302, fieldGroupId, 29, null, null, true, false, 0, false, false, null, null
	where not exists (select id from fields where internal_name = 'COMMUNITY_LEGAL_DISTRICT_DESCRIPTION_SHORT');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), true, 'COMMUNITY_LEGAL_CITY_DESCRIPTION_SHORT', 'Сокращённое описание населённого пункта', 303, fieldGroupId, 29, null, null, true, false, 0, false, false, null, null
	where not exists (select id from fields where internal_name = 'COMMUNITY_LEGAL_CITY_DESCRIPTION_SHORT');



	select id into fieldGroupId from fields_groups where internal_name = 'COMMUNITY_WITH_ORGANIZATION_LEGAL_F_ADDRESS';

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), true, 'COMMUNITY_LEGAL_F_REGION_CODE', 'Код региона', 300, fieldGroupId, 29, null, null, true, false, 0, false, false, null, null
	where not exists (select id from fields where internal_name = 'COMMUNITY_LEGAL_F_REGION_CODE');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), true, 'COMMUNITY_LEGAL_F_STREET_DESCRIPTION_SHORT', 'Сокращённое описание улицы', 301, fieldGroupId, 29, null, null, true, false, 0, false, false, null, null
	where not exists (select id from fields where internal_name = 'COMMUNITY_LEGAL_F_STREET_DESCRIPTION_SHORT');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), true, 'COMMUNITY_LEGAL_F_DISTRICT_DESCRIPTION_SHORT', 'Сокращённое описание района', 302, fieldGroupId, 29, null, null, true, false, 0, false, false, null, null
	where not exists (select id from fields where internal_name = 'COMMUNITY_LEGAL_F_DISTRICT_DESCRIPTION_SHORT');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), true, 'COMMUNITY_LEGAL_F_CITY_DESCRIPTION_SHORT', 'Сокращённое описание населённого пункта', 303, fieldGroupId, 29, null, null, true, false, 0, false, false, null, null
	where not exists (select id from fields where internal_name = 'COMMUNITY_LEGAL_F_CITY_DESCRIPTION_SHORT');





	select id into fieldGroupId from fields_groups where internal_name = 'PERSON_REGISTRATION_ADDRESS';

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), true, 'REGION_CODE', 'Код региона', 300, fieldGroupId, 29, null, null, true, false, 0, false, false, null, null
	where not exists (select id from fields where internal_name = 'REGION_CODE');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), true, 'STREET_DESCRIPTION_SHORT', 'Сокращённое описание улицы', 301, fieldGroupId, 29, null, null, true, false, 0, false, false, null, null
	where not exists (select id from fields where internal_name = 'STREET_DESCRIPTION_SHORT');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), true, 'DISTRICT_DESCRIPTION_SHORT', 'Сокращённое описание района', 302, fieldGroupId, 29, null, null, true, false, 0, false, false, null, null
	where not exists (select id from fields where internal_name = 'DISTRICT_DESCRIPTION_SHORT');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), true, 'CITY_DESCRIPTION_SHORT', 'Сокращённое описание населённого пункта', 303, fieldGroupId, 29, null, null, true, false, 0, false, false, null, null
	where not exists (select id from fields where internal_name = 'CITY_DESCRIPTION_SHORT');




	select id into fieldGroupId from fields_groups where internal_name = 'PERSON_ACTUAL_ADDRESS';

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), true, 'FREGION_CODE', 'Код региона', 300, fieldGroupId, 29, null, null, true, false, 0, false, false, null, null
	where not exists (select id from fields where internal_name = 'FREGION_CODE');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), true, 'FSTREET_DESCRIPTION_SHORT', 'Сокращённое описание улицы', 301, fieldGroupId, 29, null, null, true, false, 0, false, false, null, null
	where not exists (select id from fields where internal_name = 'FSTREET_DESCRIPTION_SHORT');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), true, 'FDISTRICT_DESCRIPTION_SHORT', 'Сокращённое описание района', 302, fieldGroupId, 29, null, null, true, false, 0, false, false, null, null
	where not exists (select id from fields where internal_name = 'FDISTRICT_DESCRIPTION_SHORT');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), true, 'FCITY_DESCRIPTION_SHORT', 'Сокращённое описание населённого пункта', 303, fieldGroupId, 29, null, null, true, false, 0, false, false, null, null
	where not exists (select id from fields where internal_name = 'FCITY_DESCRIPTION_SHORT');



	select id into fieldGroupId from fields_groups where internal_name = 'REGISTRATOR_OFFICE_ADDRESS';

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), true, 'REGISTRATOR_OFFICE_REGION_CODE', 'Код региона', 300, fieldGroupId, 29, null, null, true, false, 0, false, false, null, null
	where not exists (select id from fields where internal_name = 'REGISTRATOR_OFFICE_REGION_CODE');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), true, 'REGISTRATOR_OFFICE_STREET_DESCRIPTION_SHORT', 'Сокращённое описание улицы', 301, fieldGroupId, 29, null, null, true, false, 0, false, false, null, null
	where not exists (select id from fields where internal_name = 'REGISTRATOR_OFFICE_STREET_DESCRIPTION_SHORT');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), true, 'REGISTRATOR_OFFICE_DISTRICT_DESCRIPTION_SHORT', 'Сокращённое описание района', 302, fieldGroupId, 29, null, null, true, false, 0, false, false, null, null
	where not exists (select id from fields where internal_name = 'REGISTRATOR_OFFICE_DISTRICT_DESCRIPTION_SHORT');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), true, 'REGISTRATOR_OFFICE_CITY_DESCRIPTION_SHORT', 'Сокращённое описание населённого пункта', 303, fieldGroupId, 29, null, null, true, false, 0, false, false, null, null
	where not exists (select id from fields where internal_name = 'REGISTRATOR_OFFICE_CITY_DESCRIPTION_SHORT');


	select id into fieldGroupId from fields_groups where internal_name = 'COMMUNITY_WITH_ORGANIZATION_OKVEDS';

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), true, 'COMMUNITY_OKVED_CODES', 'Коды через запятую', 1, fieldGroupId, 19, null, null, true, false, 0, false, false, null, null
	where not exists (select id from fields where internal_name = 'COMMUNITY_OKVED_CODES');
end $$;

-- //@UNDO
-- SQL to undo the change goes here.


