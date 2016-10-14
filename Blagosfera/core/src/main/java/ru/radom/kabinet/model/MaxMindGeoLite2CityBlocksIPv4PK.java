package ru.radom.kabinet.model;

import java.io.Serializable;

/**
 * Created by ebelyaev on 19.08.2015.
 */
public class MaxMindGeoLite2CityBlocksIPv4PK implements Serializable {
    private Long ipFrom;
    private Long ipTo;

    public MaxMindGeoLite2CityBlocksIPv4PK() {
    }

    public MaxMindGeoLite2CityBlocksIPv4PK(Long ipFrom, Long ipTo) {
        this.ipFrom = ipFrom;
        this.ipTo = ipTo;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MaxMindGeoLite2CityBlocksIPv4PK that = (MaxMindGeoLite2CityBlocksIPv4PK) o;

        if (!ipFrom.equals(that.ipFrom)) return false;
        return ipTo.equals(that.ipTo);

    }

    @Override
    public int hashCode() {
        int result = ipFrom.hashCode();
        result = 31 * result + ipTo.hashCode();
        return result;
    }
}
