-- // delete_old_fields
-- Migration SQL that makes the change goes here.

delete from field_values where field_id in (select id from fields where fields_group_id in (select id from fields_groups where internal_name = 'COMMUNITY_ASSOCIATION_FORMS'));
delete from fields where fields_group_id in (select id from fields_groups where internal_name = 'COMMUNITY_ASSOCIATION_FORMS');
delete from fields_groups where internal_name = 'COMMUNITY_ASSOCIATION_FORMS';

-- //@UNDO
-- SQL to undo the change goes here.


