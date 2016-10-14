-- // fix office field position
-- Migration SQL that makes the change goes here.

do $$
begin

  UPDATE fields SET position=100 WHERE internal_name='COMMUNITY_LEGAL_GEO_LOCATION';
  UPDATE fields SET position=101 WHERE internal_name='COMMUNITY_LEGAL_GEO_POSITION';

  UPDATE fields SET position=100 WHERE internal_name='COMMUNITY_LEGAL_F_GEO_LOCATION';
  UPDATE fields SET position=101 WHERE internal_name='COMMUNITY_LEGAL_F_GEO_POSITION';

end $$;

-- //@UNDO
-- SQL to undo the change goes here.


