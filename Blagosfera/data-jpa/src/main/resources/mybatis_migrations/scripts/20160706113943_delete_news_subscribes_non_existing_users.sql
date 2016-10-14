-- // delete_news_subscribes_non_existing_users
-- Migration SQL that makes the change goes here.
delete from news_subscribes where (scope_type = 'SHARER' and not exists (select * from sharers where id = scope_id)) or (scope_type = 'COMMUNITY' and not exists (select * from communities where id = scope_id));


-- //@UNDO
-- SQL to undo the change goes here.


