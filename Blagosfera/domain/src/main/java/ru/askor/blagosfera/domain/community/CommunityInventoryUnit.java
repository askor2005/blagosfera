package ru.askor.blagosfera.domain.community;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * Created by vgusev on 31.03.2016.
 */
@Data
public class CommunityInventoryUnit implements Serializable {

    public static final long serialVersionUID = 1L;

    public static final String DEFAULT_PHOTO = "https://images.blagosfera.su/images/VGHF3HUFH5J/FUEPMLPHDC.png";

    private Long id;

    private String number;

    private String guid;

    private String description;

    private String photo;

    private CommunityInventoryUnitType type;

    private CommunityMember responsible;

    private Community community;

    private Community leasedTo;
}
