-- // fix geo field position
-- Migration SQL that makes the change goes here.

-- Исправляет позицию отображения полей адреса и координат для объединений вне рамок юр лица
do $$
begin

  UPDATE fields SET position=100 WHERE internal_name='COMMUNITY_GEO_LOCATION';
  UPDATE fields SET position=101 WHERE internal_name='COMMUNITY_GEO_POSITION';

end $$;

-- //@UNDO
-- SQL to undo the change goes here.


