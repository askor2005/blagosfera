package ru.radom.kabinet.web.communities.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.user.User;

/**
 *
 * Created by vgusev on 15.03.2016.
 */
@Data
public class CommunityVerifierUserDto {

    public long id;
    public String name;
    public String ikp;

    public static CommunityVerifierUserDto toDto(User user) {
        CommunityVerifierUserDto result = null;
        if (user != null) {
            result = new CommunityVerifierUserDto();
            result.setId(user.getId());
            result.setName(user.getFullName());
            result.setIkp(user.getIkp());
        }
        return result;
    }
}
