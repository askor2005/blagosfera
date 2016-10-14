package ru.radom.kabinet.hibernate;

import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.id.SequenceGenerator;
import org.hibernate.type.Type;

import java.util.Properties;

/**
 * 
 * Класс генератора последовательностей в базе данных. Создает новую
 * последовательность для каждой сущности.
 * 
 * @author rkorablin
 *
 */

public class PerTableSequenceGenerator extends SequenceGenerator {

	@Override
	public void configure(Type type, Properties params, Dialect dialect) throws MappingException {
		final String tableName = params.getProperty("target_table");
		final String sequenceName = "SEQ_" + tableName;
		params.setProperty("sequence", sequenceName);
		super.configure(type, params, dialect);
	}

}
