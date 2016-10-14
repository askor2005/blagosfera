package ru.radom.kabinet.services;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.crypt.domain.HttpException;
import ru.askor.blagosfera.crypt.tls.HTTP;
import ru.askor.blagosfera.crypt.tls.Response;
import ru.askor.blagosfera.domain.Address;
import ru.radom.kabinet.utils.StringUtils;

@Service
public class GeoService {

    // В бесплатной версии API Карт можно делать не более 25 000 запросов к
    // геокодеру в сутки. Чтобы сократить число обращений, одинаковые ответы
    // геокодера можно кешировать. Заодно это снизит нагрузку на ваш сайт или
    // сервис.

    private static final Logger LOGGER = LoggerFactory.getLogger(GeoService.class);

    private static final String KLADR_TOKEN = "543f7e2a7c523985378b456e";

    private String getKladrQuery(Address address) {
        StringBuilder builder = new StringBuilder();

        builder.append(address.getRegion());
        if (builder.length() > 0) {
            builder.append(" ");
        }
//        builder.append(address.getDistrict());
//        if (builder.length() > 0) {
//            builder.append(" ");
//        }
        builder.append(address.getCity());
        if (builder.length() > 0) {
            builder.append(" ");
        }
        builder.append(address.getStreet());
        if (builder.length() > 0) {
            builder.append(" ");
        }
        builder.append(address.getBuilding());
        return builder.toString();
    }

    private String getYmapsQuery(Address address) {
        StringBuilder builder = new StringBuilder();

        builder.append(address.getCountry());
        if (builder.length() > 0) {
            builder.append(" ");
        }
        builder.append(address.getRegion());
        if (builder.length() > 0) {
            builder.append(" ");
        }
//        builder.append(address.getDistrict());
//        if (builder.length() > 0) {
//            builder.append(" ");
//        }
        builder.append(address.getCity());
        if (builder.length() > 0) {
            builder.append(" ");
        }
        builder.append(address.getStreet());
        if (builder.length() > 0) {
            builder.append(" ");
        }
        builder.append(address.getBuilding());
        return builder.toString();
    }

    public Address updateGeoData(Address address) {
        String kladrQuery = getKladrQuery(address);
        String ymapsQuery = getYmapsQuery(address);

        if (StringUtils.isEmpty(kladrQuery) || StringUtils.isEmpty(ymapsQuery)) {
            address.setGeoLocation("");
            address.setGeoPosition("");
        } else {
            try {
                HTTP http = new HTTP();
                Response kladrResponse = http.doGet("http://kladr-api.ru/api.php?query=" + kladrQuery + "&oneString=1&limit=1&withParent=1&token=" + KLADR_TOKEN);

                JSONObject response = new JSONObject(kladrResponse.getDataAsString());
                JSONObject result = response.getJSONArray("result").getJSONObject(0);

                address.setGeoLocation(result.getString("fullName"));

                Response ymapsResponse = http.doGet("http://geocode-maps.yandex.ru/1.x?geocode=" + ymapsQuery + "&format=json&results=1");

                response = new JSONObject(ymapsResponse.getDataAsString());

                String coordinatesString = response.getJSONObject("response").getJSONObject("GeoObjectCollection").getJSONArray("featureMember").getJSONObject(0).getJSONObject("GeoObject").getJSONObject("Point").getString("pos");
                address.setGeoPosition(coordinatesString.replace(" ", ","));
            } catch (HttpException e) {
                address.setGeoLocation("");
                address.setGeoPosition("");
                LOGGER.error(e.getMessage());
            }
        }

        return address;
    }
}
