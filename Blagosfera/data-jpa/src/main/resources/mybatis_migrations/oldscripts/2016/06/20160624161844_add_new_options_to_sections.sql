-- // add_new_options_to_sections
-- Migration SQL that makes the change goes here.
do $$
begin
alter table sections add column disabled boolean not null default false;
alter table sections add column  show_to_verified_users_only boolean not null default false;
alter table sections add column  show_to_admin_users_only boolean not null default false;
alter table sections add column  min_registrator_level_to_show integer;
end $$;




-- //@UNDO
-- SQL to undo the change goes here.
do $$
begin
alter table sections drop column disabled;
alter table sections drop column  show_to_verified_users_only;
alter table sections drop column  show_to_admin_users_only;
alter table sections drop column  min_registrator_level_to_show;
end $$;


