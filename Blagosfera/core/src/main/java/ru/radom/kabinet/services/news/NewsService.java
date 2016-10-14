package ru.radom.kabinet.services.news;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.core.util.DateUtils;
import ru.askor.blagosfera.data.jpa.repositories.community.CommunityRepository;
import ru.askor.blagosfera.data.jpa.repositories.news.NewsAttachmentRepository;
import ru.askor.blagosfera.data.jpa.repositories.news.NewsFilterRepository;
import ru.askor.blagosfera.data.jpa.repositories.news.NewsRepository;
import ru.askor.blagosfera.data.jpa.repositories.news.NewsSubscribeRepository;
import ru.askor.blagosfera.data.jpa.repositories.rameraListEditor.RameraListEditorItemRepository;
import ru.askor.blagosfera.data.jpa.specifications.news.NewsFilterSpecifications;
import ru.askor.blagosfera.data.jpa.specifications.news.NewsSpecifications;
import ru.askor.blagosfera.domain.common.Tag;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.askor.blagosfera.domain.events.BlagosferaEvent;
import ru.askor.blagosfera.domain.events.BlagosferaEventPublisher;
import ru.askor.blagosfera.domain.events.community.CommunityMemberEvent;
import ru.askor.blagosfera.domain.events.news.NewsEvent;
import ru.askor.blagosfera.domain.events.news.NewsEventType;
import ru.askor.blagosfera.domain.events.user.ContactEvent;
import ru.askor.blagosfera.domain.events.user.SharerEvent;
import ru.askor.blagosfera.domain.events.user.SharerEventType;
import ru.askor.blagosfera.domain.news.NewsAttachmentDomain;
import ru.askor.blagosfera.domain.news.NewsItem;
import ru.askor.blagosfera.domain.news.filter.NewsFilterData;
import ru.askor.blagosfera.domain.news.filter.NewsFilterDataBuilder;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dao.communities.CommunityMemberDao;
import ru.radom.kabinet.dao.news.NewsDao;
import ru.radom.kabinet.dao.news.NewsSubscribeDao;
import ru.radom.kabinet.enums.system.SystemSetting;
import ru.radom.kabinet.json.SerializationManager;
import ru.radom.kabinet.model.Discriminators;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.common.TagEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.discussion.Discussion;
import ru.radom.kabinet.model.news.News;
import ru.radom.kabinet.model.news.NewsAttachment;
import ru.radom.kabinet.model.news.NewsFilterEntity;
import ru.radom.kabinet.model.news.NewsSubscribe;
import ru.askor.blagosfera.domain.RadomAccount;
import ru.radom.kabinet.model.rameralisteditor.RameraListEditorItem;
import ru.radom.kabinet.module.rameralisteditor.service.ListEditorItemDomainService;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.NewsException;
import ru.radom.kabinet.services.common.TagService;
import ru.radom.kabinet.services.communities.CommunitiesService;
import ru.radom.kabinet.services.discuss.DiscussionBuilder;
import ru.radom.kabinet.services.discuss.DiscussionService;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
@Service("newsService")
public class NewsService {

	@Autowired
	private NewsDao newsDao;

	@Autowired
	private NewsValidationService newsValidationService;

	@Autowired
	private NewsSubscribeDao newsSubscribeDao;

	@Autowired
	private NewsRepository newsRepository;

	@Autowired
	private NewsSubscribeRepository newsSubscribeRepository;

	@Autowired
	private NewsAttachmentRepository newsAttachmentRepository;

	@Autowired
	private DiscussionService discussionService;

	@Autowired
	private CommunitiesService communitiesService;

	@Autowired
	private CommunityRepository communityRepository;

	@Autowired
    private SerializationManager serializationManager;

	@Autowired
	private NewsLayersService newsLayersService;

	@Autowired
	private ListEditorItemDomainService rameraListEditorItemService;

	@Autowired
	private RameraListEditorItemRepository rameraListEditorItemRepository;

	@Autowired
	private SharerDao sharerDao;

	@Autowired
	private CommunityMemberDao communityMemberDao;

	@Autowired
	private NewsFilterRepository newsFilterRepository;

	@Autowired
	private TagService tagService;

	@Autowired
	private SettingsManager settingsManager;

    @Autowired
    private BlagosferaEventPublisher blagosferaEventPublisher;

	private void checkPermissions(RadomAccount author, RadomAccount scope) {
		if (author instanceof UserEntity && scope instanceof UserEntity) {
			if (!scope.equals(author)) {
				throw new NewsException("Нельзя создать новость от имени другого участника");
			}
		} else if (author instanceof UserEntity && scope instanceof CommunityEntity) {
			if (!communitiesService.hasPermission((CommunityEntity) scope, author.getId(), "NEWS_CREATE")) {
				throw new NewsException("У Вас нет прав писать новости в этом объединении");
			}
		}
	}

	@Transactional(readOnly = false)
	public NewsItem createNews(NewsItem newsItem) {

		//Первичная валидация
		if (newsItem.getAuthor() == null) {
			throw new NewsException("Автор не существует");
		}

		if (newsItem.getScope() == null) {
			throw new NewsException("Объект привязки не существует");
		}

		RadomAccount scope = null;

		//Выясняем, как работать с объектом привязки
		if (!(newsItem.getScope() instanceof UserEntity)) {
			//Если scope не sharer - работаем с ним, как с community
			if (!communityRepository.exists(newsItem.getScope().getId())) {
					throw new NewsException("Объект привязки не существует");
			}

			newsItem.setScope(communityRepository.findOne(newsItem.getScope().getId()));
		}

		checkPermissions(newsItem.getAuthor(), newsItem.getScope());

		//Глобальная валидация
		newsValidationService.validateNewsIsNotEmpty(newsItem);
		newsValidationService.validateDataConsistency(newsItem);
		newsValidationService.filterNews(newsItem);
		newsValidationService.validateAttachmentsCount(newsItem);
		newsValidationService.validateTagsCount(newsItem);

		//Подготавливаем вложения
		List<NewsAttachment> attachments = new ArrayList<>();

		for (NewsAttachmentDomain attachmentDomain : newsItem.getAttachments()) {
			attachments.add(new NewsAttachment(attachmentDomain));
		}

		RameraListEditorItem category = rameraListEditorItemRepository.findOne(newsItem.getCategory().getId());

		News news = new News(newsItem.getTitle(), newsItem.getText(),
				newsItem.getAuthor(), newsItem.getScope());
		news.setCategory(category);

		news = attachDiscussionAndSave(news);

		for (NewsAttachment attachment : attachments) {
			attachment.setNews(news);
			newsAttachmentRepository.save(attachment);
		}

		//Сохраняем теги
		List<Tag> tagsForUpdate = new ArrayList<>();
		List<TagEntity> tagEntities = tagService.saveTags(newsItem.getTags()).stream()
				.map(tag -> {
					tagsForUpdate.add(tag);
					return new TagEntity(tag);
				})
				.collect(Collectors.toList());

		news.setTags(tagEntities);

		tagService.updateUsageCount(tagsForUpdate);

        blagosferaEventPublisher.publishEvent(new NewsEvent(this, NewsEventType.CREATE, news));

		NewsItem result = newsLayersService.makeDomainForSharer(news, SecurityUtils.getUser().getId());
		result.setAttachments(newsItem.getAttachments());

		return result;
	}

	public News attachDiscussionAndSave(News news) {
		final RadomAccount author = news.getAuthor();
		if (author instanceof UserEntity) {
			UserEntity userEntity = (UserEntity) author;
			DiscussionBuilder discussionBuilder = new DiscussionBuilder(userEntity).title(news.getTitle()).content(news.getText()).scope(news.getScope());
			Discussion discussion = discussionService.createDiscussion(discussionBuilder);
			news.setDiscussion(discussion);
		}
		newsDao.saveOrUpdate(news);

		return news;
	}

	public NewsItem editNews(NewsItem newsItem, UserEntity editor) {

		//Первичная валидация
		if (editor == null) {
			throw new NewsException("Редактор не существует");
		}

		News news = newsDao.getById(newsItem.getId());

		if (news == null) {
			throw new NewsException("Новость не существует");
		}

		if (!checkEditPermissions(news, editor)) {
			throw new NewsException("У Вас нет прав для редактирования этой новости");
		}


		//Глобальная валидация
		newsValidationService.validateNewsIsNotEmpty(newsItem);
		newsValidationService.validateDataConsistency(newsItem);
		newsValidationService.filterNews(newsItem);
		newsValidationService.validateAttachmentsCount(newsItem);
		newsValidationService.validateTagsCount(newsItem);

		news.setTitle(newsItem.getTitle());
		news.setText(newsItem.getText());

		news.setEditDate(new Date());
		news.setEditCount(news.getEditCount() + 1);
		news.setEditor(editor);

		RameraListEditorItem category = rameraListEditorItemRepository.findOne(newsItem.getCategory().getId());
		news.setCategory(category);

		//Обновляем вложения
		news.getAttachments().clear();

		for (NewsAttachmentDomain attachmentDomain : newsItem.getAttachments()) {
			NewsAttachment attachment = new NewsAttachment(attachmentDomain);
			attachment.setNews(news);
			newsAttachmentRepository.save(attachment);
			news.getAttachments().add(attachment);
		}

		//Обновляем теги
		Set<Tag> tagForUpdate = news.getTags().stream()
				.map(tagEntity -> tagEntity.toDomain())
				.collect(Collectors.toSet());

		List<TagEntity> tagEntities = tagService.saveTags(newsItem.getTags()).stream()
				.map(tag -> {
					//Здесь собираем список для сохранения и дополняем множество для обновления использований
					tagForUpdate.add(tag);
					return new TagEntity(tag);
				})
				.collect(Collectors.toList());

		news.setTags(tagEntities);

		newsDao.update(news);
		tagService.updateUsageCount(Lists.newArrayList(tagForUpdate));

        blagosferaEventPublisher.publishEvent(new NewsEvent(this, NewsEventType.EDIT, news));

		NewsItem result = newsLayersService.makeDomainForSharer(news, SecurityUtils.getUser().getId());
		result.setAttachments(newsItem.getAttachments());

		return result;
	}


	private boolean checkEditPermissions(News news, UserEntity userEntity) {
			if (news.getAuthor().equals(userEntity)) {
				return true;
			}
			if (news.getScope().equals(userEntity)) {
				return true;
			}
			if (news.getScope() instanceof CommunityEntity) {
				return communitiesService.hasPermission((CommunityEntity) news.getScope(), userEntity.getId(), "NEWS_MODERATE");
			}
			return false;

	}

	public NewsSubscribe subscribe(Community scope, User user) {
		// TODO Переделать
		CommunityEntity community = communityRepository.findOne(scope.getId());
		UserEntity userEntity = sharerDao.getById(user.getId());
		return subscribe(community, userEntity);
	}

	public NewsSubscribe subscribe(User scope, User user) {
		// TODO Переделать
		UserEntity userEntityScope = sharerDao.getById(scope.getId());
		UserEntity userEntity = sharerDao.getById(user.getId());
		return subscribe(userEntityScope, userEntity);
	}

	public void unsubscribe(Community scope, User user) {
		// TODO Переделать
		CommunityEntity community = communityRepository.findOne(scope.getId());
		UserEntity userEntity = sharerDao.getById(user.getId());
		unsubscribe(community, userEntity);
	}

	public void unsubscribe(User scope, User user) {
		// TODO Переделать
		UserEntity userEntityScope = sharerDao.getById(scope.getId());
		UserEntity userEntity = sharerDao.getById(user.getId());
		unsubscribe(userEntityScope, userEntity);
	}

	@Deprecated
	public NewsSubscribe subscribe(LongIdentifiable scope, UserEntity userEntity) {
		NewsSubscribe newsSubscribe = newsSubscribeDao.get(scope, userEntity);
		if (newsSubscribe == null) {
			newsSubscribe = new NewsSubscribe(scope, userEntity);
			newsSubscribeDao.save(newsSubscribe);
		}
		return newsSubscribe;
	}

	@Deprecated
	public void unsubscribe(LongIdentifiable scope, UserEntity userEntity) {
		NewsSubscribe newsSubscribe = newsSubscribeDao.get(scope, userEntity);
		if (newsSubscribe != null) {
			newsSubscribeDao.delete(newsSubscribe);
		}
	}

	private boolean checkDeletePermission(News news, UserEntity userEntity) {
		if (news.getAuthor().equals(userEntity)) {
			return true;
		}
		if (news.getScope().equals(userEntity)) {
			return true;
		}
		if (news.getScope() instanceof CommunityEntity) {
			return communitiesService.hasPermission((CommunityEntity) news.getScope(), userEntity.getId(), "NEWS_MODERATE");
		}
		return false;
	}

	@Transactional
	public News deleteNews(News news, UserEntity userEntity) {
		if (!checkDeletePermission(news, userEntity)) {
			throw new NewsException("Вы не можете удалить эту новость");
		}
		// discussionService.prepareComments(news.getDiscussion());
		// discussionService.removeDiscussion(news.getDiscussion());
		// newsDao.delete(news);

		news.setDeleted(true);
		newsDao.update(news);

		tagService.updateUsageCount(news.getTags().stream()
				.map(tagEntity -> tagEntity.toDomain())
				.collect(Collectors.toList()));

        blagosferaEventPublisher.publishEvent(new NewsEvent(this, NewsEventType.DELETE, news));

		return news;
	}

    @EventListener
    public void onBlagosferaEvent(BlagosferaEvent event) {
		if (event instanceof SharerEvent) {
			SharerEvent sharerEvent = (SharerEvent) event;
			if (sharerEvent.getType().equals(SharerEventType.REGISTER)) {
				// TODO Переделать
				//subscribe(systemAccountDao.getById(SystemAccountEntity.BLAGOSFERA_ID), sharerEvent.getUser());
				subscribe(sharerEvent.getUser(), sharerEvent.getUser());
			}
		} else if (event instanceof ContactEvent) {
			ContactEvent contactEvent = (ContactEvent) event;

			switch (contactEvent.getType()) {
			case ACCEPTED:
				subscribe(contactEvent.getContact().getOther(), contactEvent.getContact().getUser());
				break;
			case DELETE:
				unsubscribe(contactEvent.getContact().getOther(), contactEvent.getContact().getUser());
				break;
			default:
				break;
			}
		} else if (event instanceof CommunityMemberEvent) {
			CommunityMemberEvent communityEvent = (CommunityMemberEvent) event;
			CommunityMember member = communityEvent.getMember();
			switch (communityEvent.getType()) {
			case ACCEPT_INVITE:
			case JOIN:
			case ACCEPT_REQUEST:
			case ADD_MEMBER:
				subscribe(member.getCommunity(), member.getUser());
				break;
			case EXCLUDE:
			case LEAVE:
				unsubscribe(member.getCommunity(), member.getUser());
				break;
			default:
				break;
			}
		}
	}

	public News setModerated(News news, UserEntity userEntity) {
		if (!checkModeratePermission(news, userEntity)) {
			throw new NewsException("Вы не можете модерировать эту новость");
		}
		news.setModerated(true);
		newsDao.update(news);
		return news;
	}

	private boolean checkModeratePermission(News news, UserEntity userEntity) {
		if (news.getScope() instanceof CommunityEntity) {
			return communitiesService.hasPermission((CommunityEntity) news.getScope(), userEntity.getId(), "NEWS_MODERATE");
		} else if (news.getScope() instanceof UserEntity) {
			return news.getScope().equals(userEntity);
		}
		return false;
	}

    public String list(CommunityEntity community, News lastLoaded, Integer perPage, boolean excludeModerated) {
        List<News> list = newsDao.getByScope(community, lastLoaded, perPage, excludeModerated);
        return serializationManager.serializeCollection(list).toString();
    }



	public List<NewsItem> getBySharer(UserEntity user, NewsItem lastLoaded, int maxResults) {

		List<NewsItem> result = new ArrayList<>();

		//Получаем объект с критериями фильтров
		NewsFilterEntity newsFilterDataEntity = newsFilterRepository.findOneBySharerWithoutCommunity(user.getId());

		NewsFilterData newsFilterData;

		if (newsFilterDataEntity != null) {
			newsFilterData = newsFilterDataEntity.toDomain();
		} else {
			NewsFilterDataBuilder builder = new NewsFilterDataBuilder();
			newsFilterData = builder.createNewsFilterData();
		}


		//Получаем идентификаторы всех подписок пользователя (на новости пользователей отдельно,
		// а на новости объединений отдельно - это нужно во избежание пересечений их идентификаторов)
		List<Long> sharerScopeIds = new ArrayList<>();
		List<Long> communityScopeIds = new ArrayList<>();
		List<NewsSubscribe> subscribes = newsSubscribeRepository.findAllByUser_Id(user.getId());

		for (NewsSubscribe subscribe : subscribes) {

			if (Discriminators.SHARER.equals(subscribe.getScopeType())) {
				sharerScopeIds.add(subscribe.getScopeId());
			} else if (Discriminators.COMMUNITY.equals(subscribe.getScopeType())) {
				communityScopeIds.add(subscribe.getScopeId());
			}

		}

		Long lastLoadedId = null;

		if (lastLoaded != null) {
			lastLoadedId = lastLoaded.getId();
		}

		//Генерируем спецификации
		Specification<News> subscribesToSharersSpec = NewsSpecifications.scopeIdIn(sharerScopeIds);
		Specification<News> subscribesToCommunitiesSpec = NewsSpecifications.scopeIdIn(communityScopeIds);
		Specification<News> scopeTypeIsSharerSpec = NewsFilterSpecifications.inScopeWithType(Discriminators.SHARER);
		Specification<News> scopeTypeIsCommunitySpec = NewsFilterSpecifications.inScopeWithType(Discriminators.COMMUNITY);
		Specification<News> idLessThanSpec = NewsSpecifications.idLessThan(lastLoadedId);
		Specification<News> authorIdSpec = NewsFilterSpecifications.authorIdIs(newsFilterData.getAuthorId());
		Specification<News> categoryIdSpec = NewsFilterSpecifications.categoryIdIn(rameraListEditorItemService.getDescendantIdsWithParent(newsFilterData.getCategoryId()));
        Specification<News> dateBetweenSpec;

        if (newsFilterData.getDateTo() != null) {
			Date dateTo = newsFilterData.getDateTo();
			if (dateTo instanceof java.sql.Date) {
				dateTo = new Date(dateTo.getTime());
			}
			ZonedDateTime dateTime = ZonedDateTime.ofInstant(dateTo.toInstant(), ZoneId.systemDefault()).plusDays(1);
			dateBetweenSpec = NewsFilterSpecifications.dateBetween(newsFilterData.getDateFrom(), DateUtils.toDate(dateTime));
        } else {
            dateBetweenSpec = NewsFilterSpecifications.dateBetween(newsFilterData.getDateFrom(), null);
        }

		Specification<News> isDeletedSpec = NewsFilterSpecifications.isDeleted(false);
		Specification<News> hasAnyTagSpec = NewsFilterSpecifications.hasAnyTag(newsFilterData.getTags().stream()
				.map(t -> new TagEntity(t))
				.collect(Collectors.toList()));

		PageRequest pageRequest = new PageRequest(0, maxResults, Sort.Direction.DESC, "date");

		Page<News> page = newsRepository.findAll(
				Specifications.where(
						(Specifications.where(subscribesToSharersSpec).and(scopeTypeIsSharerSpec))
								.or(Specifications.where(subscribesToCommunitiesSpec).and(scopeTypeIsCommunitySpec)))
						.and(authorIdSpec)
						.and(idLessThanSpec)
						.and(categoryIdSpec)
						.and(dateBetweenSpec)
						.and(isDeletedSpec)
						.and(hasAnyTagSpec),
				pageRequest);


		for (News news : page.getContent()) {
			result.add(newsLayersService.makeDomainForSharer(news, user.getId()));
		}

		return result;
	}

	public List<NewsItem> getByScope(Long communityId, NewsItem lastLoaded, int maxResults, boolean excludeModerated) {
		List<NewsItem> result = new ArrayList<>();

		//Получаем объект с критериями фильтров
		NewsFilterEntity newsFilterDataEntity = newsFilterRepository.findOneByUser_IdAndCommunity_Id(SecurityUtils.getUser().getId(), communityId);

		NewsFilterData newsFilterData;

		if (newsFilterDataEntity != null) {
			newsFilterData = newsFilterDataEntity.toDomain();
		} else {
			NewsFilterDataBuilder builder = new NewsFilterDataBuilder();
			newsFilterData = builder.createNewsFilterData();
		}

		//Получаем идентификаторы всех подписок пользователя
		List<Long> scopeIds = new ArrayList<>();

		Long lastLoadedId = null;

		if (lastLoaded != null) {
			lastLoadedId = lastLoaded.getId();
		}

		//Генерируем спецификации
		Specification<News> idLessThanSpec = NewsSpecifications.idLessThan(lastLoadedId);
		Specification<News> authorIdSpec = NewsFilterSpecifications.authorIdIs(newsFilterData.getAuthorId());
		Specification<News> categoryIdSpec = NewsFilterSpecifications.categoryIdIn(rameraListEditorItemService.getDescendantIdsWithParent(newsFilterData.getCategoryId()));
        Specification<News> dateBetweenSpec;

        if (newsFilterData.getDateTo() != null) {
			Date dateTo = newsFilterData.getDateTo();
			if (dateTo instanceof java.sql.Date) {
				dateTo = new Date(dateTo.getTime());
			}
				ZonedDateTime dateTime = ZonedDateTime.ofInstant(dateTo.toInstant(), ZoneId.systemDefault()).plusDays(1);
				dateBetweenSpec = NewsFilterSpecifications.dateBetween(newsFilterData.getDateFrom(), DateUtils.toDate(dateTime));
        } else {
            dateBetweenSpec = NewsFilterSpecifications.dateBetween(newsFilterData.getDateFrom(), null);
        }

        Specification<News> inScopeWithId = NewsFilterSpecifications.inScopeWithId(communityId);
		Specification<News> inScopeWithType = NewsFilterSpecifications.inScopeWithType(Discriminators.COMMUNITY);
		Specification<News> whereDeletedIsSpec = NewsFilterSpecifications.whereDeletedIs(false);
		Specification<News> isDeletedSpec = NewsFilterSpecifications.isDeleted(false);
		Specification<News> hasAnyTagSpec = NewsFilterSpecifications.hasAnyTag(newsFilterData.getTags().stream()
				.map(t -> new TagEntity(t))
				.collect(Collectors.toList()));
		Specification<News> whereModeratedIs;

		if (excludeModerated) {
			whereDeletedIsSpec = NewsFilterSpecifications.whereModeratedIs(false);
		} else {
			whereDeletedIsSpec = NewsFilterSpecifications.whereModeratedIs(null);
		}

		PageRequest pageRequest = new PageRequest(0, maxResults, Sort.Direction.DESC, "date");

		Page<News> page = newsRepository.findAll(Specifications.where(idLessThanSpec)
				.and(authorIdSpec)
				.and(categoryIdSpec)
				.and(dateBetweenSpec)
				.and(inScopeWithId)
				.and(inScopeWithType)
				.and(isDeletedSpec)
				.and(whereDeletedIsSpec)
				.and(hasAnyTagSpec), pageRequest);

		for (News news : page.getContent()) {
			result.add(newsLayersService.makeDomainForSharer(news, SecurityUtils.getUser().getId()));
		}

		return result;
	}

	@Transactional(readOnly = false)
	public NewsFilterData saveFilter(NewsFilterData newsFilterData) {

		UserEntity currentUser = sharerDao.getById(SecurityUtils.getUser().getId());
		validateNewsFilterDataForSharer(newsFilterData, currentUser);

		NewsFilterEntity newsFilterEntity;

		//Ищем существующий фильтр. В случае его отсутствия заводим новый.
		if (newsFilterData.getCommunityId() == null) {
			newsFilterEntity = newsFilterRepository.findOneBySharerWithoutCommunity(currentUser.getId());

			if (newsFilterEntity == null) {
				newsFilterEntity = new NewsFilterEntity();
				newsFilterEntity.setUser(currentUser);
			}
		} else {
			CommunityEntity community = communityRepository.findOne(newsFilterData.getCommunityId());
			newsFilterEntity = newsFilterRepository.findOneByUser_IdAndCommunity_Id(currentUser.getId(), community.getId());

			if (newsFilterEntity == null) {
				newsFilterEntity = new NewsFilterEntity();
				newsFilterEntity.setUser(currentUser);
				newsFilterEntity.setCommunity(community);
			}
		}

		//Обновляем базу тегов
		List<Tag> tags = tagService.saveTags(newsFilterData.getTags());

		//Записываем в него необходимые данные и сохраняем в базе
		newsFilterEntity.setAuthorId(newsFilterData.getAuthorId());
		newsFilterEntity.setCategoryId(newsFilterData.getCategoryId());
		newsFilterEntity.setDateFrom(newsFilterData.getDateFrom());
		newsFilterEntity.setDateTo(newsFilterData.getDateTo());
		newsFilterEntity.setTags(tags.stream()
				.map(t -> new TagEntity(t))
				.collect(Collectors.toList()));



		newsFilterEntity = newsFilterRepository.save(newsFilterEntity);

		return  newsFilterEntity.toDomain();
	}

	/**
	 * Позволяет провести валидацию фильтра новостей для указанного пользователя. При неудаче выбрасывает RuntimeException.
	 * @param newsFilterData данные фильтра
	 * @param user пользователь, для которого проводится валидация
	 */
	private void validateNewsFilterDataForSharer(NewsFilterData newsFilterData, UserEntity user) {

		if (newsFilterData.getCategoryId() != null) {
			if (!rameraListEditorItemRepository.exists(newsFilterData.getCategoryId())) {
				throw new RuntimeException("Указанной категории новостей не существует!");
			}
		}

		//Валидируем автора новостей, чей идентификатор указан в фильтре
		if (newsFilterData.getAuthorId() != null) {

			UserEntity author = sharerDao.getById(newsFilterData.getAuthorId());

			if (author == null) {
				throw new RuntimeException("Указанного автора не существует!");
			}

			//Теперь нужно проверить подписку на этого автора или на сообщество, в котором он состоит
			if (newsFilterData.getCommunityId() == null) {

				List<NewsSubscribe> subscribes = newsSubscribeRepository.findAllByUser_Id(user.getId());

				boolean subscribed = false;

				for (NewsSubscribe subscribe: subscribes) {
					if (subscribe.getScope() instanceof UserEntity && (subscribe.getScope().equals(author))) {
						subscribed = true;
						break;
					}
				}

				if (!subscribed) {
					throw  new RuntimeException("Вы не подписаны на новости указанного автора!");
				}
			} else {
				//Работаем с фильтром объединения
				if (!communityRepository.exists(newsFilterData.getCommunityId())) {
					throw new RuntimeException("Указанного объединения не существует!");
				}

				CommunityEntity community = communityRepository.findOne(newsFilterData.getCommunityId());

				List<NewsSubscribe> subscribes = newsSubscribeRepository.findAllByUser_Id(user.getId());

				boolean subscribed = false;

				if (!communityMemberDao.exists(community, author)) {
					throw new RuntimeException("Указанный автор не является членом указанного объединения!");
				}

			}
		}

		final int maxCountOfTags = Integer.valueOf(settingsManager.getSystemSetting(SystemSetting.NEWS_TAGS_MAX_COUNT.getKey(), "10"));

		if (newsFilterData.getTags().size() > maxCountOfTags) {
			throw new RuntimeException("Нельзя указывать более " + maxCountOfTags  +  " тегов.");
		}
	}

	/**
	 * Позволяет получить данные новостного фильтра пользователя по отношению к указанному объединению
	 * (или к деловому порталу, елси communityId -  null)
	 * @param communityId
	 * @return
	 */
	public NewsFilterData getNewsFilterData(Long communityId) {
		NewsFilterEntity newsFilterEntity;

		if (communityId == null) {
			//Получаем фильтр для делового портала
			newsFilterEntity = newsFilterRepository.findOneBySharerWithoutCommunity(SecurityUtils.getUser().getId());
		} else {

			CommunityEntity community = communityRepository.findOne(communityId);

			if (community == null) {
				throw new RuntimeException("Указанного сообщества не существует!");
			}

			//Получаем фильтр для объединения
			newsFilterEntity = newsFilterRepository.findOneByUser_IdAndCommunity_Id(SecurityUtils.getUser().getId(), community.getId());
		}

		//Если фильтр найден - возвращаем его domain отражение
		if (newsFilterEntity != null) {
			return newsFilterEntity.toDomain();
		} else {
			//Иначе возвращаем пустой фильтр
			NewsFilterDataBuilder builder = new NewsFilterDataBuilder();

			return builder.createNewsFilterData();
		}

	}

}
