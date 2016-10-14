-- // create_ntc_ascor_account
-- Migration SQL that makes the change goes here.

DO $$
DECLARE
avatarRamera varchar;
BEGIN
  select avatar into avatarRamera from system_accounts where id = 1;

  insert into system_accounts (id, name, avatar) values(2, 'НТЦ "АСКОР"', avatarRamera);

END $$;

-- //@UNDO
-- SQL to undo the change goes here.


