package ru.radom.kabinet.model;

import org.hibernate.annotations.Formula;
import ru.askor.blagosfera.domain.contacts.ContactGroup;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "contacts_groups")
public class ContactsGroupEntity extends LongIdentifiable {

	@JoinColumn(name = "sharer_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private UserEntity user;

	@Column(length = 100, nullable = false)
	@Size(min = 1, max = 100, message = "Допустимая длина названия от 1 до 100 символов")
	private String name;

	@Column(nullable = false)
	@NotNull(message = "Не выбран цвет списка")
	private int color;


	//@OneToMany(fetch = FetchType.LAZY, mappedBy = "contactsGroup")
	@OrderBy("id")
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "contacts_group_contacts", joinColumns = {@JoinColumn(name = "contact_group_id", nullable = false)}, inverseJoinColumns = {@JoinColumn(name = "contact_id", nullable = false)})
	private Set<ContactEntity> contacts = new HashSet<>();

	@Formula(value="(select count(distinct c.id) from contacts c inner join contacts_group_contacts cgr on cgr.contact_group_id = id and cgr.contact_id = c.id where c.sharerstatus = 1 and c.otherstatus = 1 and (not (select s.deleted from sharers s where s.id = c.other_id)))")
	private int contactsCount;
	
	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public Set<ContactEntity> getContacts() {
		return contacts;
	}

	public void setContacts(Set<ContactEntity> contacts) {
		this.contacts = contacts;
	}

	public int getContactsCount() {
		return contactsCount;
	}

	public void setContactsCount(int contactsCount) {
		this.contactsCount = contactsCount;
	}
	public ContactGroup toDomain() {
		return new ContactGroup(getId(),getName(),getUser().getId(),color,getContactsCount());
	}

}
