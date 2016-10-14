package ru.radom.kabinet.model;

import org.hibernate.Hibernate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * 
 * Базовый класс для всех классов модели. Определяет поле id, являющееся
 * первичным ключом.
 * 
 * @author rkorablin
 *
 */

@MappedSuperclass
public class LongIdentifiable {

	@Id
	@GeneratedValue(generator = "per_table_sequence_generator")
	@GenericGenerator(name = "per_table_sequence_generator", strategy = "ru.radom.kabinet.hibernate.PerTableSequenceGenerator")
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		return ((getId() == null) ? super.hashCode() : getId().hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof LongIdentifiable)) {
			return false;
		}
		if (obj == null || Hibernate.getClass(getClass()) != Hibernate.getClass(obj.getClass())) {
			return false;
		}
		LongIdentifiable other = (LongIdentifiable) obj;
		return getId() != null && other.getId() != null && getId().equals(other.getId());
	}

}
