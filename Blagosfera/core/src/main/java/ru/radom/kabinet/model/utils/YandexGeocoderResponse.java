package ru.radom.kabinet.model.utils;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Обёртка для запроса на прямое геокодирование Яндекс
 * https://tech.yandex.ru/maps/doc/geocoder/desc/concepts/input_params-docpage/
 *
 * Пример запроса:
 * https://geocode-maps.yandex.ru/1.x/?format=json&geocode=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0,+%D0%A2%D0%B2%D0%B5%D1%80%D1%81%D0%BA%D0%B0%D1%8F+%D1%83%D0%BB%D0%B8%D1%86%D0%B0,+%D0%B4%D0%BE%D0%BC+7
 *
 * Парсится через Gson.
 *
 * Created by ebelyaev on 15.10.2015.
 */
public class YandexGeocoderResponse {
    public class Response {
        public class GeoObjectCollection {
            public class GeoObjectItem {
                public class GeoObject {
                    public class Point {
                        // ВНИМАНИЕ. Геокодер яндекса по умолчанию возвращает сначала долготу а потом широту
                        @SerializedName("pos")
                        private String pos; // "37.611006 55.757962"

                        public Point() {
                        }

                        public Point(String pos) {
                            this.pos = pos;
                        }

                        public String getPos() {
                            return pos;
                        }

                        public void setPos(String pos) {
                            this.pos = pos;
                        }
                    }

                    @SerializedName("Point")
                    private Point point;

                    @SerializedName("description")
                    private String description; // "Москва, Россия"

                    public class MetaDataProperty {
                        public class GeocoderMetaData {
                            public class AddressDetails {
                                public class Country {

                                    @SerializedName("AddressLine")
                                    private String addressLine; // "Город, Уличная улица, 57А"

                                    public class AdministrativeArea {
                                        @SerializedName("AdministrativeAreaName")
                                        private String administrativeAreaName; // "Пермский край"

                                        public class SubAdministrativeArea {
                                            public class Locality {
                                                @SerializedName("LocalityName")
                                                private String localityName; // "Пермь"

                                                public class Thoroughfare {
                                                    @SerializedName("ThoroughfareName")
                                                    private String thoroughfareName; // "Уличная уица"

                                                    public class Premise {
                                                        @SerializedName("PremiseNumber")
                                                        private String premiseNumber; // 57А

                                                        public Premise() {
                                                        }

                                                        public Premise(String premiseNumber) {
                                                            this.premiseNumber = premiseNumber;
                                                        }

                                                        public String getPremiseNumber() {
                                                            return premiseNumber;
                                                        }

                                                        public void setPremiseNumber(String premiseNumber) {
                                                            this.premiseNumber = premiseNumber;
                                                        }
                                                    }

                                                    @SerializedName("Premise")
                                                    private Premise premise;

                                                    public Thoroughfare() {
                                                    }

                                                    public Thoroughfare(String thoroughfareName, Premise premise) {
                                                        this.thoroughfareName = thoroughfareName;
                                                        this.premise = premise;
                                                    }

                                                    public String getThoroughfareName() {
                                                        return thoroughfareName;
                                                    }

                                                    public void setThoroughfareName(String thoroughfareName) {
                                                        this.thoroughfareName = thoroughfareName;
                                                    }

                                                    public Premise getPremise() {
                                                        return premise;
                                                    }

                                                    public void setPremise(Premise premise) {
                                                        this.premise = premise;
                                                    }
                                                }

                                                @SerializedName("Thoroughfare")
                                                private Thoroughfare thoroughfare;

                                                public class DependentLocality {

                                                    @SerializedName("DependentLocality")
                                                    private DependentLocality dependentLocality;

                                                    @SerializedName("DependentLocalityName")
                                                    private String dependentLocalityName;

                                                    @SerializedName("Thoroughfare")
                                                    private Thoroughfare thoroughfare;

                                                    public DependentLocality() {
                                                    }

                                                    public DependentLocality(DependentLocality dependentLocality, String dependentLocalityName, Thoroughfare thoroughfare) {
                                                        this.dependentLocality = dependentLocality;
                                                        this.dependentLocalityName = dependentLocalityName;
                                                        this.thoroughfare = thoroughfare;
                                                    }

                                                    public DependentLocality getDependentLocality() {
                                                        return dependentLocality;
                                                    }

                                                    public void setDependentLocality(DependentLocality dependentLocality) {
                                                        this.dependentLocality = dependentLocality;
                                                    }

                                                    public String getDependentLocalityName() {
                                                        return dependentLocalityName;
                                                    }

                                                    public void setDependentLocalityName(String dependentLocalityName) {
                                                        this.dependentLocalityName = dependentLocalityName;
                                                    }

                                                    public Thoroughfare getThoroughfare() {
                                                        return thoroughfare;
                                                    }

                                                    public void setThoroughfare(Thoroughfare thoroughfare) {
                                                        this.thoroughfare = thoroughfare;
                                                    }
                                                }

                                                @SerializedName("DependentLocality")
                                                private DependentLocality dependentLocality;

                                                public Locality() {
                                                }

                                                public Locality(String localityName, Thoroughfare thoroughfare, DependentLocality dependentLocality) {
                                                    this.localityName = localityName;
                                                    this.thoroughfare = thoroughfare;
                                                    this.dependentLocality = dependentLocality;
                                                }

                                                public String getLocalityName() {
                                                    return localityName;
                                                }

                                                public void setLocalityName(String localityName) {
                                                    this.localityName = localityName;
                                                }

                                                public Thoroughfare getThoroughfare() {
                                                    return thoroughfare;
                                                }

                                                public void setThoroughfare(Thoroughfare thoroughfare) {
                                                    this.thoroughfare = thoroughfare;
                                                }

                                                public DependentLocality getDependentLocality() {
                                                    return dependentLocality;
                                                }

                                                public void setDependentLocality(DependentLocality dependentLocality) {
                                                    this.dependentLocality = dependentLocality;
                                                }
                                            }

                                            @SerializedName("Locality")
                                            private Locality locality;

                                            @SerializedName("SubAdministrativeAreaName")
                                            private String subAdministrativeAreaName; // "городской округ Пермь"

                                            public SubAdministrativeArea() {
                                            }

                                            public SubAdministrativeArea(Locality locality, String subAdministrativeAreaName) {
                                                this.locality = locality;
                                                this.subAdministrativeAreaName = subAdministrativeAreaName;
                                            }

                                            public Locality getLocality() {
                                                return locality;
                                            }

                                            public void setLocality(Locality locality) {
                                                this.locality = locality;
                                            }

                                            public String getSubAdministrativeAreaName() {
                                                return subAdministrativeAreaName;
                                            }

                                            public void setSubAdministrativeAreaName(String subAdministrativeAreaName) {
                                                this.subAdministrativeAreaName = subAdministrativeAreaName;
                                            }
                                        }

                                        @SerializedName("SubAdministrativeArea")
                                        private SubAdministrativeArea subAdministrativeArea;

                                        public AdministrativeArea() {
                                        }

                                        public AdministrativeArea(String administrativeAreaName, SubAdministrativeArea subAdministrativeArea) {
                                            this.administrativeAreaName = administrativeAreaName;
                                            this.subAdministrativeArea = subAdministrativeArea;
                                        }

                                        public String getAdministrativeAreaName() {
                                            return administrativeAreaName;
                                        }

                                        public void setAdministrativeAreaName(String administrativeAreaName) {
                                            this.administrativeAreaName = administrativeAreaName;
                                        }

                                        public SubAdministrativeArea getSubAdministrativeArea() {
                                            return subAdministrativeArea;
                                        }

                                        public void setSubAdministrativeArea(SubAdministrativeArea subAdministrativeArea) {
                                            this.subAdministrativeArea = subAdministrativeArea;
                                        }
                                    }

                                    @SerializedName("AdministrativeArea")
                                    private AdministrativeArea administrativeArea;

                                    @SerializedName("CountryName")
                                    private String countryName; // "Россия"

                                    @SerializedName("CountryNameCode")
                                    private String countryNameCode; // "RU"

                                    public Country() {
                                    }

                                    public Country(String addressLine, AdministrativeArea administrativeArea, String countryName, String countryNameCode) {
                                        this.addressLine = addressLine;
                                        this.administrativeArea = administrativeArea;
                                        this.countryName = countryName;
                                        this.countryNameCode = countryNameCode;
                                    }

                                    public String getAddressLine() {
                                        return addressLine;
                                    }

                                    public void setAddressLine(String addressLine) {
                                        this.addressLine = addressLine;
                                    }

                                    public AdministrativeArea getAdministrativeArea() {
                                        return administrativeArea;
                                    }

                                    public void setAdministrativeArea(AdministrativeArea administrativeArea) {
                                        this.administrativeArea = administrativeArea;
                                    }

                                    public String getCountryName() {
                                        return countryName;
                                    }

                                    public void setCountryName(String countryName) {
                                        this.countryName = countryName;
                                    }

                                    public String getCountryNameCode() {
                                        return countryNameCode;
                                    }

                                    public void setCountryNameCode(String countryNameCode) {
                                        this.countryNameCode = countryNameCode;
                                    }
                                }

                                @SerializedName("Country")
                                private Country country;

                                public AddressDetails() {
                                }

                                public AddressDetails(Country country) {
                                    this.country = country;
                                }

                                public Country getCountry() {
                                    return country;
                                }

                                public void setCountry(Country country) {
                                    this.country = country;
                                }
                            }

                            @SerializedName("AddressDetails")
                            private AddressDetails addressDetails;

                            @SerializedName("kind")
                            private String kind; // "house"

                            @SerializedName("precision")
                            private String precision; // "exact"

                            @SerializedName("text")
                            private String text; // "Россия, Москва, Тверская улица, 7"

                            public GeocoderMetaData() {
                            }

                            public GeocoderMetaData(AddressDetails addressDetails, String kind, String precision, String text) {
                                this.addressDetails = addressDetails;
                                this.kind = kind;
                                this.precision = precision;
                                this.text = text;
                            }

                            public AddressDetails getAddressDetails() {
                                return addressDetails;
                            }

                            public void setAddressDetails(AddressDetails addressDetails) {
                                this.addressDetails = addressDetails;
                            }

                            public String getKind() {
                                return kind;
                            }

                            public void setKind(String kind) {
                                this.kind = kind;
                            }

                            public String getPrecision() {
                                return precision;
                            }

                            public void setPrecision(String precision) {
                                this.precision = precision;
                            }

                            public String getText() {
                                return text;
                            }

                            public void setText(String text) {
                                this.text = text;
                            }
                        }

                        @SerializedName("GeocoderMetaData")
                        private GeocoderMetaData geocoderMetaData;

                        public MetaDataProperty() {
                        }

                        public MetaDataProperty(GeocoderMetaData geocoderMetaData) {
                            this.geocoderMetaData = geocoderMetaData;
                        }

                        public GeocoderMetaData getGeocoderMetaData() {
                            return geocoderMetaData;
                        }

                        public void setGeocoderMetaData(GeocoderMetaData geocoderMetaData) {
                            this.geocoderMetaData = geocoderMetaData;
                        }
                    }

                    @SerializedName("metaDataProperty")
                    private MetaDataProperty metaDataProperty;

                    @SerializedName("name")
                    private String name; // "Тверская улица, 7"

                    public GeoObject() {
                    }

                    public GeoObject(Point point, String description, MetaDataProperty metaDataProperty, String name) {
                        this.point = point;
                        this.description = description;
                        this.metaDataProperty = metaDataProperty;
                        this.name = name;
                    }

                    public Point getPoint() {
                        return point;
                    }

                    public void setPoint(Point point) {
                        this.point = point;
                    }

                    public String getDescription() {
                        return description;
                    }

                    public void setDescription(String description) {
                        this.description = description;
                    }

                    public MetaDataProperty getMetaDataProperty() {
                        return metaDataProperty;
                    }

                    public void setMetaDataProperty(MetaDataProperty metaDataProperty) {
                        this.metaDataProperty = metaDataProperty;
                    }

                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }
                }

                @SerializedName("GeoObject")
                private GeoObject geoObject;

                public GeoObjectItem() {
                }

                public GeoObjectItem(GeoObject geoObject) {
                    this.geoObject = geoObject;
                }

                public GeoObject getGeoObject() {
                    return geoObject;
                }

                public void setGeoObject(GeoObject geoObject) {
                    this.geoObject = geoObject;
                }
            }

            private List<GeoObjectItem> featureMember = new ArrayList<>();


            public class MetaDataProperty {
                public class GeocoderResponseMetaData {
                    private Integer found;
                    private String request;
                    private Integer results;

                    public GeocoderResponseMetaData() {
                    }

                    public GeocoderResponseMetaData(Integer found, String request, Integer results) {
                        this.found = found;
                        this.request = request;
                        this.results = results;
                    }

                    public Integer getFound() {
                        return found;
                    }

                    public void setFound(Integer found) {
                        this.found = found;
                    }

                    public String getRequest() {
                        return request;
                    }

                    public void setRequest(String request) {
                        this.request = request;
                    }

                    public Integer getResults() {
                        return results;
                    }

                    public void setResults(Integer results) {
                        this.results = results;
                    }
                }

                @SerializedName("GeocoderResponseMetaData")
                private GeocoderResponseMetaData geocoderResponseMetaData;

                public MetaDataProperty() {
                }

                public MetaDataProperty(GeocoderResponseMetaData geocoderResponseMetaData) {
                    this.geocoderResponseMetaData = geocoderResponseMetaData;
                }

                public GeocoderResponseMetaData getGeocoderResponseMetaData() {
                    return geocoderResponseMetaData;
                }

                public void setGeocoderResponseMetaData(GeocoderResponseMetaData geocoderResponseMetaData) {
                    this.geocoderResponseMetaData = geocoderResponseMetaData;
                }
            }

            private MetaDataProperty metaDataProperty;

            public GeoObjectCollection() {
            }

            public GeoObjectCollection(List<GeoObjectItem> featureMember, MetaDataProperty metaDataProperty) {
                this.featureMember = featureMember;
                this.metaDataProperty = metaDataProperty;
            }

            public List<GeoObjectItem> getFeatureMember() {
                return featureMember;
            }

            public void setFeatureMember(List<GeoObjectItem> featureMember) {
                this.featureMember = featureMember;
            }

            public MetaDataProperty getMetaDataProperty() {
                return metaDataProperty;
            }

            public void setMetaDataProperty(MetaDataProperty metaDataProperty) {
                this.metaDataProperty = metaDataProperty;
            }
        }

        @SerializedName("GeoObjectCollection")
        private GeoObjectCollection geoObjectCollection;

        public Response() {
        }

        public Response(GeoObjectCollection geoObjectCollection) {
            this.geoObjectCollection = geoObjectCollection;
        }

        public GeoObjectCollection getGeoObjectCollection() {
            return geoObjectCollection;
        }

        public void setGeoObjectCollection(GeoObjectCollection geoObjectCollection) {
            this.geoObjectCollection = geoObjectCollection;
        }
    }

    private Response response;

    public YandexGeocoderResponse() {
    }

    public YandexGeocoderResponse(Response response) {
        this.response = response;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}

