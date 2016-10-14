package ru.radom.kabinet.document.services;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.data.jpa.repositories.document.DocumentTemplateRepository;
import ru.askor.blagosfera.domain.document.DocumentTemplate;
import ru.radom.kabinet.dao.flowofdocuments.DocumentTemplateDao;
import ru.radom.kabinet.document.model.DocumentTemplateEntity;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 *
 * Created by vgusev on 06.04.2016.
 */
@Service
@Transactional
public class DocumentTemplateDataServiceImpl implements DocumentTemplateDataService {

    @Autowired
    private DocumentTemplateRepository documentTemplateRepository;

    @Autowired
    private DocumentTemplateDao documentTemplateDao;

    @Override
    public DocumentTemplate getById(Long id) {
        return DocumentTemplateEntity.toDomainSafe(documentTemplateRepository.findOne(id), true);
    }

    @Override
    public DocumentTemplate getByCode(String code) {
        return DocumentTemplateEntity.toDomainSafe(documentTemplateRepository.findByCode(code), true);
    }

    @Override
    public List<DocumentTemplate> getAll() {
        return DocumentTemplateEntity.toDomainList(documentTemplateRepository.findAll(), true);
    }

    @Override
    public List<DocumentTemplate> getFilteredTemplate(String namePart, Long templateClassId, Integer page, Integer perPage) {
        Function<Criterion[], List<DocumentTemplateEntity>> fun;
        if (page != null && perPage != null) {
            fun = c -> documentTemplateDao.find(perPage * (page - 1), perPage, c);
        } else {
            fun = documentTemplateDao::find;
        }
        Criterion[] criterions;
        if (templateClassId == null) {
            criterions = new Criterion[]{
                    Restrictions.ilike("name", namePart.trim().replaceAll("\\s+", "%"), MatchMode.ANYWHERE)
            };
        } else {
            criterions = new Criterion[]{
                    Restrictions.ilike("name", namePart.trim().replaceAll("\\s+", "%"), MatchMode.ANYWHERE),
                    Restrictions.eq("documentType.id", templateClassId)
            };
        }
        List<DocumentTemplateEntity> documentTemplateList = fun.apply(criterions);
        if (CollectionUtils.isEmpty(documentTemplateList)) {
            //noinspection unchecked
            return Collections.EMPTY_LIST;
        }
        return DocumentTemplateEntity.toDomainList(documentTemplateList, true);

    }
}
