-- // create_support_tables
-- Migration SQL that makes the change goes here.
do $$
begin
create sequence support_requests_id;
create sequence support_requests_types_id; 
create table support_requests_types(
 id bigint PRIMARY KEY DEFAULT nextval('support_requests_types_id'),
  name text UNIQUE NOT NULL,
  admin_emails_list text NOT NULL
);
create table support_requests(
  id bigint PRIMARY KEY DEFAULT nextval('support_requests_id'),
  email text NOT NULL,
  theme text NOT NULL,
  description text NOT NULL,
  status text NOT NULL,
  support_request_type_id bigint references      support_requests_types(id)
  
);
insert into support_requests_types (name,admin_emails_list) values('Другая тема','');
end $$;


-- //@UNDO
-- SQL to undo the change goes here.
 do $$
 begin
 drop table support_requests;
 drop table support_requests_types;                       
 drop sequence support_requests_id;
 drop sequence support_requests_types_id; 
 end $$;


