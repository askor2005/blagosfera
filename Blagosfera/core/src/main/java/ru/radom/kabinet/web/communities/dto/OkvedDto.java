package ru.radom.kabinet.web.communities.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.community.OkvedDomain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * Created by vgusev on 10.03.2016.
 */
@Data
public class OkvedDto {

    private Long id;

    private String code;

    private String shortName;

    private String longName;

    public OkvedDto() {}

    public OkvedDto(OkvedDomain okvedDomain) {
        setLongName(okvedDomain.getLongName());
        setShortName(okvedDomain.getShortName());
        setId(okvedDomain.getId());
        setCode(okvedDomain.getCode());
    }

    public static List<OkvedDto> toListDto(Collection<OkvedDomain> okveds) {
        List<OkvedDto> result = null;
        if (okveds != null) {
            result = new ArrayList<>();
            for (OkvedDomain okved : okveds) {
                result.add(new OkvedDto(okved));
            }
        }
        return result;
    }

    public OkvedDomain toDomain() {
        OkvedDomain result = new OkvedDomain();
        result.setId(getId());
        result.setCode(getCode());
        result.setShortName(getShortName());
        result.setLongName(getLongName());
        return result;
    }

    public static List<OkvedDomain> toListDomain(Collection<OkvedDto> okveds) {
        List<OkvedDomain> result = null;
        if (okveds != null) {
            result = new ArrayList<>();
            for (OkvedDto okved : okveds) {
                result.add(okved.toDomain());
            }
        }
        return result;
    }
}
