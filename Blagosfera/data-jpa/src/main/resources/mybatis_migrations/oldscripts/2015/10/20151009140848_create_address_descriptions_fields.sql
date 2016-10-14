-- // create_address_descriptions_fields
-- Migration SQL that makes the change goes here.

-- Описание адресных полей

do $$
declare
groupid bigint;
begin
	select id into groupid from fields_groups where internal_name =  'COMMUNITY_WITH_ORGANIZATION_LEGAL_ADDRESS';

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), false, 'COMMUNITY_LEGAL_REGION_DESCRIPTION', 'Описание региона', 0, groupid, 26, null, null, false, false, 0, false, false, false, false
	where not exists(select 1 from fields where fields_group_id = groupid and internal_name = 'COMMUNITY_LEGAL_REGION_DESCRIPTION');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), false, 'COMMUNITY_LEGAL_AREA_DESCRIPTION', 'Описание райна', 0, groupid, 26, null, null, false, false, 0, false, false, false, false
	where not exists(select 1 from fields where fields_group_id = groupid and internal_name = 'COMMUNITY_LEGAL_AREA_DESCRIPTION');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), false, 'COMMUNITY_LEGAL_LOCALITY_DESCRIPTION', 'Описание населённого пункта', 0, groupid, 26, null, null, false, false, 0, false, false, false, false
	where not exists(select 1 from fields where fields_group_id = groupid and internal_name = 'COMMUNITY_LEGAL_LOCALITY_DESCRIPTION');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), false, 'COMMUNITY_LEGAL_STREET_DESCRIPTION', 'Описание улицы', 0, groupid, 26, null, null, false, false, 0, false, false, false, false
	where not exists(select 1 from fields where fields_group_id = groupid and internal_name = 'COMMUNITY_LEGAL_STREET_DESCRIPTION');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), false, 'COMMUNITY_LEGAL_HOUSE_DESCRIPTION', 'Описание строения', 0, groupid, 26, null, null, false, false, 0, false, false, false, false
	where not exists(select 1 from fields where fields_group_id = groupid and internal_name = 'COMMUNITY_LEGAL_HOUSE_DESCRIPTION');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), false, 'COMMUNITY_LEGAL_OFFICE_DESCRIPTION', 'Описание офиса', 0, groupid, 26, null, null, false, false, 0, false, false, false, false
	where not exists(select 1 from fields where fields_group_id = groupid and internal_name = 'COMMUNITY_LEGAL_OFFICE_DESCRIPTION');

	--///////////////////////////

	select id into groupid from fields_groups where internal_name =  'COMMUNITY_WITH_ORGANIZATION_LEGAL_F_ADDRESS';

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), false, 'COMMUNITY_LEGAL_F_REGION_DESCRIPTION', 'Описание региона', 0, groupid, 26, null, null, false, false, 0, false, false, false, false
	where not exists(select 1 from fields where fields_group_id = groupid and internal_name = 'COMMUNITY_LEGAL_F_REGION_DESCRIPTION');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), false, 'COMMUNITY_LEGAL_F_AREA_DESCRIPTION', 'Описание райна', 0, groupid, 26, null, null, false, false, 0, false, false, false, false
	where not exists(select 1 from fields where fields_group_id = groupid and internal_name = 'COMMUNITY_LEGAL_F_AREA_DESCRIPTION');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), false, 'COMMUNITY_LEGAL_F_LOCALITY_DESCRIPTION', 'Описание населённого пункта', 0, groupid, 26, null, null, false, false, 0, false, false, false, false
	where not exists(select 1 from fields where fields_group_id = groupid and internal_name = 'COMMUNITY_LEGAL_F_LOCALITY_DESCRIPTION');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), false, 'COMMUNITY_LEGAL_F_STREET_DESCRIPTION', 'Описание улицы', 0, groupid, 26, null, null, false, false, 0, false, false, false, false
	where not exists(select 1 from fields where fields_group_id = groupid and internal_name = 'COMMUNITY_LEGAL_F_STREET_DESCRIPTION');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), false, 'COMMUNITY_LEGAL_F_HOUSE_DESCRIPTION', 'Описание строения', 0, groupid, 26, null, null, false, false, 0, false, false, false, false
	where not exists(select 1 from fields where fields_group_id = groupid and internal_name = 'COMMUNITY_LEGAL_F_HOUSE_DESCRIPTION');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), false, 'COMMUNITY_LEGAL_F_OFFICE_DESCRIPTION', 'Описание офиса', 0, groupid, 26, null, null, false, false, 0, false, false, false, false
	where not exists(select 1 from fields where fields_group_id = groupid and internal_name = 'COMMUNITY_LEGAL_F_OFFICE_DESCRIPTION');

	--///////////////////////////

	select id into groupid from fields_groups where internal_name =  'REGISTRATOR_OFFICE_ADDRESS';

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), false, 'REGISTRATOR_OFFICE_REGION_DESCRIPTION', 'Описание региона', 0, groupid, 26, null, null, false, false, 0, false, false, false, false
	where not exists(select 1 from fields where fields_group_id = groupid and internal_name = 'REGISTRATOR_OFFICE_REGION_DESCRIPTION');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), false, 'REGISTRATOR_OFFICE_DISTRICT_DESCRIPTION', 'Описание райна', 0, groupid, 26, null, null, false, false, 0, false, false, false, false
	where not exists(select 1 from fields where fields_group_id = groupid and internal_name = 'REGISTRATOR_OFFICE_DISTRICT_DESCRIPTION');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), false, 'REGISTRATOR_OFFICE_CITY_DESCRIPTION', 'Описание населённого пункта', 0, groupid, 26, null, null, false, false, 0, false, false, false, false
	where not exists(select 1 from fields where fields_group_id = groupid and internal_name = 'REGISTRATOR_OFFICE_CITY_DESCRIPTION');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), false, 'REGISTRATOR_OFFICE_STREET_DESCRIPTION', 'Описание улицы', 0, groupid, 26, null, null, false, false, 0, false, false, false, false
	where not exists(select 1 from fields where fields_group_id = groupid and internal_name = 'REGISTRATOR_OFFICE_STREET_DESCRIPTION');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), false, 'REGISTRATOR_OFFICE_BUILDING_DESCRIPTION', 'Описание строения', 0, groupid, 26, null, null, false, false, 0, false, false, false, false
	where not exists(select 1 from fields where fields_group_id = groupid and internal_name = 'REGISTRATOR_OFFICE_BUILDING_DESCRIPTION');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), false, 'REGISTRATOR_OFFICE_ROOM_DESCRIPTION', 'Описание офиса', 0, groupid, 26, null, null, false, false, 0, false, false, false, false
	where not exists(select 1 from fields where fields_group_id = groupid and internal_name = 'REGISTRATOR_OFFICE_ROOM_DESCRIPTION');

	--///////////////////////////

	select id into groupid from fields_groups where internal_name =  'PERSON_REGISTRATION_ADDRESS';

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), false, 'REGION_RL_DESCRIPTION', 'Описание региона', 0, groupid, 26, null, null, false, false, 0, false, false, false, false
	where not exists(select 1 from fields where fields_group_id = groupid and internal_name = 'REGION_RL_DESCRIPTION');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), false, 'AREA_AL_DESCRIPTION', 'Описание райна', 0, groupid, 26, null, null, false, false, 0, false, false, false, false
	where not exists(select 1 from fields where fields_group_id = groupid and internal_name = 'AREA_AL_DESCRIPTION');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), false, 'CITY_TL_DESCRIPTION', 'Описание населённого пункта', 0, groupid, 26, null, null, false, false, 0, false, false, false, false
	where not exists(select 1 from fields where fields_group_id = groupid and internal_name = 'CITY_TL_DESCRIPTION');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), false, 'STREET_DESCRIPTION', 'Описание улицы', 0, groupid, 26, null, null, false, false, 0, false, false, false, false
	where not exists(select 1 from fields where fields_group_id = groupid and internal_name = 'STREET_DESCRIPTION');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), false, 'HOUSE_DESCRIPTION', 'Описание строения', 0, groupid, 26, null, null, false, false, 0, false, false, false, false
	where not exists(select 1 from fields where fields_group_id = groupid and internal_name = 'HOUSE_DESCRIPTION');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), false, 'ROOM_DESCRIPTION', 'Описание офиса', 0, groupid, 26, null, null, false, false, 0, false, false, false, false
	where not exists(select 1 from fields where fields_group_id = groupid and internal_name = 'ROOM_DESCRIPTION');

	--///////////////////////////

	select id into groupid from fields_groups where internal_name =  'PERSON_ACTUAL_ADDRESS';

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), false, 'FREGION_RL_DESCRIPTION', 'Описание региона', 0, groupid, 26, null, null, false, false, 0, false, false, false, false
	where not exists(select 1 from fields where fields_group_id = groupid and internal_name = 'FREGION_RL_DESCRIPTION');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), false, 'FAREA_AL_DESCRIPTION', 'Описание райна', 0, groupid, 26, null, null, false, false, 0, false, false, false, false
	where not exists(select 1 from fields where fields_group_id = groupid and internal_name = 'FAREA_AL_DESCRIPTION');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), false, 'FCITY_TL_DESCRIPTION', 'Описание населённого пункта', 0, groupid, 26, null, null, false, false, 0, false, false, false, false
	where not exists(select 1 from fields where fields_group_id = groupid and internal_name = 'FCITY_TL_DESCRIPTION');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), false, 'FSTREET_DESCRIPTION', 'Описание улицы', 0, groupid, 26, null, null, false, false, 0, false, false, false, false
	where not exists(select 1 from fields where fields_group_id = groupid and internal_name = 'FSTREET_DESCRIPTION');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), false, 'FHOUSE_DESCRIPTION', 'Описание строения', 0, groupid, 26, null, null, false, false, 0, false, false, false, false
	where not exists(select 1 from fields where fields_group_id = groupid and internal_name = 'FHOUSE_DESCRIPTION');

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), false, 'FROOM_DESCRIPTION', 'Описание офиса', 0, groupid, 26, null, null, false, false, 0, false, false, false, false
	where not exists(select 1 from fields where fields_group_id = groupid and internal_name = 'FROOM_DESCRIPTION');


end $$;

-- //@UNDO
-- SQL to undo the change goes here.


