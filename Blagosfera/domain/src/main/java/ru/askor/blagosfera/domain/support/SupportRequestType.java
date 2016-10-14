package ru.askor.blagosfera.domain.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by vtarasenko on 18.05.2016.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupportRequestType {
    private Long id;
    private String name;
    private String adminEmailsList;
}
