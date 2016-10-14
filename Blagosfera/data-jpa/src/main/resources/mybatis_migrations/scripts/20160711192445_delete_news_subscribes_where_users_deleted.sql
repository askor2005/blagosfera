-- // delete_news_subscribes_where_users_deleted
-- Migration SQL that makes the change goes here.
BEGIN;
delete from news_subscribes where (scope_type = 'SHARER' and  ((select deleted from sharers where id = scope_id) is true) ) or (scope_type = 'COMMUNITY' and  ((select deleted from communities where id = scope_id) is true) );
delete from news_subscribes where (scope_type = 'SHARER' and not exists (select * from sharers where id = scope_id)) or (scope_type = 'COMMUNITY' and not exists (select * from communities where id = scope_id));
COMMIT;

-- //@UNDO
-- SQL to undo the change goes here.


