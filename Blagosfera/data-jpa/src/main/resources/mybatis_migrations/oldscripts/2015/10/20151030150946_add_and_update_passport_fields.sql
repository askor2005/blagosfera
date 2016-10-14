-- // add and update passport fields
-- Migration SQL that makes the change goes here.

-- Добавляем к странам Казахстан
do $$
DECLARE listEditorId BIGINT;
begin

  select id into listEditorId from list_editor where name = 'country_id';

  INSERT INTO list_editor_item (id, is_active, text, list_editor, parent_id, is_selected_item, listeditoritemtype, item_order, mnemo_code)
  SELECT nextval('seq_list_editor_item'), false, 'Казахстан', listEditorId, null, true, 0, 4, 'kz'
  WHERE NOT EXISTS(SELECT 1 FROM list_editor_item WHERE list_editor = listEditorId and mnemo_code = 'kz');

end $$;

-- Добавляем поля паспортных данных: "Идентификационный номер" для РБ и "Индивидуальный идентификационный номер" для РК
do $$
DECLARE groupId BIGINT;
begin

  select id into groupId from fields_groups where internal_name = 'PERSON_PASSPORT';

  INSERT INTO fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
  SELECT nextval('seq_fields'), true, 'BY_IDENTIFICATION_NUMBER', 'Идентификационный номер', 2, groupId, 0, null, null, true, false, 0, false, false, false, false
  WHERE NOT EXISTS(select 1 from fields where fields_group_id = groupId and internal_name = 'BY_IDENTIFICATION_NUMBER');

  INSERT INTO fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
  SELECT nextval('seq_fields'), true, 'KZ_INDIVIDUAL_IDENTIFICATION_NUMBER', 'Индивидуальный идентификационный номер', 2, groupId, 0, null, null, true, false, 0, false, false, false, false
  WHERE NOT EXISTS(select 1 from fields where fields_group_id = groupId and internal_name = 'KZ_INDIVIDUAL_IDENTIFICATION_NUMBER');

end $$;

-- Выставляем последовательность следования полей паспортных данных
do $$
begin

  UPDATE fields SET position = 0 WHERE internal_name = 'PERSON_CITIZENSHIP';
  UPDATE fields SET position = 1 WHERE internal_name = 'PERSON_INN';
  UPDATE fields SET position = 2 WHERE internal_name = 'SNILS';
  UPDATE fields SET position = 3 WHERE internal_name = 'BY_IDENTIFICATION_NUMBER';
  UPDATE fields SET position = 4 WHERE internal_name = 'KZ_INDIVIDUAL_IDENTIFICATION_NUMBER';
  UPDATE fields SET position = 5 WHERE internal_name = 'PASSPORT_SERIAL';
  UPDATE fields SET position = 6 WHERE internal_name = 'PASSPORT_NUMBER';
  UPDATE fields SET position = 7 WHERE internal_name = 'PASSPORT_DATE';
  UPDATE fields SET position = 8 WHERE internal_name = 'PASSPORT_DEALER';
  UPDATE fields SET position = 9 WHERE internal_name = 'PASSPORT_DIVISION';
  UPDATE fields SET position = 10 WHERE internal_name = 'PERSON_SYSTEM_SIGNATURE';

end $$;

-- Обновляем настройку в системной переменной
do $$
begin

  UPDATE system_settings SET val = '{
    defaultSelectedCitizenship : "ru",
    defaultSetting : {
      countryComCode : "default",

      showPersonInn : true,
      showSnils : true,
      showByIdentificationNumber : false,
      showKzIndividualIdentificationNumber : false,
      showPassportSerial : true,
      showPassportNumber : true,
      showPassportDivision : true,

      holder : "_",

      maskPersonInn : "999999999999",
      holderPersonInn : "____________",

      maskSnils : "999-999-999 99",
      holderSnils : "___-___-___ __",

      maskByIdentificationNumber : "9999999 r 999 rr 9",
      holderByIdentificationNumber : "_______ _ ___ __ _",

      maskKzIndividualIdentificationNumber : "999999999999",
      holderKzIndividualIdentificationNumber : "____________",

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
        showByIdentificationNumber : false,
        showKzIndividualIdentificationNumber : false,
        showPassportSerial : true,
        showPassportNumber : true,
        showPassportDivision : true,

        holder : "_",

        maskPersonInn : "999999999999",
        holderPersonInn : "____________",

        maskSnils : "999-999-999 99",
        holderSnils : "___-___-___ __",

        maskByIdentificationNumber : "9999999 r 999 rr 9",
        holderByIdentificationNumber : "_______ _ ___ __ _",

        maskKzIndividualIdentificationNumber : "999999999999",
        holderKzIndividualIdentificationNumber : "____________",

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
        showSnils : false,
        showByIdentificationNumber : true,
        showKzIndividualIdentificationNumber : false,
        showPassportSerial : true,
        showPassportNumber : true,
        showPassportDivision : false,

        holder : "_",

        maskPersonInn : "999999999999",
        holderPersonInn : "____________",

        maskSnils : "999-999-999 99",
        holderSnils : "___-___-___ __",

        maskByIdentificationNumber : "9999999 r 999 rr 9",
        holderByIdentificationNumber : "_______ _ ___ __ _",

        maskKzIndividualIdentificationNumber : "999999999999",
        holderKzIndividualIdentificationNumber : "____________",

        maskPassportSerial : "rr",
        holderPassportSerial : "__",

        maskPassportNumber : "9999999",
        holderPassportNumber : "_______",

        maskPassportDivision : "999-999",
        holderPassportDivision : "___-___"
      },
      {
        countryComCode : "ua",

        showPersonInn : true,
        showSnils : false,
        showByIdentificationNumber : false,
        showKzIndividualIdentificationNumber : false,
        showPassportSerial : true,
        showPassportNumber : true,
        showPassportDivision : false,

        holder : "_",

        maskPersonInn : "999999999999",
        holderPersonInn : "____________",

        maskSnils : "999-999-999 99",
        holderSnils : "___-___-___ __",

        maskByIdentificationNumber : "9999999 r 999 rr 9",
        holderByIdentificationNumber : "_______ _ ___ __ _",

        maskKzIndividualIdentificationNumber : "999999999999",
        holderKzIndividualIdentificationNumber : "____________",

        maskPassportSerial : "rr",
        holderPassportSerial : "__",

        maskPassportNumber : "999999",
        holderPassportNumber : "______",

        maskPassportDivision : "999-999",
        holderPassportDivision : "___-___"
      },
      {
        countryComCode : "kz",

        showPersonInn : false,
        showSnils : false,
        showByIdentificationNumber : false,
        showKzIndividualIdentificationNumber : true,
        showPassportSerial : true,
        showPassportNumber : true,
        showPassportDivision : false,

        holder : "_",

        maskPersonInn : "999999999999",
        holderPersonInn : "____________",

        maskSnils : "999-999-999 99",
        holderSnils : "___-___-___ __",

        maskByIdentificationNumber : "9999999 r 999 rr 9",
        holderByIdentificationNumber : "_______ _ ___ __ _",

        maskKzIndividualIdentificationNumber : "999999999999",
        holderKzIndividualIdentificationNumber : "____________",

        maskPassportSerial : "a",
        holderPassportSerial : "_",

        maskPassportNumber : "9999999",
        holderPassportNumber : "_______",

        maskPassportDivision : "999-999",
        holderPassportDivision : "___-___"
      }
    ]
  }' WHERE key = 'passport.citizenship.settings';

end $$;

-- //@UNDO
-- SQL to undo the change goes here.


