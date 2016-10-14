-- // news_refactor
-- Migration SQL that makes the change goes here.

ALTER TABLE news ADD COLUMN category_id bigint  NOT NULL DEFAULT 1;
ALTER TABLE news ALTER COLUMN category_id DROP DEFAULT;
UPDATE news SET category_id = (SELECT MAX(id) FROM list_editor_item WHERE mnemo_code='common_news_category');

ALTER TABLE news ADD CONSTRAINT fk_list_editor_item FOREIGN KEY (category_id)
      REFERENCES list_editor_item (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
-- //@UNDO
-- SQL to undo the change goes here.


