-- // add_ramera_list_editors_item
-- Migration SQL that makes the change goes here.
do $$
begin
if not exists(select * from list_editor_item where mnemo_code = 'blagosfera_editors') then
insert into list_editor_item (id,is_active,text,list_editor,parent_id,is_selected_item,listeditoritemtype,item_order,mnemo_code) 
	values(190,FALSE,'Редакторы Благосферы',null,(select id from list_editor_item where text='Объединение по Труду'),TRUE,0,9,'blagosfera_editors');
end if;
end $$;

-- //@UNDO
-- SQL to undo the change goes here.


