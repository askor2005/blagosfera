package ru.askor.blagosfera.core.services.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.data.jpa.entities.support.SupportRequestEntity;
import ru.askor.blagosfera.data.jpa.repositories.support.SupportRequestRepository;
import ru.askor.blagosfera.data.jpa.repositories.support.SupportRequestTypeRepository;
import ru.askor.blagosfera.data.jpa.specifications.contacts.ContactSpecifications;
import ru.askor.blagosfera.data.jpa.specifications.support.SupportRequestsSpecifications;
import ru.askor.blagosfera.domain.support.SupportRequest;
import ru.askor.blagosfera.domain.support.SupportRequestStatus;
import ru.radom.kabinet.model.ContactEntity;
import ru.radom.kabinet.model.ContactStatus;
import ru.radom.kabinet.model.ContactsGroupEntity;
import ru.radom.kabinet.model.UserEntity;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by vtarasenko on 18.05.2016.
 */
@Service
@Transactional
public class SupportRequestDataServiceImpl implements SupportRequestDataService {
    @Autowired
    private SupportRequestRepository supportRequestRepository;
    @Autowired
    private SupportRequestTypeRepository supportRequestTypeRepository;
    @Override
    public void save(SupportRequest supportRequest) {
        SupportRequestEntity supportRequestEntity = null;
        if (supportRequest.getId() != null) {
            supportRequestEntity = supportRequestRepository.findOne(supportRequest.getId());
        }
        else {
            supportRequestEntity = new SupportRequestEntity();
        }
        supportRequestEntity.setDescription(supportRequest.getDescription());
        supportRequestEntity.setEmail(supportRequest.getEmail());
        supportRequestEntity.setTheme(supportRequest.getTheme());
        supportRequestEntity.setSupportRequestType(supportRequest.getType() != null ? supportRequestTypeRepository.findOne(supportRequest.getType().getId()) : null);
        supportRequestEntity.setStatus(supportRequest.getStatus());
        supportRequestRepository.saveAndFlush(supportRequestEntity);
    }

    @Override
    public void delete(Long id) {
        supportRequestRepository.delete(id);
    }

    @Override
    public SupportRequest getById(Long id) {
        SupportRequestEntity supportRequestEntity = supportRequestRepository.findOne(id);
        return supportRequestEntity != null ? supportRequestEntity.toDomain() : null;
    }
    @Override
    public List<SupportRequest> search(SupportRequestStatus status, int page, int perPage) {
        Specification<SupportRequestEntity> specification = buildSearchSpecification(status);
        PageRequest pageRequest = new PageRequest(page, perPage, new Sort(new Sort.Order(Sort.Direction.DESC, "id")));
        return supportRequestRepository.findAll(specification,pageRequest).getContent().stream().map(supportRequestTypeEntity -> supportRequestTypeEntity.toDomain()).collect(Collectors.toList());
    }

    @Override
    public long count(SupportRequestStatus status) {
        Specification<SupportRequestEntity> specification = buildSearchSpecification(status);
        return supportRequestRepository.count(specification);
    }

    private Specifications<SupportRequestEntity> buildSearchSpecification(SupportRequestStatus status) {
        Specifications<SupportRequestEntity> result = Specifications.where(SupportRequestsSpecifications.all());
        if (status != null) {
            result = result.and(SupportRequestsSpecifications.status(status));
        }

        return result;
    }
}
