-- // add column leased_to_community_id to inventory units
-- Migration SQL that makes the change goes here.

ALTER TABLE community_inventory_units ADD COLUMN leased_to_community_id BIGINT;

-- //@UNDO
-- SQL to undo the change goes here.


