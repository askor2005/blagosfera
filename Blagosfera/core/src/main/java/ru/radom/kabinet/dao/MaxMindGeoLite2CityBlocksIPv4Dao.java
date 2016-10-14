package ru.radom.kabinet.dao;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.model.MaxMindGeoLite2CityBlocksIPv4;
import ru.radom.kabinet.model.MaxMindGeoLite2CityBlocksIPv4PK;
import ru.radom.kabinet.utils.IpUtils;

/**
 * Created by ebelyaev on 18.08.2015.
 */
@Repository("maxMindGeoLite2CityBlocksIPv4Dao")
public class MaxMindGeoLite2CityBlocksIPv4Dao extends AbstractDao<MaxMindGeoLite2CityBlocksIPv4, MaxMindGeoLite2CityBlocksIPv4PK> {
    public MaxMindGeoLite2CityBlocksIPv4 getByIp(String stringIp) {
        Long longIp = IpUtils.stringIpToLongIp(stringIp);
        return findFirst(Restrictions.le("ipFrom", longIp),Restrictions.ge("ipTo", longIp));
    }
}
