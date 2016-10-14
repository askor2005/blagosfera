-- // drop voting tables
-- Migration SQL that makes the change goes here.

DROP TABLE voting CASCADE;
DROP TABLE voting_additional_data CASCADE;
DROP TABLE voting_allowed_candidates CASCADE;
DROP TABLE voting_allowed_voters CASCADE;
DROP TABLE voting_batch CASCADE;
DROP TABLE voting_batch_additional_data CASCADE;
DROP TABLE voting_batch_allowed_voters CASCADE;
DROP TABLE voting_item CASCADE;
DROP TABLE voting_system_configuration CASCADE;
DROP TABLE voting_system_configuration_parameters CASCADE;
DROP TABLE voting_vote CASCADE;
DROP TABLE voting_vote_parameters CASCADE;

DROP SEQUENCE batch_voting_id;
DROP SEQUENCE vote_id;
DROP SEQUENCE voting_batch_allowed_voters_id;
DROP SEQUENCE voting_id;
DROP SEQUENCE voting_item_id;
DROP SEQUENCE voting_system_configuration_id;

-- //@UNDO
-- SQL to undo the change goes here.


