-- // optimization_rev_2
-- Migration SQL that makes the change goes here.

CREATE INDEX ON field_values(object_id);
CREATE INDEX ON field_values(field_id);
CREATE INDEX ON communities(deleted);

-- //@UNDO
-- SQL to undo the change goes here.


