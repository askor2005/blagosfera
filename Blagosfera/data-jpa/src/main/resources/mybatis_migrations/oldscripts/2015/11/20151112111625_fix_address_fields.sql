-- // fix_address_fields
-- Migration SQL that makes the change goes here.

delete from field_values where field_id in (select id from fields where internal_name in ('SUBHOUSE','FSUBHOUSE','OSUBHOUSE','YSUBHOUSE'));
delete from fields where internal_name in ('SUBHOUSE','FSUBHOUSE','OSUBHOUSE','YSUBHOUSE');

update fields set comment = 'Индекс проставляется автоматически, если адресные данные получены из подасказок' where internal_name in ('REGISTRATOR_OFFICE_POSTAL_CODE','OPOSTAL_CODE','POSTAL_CODE','FPOSTAL_CODE','YPOSTAL_CODE');

-- //@UNDO
-- SQL to undo the change goes here.


