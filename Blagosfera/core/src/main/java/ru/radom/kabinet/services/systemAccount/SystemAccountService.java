package ru.radom.kabinet.services.systemAccount;

import ru.askor.blagosfera.domain.systemaccount.SystemAccount;

/**
 *
 * Created by vgusev on 14.04.2016.
 */
public interface SystemAccountService {

    SystemAccount getById(Long id);
}
