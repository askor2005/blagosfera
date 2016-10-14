package ru.radom.kabinet.services.communities.organization.dto;

import lombok.Data;

import java.util.Map;

/**
 * Обёртка данных формы создания юр лица
 * Created by vgusev on 02.02.2016.
 */
@Data
public class CreateOrganizationTempDataDto {

    private String associationForm;

    private Map<String, Object> data;
}
