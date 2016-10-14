package ru.radom.kabinet.model.utils;

import org.apache.commons.lang3.StringUtils;
import ru.radom.kabinet.utils.AddressUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ebelyaev on 15.10.2015.
 */
// Вспомогательный класс для любого адреса в системе.
// Создавался прежде всего для того, чтобы можно было сравнивать и корректировать адреса.
// Использовать можно только нужные в каждом конкретном случае поля
public class SupportAddress {
    // systemHadCoordinates, isReliableByCoordinates, isReliableBySimilarity - используются при "склеивании" адресов
    private boolean systemHadCoordinates = false;    // в системе(благосфера) имелись непустые координаты
    private boolean isReliableByCoordinates = false; // достоверный по координатам, значит что проверяющяяя система так же определила местоположение, а это значит что адрес скорее всего правильный
    private boolean isReliableBySimilarity = false;  // достоверный по похожести адреса, значит что координаты в системе и проверяющей системы отличаются или отсутсвуют, а текстовые варианты адреса похожи, а это значит что адрес может быть правильным
    private Similarities similarities = new Similarities(0,false); // объект, который характеризует степерь похожести // определено только при isReliableBySimilarity = true

    // По умолчанию для удобства проставляются пустые строки
    private String country = ""; // страна
    private String postalCode = ""; // почтовый индекс
    private String region = ""; // регион
    private String regionDescription = "";
    private String area = ""; // район
    private String areaDescription = "";
    private String city = ""; // населённый пункт
    private String cityDescription = "";
    private String street = ""; // улица
    private String streetDescription = "";
    private String house = "";// дом
    private String houseDescription = "";
    private String subHouse = ""; // корпус
    private String room = ""; // квартира // для фактического и регистрационного адресов
    private String roomDescription = "";
    private String office = ""; // офис // для адресов объединений и адресов офисов регистраторов
    private String officeDescription = "";
    private String geoPosition = ""; // координаты, строка типа "38.084768,55.629176" // разбивать на широту и долготу не стал, так как порядок следования может менятсья
    private String geoLocation = ""; // полный адрес

    public boolean isSystemHadCoordinates() {
        return systemHadCoordinates;
    }

    public void setSystemHadCoordinates(boolean systemHadCoordinates) {
        this.systemHadCoordinates = systemHadCoordinates;
    }

    public boolean isReliableByCoordinates() {
        return isReliableByCoordinates;
    }

    public void setReliableByCoordinates(boolean isReliableByCoordinates) {
        this.isReliableByCoordinates = isReliableByCoordinates;
    }

    public boolean isReliableBySimilarity() {
        return isReliableBySimilarity;
    }

    public void setReliableBySimilarity(boolean isReliableBySimilarity) {
        this.isReliableBySimilarity = isReliableBySimilarity;
    }

    public Similarities getSimilarities() {
        return similarities;
    }

    public void setSimilarities(Similarities similarities) {
        this.similarities = similarities;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegionDescription() {
        return regionDescription;
    }

    public void setRegionDescription(String regionDescription) {
        this.regionDescription = regionDescription;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAreaDescription() {
        return areaDescription;
    }

    public void setAreaDescription(String areaDescription) {
        this.areaDescription = areaDescription;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityDescription() {
        return cityDescription;
    }

    public void setCityDescription(String cityDescription) {
        this.cityDescription = cityDescription;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreetDescription() {
        return streetDescription;
    }

    public void setStreetDescription(String streetDescription) {
        this.streetDescription = streetDescription;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getHouseDescription() {
        return houseDescription;
    }

    public void setHouseDescription(String houseDescription) {
        this.houseDescription = houseDescription;
    }

    public String getSubHouse() {
        return subHouse;
    }

    public void setSubHouse(String subHouse) {
        this.subHouse = subHouse;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getRoomDescription() {
        return roomDescription;
    }

    public void setRoomDescription(String roomDescription) {
        this.roomDescription = roomDescription;
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getOfficeDescription() {
        return officeDescription;
    }

    public void setOfficeDescription(String officeDescription) {
        this.officeDescription = officeDescription;
    }

    public String getGeoPosition() {
        return geoPosition;
    }

    public void setGeoPosition(String geoPosition) {
        this.geoPosition = geoPosition;
    }

    public String getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(String geoLocation) {
        this.geoLocation = geoLocation;
    }

    public String getFullAddress() {
        List<String> geoLocationParts = new ArrayList<>();

        if (!StringUtils.isBlank(country)) {
            geoLocationParts.add(country);
        }

        if (!StringUtils.isBlank(postalCode)) {
            geoLocationParts.add(postalCode);
        }

        if (!StringUtils.isBlank(region)) {
            String withDescription = region;

            String shortDescription = AddressUtils.getRegionShortDescription(regionDescription);
            if (!StringUtils.isBlank(shortDescription)) {
                withDescription = shortDescription + " " + region;
            }

            geoLocationParts.add(withDescription);
        }

        if (!StringUtils.isBlank(area)) {
            String withDescription = area;

            String shortDescription = AddressUtils.getAreaShortDescription(areaDescription);
            if (!StringUtils.isBlank(shortDescription)) {
                withDescription = shortDescription + " " + area;
            }

            geoLocationParts.add(withDescription);
        }

        if (!StringUtils.isBlank(city)) {
            String withDescription = city;

            String shortDescription = AddressUtils.getCityShortDescription(cityDescription);
            if (!StringUtils.isBlank(shortDescription)) {
                withDescription = shortDescription + " " + city;
            }

            geoLocationParts.add(withDescription);
        }

        if (!StringUtils.isBlank(street)) {
            String withDescription = street;

            String shortDescription = AddressUtils.getStreetShortDescription(streetDescription);
            if (!StringUtils.isBlank(shortDescription)) {
                withDescription = shortDescription + " " + street;
            }

            geoLocationParts.add(withDescription);
        }

        if (!StringUtils.isBlank(house)) {
            String withDescription = house;

            String shortDescription = AddressUtils.getHouseShortDescription(houseDescription);
            if (!StringUtils.isBlank(shortDescription)) {
                withDescription = shortDescription + " " + house;
            }

            geoLocationParts.add(withDescription);
        }

        if (!StringUtils.isBlank(subHouse)) {
            geoLocationParts.add("корпус " + subHouse);
        }

        if (!StringUtils.isBlank(room)) {
            String withDescription = room;

            String shortDescription = AddressUtils.getRoomShortDescription(roomDescription);
            if (!StringUtils.isBlank(shortDescription)) {
                withDescription = shortDescription + " " + room;
            }

            geoLocationParts.add(withDescription);
        }

        if (!StringUtils.isBlank(office)) {
            String withDescription = office;

            String shortDescription = AddressUtils.getOfficeShortDescription(officeDescription);
            if (!StringUtils.isBlank(shortDescription)) {
                withDescription = shortDescription + " " + office;
            }

            geoLocationParts.add(withDescription);
        }

        String result = "";
        if (geoLocationParts.size() > 0) {
            result = StringUtils.join(geoLocationParts, ", ");
        }

        return result;
    }

    public String getJoinedAddress() {
        return getJoinedAddress(false);
    }

    public String getJoinedAddress(boolean includeDescription) {
        List<String> geoLocationParts = new ArrayList<>();

        if (!StringUtils.isBlank(country)) {
            geoLocationParts.add(country);
        }

        if (!StringUtils.isBlank(postalCode)) {
            geoLocationParts.add(postalCode);
        }

        if (!StringUtils.isBlank(region)) {
            //geoLocationParts.add(region);
            String withDescription = region;
            if (includeDescription && !StringUtils.isBlank(regionDescription)) {
                withDescription += " [" + regionDescription + "]";
            }
            geoLocationParts.add(withDescription);
        }

        if (!StringUtils.isBlank(area)) {
            // geoLocationParts.add(area);
            String withDescription = area;
            if (includeDescription && !StringUtils.isBlank(areaDescription)) {
                withDescription += " [" + areaDescription + "]";
            }
            geoLocationParts.add(withDescription);
        }

        if (!StringUtils.isBlank(city)) {
            //geoLocationParts.add(city);
            String withDescription = city;
            if (includeDescription && !StringUtils.isBlank(cityDescription)) {
                withDescription += " [" + cityDescription + "]";
            }
            geoLocationParts.add(withDescription);
        }

        if (!StringUtils.isBlank(street)) {
            //geoLocationParts.add(street);
            String withDescription = street;
            if (includeDescription && !StringUtils.isBlank(streetDescription)) {
                withDescription += " [" + streetDescription + "]";
            }
            geoLocationParts.add(withDescription);
        }

        if (!StringUtils.isBlank(house)) {
            // geoLocationParts.add("дом " + house);
            String withDescription = house;
            if (includeDescription && !StringUtils.isBlank(houseDescription)) {
                withDescription += " [" + houseDescription + "]";
            }
            geoLocationParts.add(withDescription);
        }

        if (!StringUtils.isBlank(subHouse)) {
            geoLocationParts.add("корпус " + subHouse);
        }

        if (!StringUtils.isBlank(room)) {
            // geoLocationParts.add("квартира " + room);
            String withDescription = room;
            if (includeDescription && !StringUtils.isBlank(roomDescription)) {
                withDescription += " [" + roomDescription + "]";
            }
            geoLocationParts.add(withDescription);
        }

        if (!StringUtils.isBlank(office)) {
            //geoLocationParts.add("офис " + office);
            String withDescription = office;
            if (includeDescription && !StringUtils.isBlank(officeDescription)) {
                withDescription += " [" + officeDescription + "]";
            }
            geoLocationParts.add(withDescription);
        }

        String joinedAddress = "";
        if (geoLocationParts.size() > 0) {
            joinedAddress = StringUtils.join(geoLocationParts, ", ");
        }
        return joinedAddress;
    }

    // Возвращает базовый адрес до корпуса(без квартиры и офиса)
    public String getJoinedAddressBase() {
        return getJoinedAddressBase(false);
    }

    // Возвращает базовый адрес до корпуса(без квартиры и офиса)
    public String getJoinedAddressBase(boolean includeDescription) {
        List<String> geoLocationParts = new ArrayList<>();

        if (!StringUtils.isBlank(country)) {
            geoLocationParts.add(country);
        }

        if (!StringUtils.isBlank(postalCode)) {
            geoLocationParts.add(postalCode);
        }

        if (!StringUtils.isBlank(region)) {
            //geoLocationParts.add(region);
            String withDescription = region;
            if (includeDescription && !StringUtils.isBlank(regionDescription)) {
                withDescription += " [" + regionDescription + "]";
            }
            geoLocationParts.add(withDescription);
        }

        if (!StringUtils.isBlank(area)) {
            // geoLocationParts.add(area);
            String withDescription = area;
            if (includeDescription && !StringUtils.isBlank(areaDescription)) {
                withDescription += " [" + areaDescription + "]";
            }
            geoLocationParts.add(withDescription);
        }

        if (!StringUtils.isBlank(city)) {
            //geoLocationParts.add(city);
            String withDescription = city;
            if (includeDescription && !StringUtils.isBlank(cityDescription)) {
                withDescription += " [" + cityDescription + "]";
            }
            geoLocationParts.add(withDescription);
        }

        if (!StringUtils.isBlank(street)) {
            //geoLocationParts.add(street);
            String withDescription = street;
            if (includeDescription && !StringUtils.isBlank(streetDescription)) {
                withDescription += " [" + streetDescription + "]";
            }
            geoLocationParts.add(withDescription);
        }

        if (!StringUtils.isBlank(house)) {
            // geoLocationParts.add("дом " + house);
            String withDescription = house;
            if (includeDescription && !StringUtils.isBlank(houseDescription)) {
                withDescription += " [" + houseDescription + "]";
            }
            geoLocationParts.add(withDescription);
        }

        if (!StringUtils.isBlank(subHouse)) {
            geoLocationParts.add("корпус " + subHouse);
        }

        String joinedAddress = "";
        if (geoLocationParts.size() > 0) {
            joinedAddress = StringUtils.join(geoLocationParts, ", ");
        }
        return joinedAddress;
    }

    @Override
    public String toString() {
        return getJoinedAddress(true);
    }
}
