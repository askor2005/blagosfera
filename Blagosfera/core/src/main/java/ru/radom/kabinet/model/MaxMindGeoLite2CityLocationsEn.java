package ru.radom.kabinet.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by ebelyaev on 19.08.2015.
 */
@Entity
@Table(name = "maxmind_geolite2_city_locations_en")
public class MaxMindGeoLite2CityLocationsEn {
    @Id
    @Column(name = "geoname_id")
    private Long geonameId;

    @Column(name = "locale_code")
    private String localeCode;

    @Column(name = "continent_code")
    private String continentCode;

    @Column(name = "continent_name")
    private String continentName;

    @Column(name = "country_iso_code")
    private String countryIsoCode;

    @Column(name = "country_name")
    private String countryName;

    @Column(name = "subdivision_1_iso_code")
    private String subdivision1IsoCode;

    @Column(name = "subdivision_1_name")
    private String subdivision1Name;

    @Column(name = "subdivision_2_iso_code")
    private String subdivision2IsoCode;

    @Column(name = "subdivision_2_name")
    private String subdivision21Name;

    @Column(name = "city_name")
    private String cityName;

    @Column(name = "metro_code")
    private String metroCode;

    @Column(name = "time_zone")
    private String timeZone;

    public MaxMindGeoLite2CityLocationsEn() {
    }

    public MaxMindGeoLite2CityLocationsEn(Long geonameId, String localeCode, String continentCode, String continentName, String countryIsoCode, String countryName, String subdivision1IsoCode, String subdivision1Name, String subdivision2IsoCode, String subdivision21Name, String cityName, String metroCode, String timeZone) {
        this.geonameId = geonameId;
        this.localeCode = localeCode;
        this.continentCode = continentCode;
        this.continentName = continentName;
        this.countryIsoCode = countryIsoCode;
        this.countryName = countryName;
        this.subdivision1IsoCode = subdivision1IsoCode;
        this.subdivision1Name = subdivision1Name;
        this.subdivision2IsoCode = subdivision2IsoCode;
        this.subdivision21Name = subdivision21Name;
        this.cityName = cityName;
        this.metroCode = metroCode;
        this.timeZone = timeZone;
    }

    public Long getGeonameId() {
        return geonameId;
    }

    public void setGeonameId(Long geonameId) {
        this.geonameId = geonameId;
    }

    public String getLocaleCode() {
        return localeCode;
    }

    public void setLocaleCode(String localeCode) {
        this.localeCode = localeCode;
    }

    public String getContinentCode() {
        return continentCode;
    }

    public void setContinentCode(String continentCode) {
        this.continentCode = continentCode;
    }

    public String getContinentName() {
        return continentName;
    }

    public void setContinentName(String continentName) {
        this.continentName = continentName;
    }

    public String getCountryIsoCode() {
        return countryIsoCode;
    }

    public void setCountryIsoCode(String countryIsoCode) {
        this.countryIsoCode = countryIsoCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getSubdivision1IsoCode() {
        return subdivision1IsoCode;
    }

    public void setSubdivision1IsoCode(String subdivision1IsoCode) {
        this.subdivision1IsoCode = subdivision1IsoCode;
    }

    public String getSubdivision1Name() {
        return subdivision1Name;
    }

    public void setSubdivision1Name(String subdivision1Name) {
        this.subdivision1Name = subdivision1Name;
    }

    public String getSubdivision2IsoCode() {
        return subdivision2IsoCode;
    }

    public void setSubdivision2IsoCode(String subdivision2IsoCode) {
        this.subdivision2IsoCode = subdivision2IsoCode;
    }

    public String getSubdivision21Name() {
        return subdivision21Name;
    }

    public void setSubdivision21Name(String subdivision21Name) {
        this.subdivision21Name = subdivision21Name;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getMetroCode() {
        return metroCode;
    }

    public void setMetroCode(String metroCode) {
        this.metroCode = metroCode;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MaxMindGeoLite2CityLocationsEn that = (MaxMindGeoLite2CityLocationsEn) o;

        return geonameId.equals(that.geonameId);

    }

    @Override
    public int hashCode() {
        return geonameId.hashCode();
    }
}
