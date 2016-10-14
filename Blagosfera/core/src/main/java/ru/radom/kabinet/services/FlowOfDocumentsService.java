package ru.radom.kabinet.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dao.flowofdocuments.*;
import ru.radom.kabinet.document.model.*;
import ru.radom.kabinet.model.fields.FieldEntity;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.web.flowofdocuments.dto.DocumentTemplateParticipantDto;
import ru.radom.kabinet.web.flowofdocuments.dto.DocumentTemplateSaveRequestDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Сервис для работы с документооборотом
 */

@Service("flowOfDocumentsService")
@Transactional
public class FlowOfDocumentsService {
	@Autowired
	private DocumentTypeDao documentTypeDao;

	@Autowired
	private DocumentTypeParticipantDao documentTypeParticipantDao;

	@Autowired
	private DocumentTemplateDao documentTemplateDao;

	@Autowired
	private DocumentTemplateFilterValueDao documentTemplateFilterValueDao;

	@Autowired
	private DocumentTemplateParticipantDao documentTemplateParticipantDao;

	@Autowired
	private SharerDao sharerDao;

	public void saveDocumentType(DocumentClassEntity documentTypeForm) {
		DocumentClassEntity documentType = documentTypeDao.getById(documentTypeForm.getId());
		if (documentType == null) {
			documentType = new DocumentClassEntity();
		}
		if (documentTypeForm.getParent() != null && documentTypeForm.getParent().getId() != null &&
			documentType.getId() != null && documentType.getId().longValue() == documentTypeForm.getParent().getId().longValue()) {
			throw new RuntimeException("Нельзя прикреплять класс документов к самому себе!");
		}
		// Запрет одинаковых имён на одном уровне
		List<DocumentClassEntity> brotherDocumentTypes = documentTypeDao.getChildrenList(documentTypeForm.getParent());
		for (DocumentClassEntity brotherDocumentType : brotherDocumentTypes) {
			if (documentType.getId() != null && documentType.getId().longValue() != brotherDocumentType.getId().longValue() &&
				documentTypeForm.getName() != null && brotherDocumentType.getName().equalsIgnoreCase(documentTypeForm.getName())) {
				throw new RuntimeException("Нельзя создавать классы документов с одинаковыми именами на одном уровне!");
			}
		}

		documentType.setParent(documentTypeForm.getParent());
		documentType.setName(documentTypeForm.getName());
		documentType.setKey(documentTypeForm.getKey());
		if (documentType.getPosition() == null) {
			documentType.setPosition(0);
		}
		documentTypeDao.saveOrUpdate(documentType);

		//предыдущие участники шаблона
		List<DocumentClassDataSourceEntity> oldParticipants = documentTypeParticipantDao.getListByDocumentType(documentType);
		List<DocumentClassDataSourceEntity> currentParticipants = documentTypeForm.getParticipants();
		if (currentParticipants == null) {
			currentParticipants = new ArrayList<>();
		}
		List<Long> currentParticipantsIds = new ArrayList<>();
		Map<DocumentClassDataSourceEntity, Long> currentParticipantsMap = new HashMap<>();

		//зачистим пустые элементы
		for (DocumentClassDataSourceEntity participant : currentParticipants) {
			currentParticipantsMap.put(participant, participant.getId());
		}
		for(Map.Entry<DocumentClassDataSourceEntity, Long> entry : currentParticipantsMap.entrySet()) {
			if (entry.getValue() == null) {
				currentParticipants.remove(entry.getKey());
			}
		}
		currentParticipantsMap.clear();

		//сохраняем текущих участников шаблона
		for (DocumentClassDataSourceEntity participant : currentParticipants) {
			if (participant.getId() < 0 ) {
				participant.setId(null);
			} else {
				DocumentClassDataSourceEntity oldParticipant = documentTypeParticipantDao.getById(participant.getId());
				oldParticipant.setAssociationForm(participant.getAssociationForm());
				oldParticipant.setAssociationFormSearchType(participant.getAssociationFormSearchType());
				oldParticipant.setFieldsFilters(participant.getFieldsFilters());
				oldParticipant.setParticipantName(participant.getParticipantName());
				oldParticipant.setParticipantType(participant.getParticipantType());
				participant = oldParticipant;
			}

			participant.setDocumentType(documentType);
			documentTypeParticipantDao.saveOrUpdate(participant);
			//saveParticipantRecursive(participant);
			currentParticipantsIds.add(participant.getId());
		}
		//удаляем убранных учатников
		for (DocumentClassDataSourceEntity participant : oldParticipants) {
			if (!currentParticipantsIds.contains(participant.getId())) {
				documentTypeParticipantDao.delete(participant);
			}
		}
		oldParticipants.clear();
		currentParticipants.clear();
		currentParticipantsIds.clear();
	}

	public void deleteDocumentType(DocumentClassEntity documentType) {
		//удаляем шаблоны документов использующие данный класс
		List<DocumentTemplateEntity> documentTemplates = documentTemplateDao.getByDocumentType(documentType);
		for (DocumentTemplateEntity documentTemplate : documentTemplates) {
			deleteDocumentTemplate(documentTemplate);
		}
		//удаляем участников типа шаблона
		List<DocumentClassDataSourceEntity> documentTypeParticipant = documentTypeParticipantDao.getListByDocumentType(documentType);
		for (DocumentClassDataSourceEntity participant : documentTypeParticipant) {
			documentTypeParticipantDao.delete(participant);
		}
		//удаляем тип шаблона
		documentTypeDao.delete(documentType);
	}

	public DocumentTemplateEntity saveDocumentTemplate(DocumentTemplateSaveRequestDto documentTemplateForm) {
		DocumentTemplateEntity documentTemplate = documentTemplateDao.getById(documentTemplateForm.getId());
		if (documentTemplate == null) {
			documentTemplate = new DocumentTemplateEntity();
		} else {
			if (documentTemplate.getTemplateParticipants() != null) { // Удаляем старых участников шаблона
				for (DocumentTemplateParticipantEntity participant : documentTemplate.getTemplateParticipants()) {
					documentTemplateParticipantDao.delete(participant);
				}
			}
			// Делаем проверку на поля
			if (documentTemplateForm.getName() == null || documentTemplateForm.getName().equals("")){
				throw new RuntimeException("Имя шаблона не может быть пустым!");
			}
			if (documentTemplateForm.getContent() == null || documentTemplateForm.getContent().equals("")){
				throw new RuntimeException("Текст шаблона не может быть пустым!");
			}
		}
		String templateCode = null;
		if (documentTemplateForm.getCode() != null && !documentTemplateForm.getCode().equals("")){
			templateCode = documentTemplateForm.getCode();
			if (documentTemplate.getCode() == null || !documentTemplate.getCode().equals(templateCode)) {
				if (documentTemplateDao.getByCode(templateCode) != null) {
					throw new RuntimeException("Шаблон с таким кодом уже существует!");
				}
			}
		}

		documentTemplate.setCreator(sharerDao.getById(SecurityUtils.getUser().getId()));
		documentTemplate.setDocumentType(documentTypeDao.loadById(documentTemplateForm.getDocumentClassId()));
		documentTemplate.setName(documentTemplateForm.getName());
		documentTemplate.setDocumentShortName(documentTemplateForm.getDocumentShortName());
		documentTemplate.setDocumentName(documentTemplateForm.getDocumentName());
		documentTemplate.setContent(documentTemplateForm.getContent());
		documentTemplate.setCode(templateCode);
		documentTemplate.setHelpLink(documentTemplateForm.getHelpLink());
		if (documentTemplate.getPosition() == null) {
			documentTemplate.setPosition(0);
		}
		documentTemplate.setPdfExportArguments(documentTemplateForm.getPdfExportArguments());
		documentTemplateDao.saveOrUpdate(documentTemplate);

		List<DocumentTemplateParticipantDto> documentTemplateParticipants = documentTemplateForm.getTemplateParticipants();
		if (documentTemplateParticipants != null) {
			for (DocumentTemplateParticipantDto documentTemplateParticipant : documentTemplateParticipants) {
				DocumentTemplateParticipantEntity documentTemplateParticipantEntity = new DocumentTemplateParticipantEntity();
				documentTemplateParticipantEntity.setParentParticipantName(documentTemplateParticipant.getParentParticipantName());
				documentTemplateParticipantEntity.setParticipantName(documentTemplateParticipant.getParticipantName());
				documentTemplateParticipantEntity.setDocumentTemplate(documentTemplate);
				documentTemplateParticipantDao.saveOrUpdate(documentTemplateParticipantEntity);
			}
		}

		//если это новая модель тогда занаследуем из типа шаблона фильтры
		/*if (isNewDocumentTemplate) {
			List<DocumentClassDataSourceEntity> participants = documentTemplate.getDocumentType().getParticipants();
			for (DocumentClassDataSourceEntity participant : participants) {
				for (FieldEntity field : participant.getFieldsFilters()) {
					DocumentTemplateFilterValueEntity filter = new DocumentTemplateFilterValueEntity();
					filter.setDocumentTemplate(documentTemplate);
					filter.setParticipant(participant);
					filter.setFilterField(field);
					filter.setValue("");
					documentTemplateFilterValueDao.save(filter);
				}
			}
		} else {

			//предыдущие фильтры шаблона
			List<DocumentTemplateFilterValueEntity> oldFilters = documentTemplateFilterValueDao.getByDocumentTemplate(documentTemplate);
			List<DocumentTemplateFilterValueEntity> currentFilters = documentTemplateForm.getFilters();
			if (currentFilters == null) {
				currentFilters = new ArrayList<>();
			}
			List<Long> currentFiltersIds = new ArrayList<>();
			Map<DocumentTemplateFilterValueEntity, Long> currentFiltersMap = new HashMap<>();

			//зачистим пустые элементы
			for (DocumentTemplateFilterValueEntity filter : currentFilters) {
				currentFiltersMap.put(filter, filter.getId());
			}
			for (Map.Entry<DocumentTemplateFilterValueEntity, Long> entry : currentFiltersMap.entrySet()) {
				if (entry.getValue() == null) {
					currentFilters.remove(entry.getKey());
				}
			}
			currentFiltersMap.clear();

			//сохраняем текущие фильтры шаблона
			for (DocumentTemplateFilterValueEntity filter : currentFilters) {
				if (filter.getId() < 0) {
					filter.setId(null);
				}
				filter.setDocumentTemplate(documentTemplate);
				documentTemplateFilterValueDao.saveOrUpdate(filter);
				currentFiltersIds.add(filter.getId());
			}

			//удаляем убранные фильтры
			for (DocumentTemplateFilterValueEntity filter : oldFilters) {
				if (!currentFiltersIds.contains(filter.getId())) {
					documentTemplateFilterValueDao.delete(filter);
				}
			}
			oldFilters.clear();
			currentFilters.clear();
			currentFiltersIds.clear();
		}*/

		return documentTemplate;
	}

	public void deleteDocumentTemplate(DocumentTemplateEntity documentTemplate) {
		//удаляем фильтры шаблона документа
		List<DocumentTemplateFilterValueEntity> filtersValues = documentTemplateFilterValueDao.getByDocumentTemplate(documentTemplate);
		for (DocumentTemplateFilterValueEntity filterValue : filtersValues) {
			documentTemplateFilterValueDao.delete(filterValue);
		}
		// Удаляем участников шаблона
		List<DocumentTemplateParticipantEntity> templateParticipants = documentTemplate.getTemplateParticipants();
		if (templateParticipants != null) {
			for (DocumentTemplateParticipantEntity participant : templateParticipants) {
				documentTemplateParticipantDao.delete(participant);
			}
		}
		//удаляем шаблон документа
		documentTemplateDao.delete(documentTemplate);
	}


	/**
	 * Обновить позиции классов документов
	 * @param positionsData
	 */
	public void changePositionsDocumentTypes(Map<Long, Integer> positionsData) {
		for (Long documentTypeId : positionsData.keySet()) {
			DocumentClassEntity documentType = documentTypeDao.getById(documentTypeId);
			documentType.setPosition(positionsData.get(documentTypeId));
			documentTypeDao.saveOrUpdate(documentType);
		}
	}


	/**
	 * Обновить позиции шаблонов документов
	 * @param positionsData
	 */
	public void changePositionsDocumentTemplates(Map<Long, Integer> positionsData) {
		for (Long documentTemplateId : positionsData.keySet()) {
			DocumentTemplateEntity documentTemplate = documentTemplateDao.getById(documentTemplateId);
			documentTemplate.setPosition(positionsData.get(documentTemplateId));
			documentTemplateDao.saveOrUpdate(documentTemplate);
		}
	}
}
