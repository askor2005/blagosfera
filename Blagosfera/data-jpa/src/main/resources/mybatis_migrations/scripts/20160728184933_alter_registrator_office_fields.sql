-- // alter_registrator_office_fields
-- Migration SQL that makes the change goes here.

do $$
declare
	fieldGroupId bigint;
begin
	select id into fieldGroupId from fields_groups where internal_name = 'REGISTRATOR_OFFICE_ADDRESS';
	update fields set hideable = false where fields_group_id = fieldGroupId;
	update field_values set hidden = false where field_id in (select id from fields where fields_group_id = fieldGroupId);
end $$;

-- //@UNDO
-- SQL to undo the change goes here.


