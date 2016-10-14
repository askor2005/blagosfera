-- // add navigation menu tables
-- Migration SQL that makes the change goes here.

CREATE SEQUENCE nav_menu_item_id INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 20 CACHE 1;

CREATE TABLE nav_menu_item
(
  id            BIGINT  NOT NULL,
  parent_id     BIGINT,
  default_route BOOLEAN NOT NULL DEFAULT FALSE,
  title         TEXT,
  icon          TEXT,
  path          TEXT,
  expandable    BOOLEAN NOT NULL DEFAULT FALSE,
  collapsed     BOOLEAN NOT NULL DEFAULT FALSE,
  switch_menu   BOOLEAN NOT NULL DEFAULT FALSE,
  lazy_load     BOOLEAN NOT NULL DEFAULT FALSE,

  CONSTRAINT nav_menu_item_pkey PRIMARY KEY (id),
  CONSTRAINT fk_nav_menu_item_nav_menu_item FOREIGN KEY (parent_id)
  REFERENCES nav_menu_item (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
);

-- //@UNDO
-- SQL to undo the change goes here.


