package ru.radom.kabinet.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import ru.radom.kabinet.json.TimeStampDateSerializer;

import java.util.Date;

/**
 *
 * Created by ebelyaev on 06.11.2015.
 */
@Data
public class InvitedUserDto {
    private String ikp;

    private Integer verified;

    @JsonSerialize(using = TimeStampDateSerializer.class)
    private Date verificationDate;
    @JsonSerialize(using = TimeStampDateSerializer.class)
    private Date registrationDate;

    private String verifier;
    private String verifierFirstName;
    private String verifierSecondName;
    private String verifierLastName;
    private Long verifierId;
    private String verifierIkp;


    private int registratorLevel;

    private int streamSharers;
    private int streamOrganizations;


}
