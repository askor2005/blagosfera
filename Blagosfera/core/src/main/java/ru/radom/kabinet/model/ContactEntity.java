package ru.radom.kabinet.model;

import ru.askor.blagosfera.domain.contacts.Contact;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "contacts", uniqueConstraints = @UniqueConstraint(columnNames = { "sharer_id", "other_id" }))
public class ContactEntity extends LongIdentifiable {

	@JoinColumn(name = "sharer_id", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private UserEntity user;

	@JoinColumn(name = "other_id", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private UserEntity other;

	//@JoinColumn(name = "contacts_group_id", nullable = true)
	//@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@OrderBy("id")
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "contacts_group_contacts", joinColumns = {@JoinColumn(name = "contact_id", nullable = false)}, inverseJoinColumns = {@JoinColumn(name = "contact_group_id", nullable = false)})
	private Set<ContactsGroupEntity> contactsGroups = new HashSet<>();

	@Column(name = "sharerStatus")
	private ContactStatus sharerStatus;

	@Column(name = "otherStatus")
	private ContactStatus otherStatus;

	@Column(name = "request_date", nullable = true)
	private Date requestDate;

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public UserEntity getOther() {
		return other;
	}

	public void setOther(UserEntity other) {
		this.other = other;
	}


	public ContactStatus getSharerStatus() {
		return sharerStatus;
	}

	public void setSharerStatus(ContactStatus sharerStatus) {
		this.sharerStatus = sharerStatus;
	}

	public ContactStatus getOtherStatus() {
		return otherStatus;
	}

	public void setOtherStatus(ContactStatus otherStatus) {
		this.otherStatus = otherStatus;
	}

	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public Set<ContactsGroupEntity> getContactsGroups() {
		return contactsGroups;
	}

	public void setContactsGroups(Set<ContactsGroupEntity> contactsGroups) {
		this.contactsGroups = contactsGroups;
	}

	public Contact toDomain() {
		return new Contact(getId(),getUser().toDomain(),getOther().toDomain(),getContactsGroups().stream().map(contactsGroupEntity -> contactsGroupEntity.toDomain()).collect(Collectors.toSet()),getSharerStatus(),getOtherStatus(),getRequestDate());
	}

	public static Contact toDomainSafe(ContactEntity entity) {
		Contact result = null;
		if (entity != null) {
			result = entity.toDomain();
		}
		return result;
	}

	public static List<Contact> toDomainList(List<ContactEntity> entities) {
		List<Contact> result = null;
		if (entities != null) {
			result = new ArrayList<>();
			for (ContactEntity entity : entities) {
				result.add(toDomainSafe(entity));
			}
		}
		return result;
	}

}
