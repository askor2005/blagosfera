package ru.radom.kabinet.document.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.el.ExpressionFactoryImpl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import padeg.lib.Padeg;
import ru.askor.blagosfera.domain.document.ParticipantField;
import ru.askor.blagosfera.domain.field.FieldType;
import ru.askor.blagosfera.domain.listEditor.ListEditorItem;
import ru.askor.voting.domain.BatchVoting;
import ru.radom.kabinet.document.dto.DocumentParticipantSourceDto;
import ru.radom.kabinet.expressions.Functions;
import ru.radom.kabinet.module.rameralisteditor.service.ListEditorItemDomainService;
import ru.radom.kabinet.services.EmailTemplateContext;
import ru.radom.kabinet.utils.*;

import javax.el.ExpressionFactory;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Created by ebelyaev on 03.08.2015.
 */
public class ParticipantFieldParser {
    // Имя поля - дата создания документа
    private static final String DATE_CREATE_DOCUMENT_FIELD_NAME = "DATE_CREATE_DOCUMENT";

    // Имя поля - дата последнего подписания документа
    private static final String DATE_LAST_SIGN_DOCUMENT_FIELD_NAME = "DATE_LAST_SIGN_DOCUMENT";

    // Имя поля Email участника
    private static final String PERSON_EMAIL_FIELD_NAME = "PERSON_EMAIL";

    // Фамилия ИО
    private static final String PERSON_LAST_NAME_WITH_INITIALS_FIELD_NAME = "PERSON_LAST_NAME_WITH_INITIALS";

    // ФИО
    private static final String PERSON_FULL_NAME_FIELD_NAME = "PERSON_FULL_NAME";

    // Класс спана для подписания документа
    private static final String SIGN_PARTICIPANT_SPAN_CLASS_ATTR_NAME = "participant_signature";

    // Класс спана для даты подписания документа
    private static final String DATE_SIGN_PARTICIPANT_SPAN_CLASS_ATTR_NAME = "participant_signature_date";

    // Аттрибут спана с ИД участника для подписания документа
    private static final String SIGN_PARTICIPANT_ID_SPAN_ATTR_NAME = "data-participant_id";

    // Наименования полей ФИО
    private static final String LAST_NAME_FIELD_INTERNAL_NAME = "LASTNAME";
    private static final String FIRST_NAME_FIELD_INTERNAL_NAME = "FIRSTNAME";
    private static final String SECOND_NAME_FIELD_INTERNAL_NAME = "SECONDNAME";

    // Наименование поля Пол
    private static final String GENDER_FIELD_INTERNAL_NAME = "GENDER";
    private static final String GENDER_FIELD_MALE_VALUE = "Мужской";

    // Тип спана. Берётся значение аттрибута data-span-type у спана
    private static final String PARTICIPANT_FILTER_FIELD_DATA_SPAN_TYPE = "radom-participant-filter"; // участник - поле
    private static final String PARTICIPANT_CUSTOM_FIELDS_FIELD_DATA_SPAN_TYPE = "radom-participant-custom-fields"; // пользовательское поле
    private static final String PARTICIPANT_CUSTOM_TEXT_FIELD_DATA_SPAN_TYPE = "participant-custom-text"; // поле с указанием пола
    private static final String PARTICIPANT_SYSTEM_FIELD_DATA_SPAN_TYPE = "radom-system-fields"; // системное поле

    // Аттрибут - дата прописью
    private static final String DATE_FIELD_IS_WORD_ATTR_NAME = "data-object-date-is-word";
    // Аттрибут - число прописью
    private static final String NUMBER_FIELD_IS_WORD_ATTR_NAME = "data-object-number-is-word";
    // Аттрибут - сумма прописью
    private static final String CURRENCY_FIELD_IS_WORD_ATTR_NAME = "data-object-currency-is-word";
    // Аттрибут - тип валюты
    private static final String CURRENCY_FIELD_TYPE_ATTR_NAME = "data-object-currency-type";

    // Аттрибут - дата прописью для системных полей
    private static final String SYSTEM_DATE_FIELD_IS_WORD_ATTR_NAME = "data-object-system-date-is-word";

    private ListEditorItemDomainService listEditorItemDomainService;

    // мапа соответствий (fieldId -> internalName) для парсина текущего участника. (см. метод fillFieldIdToInternalNameMap())
    private Map<String, String> fieldIdToInternalName = new HashMap<>();

    // Наименование участника шаблона
    private String participantName;

    // Список реальных участников по участнику шаблона для которых всё будет парситься
    private List<DocumentParticipantSourceDto> participants;

    // Мапа со всеми участниками шаблона
    private Map<String, List<DocumentParticipantSourceDto>> allTemplateParticipants;

    private ExpressionFactory expressionFactory = new ExpressionFactoryImpl();

    private ParticipantsFieldParserUtils parserUtils;

    public ParticipantFieldParser(String participantName, List<DocumentParticipantSourceDto> participants, ListEditorItemDomainService listEditorItemDomainService, Map<String, List<DocumentParticipantSourceDto>> allTemplateParticipants) {
        this.participantName = participantName;
        this.participants = participants;
        this.allTemplateParticipants = allTemplateParticipants;
        this.listEditorItemDomainService = listEditorItemDomainService;
        this.parserUtils = new ParticipantsFieldParserUtils();
        for (DocumentParticipantSourceDto participant : participants) {
            fillFieldIdToInternalNameMap(participant);
        }
    }

    /**
     * Заполняем мапу соответствий (fieldId -> internalName) fieldIdToInternalName для участника participant
     *
     * @param participant
     */
    private void fillFieldIdToInternalNameMap(DocumentParticipantSourceDto participant) {
        fieldIdToInternalName.clear();
        for (ParticipantField field : participant.getParticipantFields()) {
            fieldIdToInternalName.put(String.valueOf(field.getId()), field.getInternalName());
        }
    }

    /**
     * Возвращает internal_name поля для заданного id поля(fieldIdString) по мапе fieldIdToInternalName.
     *
     * @param fieldIdString id поля.
     * @return internalName или пустая строка.
     */
    private String getInternalNameById(String fieldIdString) {
        if (fieldIdToInternalName.containsKey(fieldIdString)) {
            return fieldIdToInternalName.get(fieldIdString);
        }
        return "";
    }

    /**
     * Возвращает пол участника документа.
     *
     * @param span   изначальный спан с аттрибутами.
     * @param fields список полей, принадлежащих одному человеку(человек != участник, т.к. участник в своих полях может иметь много sharer-людей).
     * @return true - муж., false - жен.
     */
    private boolean getSexFromParticipantFields(@SuppressWarnings("unused") Element span, List<ParticipantField> fields) {
        String internalName = GENDER_FIELD_INTERNAL_NAME;
        return getSafeValue(getSpecifiedField(internalName, fields)).equals(GENDER_FIELD_MALE_VALUE);
    }

    /**
     * Возвращает имя участника шаблона из текста типа [имя:....].
     * Например [part1:sdfsdf:csa] вернёт part1.
     *
     * @param text строка типа [имя:....].
     * @return имя участника шаблона.
     */
    private String getParticipantTemplateTypeName(String text) {
        int firstIndex = text.indexOf("[");
        String substring = text.substring(firstIndex + 1);
        int lastIndex = substring.indexOf(":");
        if (firstIndex < 0 || lastIndex < 0 || firstIndex == lastIndex) return "";
        return substring.substring(0, lastIndex);
    }

    /**
     * Возвращает имя группы участника шаблона из текста типа [...:имя:...].
     * Например [part1:sdfsdf:csa] вернёт sdfsdf.
     *
     * @param text строка типа [...:имя:...].
     * @return имя участника шаблона.
     */
    private String getParticipantTemplateTypeGroupName(String text) {
        int firstIndex = text.indexOf(":");
        int lastIndex = text.indexOf(":", firstIndex + 1);
        if (firstIndex < 0 || lastIndex < 0 || firstIndex == lastIndex) return "";
        return text.substring(firstIndex + 1, lastIndex);
    }

    /**
     * Возвращает индекс падежа на основании атрибута "data-case-id".
     * Если аттрибут отсутствует или пуст, то возвращает 1 - именительный падеж по умолчанию.
     *
     * @param span изначальный спан с аттрибутами.
     * @return индекс падежа.
     */
    private int getPadegIndexFromSpan(Element span) {
        if (span.hasAttr("data-case-id")) {
            String padegKey = span.attr("data-case-id"); // ключ падежа
            return PadegConstants.PADEGES_MAP.get(padegKey); // индекс падежа
        }
        return 1; // именительный падеж по умолчанию
    }

    /**
     * Возвращает установленный у поля регистр символов
     * @param span изначальный спан с атрибутами
     * @return вид отображения текста
     */
    private FieldCharsType getSpanCharsType(Element span) {
        FieldCharsType result = FieldCharsType.NORMAL;
        if (span.hasAttr("data-chars-type")) {
            String charsType = span.attr("data-chars-type");
            if (charsType.equals(FieldCharsType.UPPERCASE.name())) {
                result = FieldCharsType.UPPERCASE;
            } else if (charsType.equals(FieldCharsType.LOWERCASE.name())) {
                result = FieldCharsType.LOWERCASE;
            } else if (charsType.equals(FieldCharsType.NORMAL.name())) {
                result = FieldCharsType.NORMAL;
            }
        }
        return result;
    }

    /**
     * Возвращает internal_name поля для спана.
     *
     * @param span изначальный спан с аттрибутами.
     * @return internal_name или пустая строка.
     */
    private String getInternalNameFromSpan(Element span) {
        // Если в спане уже есть непустое internal_name, то возвращаем его
        String internalName = span.attr("data-internal-name");
        if (!StringUtils.isBlank(internalName)) {
            return internalName;
        }

        // Если в спане уже есть непустое system_internal_name, то возвращаем его
        String systemInternalName = span.attr("data-system-field-internal-name");
        if (!StringUtils.isBlank(systemInternalName)) {
            return systemInternalName;
        }

        // Если в спане есть непустой field_id, то определяем по нему internal_name и возвращаем его
        String fieldIdString = span.attr("data-field-id");
        if (!StringUtils.isBlank(fieldIdString)) {
            return getInternalNameById(fieldIdString);
        }

        // Если у спана нет ни internal_name ни field_id, то возвращаем пустую строку
        return "";
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * Возвращает поле из списка полей.
     *
     * @param internalName имя поля(не пустая строка).
     * @param fields       список полей.
     * @return поле или null.
     */
    private ParticipantField getSpecifiedField(String internalName, List<ParticipantField> fields) {
        if (!StringUtils.isBlank(internalName) && fields != null) {
            for (ParticipantField participantField : fields) {
                if (StringUtils.equalsIgnoreCase(participantField.getInternalName(), internalName)) {
                    participantField.setUsedInDocument(true);
                    return participantField;
                }
            }
        }
        return null;
    }

    /**
     * Возвращает поля из списка полей.
     *
     * @param internalName имя поля(не пустая строка, иначе вернёт пустой список).
     * @param fields       список полей.
     * @return список полей или пустой список.
     */
    private List<ParticipantField> getSpecifiedFields(String internalName, List<ParticipantField> fields) {
        List<ParticipantField> values = new ArrayList<>();
        if (!StringUtils.isBlank(internalName) && fields != null) {
            fields.stream()
                    .filter(participantField -> StringUtils.equalsIgnoreCase(participantField.getInternalName(), internalName))
                    .forEach(participantField -> {
                        participantField.setUsedInDocument(true);
                        values.add(participantField);
            });
        }
        return values;
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------


    /**
     * Безопасно возвращает значение поля.
     *
     * @param field поле.
     * @return значение или пустая строка.
     */
    private String getSafeValue(ParticipantField field) {
        if (field != null && field.getValue() != null) {
            return field.getValue();
        }
        return "";
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * Возвращает список значений для спана(Для полей типа лист).
     * // значения отсортированны по sharer_lik, а это значит, что для разных людей
     * // они будут идти в одинаковых(соответствующих) порядках. это нужно для групповых полей.
     *
     * @param span        изначальный спан с аттрибутами.
     * @param participant участник документа.
     * @param isWithGroup флаг - поле с группой
     * @return список значений(Например: для спана с членами правления - Иванов, Петров, Сидоров...).
     */
    private List<ParticipantSpanValue> getParticipantListValues(Element span, DocumentParticipantSourceDto participant, boolean isWithGroup) {
        String internalName = getInternalNameFromSpan(span);
        String groupName = getParticipantTemplateTypeGroupName(span.text());

        List<ParticipantSpanValue> preparedValues = new ArrayList<>(); // список подготовленных значений...
        // Если есть группировка, то это всегда поля дочерних участников
        if (isWithGroup && participant.getChildMap().containsKey(groupName)) {
            List<DocumentParticipantSourceDto> groupedParticipants = participant.getChildMap().get(groupName);
            for (DocumentParticipantSourceDto groupedParticipant : groupedParticipants) {
                ParticipantField field = getSpecifiedField(internalName, groupedParticipant.getParticipantFields());
                String value = getSafeValue(field);
                preparedValues.add(new ParticipantSpanValue(groupedParticipant, prepareValue(value, span, groupedParticipant.getParticipantFields())));
                //preparedValues.add(prepareValue(value, span, groupedParticipant.getParticipantFields()));
            }
        } else {
            ParticipantField field = getSpecifiedField(internalName, participant.getParticipantFields());
            String value = getSafeValue(field);
            preparedValues.add(new ParticipantSpanValue(participant, prepareValue(value, span, participant.getParticipantFields())));
            //preparedValues.add(prepareValue(value, span, participant.getParticipantFields()));
        }
        return preparedValues;
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * Подготавливает одно(не список) значение(например склоняет ФИО, склоняет Имя или что-то ещё).
     *
     * @param value  значение которое нужно подготовить(например склонить ФИО).
     * @param span   изначальный спан с аттрибутами.
     * @param fields список полей, принадлежащих одному человеку(человек != участник, т.к. участник в своих полях может иметь много sharer-людей).
     * @return сформированное значение.
     */
    private String prepareValue(String value, Element span, List<ParticipantField> fields) {
        String result = value;
        String internalName = getInternalNameFromSpan(span);

        // Значения, требующие особой обработки
        switch (internalName) {
            case LAST_NAME_FIELD_INTERNAL_NAME: {
                boolean sex = getSexFromParticipantFields(span, fields);
                result = Padeg.getFIOPadeg(value, null, null, sex, getPadegIndexFromSpan(span));
                break;
            }

            case FIRST_NAME_FIELD_INTERNAL_NAME: {
                boolean sex = getSexFromParticipantFields(span, fields);
                result = Padeg.getFIOPadeg(null, value, null, sex, getPadegIndexFromSpan(span));
                break;
            }

            case SECOND_NAME_FIELD_INTERNAL_NAME: {
                boolean sex = getSexFromParticipantFields(span, fields);
                result = Padeg.getFIOPadeg(null, null, value, sex, getPadegIndexFromSpan(span));
                break;
            }

            case PERSON_LAST_NAME_WITH_INITIALS_FIELD_NAME: {
                boolean sex = getSexFromParticipantFields(span, fields);
                String[] nameParameters = value.split(" ");
                String lastName = nameParameters.length > 0 ? nameParameters[0] : ""; // Фамилия
                String initials = value.replaceAll(lastName, ""); // инициалы
                result = Padeg.getFIOPadeg(lastName, null, null, sex, getPadegIndexFromSpan(span)) + " " + initials; // результат склонения
                break;
            }

            case PERSON_FULL_NAME_FIELD_NAME: {
                boolean sex = getSexFromParticipantFields(span, fields);
                String[] nameParameters = value.split(" ");
                String lastName = nameParameters.length > 0 ? nameParameters[0] : ""; // Фамилия
                String firstName = nameParameters.length > 1 ? nameParameters[1] : ""; // Имя
                String secondName = nameParameters.length > 2 ? nameParameters[2] : ""; // Отчество
                result = Padeg.getFIOPadeg(lastName, firstName, secondName, sex, getPadegIndexFromSpan(span)); // результат склонения
                break;
            }

            default : {
                int padegIndex = getPadegIndexFromSpan(span);
                if (padegIndex > 1) {
                    result = Padeg.getOfficePadeg(value, padegIndex);
                }
            }
        }
        FieldCharsType fieldCharsType = getSpanCharsType(span);
        switch (fieldCharsType) {
            case UPPERCASE:
                result = result.toUpperCase();
                break;
            case LOWERCASE:
                result = result.toLowerCase();
                break;
        }

        // Если поле - дата с прописью
        String dateIsWord = span.attr(DATE_FIELD_IS_WORD_ATTR_NAME);
        if (!StringUtils.isEmpty(dateIsWord) && Boolean.valueOf(dateIsWord)) {
            result = HumansStringUtils.date2string(result);
        }
        // Число прописью
        String numberIsWord = span.attr(NUMBER_FIELD_IS_WORD_ATTR_NAME);
        if (!StringUtils.isEmpty(numberIsWord) && Boolean.valueOf(numberIsWord)) {
            if (result != null && !result.equals("")) {
                result = HumansStringUtils.number2string(result);
            }
        }
        // Сумма прописью
        String currencyIsWord = span.attr(CURRENCY_FIELD_IS_WORD_ATTR_NAME);
        String currencyTypeIdStr = span.attr(CURRENCY_FIELD_TYPE_ATTR_NAME);
        if (!StringUtils.isEmpty(currencyIsWord) && Boolean.valueOf(currencyIsWord)) {
            Long currencyTypeId = VarUtils.getLong(currencyTypeIdStr, -1l);
            ListEditorItem listEditorItem = listEditorItemDomainService.getById(currencyTypeId);
            String currencyTypeCode = null;
            if (listEditorItem != null) {
                currencyTypeCode = listEditorItem.getCode();
            }
            if (result != null && !result.equals("")) {
                result = HumansStringUtils.money2string(result, currencyTypeCode);
            }
        }

        // Если значение не требует особой обработки, то возвращаем его как есть
        return result;
    }

    /**
     * Подготавливает элемент для значения.
     * Для обычного текста возвращает соответствующий <span></span>, для
     * имейла соответсвующий <a><a/> и тд.
     *
     * @param value значение.
     * @param span  изначальный спан с аттрибутами.
     * @param fieldType
     * @return сформированный элемент.
     */
    private Element prepareElement(String value, Element span, FieldType fieldType) {
        String internalName = getInternalNameFromSpan(span);
        Element result = null;
        boolean isSimpleField = true;
        if (fieldType != null) {
            switch (fieldType) {
                case SYSTEM_IMAGE:
                    isSimpleField = false;
                    int imageSize = VarUtils.getInt(span.attr("data-system-image-size"), 254);
                    String imageFloat = span.attr("data-system-image-float");
                    String imageStyle = "";
                    if (!StringUtils.isBlank(imageFloat)) {
                        if ("left".equals(imageFloat)) {
                            imageStyle = "margin: 5px 5px 5px 0px; ";
                        } else if ("right".equals(imageFloat)) {
                            imageStyle = "margin: 5px 0px 5px 5px; ";
                        }
                        imageStyle += "float: " + imageFloat + ";";
                    }
                    Element imgElement = new Element(Tag.valueOf("img"), "");
                    imgElement.attr("style", imageStyle);
                    imgElement.attr("src", Functions.resizeImage(value, "c" + imageSize));
                    result = imgElement;
                    break;
            }
        }
        if (isSimpleField) {
            switch (internalName) {
                case PERSON_EMAIL_FIELD_NAME:
                    @SuppressWarnings("redundant")
                    String email = value;  // value типа <a href='mailto:***@yandex.ru'>***@yandex.ru</a>
                    Document doc = Jsoup.parse(email);
                    Element aElement = doc.select("a").first();
                    if (aElement == null) {
                        aElement = new Element(Tag.valueOf("a"), "");
                        aElement.attr("href", "mailto:" + value);
                        aElement.appendText(value);
                    }
                    result = aElement.clone();
                    break;

                case FieldConstants.PERSON_SYSTEM_SIGNATURE_FIELD_NAME: // Подпись документа
                    Element spanElement = new Element(Tag.valueOf("span"), "");
                    spanElement.attr("class", SIGN_PARTICIPANT_SPAN_CLASS_ATTR_NAME);
                    spanElement.attr(SIGN_PARTICIPANT_ID_SPAN_ATTR_NAME, value);
                    spanElement.appendText("__________");
                    result = spanElement;
                    break;

                case FieldConstants.PERSON_DATE_SYSTEM_SIGNATURE_FIELD_NAME:
                    spanElement = new Element(Tag.valueOf("span"), "");
                    spanElement.attr("class", DATE_SIGN_PARTICIPANT_SPAN_CLASS_ATTR_NAME);
                    spanElement.attr(SIGN_PARTICIPANT_ID_SPAN_ATTR_NAME, value);
                    spanElement.appendText("__.__.____ __:__");
                    result = spanElement;
                    break;

                default:
                    // Обработка полей с email и ссылок
                    if (internalName != null && !internalName.equals("") && value != null && !value.equals("")) {
                        String href = null;
                        String linkValue = null;
                        if (internalName.toLowerCase().contains("www")) { // Если имя поля содержит www, то это ссылка
                            if (!value.toLowerCase().contains("http://") && !value.toLowerCase().contains("https://")) {
                                href = "http://" + value;
                            }
                            linkValue = href;
                        } else if (internalName.toLowerCase().contains("email")) { // Если имя поля содержит email, то это электронная почта
                            href = "mailto:" + value;
                            linkValue = value;
                        }
                        if (linkValue != null && href != null) {
                            aElement = new Element(Tag.valueOf("a"), "");
                            aElement.attr("href", href);
                            aElement.appendText(linkValue);
                            result = aElement;
                        }
                    }

                    if (result == null) {
                        spanElement = new Element(Tag.valueOf("span"), "");
                        spanElement.append(value);
                        result = spanElement;
                    }
                    break;
            }
        }
        return result;
    }

    /**
     * Возвращает имеет ли поле не пустой аттрибут группы.
     *
     * @param span изначальный спан с аттрибутами.
     * @return true - поле является списком, false - поле не является списком.
     */
    private static boolean isListField(Element span) {
        return !StringUtils.isBlank(span.attr("data-group-internal-name"));
    }

    /**
     * Формирует новый элемент [Участник:Поле] на основании данных участника и данных аттрибутов старого спана.
     *
     * @param span        изначальный спан с аттрибутами.
     * @param participants участники.
     * @return сформированный элемент.
     */
    private Element createParticipantFilterElement(Element span, List<DocumentParticipantSourceDto> participants) {
        List<String> values = new ArrayList<>();
        for (DocumentParticipantSourceDto participant : participants) {
            List<ParticipantSpanValue> participantSpanValues = getParticipantListValues(span, participant, isListField(span));
            for (ParticipantSpanValue participantSpanValue : participantSpanValues) {
                values.add(participantSpanValue.getValue());
            }
            //values.addAll(getParticipantListValues(span, participant, isListField(span)));
        }
        // Создать спан для каждого значения, подготовить каждый спан, склеить спаны через запятую
        Element parentSpan = new Element(Tag.valueOf("span"), "");
        List<String> spanTextValues = new ArrayList<>();
        String internalName = getInternalNameFromSpan(span);
        ParticipantField field = getSpecifiedField(internalName, participants.get(0).getParticipantFields());
        FieldType fieldType = field == null ? FieldType.TEXT : field.getFieldType();
        for (String value : values) {
            Element spanElement = prepareElement(value, span, fieldType);
            spanTextValues.add(spanElement.outerHtml());
        }
        String result = StringUtils.join(spanTextValues, ", ");
        parentSpan.html(result);
        return parentSpan;
    }

    /**
     * Формирует новый элемент пользовательского поля на основании данных участника и данных аттрибутов старого спана.
     * <p/>
     * // Метод ничего не делает, просто возвращает первый параметр span не тронутым, никак его не обрабатывая.(return span.clone()).
     * // Пользовательские поля парсятся в другом месте при сохранении документа.
     *
     * @param span        изначальный спан с аттрибутами.
     * @param participant участник документа.
     * @return сформированный элемент.
     */
    private Element createParticipantCustomFieldsElement(Element span, @SuppressWarnings("unused") DocumentParticipantSourceDto participant) {
        return span.clone();
    }

    /**
     * Формирует новый элемент поля с указанием пола на основании данных участника и данных аттрибутов старого спана.
     *
     * @param span        изначальный спан с аттрибутами.
     * @param participant участник документа.
     * @return сформированный элемент
     */
    private Element createParticipantCustomTextElement(Element span, DocumentParticipantSourceDto participant) {
        Element spanNew = new Element(Tag.valueOf("span"), "");

        String maleText = span.attr("data-custom-text-male");
        String femaleText = span.attr("data-custom-text-female");

        boolean sex = getSexFromParticipantFields(span, participant.getParticipantFields());
        if (sex) {
            spanNew.appendText(maleText);
        } else {
            spanNew.appendText(femaleText);
        }

        return spanNew;
    }

    // Возвращает значение системного поля, обработанное на основе параметров поля
    private String getSystemFieldValue(Element span, String value) {
        // Если поле - дата с прописью
        String dateIsWord = span.attr(SYSTEM_DATE_FIELD_IS_WORD_ATTR_NAME);
        if (!StringUtils.isEmpty(dateIsWord) && Boolean.valueOf(dateIsWord)) {
            value = HumansStringUtils.date2string(value);
        }
        return value;
    }

    /**
     * Формирует новый элемент системного поля на основании данных участника и данных аттрибутов старого спана.
     *
     * @param span        изначальный спан с аттрибутами.
     * @param participant участник документа.
     * @return сформированный элемент.
     */
    private Element createParticipantSystemElement(Element span, DocumentParticipantSourceDto participant) {
        Element spanNew = new Element(Tag.valueOf("span"), "");

        String internalName = getInternalNameFromSpan(span);
        switch (internalName) {
            // Поля, требующие особой обработки
            case DATE_LAST_SIGN_DOCUMENT_FIELD_NAME: {
                String value = getSafeValue(getSpecifiedField(internalName, participant.getParticipantFields()));
                if (StringUtils.isBlank(value)) {
                    spanNew = span.clone(); // не трогаем изначальный спан
                } else {
                    spanNew.appendText(getSystemFieldValue(span, value));
                }
                break;
            }
            case DATE_CREATE_DOCUMENT_FIELD_NAME: { // Дата создания документа
                ParticipantField participantField = getSpecifiedField(internalName, participant.getParticipantFields());
                if (participantField == null) {
                    spanNew = span.clone();
                } else {
                    String value = getSafeValue(participantField);
                    spanNew.appendText(getSystemFieldValue(span, value));
                }
                break;
            }

            // все остальные поля
            default: {
                ParticipantField participantField = getSpecifiedField(internalName, participant.getParticipantFields());
                if (participantField == null) {
                    spanNew = span.clone();
                } else {
                    String value = getSafeValue(participantField);
                    spanNew.appendText(value);
                }
                break;
            }
        }

        return spanNew; // TODO при формировании значения и элемента использовать методы prepareValue и prepareElement
    }

    /**
     * Формирует новый элемент на основании данных участника и данных аттрибутов старого спана.
     * <p/>
     * Пример:
     * вход:
     * span = <span class="mceNonEditable" data-placeholder="" data-span-type="radom-participant-filter" data-span-id="1438246418501" data-is-meta-field="false" data-participant-id="152" data-field-id="251" data-internal-name="PERSON_LIK" data-mce-contenteditable="false">[part1:ЛИК пользователя]</span>
     * participant = ...
     * выход:
     * <span>15945752815407099733</span>
     * <p/>
     * Если span не имеет аттрибута "data-span-type", то он возвращается без изменений (считается что он уже обработан).
     *
     * @param span        изначальный спан с аттрибутами.
     * @param participants участник документа.
     * @return сформированный элемент.
     */
    private Element createElement(Element span, List<DocumentParticipantSourceDto> participants) {
        String dataSpanType = span.attr("data-span-type");
        switch (dataSpanType) {
            case PARTICIPANT_FILTER_FIELD_DATA_SPAN_TYPE: // участник - поле
                return createParticipantFilterElement(span, participants);

            case PARTICIPANT_CUSTOM_FIELDS_FIELD_DATA_SPAN_TYPE: // пользовательское поле
                return createParticipantCustomFieldsElement(span, participants.get(0));

            case PARTICIPANT_CUSTOM_TEXT_FIELD_DATA_SPAN_TYPE: // поле с указанием пола
                return createParticipantCustomTextElement(span, participants.get(0));

            case PARTICIPANT_SYSTEM_FIELD_DATA_SPAN_TYPE: // системное поле
                return createParticipantSystemElement(span, participants.get(0));

            default:
                return span.clone();
        }
    }


    /**
     * Парсит группу. Возвращаем список заполненных групповых шаблонов:
     * вход:    "<span ...>[...]</span> ( <span ...>[...]</span> )"
     * выход:   "<span>Иванов</span> ( <a>ivanov@mail.ru</a> )",
     * "<span>...</span> ( <a>...</a> )",
     * "<span>...</span> ( <a>...</a> )"...
     *
     * @param groupTemplate шаблон группы.
     * @param participants  реальные участники.
     * @return список готовых значений по групповому полю.
     */
    private List<String> parseGroup(String groupTemplate, List<DocumentParticipantSourceDto> participants) {
        List<String> subContents = new ArrayList<>();

        int valuesSize = 0; // количество значений в групповом поле.(например, количество членов совета, членов правления и тд)
        List<MutableTriple<Element, Element, List<ParticipantSpanValue>>> spanValues = new ArrayList<>(); // список триплов: поле для замены - оригинальное поле(со всеми аттрибутами) - список значений. // мидл-значение уже нигде не нужно

        Document doc = Jsoup.parse(groupTemplate);
        Elements spans = doc.select("span");
        for (Element span : spans) {
            List<ParticipantSpanValue> values = new ArrayList<>();
            for (DocumentParticipantSourceDto participant : participants) {
                values.addAll(getParticipantListValues(span, participant, isListField(span)));
            }

            valuesSize = Math.max(valuesSize, values.size());

            Element emptySpan = new Element(Tag.valueOf("span"), "");

            MutableTriple<Element, Element, List<ParticipantSpanValue>> triple = new MutableTriple<>(emptySpan, span, values);
            spanValues.add(triple);

            span.replaceWith(emptySpan);
        }

        for (int i = 0; i < valuesSize; i++) {
            ParticipantSpanValue foundParticipantSpanValue = null;
            for (MutableTriple<Element, Element, List<ParticipantSpanValue>> p : spanValues) {
                Element spanOriginal = p.getMiddle();
                Element span = p.getLeft();
                List<ParticipantSpanValue> participantSpanValues = p.getRight();

                ParticipantSpanValue participantSpanValue = (i < participantSpanValues.size()) ? participantSpanValues.get(i) : null;
                if (foundParticipantSpanValue == null) {
                    foundParticipantSpanValue = participantSpanValue;
                }
                List<ParticipantField> fields = participantSpanValue.getParticipant() == null ? null : participantSpanValue.getParticipant().getParticipantFields();
                ParticipantField field = getSpecifiedField(getInternalNameFromSpan(span), fields);
                FieldType fieldType = field == null ? null : field.getFieldType();
                String value = participantSpanValue == null ? "" : participantSpanValue.getValue();
                Element element = prepareElement(value, spanOriginal, fieldType); // подготавливаем элемент(<a> или <span> ...)

                span.replaceWith(element); // заменяем старый элемент
                p.setLeft(element); // ставим новый элемент как элемент для дайльнейших замен
            }
            String subContent = doc.body().html();

            // Парсим EL перменные в шаблоне
            Map<String, Object> variables = new HashMap<>();
            DocumentParticipantSourceDto participant = foundParticipantSpanValue != null ? foundParticipantSpanValue.getParticipant() : null;
            variables.put("participant", participant);
            subContent = parseTemplateByEL(subContent, variables);

            subContents.add(subContent);
        }

        return subContents;
    }

    private Map<String, List<DocumentParticipantSourceDto>> getParticipantsMap(List<DocumentParticipantSourceDto> participants) {
        Map<String, List<DocumentParticipantSourceDto>> participantsMap = new HashMap<>();
        for (DocumentParticipantSourceDto participant : participants) {
            if (!participantsMap.containsKey(participant.getName())) {
                participantsMap.put(participant.getName(), new ArrayList<>());
            }
            participantsMap.get(participant.getName()).add(participant);
        }
        return participantsMap;
    }

    /**
     * Проверят относится ли участник к группе.
     * Например:
     * "<span ...>[part2:...]</> - ( <span ...>[part2:...]</> )" для участника part2 вернёт true.
     *
     * @param groupTemplate шаблон группы.
     * @param participantTemplateTypeName   участник документа.
     * @return true - участник должен применяться к шаблону groupTemplate, иначе false
     */
    private boolean isGroupParticipant(String groupTemplate, String participantTemplateTypeName) {
        Document doc = Jsoup.parse(groupTemplate);
        Elements spans = doc.select("span"); // получаем список всех спанов в шаблоне
        for (Element span : spans) {
            // Берём первое попавшееся имя участника
            if (getParticipantTemplateTypeName(span.text()).equals(participantTemplateTypeName)) {
                return true;
            }
            // т.к. берём первое попавшееся имя, то строка содержащая в себе нескольких участников
            // "<span ...>[part2:...]</> - ( <span ...>[part1:...]</> )" для участника part2 тоже вернёт true.
        }

        return false;
    }

    /**
     * Парсит группы.
     *
     * @param template    шаблон документа.
     * @param participantTemplateTypeName Наименование участника документа
     * @param participants список реальных участников
     * @return изначальный элемент, но с уже распарсенными и готовыми групповыми полями(обычные поля не трогает).
     */
    private String parseGroups(String template, String participantTemplateTypeName, List<DocumentParticipantSourceDto> participants) {
        String result = template;

        Pattern pattern = Pattern.compile("<span[^>]*groupFieldStart([\\s\\S]*?)\\[\\[</span>([\\s\\S]*?)<span[^>]*groupFieldEnd[^>]*>\\]\\]</span>"); // может зафейлится от неправильных входных данных
        Matcher matcher = pattern.matcher(result);

        /*Pattern pattern1 = Pattern.compile("<span[^>]*groupFieldStart([\\s\\S]*?)\\[\\[</span>"); // может зафейлится от неправильных входных данных
        Matcher matcher1 = pattern1.matcher(result);
        if (matcher1.find()) {
            System.err.println(matcher1.group(1));
        }*/

        Map<String, String> subContentList = new HashMap<>(); // "<span ...>[...]</span> ( <span ...>[...]</span> )" -> "<span>...</span> ( <a>...</a> ), <span>...</span> ( <a>...</a> ), ..."
        while (matcher.find()) {
            String templateGroup = matcher.group(); //  шаблон группы(включая "[[" и "]]") - "[[ *спаны, текст* ]]" - строка является уникальной, так как у спанов имеются уникальные идентификаторы
            String templateGroupContent = matcher.group(2); // содержимое шаблона группы(то, что между "[[" и "]]"), например "<span ...>[...]</span> ( <span ...>[...]</span> )"
            String groupAttrRaw = matcher.group(1);

            String joinString = ", ";
            // Ищем разделитель между группами
            if (groupAttrRaw.contains("data-join-string=")) {
                Pattern patternJoinString = Pattern.compile("data-join-string=\"([^\"]*)\"");
                Matcher matcherJoinString = patternJoinString.matcher(groupAttrRaw);
                if (matcherJoinString.find()) {
                    joinString = StringEscapeUtils.unescapeHtml4(matcherJoinString.group(1));
                }
            }

            if (isGroupParticipant(templateGroupContent, participantTemplateTypeName)) { // шаблона группы templateGroupContent для участника participant?
                List<String> subContents = parseGroup(templateGroupContent, participants); //  "<span>...</span> ( <a>...</a> )", "<span>...</span> ( <a>...</a> )", ...

                // ставим в соответствии с шаблоном группы заполненную группу
                /*if (subContents.size() > 0 && subContents.get(0).replaceAll("[\\s\\S]*<br>$", "").equals("")) { // Проверяем, что группа полей заканчивается переносом строки - <br>
                    // Склеиваем через запятую до переноса строки
                    List<String> subContentsWithNewLine =
                            subContents.stream()
                                    .map(subContent -> subContent.replaceAll("([\\s\\S]*)<br>$", "$1"))
                                    .collect(Collectors.toList());
                    subContentList.put(templateGroup, StringUtils.join(subContentsWithNewLine, "<br/>"));
                } else {
                    subContentList.put(templateGroup, StringUtils.join(subContents, joinString));
                }*/
                String groupContent;
                if (subContents.size() > 1) {
                    groupContent = StringUtils.join(subContents, joinString);
                } else if (subContents.size() == 1) {
                    groupContent = StringUtils.join(subContents, "");
                } else {
                    groupContent = "";
                }
                subContentList.put(templateGroup, groupContent);

            }
        }

        // Заменяем шаблоны групп на заполненные группы
        for (String templateGroup : subContentList.keySet()) {
            String filledGroup = subContentList.get(templateGroup);
            result = result.replace(templateGroup, filledGroup);
        }

        return result;
    }

    /**
     * Парсит участников.
     *
     * @param template    шаблон с уже распарсенными групповыми полями.
     * @param participantTemplateTypeName
     * @param participants участник документа.
     * @return шаблон с распарсенными участниками.
     */
    private String parseParticipants(String template, String participantTemplateTypeName, List<DocumentParticipantSourceDto> participants) {
        Document doc = Jsoup.parse(template);



        Elements spans = doc.select("span"); // получаем список всех спанов в шаблоне
        //boolean needChangeSourceTemplate = false;
        for (Element span : spans) {
            // Определяем относится ли participant к текущему span'у
            String spanParticipantTemplateTypeName = getParticipantTemplateTypeName(span.text());
            if (spanParticipantTemplateTypeName.equals(participantTemplateTypeName)) {
                // Формируем новый спан на основе текущего
                Element spanNew = createElement(span, participants);
                // Заменяем текущий спан новым
                span.replaceWith(spanNew);
                //needChangeSourceTemplate = true;
            }
        }

        // Делаем замену вложенных парагрофов на вложенные блоки, потому что jsoup делает несколько последовательных парагрофов
        Elements pChild = doc.select("p p");
        for (Element pElement : pChild) {
            Element el = pElement;
            while (!el.parent().tagName().toLowerCase().equals("p")) {
                el = el.parent();
            }
            el.parent().addClass("replaceToDiv");
        }
        pChild = doc.select(".replaceToDiv");
        for (Element pElement : pChild) {
            pElement.tagName("div");
        }

        return doc.body().html();
    }

    public String parseTemplateByEL(String template, Map<String, Object> vars, Map<String, List<DocumentParticipantSourceDto>> participantsMap) {
        // Парсим EL перменные в шаблоне
        Map<String, Object> variables = new HashMap<>();
        if (vars != null) {
            variables.putAll(vars);
        }
        if (participantsMap != null) {
            variables.put("participants", participantsMap);
        }
        variables.put("utils", parserUtils);
        /*BatchVoting batchVoting = parserUtils.testBatchVoting();
        ObjectMapper objectMapper = new ObjectMapper();
        variables.put("batchVoting", objectMapper.convertValue(batchVoting, Map.class));*/
        //variables.put("batchVoting", parserUtils.testBatchVoting());
        EmailTemplateContext elContext = new EmailTemplateContext(variables, expressionFactory);
        String result;
        try {
            result = (String) expressionFactory.createValueExpression(elContext, template, String.class).getValue(elContext);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Произошла ошибка во время выполнения скрипта в шаблоне документа. Текст ошибки: " + e.getMessage());
            result = template;
        }
        return result;
    }

    private String parseTemplateByEL(String template, Map<String, Object> vars) {
        return parseTemplateByEL(template, vars, allTemplateParticipants);
    }

    /**
     * Реализация парсинга шаблона документа.
     *
     * @param template шаблон документа.
     * @param scriptVars дополнительные переменные EL скрипта
     * @return документ.
     */
    public String parseTemplate(String template, Map<String, Object> scriptVars) {
        String result = template;
        result = parseGroups(result, participantName, participants);
        result = parseParticipants(result, participantName, participants);

        // Парсим EL перменные в шаблоне
        result = parseTemplateByEL(result, scriptVars);

        return result;
    }

    public String parseSignFieldValue(String template, Long participantId, String imageSource) {
        Document doc = Jsoup.parse(template);
        doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        Elements spans = doc.select("span"); // получаем список всех спанов в шаблоне
        for (Element span : spans) {
            String participantIdStr = String.valueOf(participantId);
            if (span.attr("class").equals(SIGN_PARTICIPANT_SPAN_CLASS_ATTR_NAME) && span.attr(SIGN_PARTICIPANT_ID_SPAN_ATTR_NAME).equals(participantIdStr)) {
                Element imgElement = new Element(Tag.valueOf("img"), "");
                imgElement.attr("src", imageSource);
                span.replaceWith(imgElement);
            } else if (span.attr("class").equals(DATE_SIGN_PARTICIPANT_SPAN_CLASS_ATTR_NAME) && span.attr(SIGN_PARTICIPANT_ID_SPAN_ATTR_NAME).equals(participantIdStr)) {
                Element spanElement = new Element(Tag.valueOf("span"), "");
                spanElement.text(DateUtils.formatDate(new Date(), DateUtils.Format.DATE_TIME_SHORT));
                span.replaceWith(spanElement);
            }
        }
        return doc.body().html();
    }

    /**
     * Найти поля в документе, которые незаполнены
     * @param content контент
     * @param excludedParticipants наименования участников, которых игнорировать
     * @return true если найдены поля false если не найдены
     */
    public static boolean findFieldsInContent(String content, Set<String> excludedParticipants) {
        boolean result = false;
        Document doc = Jsoup.parse(content);
        Elements spans = doc.select("span"); // получаем список всех спанов в шаблоне
        for (Element span : spans) {
            boolean findNotExcludedField = false;
            for (String excludedParticipant : excludedParticipants) {
                findNotExcludedField = span.text().contains("[" + excludedParticipant);
                if (!findNotExcludedField) {
                    break;
                }
            }

            if (span.hasClass("mceNonEditable") && !span.hasClass("groupFieldStart") && !span.hasClass("groupFieldEnd") && !findNotExcludedField) {
                System.err.println("Незаполненное поле: " + span.html());
                result = true;
                break;
            }
        }
        return result;
    }
}