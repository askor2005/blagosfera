package ru.radom.kabinet.web.communities.organization.po.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.Address;

import java.util.List;

/**
 * Обёртка с данными филиалов и представительств ПО
 * Created by vgusev on 05.03.2016.
 */
@Data
public class RegisterPOBranchDto {
    /**
     * Наименование филиала\представительства
     */
    private String branchName;

    /**
     * Адресс филиала\представительства
     */
    private Address address;

    /**
     * Код города
     */
    private Long branchCountryId;

    /**
     * Список функций филиала
     */
    private List<BranchFunctionDto> branchFunctions;
}
