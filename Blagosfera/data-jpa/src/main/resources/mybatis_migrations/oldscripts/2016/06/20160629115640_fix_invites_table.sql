-- // fix_invites_table
-- Migration SQL that makes the change goes here.

update invites i
set
invited_father_name = (select string_value from field_values where object_id = i.invited_sharer_id and field_id = (select id from fields where internal_name = 'SECONDNAME')),
invited_first_name = (select string_value from field_values where object_id = i.invited_sharer_id and field_id = (select id from fields where internal_name = 'FIRSTNAME')),
invited_last_name = (select string_value from field_values where object_id = i.invited_sharer_id and field_id = (select id from fields where internal_name = 'LASTNAME')),
invited_gender = (select substr(string_value, 1, 1) from field_values where object_id = i.invited_sharer_id and field_id = (select id from fields where internal_name = 'GENDER')),
hash_url = (select string_value from field_values where object_id = i.invited_sharer_id and field_id = (select id from fields where internal_name = 'FIRSTNAME'))
where
i.hash_url = '' and i.invited_sharer_id is not null;

-- //@UNDO
-- SQL to undo the change goes here.


