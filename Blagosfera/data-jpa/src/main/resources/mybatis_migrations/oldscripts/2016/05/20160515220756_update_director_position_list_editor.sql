-- // update_director_position_list_editor
-- Migration SQL that makes the change goes here.

update list_editor set form_name = 'COMMUNITY_DIRECTOR_POSITION', name = 'COMMUNITY_DIRECTOR_POSITION' where name = 'position_id';

-- //@UNDO
-- SQL to undo the change goes here.


