-- // Process tree table create
-- Migration SQL that makes the change goes here.

CREATE SEQUENCE seq_bp_process_tree START 1;
CREATE TABLE bp_process_tree
(
  id bigint NOT NULL,
  name character varying(255) NOT NULL,
  model_id bigint NULL,
  parent_id bigint NULL,
  position bigint NOT NULL,
  CONSTRAINT bp_process_tree_pkey PRIMARY KEY (id),
  CONSTRAINT bp_process_tree_parent_fkey FOREIGN KEY (parent_id)
    REFERENCES bp_process_tree (id) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT bp_process_tree_model_fkey FOREIGN KEY (model_id)
    REFERENCES bp_model (id) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);

CREATE UNIQUE INDEX bp_process_tree_name_parent_unique ON bp_process_tree (name, parent_id);
CREATE UNIQUE INDEX bp_process_tree_position_parent_unique ON bp_process_tree (position, parent_id);
CREATE INDEX bp_process_tree_parent_index ON bp_process_tree (parent_id);
CREATE INDEX bp_process_tree_name_index ON bp_process_tree (name);

-- //@UNDO
-- SQL to undo the change goes here.

DROP TABLE bp_process_tree CASCADE;
DROP SEQUENCE seq_bp_process_tree;


