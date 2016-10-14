-- // add_system_images
-- Migration SQL that makes the change goes here.

do $$
declare
  fieldGroupId bigint;
begin
	select id into fieldGroupId from fields_groups where internal_name = 'COMMUNITY_COMMON';

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), true, 'COMMUNITY_AVATAR', 'Картинка объединения', 14, fieldGroupId, 30, null, null, true, false, 0, false, false, null, null
	where not exists (select id from fields where internal_name = 'COMMUNITY_AVATAR');

	select id into fieldGroupId from fields_groups where internal_name = 'PERSON_COMMON';

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), true, 'PERSON_AVATAR', 'Картинка участника', 14, fieldGroupId, 30, null, null, true, false, 0, false, false, null, null
	where not exists (select id from fields where internal_name = 'PERSON_AVATAR');
end $$;

-- //@UNDO
-- SQL to undo the change goes here.


