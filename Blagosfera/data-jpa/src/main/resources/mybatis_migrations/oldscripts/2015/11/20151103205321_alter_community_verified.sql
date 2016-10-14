-- // alter_community_verified
-- Migration SQL that makes the change goes here.

ALTER TABLE communities ADD COLUMN verification_date timestamp without time zone;
ALTER TABLE communities ADD COLUMN verified boolean;
ALTER TABLE communities ADD COLUMN verifier_id bigint;

ALTER TABLE communities
  ADD CONSTRAINT fk_hlco5snadbrd4twk1efjcs1ui FOREIGN KEY (verifier_id)
      REFERENCES sharers (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

-- //@UNDO
-- SQL to undo the change goes here.


