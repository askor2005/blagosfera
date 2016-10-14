package ru.askor.blagosfera.web.controllers.ng.user;

        import lombok.Data;

        import java.util.HashMap;
        import java.util.Map;

/**
 * Created by vtarasenko on 03.08.2016.
 */
@Data
public class CountryCodeMappingDto {
    Map<String,String> idToCountryMapping = new HashMap<>();
    Map<String,String> countryToIdMapping = new HashMap<>();
}
