-- // fix_appeal_in_templates
-- Migration SQL that makes the change goes here.

update notification_templates set shorttext = replace(shorttext, 'Ув.', '{{receiver.officialAppeal}}') where shorttext like '%Ув.%';

update email_templates
   set
     subject = replace(subject, 'Ув.', '${receiver.officialAppeal}'),
     body = replace(body, 'Ув.', '${receiver.officialAppeal}')
where body like '%Ув.%' or subject like '%Ув.%';

update email_templates set body = replace(body, 'ннаписал', 'написал') where body like '%ннаписал%';

-- //@UNDO
-- SQL to undo the change goes here.


