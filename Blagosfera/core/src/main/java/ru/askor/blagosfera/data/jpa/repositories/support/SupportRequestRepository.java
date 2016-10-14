package ru.askor.blagosfera.data.jpa.repositories.support;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.askor.blagosfera.data.jpa.entities.support.SupportRequestEntity;

/**
 * Created by vtarasenko on 18.05.2016.
 */
public interface SupportRequestRepository extends JpaRepository<SupportRequestEntity,Long>,JpaSpecificationExecutor<SupportRequestEntity> {
}
