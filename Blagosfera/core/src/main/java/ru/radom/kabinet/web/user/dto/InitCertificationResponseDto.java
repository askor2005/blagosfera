package ru.radom.kabinet.web.user.dto;

import ru.radom.kabinet.services.ProfileFilling;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mnikitin on 17.05.2016.
 */
public class InitCertificationResponseDto {

    public UserDataDto registrator;
    public UserDataDto sharer;
    public String sharer_short_name_padeg_2;
    public ProfileFilling sharerProfileFilling;
    public boolean isAllowWriteCard;
    public boolean isAllowSaveFinger;
    public boolean isAllowSetVerified;
    public String sessionId;
    public String sessionTimer;
    public List<CertificationDocTypeDto> docTypes = new ArrayList<>();

    public InitCertificationResponseDto() {
    }
}
