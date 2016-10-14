package ru.radom.kabinet.web.admin.dto;

public class ActiveSessionsRequestDto {

    public String sessionId;
    public String username;
    public String device;
    public String os;
    public String browser;
    public String ip;
    public int pageSize;
    public int pageIndex;
    public String sortField;
    public String sortOrder;
}
