package ru.askor.blagosfera.web.controllers.ng.accounts.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.askor.blagosfera.domain.account.TransactionState;
import ru.radom.kabinet.json.TimeStampDateSerializer;
import ru.radom.kabinet.web.admin.dto.TransactionPlainModel;

import java.util.Date;

/**
 * Created by vtarasenko on 25.04.2016.
 */
@NoArgsConstructor
@Data
public class TransactionsSearchDto {

    private int page = 1;
    private int perPage = 20;
    private Long accountTypeId;
    @JsonSerialize(using = TimeStampDateSerializer.class)
    private Date fromDate;
    @JsonSerialize(using = TimeStampDateSerializer.class)
    private Date toDate;
    private TransactionPlainModel.Type type;
    private TransactionState state;
}
