package ru.radom.kabinet.services.taxcode;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import ru.askor.blagosfera.domain.Address;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.services.communities.CommunityDataService;
import ru.radom.kabinet.services.sharer.UserDataService;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by vgusev on 16.02.2016.
 */
@Service
public class TaxCodeServiceIml implements TaxCodeService {

    /**
     * УРЛ сервиса получения данных по налоговому органу
     */
    private static final String TAX_SYSTEM_SERVICE_URL = "https://service.nalog.ru/static/kladr-edit.html";

    private static final String REGION_PARAMETER_NAME = "region";

    private static final String ADDRESS_STRING_PARAMETER_NAME = "addr";

    private static final String HOUSE_PARAMETER_NAME = "house";

    private static final Map<String, String> DEFAULT_PARAMETERS = new HashMap<String, String>(){{
        put("c", "complete");
        put("flags", "1111");
    }};

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private CommunityDataService communityDomainService;

    @Override
    public String getCodeByAddress(String region, String house, String addressString) {
        //addressString = "ПЕРМЬ Г,МИРА УЛ";
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.setAll(DEFAULT_PARAMETERS);
        parameters.add(REGION_PARAMETER_NAME, region);
        parameters.add(ADDRESS_STRING_PARAMETER_NAME, addressString.toUpperCase());
        parameters.add(HOUSE_PARAMETER_NAME, house);

        TaxCodeResponseDto taxCodeResponseDto = restTemplate.postForObject(TAX_SYSTEM_SERVICE_URL, parameters, TaxCodeResponseDto.class);
        return taxCodeResponseDto.getIfns();
    }

    @Override
    public String getCodeBySharer(User user) {
        Address address = user.getRegistrationAddress();
        String regionCode = user.getFieldValueByInternalName("REGION_CODE");
        String districtShortDescription = user.getFieldValueByInternalName("DISTRICT_DESCRIPTION_SHORT");
        String cityShortDescription = user.getFieldValueByInternalName("CITY_DESCRIPTION_SHORT");
        String streetShortDescription = user.getFieldValueByInternalName("STREET_DESCRIPTION_SHORT");

        return getCodeByAddress(address, regionCode, districtShortDescription, cityShortDescription, streetShortDescription);
    }

    @Override
    public String getCodeBySharerId(Long sharerId) {
        return getCodeBySharer(userDataService.getByIdFullData(sharerId));
    }

    @Override
    public String getCodeByCommunity(Community community) {
        Address address = community.getCommunityData().getRegistrationAddress();
        String regionCode = community.getCommunityData().getFieldValueByInternalName("COMMUNITY_LEGAL_REGION_CODE");
        String districtShortDescription = community.getCommunityData().getFieldValueByInternalName("COMMUNITY_LEGAL_DISTRICT_DESCRIPTION_SHORT");
        String cityShortDescription = community.getCommunityData().getFieldValueByInternalName("COMMUNITY_LEGAL_CITY_DESCRIPTION_SHORT");
        String streetShortDescription = community.getCommunityData().getFieldValueByInternalName("COMMUNITY_LEGAL_STREET_DESCRIPTION_SHORT");

        return getCodeByAddress(address, regionCode, districtShortDescription, cityShortDescription, streetShortDescription);
    }

    @Override
    public String getCodeByCommunityId(Long communityId) {
        return getCodeByCommunity(communityDomainService.getByIdFullData(communityId));
    }

    private String getCodeByAddress(Address address, String regionCode, String districtShortDescription, String cityShortDescription, String streetShortDescription) {
        String result = "";
        districtShortDescription = StringUtils.isBlank(districtShortDescription) ? "Р-Н" : districtShortDescription;
        cityShortDescription = StringUtils.isBlank(cityShortDescription) ? "Г" : cityShortDescription;
        streetShortDescription = StringUtils.isBlank(streetShortDescription) ? "УЛ" : streetShortDescription;
        if (address != null) {
            StringBuilder stringBuilder = new StringBuilder();
            if (!StringUtils.isBlank(address.getDistrict())) {
                stringBuilder.append(address.getDistrict()).append(" ").append(districtShortDescription).append(",");
            }
            if (!StringUtils.isBlank(address.getCity())) { // Если город не установлен - значит город федерального значения
                stringBuilder.append(address.getCity()).append(" ").append(cityShortDescription).append(",");
            }
            if (!StringUtils.isBlank(address.getStreet())) {
                stringBuilder.append(address.getStreet()).append(" ").append(streetShortDescription);
            }

            result = getCodeByAddress(regionCode, address.getBuilding(), stringBuilder.toString());
        }
        return result;
    }
}
