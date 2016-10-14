-- // patch_release_ver_1
-- Migration SQL that makes the change goes here.

-- Патч БД прода для первой стабильной версии
do $$
begin

  -- Обновляем типы полей для платежей в ПО на денежный тип
  update fields set type = 25 where fields_group_id = 49;

  -- Изменяем длину поля наименования документа в шаблоне документов
  ALTER TABLE documents_templates ALTER COLUMN document_name TYPE character varying(10000);

  -- Удаляем все устарвешиые записии из таблицы с участниками класса документа
  --delete from documents_types_participants where parent_id is not null;

  -- Увеличиваем длину поля для хранения ссылок документов в field
  ALTER TABLE field_values ALTER COLUMN file_url TYPE character varying(10000);

end $$;

DO $$
DECLARE
row_data field_values%ROWTYPE;
BEGIN

  FOR row_data IN select * from field_values where file_url is not null and file_url <> ''
  LOOP
    --select row_data.id;
    raise notice 'Value: %', row_data.field_id;
    --field_files
    insert into field_files (id, name, url, field_value_id)
    select nextval('seq_field_files'), 'Нет имени', row_data.file_url, row_data.id;
  END LOOP;
END $$;

DO $$
DECLARE
groupId BIGINT;
listEditorId BIGINT;
BEGIN

  select id into listEditorId from list_editor_item where mnemo_code = 'cooperative_plot';

  insert into fields_groups (id, internal_name, name, position, object_type, list_editor_item_id)
  select nextval('seq_fields_groups'), 'COMMUNITY_ADDITIONAL_GROUP_COOPERATIVE_PLOT_MANAGERS', 'Руководство Кооперативного Участка', 601, 'COMMUNITY', listEditorId
  where not exists(select 1 from fields_groups where internal_name = 'COMMUNITY_ADDITIONAL_GROUP_COOPERATIVE_PLOT_MANAGERS');

  select id into groupId from fields_groups where internal_name = 'COMMUNITY_ADDITIONAL_GROUP_COOPERATIVE_PLOT_MANAGERS';


  INSERT INTO fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
  SELECT nextval('seq_fields'), true, 'PRESIDENT_OF_COOPERATIVE_PLOT', 'Председатель КУч', 0, groupId, 18, null, 'Начните вводить имя пользователя', false, false, 1, false, false, true, true
  WHERE NOT EXISTS(SELECT 1 FROM fields WHERE fields_group_id = groupId and internal_name = 'PRESIDENT_OF_COOPERATIVE_PLOT');

  INSERT INTO fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
  SELECT nextval('seq_fields'), true, 'PRESIDENT_OF_COOPERATIVE_PLOT_ID', '', 1, groupId, 0, null, null, true, false, 1, false, false, false, false
  WHERE NOT EXISTS(SELECT 1 FROM fields WHERE fields_group_id = groupId and internal_name = 'PRESIDENT_OF_COOPERATIVE_PLOT_ID');



  INSERT INTO fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
  SELECT nextval('seq_fields'), true, 'REVISOR_OF_COOPERATIVE_PLOT', 'Ревизор КУч', 2, groupId, 18, null, 'Начните вводить имя пользователя', false, false, 1, false, false, true, true
  WHERE NOT EXISTS(SELECT 1 FROM fields WHERE fields_group_id = groupId and internal_name = 'REVISOR_OF_COOPERATIVE_PLOT');

  INSERT INTO fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
  SELECT nextval('seq_fields'), true, 'REVISOR_OF_COOPERATIVE_PLOT_ID', '', 3, groupId, 0, null, null, true, false, 1, false, false, false, false
  WHERE NOT EXISTS(SELECT 1 FROM fields WHERE fields_group_id = groupId and internal_name = 'REVISOR_OF_COOPERATIVE_PLOT_ID');

END $$;

-- //@UNDO
-- SQL to undo the change goes here.


