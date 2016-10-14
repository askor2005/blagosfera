-- // remove point from skype field
-- Migration SQL that makes the change goes here.

UPDATE fields SET points=0 WHERE internal_name='SKYPE';

-- //@UNDO
-- SQL to undo the change goes here.


