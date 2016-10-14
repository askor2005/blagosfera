package ru.radom.kabinet.utils;

/**
 * Наименования полей
 * Created by vgusev on 22.09.2015.
 */
public interface FieldConstants {
    //------------------------------------------------------------------------------------------------------------------
    // Поля объединений
    //------------------------------------------------------------------------------------------------------------------
    // Полное название на русском
    String COMMUNITY_FULL_RU_NAME = "COMMUNITY_NAME";

    // Короткое название на русском
    String COMMUNITY_SHORT_RU_NAME = "COMMUNITY_SHORT_NAME";

    // Полное название на англ. языке
    String COMMUNITY_FULL_EN_NAME = "COMMUNITY_ENG_NAME";

    // Короткое название на англ. языке
    String COMMUNITY_SHORT_EN_NAME = "COMMUNITY_ENG_SHORT_NAME";

    // Полное описание целей объединения
    String COMMUNITY_DESCRIPTION = "COMMUNITY_DESCRIPTION";

    // Краткое описание целей объединения
    String COMMUNITY_BRIEF_DESCRIPTION = "COMMUNITY_BRIEF_DESCRIPTION";

    // Короткое имя объединения для использования в символических ссылках
    String COMMUNITY_SHORT_LINK_NAME = "COMMUNITY_SHORT_LINK_NAME";

    //----------------------------------------------------------------------
    // Поля адресного блока объединения вне рамок юр лица
    //----------------------------------------------------------------------

    // Адрес
    String COMMUNITY_GEO_LOCATION = "COMMUNITY_GEO_LOCATION";

    // Координаты
    String COMMUNITY_GEO_POSITION = "COMMUNITY_GEO_POSITION";

    // Регион
    String COMMUNITY_REGION = "COMMUNITY_REGION";

    // Район
    String COMMUNITY_AREA = "COMMUNITY_AREA";

    // Населенный пункт
    String COMMUNITY_LOCALITY = "COMMUNITY_LOCALITY";

    // Улица
    String COMMUNITY_STREET = "COMMUNITY_STREET";

    // Дом
    String COMMUNITY_HOUSE = "COMMUNITY_HOUSE";
    //----------------------------------------------------------------------

    // Поля фактического адреса
    String COMMUNITY_FACT_REGION = "COMMUNITY_LEGAL_F_REGION";
    String COMMUNITY_FACT_REGION_DESCRIPTION = "COMMUNITY_LEGAL_F_REGION_DESCRIPTION";
    String COMMUNITY_FACT_DISTRICT = "COMMUNITY_LEGAL_F_AREA";
    String COMMUNITY_FACT_CITY = "COMMUNITY_LEGAL_F_LOCALITY";
    String COMMUNITY_FACT_CITY_DESCRIPTION = "COMMUNITY_LEGAL_F_LOCALITY_DESCRIPTION";
    String COMMUNITY_FACT_CITY_DESCRIPTION_SHORT =  "COMMUNITY_LEGAL_F_CITY_DESCRIPTION_SHORT";
    String COMMUNITY_FACT_STREET = "COMMUNITY_LEGAL_F_STREET";
    String COMMUNITY_FACT_BUILDING = "COMMUNITY_LEGAL_F_HOUSE";
    String COMMUNITY_FACT_GEO_POSITION = "COMMUNITY_LEGAL_F_GEO_POSITION";
    String COMMUNITY_FACT_GEO_LOCATION = "COMMUNITY_LEGAL_F_GEO_LOCATION";
    String COMMUNITY_FACT_OFFICE = "COMMUNITY_LEGAL_F_OFFICE";

    // Юридический адрес юр лица
    String COMMUNITY_LEGAL_REGION = "COMMUNITY_LEGAL_REGION";
    String COMMUNITY_LEGAL_REGION_DESCRIPTION = "COMMUNITY_LEGAL_REGION_DESCRIPTION";
    String COMMUNITY_LEGAL_DISTRICT = "COMMUNITY_LEGAL_AREA";
    String COMMUNITY_LEGAL_CITY = "COMMUNITY_LEGAL_LOCALITY";
    String COMMUNITY_LEGAL_CITY_DESCRIPTION = "COMMUNITY_LEGAL_LOCALITY_DESCRIPTION";
    String COMMUNITY_LEGAL_CITY_DESCRIPTION_SHORT =  "COMMUNITY_LEGAL_CITY_DESCRIPTION_SHORT";
    String COMMUNITY_LEGAL_STREET = "COMMUNITY_LEGAL_STREET";
    String COMMUNITY_LEGAL_BUILDING = "COMMUNITY_LEGAL_HOUSE";
    String COMMUNITY_LEGAL_GEO_POSITION = "COMMUNITY_LEGAL_GEO_POSITION";
    String COMMUNITY_LEGAL_GEO_LOCATION = "COMMUNITY_LEGAL_GEO_LOCATION";
    String COMMUNITY_LEGAL_OFFICE = "COMMUNITY_LEGAL_OFFICE";

    // Форма объединения
    String COMMUNITY_ASSOCIATION_FORM = "COMMUNITY_ASSOCIATION_FORM";

    // Поле - вступительный взнос физ лица
    String ENTRANCE_SHARE_FEES_FIELD_NAME = "ENTRANCE_SHARE_FEES";

    // Поле - минимальный Паевой взнос физ лица
    String MIN_SHARE_FEES_FIELD_NAME = "MIN_SHARE_FEES";

    // Поле - вступительный взнос юр лица
    String COMMUNITY_ENTRANCE_SHARE_FEES_FIELD_NAME = "COMMUNITY_ENTRANCE_SHARE_FEES";

    // Поле - минимальный Паевой взнос юр лица
    String COMMUNITY_MIN_SHARE_FEES_FIELD_NAME = "COMMUNITY_MIN_SHARE_FEES";

    // Поле - ИД директора юр лица
    String COMMUNITY_DIRECTOR_SHARER_ID = "COMMUNITY_DIRECTOR_NAME_ID";

    // Страна фактического адреса
    String COMMUNITY_LEGAL_FACT_COUNTRY = "COMMUNITY_LEGAL_F_COUNTRY";

    // Страна юридичесого адреса
    String COMMUNITY_LEGAL_REGISTRATION_COUNTRY = "COMMUNITY_LEGAL_COUNTRY";

    // Тип владения офисом
    String COMMUNITY_LEGAL_OFFICE_OWNERSHIP_TYPE = "OFFICE_OWNERSHIP_TYPE";

    // Тип владения офисом
    String COMMUNITY_LEGAL_FACT_OFFICE_OWNERSHIP_TYPE = "FACT_OFFICE_OWNERSHIP_TYPE";

    // Период аренды офиса
    String COMMUNITY_LEGAL_OFFICE_RENT_PERIOD = "OFFICE_RENT_PERIOD";

    // Период аренды офиса фактического адреса
    String COMMUNITY_LEGAL_FACT_OFFICE_RENT_PERIOD = "FACT_OFFICE_RENT_PERIOD";

    // Имя поля - ИКП сообщества
    String COMMUNITY_ID_FIELD_NAME = "COMMUNITY_LIK";

    // Виды деятельности с кодами
    String COMMUNITY_FULL_OKVEDS_FIELD_NAME = "COMMUNITY_FULL_OKVEDS";

    // Виды деятельности без кодов
    String COMMUNITY_SHORT_OKVEDS_FIELD_NAME = "COMMUNITY_SHORT_OKVEDS";

    // Коды видов деятельности через запятую
    String COMMUNITY_OKVED_CODES_FIELD_NAME = "COMMUNITY_OKVED_CODES";

    // Код основного вида деятельности
    String COMMUNITY_MAIN_OKVED_CODE_FIELD_NAME = "COMMUNITY_MAIN_OKVED_CODE";

    // Система Налогообложения
    String COMMUNITY_TAXATION_SYSTEM_FIELD_NAME = "COMMUNITY_TAXATION_SYSTEM";

    // Аватар объединения
    String COMMUNITY_AVATAR = "COMMUNITY_AVATAR";

    // ИНН объединения
    String COMMUNITY_INN = "COMMUNITY_INN";

    // тип оьъединения
    String COMMUNITY_TYPE = "COMMUNITY_TYPE";

    // Наименование поля - члены совета ПО
    String MEMBERES_OF_SOVIET_ID_FIELD_NAME = "COMMUNITY_MEMBERS_OF_THE_BOARD1";

    // Наименование поля - ИД председателя совета
    String PRESIDENT_OF_SOVIET_ID_FIELD_NAME = "COMMUNITY_CHAIRMAN_OF_THE_BOARD1_ID";

    /**
     * Наименование поля - Председатель правления
     */
    String PRESIDENT_OF_BOARD_ID_FIELD_NAME = "COMMUNITY_CHAIRMAN_OF_THE_BOARD2_ID";
    //------------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------------------------------------------------------------
    // Поля документа
    //------------------------------------------------------------------------------------------------------------------

    // Дата последней подписи в документе
    String DATE_LAST_SIGN_DOCUMENT_FIELD_NAME = "DATE_LAST_SIGN_DOCUMENT";

    // Имя поля - код документа
    String DOCUMENT_CODE_FIELD_NAME = "DOCUMENT_CODE";


    //------------------------------------------------------------------------------------------------------------------
    // Поля участника
    //------------------------------------------------------------------------------------------------------------------

    // Фамилия
    String SHARER_LASTNAME = "LASTNAME";

    // Имя
    String SHARER_FIRSTNAME = "FIRSTNAME";

    // Отчество
    String SHARER_SECONDNAME = "SECONDNAME";

    // Ссылка на учетку пользователя
    String SHARER_SHORT_LINK_NAME = "SHARER_SHORT_LINK_NAME";

    // Пол участника
    String SHARER_GENDER = "GENDER";

    // Индекс участника в шаблоне документа с групповыми полями
    String SHARER_INDEX = "PERSON_INDEX";

    // Аватар участника
    String SHARER_AVATAR = "PERSON_AVATAR";

    String SHARER_BIRTHDAY = "BIRTHDAY";
    String SHARER_PASSPORT_SERIAL = "PASSPORT_SERIAL";
    String SHARER_PASSPORT_NUMBER = "PASSPORT_NUMBER";
    String SHARER_PASSPORT_DATE = "PASSPORT_DATE";
    String SHARER_PASSPORT_DEALER = "PASSPORT_DEALER";
    String SHARER_PASSPORT_DIVISION = "PASSPORT_DIVISION";
    String SHARER_HOME_TEL = "HOME_TEL";
    String SHARER_REGISTRATOR_OFFICE_PHONE = "REGISTRATOR_OFFICE_PHONE";
    String SHARER_REGISTRATOR_MOBILE_PHONE = "REGISTRATOR_MOBILE_PHONE";
    String SHARER_MOB_TEL = "MOB_TEL";
    String SHARER_SKYPE = "SKYPE";
    String SHARER_WWW = "WWW";
    String SHARER_REGISTRATOR_OFFICE_TIMETABLE = "REGISTRATOR_OFFICE_TIMETABLE";

    // Подпись участника
    String PERSON_SYSTEM_SIGNATURE_FIELD_NAME = "PERSON_SYSTEM_SIGNATURE";

    // Дата подписи участника
    String PERSON_DATE_SYSTEM_SIGNATURE_FIELD_NAME = "PERSON_DATE_SIGN_DOCUMENT";

    //----------------------------------------------------------------
    // Поля адресного блока фактического проживания участника

    // Страна фактического адреса участника
    String FACT_COUNTRY_SHARER = "FCOUNTRY_CL";

    //
    String FACT_REGION_SHARER = "FREGION_RL";

    //
    String FACT_REGION_DESCRIPTION_SHARER = "FREGION_RL_DESCRIPTION";

    //
    String FACT_DISTRICT_SHARER = "FAREA_AL";

    //
    String FACT_CITY_SHARER = "FCITY_TL";

    //
    String FACT_CITY_DESCRIPTION_SHARER = "FCITY_TL_DESCRIPTION";

    //
    String FACT_CITY_DESCRIPTION_SHORT_SHARER = "FCITY_DESCRIPTION_SHORT";

    //
    String FACT_STREET_SHARER = "FSTREET";

    //
    String FACT_BUILDING_SHARER = "FHOUSE";

    //
    String FACT_GEO_POSITION_SHARER = "F_GEO_POSITION";

    //
    String FACT_GEO_LOCATION_SHARER = "F_GEO_LOCATION";

    //
    String FACT_ROOM_SHARER = "FROOM";
    //----------------------------------------------------------------

    //----------------------------------------------------------------
    // Поля адресного блока регистрации участника

    // Страна адреса регистрации участника
    String REGISTRATION_COUNTRY_SHARER = "COUNTRY_CL";

    //
    String REGISTRATION_REGION_SHARER = "REGION_RL";

    //
    String REGISTRATION_REGION_DESCRIPTION_SHARER = "FREGION_RL_DESCRIPTION";

    //
    String REGISTRATION_DISTRICT_SHARER = "AREA_AL";

    //
    String REGISTRATION_CITY_SHARER = "CITY_TL";

    //
    String REGISTRATION_CITY_DESCRIPTION_SHARER = "CITY_TL_DESCRIPTION";

    //
    String REGISTRATION_CITY_DESCRIPTION_SHORT_SHARER = "CITY_DESCRIPTION_SHORT";

    //
    String REGISTRATION_STREET_SHARER = "STREET";

    //
    String REGISTRATION_BUILDING_SHARER = "HOUSE";

    //
    String REGISTRATION_GEO_POSITION_SHARER = "GEO_POSITION";

    //
    String REGISTRATION_GEO_LOCATION_SHARER = "GEO_LOCATION";

    // Квартира регистрации
    String REGISTRATION_ROOM_SHARER = "ROOM";
    //----------------------------------------------------------------

    //----------------------------------------------------------------
    // Поля адресного блока офиса регистратора

    // Страна офиса регистратора
    String REGISTRATOR_OFFICE_COUNTRY = "REGISTRATOR_OFFICE_COUNTRY";

    // Регион офиса регистратора
    String REGISTRATOR_OFFICE_REGION = "REGISTRATOR_OFFICE_REGION";

    //
    String REGISTRATOR_OFFICE_REGION_DESCRIPTION = "REGISTRATOR_OFFICE_REGION_DESCRIPTION";

    // Район офиса регистратора
    String REGISTRATOR_OFFICE_DISTRICT = "REGISTRATOR_OFFICE_DISTRICT";

    // Город офиса регистратора
    String REGISTRATOR_OFFICE_CITY = "REGISTRATOR_OFFICE_CITY";

    //
    String REGISTRATOR_OFFICE_CITY_DESCRIPTION = "REGISTRATOR_OFFICE_CITY_DESCRIPTION";

    //
    String REGISTRATOR_OFFICE_CITY_DESCRIPTION_SHORT = "REGISTRATOR_OFFICE_CITY_DESCRIPTION_SHORT";

    // Улица офиса регистратора
    String REGISTRATOR_OFFICE_STREET = "REGISTRATOR_OFFICE_STREET";

    // Дом офиса регистратора
    String REGISTRATOR_OFFICE_BUILDING = "REGISTRATOR_OFFICE_BUILDING";

    String REGISTRATOR_OFFICE_GEO_POSITION = "REGISTRATOR_OFFICE_GEO_POSITION";

    String REGISTRATOR_OFFICE_GEO_LOCATION = "REGISTRATOR_OFFICE_GEO_LOCATION";

    // Офис регистратора
    String REGISTRATOR_OFFICE_ROOM = "REGISTRATOR_OFFICE_ROOM";
    //----------------------------------------------------------------

    String SHARER_BIRTHPLACE = "BIRTHPLACE";

    String SHARER_NATIONALITY="NATIONALITY";

    String SHARER_LANGUAGE = "LANGUAGE";

    String SHARER_INN = "PERSON_INN";

    String SHARER_SNILS = "SNILS";


    String SHARER_CITIZENSHIP = "PERSON_CITIZENSHIP";
    String FPOSTAL_CODE="FPOSTAL_CODE";
    String RPOSTAL_CODE = "POSTAL_CODE";
    String REGISTRATOR_OFFICE_POSTAL_CODE = "REGISTRATOR_OFFICE_POSTAL_CODE";
    String REGISTRATOR_OFFICE_AREA_DESCRIPTION = "REGISTRATOR_OFFICE_REGION_DESCRIPTION";
    String FAREA_AL_DESCRIPTION = "FAREA_AL_DESCRIPTION";
    String FDISTRICT_DESCRIPTION_SHORT = "FDISTRICT_DESCRIPTION_SHORT";
    String AREA_AL_DESCRIPTION="AREA_AL_DESCRIPTION";

    String DISTRICT_DESCRIPTION_SHORT = "DISTRICT_DESCRIPTION_SHORT";
    String REGISTRATOR_SKYPE = "REGISTRATOR_SKYPE";
}
