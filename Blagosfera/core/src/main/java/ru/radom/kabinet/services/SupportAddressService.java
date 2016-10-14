package ru.radom.kabinet.services;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.radom.kabinet.dao.fields.FieldDao;
import ru.radom.kabinet.dao.fields.FieldValueDao;
import ru.radom.kabinet.dao.rameralisteditor.RameraListEditorDAO;
import ru.radom.kabinet.dao.rameralisteditor.RameraListEditorItemDAO;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.fields.FieldEntity;
import ru.radom.kabinet.model.fields.FieldValueEntity;
import ru.radom.kabinet.model.rameralisteditor.RameraListEditor;
import ru.radom.kabinet.model.rameralisteditor.RameraListEditorItem;
import ru.radom.kabinet.model.utils.SupportAddress;
import ru.radom.kabinet.model.utils.YandexGeocoderResponse;
import ru.radom.kabinet.utils.AddressUtils;

import java.util.List;

/**
 * Сервис для работы с SupportAddress
 * <p/>
 * Created by ebelyaev on 16.10.2015.
 */
@Service("supportAddressService")
public class SupportAddressService {
    @Autowired
    private FieldDao fieldDao;

    @Autowired
    private FieldValueDao fieldValueDao;

    @Autowired
    private RameraListEditorItemDAO rameraListEditorItemDAO;

    @Autowired
    private RameraListEditorDAO rameraListEditorDAO;

    // TODO запихнуть инициализацию филдов в @PostConstruct

    //-----------------------------------------------------------------------------------------------------------------------------------------------

    // Преобразовывет YandexGeocoderResponse в удобоваримый фармат
    public SupportAddress getSupportAddressFromYandexGeocoderResponse(YandexGeocoderResponse r) {
        // Вспомогательная переменная. Много раз переприсваивается.
        String value = "";

        String country = "";
        String region = "";
        String regionDescription = "";
        String area = ""; // не совсем то, что нужно. например вместо "Одинцовский район" содержит "городской округ Краснознаменск" // update: "городской округ" по идее правильнее
        String areaDescription = "";
        String city = "";
        String cityDescription = "";
        String street = "";
        String streetDescription = "";
        String house = "";
        String houseDescription = "";

        String geoPosition = "";
        String geoLocation = ""; // полный адрес по версии Яндекса

        if (r != null && r.getResponse() != null && r.getResponse().getGeoObjectCollection() != null) {
            List<YandexGeocoderResponse.Response.GeoObjectCollection.GeoObjectItem> goil = r.getResponse().getGeoObjectCollection().getFeatureMember();
            if (goil != null && goil.size() > 0 && goil.get(0) != null && goil.get(0).getGeoObject() != null) {
                YandexGeocoderResponse.Response.GeoObjectCollection.GeoObjectItem.GeoObject go = goil.get(0).getGeoObject();

                if (go.getMetaDataProperty() != null && go.getMetaDataProperty().getGeocoderMetaData() != null) {
                    YandexGeocoderResponse.Response.GeoObjectCollection.GeoObjectItem.GeoObject.MetaDataProperty.GeocoderMetaData gmd = go.getMetaDataProperty().getGeocoderMetaData();
                    geoLocation = gmd.getText();

                    if (gmd.getAddressDetails() != null && gmd.getAddressDetails().getCountry() != null) {
                        YandexGeocoderResponse.Response.GeoObjectCollection.GeoObjectItem.GeoObject.MetaDataProperty.GeocoderMetaData.AddressDetails.Country c = gmd.getAddressDetails().getCountry();
                        value = c.getCountryName();
                        if (!StringUtils.isBlank(value)) {
                            country = value;
                        }

                        if (c.getAdministrativeArea() != null) {
                            YandexGeocoderResponse.Response.GeoObjectCollection.GeoObjectItem.GeoObject.MetaDataProperty.GeocoderMetaData.AddressDetails.Country.AdministrativeArea aa = c.getAdministrativeArea();
                            value = aa.getAdministrativeAreaName();
                            if (!StringUtils.isBlank(value)) {
                                // region = value;
                                region = AddressUtils.getRegionName(value);
                                regionDescription = AddressUtils.getRegionDescription(value);
                            }

                            if (aa.getSubAdministrativeArea() != null) {
                                YandexGeocoderResponse.Response.GeoObjectCollection.GeoObjectItem.GeoObject.MetaDataProperty.GeocoderMetaData.AddressDetails.Country.AdministrativeArea.SubAdministrativeArea saa = aa.getSubAdministrativeArea();
                                value = saa.getSubAdministrativeAreaName();
                                if (!StringUtils.isBlank(value)) {
                                    // area = value;
                                    area = AddressUtils.getAreaName(value);
                                    areaDescription = AddressUtils.getAreaDescription(value);
                                }

                                if (saa.getLocality() != null) {
                                    YandexGeocoderResponse.Response.GeoObjectCollection.GeoObjectItem.GeoObject.MetaDataProperty.GeocoderMetaData.AddressDetails.Country.AdministrativeArea.SubAdministrativeArea.Locality locality = saa.getLocality();
                                    value = locality.getLocalityName();
                                    if (!StringUtils.isBlank(value)) {
                                        // city = value;
                                        city = AddressUtils.getCityName(value);
                                        cityDescription = AddressUtils.getCityDescription(value);
                                    }

                                    if (locality.getThoroughfare() != null) {
                                        YandexGeocoderResponse.Response.GeoObjectCollection.GeoObjectItem.GeoObject.MetaDataProperty.GeocoderMetaData.AddressDetails.Country.AdministrativeArea.SubAdministrativeArea.Locality.Thoroughfare thoroughfare = locality.getThoroughfare();
                                        value = thoroughfare.getThoroughfareName();
                                        if (!StringUtils.isBlank(value)) {
                                            // street = value;
                                            street = AddressUtils.getStreetName(value);
                                            streetDescription = AddressUtils.getStreetDescription(value);
                                        }

                                        if (thoroughfare.getPremise() != null) {
                                            YandexGeocoderResponse.Response.GeoObjectCollection.GeoObjectItem.GeoObject.MetaDataProperty.GeocoderMetaData.AddressDetails.Country.AdministrativeArea.SubAdministrativeArea.Locality.Thoroughfare.Premise premise = thoroughfare.getPremise();
                                            value = premise.getPremiseNumber();
                                            if (!StringUtils.isBlank(value)) {
                                                // house = value;
                                                house = value;
                                                houseDescription = "Дом";
                                            }
                                        }
                                    } else if (locality.getDependentLocality() != null) {
                                        YandexGeocoderResponse.Response.GeoObjectCollection.GeoObjectItem.GeoObject.MetaDataProperty.GeocoderMetaData.AddressDetails.Country.AdministrativeArea.SubAdministrativeArea.Locality.DependentLocality dependentLocality = locality.getDependentLocality();
                                        while (dependentLocality.getDependentLocality() != null) {
                                            dependentLocality = dependentLocality.getDependentLocality();
                                        }

                                        if (dependentLocality.getThoroughfare() != null) {
                                            YandexGeocoderResponse.Response.GeoObjectCollection.GeoObjectItem.GeoObject.MetaDataProperty.GeocoderMetaData.AddressDetails.Country.AdministrativeArea.SubAdministrativeArea.Locality.Thoroughfare thoroughfare = dependentLocality.getThoroughfare();
                                            value = thoroughfare.getThoroughfareName();
                                            if (!StringUtils.isBlank(value)) {
                                                //street = value;
                                                street = AddressUtils.getStreetName(value);
                                                streetDescription = AddressUtils.getStreetDescription(value);
                                            }
                                            if (thoroughfare.getPremise() != null) {
                                                YandexGeocoderResponse.Response.GeoObjectCollection.GeoObjectItem.GeoObject.MetaDataProperty.GeocoderMetaData.AddressDetails.Country.AdministrativeArea.SubAdministrativeArea.Locality.Thoroughfare.Premise premise = thoroughfare.getPremise();
                                                value = premise.getPremiseNumber();
                                                if (!StringUtils.isBlank(value)) {
                                                    //house = value;
                                                    house = value;
                                                    houseDescription = "Дом";
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (go.getPoint() != null) {
                    value = go.getPoint().getPos();
                    if (!StringUtils.isBlank(value)) {
                        // Сначало широта, потом долгота. А сервис почему то возвращает сначала долготу потом широту
                        String[] coordinates = StringUtils.split(value, " ");
                        geoPosition = coordinates[1] + "," + coordinates[0];
                        //geoPosition = value.replace(" ", ",");
                    }
                }
            }
        }

        SupportAddress result = new SupportAddress();

        result.setCountry(country);

        result.setRegion(region);
        result.setRegionDescription(regionDescription);

        result.setArea(area);
        result.setAreaDescription(areaDescription);

        result.setCity(city);
        result.setCityDescription(cityDescription);

        result.setStreet(street);
        result.setStreetDescription(streetDescription);

        result.setHouse(house);
        result.setHouseDescription(houseDescription);

        result.setGeoPosition(geoPosition);
        result.setGeoLocation(geoLocation);

        processSupportAddress(result);
        return result;
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------------

    public SupportAddress getActualSupportAddress(UserEntity userEntity) {
        String country = "";
        String postalCode = "";

        String region = "";
        String regionDescription = "";

        String area = "";
        String areaDescription = "";

        String city = "";
        String cityDescription = "";

        String street = "";
        String streetDescription = "";

        String house = "";
        String houseDescription = "";

        String subHouse = "";

        String room = "";
        String roomDescription = "";

        String geoPosition = "";
        String geoLocation = "";

        FieldEntity countryField = fieldDao.getByInternalName("FCOUNTRY_CL"); // Страна
        FieldValueEntity countryFieldValue = fieldValueDao.get(userEntity, countryField);
        if (countryFieldValue != null) {
            String countryFieldValueString = countryFieldValue.getStringValue();
            if (!StringUtils.isBlank(countryFieldValueString)) {
                try {
                    Long id = Long.parseLong(countryFieldValueString);
                    country = rameraListEditorItemDAO.getById(id).getText();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        FieldEntity regionField = fieldDao.getByInternalName("FREGION_RL"); // Регион
        FieldValueEntity regionFieldValue = fieldValueDao.get(userEntity, regionField);
        if (regionFieldValue != null) {
            String regionFieldValueString = regionFieldValue.getStringValue();
            if (!StringUtils.isBlank(regionFieldValueString)) {
                region = regionFieldValueString;
            }
        }

        FieldEntity regionDescriptionField = fieldDao.getByInternalName("FREGION_RL_DESCRIPTION"); // Регион
        FieldValueEntity regionDescriptionFieldValue = fieldValueDao.get(userEntity, regionDescriptionField);
        if (regionDescriptionFieldValue != null) {
            String regionDescriptionFieldValueString = regionDescriptionFieldValue.getStringValue();
            if (!StringUtils.isBlank(regionDescriptionFieldValueString)) {
                regionDescription = regionDescriptionFieldValueString;
            }
        }

        FieldEntity areaField = fieldDao.getByInternalName("FAREA_AL"); // Район
        FieldValueEntity areaFieldValue = fieldValueDao.get(userEntity, areaField);
        if (areaFieldValue != null) {
            String areaFieldValueString = areaFieldValue.getStringValue();
            if (!StringUtils.isBlank(areaFieldValueString)) {
                area = areaFieldValueString;
            }
        }

        FieldEntity areaDescriptionField = fieldDao.getByInternalName("FAREA_AL_DESCRIPTION"); // Район
        FieldValueEntity areaDescriptionFieldValue = fieldValueDao.get(userEntity, areaDescriptionField);
        if (areaDescriptionFieldValue != null) {
            String areaDescriptionFieldValueString = areaDescriptionFieldValue.getStringValue();
            if (!StringUtils.isBlank(areaDescriptionFieldValueString)) {
                areaDescription = areaDescriptionFieldValueString;
            }
        }

        FieldEntity cityField = fieldDao.getByInternalName("FCITY_TL"); // Населённый пукнкт
        FieldValueEntity cityFieldValue = fieldValueDao.get(userEntity, cityField);
        if (cityFieldValue != null) {
            String cityFieldValueString = cityFieldValue.getStringValue();
            if (!StringUtils.isBlank(cityFieldValueString)) {
                city = cityFieldValueString;
            }
        }

        FieldEntity cityDescriptionField = fieldDao.getByInternalName("FCITY_TL_DESCRIPTION"); // Населённый пукнкт
        FieldValueEntity cityDescriptionFieldValue = fieldValueDao.get(userEntity, cityDescriptionField);
        if (cityDescriptionFieldValue != null) {
            String cityDescriptionFieldValueString = cityDescriptionFieldValue.getStringValue();
            if (!StringUtils.isBlank(cityDescriptionFieldValueString)) {
                cityDescription = cityDescriptionFieldValueString;
            }
        }

        FieldEntity postalCodeField = fieldDao.getByInternalName("FPOSTAL_CODE"); // Почтовый индекс
        FieldValueEntity postalCodeFieldValue = fieldValueDao.get(userEntity, postalCodeField);
        if (postalCodeFieldValue != null) {
            String postalCodeFieldValueString = postalCodeFieldValue.getStringValue();
            if (!StringUtils.isBlank(postalCodeFieldValueString)) {
                postalCode = postalCodeFieldValueString;
            }
        }

        FieldEntity streetField = fieldDao.getByInternalName("FSTREET"); // Улица
        FieldValueEntity streetFieldValue = fieldValueDao.get(userEntity, streetField);
        if (streetFieldValue != null) {
            String streetFieldValueString = streetFieldValue.getStringValue();
            if (!StringUtils.isBlank(streetFieldValueString)) {
                street = streetFieldValueString;
            }
        }

        FieldEntity streetDescriptionField = fieldDao.getByInternalName("FSTREET_DESCRIPTION"); // Улица
        FieldValueEntity streetDescriptionFieldValue = fieldValueDao.get(userEntity, streetDescriptionField);
        if (streetDescriptionFieldValue != null) {
            String streetDescriptionFieldValueString = streetDescriptionFieldValue.getStringValue();
            if (!StringUtils.isBlank(streetDescriptionFieldValueString)) {
                streetDescription = streetDescriptionFieldValueString;
            }
        }

        FieldEntity houseField = fieldDao.getByInternalName("FHOUSE"); // Дом
        FieldValueEntity houseFieldValue = fieldValueDao.get(userEntity, houseField);
        if (houseFieldValue != null) {
            String houseFieldValueString = houseFieldValue.getStringValue();
            if (!StringUtils.isBlank(houseFieldValueString)) {
                house = houseFieldValueString;
            }
        }

        FieldEntity houseDescriptionField = fieldDao.getByInternalName("FHOUSE_DESCRIPTION"); // Дом
        FieldValueEntity houseDescriptionFieldValue = fieldValueDao.get(userEntity, houseDescriptionField);
        if (houseDescriptionFieldValue != null) {
            String houseDescriptionFieldValueString = houseDescriptionFieldValue.getStringValue();
            if (!StringUtils.isBlank(houseDescriptionFieldValueString)) {
                houseDescription = houseDescriptionFieldValueString;
            }
        }

        FieldEntity subHouseField = fieldDao.getByInternalName("FSUBHOUSE"); // Корпус
        FieldValueEntity subHouseFieldValue = fieldValueDao.get(userEntity, subHouseField);
        if (subHouseFieldValue != null) {
            String subHouseFieldValueString = subHouseFieldValue.getStringValue();
            if (!StringUtils.isBlank(subHouseFieldValueString)) {
                subHouse = subHouseFieldValueString;
            }
        }

        FieldEntity roomField = fieldDao.getByInternalName("FROOM"); // Квартира
        FieldValueEntity roomFieldValue = fieldValueDao.get(userEntity, roomField);
        if (roomFieldValue != null) {
            String roomFieldValueString = roomFieldValue.getStringValue();
            if (!StringUtils.isBlank(roomFieldValueString)) {
                room = roomFieldValueString;
            }
        }

        FieldEntity roomDescriptionField = fieldDao.getByInternalName("FROOM_DESCRIPTION"); // Квартира
        FieldValueEntity roomDescriptionFieldValue = fieldValueDao.get(userEntity, roomDescriptionField);
        if (roomDescriptionFieldValue != null) {
            String roomDescriptionFieldValueString = roomDescriptionFieldValue.getStringValue();
            if (!StringUtils.isBlank(roomDescriptionFieldValueString)) {
                roomDescription = roomDescriptionFieldValueString;
            }
        }

        FieldEntity geoPositionField = fieldDao.getByInternalName("F_GEO_POSITION"); // Координаты
        FieldValueEntity geoPositionFieldValue = fieldValueDao.get(userEntity, geoPositionField);
        if (geoPositionFieldValue != null) {
            String geoPositionFieldValueString = geoPositionFieldValue.getStringValue();
            if (!StringUtils.isBlank(geoPositionFieldValueString)) {
                geoPosition = geoPositionFieldValueString;
            }
        }

        FieldEntity fGeoLocationField = fieldDao.getByInternalName("F_GEO_LOCATION"); // Фактический адрес
        FieldValueEntity fGeoLocationFieldValue = fieldValueDao.get(userEntity, fGeoLocationField);
        if (fGeoLocationFieldValue != null) {
            String fGeoLocationFieldValueString = fGeoLocationFieldValue.getStringValue();
            if (!StringUtils.isBlank(fGeoLocationFieldValueString)) {
                geoLocation = fGeoLocationFieldValueString;
            }
        }

        region = AddressUtils.getRegionName(region);
        regionDescription = AddressUtils.getRegionDescription(regionDescription);
        area = AddressUtils.getAreaName(area);
        areaDescription = AddressUtils.getAreaDescription(areaDescription);
        city = AddressUtils.getCityName(city);
        cityDescription = AddressUtils.getCityDescription(cityDescription);
        street = AddressUtils.getStreetName(street);
        streetDescription = AddressUtils.getStreetDescription(streetDescription);
        house = AddressUtils.getHouseName(house);
        houseDescription = AddressUtils.getHouseDescription(houseDescription);
        room = AddressUtils.getRoomName(room);
        roomDescription = AddressUtils.getRoomDescription(roomDescription);

        SupportAddress result = new SupportAddress();

        result.setCountry(country);

        result.setPostalCode(postalCode);

        result.setRegion(AddressUtils.getRegionName(region));
        if (!StringUtils.isBlank(regionDescription)) {
            result.setRegionDescription(regionDescription);
        } else {
            result.setRegionDescription(AddressUtils.getRegionDescription(region));
        }

        result.setArea(AddressUtils.getAreaName(area));
        if (!StringUtils.isBlank(areaDescription)) {
            result.setAreaDescription(areaDescription);
        } else {
            result.setAreaDescription(AddressUtils.getAreaDescription(area));
        }

        result.setCity(city);
        if (!StringUtils.isBlank(cityDescription)) {
            result.setCityDescription(cityDescription);
        } else {
            result.setCityDescription(AddressUtils.getCityDescription(city));
        }

        result.setStreet(street);
        if (!StringUtils.isBlank(streetDescription)) {
            result.setStreetDescription(streetDescription);
        } else {
            result.setStreetDescription(AddressUtils.getStreetDescription(street));
        }

        result.setHouse(house);
        if (!StringUtils.isBlank(houseDescription)) {
            result.setHouseDescription(houseDescription);
        } else {
            result.setHouseDescription(AddressUtils.getHouseDescription(house));
        }

        result.setSubHouse(subHouse);

        result.setRoom(room);
        if (!StringUtils.isBlank(roomDescription)) {
            result.setRoomDescription(roomDescription);
        } else {
            result.setRoomDescription(AddressUtils.getRoomDescription(room));
        }

        result.setGeoPosition(geoPosition);
        result.setGeoLocation(geoLocation);

        processSupportAddress(result);
        return result;
    }

    public SupportAddress getRegistrationSupportAddress(UserEntity userEntity) {
        String country = "";
        String postalCode = "";

        String region = "";
        String regionDescription = "";

        String area = "";
        String areaDescription = "";

        String city = "";
        String cityDescription = "";

        String street = "";
        String streetDescription = "";

        String house = "";
        String houseDescription = "";

        String subHouse = "";

        String room = "";
        String roomDescription = "";

        String geoPosition = "";
        String geoLocation = "";

        FieldEntity countryField = fieldDao.getByInternalName("COUNTRY_CL"); // Страна
        FieldValueEntity countryFieldValue = fieldValueDao.get(userEntity, countryField);
        if (countryFieldValue != null) {
            String countryFieldValueString = countryFieldValue.getStringValue();
            if (!StringUtils.isBlank(countryFieldValueString)) {
                try {
                    Long id = Long.parseLong(countryFieldValueString);
                    country = rameraListEditorItemDAO.getById(id).getText();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        FieldEntity regionField = fieldDao.getByInternalName("REGION_RL"); // Регион
        FieldValueEntity regionFieldValue = fieldValueDao.get(userEntity, regionField);
        if (regionFieldValue != null) {
            String regionFieldValueString = regionFieldValue.getStringValue();
            if (!StringUtils.isBlank(regionFieldValueString)) {
                //region = regionFieldValueString;
                region = AddressUtils.getRegionName(regionFieldValueString);
            }
        }

        FieldEntity regionDescriptionField = fieldDao.getByInternalName("REGION_RL_DESCRIPTION"); // Регион
        FieldValueEntity regionDescriptionFieldValue = fieldValueDao.get(userEntity, regionDescriptionField);
        if (regionDescriptionFieldValue != null) {
            String regionDescriptionFieldValueString = regionDescriptionFieldValue.getStringValue();
            if (!StringUtils.isBlank(regionDescriptionFieldValueString)) {
                //regionDescription = regionDescriptionFieldValueString;
                regionDescription = AddressUtils.getRegionDescription(regionDescriptionFieldValueString);
            }
        }

        FieldEntity areaField = fieldDao.getByInternalName("AREA_AL"); // Район
        FieldValueEntity areaFieldValue = fieldValueDao.get(userEntity, areaField);
        if (areaFieldValue != null) {
            String areaFieldValueString = areaFieldValue.getStringValue();
            if (!StringUtils.isBlank(areaFieldValueString)) {
                //area = areaFieldValueString;
                area = AddressUtils.getAreaName(areaFieldValueString);
            }
        }

        FieldEntity areaDescriptionField = fieldDao.getByInternalName("AREA_AL_DESCRIPTION"); // Район
        FieldValueEntity areaDescriptionFieldValue = fieldValueDao.get(userEntity, areaDescriptionField);
        if (areaDescriptionFieldValue != null) {
            String areaDescriptionFieldValueString = areaDescriptionFieldValue.getStringValue();
            if (!StringUtils.isBlank(areaDescriptionFieldValueString)) {
                //area = areaDescriptionFieldValueString;
                areaDescription = AddressUtils.getAreaDescription(areaDescriptionFieldValueString);
            }
        }

        FieldEntity cityField = fieldDao.getByInternalName("CITY_TL"); // Населённый пукнкт
        FieldValueEntity cityFieldValue = fieldValueDao.get(userEntity, cityField);
        if (cityFieldValue != null) {
            String cityFieldValueString = cityFieldValue.getStringValue();
            if (!StringUtils.isBlank(cityFieldValueString)) {
                //city = cityFieldValueString;
                city = AddressUtils.getCityName(cityFieldValueString);
            }
        }

        FieldEntity cityDescriptionField = fieldDao.getByInternalName("CITY_TL_DESCRIPTION"); // Населённый пукнкт
        FieldValueEntity cityDescriptionFieldValue = fieldValueDao.get(userEntity, cityDescriptionField);
        if (cityDescriptionFieldValue != null) {
            String cityDescriptionFieldValueString = cityDescriptionFieldValue.getStringValue();
            if (!StringUtils.isBlank(cityDescriptionFieldValueString)) {
                //cityDescription = cityDescriptionFieldValueString;
                cityDescription = AddressUtils.getCityDescription(cityDescriptionFieldValueString);
            }
        }

        FieldEntity postalCodeField = fieldDao.getByInternalName("POSTAL_CODE"); // Почтовый индекс
        FieldValueEntity postalCodeFieldValue = fieldValueDao.get(userEntity, postalCodeField);
        if (postalCodeFieldValue != null) {
            String postalCodeFieldValueString = postalCodeFieldValue.getStringValue();
            if (!StringUtils.isBlank(postalCodeFieldValueString)) {
                postalCode = postalCodeFieldValueString;
            }
        }

        FieldEntity streetField = fieldDao.getByInternalName("STREET"); // Улица
        FieldValueEntity streetFieldValue = fieldValueDao.get(userEntity, streetField);
        if (streetFieldValue != null) {
            String streetFieldValueString = streetFieldValue.getStringValue();
            if (!StringUtils.isBlank(streetFieldValueString)) {
                //street = streetFieldValueString;
                street = AddressUtils.getStreetName(streetFieldValueString);
            }
        }

        FieldEntity streetDescriptionField = fieldDao.getByInternalName("STREET_DESCRIPTION"); // Улица
        FieldValueEntity streetDescriptionFieldValue = fieldValueDao.get(userEntity, streetDescriptionField);
        if (streetDescriptionFieldValue != null) {
            String streetDescriptionFieldValueString = streetDescriptionFieldValue.getStringValue();
            if (!StringUtils.isBlank(streetDescriptionFieldValueString)) {
                //streetDescription = streetDescriptionFieldValueString;
                streetDescription = AddressUtils.getStreetDescription(streetDescriptionFieldValueString);
            }
        }

        FieldEntity houseField = fieldDao.getByInternalName("HOUSE"); // Дом
        FieldValueEntity houseFieldValue = fieldValueDao.get(userEntity, houseField);
        if (houseFieldValue != null) {
            String houseFieldValueString = houseFieldValue.getStringValue();
            if (!StringUtils.isBlank(houseFieldValueString)) {
                house = houseFieldValueString;
            }
        }

        FieldEntity houseDescriptionField = fieldDao.getByInternalName("HOUSE_DESCRIPTION"); // Дом
        FieldValueEntity houseDescriptionFieldValue = fieldValueDao.get(userEntity, houseDescriptionField);
        if (houseDescriptionFieldValue != null) {
            String houseDescriptionFieldValueString = houseDescriptionFieldValue.getStringValue();
            if (!StringUtils.isBlank(houseDescriptionFieldValueString)) {
                houseDescription = houseDescriptionFieldValueString;
            }
        }

        FieldEntity subHouseField = fieldDao.getByInternalName("SUBHOUSE"); // Корпус
        FieldValueEntity subHouseFieldValue = fieldValueDao.get(userEntity, subHouseField);
        if (subHouseFieldValue != null) {
            String subHouseFieldValueString = subHouseFieldValue.getStringValue();
            if (!StringUtils.isBlank(subHouseFieldValueString)) {
                subHouse = subHouseFieldValueString;
            }
        }

        FieldEntity roomField = fieldDao.getByInternalName("ROOM"); // Квартира
        FieldValueEntity roomFieldValue = fieldValueDao.get(userEntity, roomField);
        if (roomFieldValue != null) {
            String roomFieldValueString = roomFieldValue.getStringValue();
            if (!StringUtils.isBlank(roomFieldValueString)) {
                room = roomFieldValueString;
            }
        }

        FieldEntity roomDescriptionField = fieldDao.getByInternalName("ROOM_DESCRIPTION"); // Квартира
        FieldValueEntity roomDescriptionFieldValue = fieldValueDao.get(userEntity, roomDescriptionField);
        if (roomDescriptionFieldValue != null) {
            String roomDescriptionFieldValueString = roomDescriptionFieldValue.getStringValue();
            if (!StringUtils.isBlank(roomDescriptionFieldValueString)) {
                roomDescription = roomDescriptionFieldValueString;
            }
        }

        FieldEntity geoPositionField = fieldDao.getByInternalName("GEO_POSITION"); // Координаты
        FieldValueEntity geoPositionFieldValue = fieldValueDao.get(userEntity, geoPositionField);
        if (geoPositionFieldValue != null) {
            String geoPositionFieldValueString = geoPositionFieldValue.getStringValue();
            if (!StringUtils.isBlank(geoPositionFieldValueString)) {
                geoPosition = geoPositionFieldValueString;
            }
        }

        FieldEntity geoLocationField = fieldDao.getByInternalName("GEO_LOCATION"); // Фактический адрес
        FieldValueEntity geoLocationFieldValue = fieldValueDao.get(userEntity, geoLocationField);
        if (geoLocationFieldValue != null) {
            String geoLocationFieldValueString = geoLocationFieldValue.getStringValue();
            if (!StringUtils.isBlank(geoLocationFieldValueString)) {
                geoLocation = geoLocationFieldValueString;
            }
        }

        region = AddressUtils.getRegionName(region);
        regionDescription = AddressUtils.getRegionDescription(regionDescription);
        area = AddressUtils.getAreaName(area);
        areaDescription = AddressUtils.getAreaDescription(areaDescription);
        city = AddressUtils.getCityName(city);
        cityDescription = AddressUtils.getCityDescription(cityDescription);
        street = AddressUtils.getStreetName(street);
        streetDescription = AddressUtils.getStreetDescription(streetDescription);
        house = AddressUtils.getHouseName(house);
        houseDescription = AddressUtils.getHouseDescription(houseDescription);
        room = AddressUtils.getRoomName(room);
        roomDescription = AddressUtils.getRoomDescription(roomDescription);

        SupportAddress result = new SupportAddress();

        result.setCountry(country);

        result.setPostalCode(postalCode);

        result.setRegion(AddressUtils.getRegionName(region));
        if (!StringUtils.isBlank(regionDescription)) {
            result.setRegionDescription(regionDescription);
        } else {
            result.setRegionDescription(AddressUtils.getRegionDescription(region));
        }

        result.setArea(AddressUtils.getAreaName(area));
        if (!StringUtils.isBlank(areaDescription)) {
            result.setAreaDescription(areaDescription);
        } else {
            result.setAreaDescription(AddressUtils.getAreaDescription(area));
        }

        result.setCity(city);
        if (!StringUtils.isBlank(cityDescription)) {
            result.setCityDescription(cityDescription);
        } else {
            result.setCityDescription(AddressUtils.getCityDescription(city));
        }

        result.setStreet(street);
        if (!StringUtils.isBlank(streetDescription)) {
            result.setStreetDescription(streetDescription);
        } else {
            result.setStreetDescription(AddressUtils.getStreetDescription(street));
        }

        result.setHouse(house);
        if (!StringUtils.isBlank(houseDescription)) {
            result.setHouseDescription(houseDescription);
        } else {
            result.setHouseDescription(AddressUtils.getHouseDescription(house));
        }

        result.setSubHouse(subHouse);

        result.setRoom(room);
        if (!StringUtils.isBlank(roomDescription)) {
            result.setRoomDescription(roomDescription);
        } else {
            result.setRoomDescription(AddressUtils.getRoomDescription(room));
        }

        result.setGeoPosition(geoPosition);
        result.setGeoLocation(geoLocation);

        processSupportAddress(result);
        return result;
    }

    public SupportAddress getRegistratorOfficeSupportAddress(UserEntity userEntity) {
        String country = "";
        String postalCode = "";

        String region = "";
        String regionDescription = "";

        String area = "";
        String areaDescription = "";

        String city = "";
        String cityDescription = "";

        String street = "";
        String streetDescription = "";

        String house = "";
        String houseDescription = "";

        String office = "";
        String officeDescription = "";

        String geoPosition = "";
        String geoLocation = "";

        FieldEntity countryField = fieldDao.getByInternalName("REGISTRATOR_OFFICE_COUNTRY"); // Страна
        FieldValueEntity countryFieldValue = fieldValueDao.get(userEntity, countryField);
        if (countryFieldValue != null) {
            String countryFieldValueString = countryFieldValue.getStringValue();
            if (!StringUtils.isBlank(countryFieldValueString)) {
                try {
                    Long id = Long.parseLong(countryFieldValueString);
                    country = rameraListEditorItemDAO.getById(id).getText();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        FieldEntity regionField = fieldDao.getByInternalName("REGISTRATOR_OFFICE_REGION"); // Регион
        FieldValueEntity regionFieldValue = fieldValueDao.get(userEntity, regionField);
        if (regionFieldValue != null) {
            String regionFieldValueString = regionFieldValue.getStringValue();
            if (!StringUtils.isBlank(regionFieldValueString)) {
                region = regionFieldValueString;
            }
        }

        FieldEntity regionDescriptionField = fieldDao.getByInternalName("REGISTRATOR_OFFICE_REGION_DESCRIPTION"); // Регион
        FieldValueEntity regionDescriptionFieldValue = fieldValueDao.get(userEntity, regionDescriptionField);
        if (regionDescriptionFieldValue != null) {
            String regionDescriptionFieldValueString = regionDescriptionFieldValue.getStringValue();
            if (!StringUtils.isBlank(regionDescriptionFieldValueString)) {
                regionDescription = regionDescriptionFieldValueString;
            }
        }


        FieldEntity areaField = fieldDao.getByInternalName("REGISTRATOR_OFFICE_DISTRICT"); // Район
        FieldValueEntity areaFieldValue = fieldValueDao.get(userEntity, areaField);
        if (areaFieldValue != null) {
            String areaFieldValueString = areaFieldValue.getStringValue();
            if (!StringUtils.isBlank(areaFieldValueString)) {
                area = areaFieldValueString;
            }
        }

        FieldEntity areaDescriptionField = fieldDao.getByInternalName("REGISTRATOR_OFFICE_DISTRICT_DESCRIPTION"); // Район
        FieldValueEntity areaDescriptionFieldValue = fieldValueDao.get(userEntity, areaDescriptionField);
        if (areaDescriptionFieldValue != null) {
            String areaDescriptionFieldValueString = areaDescriptionFieldValue.getStringValue();
            if (!StringUtils.isBlank(areaDescriptionFieldValueString)) {
                areaDescription = areaDescriptionFieldValueString;
            }
        }

        FieldEntity cityField = fieldDao.getByInternalName("REGISTRATOR_OFFICE_CITY"); // Населённый пукнкт
        FieldValueEntity cityFieldValue = fieldValueDao.get(userEntity, cityField);
        if (cityFieldValue != null) {
            String cityFieldValueString = cityFieldValue.getStringValue();
            if (!StringUtils.isBlank(cityFieldValueString)) {
                city = cityFieldValueString;
            }
        }

        FieldEntity cityDescriptionField = fieldDao.getByInternalName("REGISTRATOR_OFFICE_CITY_DESCRIPTION"); // Населённый пукнкт
        FieldValueEntity cityDescriptionFieldValue = fieldValueDao.get(userEntity, cityDescriptionField);
        if (cityDescriptionFieldValue != null) {
            String cityDescriptionFieldValueString = cityDescriptionFieldValue.getStringValue();
            if (!StringUtils.isBlank(cityDescriptionFieldValueString)) {
                cityDescription = cityDescriptionFieldValueString;
            }
        }


        FieldEntity postalCodeField = fieldDao.getByInternalName("REGISTRATOR_OFFICE_POSTAL_CODE"); // Почтовый индекс
        FieldValueEntity postalCodeFieldValue = fieldValueDao.get(userEntity, postalCodeField);
        if (postalCodeFieldValue != null) {
            String postalCodeFieldValueString = postalCodeFieldValue.getStringValue();
            if (!StringUtils.isBlank(postalCodeFieldValueString)) {
                postalCode = postalCodeFieldValueString;
            }
        }

        FieldEntity streetField = fieldDao.getByInternalName("REGISTRATOR_OFFICE_STREET"); // Улица
        FieldValueEntity streetFieldValue = fieldValueDao.get(userEntity, streetField);
        if (streetFieldValue != null) {
            String streetFieldValueString = streetFieldValue.getStringValue();
            if (!StringUtils.isBlank(streetFieldValueString)) {
                street = streetFieldValueString;
            }
        }

        FieldEntity streetDescriptionField = fieldDao.getByInternalName("REGISTRATOR_OFFICE_STREET_DESCRIPTION"); // Улица
        FieldValueEntity streetDescriptionFieldValue = fieldValueDao.get(userEntity, streetDescriptionField);
        if (streetDescriptionFieldValue != null) {
            String streetDescriptionFieldValueString = streetDescriptionFieldValue.getStringValue();
            if (!StringUtils.isBlank(streetDescriptionFieldValueString)) {
                streetDescription = streetDescriptionFieldValueString;
            }
        }

        FieldEntity houseField = fieldDao.getByInternalName("REGISTRATOR_OFFICE_BUILDING"); // Дом
        FieldValueEntity houseFieldValue = fieldValueDao.get(userEntity, houseField);
        if (houseFieldValue != null) {
            String houseFieldValueString = houseFieldValue.getStringValue();
            if (!StringUtils.isBlank(houseFieldValueString)) {
                house = houseFieldValueString;
            }
        }

        FieldEntity houseDescriptionField = fieldDao.getByInternalName("REGISTRATOR_OFFICE_BUILDING_DESCRIPTION"); // Дом
        FieldValueEntity houseDescriptionFieldValue = fieldValueDao.get(userEntity, houseDescriptionField);
        if (houseDescriptionFieldValue != null) {
            String houseDescriptionFieldValueString = houseDescriptionFieldValue.getStringValue();
            if (!StringUtils.isBlank(houseDescriptionFieldValueString)) {
                houseDescription = houseDescriptionFieldValueString;
            }
        }

        FieldEntity officeField = fieldDao.getByInternalName("REGISTRATOR_OFFICE_ROOM"); // Офис
        FieldValueEntity officeFieldValue = fieldValueDao.get(userEntity, officeField);
        if (officeFieldValue != null) {
            String officeFieldValueString = officeFieldValue.getStringValue();
            if (!StringUtils.isBlank(officeFieldValueString)) {
                office = officeFieldValueString;
            }
        }

        FieldEntity officeDescriptionField = fieldDao.getByInternalName("REGISTRATOR_OFFICE_ROOM_DESCRIPTION"); // Офис
        FieldValueEntity officeDescriptionFieldValue = fieldValueDao.get(userEntity, officeDescriptionField);
        if (officeDescriptionFieldValue != null) {
            String officeDescriptionFieldValueString = officeDescriptionFieldValue.getStringValue();
            if (!StringUtils.isBlank(officeDescriptionFieldValueString)) {
                officeDescription = officeDescriptionFieldValueString;
            }
        }

        FieldEntity geoPositionField = fieldDao.getByInternalName("REGISTRATOR_OFFICE_GEO_POSITION"); // Координаты
        FieldValueEntity geoPositionFieldValue = fieldValueDao.get(userEntity, geoPositionField);
        if (geoPositionFieldValue != null) {
            String geoPositionFieldValueString = geoPositionFieldValue.getStringValue();
            if (!StringUtils.isBlank(geoPositionFieldValueString)) {
                geoPosition = geoPositionFieldValueString;
            }
        }

        FieldEntity geoLocationField = fieldDao.getByInternalName("REGISTRATOR_OFFICE_GEO_LOCATION"); // Фактический адрес
        FieldValueEntity geoLocationFieldValue = fieldValueDao.get(userEntity, geoLocationField);
        if (geoLocationFieldValue != null) {
            String geoLocationFieldValueString = geoLocationFieldValue.getStringValue();
            if (!StringUtils.isBlank(geoLocationFieldValueString)) {
                geoLocation = geoLocationFieldValueString;
            }
        }


        region = AddressUtils.getRegionName(region);
        regionDescription = AddressUtils.getRegionDescription(regionDescription);
        area = AddressUtils.getAreaName(area);
        areaDescription = AddressUtils.getAreaDescription(areaDescription);
        city = AddressUtils.getCityName(city);
        cityDescription = AddressUtils.getCityDescription(cityDescription);
        street = AddressUtils.getStreetName(street);
        streetDescription = AddressUtils.getStreetDescription(streetDescription);
        house = AddressUtils.getHouseName(house);
        houseDescription = AddressUtils.getHouseDescription(houseDescription);
        office = AddressUtils.getOfficeName(office);
        officeDescription = AddressUtils.getOfficeDescription(officeDescription);

        SupportAddress result = new SupportAddress();

        result.setCountry(country);

        result.setPostalCode(postalCode);

        result.setRegion(AddressUtils.getRegionName(region));
        if (!StringUtils.isBlank(regionDescription)) {
            result.setRegionDescription(regionDescription);
        } else {
            result.setRegionDescription(AddressUtils.getRegionDescription(region));
        }

        result.setArea(AddressUtils.getAreaName(area));
        if (!StringUtils.isBlank(areaDescription)) {
            result.setAreaDescription(areaDescription);
        } else {
            result.setAreaDescription(AddressUtils.getAreaDescription(area));
        }

        result.setCity(city);
        if (!StringUtils.isBlank(cityDescription)) {
            result.setCityDescription(cityDescription);
        } else {
            result.setCityDescription(AddressUtils.getCityDescription(city));
        }

        result.setStreet(street);
        if (!StringUtils.isBlank(streetDescription)) {
            result.setStreetDescription(streetDescription);
        } else {
            result.setStreetDescription(AddressUtils.getStreetDescription(street));
        }

        result.setHouse(house);
        if (!StringUtils.isBlank(houseDescription)) {
            result.setHouseDescription(houseDescription);
        } else {
            result.setHouseDescription(AddressUtils.getHouseDescription(house));
        }

        result.setOffice(office);
        if (!StringUtils.isBlank(officeDescription)) {
            result.setOfficeDescription(officeDescription);
        } else {
            result.setOfficeDescription(AddressUtils.getOfficeDescription(office));
        }

        result.setGeoPosition(geoPosition);
        result.setGeoLocation(geoLocation);

        processSupportAddress(result);
        return result;
    }

    public SupportAddress getCommunityActualSupportAddress(CommunityEntity community) {
        String country = "";
        String postalCode = "";

        String region = "";
        String regionDescription = "";

        String area = "";
        String areaDescription = "";

        String city = "";
        String cityDescription = "";

        String street = "";
        String streetDescription = "";

        String house = "";
        String houseDescription = "";

        String office = "";
        String officeDescription = "";

        String geoPosition = "";
        String geoLocation = "";

        FieldEntity countryField = fieldDao.getByInternalName("COMMUNITY_LEGAL_F_COUNTRY"); // Страна
        FieldValueEntity countryFieldValue = fieldValueDao.get(community, countryField);
        if (countryFieldValue != null) {
            String countryFieldValueString = countryFieldValue.getStringValue();
            if (!StringUtils.isBlank(countryFieldValueString)) {
                try {
                    Long id = Long.parseLong(countryFieldValueString);
                    country = rameraListEditorItemDAO.getById(id).getText();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        FieldEntity regionField = fieldDao.getByInternalName("COMMUNITY_LEGAL_F_REGION"); // Регион
        FieldValueEntity regionFieldValue = fieldValueDao.get(community, regionField);
        if (regionFieldValue != null) {
            String regionFieldValueString = regionFieldValue.getStringValue();
            if (!StringUtils.isBlank(regionFieldValueString)) {
                region = regionFieldValueString;
            }
        }

        FieldEntity regionDescriptionField = fieldDao.getByInternalName("COMMUNITY_LEGAL_F_REGION_DESCRIPTION"); // Регион
        FieldValueEntity regionDescriptionFieldValue = fieldValueDao.get(community, regionDescriptionField);
        if (regionDescriptionFieldValue != null) {
            String regionDescriptionFieldValueString = regionDescriptionFieldValue.getStringValue();
            if (!StringUtils.isBlank(regionDescriptionFieldValueString)) {
                regionDescription = regionDescriptionFieldValueString;
            }
        }


        FieldEntity areaField = fieldDao.getByInternalName("COMMUNITY_LEGAL_F_AREA"); // Район
        FieldValueEntity areaFieldValue = fieldValueDao.get(community, areaField);
        if (areaFieldValue != null) {
            String areaFieldValueString = areaFieldValue.getStringValue();
            if (!StringUtils.isBlank(areaFieldValueString)) {
                area = areaFieldValueString;
            }
        }

        FieldEntity areaDescriptionField = fieldDao.getByInternalName("COMMUNITY_LEGAL_F_AREA_DESCRIPTION"); // Район
        FieldValueEntity areaDescriptionFieldValue = fieldValueDao.get(community, areaDescriptionField);
        if (areaDescriptionFieldValue != null) {
            String areaDescriptionFieldValueString = areaDescriptionFieldValue.getStringValue();
            if (!StringUtils.isBlank(areaDescriptionFieldValueString)) {
                areaDescription = areaDescriptionFieldValueString;
            }
        }

        FieldEntity cityField = fieldDao.getByInternalName("COMMUNITY_LEGAL_F_LOCALITY"); // Населённый пукнкт
        FieldValueEntity cityFieldValue = fieldValueDao.get(community, cityField);
        if (cityFieldValue != null) {
            String cityFieldValueString = cityFieldValue.getStringValue();
            if (!StringUtils.isBlank(cityFieldValueString)) {
                city = cityFieldValueString;
            }
        }

        FieldEntity cityDescriptionField = fieldDao.getByInternalName("COMMUNITY_LEGAL_F_LOCALITY_DESCRIPTION"); // Населённый пукнкт
        FieldValueEntity cityDescriptionFieldValue = fieldValueDao.get(community, cityDescriptionField);
        if (cityDescriptionFieldValue != null) {
            String cityDescriptionFieldValueString = cityDescriptionFieldValue.getStringValue();
            if (!StringUtils.isBlank(cityDescriptionFieldValueString)) {
                cityDescription = cityDescriptionFieldValueString;
            }
        }


        FieldEntity streetField = fieldDao.getByInternalName("COMMUNITY_LEGAL_F_STREET"); // Улица
        FieldValueEntity streetFieldValue = fieldValueDao.get(community, streetField);
        if (streetFieldValue != null) {
            String streetFieldValueString = streetFieldValue.getStringValue();
            if (!StringUtils.isBlank(streetFieldValueString)) {
                street = streetFieldValueString;
            }
        }

        FieldEntity streetDescriptionField = fieldDao.getByInternalName("COMMUNITY_LEGAL_F_STREET_DESCRIPTION"); // Улица
        FieldValueEntity streetDescriptionFieldValue = fieldValueDao.get(community, streetDescriptionField);
        if (streetDescriptionFieldValue != null) {
            String streetDescriptionFieldValueString = streetDescriptionFieldValue.getStringValue();
            if (!StringUtils.isBlank(streetDescriptionFieldValueString)) {
                streetDescription = streetDescriptionFieldValueString;
            }
        }

        FieldEntity houseField = fieldDao.getByInternalName("COMMUNITY_LEGAL_F_HOUSE"); // Дом
        FieldValueEntity houseFieldValue = fieldValueDao.get(community, houseField);
        if (houseFieldValue != null) {
            String houseFieldValueString = houseFieldValue.getStringValue();
            if (!StringUtils.isBlank(houseFieldValueString)) {
                house = houseFieldValueString;
            }
        }

        FieldEntity houseDescriptionField = fieldDao.getByInternalName("COMMUNITY_LEGAL_F_HOUSE_DESCRIPTION"); // Дом
        FieldValueEntity houseDescriptionFieldValue = fieldValueDao.get(community, houseDescriptionField);
        if (houseDescriptionFieldValue != null) {
            String houseDescriptionFieldValueString = houseDescriptionFieldValue.getStringValue();
            if (!StringUtils.isBlank(houseDescriptionFieldValueString)) {
                houseDescription = houseDescriptionFieldValueString;
            }
        }

        FieldEntity officeField = fieldDao.getByInternalName("COMMUNITY_LEGAL_F_OFFICE"); // Офис
        FieldValueEntity officeFieldValue = fieldValueDao.get(community, officeField);
        if (officeFieldValue != null) {
            String officeFieldValueString = officeFieldValue.getStringValue();
            if (!StringUtils.isBlank(officeFieldValueString)) {
                office = officeFieldValueString;
            }
        }

        FieldEntity officeDescriptionField = fieldDao.getByInternalName("COMMUNITY_LEGAL_F_OFFICE_DESCRIPTION"); // Офис
        FieldValueEntity officeDescriptionFieldValue = fieldValueDao.get(community, officeDescriptionField);
        if (officeDescriptionFieldValue != null) {
            String officeDescriptionFieldValueString = officeDescriptionFieldValue.getStringValue();
            if (!StringUtils.isBlank(officeDescriptionFieldValueString)) {
                officeDescription = officeDescriptionFieldValueString;
            }
        }

        FieldEntity geoPositionField = fieldDao.getByInternalName("COMMUNITY_LEGAL_F_GEO_POSITION"); // Координаты
        FieldValueEntity geoPositionFieldValue = fieldValueDao.get(community, geoPositionField);
        if (geoPositionFieldValue != null) {
            String geoPositionFieldValueString = geoPositionFieldValue.getStringValue();
            if (!StringUtils.isBlank(geoPositionFieldValueString)) {
                geoPosition = geoPositionFieldValueString;
            }
        }

        FieldEntity geoLocationField = fieldDao.getByInternalName("COMMUNITY_LEGAL_F_GEO_LOCATION"); // Фактический адрес
        FieldValueEntity geoLocationFieldValue = fieldValueDao.get(community, geoLocationField);
        if (geoLocationFieldValue != null) {
            String geoLocationFieldValueString = geoLocationFieldValue.getStringValue();
            if (!StringUtils.isBlank(geoLocationFieldValueString)) {
                geoLocation = geoLocationFieldValueString;
            }
        }


        region = AddressUtils.getRegionName(region);
        regionDescription = AddressUtils.getRegionDescription(regionDescription);
        area = AddressUtils.getAreaName(area);
        areaDescription = AddressUtils.getAreaDescription(areaDescription);
        city = AddressUtils.getCityName(city);
        cityDescription = AddressUtils.getCityDescription(cityDescription);
        street = AddressUtils.getStreetName(street);
        streetDescription = AddressUtils.getStreetDescription(streetDescription);
        house = AddressUtils.getHouseName(house);
        houseDescription = AddressUtils.getHouseDescription(houseDescription);
        office = AddressUtils.getOfficeName(office);
        officeDescription = AddressUtils.getOfficeDescription(officeDescription);

        SupportAddress result = new SupportAddress();

        result.setCountry(country);

        result.setPostalCode(postalCode);

        result.setRegion(AddressUtils.getRegionName(region));
        if (!StringUtils.isBlank(regionDescription)) {
            result.setRegionDescription(regionDescription);
        } else {
            result.setRegionDescription(AddressUtils.getRegionDescription(region));
        }

        result.setArea(AddressUtils.getAreaName(area));
        if (!StringUtils.isBlank(areaDescription)) {
            result.setAreaDescription(areaDescription);
        } else {
            result.setAreaDescription(AddressUtils.getAreaDescription(area));
        }

        result.setCity(city);
        if (!StringUtils.isBlank(cityDescription)) {
            result.setCityDescription(cityDescription);
        } else {
            result.setCityDescription(AddressUtils.getCityDescription(city));
        }

        result.setStreet(street);
        if (!StringUtils.isBlank(streetDescription)) {
            result.setStreetDescription(streetDescription);
        } else {
            result.setStreetDescription(AddressUtils.getStreetDescription(street));
        }

        result.setHouse(house);
        if (!StringUtils.isBlank(houseDescription)) {
            result.setHouseDescription(houseDescription);
        } else {
            result.setHouseDescription(AddressUtils.getHouseDescription(house));
        }

        result.setOffice(office);
        if (!StringUtils.isBlank(officeDescription)) {
            result.setOfficeDescription(officeDescription);
        } else {
            result.setOfficeDescription(AddressUtils.getOfficeDescription(office));
        }

        result.setGeoPosition(geoPosition);
        result.setGeoLocation(geoLocation);

        processSupportAddress(result);
        return result;
    }

    public SupportAddress getCommunityRegistrationSupportAddress(CommunityEntity community) {
        String country = "";
        String postalCode = "";

        String region = "";
        String regionDescription = "";

        String area = "";
        String areaDescription = "";

        String city = "";
        String cityDescription = "";

        String street = "";
        String streetDescription = "";

        String house = "";
        String houseDescription = "";

        String office = "";
        String officeDescription = "";

        String geoPosition = "";
        String geoLocation = "";

        FieldEntity countryField = fieldDao.getByInternalName("COMMUNITY_LEGAL_COUNTRY"); // Страна
        FieldValueEntity countryFieldValue = fieldValueDao.get(community, countryField);
        if (countryFieldValue != null) {
            String countryFieldValueString = countryFieldValue.getStringValue();
            if (!StringUtils.isBlank(countryFieldValueString)) {
                try {
                    Long id = Long.parseLong(countryFieldValueString);
                    country = rameraListEditorItemDAO.getById(id).getText();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        FieldEntity regionField = fieldDao.getByInternalName("COMMUNITY_LEGAL_REGION"); // Регион
        FieldValueEntity regionFieldValue = fieldValueDao.get(community, regionField);
        if (regionFieldValue != null) {
            String regionFieldValueString = regionFieldValue.getStringValue();
            if (!StringUtils.isBlank(regionFieldValueString)) {
                region = regionFieldValueString;
            }
        }

        FieldEntity regionDescriptionField = fieldDao.getByInternalName("COMMUNITY_LEGAL_REGION_DESCRIPTION"); // Регион
        FieldValueEntity regionDescriptionFieldValue = fieldValueDao.get(community, regionDescriptionField);
        if (regionDescriptionFieldValue != null) {
            String regionDescriptionFieldValueString = regionDescriptionFieldValue.getStringValue();
            if (!StringUtils.isBlank(regionDescriptionFieldValueString)) {
                regionDescription = regionDescriptionFieldValueString;
            }
        }


        FieldEntity areaField = fieldDao.getByInternalName("COMMUNITY_LEGAL_AREA"); // Район
        FieldValueEntity areaFieldValue = fieldValueDao.get(community, areaField);
        if (areaFieldValue != null) {
            String areaFieldValueString = areaFieldValue.getStringValue();
            if (!StringUtils.isBlank(areaFieldValueString)) {
                area = areaFieldValueString;
            }
        }

        FieldEntity areaDescriptionField = fieldDao.getByInternalName("COMMUNITY_LEGAL_AREA_DESCRIPTION"); // Район
        FieldValueEntity areaDescriptionFieldValue = fieldValueDao.get(community, areaDescriptionField);
        if (areaDescriptionFieldValue != null) {
            String areaDescriptionFieldValueString = areaDescriptionFieldValue.getStringValue();
            if (!StringUtils.isBlank(areaDescriptionFieldValueString)) {
                areaDescription = areaDescriptionFieldValueString;
            }
        }

        FieldEntity cityField = fieldDao.getByInternalName("COMMUNITY_LEGAL_LOCALITY"); // Населённый пукнкт
        FieldValueEntity cityFieldValue = fieldValueDao.get(community, cityField);
        if (cityFieldValue != null) {
            String cityFieldValueString = cityFieldValue.getStringValue();
            if (!StringUtils.isBlank(cityFieldValueString)) {
                city = cityFieldValueString;
            }
        }

        FieldEntity cityDescriptionField = fieldDao.getByInternalName("COMMUNITY_LEGAL_LOCALITY_DESCRIPTION"); // Населённый пукнкт
        FieldValueEntity cityDescriptionFieldValue = fieldValueDao.get(community, cityDescriptionField);
        if (cityDescriptionFieldValue != null) {
            String cityDescriptionFieldValueString = cityDescriptionFieldValue.getStringValue();
            if (!StringUtils.isBlank(cityDescriptionFieldValueString)) {
                cityDescription = cityDescriptionFieldValueString;
            }
        }


        FieldEntity streetField = fieldDao.getByInternalName("COMMUNITY_LEGAL_STREET"); // Улица
        FieldValueEntity streetFieldValue = fieldValueDao.get(community, streetField);
        if (streetFieldValue != null) {
            String streetFieldValueString = streetFieldValue.getStringValue();
            if (!StringUtils.isBlank(streetFieldValueString)) {
                street = streetFieldValueString;
            }
        }

        FieldEntity streetDescriptionField = fieldDao.getByInternalName("COMMUNITY_LEGAL_STREET_DESCRIPTION"); // Улица
        FieldValueEntity streetDescriptionFieldValue = fieldValueDao.get(community, streetDescriptionField);
        if (streetDescriptionFieldValue != null) {
            String streetDescriptionFieldValueString = streetDescriptionFieldValue.getStringValue();
            if (!StringUtils.isBlank(streetDescriptionFieldValueString)) {
                streetDescription = streetDescriptionFieldValueString;
            }
        }

        FieldEntity houseField = fieldDao.getByInternalName("COMMUNITY_LEGAL_HOUSE"); // Дом
        FieldValueEntity houseFieldValue = fieldValueDao.get(community, houseField);
        if (houseFieldValue != null) {
            String houseFieldValueString = houseFieldValue.getStringValue();
            if (!StringUtils.isBlank(houseFieldValueString)) {
                house = houseFieldValueString;
            }
        }

        FieldEntity houseDescriptionField = fieldDao.getByInternalName("COMMUNITY_LEGAL_HOUSE_DESCRIPTION"); // Дом
        FieldValueEntity houseDescriptionFieldValue = fieldValueDao.get(community, houseDescriptionField);
        if (houseDescriptionFieldValue != null) {
            String houseDescriptionFieldValueString = houseDescriptionFieldValue.getStringValue();
            if (!StringUtils.isBlank(houseDescriptionFieldValueString)) {
                houseDescription = houseDescriptionFieldValueString;
            }
        }

        FieldEntity officeField = fieldDao.getByInternalName("COMMUNITY_LEGAL_OFFICE"); // Офис
        FieldValueEntity officeFieldValue = fieldValueDao.get(community, officeField);
        if (officeFieldValue != null) {
            String officeFieldValueString = officeFieldValue.getStringValue();
            if (!StringUtils.isBlank(officeFieldValueString)) {
                office = officeFieldValueString;
            }
        }

        FieldEntity officeDescriptionField = fieldDao.getByInternalName("COMMUNITY_LEGAL_OFFICE_DESCRIPTION"); // Офис
        FieldValueEntity officeDescriptionFieldValue = fieldValueDao.get(community, officeDescriptionField);
        if (officeDescriptionFieldValue != null) {
            String officeDescriptionFieldValueString = officeDescriptionFieldValue.getStringValue();
            if (!StringUtils.isBlank(officeDescriptionFieldValueString)) {
                officeDescription = officeDescriptionFieldValueString;
            }
        }

        FieldEntity geoPositionField = fieldDao.getByInternalName("COMMUNITY_LEGAL_GEO_POSITION"); // Координаты
        FieldValueEntity geoPositionFieldValue = fieldValueDao.get(community, geoPositionField);
        if (geoPositionFieldValue != null) {
            String geoPositionFieldValueString = geoPositionFieldValue.getStringValue();
            if (!StringUtils.isBlank(geoPositionFieldValueString)) {
                geoPosition = geoPositionFieldValueString;
            }
        }

        FieldEntity geoLocationField = fieldDao.getByInternalName("COMMUNITY_LEGAL_GEO_LOCATION"); // Фактический адрес
        FieldValueEntity geoLocationFieldValue = fieldValueDao.get(community, geoLocationField);
        if (geoLocationFieldValue != null) {
            String geoLocationFieldValueString = geoLocationFieldValue.getStringValue();
            if (!StringUtils.isBlank(geoLocationFieldValueString)) {
                geoLocation = geoLocationFieldValueString;
            }
        }

        region = AddressUtils.getRegionName(region);
        regionDescription = AddressUtils.getRegionDescription(regionDescription);
        area = AddressUtils.getAreaName(area);
        areaDescription = AddressUtils.getAreaDescription(areaDescription);
        city = AddressUtils.getCityName(city);
        cityDescription = AddressUtils.getCityDescription(cityDescription);
        street = AddressUtils.getStreetName(street);
        streetDescription = AddressUtils.getStreetDescription(streetDescription);
        house = AddressUtils.getHouseName(house);
        houseDescription = AddressUtils.getHouseDescription(houseDescription);
        office = AddressUtils.getOfficeName(office);
        officeDescription = AddressUtils.getOfficeDescription(officeDescription);

        SupportAddress result = new SupportAddress();

        result.setCountry(country);

        result.setPostalCode(postalCode);

        result.setRegion(AddressUtils.getRegionName(region));
        if (!StringUtils.isBlank(regionDescription)) {
            result.setRegionDescription(regionDescription);
        } else {
            result.setRegionDescription(AddressUtils.getRegionDescription(region));
        }

        result.setArea(AddressUtils.getAreaName(area));
        if (!StringUtils.isBlank(areaDescription)) {
            result.setAreaDescription(areaDescription);
        } else {
            result.setAreaDescription(AddressUtils.getAreaDescription(area));
        }

        result.setCity(city);
        if (!StringUtils.isBlank(cityDescription)) {
            result.setCityDescription(cityDescription);
        } else {
            result.setCityDescription(AddressUtils.getCityDescription(city));
        }

        result.setStreet(street);
        if (!StringUtils.isBlank(streetDescription)) {
            result.setStreetDescription(streetDescription);
        } else {
            result.setStreetDescription(AddressUtils.getStreetDescription(street));
        }

        result.setHouse(house);
        if (!StringUtils.isBlank(houseDescription)) {
            result.setHouseDescription(houseDescription);
        } else {
            result.setHouseDescription(AddressUtils.getHouseDescription(house));
        }

        result.setOffice(office);
        if (!StringUtils.isBlank(officeDescription)) {
            result.setOfficeDescription(officeDescription);
        } else {
            result.setOfficeDescription(AddressUtils.getOfficeDescription(office));
        }

        result.setGeoPosition(geoPosition);
        result.setGeoLocation(geoLocation);

        processSupportAddress(result);
        return result;
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------------

    private void saveCountryFieldValue(LongIdentifiable object, FieldEntity countryField, String country) {
        String countryId = "";

        RameraListEditor editor = rameraListEditorDAO.getByName("country_id");
        RameraListEditorItem rlei = rameraListEditorItemDAO.getByEditorAndText(editor, country);
        if (rlei != null && !StringUtils.isBlank(rlei.getText())) {
            countryId = String.valueOf(rlei.getId());
        }

        saveFieldValue(object, countryField, countryId);
    }

    @Transactional
    private void saveFieldValue(LongIdentifiable object, FieldEntity field, String value) {
        FieldValueEntity fieldValue = fieldValueDao.get(object, field);
        if (fieldValue == null) {
            fieldValue = new FieldValueEntity();
            fieldValue.setField(field);
            fieldValue.setHidden(field.isHiddenByDefault());
            fieldValue.setObject(object);
        }
        fieldValue.setStringValue(value);
        fieldValueDao.saveOrUpdate(fieldValue);
    }

    public void saveSharerActualSupportAddress(UserEntity userEntity, SupportAddress address) {
        System.out.println("saveSharerActualSupportAddress: [" + userEntity + "] [" + address + "]");

        FieldEntity countryField = fieldDao.getByInternalName("FCOUNTRY_CL"); // Страна
        FieldEntity regionField = fieldDao.getByInternalName("FREGION_RL"); // Регион
        FieldEntity regionDescriptionField = fieldDao.getByInternalName("FREGION_RL_DESCRIPTION"); // Регион
        FieldEntity areaField = fieldDao.getByInternalName("FAREA_AL"); // Район
        FieldEntity areaDescriptionField = fieldDao.getByInternalName("FAREA_AL_DESCRIPTION"); // Район
        FieldEntity cityField = fieldDao.getByInternalName("FCITY_TL"); // Населённый пукнкт
        FieldEntity cityDescriptionField = fieldDao.getByInternalName("FCITY_TL_DESCRIPTION"); // Населённый пукнкт
        FieldEntity postalCodeField = fieldDao.getByInternalName("FPOSTAL_CODE"); // Почтовый индекс
        FieldEntity streetField = fieldDao.getByInternalName("FSTREET"); // Улица
        FieldEntity streetDescriptionField = fieldDao.getByInternalName("FSTREET_DESCRIPTION"); // Улица
        FieldEntity houseField = fieldDao.getByInternalName("FHOUSE"); // Дом
        FieldEntity houseDescriptionField = fieldDao.getByInternalName("FHOUSE_DESCRIPTION"); // Дом
        FieldEntity subHouseField = fieldDao.getByInternalName("FSUBHOUSE"); // Корпус
        FieldEntity roomField = fieldDao.getByInternalName("FROOM"); // Квартира
        FieldEntity roomDescriptionField = fieldDao.getByInternalName("FROOM_DESCRIPTION"); // Квартира
        FieldEntity geoPositionField = fieldDao.getByInternalName("F_GEO_POSITION"); // Координаты
        FieldEntity geoLocationField = fieldDao.getByInternalName("F_GEO_LOCATION"); // Фактический адрес

        saveCountryFieldValue(userEntity, countryField, address.getCountry());
        saveFieldValue(userEntity, regionField, address.getRegion());
        saveFieldValue(userEntity, regionDescriptionField, address.getRegionDescription());
        saveFieldValue(userEntity, areaField, address.getArea());
        saveFieldValue(userEntity, areaDescriptionField, address.getAreaDescription());
        saveFieldValue(userEntity, cityField, address.getCity());
        saveFieldValue(userEntity, cityDescriptionField, address.getCityDescription());
        saveFieldValue(userEntity, postalCodeField, address.getPostalCode());
        saveFieldValue(userEntity, streetField, address.getStreet());
        saveFieldValue(userEntity, streetDescriptionField, address.getStreetDescription());
        saveFieldValue(userEntity, houseField, address.getHouse());
        saveFieldValue(userEntity, houseDescriptionField, address.getHouseDescription());
        //saveFieldValue(sharer, subHouseField, address.getSubHouse());
        saveFieldValue(userEntity, roomField, address.getRoom());
        saveFieldValue(userEntity, roomDescriptionField, address.getRoomDescription());
        saveFieldValue(userEntity, geoPositionField, address.getGeoPosition());
        saveFieldValue(userEntity, geoLocationField, address.getFullAddress());
    }

    public void saveSharerRegistrationSupportAddress(UserEntity userEntity, SupportAddress address) {
        System.out.println("saveSharerRegistrationSupportAddress: [" + userEntity + "] [" + address + "]");

        FieldEntity countryField = fieldDao.getByInternalName("COUNTRY_CL"); // Страна
        FieldEntity regionField = fieldDao.getByInternalName("REGION_RL"); // Регион
        FieldEntity regionDescriptionField = fieldDao.getByInternalName("REGION_RL_DESCRIPTION"); // Регион
        FieldEntity areaField = fieldDao.getByInternalName("AREA_AL"); // Район
        FieldEntity areaDescriptionField = fieldDao.getByInternalName("AREA_AL_DESCRIPTION"); // Район
        FieldEntity cityField = fieldDao.getByInternalName("CITY_TL"); // Населённый пукнкт
        FieldEntity cityDescriptionField = fieldDao.getByInternalName("CITY_TL_DESCRIPTION"); // Населённый пукнкт
        FieldEntity postalCodeField = fieldDao.getByInternalName("POSTAL_CODE"); // Почтовый индекс
        FieldEntity streetField = fieldDao.getByInternalName("STREET"); // Улица
        FieldEntity streetDescriptionField = fieldDao.getByInternalName("STREET_DESCRIPTION"); // Улица
        FieldEntity houseField = fieldDao.getByInternalName("HOUSE"); // Дом
        FieldEntity houseDescriptionField = fieldDao.getByInternalName("HOUSE_DESCRIPTION"); // Дом
        FieldEntity subHouseField = fieldDao.getByInternalName("SUBHOUSE"); // Корпус
        FieldEntity roomField = fieldDao.getByInternalName("ROOM"); // Квартира
        FieldEntity roomDescriptionField = fieldDao.getByInternalName("ROOM_DESCRIPTION"); // Квартира
        FieldEntity geoPositionField = fieldDao.getByInternalName("GEO_POSITION"); // Координаты
        FieldEntity geoLocationField = fieldDao.getByInternalName("GEO_LOCATION"); // Фактический адрес

        saveCountryFieldValue(userEntity, countryField, address.getCountry());
        saveFieldValue(userEntity, regionField, address.getRegion());
        saveFieldValue(userEntity, regionDescriptionField, address.getRegionDescription());
        saveFieldValue(userEntity, areaField, address.getArea());
        saveFieldValue(userEntity, areaDescriptionField, address.getAreaDescription());
        saveFieldValue(userEntity, cityField, address.getCity());
        saveFieldValue(userEntity, cityDescriptionField, address.getCityDescription());
        saveFieldValue(userEntity, postalCodeField, address.getPostalCode());
        saveFieldValue(userEntity, streetField, address.getStreet());
        saveFieldValue(userEntity, streetDescriptionField, address.getStreetDescription());
        saveFieldValue(userEntity, houseField, address.getHouse());
        saveFieldValue(userEntity, houseDescriptionField, address.getHouseDescription());
        //saveFieldValue(sharer, subHouseField, address.getSubHouse());
        saveFieldValue(userEntity, roomField, address.getRoom());
        saveFieldValue(userEntity, roomDescriptionField, address.getRoomDescription());
        saveFieldValue(userEntity, geoPositionField, address.getGeoPosition());
        saveFieldValue(userEntity, geoLocationField, address.getFullAddress());
    }

    public void saveSharerRegistratorOfficeSupportAddress(UserEntity userEntity, SupportAddress address) {
        System.out.println("saveSharerRegistratorOfficeSupportAddress: [" + userEntity + "] [" + address + "]");

        FieldEntity countryField = fieldDao.getByInternalName("REGISTRATOR_OFFICE_COUNTRY"); // Страна
        FieldEntity regionField = fieldDao.getByInternalName("REGISTRATOR_OFFICE_REGION"); // Регион
        FieldEntity regionDescriptionField = fieldDao.getByInternalName("REGISTRATOR_OFFICE_REGION_DESCRIPTION"); // Регион
        FieldEntity areaField = fieldDao.getByInternalName("REGISTRATOR_OFFICE_DISTRICT"); // Район
        FieldEntity areaDescriptionField = fieldDao.getByInternalName("REGISTRATOR_OFFICE_DISTRICT_DESCRIPTION"); // Район
        FieldEntity cityField = fieldDao.getByInternalName("REGISTRATOR_OFFICE_CITY"); // Населённый пукнкт
        FieldEntity cityDescriptionField = fieldDao.getByInternalName("REGISTRATOR_OFFICE_CITY_DESCRIPTION"); // Населённый пукнкт
        FieldEntity postalCodeField = fieldDao.getByInternalName("REGISTRATOR_OFFICE_POSTAL_CODE"); // Почтовый индекс
        FieldEntity streetField = fieldDao.getByInternalName("REGISTRATOR_OFFICE_STREET"); // Улица
        FieldEntity streetDescriptionField = fieldDao.getByInternalName("REGISTRATOR_OFFICE_STREET_DESCRIPTION"); // Улица
        FieldEntity houseField = fieldDao.getByInternalName("REGISTRATOR_OFFICE_BUILDING"); // Дом
        FieldEntity houseDescriptionField = fieldDao.getByInternalName("REGISTRATOR_OFFICE_BUILDING_DESCRIPTION"); // Дом
        FieldEntity officeField = fieldDao.getByInternalName("REGISTRATOR_OFFICE_ROOM"); // Офис
        FieldEntity officeDescriptionField = fieldDao.getByInternalName("REGISTRATOR_OFFICE_ROOM_DESCRIPTION"); // Офис
        FieldEntity geoPositionField = fieldDao.getByInternalName("REGISTRATOR_OFFICE_GEO_POSITION"); // Координаты
        FieldEntity geoLocationField = fieldDao.getByInternalName("REGISTRATOR_OFFICE_GEO_LOCATION"); // Фактический адрес

        saveCountryFieldValue(userEntity, countryField, address.getCountry());
        saveFieldValue(userEntity, regionField, address.getRegion());
        saveFieldValue(userEntity, regionDescriptionField, address.getRegionDescription());
        saveFieldValue(userEntity, areaField, address.getArea());
        saveFieldValue(userEntity, areaDescriptionField, address.getAreaDescription());
        saveFieldValue(userEntity, cityField, address.getCity());
        saveFieldValue(userEntity, cityDescriptionField, address.getCityDescription());
        saveFieldValue(userEntity, postalCodeField, address.getPostalCode());
        saveFieldValue(userEntity, streetField, address.getStreet());
        saveFieldValue(userEntity, streetDescriptionField, address.getStreetDescription());
        saveFieldValue(userEntity, houseField, address.getHouse());
        saveFieldValue(userEntity, houseDescriptionField, address.getHouseDescription());
        saveFieldValue(userEntity, officeField, address.getOffice());
        saveFieldValue(userEntity, officeDescriptionField, address.getOfficeDescription());
        saveFieldValue(userEntity, geoPositionField, address.getGeoPosition());
        saveFieldValue(userEntity, geoLocationField, address.getFullAddress());
    }

    public void saveCommunityActualSupportAddress(CommunityEntity community, SupportAddress address) {
        System.out.println("saveCommunityActualSupportAddress: [" + community.getName() + "] [" + address + "]");

        FieldEntity countryField = fieldDao.getByInternalName("COMMUNITY_LEGAL_F_COUNTRY"); // Страна
        FieldEntity regionField = fieldDao.getByInternalName("COMMUNITY_LEGAL_F_REGION"); // Регион
        FieldEntity regionDescriptionField = fieldDao.getByInternalName("COMMUNITY_LEGAL_F_REGION_DESCRIPTION"); // Регион
        FieldEntity areaField = fieldDao.getByInternalName("COMMUNITY_LEGAL_F_AREA"); // Район
        FieldEntity areaDescriptionField = fieldDao.getByInternalName("COMMUNITY_LEGAL_F_AREA_DESCRIPTION"); // Район
        FieldEntity cityField = fieldDao.getByInternalName("COMMUNITY_LEGAL_F_LOCALITY"); // Населённый пукнкт
        FieldEntity cityDescriptionField = fieldDao.getByInternalName("COMMUNITY_LEGAL_F_LOCALITY_DESCRIPTION"); // Населённый пукнкт
        FieldEntity streetField = fieldDao.getByInternalName("COMMUNITY_LEGAL_F_STREET"); // Улица
        FieldEntity streetDescriptionField = fieldDao.getByInternalName("COMMUNITY_LEGAL_F_STREET_DESCRIPTION"); // Улица
        FieldEntity houseField = fieldDao.getByInternalName("COMMUNITY_LEGAL_F_HOUSE"); // Дом
        FieldEntity houseDescriptionField = fieldDao.getByInternalName("COMMUNITY_LEGAL_F_HOUSE_DESCRIPTION"); // Дом
        FieldEntity officeField = fieldDao.getByInternalName("COMMUNITY_LEGAL_F_OFFICE"); // Офис
        FieldEntity officeDescriptionField = fieldDao.getByInternalName("COMMUNITY_LEGAL_F_OFFICE_DESCRIPTION"); // Офис
        FieldEntity geoPositionField = fieldDao.getByInternalName("COMMUNITY_LEGAL_F_GEO_POSITION"); // Координаты
        FieldEntity geoLocationField = fieldDao.getByInternalName("COMMUNITY_LEGAL_F_GEO_LOCATION"); // Фактический адрес

        saveCountryFieldValue(community, countryField, address.getCountry());
        saveFieldValue(community, regionField, address.getRegion());
        saveFieldValue(community, regionDescriptionField, address.getRegionDescription());
        saveFieldValue(community, areaField, address.getArea());
        saveFieldValue(community, areaDescriptionField, address.getAreaDescription());
        saveFieldValue(community, cityField, address.getCity());
        saveFieldValue(community, cityDescriptionField, address.getCityDescription());
        saveFieldValue(community, streetField, address.getStreet());
        saveFieldValue(community, streetDescriptionField, address.getStreetDescription());
        saveFieldValue(community, houseField, address.getHouse());
        saveFieldValue(community, houseDescriptionField, address.getHouseDescription());
        saveFieldValue(community, officeField, address.getOffice());
        saveFieldValue(community, officeDescriptionField, address.getOfficeDescription());
        saveFieldValue(community, geoPositionField, address.getGeoPosition());
        saveFieldValue(community, geoLocationField, address.getFullAddress());
    }

    public void saveCommunityRegistrationSupportAddress(CommunityEntity community, SupportAddress address) {
        System.out.println("saveCommunityRegistrationSupportAddress: [" + community.getName() + "] [" + address + "]");

        FieldEntity countryField = fieldDao.getByInternalName("COMMUNITY_LEGAL_COUNTRY"); // Страна
        FieldEntity regionField = fieldDao.getByInternalName("COMMUNITY_LEGAL_REGION"); // Регион
        FieldEntity regionDescriptionField = fieldDao.getByInternalName("COMMUNITY_LEGAL_REGION_DESCRIPTION"); // Регион
        FieldEntity areaField = fieldDao.getByInternalName("COMMUNITY_LEGAL_AREA"); // Район
        FieldEntity areaDescriptionField = fieldDao.getByInternalName("COMMUNITY_LEGAL_AREA_DESCRIPTION"); // Район
        FieldEntity cityField = fieldDao.getByInternalName("COMMUNITY_LEGAL_LOCALITY"); // Населённый пукнкт
        FieldEntity cityDescriptionField = fieldDao.getByInternalName("COMMUNITY_LEGAL_LOCALITY_DESCRIPTION"); // Населённый пукнкт
        FieldEntity streetField = fieldDao.getByInternalName("COMMUNITY_LEGAL_STREET"); // Улица
        FieldEntity streetDescriptionField = fieldDao.getByInternalName("COMMUNITY_LEGAL_STREET_DESCRIPTION"); // Улица
        FieldEntity houseField = fieldDao.getByInternalName("COMMUNITY_LEGAL_HOUSE"); // Дом
        FieldEntity houseDescriptionField = fieldDao.getByInternalName("COMMUNITY_LEGAL_HOUSE_DESCRIPTION"); // Дом
        FieldEntity officeField = fieldDao.getByInternalName("COMMUNITY_LEGAL_OFFICE"); // Офис
        FieldEntity officeDescriptionField = fieldDao.getByInternalName("COMMUNITY_LEGAL_OFFICE_DESCRIPTION"); // Офис
        FieldEntity geoPositionField = fieldDao.getByInternalName("COMMUNITY_LEGAL_GEO_POSITION"); // Координаты
        FieldEntity geoLocationField = fieldDao.getByInternalName("COMMUNITY_LEGAL_GEO_LOCATION"); // Фактический адрес

        saveCountryFieldValue(community, countryField, address.getCountry());
        saveFieldValue(community, regionField, address.getRegion());
        saveFieldValue(community, regionDescriptionField, address.getRegionDescription());
        saveFieldValue(community, areaField, address.getArea());
        saveFieldValue(community, areaDescriptionField, address.getAreaDescription());
        saveFieldValue(community, cityField, address.getCity());
        saveFieldValue(community, cityDescriptionField, address.getCityDescription());
        saveFieldValue(community, streetField, address.getStreet());
        saveFieldValue(community, streetDescriptionField, address.getStreetDescription());
        saveFieldValue(community, houseField, address.getHouse());
        saveFieldValue(community, houseDescriptionField, address.getHouseDescription());
        saveFieldValue(community, officeField, address.getOffice());
        saveFieldValue(community, officeDescriptionField, address.getOfficeDescription());
        saveFieldValue(community, geoPositionField, address.getGeoPosition());
        saveFieldValue(community, geoLocationField, address.getFullAddress());
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------------

    // убирает лишние поля из адреса и меняет некоторые существующие.
    // данный метод служит для "сдружения" данных к оговоренному формату
    public void processSupportAddress(SupportAddress address) {
        String city = address.getCity();
        if (StringUtils.equalsIgnoreCase(city, "Москва")) {
            address.setArea("");
            address.setAreaDescription("");

            address.setRegion("Москва");
            address.setRegionDescription("Город");
        }

        if (StringUtils.equalsIgnoreCase(city, "Санкт-Петербург")) {
            address.setArea("");
            address.setAreaDescription("");

            address.setRegion("Санкт-Петербург");
            address.setRegionDescription("Город");
        }

        if (StringUtils.equalsIgnoreCase(city, "Севастополь")) {
            address.setArea("");
            address.setAreaDescription("");

            address.setRegion("Севастополь");
            address.setRegionDescription("Город");
        }

        // Если район является городским округом, то его можно опустить
        if (StringUtils.equalsIgnoreCase(address.getAreaDescription(), "Городской округ")) {
            address.setArea("");
            address.setAreaDescription("");
        }

        if (StringUtils.isBlank(address.getRegion())) {
            address.setRegionDescription("");
        }

        if (StringUtils.isBlank(address.getArea())) {
            address.setAreaDescription("");
        }

        if (StringUtils.isBlank(address.getCity())) {
            address.setCityDescription("");
        }

        if (StringUtils.isBlank(address.getStreet())) {
            address.setStreetDescription("");
        }

        if (StringUtils.isBlank(address.getHouse())) {
            address.setHouseDescription("");
        }

        if (StringUtils.isBlank(address.getRoom())) {
            address.setRoomDescription("");
        }

        if (StringUtils.isBlank(address.getOffice())) {
            address.setOfficeDescription("");
        }
    }
}
