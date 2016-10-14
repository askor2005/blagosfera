-- // add_open_in_new_link_column_to_section
-- Migration SQL that makes the change goes here.

alter table sections add column open_in_new_link boolean not null default false;

-- //@UNDO
-- SQL to undo the change goes here.


