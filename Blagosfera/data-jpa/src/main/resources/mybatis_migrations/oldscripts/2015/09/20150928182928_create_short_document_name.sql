-- // create_short_document_name
-- Migration SQL that makes the change goes here.

DO $$
    BEGIN
        BEGIN
	          -- Поле в шаблонах документа - сокращённое наименование документа
            ALTER TABLE documents_templates ADD COLUMN document_short_name character varying(10000);
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column document_short_name already exists in documents_templates.';
        END;
        BEGIN
            -- Поле в документах - сокращённое наименование документа
            ALTER TABLE flowofdocument ADD COLUMN short_name character varying(1000);
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column short_name expired_date exists in flowofdocument.';
        END;
    END;
$$;

-- Вносим в поле с коротким именем документа данные из полного имени
update documents_templates dt set document_short_name = (select odt.document_name from documents_templates odt where dt.id = odt.id);

-- //@UNDO
-- SQL to undo the change goes here.


