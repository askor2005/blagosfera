package ru.radom.kabinet.web.admin.dto;

import java.util.List;

public class ActiveSessionsResultDto {

    public List<ActiveSessionDto> data;
    public int itemsCount;

    public ActiveSessionsResultDto(List<ActiveSessionDto> data) {
        this.data = data;
        this.itemsCount = data.size();
    }

    public ActiveSessionsResultDto(List<ActiveSessionDto> data, int itemsCount) {
        this(data);
        this.itemsCount = itemsCount;
    }
}
