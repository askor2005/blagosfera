package ru.radom.kabinet.services.bpmhandlers.voting.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.radom.kabinet.json.TimeStampDateSerializer;

import java.util.Date;
import java.util.Map;

/**
 * Обёртка для ответа на клиентскую часть через веб сокет
 * Created by vgusev on 04.01.2016.
 */
@Data
@AllArgsConstructor
public class BPMBatchVotingResponseDto {

    private Long id;

    @JsonSerialize(using = TimeStampDateSerializer.class)
    private Date startDate;

    @JsonSerialize(using = TimeStampDateSerializer.class)
    private Date endDate;

    @JsonSerialize(using = TimeStampDateSerializer.class)
    private Date registrationDateEnd;

    @JsonSerialize(using = TimeStampDateSerializer.class)
    private Date currentServerDate;

    private String subject;

    private Map<String, String> additionalParameters;

    public BPMBatchVotingResponseDto(){}
}
