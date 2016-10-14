-- // add_community_files
-- Migration SQL that makes the change goes here.

do $$
declare
  sectionId bigint;
begin
	select id into sectionId from community_sections where name = 'DOCUMENTS';

	insert into community_sections (id, link, name, permission, position, title, parent_id, is_guest_access)
	select nextval('seq_community_sections'), '/files', 'COMMUNITY_FILES', null, 4, 'Файлы объединения', sectionId, false
	where not exists (select id from community_sections where name = 'COMMUNITY_FILES');
end $$;

-- //@UNDO
-- SQL to undo the change goes here.


