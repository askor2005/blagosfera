-- // edit ua citizenship
-- Migration SQL that makes the change goes here.

UPDATE system_settings SET val='{
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
  }' WHERE key='passport.citizenship.settings';

-- //@UNDO
-- SQL to undo the change goes here.


