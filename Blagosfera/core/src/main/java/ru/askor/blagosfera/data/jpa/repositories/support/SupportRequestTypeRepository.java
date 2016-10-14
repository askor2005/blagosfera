package ru.askor.blagosfera.data.jpa.repositories.support;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.askor.blagosfera.data.jpa.entities.support.SupportRequestEntity;
import ru.askor.blagosfera.data.jpa.entities.support.SupportRequestTypeEntity;

/**
 * Created by vtarasenko on 18.05.2016.
 */
public interface SupportRequestTypeRepository extends JpaRepository<SupportRequestTypeEntity,Long> {
}
