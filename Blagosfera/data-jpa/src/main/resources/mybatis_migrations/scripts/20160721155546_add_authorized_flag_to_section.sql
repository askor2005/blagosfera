-- // add_authorized_flag_to_section
-- Migration SQL that makes the change goes here.

alter table sections add column show_to_authorized_users_only boolean not null default false; 

-- //@UNDO
-- SQL to undo the change goes here.


