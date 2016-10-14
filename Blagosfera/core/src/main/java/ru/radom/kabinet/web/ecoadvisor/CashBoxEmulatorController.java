package ru.radom.kabinet.web.ecoadvisor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.radom.kabinet.dto.CommonResponseDto;
import ru.radom.kabinet.dto.SuccessResponseDto;
import ru.radom.kabinet.utils.CommonConstants;

/**
 *
 * Created by vgusev on 17.08.2016.
 */
@PreAuthorize("hasAnyRole('SUPERADMIN')")
@RestController
@RequestMapping(value = "/cashboxemu", /*CommonConstants.RESPONSE_JSON_MEDIA_TYPE, */produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
public class CashBoxEmulatorController extends CashboxAPIControllerBase {

    @RequestMapping(value = "test", method = RequestMethod.GET)
    public CommonResponseDto test() {
        return SuccessResponseDto.get();
    }
}
