package ru.radom.kabinet.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.core.services.account.AccountService;
import ru.askor.blagosfera.data.jpa.entities.account.AccountTypeEntity;
import ru.askor.blagosfera.domain.account.*;
import ru.askor.blagosfera.domain.events.file.ImagesEvent;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dao.applications.ApplicationDao;
import ru.radom.kabinet.dao.applications.SharerApplicationDao;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.applications.Application;
import ru.radom.kabinet.model.applications.SharerApplication;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.utils.Roles;
import ru.radom.kabinet.utils.StringUtils;

/*
 * 
 * Сервис по работе с приложениями
 * 
 */

@Service
public class ApplicationsService {

	@Autowired
	private ApplicationDao applicationDao;

	@Autowired
	private SharerApplicationDao sharerApplicationDao;

	@Autowired
	private AccountService accountService;

    @Autowired
    private SharerDao sharerDao;

	// скачивание приложения пользователем

	public SharerApplication downloadApplication(Long userId, Application application) {
        UserEntity userEntity = sharerDao.getById(userId);

		if (!application.isFree()) {
			throw new ApplicationException("Невозможно загрузить платное приложение. Платное приложение нужно купить.");
		}
		SharerApplication sharerApplication = sharerApplicationDao.get(userEntity.getId(), application);
		if (sharerApplication != null) {
			throw new ApplicationException("Данное приложение уже загружено.");
		}
		sharerApplication = new SharerApplication(userEntity, application);
		sharerApplicationDao.save(sharerApplication);
		return sharerApplication;
	}

	// покупка приложения пользователем

	public SharerApplication buyApplication(Long userId, Application application, AccountTypeEntity accountType) {
        UserEntity userEntity = sharerDao.getById(userId);

		if (application.isFree()) {
			throw new ApplicationException("Невозможно купить бесплатное приложение. Бесплатное приложение нужно загрузить.");
		}
		if (sharerApplicationDao.get(userId, application) != null) {
			throw new ApplicationException("Данное приложение уже куплено.");
		}

		final SharerApplication sharerApplication = new SharerApplication(userEntity, application);
		Account account = accountService.getUserAccount(userId, accountType.getId());
		try {
            TransactionDetail transactionDetail = new TransactionDetailBuilder()
                    .setType(TransactionDetailType.DEBIT)
                    .setAmount(application.getCost())
                    .setAccountId(account.getId())
                    .build();

            Transaction transaction = new TransactionBuilder()
                    .setAmount(application.getCost())
                    .setDescription("Покупка приложения [" + application.getName() + "]")
                    .addDetail(transactionDetail)
                    .setTransactionType(TransactionType.IAP)
                    .setParameter("applicationId", String.valueOf(application.getId()))
                    .build();

            transaction = accountService.submitTransaction(transaction);
            accountService.postTransaction(transaction.getId());

			sharerApplicationDao.save(sharerApplication);
            return sharerApplication;
		} catch (TransactionException te) {
			throw new ApplicationException("Ошибка при списании средств: " + te.getMessage());
		}
	}

	// установка приложения пользователем

	public SharerApplication installApplication(Long userId, Application application) {
        SharerApplication sharerApplication = sharerApplicationDao.get(userId, application);
		if (sharerApplication == null) {
			throw new ApplicationException("Перед установкой необходимо " + (application.isFree() ? "загрузить" : "купить") + " приложение");
		}
		if (sharerApplication.isInstalled()) {
			throw new ApplicationException("Данное приложение уже установлено");
		}
		sharerApplication.setInstalled(true);
		sharerApplication.setShowInMenu(true);
		sharerApplicationDao.update(sharerApplication);
		return sharerApplication;
	}

	// удаление приложения пользователем

	public SharerApplication uninstallApplication(Long userId, Application application) {
        SharerApplication sharerApplication = sharerApplicationDao.get(userId, application);
		if (sharerApplication == null || !sharerApplication.isInstalled()) {
			throw new ApplicationException("Данное приложение не установлено");
		}
		sharerApplication.setInstalled(false);
		sharerApplicationDao.update(sharerApplication);
		return sharerApplication;
	}

	// делает приложение видимымы влевом меню пользователя

	public SharerApplication showApplicationInMenu(Long userId, Application application) {
        SharerApplication sharerApplication = sharerApplicationDao.get(userId, application);
		if (sharerApplication == null || !sharerApplication.isInstalled()) {
			throw new ApplicationException("Данное приложение не установлено");
		}
		sharerApplication.setShowInMenu(true);
		sharerApplicationDao.update(sharerApplication);
		return sharerApplication;
	}

	// делает приложение невидимымы влевом меню пользователя

	public SharerApplication hideApplicationInMenu(Long userId, Application application) {
        SharerApplication sharerApplication = sharerApplicationDao.get(userId, application);
		if (sharerApplication == null || !sharerApplication.isInstalled()) {
			throw new ApplicationException("Данное приложение не установлено");
		}
		sharerApplication.setShowInMenu(false);
		sharerApplicationDao.update(sharerApplication);
		return sharerApplication;
	}

	// обработка событий, публикуемых другими компонентами системы, в данном
	// случае обработка загрузки картинки приложения

    @EventListener
	public void onImagesEvent(ImagesEvent event) {
        if (event.getObjectType().equals("application")) {
            Application application = applicationDao.getById(event.getObjectId());
            if (!SecurityUtils.getUserDetails().hasRole(Roles.ROLE_ADMIN)) {
                throw new ApplicationException("У Вас нет прав редактировать приложения");
            }
            application.setLogoUrl(event.getUrl());
            applicationDao.update(application);
        }
	}

	// сохранение приложение при создании или редактировании
	
	public Application saveApplication(Application application) {
		if (StringUtils.isEmpty(application.getName())) {
			throw new ApplicationException("Не задано название");
		}
		if (StringUtils.isEmpty(application.getIframeUrl())) {
			throw new ApplicationException("Не задан URL IFrame'а");
		}
		if (application.getFeaturesLibrarySection() == null) {
			throw new ApplicationException("Не задан раздел библиотеки возможностей");
		}
		// TODO Доработать модель для форм объединений на основе универсальных списков
		/*if (!application.isForCommunities()) {
			application.setCommunityAssociationForms(Collections.EMPTY_LIST);
		}*/
		applicationDao.saveOrUpdate(application);
		return application;
	}

	//  генерация идентификатора клиента и секретного ключа для приложения для нужд OAuth
	
	public Application generateClientIdAndSecret(Application application) {

		while (true) {
			application.setClientId(StringUtils.randomString(48));
			if (applicationDao.checkClientId(application.getClientId())) {
				break;
			}
		}
		application.setClientSecret(StringUtils.randomString(48));
		applicationDao.update(application);
		return application;
	}

}
