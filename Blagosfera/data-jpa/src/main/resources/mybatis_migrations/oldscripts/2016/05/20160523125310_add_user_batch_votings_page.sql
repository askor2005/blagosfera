-- // add_user_batch_votings_page
-- Migration SQL that makes the change goes here.

do $$
declare
  sectionId bigint;
begin
  select id into sectionId from sections where name = 'radom';

  insert into sections(id, link, name, position, title, parent_id, hint, icon, published, page_id, help_link, application_id, image_url, type, access_type, can_set_forward_url, forward_url)
  select nextval('seq_sections'), null, 'uservotings', 9, 'Волеизъявления', sectionId, null, null, true, null, null, null, null, 0, null, null, null
  where not exists(select id from sections where name = 'uservotings');

  select id into sectionId from sections where name = 'uservotings';

  insert into sections(id, link, name, position, title, parent_id, hint, icon, published, page_id, help_link, application_id, image_url, type, access_type, can_set_forward_url, forward_url)
  select nextval('seq_sections'), '/uservotings/batchvotings', 'userbatchvotings', 0, 'Собрания с моим участием', sectionId, null, null, true, null, null, null, null, 0, null, null, null
  where not exists(select id from sections where name = 'userbatchvotings');
end $$

-- //@UNDO
-- SQL to undo the change goes here.


