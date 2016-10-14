package ru.radom.blagosferabp.activiti.component.stencil.rules;

import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.List;

/**
 * Created by alex on 01.10.2015.<br/>
 * Описание одной связи
 */
@Value
@RequiredArgsConstructor(staticName = "of")
public class Connect {

    /**
     * Роль, из которой выходит соединение
     */
    private final String from;

    /**
     * Роли, с которыми может быть соединена
     */
    private final List<String> to;
}
