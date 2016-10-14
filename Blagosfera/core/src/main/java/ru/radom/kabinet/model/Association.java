package ru.radom.kabinet.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "associations")
public class Association extends LongIdentifiable {

	@Column(length = 100)
	@Size(min = 5, max = 100, message = "Допустимая длина названия от 5 до 100 символов")
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogo() {
		return "/i/logos/" + getId().toString() + ".png";
	}
	
}
