package ru.radom.kabinet.web.lettersofauthority.dto.request;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by vgusev on 08.10.2015.
 */
public class CreateLetterOfAuthorityRequest {

    public String roleKey;
    public String expiredDate;
    public Long radomAccountId;
    public Long delegateId;
    public Map<String, String> attributes = new HashMap<>();
}
