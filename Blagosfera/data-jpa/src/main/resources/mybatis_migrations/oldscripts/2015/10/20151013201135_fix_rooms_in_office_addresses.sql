-- // fix rooms in office addresses
-- Migration SQL that makes the change goes here.

do $$
declare v record;
declare fieldId bigint;
declare room character varying(5000000);
declare fixedGeoLocation character varying(5000000);
begin

  SELECT id INTO fieldId FROM fields WHERE internal_name='COMMUNITY_LEGAL_GEO_LOCATION';

  FOR v IN select * from field_values where field_id=fieldId and string_value SIMILAR TO '% квартира (%,|%)'
  LOOP
    RAISE NOTICE 'before: %', v.string_value;

    room := substring(v.string_value from  '%#" квартира %,#"%' for '#');

    if room is null
    then
      room := substring(v.string_value from  '%#", квартира %#"' for '#');
    end if;

    if room is not null
    then
      fixedGeoLocation := replace(v.string_value,room,'');
      RAISE NOTICE 'after: %', fixedGeoLocation;
      UPDATE field_values SET string_value=fixedGeoLocation WHERE id=v.id;
    end if;

  END LOOP;


  SELECT id INTO fieldId FROM fields WHERE internal_name='COMMUNITY_LEGAL_F_GEO_LOCATION';

  FOR v IN select * from field_values where field_id=fieldId and string_value SIMILAR TO '% квартира (%,|%)'
  LOOP
    RAISE NOTICE 'before: %', v.string_value;

    room := substring(v.string_value from  '%#" квартира %,#"%' for '#');

    if room is null
    then
      room := substring(v.string_value from  '%#", квартира %#"' for '#');
    end if;

    if room is not null
    then
      fixedGeoLocation := replace(v.string_value,room,'');
      RAISE NOTICE 'after: %', fixedGeoLocation;
      UPDATE field_values SET string_value=fixedGeoLocation WHERE id=v.id;
    end if;

  END LOOP;

end $$;

-- //@UNDO
-- SQL to undo the change goes here.


