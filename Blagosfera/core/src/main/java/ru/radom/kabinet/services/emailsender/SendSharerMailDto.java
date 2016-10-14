package ru.radom.kabinet.services.emailsender;

import lombok.Data;

/**
 *
 * Created by vgusev on 22.02.2016.
 */
@Data
public class SendSharerMailDto {

    private long requestTime;

    private String fio;

    private boolean result;

    private String error;

    private int countSharers;

    private int sharerIndex;

    public SendSharerMailDto(long requestTime, String fio, boolean result, String error, int countSharers, int sharerIndex) {
        this.requestTime = requestTime;
        this.fio = fio;
        this.result = result;
        this.error = error;
        this.countSharers = countSharers;
        this.sharerIndex = sharerIndex;
    }
}
