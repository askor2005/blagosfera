-- // batchvotings permissions
-- Migration SQL that makes the change goes here.

UPDATE community_sections
SET permission = 'VOTINGS_ADMIN'
WHERE ('name' = 'BATCH_VOTING_TEMPLATES') OR ('name' = 'BATCH_VOTING_CONSTRUCTOR');

-- //@UNDO
-- SQL to undo the change goes here.


