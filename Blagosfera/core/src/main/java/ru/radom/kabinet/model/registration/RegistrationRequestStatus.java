package ru.radom.kabinet.model.registration;

import java.util.Objects;

/**
 * Created by vzuev on 18.03.2015.
 */
public enum RegistrationRequestStatus {

    NEW,      // новая заявка, проставляется при создании
    DELETED,  // отклонена заявителем
    CANCELED, // отменена регистратором
    PROCESSED; // обработана

    public static RegistrationRequestStatus getByName(final String name){
        for(final RegistrationRequestStatus status: RegistrationRequestStatus.values()){
            if(Objects.equals(status.name(), name)) return status;
        }
        return null;
    }
}
