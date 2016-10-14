-- // update_community_fields
-- Migration SQL that makes the change goes here.

do $$
  declare
  sectionId bigint;
  begin
    select id into sectionId from community_sections where name = 'COMMON';

    insert into community_sections (id, link, name, permission, position, title, parent_id, is_guest_access)
    select nextval('seq_community_sections'), '/info', 'COMMON_FULL_INFO', null, 4, 'Подробная информация', sectionId, false
    where not exists(select id from community_sections where name = 'COMMON_FULL_INFO');

    update community_sections set title = 'Редактировать' where name = 'SETTINGS_COMMON';

    update fields set type = 24 where internal_name = 'COMMUNITY_BRIEF_DESCRIPTION';

    update fields set type = 27 where internal_name = 'COMMUNITY_DIRECTOR_POSITION';

  end;
$$;

-- //@UNDO
-- SQL to undo the change goes here.


