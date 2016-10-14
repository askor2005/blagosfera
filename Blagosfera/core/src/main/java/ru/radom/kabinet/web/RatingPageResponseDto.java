package ru.radom.kabinet.web;

import lombok.Data;
import ru.askor.blagosfera.domain.user.RatingDomain;

import java.util.List;

/**
 * Created by vtarasenko on 23.06.2016.
 */
public class RatingPageResponseDto {
    private int total;
    private List<RatingDomain> rows;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<RatingDomain> getRows() {
        return rows;
    }

    public void setRows(List<RatingDomain> rows) {
        this.rows = rows;
    }
}
