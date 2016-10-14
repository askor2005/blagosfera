-- // update_community_fields_for_validation
-- Migration SQL that makes the change goes here.

do $$
begin
	update fields set type = 31 where internal_name = 'COMMUNITY_NAME';
	update fields set type = 31 where internal_name = 'COMMUNITY_SHORT_NAME';

	update fields set type = 32 where internal_name = 'COMMUNITY_ENG_NAME';
	update fields set type = 32 where internal_name = 'COMMUNITY_ENG_SHORT_NAME';

	update fields set type = 33 where internal_name = 'COMMUNITY_CONTACTS_EMAIL';

	update fields set type = 11 where internal_name = 'COMMUNITY_CONTACTS_WORK_TEL';
	update fields set type = 11 where internal_name = 'COMMUNITY_CONTACTS_ADDITIONAL_TEL';

	ALTER TABLE public.fields ADD COLUMN mask character varying(100);
	ALTER TABLE public.fields ADD COLUMN placeholder character varying(100);

	update fields set mask = '9999999999', placeholder = '__________' where internal_name = 'COMMUNITY_INN';
	update fields set mask = '999999999', placeholder = '_________' where internal_name = 'COMMUNITY_BANK_DETAILS_KPP';
	update fields set mask = '99999999', placeholder = '________' where internal_name = 'COMMUNITY_BANK_DETAILS_OKPO';
	update fields set mask = '9999999999999', placeholder = '_____________' where internal_name = 'COMMUNITY_BANK_DETAILS_OGRN';
	update fields set mask = '99999999999999999999', placeholder = '____________________' where internal_name = 'COMMUNITY_BANK_DETAILS_SETTLEMENT_ACCOUNT';
	update fields set mask = '99999999999999999999', placeholder = '____________________' where internal_name = 'COMMUNITY_BANK_DETAILS_CORRESPONDENT_ACCOUNT';
	update fields set mask = '999999999', placeholder = '_________' where internal_name = 'COMMUNITY_BANK_DETAILS_BIK';
	update fields set mask = '999-999-999999', placeholder = '___-___-______' where internal_name = 'COMMUNITY_PFR_ID';
	update fields set mask = '999999999999999', placeholder = '_______________' where internal_name = 'COMMUNITY_FOMS_ID';

	update fields set mask = '9999999999', placeholder = '__________' where internal_name = 'COMMUNITY_INN';

	update fields set type = 23 where internal_name in (
	'COMMUNITY_EGRUL',
	'COMMUNITY_PFR',
	'COMMUNITY_FOMS',
	'COMMUNITY_STAT',
	'COMMUNITY_DIRECTOR_PROTOCOL_NUMBER',
	'COMMUNITY_CHARTER_PROTOCOL_NUMBER');

	update fields set points = 0 where internal_name in (
	'COMMUNITY_MEMBERS_OF_THE_BOARD1',
	'COMMUNITY_MEMBERS_OF_THE_BOARD2',
	'COMMUNITY_MEMBERS_REVISOR_COMMITTEE');

end $$;


-- //@UNDO
-- SQL to undo the change goes here.


