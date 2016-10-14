package ru.askor.blagosfera.domain.user;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import ru.ramera.signer.common.json.TimeStampDateSerializer;

import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Created by vtarasenko on 22.06.2016.
 */
@Data
public class RatingDomain {
    private Double weight;
    @JsonSerialize(using = TimeStampDateSerializer.class)
    private Date created;
    private User user;
}
