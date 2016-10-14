-- // add_association_form_value_field
-- Migration SQL that makes the change goes here.

do $$
declare
  fieldGroupId bigint;
begin
	select id into fieldGroupId from fields_groups where internal_name = 'COMMUNITY_COMMON';

	insert into fields(id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), true, 'COMMUNITY_ASSOCIATION_FORM_NAME', 'Форма объединения', 0, fieldGroupId, 19, null, null, false, false, 0, false, false, true, false
	where not exists (select 1 from fields where internal_name = 'COMMUNITY_ASSOCIATION_FORM_NAME');
end $$;

-- //@UNDO
-- SQL to undo the change goes here.


