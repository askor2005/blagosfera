package ru.radom.kabinet.document.services;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import padeg.lib.Padeg;
import ru.askor.blagosfera.domain.document.Document;
import ru.askor.blagosfera.domain.document.DocumentParticipant;
import ru.askor.blagosfera.domain.document.ParticipantField;
import ru.askor.blagosfera.domain.document.userfields.DocumentUserField;
import ru.askor.blagosfera.domain.document.userfields.fieldsgroups.UserFieldsGroup;
import ru.radom.kabinet.document.dao.FlowOfDocumentDao;
import ru.radom.kabinet.document.dto.DocumentParticipantSourceDto;
import ru.radom.kabinet.document.generator.CreateDocumentParameter;
import ru.radom.kabinet.document.generator.UserFieldValue;
import ru.radom.kabinet.document.model.DocumentEntity;
import ru.radom.kabinet.utils.DateUtils;
import ru.radom.kabinet.utils.HumansStringUtils;
import ru.radom.kabinet.utils.PadegConstants;
import ru.radom.kabinet.utils.VarUtils;

import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Сервисный класс для обработки действий над пользовательскими полями
 * Created by vgusev on 06.08.2015.
 */
@Service
public class UserFieldsParserService {

    @Autowired
    private FlowOfDocumentDao documentDao;

    @Autowired
    private DocumentParticipantService participantService;

    public String parseDocumentByUserFields(String content, DocumentParticipantSourceDto participant, List<DocumentUserField> userFields) {
        // Перебираем поля и подставляем значения
        for (DocumentUserField userField : userFields) {
            List<String> documentFieldValues = userField.getDocumentFieldValues();
            if (documentFieldValues == null) {
                continue;
            }
            Map<String, Object> parameters = userField.getParameters();

            String participantTemplateTypeName = participant.getName();

            Pattern pattern = Pattern.compile("<[^\\<]*data-participant-name=\"" + Pattern.quote(participantTemplateTypeName) + "\"([^\\>]*)>([^\\<]*)</span>");
            Matcher matcher = pattern.matcher(content);

            // Найдены пользовательские поля, которые ещё не заполнены
            while (matcher.find()) {
                String rawParametersField = matcher.group(1);
                DocumentUserField foundUserField = getUserFieldFromRawData(rawParametersField, participant.getName());
                if (foundUserField != null && userField.getName().equals(foundUserField.getName()) && userField.getType().equals(foundUserField.getType())) {
                    List<String> fieldValues = new ArrayList<>();
                    if (foundUserField.getType().equals("participant")) {// Тип поля - участник
                        // В значении поля должны быть ИД участников
                        String participantFieldTemplate = (String) foundUserField.getParameters().get("participantFieldTemplate");
                        String participantType = (String) foundUserField.getParameters().get("participantType");
                        for (String strValue : documentFieldValues) {
                            Long participantId = -1l;
                            try {
                                participantId = Long.valueOf(strValue);
                            } catch (Exception e) {
                                // do nothing
                            }
                            if (participantId > -1) {
                                fieldValues.add(getParticipantValueFromParameters(participantType, participantFieldTemplate, participantId));
                            }
                        }

                    } else if (foundUserField.getType().equals("date")) { // Тип поля дата
                        // Дата прописью
                        String isWordsValueType = (String) foundUserField.getParameters().get("dateIsWordsType");
                        if (isWordsValueType == null || isWordsValueType.equals("") || isWordsValueType.equals("false")) {
                            fieldValues = documentFieldValues;
                        } else {
                            for (String dateStr : documentFieldValues) {
                                fieldValues.add(HumansStringUtils.date2string(dateStr));
                            }
                        }
                    } else if (foundUserField.getType().equals("string")) { // Тип поля строка
                        // Применяем падеж к полю со строками
                        String stringCaseName = (String)foundUserField.getParameters().get("stringCase");
                        int padegIndex = PadegConstants.PADEG_I;
                        if (stringCaseName != null && PadegConstants.PADEGES_MAP.containsKey(stringCaseName)) {
                            padegIndex = PadegConstants.PADEGES_MAP.get(stringCaseName);
                        }
                        if (padegIndex != PadegConstants.PADEG_I) {
                            List<String> padegStrings = new ArrayList<>();
                            for (String str : documentFieldValues) {
                                // TODO Наверно надо сделать сложнее - с выбором объекта склонения (ФИО и т.п.)
                                padegStrings.add(Padeg.getOfficePadeg(str, padegIndex));
                            }
                            documentFieldValues = padegStrings;
                        }
                        fieldValues = documentFieldValues;
                    } else if (foundUserField.getType().equals("number")) { // Тип поля число
                        // Число прописью
                        String isWordsValueType = (String) foundUserField.getParameters().get("numberIsWordsType");
                        if (isWordsValueType == null || isWordsValueType.equals("") || isWordsValueType.equals("false")) {
                            fieldValues = documentFieldValues;
                        } else {
                            for (String numberStr : documentFieldValues) {
                                fieldValues.add(StringUtils.trim(HumansStringUtils.number2string(numberStr.replaceAll("[^\\d]", ""))));
                            }
                        }
                    } else if (foundUserField.getType().equals("currency")) { // Тип поля денежный
                        // Деньги прописью
                        String isWordsValueType = (String) foundUserField.getParameters().get("currencyIsWordsType");
                        // Наименование типа валюты
                        String currencyType = null;
                        if (userField.getParameters() != null) {
                            currencyType = (String) userField.getParameters().get("currency_type");
                        }
                        if (isWordsValueType == null || isWordsValueType.equals("") || isWordsValueType.equals("false")) {
                            for (String currencyStr : documentFieldValues) {
                                fieldValues.add(HumansStringUtils.money2numbers(currencyStr, currencyType));
                            }
                        } else {
                            for (String currencyStr : documentFieldValues) {
                                fieldValues.add(HumansStringUtils.money2string(currencyStr, currencyType));
                            }
                        }
                    } else if (foundUserField.getType().equals("fieldsGroups")) { // Тип поля - группы полей
                        // Нужно собрать группы полей в таблицу, где шапка таблицы - наименования групп, а ячейки - контент групп

                        // Данные группового поля с UI
                        Gson gson = new Gson();
                        String fieldsGroupsStr = gson.toJson(parameters.get("fieldsGroups"));
                        Type listTypeRows = new TypeToken<ArrayList<Object>>() {}.getType();
                        Type listType = new TypeToken<ArrayList<UserFieldsGroup>>() {}.getType();
                        List<Object> fieldsGroupsRows = gson.fromJson(fieldsGroupsStr, listTypeRows);

                        List<List<UserFieldsGroup>> userFieldsGroupsForm = new ArrayList<>();
                        for (Object fieldsGroupsRow :  fieldsGroupsRows) {
                            List<UserFieldsGroup> userFieldsGroups = gson.fromJson(gson.toJson(fieldsGroupsRow), listType);
                            userFieldsGroupsForm.add(userFieldsGroups);
                        }

                        // Парсим контент группового поля
                        String fieldsGroupsString = (String)foundUserField.getParameters().get("fieldsGroupsString");
                        List<Map<String, String>> fieldsGroupsMap = (List<Map<String, String>>)getFieldsGroups(fieldsGroupsString);

                        // Значения заголовков таблицы
                        List<String> headValues = new ArrayList<>();
                        // Значения ячеек таблицы
                        Map<String, List<String>> columnValues = new HashMap<>();
                        // Значения ячеек без пользовательских полей
                        Map<String, String> columnWithoutFieldsValues = new HashMap<>();

                        // Ищем в контенте документа поле
                        // Каждая итерация данного цикла формирует колонку данных
                        for (Map<String, String> fieldsGroupItem : fieldsGroupsMap) {
                            String fieldsGroupItemName = fieldsGroupItem.get("name");
                            String fieldsGroupItemContent = fieldsGroupItem.get("content");

                            headValues.add(fieldsGroupItemName);

                            // Создаём фиктивного участника для парсинга полей
                            DocumentParticipantSourceDto childFieldsParticipant = new DocumentParticipantSourceDto();
                            childFieldsParticipant.setName(participant.getName());

                            // Перебираем данные из формы
                            for (List<UserFieldsGroup> userFieldsGroupRowForm : userFieldsGroupsForm) {
                                for (UserFieldsGroup userFieldsGroupForm : userFieldsGroupRowForm) {
                                    // Если нашли группу, то парсим её
                                    if (fieldsGroupItemName.equals(userFieldsGroupForm.getName())) {
                                        String resultContent = parseDocumentByUserFields(fieldsGroupItemContent, childFieldsParticipant, userFieldsGroupForm.getUserFields());

                                        // Добавляем значения таблицы
                                        if (!columnValues.containsKey(fieldsGroupItemName)) {
                                            columnValues.put(fieldsGroupItemName, new ArrayList<String>());
                                        }
                                        columnValues.get(fieldsGroupItemName).add(resultContent);
                                    }
                                }
                            }
                            // Если пользовательских полей в контенте группы не было, то контент не добавится
                            // Поэтому создаём ячейки с контентом без пользовательских полей
                            if (!columnValues.containsKey(fieldsGroupItemName)) {
                                columnWithoutFieldsValues.put(fieldsGroupItemName, fieldsGroupItemContent);
                            }
                        }

                        // Посчитаем количество строк в таблице
                        int countRow = 0;
                        for (String columnName : columnValues.keySet()) {
                            countRow = columnValues.get(columnName).size();
                            break;
                        }

                        StringBuilder sbValues = new StringBuilder("");
                        StringBuilder sbHead = new StringBuilder("");
                        Map<Integer, String> rows = new HashMap<>();
                        sbHead.append("<tr>");
                        for (String headValue : headValues) {
                            sbHead.append("<th>").append(headValue).append("</th>");
                            int rowIndex = 0;
                            if (columnValues.containsKey(headValue)) {
                                List<String> columnValueList = columnValues.get(headValue);
                                for (String columnValue : columnValueList) {
                                    if (!rows.containsKey(rowIndex)) {
                                        rows.put(rowIndex, "<td>" + columnValue + "</td>");
                                    } else {
                                        rows.put(rowIndex, rows.get(rowIndex) + "<td>" + columnValue + "</td>");
                                    }
                                    rowIndex++;
                                }
                            } else {
                                String columnValue = columnWithoutFieldsValues.get(headValue);
                                for (int i=0; i<countRow; i++) {
                                    if (!rows.containsKey(rowIndex)) {
                                        rows.put(rowIndex, "<td>" + columnValue + "</td>");
                                    } else {
                                        rows.put(rowIndex, rows.get(rowIndex) + "<td>" + columnValue + "</td>");
                                    }
                                    rowIndex++;
                                }
                            }
                        }
                        sbHead.append("</tr>");

                        for (int i = 0; i<rows.size(); i++) {
                            sbValues.append("<tr>").append(rows.get(i)).append("</tr>");
                        }

                        fieldValues.add("<table class='documentTable'>" + sbHead + sbValues + "</table>");
                    } else if (foundUserField.getType().equals("document")) { // Тип поля - Документ
                        String documentViewType = (String)parameters.get("documentViewType");

                        for (String documentIdStr : documentFieldValues) {
                            Long documentId = VarUtils.getLong(documentIdStr, -1l);
                            DocumentEntity document = documentDao.getById(documentId);
                            if (document != null) {
                                // Полное наименование документа
                                if (documentViewType != null && documentViewType.equals("shortName")) {
                                    // Сокращённое наименование документа
                                    String shortName = document.getShortName() == null || document.getShortName().equals("") ? document.getName() : document.getShortName();
                                    fieldValues.add("<a href='" + document.getLink() + "'>" + shortName + "</a>");
                                } else /*if (documentViewType == null || documentViewType.equals("fullName"))*/ { // По умолчанию заполняется полным именем документа
                                    fieldValues.add("<a href='" + document.getLink() + "'>" + document.getName() + " от " + DateUtils.formatDate(document.getCreateDate(), DateUtils.Format.DATE) + "</a>");
                                }
                            } else {
                                fieldValues.add("");
                            }
                        }
                    }

                    if (fieldValues != null && fieldValues.size() > 0) {
                        String fieldContent = "";
                        if (foundUserField.isList()) {
                            String viewType = userField.getListViewType();
                            if (viewType == null || viewType.equals("table")) { // Отобржаение таблицей по умполчанию
                                StringBuilder sb = new StringBuilder("");
                                sb.append("<table>");
                                for (String documentFieldValue : fieldValues) {
                                    sb.append("<tr><td>").append(documentFieldValue).append("</td></tr>");
                                }
                                sb.append("</table>");
                                fieldContent = sb.toString();
                            } else if (viewType.equals("byComma")) { // Отображение через запятую
                                fieldContent = StringUtils.join(fieldValues, ", ");
                            }
                        } else {
                            if (fieldValues.size() > 0) fieldContent = fieldValues.get(0);
                        }

                        content = content.substring(0, matcher.start()) + fieldContent + content.substring(matcher.end(), content.length());

                        matcher = pattern.matcher(content);
                    }
                }
            }
            //content = content.replaceAll("<[^>]*class=\"mceNonEditable\"[^>]*data-custom-field-name=\"" + userField.getName() + "\"[^>]*>\\[" + participant.getParticipantTemplateTypeName() + ":" + userField.getName() + "[^\\]]*\\]<", "<span>" + fieldContent + "<");
        }
        return content;
    }

    private DocumentUserField getUserFieldFromRawData(String rawData, String templateTypeName) {
        if (!rawData.contains("data-custom-field-type-name")) { // аттрибут пользовательского поля
            return null;
        }

        DocumentUserField userField = new DocumentUserField();

        String fieldTypeName = rawData.replaceAll("[\\s\\S]*data-custom-field-type-name=\"([^\"]*)\"[\\s\\S]*", "$1");
        String fieldName = rawData.replaceAll("[\\s\\S]*data-custom-field-name=\"([^\"]*)\"[\\s\\S]*", "$1");
        String fieldDescription = rawData.replaceAll("[\\s\\S]*data-custom-field-description=\"([^\"]*)\"[\\s\\S]*", "$1");
        String participantType = "";
        if (rawData.contains(" data-participant-type=\"")) {
            participantType = rawData.replaceAll("[\\s\\S]* data-participant-type=\"([^\"]*)\"[\\s\\S]*", "$1");
        }
        String listSizeStr = "";
        if (rawData.contains(" data-list-size=\"")) {
            listSizeStr = rawData.replaceAll("[\\s\\S]* data-list-size=\"([^\"]*)\"[\\s\\S]*", "$1");
        }
        String listViewType = "table";
        if (rawData.contains(" data-list-view-type=\"")) {
            listViewType = rawData.replaceAll("[\\s\\S]* data-list-view-type=\"([^\"]*)\"[\\s\\S]*", "$1");
        }

        userField.setName(fieldName);
        userField.setDescription(fieldDescription);
        userField.setType(fieldTypeName);
        //userField.setParticipantName(participantTemplateTypeName);
        //userField.setParticipantTypeName(participantType);

        // Поле имеет список значений
        if (listSizeStr != null && !listSizeStr.equals("")) {
            int listSize = -1;
            try {
                listSize = Integer.valueOf(listSizeStr);
            } catch (NumberFormatException e) {
                // do nothing
            }
            if (listSize > -1) {
                userField.setList(true);
                userField.setListSize(listSize);
            } else {
                userField.setList(false);
            }
            if (listViewType == null || listViewType.equals("")) {
                listViewType = "table";
            }
            userField.setListViewType(listViewType);
        }
        Map<String, Object> parameters = new HashMap<>();
        if (fieldTypeName.equals("participant")) {// Тип поля - участник
            String participantFieldTemplate = rawData.replaceAll("[\\s\\S]*data-participant-field-template=\"([^\"]*)\"[\\s\\S]*", "$1");
            String customFieldParticipantType = rawData.replaceAll("[\\s\\S]*data-participant-type=\"([^\"]*)\"[\\s\\S]*", "$1");

            parameters.put("participantFieldTemplate", participantFieldTemplate);
            parameters.put("participantType", customFieldParticipantType);

        } else if (fieldTypeName.equals("date")) { // Тип поля дата
            String dateFieldStart = rawData.replaceAll("[\\s\\S]*data-date-start=\"([^\"]*)\"[\\s\\S]*", "$1");
            String dateFieldEnd = rawData.replaceAll("[\\s\\S]*data-date-end=\"([^\"]*)\"[\\s\\S]*", "$1");
            String isWordsValueType = rawData.replaceAll("[\\s\\S]*data-date-is-words-type=\"([^\"]*)\"[\\s\\S]*", "$1");
            parameters.put("dateFieldStart", dateFieldStart);
            parameters.put("dateFieldEnd", dateFieldEnd);
            parameters.put("dateIsWordsType", isWordsValueType);
        } else if (fieldTypeName.equals("string")) { // Тип поля строка
            String stringMask = rawData.replaceAll("[\\s\\S]*data-string-mask=\"([^\"]*)\"[\\s\\S]*", "$1");
            String stringCase = rawData.replaceAll("[\\s\\S]*data-string-case=\"([^\"]*)\"[\\s\\S]*", "$1");
            parameters.put("stringMask", stringMask);
            parameters.put("stringCase", stringCase);
        } else if (fieldTypeName.equals("number")) { // Тип поля число
            String numberFieldStart = rawData.replaceAll("[\\s\\S]*data-number-start=\"([^\"]*)\"[\\s\\S]*", "$1");
            String numberFieldEnd = rawData.replaceAll("[\\s\\S]*data-number-end=\"([^\"]*)\"[\\s\\S]*", "$1");
            String numberIsPrecision = rawData.replaceAll("[\\s\\S]*data-number-is-precision=\"([^\"]*)\"[\\s\\S]*", "$1");
            String numberCountDigitals = rawData.replaceAll("[\\s\\S]*data-number-count-digitals=\"([^\"]*)\"[\\s\\S]*", "$1");
            String isWordsValueType = rawData.replaceAll("[\\s\\S]*data-number-is-words-type=\"([^\"]*)\"[\\s\\S]*", "$1");

            parameters.put("numberFieldStart", numberFieldStart);
            parameters.put("numberFieldEnd", numberFieldEnd);
            parameters.put("numberIsPrecision", numberIsPrecision);
            parameters.put("numberCountDigitals", numberCountDigitals);
            parameters.put("numberIsWordsType", isWordsValueType);
        } else if (fieldTypeName.equals("currency")) { // Тип поля денежный
            String isWordsValueType = rawData.replaceAll("[\\s\\S]*data-currency-is-words-type=\"([^\"]*)\"[\\s\\S]*", "$1");

            parameters.put("currencyIsWordsType", isWordsValueType);
        } else if (fieldTypeName.equals("fieldsGroups")) { // Группы полей
            String fieldsGroupsString = rawData.replaceAll("[\\s\\S]*data-fields-groups=\"([^\"]*)\"[\\s\\S]*", "$1");
            parameters.put("fieldsGroupsString", fieldsGroupsString);
            List<Map<String, String>> groupsFields = (List<Map<String, String>>)getFieldsGroups(fieldsGroupsString);
            List<Map<String, Object>> fieldsGroupsList = new ArrayList<>();
            for (Map<String, String> fieldsGroup : groupsFields) {
                List<DocumentUserField> childUserFields = getDocumentUserFieldsByParticipant(fieldsGroup.get("content"), templateTypeName);

                Map<String, Object> fieldsGroupMap = new HashMap<>();
                fieldsGroupMap.put("name", fieldsGroup.get("name"));
                fieldsGroupMap.put("userFields", childUserFields);
                fieldsGroupsList.add(fieldsGroupMap);
            }
            parameters.put("fieldsGroupsList", fieldsGroupsList);
        } else if (fieldTypeName.equals("document")) { // Документ
            // Вид отображения документа (короткое наименование или полное)
            String documentViewType = rawData.replaceAll("[\\s\\S]*data-document-view-type=\"([^\"]*)\"[\\s\\S]*", "$1");
            parameters.put("documentViewType", documentViewType);
        }
        String fieldPositionStr = rawData.replaceAll("[\\s\\S]*data-position=\"([^\"]*)\"[\\s\\S]*", "$1");
        Long position = VarUtils.getLong(fieldPositionStr, -1l);
        if (position > -1) {
            parameters.put("fieldPosition", position);
        }

        userField.setParameters(parameters);
        return userField;
    }

    /**
     * Декодинг контента группового поля
     * @param fieldsGroupsString
     * @return
     */
    private List getFieldsGroups(String fieldsGroupsString) {
        List fieldsGroups = new ArrayList();
        String json = null;
        try {
            json = URLDecoder.decode(fieldsGroupsString.replaceAll("sqquot;", "'"), "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (json != null) {
            fieldsGroups = new Gson().fromJson(json, List.class);
        }
        return fieldsGroups;
    }

    /**
     * Получить пользовательские поля участника документа
     *
     * @param documentContent
     * @param templateTypeName
     * @return
     */
    public List<DocumentUserField> getDocumentUserFieldsByParticipant(String documentContent, String templateTypeName) {
        // Поля по имени уникальны, если 2 одинаковых имени - то это одно и тоже поле
        Map<String, DocumentUserField> resultMap = new HashMap<>();

        // Наименование участника шаблона
        //String participantTemplateTypeName = participant.getParticipantTemplateTypeName();

        Pattern pattern = Pattern.compile("<[^\\<]*data-participant-name=\"" + Pattern.quote(templateTypeName) + "\"([^\\>]*)>([^\\<]*)<");
        Matcher matcher = pattern.matcher(documentContent);

        // Найдены пользовательские поля, которые ещё не заполнены
        while (matcher.find()) {
            String rawParametersField = matcher.group(1);
            DocumentUserField userField = getUserFieldFromRawData(rawParametersField, templateTypeName);
            if (userField != null) {
                resultMap.put(userField.getName(), userField);
            }
        }
        List<DocumentUserField> result = new ArrayList<>();
        result.addAll(resultMap.values());
        return result;
    }

    /**
     * Получить значение составного пользовательского поля - участник.
     *
     * @param participantType
     * @param participantFieldTemplate
     * @param participantId
     * @return
     */
    private String getParticipantValueFromParameters(String participantType, String participantFieldTemplate, Long participantId) {
        String result = participantFieldTemplate;
        DocumentParticipantSourceDto participant = participantService.createFlowOfDocumentParticipant(participantType, participantId, 1);
        for (ParticipantField participantField : participant.getParticipantFields()) {
            result = result.replaceAll("\\{" + participantField.getInternalName() + "\\}", participantField.getValue());
        }
        return result;
    }

    /**
     * Получить значение пользовательского поля - число
     *
     * @param numberFieldStart
     * @param numberFieldEnd
     * @param numberIsPrecision
     * @param numberCountDigitals
     * @param number
     * @return
     */
    private String getNumberValueFromParameters(long numberFieldStart, long numberFieldEnd, boolean numberIsPrecision, int numberCountDigitals, Number number) {
        String pattern = "###,###.##";
        DecimalFormat numberFormatter = new DecimalFormat(pattern);

        return numberFormatter.format(number).replaceAll(",", ".");
    }

    /**
     * Получить значение пользовательского поля - дата
     *
     * @param dateFieldStart
     * @param dateFieldEnd
     * @param date
     * @return
     */
    private String getDateValueFromParameters(String dateFieldStart, String dateFieldEnd, Date date) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
        return dateFormatter.format(date);
    }

    /**
     * Установить значения пользовательских полей из параметров
     * @param userFields
     * @param createDocumentParameter
     */
    public void setUserFieldsValues(List<DocumentUserField> userFields, CreateDocumentParameter createDocumentParameter) {
        for (DocumentUserField userField : userFields) {
            //
            for (UserFieldValue userFieldValue : createDocumentParameter.getUserFieldValueList()) {
                // Если имя пользовательского поля совпадает с именем из параметра создания документа
                if (userField.getName().equalsIgnoreCase(userFieldValue.getFieldName())) {
                    List<String> fieldValues = new ArrayList<>();
                    switch (userFieldValue.getType()) {
                        case PARTICIPANT: {
                            String participantFieldTemplate = (String) userField.getParameters().get("participantFieldTemplate");
                            String participantType = (String) userField.getParameters().get("participantType");

                            List<Long> participantIds = getLongValueList(userFieldValue.getValues());
                            for (Long participantId : participantIds) {
                                fieldValues.add(String.valueOf(participantId));
                                //fieldValues.add(getParticipantValueFromParameters(participantType, participantFieldTemplate, participantId));
                            }
                            break;
                        }
                        case STRING: {
                            List<String> strings = (List<String>) userFieldValue.getValues();
                            fieldValues.addAll(strings);
                            break;
                        }
                        case NUMBER: {
                            long numberFieldStart = VarUtils.getLong((String) userField.getParameters().get("numberFieldStart"), -1l);
                            long numberFieldEnd = VarUtils.getLong((String) userField.getParameters().get("numberFieldEnd"), -1l);
                            boolean numberIsPrecision = VarUtils.getBool((String) userField.getParameters().get("numberIsPrecision"), false);
                            int numberCountDigitals = VarUtils.getLong((String) userField.getParameters().get("numberCountDigitals"), -1l).intValue();

                            List<Object> numbers = (List<Object>) userFieldValue.getValues();
                            for (Object numberObj : numbers) {
                                Number number = null;
                                if (numberObj instanceof Number) {
                                    number = (Number) numberObj;
                                } else if (numberObj instanceof String) {
                                    number = Long.valueOf((String) numberObj);
                                }
                                //System.err.println(getNumberValueFromParameters(numberFieldStart, numberFieldEnd, numberIsPrecision, numberCountDigitals, number) + "-" + number);
                                fieldValues.add(getNumberValueFromParameters(numberFieldStart, numberFieldEnd, numberIsPrecision, numberCountDigitals, number));
                            }
                            break;
                        }
                        case DATE: {
                            String dateFieldStart = (String) userField.getParameters().get("dateFieldStart");
                            String dateFieldEnd = (String) userField.getParameters().get("dateFieldEnd");

                            List<Date> dates = (List<Date>) userFieldValue.getValues();
                            for (Date date : dates) {
                                fieldValues.add(getDateValueFromParameters(dateFieldStart, dateFieldEnd, date));
                            }
                            break;
                        }
                        case CURRENCY: {
                            List<Object> numbers = (List<Object>) userFieldValue.getValues();
                            for (Object numberObj : numbers) {
                                Number number = null;
                                if (numberObj instanceof Number) {
                                    number = (Number) numberObj;
                                } else if (numberObj instanceof String) {
                                    number = Long.valueOf((String) numberObj);
                                }
                                // Собираем значения денежного поля как число
                                fieldValues.add(String.valueOf(number));
                            }
                            break;
                        }
                        case FIELDS_GROUPS: {
                            List<List<UserFieldsGroup>> userFieldsFroupsList = (List<List<UserFieldsGroup>>) userFieldValue.getValues();
                            userField.getParameters().put("fieldsGroups", userFieldsFroupsList);
                            break;
                        }
                        case DOCUMENT: {
                            List<Long> documentIds = getLongValueList(userFieldValue.getValues());
                            for (Long documentId : documentIds) {
                                // Собираем значения поля документ как ИД документа
                                fieldValues.add(String.valueOf(documentId));
                            }
                            break;
                        }
                    }
                    userField.setDocumentFieldValues(fieldValues);
                }
            }

        }
    }

    private List<Long> getLongValueList(List<? extends Object> values) {
        List<Long> result = new ArrayList<>();
        for (Object value : values) {
            if (value instanceof Long) {
                result.add((Long)value);
            } else if (value instanceof String) {
                Long val = VarUtils.getLong((String)value, null);
                if (val != null) {
                    result.add(val);
                }
            }
        }
        return result;
    }

    /**
     * Документ имеет не заполненные пользовательские поля участника
     *
     * @param document
     * @param participant
     * @return
     */
    public boolean isDocumentHasUserFields(Document document, DocumentParticipant participant) {
        boolean result = false;
        String documentContent = document.getContent();

        // Наименование участника шаблона
        String participantTemplateTypeName = participant.getParticipantTemplateTypeName();
        Pattern pattern = Pattern.compile("[\\s\\S]*data-participant-name=\"" + Pattern.quote(participantTemplateTypeName) + "\"[\\s\\S]*");
        Matcher matcher = pattern.matcher(documentContent);
        // Найдены пользовательские поля, которые ещё не заполнены
        if (matcher.matches()) {
            result = true;
        }
        return result;
    }
}
