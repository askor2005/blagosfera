-- // add_passport_litva
-- Migration SQL that makes the change goes here.
update system_settings set val='{
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
        countryComCode : "lt",

        showPersonInn : true,
        showSnils : false,
        showByIdentificationNumber : true,
        showKzIndividualIdentificationNumber : false,
        showPassportSerial : false,
        showPassportNumber : true,
        showPassportDivision : false,
        showPassportExpiredDate : false,
        showPassportExpirationDate : true,
        showPassportDealer : true,
        holder : "_",
        maskByIdentificationNumber : "99999999999",
        holderByIdentificationNumber : "______________",
        maskPassportNumber: "99999999",
        holderPassportNumber: "________"

        
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
  }' where key='passport.citizenship.settings';


-- //@UNDO
-- SQL to undo the change goes here.


