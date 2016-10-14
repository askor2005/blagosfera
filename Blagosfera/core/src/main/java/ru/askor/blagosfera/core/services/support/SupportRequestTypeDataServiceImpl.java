package ru.askor.blagosfera.core.services.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.data.jpa.entities.support.SupportRequestTypeEntity;
import ru.askor.blagosfera.data.jpa.repositories.support.SupportRequestTypeRepository;
import ru.askor.blagosfera.domain.support.SupportRequestType;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * сервис для работы с supportRequestDataTypes
 */
@Service
@Transactional
public class SupportRequestTypeDataServiceImpl implements SupportRequestTypeDataService {
    @Autowired
    private SupportRequestTypeRepository supportRequestTypeRepository;
    @Override
    public List<SupportRequestType> findAll(){
        return supportRequestTypeRepository.findAll().
                stream().map(supportRequestTypeEntity -> supportRequestTypeEntity.toDomain()).collect(Collectors.toList());
    }
    @Override
    public SupportRequestType getById(Long id) {
        SupportRequestTypeEntity supportRequestTypeEntity = supportRequestTypeRepository.findOne(id);
        return supportRequestTypeEntity != null ? supportRequestTypeEntity.toDomain() : null;
    }
}
