package ru.radom.kabinet.web.invite.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.askor.blagosfera.domain.invite.InviteRelationshipTypeDomain;

import java.util.List;

/**
 * Created by vtarasenko on 17.04.2016.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvitePageDto {
    private boolean verified;
    private boolean admin;
    private List<InviteRelationshipTypeDomain> inviteRelationShipTypes;
    private InviteCountDto inviteCount;
}
