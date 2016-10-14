package ru.askor.blagosfera.domain.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.math.BigDecimal;

/**
 * Created by vtarasenko on 27.04.2016.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDomain {
    private BigDecimal raAmount;
    private BigDecimal rurAmount;
    private double rameraComission;
    private String sender;
    private String receiver;
    private String additionalData;
    private BigDecimal rameraComissionAmount;
    private String system;
}
