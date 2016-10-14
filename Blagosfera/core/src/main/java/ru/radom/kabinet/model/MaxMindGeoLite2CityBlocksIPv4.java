package ru.radom.kabinet.model;

import javax.persistence.*;

/**
 * Created by ebelyaev on 19.08.2015.
 */
@Entity
@Table(name = "maxmind_geolite2_city_blocks_ipv4")
@IdClass(MaxMindGeoLite2CityBlocksIPv4PK.class)
public class MaxMindGeoLite2CityBlocksIPv4 {

    @Id
    @Column(name = "ip_from")
    private Long ipFrom;

    @Id
    @Column(name = "ip_to")
    private Long ipTo;

    @Column(name = "network")
    private String network;

    @Column(name = "geoname_id")
    private Long geonameId;

    @Column(name = "registered_country_geoname_id")
    private Long registeredCountryGeonameId;

    @Column(name = "represented_country_geoname_id")
    private Long representedCountryGeonameId;

    @Column(name = "is_anonymous_proxy")
    private Integer isAnonymousProxy;

    @Column(name = "is_satellite_provider")
    private Integer isSatelliteProvider;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "latitude")
    private Float latitude;

    @Column(name = "longitude")
    private Float longitude;

    public MaxMindGeoLite2CityBlocksIPv4() {
    }

    public MaxMindGeoLite2CityBlocksIPv4(Long ipFrom, Long ipTo, String network, Long geonameId, Long registeredCountryGeonameId, Long representedCountryGeonameId, Integer isAnonymousProxy, Integer isSatelliteProvider, String postalCode, Float latitude, Float longitude) {
        this.ipFrom = ipFrom;
        this.ipTo = ipTo;
        this.network = network;
        this.geonameId = geonameId;
        this.registeredCountryGeonameId = registeredCountryGeonameId;
        this.representedCountryGeonameId = representedCountryGeonameId;
        this.isAnonymousProxy = isAnonymousProxy;
        this.isSatelliteProvider = isSatelliteProvider;
        this.postalCode = postalCode;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Long getIpFrom() {
        return ipFrom;
    }

    public void setIpFrom(Long ipFrom) {
        this.ipFrom = ipFrom;
    }

    public Long getIpTo() {
        return ipTo;
    }

    public void setIpTo(Long ipTo) {
        this.ipTo = ipTo;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public Long getGeonameId() {
        return geonameId;
    }

    public void setGeonameId(Long geonameId) {
        this.geonameId = geonameId;
    }

    public Long getRegisteredCountryGeonameId() {
        return registeredCountryGeonameId;
    }

    public void setRegisteredCountryGeonameId(Long registeredCountryGeonameId) {
        this.registeredCountryGeonameId = registeredCountryGeonameId;
    }

    public Long getRepresentedCountryGeonameId() {
        return representedCountryGeonameId;
    }

    public void setRepresentedCountryGeonameId(Long representedCountryGeonameId) {
        this.representedCountryGeonameId = representedCountryGeonameId;
    }

    public Integer getIsAnonymousProxy() {
        return isAnonymousProxy;
    }

    public void setIsAnonymousProxy(Integer isAnonymousProxy) {
        this.isAnonymousProxy = isAnonymousProxy;
    }

    public Integer getIsSatelliteProvider() {
        return isSatelliteProvider;
    }

    public void setIsSatelliteProvider(Integer isSatelliteProvider) {
        this.isSatelliteProvider = isSatelliteProvider;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }
}
