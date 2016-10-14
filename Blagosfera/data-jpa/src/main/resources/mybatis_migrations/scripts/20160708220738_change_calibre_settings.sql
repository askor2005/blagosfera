-- // change_calibre_settings
-- Migration SQL that makes the change goes here.

update system_settings set val = '${htmlFilePath} ${pdfFilePath} --disable-font-rescaling --margin-top 20 --margin-bottom 20 --margin-left 25 --margin-right 15 --paper-size a4 --custom-size 210x297 --unit millimeter' where key = 'calibre.arguments';

update documents_templates set pdf_export_arguments = '${htmlFilePath} ${pdfFilePath} --disable-font-rescaling --margin-top 0 --margin-bottom 0 --margin-left 0 --margin-right 0 --paper-size a4 --custom-size 202x285.2 --unit millimeter' where code in ('R11001_PO', 'usn_taxation_system');

-- //@UNDO
-- SQL to undo the change goes here.


