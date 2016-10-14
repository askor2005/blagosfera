package ru.radom.kabinet.web.user.dto;

/**
 *
 * Created by vgusev on 06.06.2016.
 */
public class ServerTimeDto {

    public long timeStamp;

    public int timeZoneOffset;

    public ServerTimeDto(long timeStamp, int timeZoneOffset) {
        this.timeStamp = timeStamp;
        this.timeZoneOffset = timeZoneOffset;
    }
}
