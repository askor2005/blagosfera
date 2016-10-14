-- // create_community_visit_log_entity
-- Migration SQL that makes the change goes here.
do $$
begin
create sequence community_visit_logs_id;
create table community_visit_logs(
  id bigint PRIMARY KEY DEFAULT nextval('community_visit_logs_id'),
  community_id bigint references  communities(id) NOT NULL,
  user_id bigint references  sharers(id) NOT NULL,
  visit_time timestamp NOT NULL  
);
end $$;


-- //@UNDO
-- SQL to undo the change goes here.
do $$
begin
drop table community_visit_logs;
drop sequence community_visit_logs_id;
end $$;
