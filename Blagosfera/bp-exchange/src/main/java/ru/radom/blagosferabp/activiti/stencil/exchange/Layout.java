package ru.radom.blagosferabp.activiti.stencil.exchange;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by alex on 01.10.2015.<br/>
 * Используется в {@link Stencil#layout}
 */
@RequiredArgsConstructor(staticName = "of")
public class Layout {

    @Getter
    private final String type;
}
