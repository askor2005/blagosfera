package ru.radom.kabinet.web.lettersofauthority.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mnikitin on 03.08.2016.
 */
public class DelegatesResponseDto {

    public boolean success = true;
    public int total = 0;
    public List<DelegateDto> items = new ArrayList<>();
}
