package ru.radom.kabinet.web.invite.dto;

import lombok.Data;

/**
 *
 * Created by vgusev on 19.04.2016.
 */
@Data
public class InviteCountDto {

    private int countRegisterd;

    private int countVerified;

    private int countRegistrators;

    Long registratorsLevel3InvitedSharersCount;
    Long registratorsLevel2InvitedSharersCount;
    Long registratorsLevel1InvitedSharersCount;

    //private int commonCount;
}
