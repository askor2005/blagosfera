-- // fix_show_email_settings
-- Migration SQL that makes the change goes here.


update sharer_settings set val ='CONTACTS' where (key = 'profile.show-email.mode' and val = 'LISTS');
-- //@UNDO
-- SQL to undo the change goes here.


