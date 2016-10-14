-- // reallocate_community_fields
-- Migration SQL that makes the change goes here.

do $$
  declare
  groupId bigint;
  begin

  insert into fields_groups (id, internal_name, name, position, object_type)
  select nextval('seq_fields_groups'), 'COMMUNITY_WITH_ORGANIZATION_COMMON_DATA', 'Общие данные юр лица', 400, 'COMMUNITY'
  where not exists (select id from fields_groups where internal_name = 'COMMUNITY_WITH_ORGANIZATION_COMMON_DATA');

  select id into groupId from fields_groups where internal_name = 'COMMUNITY_WITH_ORGANIZATION_COMMON_DATA';

  update fields
  set
  fields_group_id = groupId
  where internal_name in (
  'COMMUNITY_INN',
  'COMMUNITY_PFR',
  'COMMUNITY_STAT',
  'COMMUNITY_FOMS',
  'COMMUNITY_EGRUL',
  'COMMUNITY_TAXATION_SYSTEM'
  );

  select id into groupId from fields_groups where internal_name = 'COMMUNITY_WITHOUT_ORGANIZATION_GEOGRAPHICAL_POSITION';

  insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
  select nextval('seq_fields'), true, 'COMMUNITY_COUNTRY', 'Страна', 0, groupId, 3, '', '', false, false, 1, false, false, false, false
  where not exists (select id from fields where internal_name = 'COMMUNITY_COUNTRY');

  insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
  select nextval('seq_fields'), true, 'COMMUNITY_POST_CODE', 'Индекс', 1, groupId, 0, '', '', false, false, 1, false, false, false, false
  where not exists (select id from fields where internal_name = 'COMMUNITY_POST_CODE');

  insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
  select nextval('seq_fields'), true, 'COMMUNITY_OFFICE', 'Офис', 9, groupId, 0, '', '', false, false, 1, false, false, false, false
  where not exists (select id from fields where internal_name = 'COMMUNITY_OFFICE');

  end;
$$;

-- //@UNDO
-- SQL to undo the change goes here.


