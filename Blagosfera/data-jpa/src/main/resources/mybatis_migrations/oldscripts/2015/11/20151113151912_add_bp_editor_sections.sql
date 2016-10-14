-- // add bp editor sections
-- Migration SQL that makes the change goes here.

DO $$
DECLARE rootId BIGINT;
BEGIN
  rootId := nextval('seq_sections');
  INSERT INTO sections (id, link, name, position, title, parent_id, hint, icon, published, page_id, help_link, application_id, image_url, type)
  VALUES
    (rootId, NULL, 'adminBPSection', 2, 'Бизнес процессы', 676, '', 'glyphicon glyphicon-briefcase', TRUE, NULL, NULL,
     NULL, NULL, 2);

  INSERT INTO sections (id, link, name, position, title, parent_id, hint, icon, published, page_id, help_link, application_id, image_url, type)
  VALUES
    (nextval('seq_sections'), '/admin/bpeditor/tree', 'adminBPEditorTree', 0, 'Дерево бизнес процессов', rootId, '',
     'glyphicon glyphicon-tree-deciduous', TRUE, NULL, NULL, NULL, NULL, 2);

  INSERT INTO sections (id, link, name, position, title, parent_id, hint, icon, published, page_id, help_link, application_id, image_url, type)
  VALUES
    (nextval('seq_sections'), '/admin/bpeditor/stencils', 'adminBPComponentsEditor', 1, 'Редактор компонентов', rootId,
     '', 'glyphicon glyphicon-wrench', TRUE, NULL, NULL, NULL, NULL, 2);

END $$;


-- //@UNDO
-- SQL to undo the change goes here.

DELETE FROM sections
WHERE name IN (
  'adminBPSection',
  'adminBPEditorTree',
  'adminBPComponentsEditor'
)


