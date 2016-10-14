-- // passport citizenship
-- Migration SQL that makes the change goes here.

-- Создание поля "Гражданство"
do $$
declare
fieldsGroupId bigint;
begin
  select id into fieldsGroupId from fields_groups where internal_name = 'PERSON_PASSPORT';

  INSERT INTO fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
  SELECT nextval('seq_fields'), true, 'PERSON_CITIZENSHIP', 'Гражданство', 0, fieldsGroupId, 3, null, null, true, false, 1, false, false, false,false
  WHERE NOT EXISTS(SELECT 1 FROM fields WHERE fields_group_id = fieldsGroupId and internal_name = 'PERSON_CITIZENSHIP');
end $$;

-- Добавляем мнемокод для элементов "Украина", "Белорусия", "Камбоджа" для списков выбора страны.("Россия" уже есть)
do $$
DECLARE listEditorId BIGINT;
begin

  select id into listEditorId from list_editor where name = 'country_id';

  UPDATE list_editor_item SET mnemo_code = 'ua' WHERE list_editor = listEditorId and text = 'Украина';
  UPDATE list_editor_item SET mnemo_code = 'by' WHERE list_editor = listEditorId and text = 'Белорусия';
  UPDATE list_editor_item SET mnemo_code = 'kh' WHERE list_editor = listEditorId and text = 'Камбоджа';

end $$;

do $$
begin

  insert into system_settings (id, key, val, description)
  select nextval('seq_system_settings'), 'passport.citizenship.settings',
  '{
    defaultSelectedCitizenship : "ru",
    defaultSetting : {
      countryComCode : "default",

      showPersonInn : true,
      showSnils : true,
      showPassportSerial : true,
      showPassportNumber : true,
      showPassportDivision : true,

      holder : "_",

      maskPersonInn : "999999999999",
      holderPersonInn : "____________",

      maskSnils : "999-999-999 99",
      holderSnils : "___-___-___ __",

      maskPassportSerial : "99 99",
      holderPassportSerial : "__ __",

      maskPassportNumber : "999999",
      holderPassportNumber : "______",

      maskPassportDivision : "999-999",
      holderPassportDivision : "___-___"
    },
    settings : [
      {
        countryComCode : "ru",

        showPersonInn : true,
        showSnils : true,
        showPassportSerial : true,
        showPassportNumber : true,
        showPassportDivision : true,

        holder : "_",

        maskPersonInn : "999999999999",
        holderPersonInn : "____________",

        maskSnils : "999-999-999 99",
        holderSnils : "___-___-___ __",

        maskPassportSerial : "99 99",
        holderPassportSerial : "__ __",

        maskPassportNumber : "999999",
        holderPassportNumber : "______",

        maskPassportDivision : "999-999",
        holderPassportDivision : "___-___"
      },
      {
        countryComCode : "by",

        showPersonInn : false,
        showSnils : true,
        showPassportSerial : true,
        showPassportNumber : true,
        showPassportDivision : true,

        holder : "_",

        maskPersonInn : "999999999999",
        holderPersonInn : "____________",

        maskSnils : "999-999-999 99",
        holderSnils : "___-___-___ __",

        maskPassportSerial : "99 99",
        holderPassportSerial : "__ __",

        maskPassportNumber : "999999",
        holderPassportNumber : "______",

        maskPassportDivision : "999-999",
        holderPassportDivision : "___-___"
      },
      {
        countryComCode : "ua",

        showPersonInn : true,
        showSnils : false,
        showPassportSerial : true,
        showPassportNumber : true,
        showPassportDivision : true,

        holder : "_",

        maskPersonInn : "999999999999",
        holderPersonInn : "____________",

        maskSnils : "999-999-999 99",
        holderSnils : "___-___-___ __",

        maskPassportSerial : "99 99",
        holderPassportSerial : "__ __",

        maskPassportNumber : "999999",
        holderPassportNumber : "______",

        maskPassportDivision : "999-999",
        holderPassportDivision : "___-___"
      },
      {
        countryComCode : "kh",

        showPersonInn : true,
        showSnils : true,
        showPassportSerial : true,
        showPassportNumber : true,
        showPassportDivision : true,

        holder : "_",

        maskPersonInn : "999999999999",
        holderPersonInn : "____________",

        maskSnils : "999-999-999 99",
        holderSnils : "___-___-___ __",

        maskPassportSerial : "99 99",
        holderPassportSerial : "__ __",

        maskPassportNumber : "999999",
        holderPassportNumber : "______",

        maskPassportDivision : "999-999",
        holderPassportDivision : "___-___"
      },
    ]
  }',
  'Настройки отображения и формата полей паспортных данных в зависимости от выбранного гражданства'
  where not exists(select 1 from system_settings where key = 'passport.citizenship.settings');

end $$;


-- Выставляем всем существующим участникам гражданство "Россия"
do $$
declare personCitizenshipFieldId bigint;
declare listEditorId bigint;
declare listEditorItemId bigint;
declare sharer record;
begin

  select id into personCitizenshipFieldId from fields where internal_name = 'PERSON_CITIZENSHIP';
  select id into listEditorId from list_editor where name = 'country_id';
  select id into listEditorItemId from list_editor_item where list_editor = listEditorId and mnemo_code = 'ru';

  -- цикл по всем шарерам
  FOR sharer IN SELECT * FROM sharers
  LOOP

    if exists (select * from field_values where object_type = 'SHARER' and object_id = sharer.id and field_id = personCitizenshipFieldId)
    then
       UPDATE field_values SET string_value = listEditorItemId WHERE object_type = 'SHARER' and object_id = sharer.id and field_id = personCitizenshipFieldId;
    else
       insert into field_values (id, hidden, object_type, object_id, field_id, string_value, file_url)
       select nextval('seq_field_values'), true, 'SHARER', sharer.id, personCitizenshipFieldId, listEditorItemId, null;
    end if;

  END LOOP;

end $$;

-- //@UNDO
-- SQL to undo the change goes here.


