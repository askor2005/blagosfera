package ru.radom.kabinet.web.admin.dto;

import lombok.Data;
import ru.radom.kabinet.model.SmtpServer;

import java.util.List;

/**
 *
 * Created by vgusev on 07.05.2016.
 */
@Data
public class SmtpServerResponseListDto {

    private boolean isSuccess;
    private int total;
    private List<SmtpServerResponseDto> items;

    public static SmtpServerResponseListDto successDto(int total, List<SmtpServer> smtpServers) {
        SmtpServerResponseListDto result = new SmtpServerResponseListDto();
        result.setTotal(total);
        result.setItems(SmtpServerResponseDto.toDtoList(smtpServers));
        result.setSuccess(true);
        return result;
    }

    public static SmtpServerResponseListDto errorDto() {
        SmtpServerResponseListDto result = new SmtpServerResponseListDto();
        result.setSuccess(false);
        return result;
    }
}
