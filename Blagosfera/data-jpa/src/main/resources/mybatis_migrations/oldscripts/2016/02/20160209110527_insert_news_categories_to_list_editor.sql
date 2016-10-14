-- // insert_news_categories_to_list_editor
-- Migration SQL that makes the change goes here.


INSERT INTO list_editor(
            id, form_name, listeditortype, name)
    VALUES (nextval('seq_list_editor'), 'news_categories_form_id', 0, 'news_categories');

INSERT INTO list_editor_item(
            id, is_active, text, list_editor, is_selected_item,
            listeditoritemtype, item_order, mnemo_code)
    VALUES (nextval('seq_list_editor_item'), TRUE , 'Общие', (SELECT MAX(id) FROM list_editor WHERE name='news_categories'), TRUE,
            0, 0, 'common_news_category');


-- //@UNDO
-- SQL to undo the change goes here.


