package ru.radom.kabinet.web;

import lombok.Data;
import ru.askor.blagosfera.domain.user.RatingDomain;

/**
 * Created by vtarasenko on 22.06.2016.
 */
@Data
public class RatingResponseDto {
    private int count;
    private RatingDomain rating;
}
