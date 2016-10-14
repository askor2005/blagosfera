package ru.askor.blagosfera.domain;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Data
public class Address implements Serializable {

	public static final long serialVersionUID = 1L;

	private String country;

	private String postalCode;

	private String region;
	private String district;
	private String city;
	private String street;
	private String building;
	private String geoPosition;
	private String geoLocation;

	private String room;
	private String roomLabel;

	private Double longitude;
	private Double latitude;

    private String fullAddress;

	public Address() {
	}

	public Address(String country, String region, String district, String city, String street, String building, String geoPosition, String geoLocation, String room, String roomLabel) {
		this.country = country;
		this.region = region;
		this.district = district;
		this.city = city;
		this.street = street;
		this.building = building;
		this.geoPosition = geoPosition;
		this.geoLocation = geoLocation;

		this.room = room;
		this.roomLabel = roomLabel;

		try {
			String[] parts = geoPosition.split(",");
			this.latitude = Double.parseDouble(parts[0]);
			this.longitude = Double.parseDouble(parts[1]);
		} catch (Exception e) {
			// ignore
			this.latitude = null;
			this.longitude = null;
		}
	}

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

	public String getFullAddress() {
        if (fullAddress != null) return fullAddress;

		final StringBuilder result = new StringBuilder();
		if(!StringUtils.isEmpty(country)) result.append(country);
		if(!StringUtils.isEmpty(city)) result.append(", ").append(city);
		if(!StringUtils.isEmpty(street)) result.append(", ").append(street);
		if(!StringUtils.isEmpty(building)) result.append(" ").append(building);
		if(!StringUtils.isEmpty(roomLabel) && (result.length() > 0) && !StringUtils.isEmpty(room)) result.append(" ").append(roomLabel);
		if(!StringUtils.isEmpty(room)) result.append(" ").append(room);

		return  result.toString();
	}
}