-- // insert_registrator_mnemocode
-- Migration SQL that makes the change goes here.

update community_posts set mnemo = 'registrator.level0' where name like '%Регистратор В%';
update community_posts set mnemo = 'registrator.level1' where name like '%Регистратор 1%';
update community_posts set mnemo = 'registrator.level3' where name like '%Регистратор 2%';
update community_posts set mnemo = 'registrator.level3' where name like '%Регистратор 3%';

-- //@UNDO
-- SQL to undo the change goes here.


