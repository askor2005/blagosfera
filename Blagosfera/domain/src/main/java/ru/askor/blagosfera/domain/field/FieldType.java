package ru.askor.blagosfera.domain.field;

public enum FieldType {
	TEXT, //0
	DATE, //1
	SELECT, //2
	COUNTRY, //3
	REGION, //4
	DISTRICT, //5
	CITY, //6
	STREET, //7
	BUILDING, //8
	SUBHOUSE, //9
	MULTILINE_TEXT, //10
	LANDLINE_PHONE, //11
	LINK, //12
	SKYPE, //13
	MOBILE_PHONE, //14
	GEO_POSITION, //15
	GEO_LOCATION, //16
	TIMETABLE, //17
	SHARER, //18
	SYSTEM, // 19
	PARTICIPANTS_LIST, //20
	PARTICIPANTS_LIST_COMMUNITY, //21
	IMAGE, //22 Картинка
	NUMBER, // 23 число
	HTML_TEXT, // 24 поле с содержимым - html
	CURRENCY, // 25 денежный тип поля
	ADDRESS_FIELD_DESCRIPTION, // 26 Расшифровка поля адреса (улица, проспект)
	UNIVERSAL_LIST, // 27 Поле с универсальным списокм
	DATE_RANGE, // 28 Диапазон дат
	HIDDEN_TEXT, // 29 Скрытый текст
	SYSTEM_IMAGE, // 30 Картинка, которая добавляется из нестандартного источника
	RU_TEXT, // 31
	EN_TEXT, // 32
	MAIL // 33
}