package ru.radom.kabinet.utils;

import com.google.gson.Gson;
import ru.askor.blagosfera.crypt.domain.HttpException;
import ru.askor.blagosfera.crypt.tls.HTTP;
import ru.radom.kabinet.model.utils.YandexGeocoderResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by ebelyaev on 15.10.2015.
 */
public class YandexGeocoder {

    /**
     * Формирует url
     *
     * @param address "Москва, ул. Тверская, дом 7"
     * @return https://geocode-maps.yandex.ru/1.x/?format=json&geocode=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0,+%D0%A2%D0%B2%D0%B5%D1%80%D1%81%D0%BA%D0%B0%D1%8F+%D1%83%D0%BB%D0%B8%D1%86%D0%B0,+%D0%B4%D0%BE%D0%BC+7
     * @throws UnsupportedEncodingException
     */
    private static String createGeocoderUrl(String address) throws UnsupportedEncodingException {
        String url = "https://geocode-maps.yandex.ru/1.x/";
        String format = "format=json";
        String geocode = "geocode=" + URLEncoder.encode(address, "UTF-8");
        url += "?" + format + "&" + geocode;
        return url;
    }

    public static YandexGeocoderResponse doGeocoderRequest(String address) throws IOException, HttpException {
        String responseString = new HTTP().doGet(createGeocoderUrl(address)).getDataAsString();
        return new Gson().fromJson(responseString, YandexGeocoderResponse.class);
    }
}
