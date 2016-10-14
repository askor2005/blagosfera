package ru.radom.kabinet.dao;

import org.springframework.stereotype.Repository;
import ru.radom.kabinet.model.MaxMindGeoLite2CityLocationsEn;

/**
 * Created by ebelyaev on 18.08.2015.
 */
@Repository("maxMindGeoLite2CityLocationsEnDao")
public class MaxMindGeoLite2CityLocationsEnDao extends AbstractDao<MaxMindGeoLite2CityLocationsEn, Long> {
}
