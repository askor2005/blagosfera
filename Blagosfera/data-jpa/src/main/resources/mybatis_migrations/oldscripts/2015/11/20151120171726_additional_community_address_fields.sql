-- // additional_community_address_fields
-- Migration SQL that makes the change goes here.

-- Создание дополнительных полей в адресных блоках объединений
do $$
declare
  groupId bigint;
  listId bigint;
begin
  select id into groupId from fields_groups where internal_name = 'COMMUNITY_WITH_ORGANIZATION_LEGAL_ADDRESS';

  insert into fields(id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
  select nextval('seq_fields'), true, 'OFFICE_OWNERSHIP_TYPE', 'Тип владения офисом', 200, groupId, 27, null, null, false, false, 0, false, false, false, true
  where not exists (select 1 from fields where internal_name = 'OFFICE_OWNERSHIP_TYPE');

  insert into fields(id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
  select nextval('seq_fields'), true, 'OFFICE_RENT_PERIOD', 'Срок аренды', 200, groupId, 28, null, null, false, false, 0, false, false, false, false
  where not exists (select 1 from fields where internal_name = 'OFFICE_RENT_PERIOD');



  insert into list_editor (id, form_name, listeditortype, name)
  select nextval('seq_list_editor'), 'OFFICE_OWNERSHIP_TYPE', 0, 'OFFICE_OWNERSHIP_TYPE'
  where not exists(select 1 from list_editor where name = 'OFFICE_OWNERSHIP_TYPE');

  select id into listId from list_editor where name = 'OFFICE_OWNERSHIP_TYPE';

  insert into list_editor_item (id,is_active,text,list_editor,parent_id,is_selected_item,listeditoritemtype,item_order,mnemo_code)
  select nextval('seq_list_editor_item'), false, 'Арендованные площади', listId, null, true, 0, null, 'rent_apartment'
  where not exists(select 1 from list_editor_item where mnemo_code = 'rent_apartment');

  insert into list_editor_item (id,is_active,text,list_editor,parent_id,is_selected_item,listeditoritemtype,item_order,mnemo_code)
  select nextval('seq_list_editor_item'), false, 'Собственные площади', listId, null, true, 0, null, 'own_apartment'
  where not exists(select 1 from list_editor_item where mnemo_code = 'own_apartment');




  select id into groupId from fields_groups where internal_name = 'COMMUNITY_WITH_ORGANIZATION_LEGAL_F_ADDRESS';

  insert into fields(id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
  select nextval('seq_fields'), true, 'FACT_OFFICE_OWNERSHIP_TYPE', 'Тип владения офисом', 200, groupId, 27, null, null, false, false, 0, false, false, false, true
  where not exists (select 1 from fields where internal_name = 'FACT_OFFICE_OWNERSHIP_TYPE');

  insert into fields(id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
  select nextval('seq_fields'), true, 'FACT_OFFICE_RENT_PERIOD', 'Срок аренды', 200, groupId, 28, null, null, false, false, 0, false, false, false, false
  where not exists (select 1 from fields where internal_name = 'FACT_OFFICE_RENT_PERIOD');



  insert into list_editor (id, form_name, listeditortype, name)
  select nextval('seq_list_editor'), 'FACT_OFFICE_OWNERSHIP_TYPE', 0, 'FACT_OFFICE_OWNERSHIP_TYPE'
  where not exists(select 1 from list_editor where name = 'FACT_OFFICE_OWNERSHIP_TYPE');

  select id into listId from list_editor where name = 'FACT_OFFICE_OWNERSHIP_TYPE';

  insert into list_editor_item (id,is_active,text,list_editor,parent_id,is_selected_item,listeditoritemtype,item_order,mnemo_code)
  select nextval('seq_list_editor_item'), false, 'Арендованные площади', listId, null, true, 0, null, 'fact_rent_apartment'
  where not exists(select 1 from list_editor_item where mnemo_code = 'fact_rent_apartment');

  insert into list_editor_item (id,is_active,text,list_editor,parent_id,is_selected_item,listeditoritemtype,item_order,mnemo_code)
  select nextval('seq_list_editor_item'), false, 'Собственные площади', listId, null, true, 0, null, 'fact_own_apartment'
  where not exists(select 1 from list_editor_item where mnemo_code = 'fact_own_apartment');

end $$;

-- //@UNDO
-- SQL to undo the change goes here.


