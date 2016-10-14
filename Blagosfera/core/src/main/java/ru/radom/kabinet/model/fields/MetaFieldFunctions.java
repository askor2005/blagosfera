package ru.radom.kabinet.model.fields;

/**
 * Список возможных функций для мета полей
 */
public enum MetaFieldFunctions {
	UPPER("UPPER"),
	LOWER("LOWER"),
	SUBSTRING("SUBSTRING");

	private final String name;

	MetaFieldFunctions (String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}