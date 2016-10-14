-- // setChRootUrlToSharer
-- Migration SQL that makes the change goes here.


UPDATE sharers SET chroot_url='/instruction' WHERE status = 4;

-- //@UNDO
-- SQL to undo the change goes here.


