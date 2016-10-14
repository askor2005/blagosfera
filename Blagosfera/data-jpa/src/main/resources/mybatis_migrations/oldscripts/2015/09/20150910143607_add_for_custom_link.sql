-- // add for custom link
-- Migration SQL that makes the change goes here.

-- Создание группы полей настроек профиля
do $$
begin

  insert into fields_groups (id, internal_name, name, position, object_type, list_editor_item_id)
  select nextval('seq_fields_groups'), 'SHARER_PROFILE_SETTINGS', 'Настройки профиля', 0, 'SHARER', null
  where not exists(select 1 from fields_groups where internal_name = 'SHARER_PROFILE_SETTINGS');

end $$;

-- Создание поля короткой ссылки пользователя
do $$
declare
fieldsGroupId bigint;
begin
  select id into fieldsGroupId from fields_groups where internal_name = 'SHARER_PROFILE_SETTINGS';

  insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
  select nextval('seq_fields'), FALSE, 'SHARER_SHORT_LINK_NAME', 'Короткое имя участника для использования в символических ссылках', 7, fieldsGroupId, 0, '', '', FALSE, FALSE, 0, FALSE, FALSE, FALSE, FALSE
  where not exists(select 1 from fields where internal_name = 'SHARER_SHORT_LINK_NAME' and fields_group_id = fieldsGroupId);

end $$;

-- //@UNDO
-- SQL to undo the change goes here.


