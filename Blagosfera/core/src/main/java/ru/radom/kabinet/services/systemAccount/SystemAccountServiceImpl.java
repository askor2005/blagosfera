package ru.radom.kabinet.services.systemAccount;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.data.jpa.repositories.SystemAccountRepository;
import ru.askor.blagosfera.domain.systemaccount.SystemAccount;
import ru.radom.kabinet.model.notifications.SystemAccountEntity;

/**
 *
 * Created by vgusev on 14.04.2016.
 */
@Service
@Transactional
public class SystemAccountServiceImpl implements SystemAccountService {

    @Autowired
    private SystemAccountRepository systemAccountRepository;

    @Override
    public SystemAccount getById(Long id) {
        return SystemAccountEntity.toDomainSafe(systemAccountRepository.findOne(id));
    }
}
