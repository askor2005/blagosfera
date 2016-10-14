package ru.radom.kabinet.web.admin.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.support.SupportRequestType;

import java.util.List;

/**
 * Created by vtarasenko on 18.05.2016.
 */
@Data
public class SupportRequestInitDataDto {
    private List<SupportRequestType> supportRequestTypes;
    private long totalRequestsCount;
}
