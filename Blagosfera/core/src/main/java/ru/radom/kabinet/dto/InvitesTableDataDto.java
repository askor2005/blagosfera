package ru.radom.kabinet.dto;

import lombok.Data;

import java.util.List;

/**
 *
 * Created by vgusev on 27.04.2016.
 */
@Data
public class InvitesTableDataDto {

    private List<InviteDto> invites;

    private int count;
}
