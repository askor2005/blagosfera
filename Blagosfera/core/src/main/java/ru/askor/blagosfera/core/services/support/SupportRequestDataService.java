package ru.askor.blagosfera.core.services.support;

import ru.askor.blagosfera.domain.support.SupportRequest;
import ru.askor.blagosfera.domain.support.SupportRequestStatus;

import java.util.List;

/**
 * Created by vtarasenko on 18.05.2016.
 */
public interface SupportRequestDataService {
    public void save(SupportRequest supportRequest);
    public void delete(Long id);

    SupportRequest getById(Long id);

    List<SupportRequest> search(SupportRequestStatus status, int page, int perPage);

    long count(SupportRequestStatus status);
}
