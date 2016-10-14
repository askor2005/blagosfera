package ru.askor.blagosfera.domain.community;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * Created by vgusev on 31.03.2016.
 */
@Data
public class CommunityInventoryUnitType implements Serializable {

    public static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String internalName;
}
