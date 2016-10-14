-- // add data section
-- Migration SQL that makes the change goes here.

-- Создание раздела "Данные" в админке
do $$
begin

  insert into sections (id, link, name, position, title, parent_id, hint, icon, published, page_id, help_link, application_id, image_url, type)
  select nextval('seq_sections'), '/admin/data', 'adminData', 9, 'Данные', 677, null, null, true, null, null, null, null, 2
  where not exists(select id from sections where link = '/admin/data');

end $$;

-- //@UNDO
-- SQL to undo the change goes here.


