package ru.radom.kabinet.web.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Created by vtarasenko on 01.04.2016.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageEditionDto {
    private Date date;
    private String editorShortName;
}
