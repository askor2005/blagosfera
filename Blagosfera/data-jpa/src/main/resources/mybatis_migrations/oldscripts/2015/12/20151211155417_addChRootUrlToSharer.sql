-- // addChRootUrlToSharer
-- Migration SQL that makes the change goes here.

ALTER TABLE sharers ADD COLUMN chroot_url character varying(255);

-- //@UNDO
-- SQL to undo the change goes here.


