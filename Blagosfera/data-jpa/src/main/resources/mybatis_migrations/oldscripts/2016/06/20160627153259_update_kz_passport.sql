-- // update_kz_passport
-- Migration SQL that makes the change goes here.

do $$
declare listEditorId bigint;
begin

select id into listEditorId from list_editor where name = 'country_id';

update list_editor_item set mnemo_code = 'kz_old', text = 'Казахстан (паспорт старого образца)' where mnemo_code = 'kz';

insert into list_editor_item (id, is_active, text, list_editor, parent_id, is_selected_item, listeditoritemtype, item_order, mnemo_code)
select nextval('seq_list_editor_item'), false, 'Казахстан', listEditorId, null, false, 0, 0, 'kz';

update system_settings set val = '{
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
        countryComCode : "kz_old",

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
      },
      {
        countryComCode : "kz",

        showPersonInn : false,
        showSnils : false,
        showByIdentificationNumber : false,
        showKzIndividualIdentificationNumber : true,
        showPassportSerial : false,
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

        maskPassportNumber : "a99999999",
        holderPassportNumber : "_______",

        maskPassportDivision : "999-999",
        holderPassportDivision : "___-___"
      }
    ]
  }' where key = 'passport.citizenship.settings';

  end $$;

-- //@UNDO
-- SQL to undo the change goes here.


