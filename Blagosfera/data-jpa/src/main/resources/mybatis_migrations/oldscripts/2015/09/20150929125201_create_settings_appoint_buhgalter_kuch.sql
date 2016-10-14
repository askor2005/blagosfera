-- // create_settings_appoint_buhgalter_kuch
-- Migration SQL that makes the change goes here.

insert into system_settings (id, key, val, description)
select nextval('seq_system_settings'),
'plot.buhgalter.post.settings',
'{
      // Настройки шаблона заявления бухгатера
      // http://dev.blagosfera.su/admin/flowOfDocuments/documentTemplate/edit?documentTemplateId=156
      statementFromBuhgalterTemplateCode: "STATEMENT_FROM_BUHGALTER_TEMPLATE_CODE", // Код шаблона
      statementBuhgalterParticipantName: "Бухгалтер-Кандидат", // Участник документа - бухгалтер
      statementPoParticipantName: "Потребительское Общество", // Участник документа - ПО
      statementKuchParticipantName: "Кооперативный Участок", // Участник документа - КУч ПО
      statementKuchPresidentParticipantName: "Председатель КУч", // Участник - председатель КУч ПО


      // Настройки приказа о назначении бухгалетра-кассира КУЧ
      // http://dev.blagosfera.su/admin/flowOfDocuments/documentTemplate/edit?documentTemplateId=160
      orderToAppointBuhgalterTemplateCode: "ORDER_TO_APPOINT_BUHGALTER_TEMPLATE_CODE", // Код шаблона
      orderToAppointBuhgalterParticipantName: "Бухгалтер-Кассир", // Участник документа - бухгалтер
      orderToAppointKuchParticipantName: "Кооперативный участок", // Участник документа - КУч ПО
      orderToAppointPoParticipantName: "Потребительское Общество", // Участник - ПО


      // Настройки договора с бухгалтером кассиром
      // http://dev.blagosfera.su/admin/flowOfDocuments/documentTemplate/edit?documentTemplateId=161
      contractWithBuhgalterTemplateCode: "CONTRACT_WITH_BUHGALTER_TEMPLATE_CODE", // Код шаблона
      contractBuhgalterParticipantName: "Бухгалтер-Кандидат", // Участник документа - бухгалтер
      contractKuchParticipantName: "Кооперативный Участок", // Участник документа - КУч ПО
      contractPoParticipantName: "Потребительское Общество", // Участник документа - ПО


      // Настройки инструкции для бухгалтера - кассира КУч
      // http://dev.blagosfera.su/admin/flowOfDocuments/documentTemplate/edit?documentTemplateId=162
      instructionBuhgalterTemplateCode: "INSTRUCTION_BUHGALTER", // Код шаблона
      instructionBuhgalterParticipantName: "Бухгалтер-Кандидат", // Участник документа - бухгалтер
      instructionKuchParticipantName: "Кооперативный Участок", // Участник документа - КУч ПО
      instructionPoParticipantName: "Потребительское Общество" // Участник документа - ПО
}',
'Настройки документов назначения бухгалтера - кассира в КУч ПО
Страница с описанием настроек: http://wiki.ramera.ru/pages/viewpage.action?pageId=1081428'
where not exists (select id from system_settings where key = 'plot.buhgalter.post.settings');

-- //@UNDO
-- SQL to undo the change goes here.


