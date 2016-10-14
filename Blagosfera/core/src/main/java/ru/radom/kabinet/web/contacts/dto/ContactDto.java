package ru.radom.kabinet.web.contacts.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import ru.askor.blagosfera.domain.contacts.Contact;
import ru.askor.blagosfera.domain.contacts.ContactGroup;
import ru.radom.kabinet.json.TimeStampDateSerializer;
import ru.radom.kabinet.model.ContactStatus;
import ru.radom.kabinet.utils.DateUtils;
import ru.radom.kabinet.web.user.dto.UserDataDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 *
 * Created by vtarasenko on 09.04.2016.
 */
public class ContactDto {

    public Long userId;
    public UserDataDto other;
    public List<ContactGroupDto> contactGroups;
    public ContactStatus sharerStatus;
    public ContactStatus otherStatus;
    public String actualCountry;
    public String actualCity;
    public boolean online;

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public UserDataDto getOther() {
        return other;
    }

    public void setOther(UserDataDto other) {
        this.other = other;
    }

    public List<ContactGroupDto> getContactGroups() {
        return contactGroups;
    }

    public void setContactGroups(List<ContactGroupDto> contactGroups) {
        this.contactGroups = contactGroups;
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

    public String getActualCountry() {
        return actualCountry;
    }

    public void setActualCountry(String actualCountry) {
        this.actualCountry = actualCountry;
    }

    public String getActualCity() {
        return actualCity;
    }

    public void setActualCity(String actualCity) {
        this.actualCity = actualCity;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public Integer getRequestHoursDistance() {
        return requestHoursDistance;
    }

    public void setRequestHoursDistance(Integer requestHoursDistance) {
        this.requestHoursDistance = requestHoursDistance;
    }

    @JsonSerialize(using = TimeStampDateSerializer.class)
    public Date requestDate;
    public Integer requestHoursDistance;

    public ContactDto() {
    }

    public static ContactDto toDto(Contact contact) {
        ContactDto contactDto = new ContactDto();
        contactDto.userId = contact.getUser().getId();
        contactDto.other = contact.getOther() != null ? new UserDataDto(contact.getOther()) : null;
        contactDto.contactGroups = new ArrayList<ContactGroupDto>();
        if (contact.getContactGroups() != null) {
            contact.getContactGroups().forEach(contactGroup -> {
                contactDto.contactGroups.add(ContactGroupDto.toDto(contactGroup));
            });
        }
        contactDto.sharerStatus = contact.getSharerStatus();
        contactDto.otherStatus = contact.getOtherStatus();
        contactDto.requestDate = contact.getRequestDate();
        contactDto.requestHoursDistance = contact.getRequestDate() != null ? DateUtils.getDistanceHours(contact.getRequestDate(), new Date()) : null;
        return contactDto;
    }
}
