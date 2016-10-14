package ru.askor.blagosfera.core.services.support;

import ru.askor.blagosfera.domain.support.SupportRequestType;

import java.util.List;

/**
 * Created by vtarasenko on 18.05.2016.
 */
public interface SupportRequestTypeDataService {
    List<SupportRequestType> findAll();

    SupportRequestType getById(Long id);
}
