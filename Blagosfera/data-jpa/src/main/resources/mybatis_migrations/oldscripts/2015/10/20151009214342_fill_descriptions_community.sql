-- // fill descriptions community
-- Migration SQL that makes the change goes here.


do $$
declare groupId bigint;

declare regionFieldId bigint;
declare areaFieldId bigint;
declare cityFieldId bigint;
declare streetFieldId bigint;
declare houseFieldId bigint;
declare roomFieldId bigint;

declare regionDescriptionFieldId bigint;
declare areaDescriptionFieldId bigint;
declare cityDescriptionFieldId bigint;
declare streetDescriptionFieldId bigint;
declare houseDescriptionFieldId bigint;
declare roomDescriptionFieldId bigint;

declare flag boolean;

declare region character varying(5000000);
declare regionDescription character varying(5000000);
declare regionFieldValue character varying(5000000);
declare regionDescriptionFieldValue character varying(5000000);

declare area character varying(5000000);
declare areaDescription character varying(5000000);
declare areaFieldValue character varying(5000000);
declare areaDescriptionFieldValue character varying(5000000);

declare city character varying(5000000);
declare cityDescription character varying(5000000);
declare cityFieldValue character varying(5000000);
declare cityDescriptionFieldValue character varying(5000000);

declare street character varying(5000000);
declare streetDescription character varying(5000000);
declare streetFieldValue character varying(5000000);
declare streetDescriptionFieldValue character varying(5000000);

declare house character varying(5000000);
declare houseDescription character varying(5000000);
declare houseFieldValue character varying(5000000);
declare houseDescriptionFieldValue character varying(5000000);

declare room character varying(5000000);
declare roomDescription character varying(5000000);
declare roomFieldValue character varying(5000000);
declare roomDescriptionFieldValue character varying(5000000);

declare pos integer;

declare community record;
begin
  select id into groupId from fields_groups where internal_name =  'COMMUNITY_WITH_ORGANIZATION_LEGAL_ADDRESS';

  select id into regionFieldId from fields where internal_name = 'COMMUNITY_LEGAL_REGION' and fields_group_id = groupId;
  select id into areaFieldId from fields where internal_name = 'COMMUNITY_LEGAL_AREA' and fields_group_id = groupId;
  select id into cityFieldId from fields where internal_name = 'COMMUNITY_LEGAL_LOCALITY' and fields_group_id = groupId;
  select id into streetFieldId from fields where internal_name = 'COMMUNITY_LEGAL_STREET' and fields_group_id = groupId;
  select id into houseFieldId from fields where internal_name = 'COMMUNITY_LEGAL_HOUSE' and fields_group_id = groupId;
  select id into roomFieldId from fields where internal_name = 'COMMUNITY_LEGAL_OFFICE' and fields_group_id = groupId;

  select id into regionDescriptionFieldId from fields where internal_name = 'COMMUNITY_LEGAL_REGION_DESCRIPTION' and fields_group_id = groupId;
  select id into areaDescriptionFieldId from fields where internal_name = 'COMMUNITY_LEGAL_AREA_DESCRIPTION' and fields_group_id = groupId;
  select id into cityDescriptionFieldId from fields where internal_name = 'COMMUNITY_LEGAL_LOCALITY_DESCRIPTION' and fields_group_id = groupId;
  select id into streetDescriptionFieldId from fields where internal_name = 'COMMUNITY_LEGAL_STREET_DESCRIPTION' and fields_group_id = groupId;
  select id into houseDescriptionFieldId from fields where internal_name = 'COMMUNITY_LEGAL_HOUSE_DESCRIPTION' and fields_group_id = groupId;
  select id into roomDescriptionFieldId from fields where internal_name = 'COMMUNITY_LEGAL_OFFICE_DESCRIPTION' and fields_group_id = groupId;

  RAISE NOTICE 'regionFieldId: %', regionFieldId;
  RAISE NOTICE 'areaFieldId: %', areaFieldId;
  RAISE NOTICE 'cityFieldId: %', cityFieldId;
  RAISE NOTICE 'streetFieldId: %', streetFieldId;
  RAISE NOTICE 'houseFieldId: %', houseFieldId;
  RAISE NOTICE 'roomFieldId: %', roomFieldId;

  RAISE NOTICE 'regionDescriptionFieldId: %', regionDescriptionFieldId;
  RAISE NOTICE 'areaDescriptionFieldId: %', areaDescriptionFieldId;
  RAISE NOTICE 'cityDescriptionFieldId: %', cityDescriptionFieldId;
  RAISE NOTICE 'streetDescriptionFieldId: %', streetDescriptionFieldId;
  RAISE NOTICE 'houseDescriptionFieldId: %', houseDescriptionFieldId;
  RAISE NOTICE 'roomDescriptionFieldId: %', roomDescriptionFieldId;

  FOR community IN select * from communities where deleted=false and id in (select object_id from field_values where string_value='COMMUNITY_WITH_ORGANIZATION'and field_id in (select id from fields where internal_name='COMMUNITY_TYPE'))
  LOOP
    RAISE NOTICE 'community: %', community;

    -- РЕГИОН * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    select string_value into regionFieldValue from field_values where object_type='COMMUNITY' and object_id=community.id and field_id = regionFieldId;

    IF regionFieldValue IS NULL
    THEN
      regionFieldValue := '';
    END if;

    regionFieldValue := trim(both ' ' from regionFieldValue);

    flag := false;
    region := lower(regionFieldValue);
    regionDescription := '';

    IF region like '%' || lower('Адыгея') || '%'
    THEN
      flag := true;
      region := 'Адыгея';
      regionDescription := 'Республика';
    ELSIF region like '%' || lower('Башкортостан') || '%'
    THEN
      flag := true;
      region := 'Башкортостан';
      regionDescription := 'Республика';
    ELSIF region like '%' || lower('Бурятия') || '%'
    THEN
      flag := true;
      region := 'Бурятия';
      regionDescription := 'Республика';
    ELSIF region like '%' || lower('Алтай') || '%'
    THEN
      flag := true;
      region := 'Алтай';
      regionDescription := 'Республика';
    ELSIF region like '%' || lower('Дагестан') || '%'
    THEN
      flag := true;
      region := 'Дагестан';
      regionDescription := 'Республика';
    ELSIF region like '%' || lower('Ингушетия') || '%'
    THEN
      flag := true;
      region := 'Ингушетия';
      regionDescription := 'Республика';
    ELSIF region like '%' || lower('Кабардино-Балкарская') || '%'
    THEN
      flag := true;
      region := 'Кабардино-Балкарская';
      regionDescription := 'Республика';
    ELSIF region like '%' || lower('Калмыкия') || '%'
    THEN
      flag := true;
      region := 'Калмыкия';
      regionDescription := 'Республика';
    ELSIF region like '%' || lower('Карачаево-Черкесия') || '%'
    THEN
      flag := true;
      region := 'Карачаево-Черкесия';
      regionDescription := 'Республика';
    ELSIF region like '%' || lower('Карелия') || '%'
    THEN
      flag := true;
      region := 'Карелия';
      regionDescription := 'Республика';
    ELSIF region like '%' || lower('Коми') || '%'
    THEN
      flag := true;
      region := 'Коми';
      regionDescription := 'Республика';
    ELSIF region like '%' || lower('Марий Эл') || '%'
    THEN
      flag := true;
      region := 'Марий Эл';
      regionDescription := 'Республика';
    ELSIF region like '%' || lower('Мордовия') || '%'
    THEN
      flag := true;
      region := 'Мордовия';
      regionDescription := 'Республика';
    ELSIF region like '%' || lower('Саха (Якутия)') || '%'
    THEN
      flag := true;
      region := 'Саха (Якутия)';
      regionDescription := 'Республика';
    ELSIF region like '%' || lower('Саха') || '%'
    THEN
      flag := true;
      region := 'Саха (Якутия)';
      regionDescription := 'Республика';
    ELSIF region like '%' || lower('Якутия') || '%'
    THEN
      flag := true;
      region := 'Саха (Якутия)';
      regionDescription := 'Республика';
    ELSIF region like '%' || lower('Северная Осетия-Алания') || '%'
    THEN
      flag := true;
      region := 'Северная Осетия-Алания';
      regionDescription := 'Республика';
    ELSIF region like '%' || lower('Татарстан') || '%'
    THEN
      flag := true;
      region := 'Татарстан';
      regionDescription := 'Республика';
    ELSIF region like '%' || lower('Тыва') || '%'
    THEN
      flag := true;
      region := 'Тыва';
      regionDescription := 'Республика';
    ELSIF region like '%' || lower('Удмуртская') || '%'
    THEN
      flag := true;
      region := 'Удмуртская';
      regionDescription := 'Республика';
    ELSIF region like '%' || lower('Хакасия') || '%'
    THEN
      flag := true;
      region := 'Хакасия';
      regionDescription := 'Республика';
    ELSIF region like '%' || lower('Чеченская') || '%'
    THEN
      flag := true;
      region := 'Чеченская';
      regionDescription := 'Республика';
    ELSIF region like '%' || lower('Чувашская') || '%'
    THEN
      flag := true;
      region := 'Чувашская';
      regionDescription := 'Республика';
    ELSIF region like '%' || lower('Алтайский') || '%'
    THEN
      flag := true;
      region := 'Алтайский';
      regionDescription := 'Край';
    ELSIF region like '%' || lower('Краснодарский') || '%'
    THEN
      flag := true;
      region := 'Краснодарский';
      regionDescription := 'Край';
    ELSIF region like '%' || lower('Красноярский') || '%'
    THEN
      flag := true;
      region := 'Красноярский';
      regionDescription := 'Край';
    ELSIF region like '%' || lower('Приморский') || '%'
    THEN
      flag := true;
      region := 'Приморский';
      regionDescription := 'Край';
    ELSIF region like '%' || lower('Ставропольский') || '%'
    THEN
      flag := true;
      region := 'Ставропольский';
      regionDescription := 'Край';
    ELSIF region like '%' || lower('Хабаровский') || '%'
    THEN
      flag := true;
      region := 'Хабаровский';
      regionDescription := 'Край';
    ELSIF region like '%' || lower('Амурская') || '%'
    THEN
      flag := true;
      region := 'Амурская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Архангельская') || '%'
    THEN
      flag := true;
      region := 'Архангельская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Астраханская') || '%'
    THEN
      flag := true;
      region := 'Астраханская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Белгородская') || '%'
    THEN
      flag := true;
      region := 'Белгородская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Брянская') || '%'
    THEN
      flag := true;
      region := 'Брянская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Владимирская') || '%'
    THEN
      flag := true;
      region := 'Владимирская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Волгоградская') || '%'
    THEN
      flag := true;
      region := 'Волгоградская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Вологодская') || '%'
    THEN
      flag := true;
      region := 'Вологодская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Воронежская') || '%'
    THEN
      flag := true;
      region := 'Воронежская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Ивановская') || '%'
    THEN
      flag := true;
      region := 'Ивановская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Иркутская') || '%'
    THEN
      flag := true;
      region := 'Иркутская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Калининградская') || '%'
    THEN
      flag := true;
      region := 'Калининградская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Калужская') || '%'
    THEN
      flag := true;
      region := 'Калужская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Камчатский') || '%'
    THEN
      flag := true;
      region := 'Камчатский';
      regionDescription := 'Край';
    ELSIF region like '%' || lower('Камчатская') || '%'
    THEN
      flag := true;
      region := 'Камчатский';
      regionDescription := 'Край';
    ELSIF region like '%' || lower('Кемеровская') || '%'
    THEN
      flag := true;
      region := 'Кемеровская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Кировская') || '%'
    THEN
      flag := true;
      region := 'Кировская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Костромская') || '%'
    THEN
      flag := true;
      region := 'Костромская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Курганская') || '%'
    THEN
      flag := true;
      region := 'Курганская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Курская') || '%'
    THEN
      flag := true;
      region := 'Курская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Ленинградская') || '%'
    THEN
      flag := true;
      region := 'Ленинградская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Липецкая') || '%'
    THEN
      flag := true;
      region := 'Липецкая';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Магаданская') || '%'
    THEN
      flag := true;
      region := 'Магаданская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Московская') || '%'
    THEN
      flag := true;
      region := 'Московская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Мурманская') || '%'
    THEN
      flag := true;
      region := 'Мурманская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Нижегородская') || '%'
    THEN
      flag := true;
      region := 'Нижегородская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Новгородская') || '%'
    THEN
      flag := true;
      region := 'Новгородская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Новосибирская') || '%'
    THEN
      flag := true;
      region := 'Новосибирская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Омская') || '%'
    THEN
      flag := true;
      region := 'Омская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Оренбургская') || '%'
    THEN
      flag := true;
      region := 'Оренбургская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Орловская') || '%'
    THEN
      flag := true;
      region := 'Орловская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Пензенская') || '%'
    THEN
      flag := true;
      region := 'Пензенская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Пермский') || '%'
    THEN
      flag := true;
      region := 'Пермский';
      regionDescription := 'Край';
    ELSIF region like '%' || lower('Пермская') || '%'
    THEN
      flag := true;
      region := 'Пермский';
      regionDescription := 'Край';
    ELSIF region like '%' || lower('Псковская') || '%'
    THEN
      flag := true;
      region := 'Псковская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Ростовская') || '%'
    THEN
      flag := true;
      region := 'Ростовская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Рязанская') || '%'
    THEN
      flag := true;
      region := 'Рязанская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Самарская') || '%'
    THEN
      flag := true;
      region := 'Самарская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Саратовская') || '%'
    THEN
      flag := true;
      region := 'Саратовская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Сахалинская') || '%'
    THEN
      flag := true;
      region := 'Сахалинская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Свердловская') || '%'
    THEN
      flag := true;
      region := 'Свердловская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Смоленская') || '%'
    THEN
      flag := true;
      region := 'Смоленская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Тамбовская') || '%'
    THEN
      flag := true;
      region := 'Тамбовская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Тверская') || '%'
    THEN
      flag := true;
      region := 'Тверская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Томская') || '%'
    THEN
      flag := true;
      region := 'Томская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Тульская') || '%'
    THEN
      flag := true;
      region := 'Тульская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Тюменская') || '%'
    THEN
      flag := true;
      region := 'Тюменская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Ульяновская') || '%'
    THEN
      flag := true;
      region := 'Ульяновская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Челябинская') || '%'
    THEN
      flag := true;
      region := 'Челябинская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Забайкальский') || '%'
    THEN
      flag := true;
      region := 'Забайкальский';
      regionDescription := 'Край';
    ELSIF region like '%' || lower('Читинская') || '%'
    THEN
      flag := true;
      region := 'Забайкальский';
      regionDescription := 'Край';
    ELSIF region like '%' || lower('Ярославская') || '%'
    THEN
      flag := true;
      region := 'Ярославская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Москва') || '%'
    THEN
      flag := true;
      region := 'Москва';
      regionDescription := 'Город';
    ELSIF region like '%' || lower('Санкт-Петербург') || '%'
    THEN
      flag := true;
      region := 'Санкт-Петербург';
      regionDescription := 'Город';
    ELSIF region like '%' || lower('Еврейская автономная') || '%'
    THEN
      flag := true;
      region := 'Еврейская автономная';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Еврейская') || '%'
    THEN
      flag := true;
      region := 'Еврейская автономная';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Агинский') || '%'
    THEN
      flag := true;
      region := 'Забайкальский';
      regionDescription := 'Край';
    ELSIF region like '%' || lower('Коми-Пермяцкий') || '%'
    THEN
      flag := true;
      region := 'Пермский';
      regionDescription := 'Край';
    ELSIF region like '%' || lower('Корякский') || '%'
    THEN
      flag := true;
      region := 'Камчатский';
      regionDescription := 'Край';
    ELSIF region like '%' || lower('Долгано-Ненецкий') || '%'
    THEN
      flag := true;
      region := 'Красноярский';
      regionDescription := 'Край';
    ELSIF region like '%' || lower('Таймырский') || '%'
    THEN
      flag := true;
      region := 'Красноярский';
      regionDescription := 'Край';
    ELSIF region like '%' || lower('Долгано') || '%'
    THEN
      flag := true;
      region := 'Красноярский';
      regionDescription := 'Край';
    ELSIF region like '%' || lower('Усть-Ордынский') || '%'
    THEN
      flag := true;
      region := 'Иркутская';
      regionDescription := 'Область';
    ELSIF region like '%' || lower('Ханты-Мансийский') || '%'
    THEN
      flag := true;
      region := 'Ханты-Мансийский';
      regionDescription := 'Автономный округ - Югра';
    ELSIF region like '%' || lower('Чукотский') || '%'
    THEN
      flag := true;
      region := 'Чукотский';
      regionDescription := 'Автономный округ';
    ELSIF region like '%' || lower('Эвенкийский') || '%'
    THEN
      flag := true;
      region := 'Красноярский';
      regionDescription := 'Край';
    ELSIF region like '%' || lower('Ямало-Ненецкий') || '%'
    THEN
      flag := true;
      region := 'Ямало-Ненецкий';
      regionDescription := 'Автономный округ';
    ELSIF region like '%' || lower('Ненецкий') || '%'
    THEN
      flag := true;
      region := 'Ненецкий';
      regionDescription := 'Автономный округ';
    ELSIF region like '%' || lower('Крым') || '%'
    THEN
      flag := true;
      region := 'Крым';
      regionDescription := 'Республика';
    ELSIF region like '%' || lower('Севастополь') || '%'
    THEN
      flag := true;
      region := 'Севастополь';
      regionDescription := 'Город';
    ELSIF region like '%' || lower('Чеченская') || '%'
    THEN
      flag := true;
      region := 'Чеченская';
      regionDescription := 'Республика';
    ELSE
      region := regionFieldValue;
    END if;

    IF flag = TRUE
    THEN
      UPDATE field_values SET string_value=region WHERE object_type='COMMUNITY' and object_id=community.id and field_id=regionFieldId;

      UPDATE field_values SET string_value=regionDescription WHERE object_type='COMMUNITY' and object_id=community.id and field_id=regionDescriptionFieldId;

      insert into field_values (id, hidden, object_type, object_id, field_id, string_value, file_url)
      select nextval('seq_field_values'), true, 'COMMUNITY', community.id, regionDescriptionFieldId, regionDescription, null
      WHERE NOT EXISTS (SELECT 1 FROM field_values WHERE object_type='COMMUNITY' and object_id=community.id and field_id=regionDescriptionFieldId);
    END if;

    RAISE NOTICE 'region: %', '[' || regionFieldValue || '] -> ' || '[' || region || ']  [' || regionDescription || ']';

    -- РАЙОН * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    select string_value into areaFieldValue from field_values where object_type='COMMUNITY' and object_id=community.id and field_id = areaFieldId;

    IF areaFieldValue IS NULL
    THEN
      areaFieldValue := '';
    END if;

    areaFieldValue := trim(both ' ' from areaFieldValue);

    flag := false;
    area := lower(areaFieldValue);
    areaDescription := '';

    IF area like '%' || lower('Москва') || '%'
    THEN
      flag := true;
      area := 'Москва';
      areaDescription := 'Город';
    ELSIF area like '%' || lower('Санкт-Петербург') || '%'
    THEN
      flag := true;
      area := 'Санкт-Петербург';
      areaDescription := 'Город';
    ELSIF area like '%' || lower('Севастополь') || '%'
    THEN
      flag := true;
      area := 'Севастополь';
      areaDescription := 'Город';
    ELSE
      flag := true;
      area := areaFieldValue;

      IF area = null or area = ''
      THEN
        areaDescription := '';
      ELSE
        areaDescription := 'Район';
      END if;

    END if;

    IF flag = TRUE
    THEN
      UPDATE field_values SET string_value=area WHERE object_type='COMMUNITY' and object_id=community.id and field_id=areaFieldId;
    END if;

    UPDATE field_values SET string_value=areaDescription WHERE object_type='COMMUNITY' and object_id=community.id and field_id=areaDescriptionFieldId;

    insert into field_values (id, hidden, object_type, object_id, field_id, string_value, file_url)
    select nextval('seq_field_values'), true, 'COMMUNITY', community.id, areaDescriptionFieldId, areaDescription, null
    WHERE NOT EXISTS (SELECT 1 FROM field_values WHERE object_type='COMMUNITY' and object_id=community.id and field_id=areaDescriptionFieldId);

    RAISE NOTICE 'area: %', '[' || areaFieldValue || '] -> ' || '[' || area || ']  [' || areaDescription || ']';

    -- НАСЕЛЁННЫЙ ПУНКТ * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    select string_value into cityFieldValue from field_values where object_type='COMMUNITY' and object_id=community.id and field_id = cityFieldId;

    IF cityFieldValue IS NULL
    THEN
      cityFieldValue := '';
    END if;

    cityFieldValue := trim(both ' ' from cityFieldValue);

    flag := false;
    city := lower(cityFieldValue);
    cityDescription := '';

    IF city like lower('Город ') || '%'
    THEN
      flag := true;
      city := substring(cityFieldValue from 7);
      city := trim(both ' ' from city);
      cityDescription := 'Город';
    ELSIF city like lower('Гор.') || '%'
    THEN
      flag := true;
      city := substring(cityFieldValue from 5);
      city := trim(both ' ' from city);
      cityDescription := 'Город';
    ELSIF city like lower('Гор ') || '%'
    THEN
      flag := true;
      city := substring(cityFieldValue from 5);
      city := trim(both ' ' from city);
      cityDescription := 'Город';
    ELSIF city like lower('Г.') || '%'
    THEN
      flag := true;
      city := substring(cityFieldValue from 3);
      city := trim(both ' ' from city);
      cityDescription := 'Город';
    ELSIF city like lower('Г ') || '%'
    THEN
      flag := true;
      city := substring(cityFieldValue from 3);
      city := trim(both ' ' from city);
      cityDescription := 'Город';
    ELSIF city like lower('Село ') || '%'
    THEN
      flag := true;
      city := substring(cityFieldValue from 6);
      city := trim(both ' ' from city);
      cityDescription := 'Село';
    ELSIF city like lower('Деревня ') || '%'
    THEN
      flag := true;
      city := substring(cityFieldValue from 9);
      city := trim(both ' ' from city);
      cityDescription := 'Деревня';
    ELSIF city like lower('Д ') || '%'
    THEN
      flag := true;
      city := substring(cityFieldValue from 3);
      city := trim(both ' ' from city);
      cityDescription := 'Деревня';
    ELSIF city like lower('Д.') || '%'
    THEN
      flag := true;
      city := substring(cityFieldValue from 3);
      city := trim(both ' ' from city);
      cityDescription := 'Деревня';
    ELSIF city like lower('Посёлок') || '%'
    THEN
      flag := true;
      city := substring(cityFieldValue from 8);
      city := trim(both ' ' from city);
      cityDescription := 'Посёлок';
    ELSIF city like lower('Поселок') || '%'
    THEN
      flag := true;
      city := substring(cityFieldValue from 8);
      city := trim(both ' ' from city);
      cityDescription := 'Посёлок';
    ELSIF city like lower('Пос ') || '%'
    THEN
      flag := true;
      city := substring(cityFieldValue from 5);
      city := trim(both ' ' from city);
      cityDescription := 'Посёлок';
    ELSIF city like lower('Пос.') || '%'
    THEN
      flag := true;
      city := substring(cityFieldValue from 5);
      city := trim(both ' ' from city);
      cityDescription := 'Посёлок';
    ELSE
      city := cityFieldValue;

      IF city = null or city = ''
      THEN
        cityDescription := '';
      ELSE
        cityDescription := 'Город';
      END if;

    END if;

    IF flag = TRUE
    THEN
      UPDATE field_values SET string_value=city WHERE object_type='COMMUNITY' and object_id=community.id and field_id=cityFieldId;
    END if;

    UPDATE field_values SET string_value=cityDescription WHERE object_type='COMMUNITY' and object_id=community.id and field_id=cityDescriptionFieldId;

    insert into field_values (id, hidden, object_type, object_id, field_id, string_value, file_url)
    select nextval('seq_field_values'), true, 'COMMUNITY', community.id, cityDescriptionFieldId, cityDescription, null
    WHERE NOT EXISTS (SELECT 1 FROM field_values WHERE object_type='COMMUNITY' and object_id=community.id and field_id=cityDescriptionFieldId);

    RAISE NOTICE 'city: %', '[' || cityFieldValue || '] -> ' || '[' || city || ']  [' || cityDescription || ']';

    -- УЛИЦА * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    select string_value into streetFieldValue from field_values where object_type='COMMUNITY' and object_id=community.id and field_id = streetFieldId;

    IF streetFieldValue IS NULL
    THEN
      streetFieldValue := '';
    END if;

    streetFieldValue := trim(both ' ' from streetFieldValue);

    flag := false;
    street := lower(streetFieldValue);
    streetDescription := '';

    IF street like '%' || lower('Улица') || '%'
    THEN
      flag := true;
      street := replace(streetFieldValue,lower('Улица'),'');
      street := trim(both ' ' from street);
      streetDescription := 'Улица';
    ELSIF street like '%' || lower('Ул.') || '%'
    THEN
      flag := true;
      street := replace(streetFieldValue,lower('Ул.'),'');
      street := trim(both ' ' from street);
      streetDescription := 'Улица';
    ELSIF street like '%' ||  lower(' Ул')
    THEN
      flag := true;
      street := replace(streetFieldValue,lower(' Ул'),'');
      street := trim(both ' ' from street);
      streetDescription := 'Улица';
    ELSIF street like '%' ||  lower(' Ул.')
    THEN
      flag := true;
      street := replace(streetFieldValue,lower(' Ул.'),'');
      street := trim(both ' ' from street);
      streetDescription := 'Улица';
    ELSIF street like '%' || lower('Проспект') || '%'
    THEN
      flag := true;
      street := replace(streetFieldValue,lower('Проспект'),'');
      street := trim(both ' ' from street);
      streetDescription := 'Проспект';
    ELSIF street like '%' ||  lower('Пр.') || '%'
    THEN
      flag := true;
      street := replace(streetFieldValue,lower('Пр.'),'');
      street := trim(both ' ' from street);
      streetDescription := 'Проспект';
    ELSIF street like '%' || lower('Переулок') || '%'
    THEN
      flag := true;
      street := replace(streetFieldValue,lower('Переулок'),'');
      street := trim(both ' ' from street);
      streetDescription := 'Переулок';
    ELSIF street like '%' || lower('Пер.') || '%'
    THEN
      flag := true;
      street := replace(streetFieldValue,lower('Пер.'),'');
      street := trim(both ' ' from street);
      streetDescription := 'Переулок';
    ELSE
      street := streetFieldValue;

      IF street = null or street = ''
      THEN
        streetDescription := '';
      ELSE
        streetDescription := 'Улица';
      END if;

    END if;

    IF flag = TRUE
    THEN
      UPDATE field_values SET string_value=street WHERE object_type='COMMUNITY' and object_id=community.id and field_id=streetFieldId;
    END if;

    UPDATE field_values SET string_value=streetDescription WHERE object_type='COMMUNITY' and object_id=community.id and field_id=streetDescriptionFieldId;

    insert into field_values (id, hidden, object_type, object_id, field_id, string_value, file_url)
    select nextval('seq_field_values'), true, 'COMMUNITY', community.id, streetDescriptionFieldId, streetDescription, null
    WHERE NOT EXISTS (SELECT 1 FROM field_values WHERE object_type='COMMUNITY' and object_id=community.id and field_id=streetDescriptionFieldId);

    RAISE NOTICE 'street: %', '[' || streetFieldValue || '] -> ' || '[' || street || ']  [' || streetDescription || ']';

    -- Дом * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    select string_value into houseFieldValue from field_values where object_type='COMMUNITY' and object_id=community.id and field_id = houseFieldId;

    IF houseFieldValue IS NULL
    THEN
      houseFieldValue := '';
    END if;

    houseFieldValue := trim(both ' ' from houseFieldValue);

    flag := false;
    house := lower(houseFieldValue);
    houseDescription := '';

    IF house like lower('Дом') || '%'
    THEN
      flag := true;
      house := replace(house,lower('Дом'),'');
      house := trim(both ' ' from house);
      houseDescription := 'Дом';
    ELSIF house like lower('д ') || '%'
    THEN
      flag := true;
      house := replace(house,lower('д '),'');
      house := trim(both ' ' from house);
      houseDescription := 'Дом';
    ELSIF house like lower('д.') || '%'
    THEN
      flag := true;
      house := replace(house,lower('д.'),'');
      house := trim(both ' ' from house);
      houseDescription := 'Дом';
    ELSE
      house := houseFieldValue;

      IF house = null or house = ''
      THEN
        houseDescription := '';
      ELSE
        houseDescription := 'Дом';
      END if;

    END if;

    IF flag = TRUE
    THEN
      UPDATE field_values SET string_value=house WHERE object_type='COMMUNITY' and object_id=community.id and field_id=houseFieldId;
    END if;

    UPDATE field_values SET string_value=houseDescription WHERE object_type='COMMUNITY' and object_id=community.id and field_id=houseDescriptionFieldId;

    insert into field_values (id, hidden, object_type, object_id, field_id, string_value, file_url)
    select nextval('seq_field_values'), true, 'COMMUNITY', community.id, houseDescriptionFieldId, houseDescription, null
    WHERE NOT EXISTS (SELECT 1 FROM field_values WHERE object_type='COMMUNITY' and object_id=community.id and field_id=houseDescriptionFieldId);

    RAISE NOTICE 'house: %', '[' || houseFieldValue || '] -> ' || '[' || house || ']  [' || houseDescription || ']';

    -- Квартира * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    select string_value into roomFieldValue from field_values where object_type='COMMUNITY' and object_id=community.id and field_id = roomFieldId;

    IF roomFieldValue IS NULL
    THEN
      roomFieldValue := '';
    END if;

    roomFieldValue := trim(both ' ' from roomFieldValue);

    flag := false;
    room := roomFieldValue;
    roomDescription := '';

    IF room = null or room = ''
    THEN
      roomDescription := '';
    ELSE
      roomDescription := 'Офис';
    END if;

    UPDATE field_values SET string_value=roomDescription WHERE object_type='COMMUNITY' and object_id=community.id and field_id=roomDescriptionFieldId;

    insert into field_values (id, hidden, object_type, object_id, field_id, string_value, file_url)
    select nextval('seq_field_values'), true, 'COMMUNITY', community.id, roomDescriptionFieldId, roomDescription, null
    WHERE NOT EXISTS (SELECT 1 FROM field_values WHERE object_type='COMMUNITY' and object_id=community.id and field_id=roomDescriptionFieldId);

    RAISE NOTICE 'room: %', '[' || roomFieldValue || '] -> ' || '[' || room || ']  [' || roomDescription || ']';

  END LOOP;

end $$;


-- //@UNDO
-- SQL to undo the change goes here.


