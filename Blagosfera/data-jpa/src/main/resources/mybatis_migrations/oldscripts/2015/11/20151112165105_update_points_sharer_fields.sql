-- // update_points_sharer_fields
-- Migration SQL that makes the change goes here.

update fields set points = 0 where fields_group_id in (select id from fields_groups where internal_name in ('SHARER_PROFILE_SETTINGS', 'BLAGOSFERA'));

-- //@UNDO
-- SQL to undo the change goes here.


