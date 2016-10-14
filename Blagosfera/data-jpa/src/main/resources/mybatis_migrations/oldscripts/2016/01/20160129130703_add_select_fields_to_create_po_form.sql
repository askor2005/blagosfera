-- // add_select_fields_to_create_po_form
-- Migration SQL that makes the change goes here.

-- Значения выпадающих списков в форме создания ПО
DO $$
declare
	listEditorId bigint;
BEGIN
	insert into list_editor (id, form_name, listeditortype, name)
	select nextval('seq_list_editor'), 'organizationOfficePeriod', 0, 'organizationOfficePeriod'
	where not exists(select id from list_editor where name = 'organizationOfficePeriod');

	select id into listEditorId from list_editor where name = 'organizationOfficePeriod';

	insert into list_editor_item (id, is_active, text, list_editor, parent_id, is_selected_item, listeditoritemtype, item_order, mnemo_code)
	select nextval('seq_list_editor_item'), false, '1 год', listEditorId, null, true, 0, 0, '1'
	where not exists(select id from list_editor_item where mnemo_code = '1');
	insert into list_editor_item (id, is_active, text, list_editor, parent_id, is_selected_item, listeditoritemtype, item_order, mnemo_code)
	select nextval('seq_list_editor_item'), false, '2 года', listEditorId, null, true, 0, 1, '2'
	where not exists(select id from list_editor_item where mnemo_code = '2');
	insert into list_editor_item (id, is_active, text, list_editor, parent_id, is_selected_item, listeditoritemtype, item_order, mnemo_code)
	select nextval('seq_list_editor_item'), false, '3 года', listEditorId, null, true, 0, 2, '3'
	where not exists(select id from list_editor_item where mnemo_code = '3');
	insert into list_editor_item (id, is_active, text, list_editor, parent_id, is_selected_item, listeditoritemtype, item_order, mnemo_code)
	select nextval('seq_list_editor_item'), false, '4 года', listEditorId, null, true, 0, 3, '4'
	where not exists(select id from list_editor_item where mnemo_code = '4');
	insert into list_editor_item (id, is_active, text, list_editor, parent_id, is_selected_item, listeditoritemtype, item_order, mnemo_code)
	select nextval('seq_list_editor_item'), false, '5 лет', listEditorId, null, true, 0, 4, '5'
	where not exists(select id from list_editor_item where mnemo_code = '5');




	insert into list_editor (id, form_name, listeditortype, name)
	select nextval('seq_list_editor'), 'presidentOfSovietKindWorking', 0, 'presidentOfSovietKindWorking'
	where not exists(select id from list_editor where name = 'presidentOfSovietKindWorking');

	select id into listEditorId from list_editor where name = 'presidentOfSovietKindWorking';

	insert into list_editor_item (id, is_active, text, list_editor, parent_id, is_selected_item, listeditoritemtype, item_order, mnemo_code)
	select nextval('seq_list_editor_item'), false, 'На общественных началах', listEditorId, null, true, 0, 0, 'free'
	where not exists(select id from list_editor_item where mnemo_code = 'free');
	insert into list_editor_item (id, is_active, text, list_editor, parent_id, is_selected_item, listeditoritemtype, item_order, mnemo_code)
	select nextval('seq_list_editor_item'), false, 'На платной основе', listEditorId, null, true, 0, 1, 'charge'
	where not exists(select id from list_editor_item where mnemo_code = 'charge');




	insert into list_editor (id, form_name, listeditortype, name)
	select nextval('seq_list_editor'), 'boardReportFrequency', 0, 'boardReportFrequency'
	where not exists(select id from list_editor where name = 'boardReportFrequency');

	select id into listEditorId from list_editor where name = 'boardReportFrequency';

	insert into list_editor_item (id, is_active, text, list_editor, parent_id, is_selected_item, listeditoritemtype, item_order, mnemo_code)
	select nextval('seq_list_editor_item'), false, 'Не менее, чем раз в месяц', listEditorId, null, true, 0, 0, 'onePerMonth'
	where not exists(select id from list_editor_item where mnemo_code = 'onePerMonth');
	insert into list_editor_item (id, is_active, text, list_editor, parent_id, is_selected_item, listeditoritemtype, item_order, mnemo_code)
	select nextval('seq_list_editor_item'), false, 'Не менее, чем раз в квартал', listEditorId, null, true, 0, 1, 'onePerQuarter'
	where not exists(select id from list_editor_item where mnemo_code = 'onePerQuarter');
	insert into list_editor_item (id, is_active, text, list_editor, parent_id, is_selected_item, listeditoritemtype, item_order, mnemo_code)
	select nextval('seq_list_editor_item'), false, 'Не менее, чем раз в год', listEditorId, null, true, 0, 2, 'onePerYear'
	where not exists(select id from list_editor_item where mnemo_code = 'onePerYear');




	insert into list_editor (id, form_name, listeditortype, name)
	select nextval('seq_list_editor'), 'whoApprovePosition', 0, 'whoApprovePosition'
	where not exists(select id from list_editor where name = 'whoApprovePosition');

	select id into listEditorId from list_editor where name = 'whoApprovePosition';

	insert into list_editor_item (id, is_active, text, list_editor, parent_id, is_selected_item, listeditoritemtype, item_order, mnemo_code)
	select nextval('seq_list_editor_item'), false, 'Общее собрание ПО', listEditorId, null, true, 0, 0, 'commonBatchVotingPO'
	where not exists(select id from list_editor_item where mnemo_code = 'commonBatchVotingPO');
	insert into list_editor_item (id, is_active, text, list_editor, parent_id, is_selected_item, listeditoritemtype, item_order, mnemo_code)
	select nextval('seq_list_editor_item'), false, 'Совет ПО', listEditorId, null, true, 0, 1, 'sovietPO'
	where not exists(select id from list_editor_item where mnemo_code = 'sovietPO');
	insert into list_editor_item (id, is_active, text, list_editor, parent_id, is_selected_item, listeditoritemtype, item_order, mnemo_code)
	select nextval('seq_list_editor_item'), false, 'Председатель Совета ПО', listEditorId, null, true, 0, 2, 'presidentOfSovietPO'
	where not exists(select id from list_editor_item where mnemo_code = 'presidentOfSovietPO');
	insert into list_editor_item (id, is_active, text, list_editor, parent_id, is_selected_item, listeditoritemtype, item_order, mnemo_code)
	select nextval('seq_list_editor_item'), false, 'Правление ПО', listEditorId, null, true, 0, 3, 'boardPO'
	where not exists(select id from list_editor_item where mnemo_code = 'boardPO');
	insert into list_editor_item (id, is_active, text, list_editor, parent_id, is_selected_item, listeditoritemtype, item_order, mnemo_code)
	select nextval('seq_list_editor_item'), false, 'Председатель Правления ПО', listEditorId, null, true, 0, 4, 'presidentOfBoardPO'
	where not exists(select id from list_editor_item where mnemo_code = 'presidentOfBoardPO');



	insert into list_editor (id, form_name, listeditortype, name)
	select nextval('seq_list_editor'), 'whoApproveDatePay', 0, 'whoApproveDatePay'
	where not exists(select id from list_editor where name = 'whoApproveDatePay');

	select id into listEditorId from list_editor where name = 'whoApproveDatePay';

	insert into list_editor_item (id, is_active, text, list_editor, parent_id, is_selected_item, listeditoritemtype, item_order, mnemo_code)
	select nextval('seq_list_editor_item'), false, 'Общее собрание пайщиков ПО', listEditorId, null, true, 0, 0, 'whoApproveDatePayCommonBatchVotingPO'
	where not exists(select id from list_editor_item where mnemo_code = 'whoApproveDatePayCommonBatchVotingPO');
	insert into list_editor_item (id, is_active, text, list_editor, parent_id, is_selected_item, listeditoritemtype, item_order, mnemo_code)
	select nextval('seq_list_editor_item'), false, 'Совет ПО', listEditorId, null, true, 0, 1, 'whoApproveDatePaySovietPO'
	where not exists(select id from list_editor_item where mnemo_code = 'whoApproveDatePaySovietPO');



	insert into list_editor (id, form_name, listeditortype, name)
	select nextval('seq_list_editor'), 'startPeriodPay', 0, 'startPeriodPay'
	where not exists(select id from list_editor where name = 'startPeriodPay');

	select id into listEditorId from list_editor where name = 'startPeriodPay';

	insert into list_editor_item (id, is_active, text, list_editor, parent_id, is_selected_item, listeditoritemtype, item_order, mnemo_code)
	select nextval('seq_list_editor_item'), false, 'После окончания квартала', listEditorId, null, true, 0, 0, 'quarter'
	where not exists(select id from list_editor_item where mnemo_code = 'quarter');
	insert into list_editor_item (id, is_active, text, list_editor, parent_id, is_selected_item, listeditoritemtype, item_order, mnemo_code)
	select nextval('seq_list_editor_item'), false, 'После окончания финансового года', listEditorId, null, true, 0, 1, 'year'
	where not exists(select id from list_editor_item where mnemo_code = 'year');
END $$;

-- //@UNDO
-- SQL to undo the change goes here.


