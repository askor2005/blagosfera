package ru.radom.kabinet.web.admin.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mnikitin on 30.06.2016.
 */
public class TransactionListDto {

    public List<TransactionPlainModel> transactions = new ArrayList<>();
    public int number;
    public int numberOfElements;
    public long totalElements;
}
