-- // edit_root_section_changes
-- Migration SQL that makes the change goes here.

ALTER TABLE sections ADD COLUMN can_set_forward_url boolean;
ALTER TABLE sections ADD COLUMN forward_url character varying(1000);

update sections set can_set_forward_url = true where name in (
'blagosferaCitizenship',
'blagosfera',
'radom',
'razum',
'ramera',
'radost',
'raven');

ALTER TABLE sections DROP CONSTRAINT uk_ph134hghd40unkjtf1g9ukstj;

update sections set name = 'blagosferaNews' where name = 'blagosfera';

-- //@UNDO
-- SQL to undo the change goes here.


