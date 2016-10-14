-- // delete_deprecated_fields
-- Migration SQL that makes the change goes here.

-- Удаление устаревших полей
delete from field_values where field_id in (
select id from fields where fields_group_id in (select id from fields_groups where internal_name in
('ORGANIZATION_COMMON',
'ORGANIZATION_CEO',
'ORGANIZATION_LEGAL_ADDRESS',
'ORGANIZATION_ACTUAL_ADDRESS',
'ORGANIZATION_BANK',
'ORGANIZATION_COMMUNICATIONS',
'ORGANIZATION_BASIS_FOR_ENTRY',
'ORGANIZATION_REGISTRATION')));

delete from field_possible_values where field_id in (select id from fields where fields_group_id in (select id from fields_groups where internal_name in
('ORGANIZATION_COMMON',
'ORGANIZATION_CEO',
'ORGANIZATION_LEGAL_ADDRESS',
'ORGANIZATION_ACTUAL_ADDRESS',
'ORGANIZATION_BANK',
'ORGANIZATION_COMMUNICATIONS',
'ORGANIZATION_BASIS_FOR_ENTRY',
'ORGANIZATION_REGISTRATION')));

delete from fields where fields_group_id in (select id from fields_groups where internal_name in
('ORGANIZATION_COMMON',
'ORGANIZATION_CEO',
'ORGANIZATION_LEGAL_ADDRESS',
'ORGANIZATION_ACTUAL_ADDRESS',
'ORGANIZATION_BANK',
'ORGANIZATION_COMMUNICATIONS',
'ORGANIZATION_BASIS_FOR_ENTRY',
'ORGANIZATION_REGISTRATION'));

delete from fields_groups where internal_name in
('ORGANIZATION_COMMON',
'ORGANIZATION_CEO',
'ORGANIZATION_LEGAL_ADDRESS',
'ORGANIZATION_ACTUAL_ADDRESS',
'ORGANIZATION_BANK',
'ORGANIZATION_COMMUNICATIONS',
'ORGANIZATION_BASIS_FOR_ENTRY',
'ORGANIZATION_REGISTRATION');

-- //@UNDO
-- SQL to undo the change goes here.


