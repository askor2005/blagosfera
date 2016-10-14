package ru.radom.kabinet.web.communities.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.askor.blagosfera.domain.community.CommunityMemberStatus;
import ru.radom.kabinet.json.TimeStampDateSerializer;
import ru.radom.kabinet.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * Created by vgusev on 01.04.2016.
 */
@Data
public class CommunityUserMemberDataDto {

    private Long id;
    private String email;
    private String firstName;
    private String shortName;
    private String secondName;
    private String lastName;
    private String fullName;
    private boolean deleted;
    private String link;
    private String avatar;
    private String ikp;
    private boolean isVerified;
    @JsonSerialize(using = TimeStampDateSerializer.class)
    private Date verificationDate;
    private boolean sex;
    private Long memberId;
    private CommunityMemberStatus memberStatus;
    @JsonSerialize(using = TimeStampDateSerializer.class)
    private Date memberRequestDate;
    private int requestHoursDistance;
    private boolean isCreator;
    private boolean online;

    public CommunityUserMemberDataDto(CommunityMember member) {
        setOnline(member.isOnline());
        setId(member.getUser().getId());
        setEmail(member.getUser().getEmail());
        setFirstName(member.getUser().getFirstName());
        setSecondName(member.getUser().getSecondName());
        setLastName(member.getUser().getLastName());
        setFullName(member.getUser().getFullName());
        setShortName(member.getUser().getShortName());
        setDeleted(member.getUser().isDeleted());
        setLink(member.getUser().getLink());
        setAvatar(member.getUser().getAvatar());
        setIkp(member.getUser().getIkp());
        setVerified(member.getUser().isVerified());
        setVerified(member.getUser().isVerified());
        setVerificationDate(member.getUser().getVerificationDate());
        setSex(member.getUser().isSex());
        setMemberId(member.getId());
        setMemberStatus(member.getStatus());
        setCreator(member.isCreator());
        if (member.getRequestDate() != null) {
            setMemberRequestDate(member.getRequestDate());
            setRequestHoursDistance(DateUtils.getDistanceHours(member.getRequestDate(), new Date()));
        }
    }

    public static List<CommunityUserMemberDataDto> toDtoList(List<CommunityMember> members) {
        List<CommunityUserMemberDataDto> result = null;
        if (members != null) {
            result = new ArrayList<>();
            for (CommunityMember member : members) {
                result.add(new CommunityUserMemberDataDto(member));
            }
        }
        return result;
    }
}
