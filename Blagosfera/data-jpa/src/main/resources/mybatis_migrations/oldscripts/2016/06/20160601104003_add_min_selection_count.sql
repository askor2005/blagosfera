-- // add min selection count
-- Migration SQL that makes the change goes here.

ALTER TABLE voting_templates
  ADD COLUMN min_selection_count BIGINT NOT NULL DEFAULT 1;

-- //@UNDO
-- SQL to undo the change goes here.


