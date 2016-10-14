-- // update fields settings
-- Migration SQL that makes the change goes here.

UPDATE fields
SET verified_editable = TRUE
WHERE
  internal_name = 'FPOSTAL_CODE'
  OR internal_name = 'FCOUNTRY_CL'
  OR internal_name = 'FREGION_RL'
  OR internal_name = 'FREGION_RL_DESCRIPTION'
  OR internal_name = 'FREGION_CODE'
  OR internal_name = 'FAREA_AL'
  OR internal_name = 'FAREA_AL_DESCRIPTION'
  OR internal_name = 'FDISTRICT_DESCRIPTION_SHORT'
  OR internal_name = 'FCITY_TL'
  OR internal_name = 'FCITY_TL_DESCRIPTION'
  OR internal_name = 'FCITY_DESCRIPTION_SHORT'
  OR internal_name = 'FSTREET'
  OR internal_name = 'FSTREET_DESCRIPTION'
  OR internal_name = 'FSTREET_DESCRIPTION_SHORT'
  OR internal_name = 'FHOUSE_DESCRIPTION'
  OR internal_name = 'FHOUSE'
  OR internal_name = 'FROOM_DESCRIPTION'
  OR internal_name = 'FROOM'
  OR internal_name = 'F_GEO_POSITION'
  OR internal_name = 'F_GEO_LOCATION';

UPDATE fields
SET hideable = FALSE, hidden_by_default = FALSE
WHERE
  internal_name = 'FCITY_TL'
  OR internal_name = 'FCITY_TL_DESCRIPTION';

UPDATE field_values
SET hidden = FALSE
WHERE
  field_id IN (SELECT id
               FROM fields
               WHERE internal_name = 'FCITY_TL' OR internal_name = 'FCITY_TL_DESCRIPTION')

-- //@UNDO
-- SQL to undo the change goes here.


