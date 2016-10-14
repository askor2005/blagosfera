-- // add_admin_news_category_section
-- Migration SQL that makes the change goes here.

INSERT INTO sections(id, link, name, "position", title, parent_id, published, type)
    VALUES (nextval('seq_sections'), '/admin/news/categories', 'adminNewsCategories',
    (SELECT max(position) + 1 FROM sections WHERE parent_id  in (SELECT id FROM sections WHERE name = 'adminSections')) , 'Категории новостей',
    (SELECT id FROM sections WHERE name = 'adminSections'), TRUE, 2);

-- //@UNDO
-- SQL to undo the change goes here.


