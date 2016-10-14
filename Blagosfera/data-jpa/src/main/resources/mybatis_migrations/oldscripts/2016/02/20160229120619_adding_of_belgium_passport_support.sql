-- // adding_of_belgium_passport_support
-- Migration SQL that makes the change goes here.

INSERT INTO list_editor_item(
            id, is_active, text, list_editor, is_selected_item,
            listeditoritemtype, mnemo_code)
    VALUES (nextval('seq_list_editor_item'), FALSE, 'Бельгия', 4, TRUE,
            0, 'be');


INSERT INTO fields(
            id, hideable, internal_name, name, "position", fields_group_id,
            type, hidden_by_default, is_unique, points,
            required, verified_editable, use_case)
    VALUES (nextval('seq_fields'), TRUE, 'PASSPORT_EXPIRATION_DATE', 'Дата окончания срока действия паспорта', 8, 18,
            1, TRUE, FALSE, 1,
            FALSE, FALSE, FALSE);

UPDATE fields SET position = 9 WHERE internal_name = 'PASSPORT_DEALER';

UPDATE fields SET position = 10 WHERE internal_name = 'PASSPORT_DIVISION';

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
       countryComCode : "be",

        showPersonInn : false,
        showSnils : false,
        showByIdentificationNumber : false,
        showKzIndividualIdentificationNumber : false,
        showPassportSerial : false,
        showPassportNumber : true,
        showPassportExpirationDate: true,
        showPassportDivision : false,
        showPassportDealer : false,

        holder : "_",

        maskPassportNumber : "aa999999",
        holderPassportNumber : "________"
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

        maskPersonInn : "9999999999",
        holderPersonInn : "__________",

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

-- //@UNDO
-- SQL to undo the change goes here.


