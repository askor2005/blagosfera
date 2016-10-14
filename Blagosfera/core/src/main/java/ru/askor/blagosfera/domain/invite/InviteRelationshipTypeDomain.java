package ru.askor.blagosfera.domain.invite;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by vtarasenko on 15.04.2016.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InviteRelationshipTypeDomain {
    private Long id;
    private String name;
    private Integer index;
}
