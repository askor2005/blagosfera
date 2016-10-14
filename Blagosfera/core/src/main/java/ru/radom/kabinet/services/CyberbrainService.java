package ru.radom.kabinet.services;

import com.google.common.collect.ComparisonChain;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.radom.kabinet.dao.AbstractDao;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dao.communities.CommunityDao;
import ru.radom.kabinet.dao.cyberbrain.*;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.cyberbrain.*;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.tools.cyberbrain.ZipProcessor;
import ru.radom.kabinet.utils.DateUtils;
import ru.radom.kabinet.utils.StringUtils;
import sun.misc.Cleaner;
import sun.nio.ch.DirectBuffer;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

@Transactional
@Service("cyberbrainService")
public class CyberbrainService {
	public static class ServiceTags {
		public static final String NOT = "Нет";
		public static final String THIS = "Это";
		public static final String QUESTION = "?";
		public static final String MANY = "Множество";
		public static final String PROPERTY = "Свойство";
		public static final String MERA = "Мера";
		public static final String AFFECT = "Влияет";
		public static final String STATUS = "Состояние";
		public static final String COPY = "Копия";
	}

	private final static Logger logger = LoggerFactory.getLogger(CyberbrainService.class);

	@Autowired
	private JournalAttentionDao journalAttentionDao;

	@Autowired
	private ThesaurusDao thesaurusDao;

	@Autowired
	private KnowledgeRepositoryDao knowledgeRepositoryDao;

	@Autowired
	private UserTaskDao userTaskDao;

	@Autowired
	private UserSubtaskDao userSubtaskDao;

	@Autowired
	private SharerDao sharerDao;

    @Autowired
	private ScoreSharerDao scoreSharerDao;

    @Autowired
	private ScoreInfoDao scoreInfoDao;

    @Autowired
	private ScoreObjectDao scoreObjectDao;

    @Autowired
    private UserProblemDao userProblemDao;

    @Autowired
    private UserProblemPerformerDao userProblemPerformerDao;

	@Autowired
	private ZipProcessor zipProcessor;

    @Autowired
    private CyberbrainObjectDao cyberbrainObjectDao;

    @Autowired
    private CommunityDao communityDao;

	private CyberbrainObject getCyberbrainObjectByName(String name) {
        return cyberbrainObjectDao.getByName(name);
    }

	/**
	 * Рекурсивная функция для получения модели Thesaurus по синониму
	 * @param sinonim синоним
	 * @return Thesaurus model
	 */
	private Thesaurus getTagBySinonim(String sinonim, CommunityEntity community) {
		Thesaurus tag = thesaurusDao.getByEssence(sinonim, community);

		if (!tag.getSinonim().equals("")) {
			tag = getTagBySinonim(tag.getSinonim(), community);
		}

		return tag;
	}

	/**
	 * Функция для получения модели Thesaurus по названию сущьности
	 * В случае если сущьности в базе нет она будет созданна
	 * @param essence название сущности тега
	 * @param isServiceTag служебный тег
	 * @return Thesaurus
	 */
	private Thesaurus getTagByEssence(String essence, boolean isServiceTag, String sourceOrigin, Long source, Long communityId) {
        CommunityEntity community = null;

        if (communityId != null) {
            community = communityDao.getById(communityId);
        }

		Thesaurus tag = thesaurusDao.getByEssence(essence.toLowerCase().trim(), community);
		if (tag == null) {
			tag = new Thesaurus();
			tag.setIsServiceTag(isServiceTag);
			tag.setEssence(essence.trim());
			tag.setSinonim("");
			tag.setFixDateEssence(new Date(System.currentTimeMillis()));
			tag.setEssenceOwner(sharerDao.getById(SecurityUtils.getUser().getId()));
			tag.setAttentionFrequency(0);
			tag.setFrequencyEssence(0);
            tag.setIsPersonalData(false);
            tag.setIsObject(true);
            tag.setCommunity(community);
            tag.setSourceOrigin(getCyberbrainObjectByName(sourceOrigin));
            tag.setSource(source);

			thesaurusDao.save(tag);

            addScoreSharer(ScoreObjects.THESAURUS_NEW_RECORD.getName(), CyberbrainObjects.THESAURUS.getName(), tag.getId(), tag.getCommunity());

			if (!isServiceTag) {
				addRecordRepositoryOfKnowledge(tag, sourceOrigin, source, community);
			}
		}

		return tag;
	}

	/**
	 * Добавление записи в хранилище знаний
	 */
	private KnowledgeRepository addRecordRepositoryOfKnowledge(Thesaurus tag, String sourceOrigin, Long source, CommunityEntity community) {
        // Если базовых служеюных тегов нет создадим их
        getTagByEssence(ServiceTags.PROPERTY, true, sourceOrigin, source, null);
        getTagByEssence(ServiceTags.MANY, true, sourceOrigin, source, null);

		Thesaurus tagThis = getTagByEssence(ServiceTags.THIS, true, sourceOrigin, source, null);
		Thesaurus tagQuestion = getTagByEssence(ServiceTags.QUESTION, true, sourceOrigin, source, null);

		KnowledgeRepository knowledge = new KnowledgeRepository();
		knowledge.setFixTimeChange(new Date(System.currentTimeMillis()));
		knowledge.setTimeReady(null);
		knowledge.setOwner(tag.getEssenceOwner());
		knowledge.setTagOwner(tag);
		knowledge.setAttribute(tagThis);
		knowledge.setTag(tagQuestion);
		knowledge.setMera(0.0);
		knowledge.setTask(null);
		knowledge.setChangeIf(null);
		knowledge.setNext(null);
		knowledge.setStatus(1);
		knowledge.setStress(0.0);
		knowledge.setAttention(1.0);
        knowledge.setShowInQuestions(true);
        knowledge.setCommunity(community);
        knowledge.setSourceOrigin(getCyberbrainObjectByName(sourceOrigin));
        knowledge.setSource(source);
        knowledge.setIsTopical(null);

		knowledgeRepositoryDao.save(knowledge);

		return knowledge;
	}

	/**
	 * Добавление записи в журнал внимания
	 */
	public boolean addAttention(String jsonData, String sourceOrigin, Long source) {
		JSONObject jsonObject = new JSONObject(jsonData);

		Long communityId = Long.valueOf(jsonObject.get("community_id").toString());

		String strPerformerId = jsonObject.get("performer_id").toString();
		String strFixTimeKvant = jsonObject.get("fix_time_kvant").toString();
		String strAttentionKvant = jsonObject.get("attention_kvant").toString();

		UserEntity performer = strPerformerId.equals("") ? null : sharerDao.getById(Long.valueOf(strPerformerId));
		Date fixTimeKvant = strFixTimeKvant.equals("") ? null : DateUtils.stringToDate(strFixTimeKvant, DateUtils.Format.DATE_TIME);
		String textKvant = jsonObject.get("text_kvant").toString();
		String tagKvant = jsonObject.get("tag_kvant").toString();
		Integer attentionKvant = strAttentionKvant.equals("") ? 0 : Integer.valueOf(strAttentionKvant);

		if (performer == null) {
			performer = sharerDao.getById(SecurityUtils.getUser().getId());
		}

		if (fixTimeKvant == null) {
			fixTimeKvant = new Date(System.currentTimeMillis());
		}

		JournalAttention attention = new JournalAttention();
		attention.setFixTimeKvant(fixTimeKvant);
		attention.setTextKvant(textKvant);
		attention.setTagKvant(tagKvant);
		attention.setPerformerKvant(performer);
		attention.setAttentionKvant(attentionKvant);
		attention.setCommunity(communityDao.getById(communityId));
        attention.setSourceOrigin(getCyberbrainObjectByName(sourceOrigin));
        attention.setSource(source);
		journalAttentionDao.save(attention);

		// обновляем тезаурус
		updateThesaurus(attention, sourceOrigin, source);

        addScoreSharer(ScoreObjects.JOURNAL_ATTENTION_NEW_RECORD.getName(), CyberbrainObjects.JOURNAL_ATTENTION.getName(), attention.getId(), attention.getCommunity());

		return true;
	}

	/**
	 * Импорт данных в журнал внимания
	 */
	public boolean importDataJournalAttention(Map<String, Object> dataMap, String sourceOrigin, Long source) {
        List<List<String>> list = (List<List<String>>) dataMap.get("data");
        CommunityEntity community = communityDao.getById((Long) dataMap.get("communityId"));

		JournalAttention attention;
		UserEntity performer;
		Date fixTimeKvant;

		String strPerformer;
		String strFixTimeKvant;
		String strAttentionKvant;
		String strTagKvant;

		for (List<String> obj : list) {
			strPerformer = obj.get(0).toLowerCase().trim();
			strFixTimeKvant = obj.get(1).trim();
			strAttentionKvant = obj.get(2).trim();
			strTagKvant = obj.get(3).trim();

			if (strPerformer.equals("")) {
				throw new CyberbrainException("Поле 'исполнитель события' пустое!");
			}
			if (strFixTimeKvant.equals("")) {
				throw new CyberbrainException("Поле 'дата создания' записи пустое!");
			}
			if (strAttentionKvant.equals("")) {
				throw new CyberbrainException("Поле 'квант внимания' пустое!");
			}
			if (strTagKvant.equals("")) {
				throw new CyberbrainException("Поле 'теговое описание задачи' пустое!");
			}

			performer = sharerDao.getBySearchString(strPerformer);
			if (performer == null) {
				throw new CyberbrainException("Не удалось найти исполнителя события в системе!");
			}

			fixTimeKvant = DateUtils.stringToDate(strFixTimeKvant + " 18:00:00", DateUtils.Format.DATE_TIME);

			attention = new JournalAttention();
			attention.setFixTimeKvant(fixTimeKvant);
			attention.setTextKvant("");
			attention.setTagKvant(strTagKvant);
			attention.setPerformerKvant(performer);
			attention.setAttentionKvant(Integer.valueOf(strAttentionKvant));
            attention.setCommunity(community);
            attention.setSourceOrigin(getCyberbrainObjectByName(sourceOrigin));
            attention.setSource(source);

			journalAttentionDao.save(attention);

            addScoreSharer(ScoreObjects.JOURNAL_ATTENTION_NEW_RECORD.getName(), CyberbrainObjects.JOURNAL_ATTENTION.getName(), attention.getId(), attention.getCommunity());

			// обновляем тезаурус
			updateThesaurus(attention, sourceOrigin, source);
		}

		return true;
	}

	/**
	 * Обновляем данные в тезаурусе
	 * @param attention запись из журнала внимания
	 */
	private void updateThesaurus(JournalAttention attention, String sourceOrigin, Long source) {
		Thesaurus tag;

		for (String strTag : attention.getTagKvant().split("\\.")) {
			tag = thesaurusDao.getByEssence(strTag.toLowerCase().trim(), attention.getCommunity());

			if (tag != null) {
				int attentionFrequency = tag.getAttentionFrequency() + attention.getAttentionKvant();
				int frequencyEssence = tag.getFrequencyEssence() + 1;

				if (!tag.getSinonim().equals("")) {
					tag = thesaurusDao.getByEssence(tag.getSinonim(), attention.getCommunity());
					if (tag != null) {
						tag.setFrequencyEssence(tag.getFrequencyEssence() + frequencyEssence);
						tag.setAttentionFrequency(tag.getAttentionFrequency() + attentionFrequency);
						thesaurusDao.update(tag);
					}
				} else {
					tag.setAttentionFrequency(attentionFrequency);
					tag.setFrequencyEssence(frequencyEssence);

					thesaurusDao.update(tag);
				}
			} else {
				tag = getTagByEssence(strTag, false, sourceOrigin, source, attention.getCommunity().getId());
				tag.setFixDateEssence(attention.getFixTimeKvant());
				tag.setEssenceOwner(attention.getPerformerKvant());
				tag.setAttentionFrequency(attention.getAttentionKvant());
				tag.setFrequencyEssence(1);

				thesaurusDao.update(tag);
			}
		}
	}

	/**
	 * Импорт данных в тезаурус (служебные теги)
     * (для служебных тегов идентификатор сообщества = null)
	 */
	public boolean importThesaurusServiceTags(Map<String, Object> dataMap, String sourceOrigin, Long source) {
        List<List<String>> list = (List<List<String>>) dataMap.get("data");

		for (List<String> obj : list) {
			getTagByEssence(obj.get(0).trim(), true, sourceOrigin, source, null);
		}

		return true;
	}

	/**
	 * Обновление синонима в таблице тезауруса
	 */
	@Transactional(rollbackFor = CyberbrainException.class)
	public String updateThesaurusSinonim(String jsonData, String sourceOrigin, Long source) {
		JSONObject jsonObject = new JSONObject(jsonData);

        String result = "";
		Long id = Long.valueOf(jsonObject.get("id").toString());
		String sinonim = jsonObject.get(Thesaurus.Columns.SINONIM).toString();

		Thesaurus tag = thesaurusDao.getById(id);
        CommunityEntity community = tag.getCommunity();

		if (tag != null) {
            // Указывать синонимы может только владелец тега
            if (tag.getEssenceOwner().getId().equals(SecurityUtils.getUser().getId())) {
                int frequencyEssence = tag.getFrequencyEssence();
                int attentionFrequency = tag.getAttentionFrequency();
                Date fixDateEssence = tag.getFixDateEssence();
                UserEntity performerKvant = tag.getEssenceOwner();
                String oldSinonim = tag.getSinonim();

                tag.setFrequencyEssence(0);
                tag.setAttentionFrequency(0);
                tag.setSinonim(sinonim);
                thesaurusDao.update(tag);

                if (oldSinonim.equals("")) {
                    Long communityId = -1l;

                    if (tag.getCommunity() != null) {
                        communityId = tag.getCommunity().getId();
                    }

                    journalAttentionDao.updateTagKvant(tag.getEssence(), tag.getSinonim(), communityId);
                }

                tag = thesaurusDao.getByEssence(sinonim, community);
                if (tag != null) {
                    tag.setFrequencyEssence(tag.getFrequencyEssence() + frequencyEssence);
                    tag.setAttentionFrequency(tag.getAttentionFrequency() + attentionFrequency);
                    thesaurusDao.update(tag);
                } else {
                    tag = getTagByEssence(sinonim, false, sourceOrigin, source, community.getId());
                    tag.setFixDateEssence(fixDateEssence);
                    tag.setEssenceOwner(performerKvant);
                    tag.setAttentionFrequency(attentionFrequency);
                    tag.setFrequencyEssence(frequencyEssence);

                    thesaurusDao.update(tag);
                }

                addScoreSharer(ScoreObjects.THESAURUS_CREATE_SINONIM.getName(), CyberbrainObjects.THESAURUS.getName(), id, community);
            } else {
                result = "Вы не являетесь владельцем данного тега.<br/>Редактировать синонимы может только владелец тега!";
            }
		}

		return result;
	}

    /**
     * Добавление пользовательского множества
     */
    public boolean addNewMany(String jsonData, String sourceOrigin, Long source) {
        JSONObject jsonObject = new JSONObject(jsonData);
        Long communityId = Long.valueOf(jsonObject.get("community_id").toString());
        String strManyName = jsonObject.get("newManyName").toString().trim();
        String newManyValue = jsonObject.get("newManyValue").toString().trim();

        if (!strManyName.equals("null") && !strManyName.equals("")) {
            Thesaurus serviceTagThis = getTagByEssence(ServiceTags.THIS, true, sourceOrigin, source, null);
            Thesaurus serviceTagQuestion = getTagByEssence(ServiceTags.QUESTION, true, sourceOrigin, source, null);
            Thesaurus serviceTagMany = getTagByEssence(ServiceTags.MANY, true, sourceOrigin, source, null);

            Thesaurus tagNewMany = getTagByEssence(strManyName, false, sourceOrigin, source, communityId);

            List<KnowledgeRepository> knowledgeList = knowledgeRepositoryDao.getByTags(tagNewMany, serviceTagThis, serviceTagQuestion, tagNewMany.getCommunity());

            if (knowledgeList.size() == 0) {
                knowledgeList = knowledgeRepositoryDao.getByTags(tagNewMany, serviceTagThis, null, tagNewMany.getCommunity());
            }

            if (knowledgeList.size() > 0) {
                // Указываем тегу что это множество
                jsonObject = new JSONObject();
                jsonObject.put("knowledge_rep_id", knowledgeList.get(0).getId());
                jsonObject.put("thesaurus_tag", ServiceTags.MANY);
                jsonObject.put("is_service_tag", ServiceTags.MANY);
                answerTheQuestion(jsonObject.toString(), sourceOrigin, source);

                if (!newManyValue.equals("") && !newManyValue.equals("null")) {
                    // Добавляем свойства для множества
                    jsonObject = new JSONObject();
                    jsonObject.put("knowledge_rep_id",  knowledgeList.get(0).getId());
                    jsonObject.put("thesaurus_tag", newManyValue);
                    answerTheQuestionMany(jsonObject.toString(), sourceOrigin, source);
                }
            }
        }

        return true;
    }

    /**
     * Добавление пользовательского тега
     */
    public boolean addNewTag(String jsonData, String sourceOrigin, Long source) {
        JSONObject jsonObject = new JSONObject(jsonData);
        String strEssence = jsonObject.get("newEssenceName").toString().trim();
        String strEssenceThisValue = jsonObject.get("newEssenceThisValue").toString().trim();
        Long communityId = Long.valueOf(jsonObject.get("community_id").toString());

        Thesaurus tagOwner = getTagByEssence(strEssence, false, sourceOrigin, source, communityId);

        if (!strEssenceThisValue.equals("")) {
            Thesaurus serviceTagThis = getTagByEssence(ServiceTags.THIS, true, sourceOrigin, source, null);
            Thesaurus serviceTagQuestion = getTagByEssence(ServiceTags.QUESTION, true, sourceOrigin, source, null);

            List<KnowledgeRepository> list = knowledgeRepositoryDao.getByTags(tagOwner, serviceTagThis, serviceTagQuestion, tagOwner.getCommunity());

            if (list.size() > 0) {
                jsonObject = new JSONObject();
                jsonObject.put("knowledge_rep_id", list.get(0).getId());
                jsonObject.put("thesaurus_tag", strEssenceThisValue);
                answerTheQuestion(jsonObject.toString(), sourceOrigin, source);
            }
        }

        return true;
    }

    /**
     * Добавление пользовательского свойства
     */
    public boolean addNewProperty(String jsonData, String sourceOrigin, Long source) {
        JSONObject jsonObject = new JSONObject(jsonData);

        String newPropertyManyName = jsonObject.get("newPropertyManyName").toString().trim();
        String newTagOwnerName = jsonObject.get("newTagOwnerName").toString().trim();
        String newPropertyName = jsonObject.get("newPropertyName").toString().trim();
        String newTagName = jsonObject.get("newTagName").toString().trim();
        String meraValue = jsonObject.get("meraValue").toString().trim();
        Long communityId = Long.valueOf(jsonObject.get("community_id").toString());

        Thesaurus tagMany = getTagByEssence(newPropertyManyName, false, sourceOrigin, source, communityId);

        Thesaurus serviceTagThis = getTagByEssence(ServiceTags.THIS, true, sourceOrigin, source, null);
        Thesaurus serviceTagQuestion = getTagByEssence(ServiceTags.QUESTION, true, sourceOrigin, source, null);
        Thesaurus serviceTagMany = getTagByEssence(ServiceTags.MANY, true, sourceOrigin, source, null);
        Thesaurus serviceTagProperty = getTagByEssence(ServiceTags.PROPERTY, true, sourceOrigin, source, null);

        // Множество
        List<KnowledgeRepository> list = knowledgeRepositoryDao.getByTags(tagMany, serviceTagThis, serviceTagQuestion, tagMany.getCommunity());
        if (list.size() > 0) {
            jsonObject = new JSONObject();
            jsonObject.put("knowledge_rep_id", list.get(0).getId());
            jsonObject.put("thesaurus_tag", ServiceTags.MANY);
            jsonObject.put("is_service_tag", ServiceTags.MANY);
            answerTheQuestion(jsonObject.toString(), sourceOrigin, source);
        }

        // Укаажем что Тег владелец это множество
        Thesaurus tagOwner = null;
        if (!newTagOwnerName.equals("")) {
            tagOwner = getTagByEssence(newTagOwnerName, false, sourceOrigin, source, communityId);

            list = knowledgeRepositoryDao.getByTags(tagOwner, serviceTagThis, serviceTagQuestion, tagOwner.getCommunity());
            if (list.size() > 0) {
                jsonObject = new JSONObject();
                jsonObject.put("knowledge_rep_id", list.get(0).getId());
                jsonObject.put("thesaurus_tag", newPropertyManyName);
                answerTheQuestion(jsonObject.toString(), sourceOrigin, source);
            }
        }

        // Добавим для множества новые свойства
        if (!newPropertyName.equals("")) {
            list = knowledgeRepositoryDao.getByTags(tagMany, serviceTagThis, serviceTagMany, tagMany.getCommunity());
            if (list.size() > 0) {
                // Добавляем свойства для множества
                jsonObject = new JSONObject();
                jsonObject.put("knowledge_rep_id", list.get(0).getId());
                jsonObject.put("thesaurus_tag", newPropertyName);
                answerTheQuestionMany(jsonObject.toString(), sourceOrigin, source);
            }
        }

        // Присвоим "Тегу владельцу" значение свойства
        if (!newPropertyName.equals("") && (!newTagName.equals("") || !meraValue.equals("")) && tagOwner != null) {
            List<KnowledgeRepository> propertyList = knowledgeRepositoryDao.getByTags(tagMany, serviceTagProperty, null, tagMany.getCommunity());

            KnowledgeRepository knowledge = null;
            list = knowledgeRepositoryDao.getByTags(tagOwner, serviceTagThis, tagMany, tagOwner.getCommunity());
            if (list.size() > 0) {
                knowledge = list.get(0);
            }

            for (KnowledgeRepository obj : propertyList) {
                if (knowledge != null) {
                    jsonObject = new JSONObject();
                    jsonObject.put("knowledge_rep_id", knowledge.getId());
                    jsonObject.put("thesaurus_tag_property_id", obj.getTag().getId());
                    jsonObject.put("thesaurus_tag", newTagName);
                    jsonObject.put("value_mera", meraValue);
                    answerTheQuestionProperty(jsonObject.toString(), sourceOrigin, source);
                }
            }
        }

        return true;
    }

    /**
     * Метод срабатывает при ответе на вопрос "что это" в хранилище знаний
     */
    public boolean answerTheQuestion(String jsonData, String sourceOrigin, Long source) {
        JSONObject jsonObject = new JSONObject(jsonData);
        List<KnowledgeRepository> knowledgeList;

        Long knowledgeId = Long.valueOf(jsonObject.get("knowledge_rep_id").toString());
        String strTag = jsonObject.get("thesaurus_tag").toString().trim();

        KnowledgeRepository knowledge = knowledgeRepositoryDao.getById(knowledgeId);

        Thesaurus serviceTagThis = getTagByEssence(ServiceTags.THIS, true, sourceOrigin, source, null);
        Thesaurus serviceTagQuestion = getTagByEssence(ServiceTags.QUESTION, true, sourceOrigin, source, null);
        Thesaurus serviceTagMany = getTagByEssence(ServiceTags.MANY, true, sourceOrigin, source, null);

        Thesaurus tag;

        if (jsonObject.isNull("is_service_tag")
                && !strTag.equals(ServiceTags.MANY.toLowerCase())
                && !strTag.equals(ServiceTags.AFFECT.toLowerCase())
                && !strTag.equals(ServiceTags.COPY.toLowerCase())
                && !strTag.equals(ServiceTags.MERA.toLowerCase())
                && !strTag.equals(ServiceTags.NOT.toLowerCase())
                && !strTag.equals(ServiceTags.PROPERTY.toLowerCase())
                && !strTag.equals(ServiceTags.QUESTION.toLowerCase())
                && !strTag.equals(ServiceTags.STATUS.toLowerCase())
                && !strTag.equals(ServiceTags.THIS.toLowerCase())) {
            tag = getTagByEssence(strTag, false, sourceOrigin, source, knowledge.getCommunity().getId());
        } else {
            tag = getTagByEssence(strTag, true, sourceOrigin, source, null);
        }

        knowledge.setTag(tag);
        knowledge.setMera(0.0);

        if (!tag.getIsServiceTag()) {
            knowledge.setShowInQuestions(false);

            // находим запись в хранилище знаний
            knowledgeList = knowledgeRepositoryDao.getByTags(tag, serviceTagThis, serviceTagQuestion, tag.getCommunity());

            if (knowledgeList.size() == 0) {
                knowledgeList = knowledgeRepositoryDao.getByTags(tag, serviceTagThis, null, tag.getCommunity());
            }

            if (knowledgeList.size() > 0) {
                knowledge.setNext(knowledgeList.get(0).getId());
            }

            knowledgeRepositoryDao.update(knowledge);

            // Проставим число элементов множества
            knowledgeList = knowledgeRepositoryDao.getByTags(null, knowledge.getAttribute(), knowledge.getTag(), knowledge.getCommunity());
            int manyCount = knowledgeList.size();
            knowledge.setMera((double) manyCount);
            knowledgeRepositoryDao.update(knowledge);

            knowledgeList = knowledgeRepositoryDao.getByTags(knowledge.getTag(), serviceTagThis, serviceTagMany, knowledge.getCommunity());
            if (knowledgeList.size() > 0) {
                KnowledgeRepository knowledgeMany = knowledgeList.get(0);
                knowledgeMany.setMera((double) manyCount);
                knowledgeRepositoryDao.update(knowledgeMany);
            }

            if (jsonObject.isNull("is_service_tag")) {
                // Сразу же зададим указаному тегу что это множество
                knowledgeList = knowledgeRepositoryDao.getByTags(tag, serviceTagThis, serviceTagQuestion, tag.getCommunity());

                if (knowledgeList.size() > 0) {
                    jsonObject = new JSONObject();
                    jsonObject.put("knowledge_rep_id", knowledgeList.get(0).getId());
                    jsonObject.put("thesaurus_tag", ServiceTags.MANY);
                    jsonObject.put("is_service_tag", ServiceTags.MANY);
                    answerTheQuestion(jsonObject.toString(), sourceOrigin, source);
                }
            }
        } else {
            if (strTag.toLowerCase().trim().equals(ServiceTags.MANY.toLowerCase())) {
                Thesaurus tagOwner = knowledge.getTagOwner();
                tagOwner.setIsObject(false);
                thesaurusDao.update(tagOwner);
            }
        }

        knowledgeRepositoryDao.update(knowledge);
        addScoreSharer(ScoreObjects.KNOWLEDGE_REPOSITORY_ANSWER_QUESTION_THIS.getName(), CyberbrainObjects.KNOWLEDGE_REPOSITORY.getName(), knowledge.getId(), knowledge.getCommunity());

        return true;
    }

	/**
	 * Метод срабатывает при ответе на вопрос по множеству в хранилище знаний
	 */
	public boolean answerTheQuestionMany(String jsonData, String sourceOrigin, Long source) {
		JSONObject jsonObject = new JSONObject(jsonData);

		Long knowledgeId = Long.valueOf(jsonObject.get("knowledge_rep_id").toString());
		String strProperties = jsonObject.get("thesaurus_tag").toString().trim();

		KnowledgeRepository knowledge = knowledgeRepositoryDao.getById(knowledgeId);
        CommunityEntity community = knowledge.getCommunity();

		Thesaurus tagOwner = knowledge.getTagOwner();
		Thesaurus tagProperty = getTagByEssence(ServiceTags.PROPERTY, true, sourceOrigin, source, null);

		// Пользователь может указать служебный тег "Нет" (код 10)
		// Тогда считаем что пользователь ответил на вопрос и убираем его из списка вопросов
		if (! strProperties.toLowerCase().equals(ServiceTags.NOT.toLowerCase())) {
            Set<String> propertiesSet = new HashSet<>();
            Collections.addAll(propertiesSet, strProperties.split("\\."));

            ///////////////////////////
            if (propertiesSet.size() == 1) {
                Thesaurus tag = getTagByEssence(propertiesSet.iterator().next().trim(), false, sourceOrigin, source, community.getId());
                if (!tag.getIsObject()) {
                    if (inheritancePropertiesMany(knowledge.getTagOwner(), tag, sourceOrigin, source)) {
                        return true;
                    }
                }
            }
            ///////////////////////////

			for (String strProperty : propertiesSet) {
				Thesaurus tag = getTagByEssence(strProperty, false, sourceOrigin, source, community.getId());

                if (knowledgeRepositoryDao.getByTags(tagOwner, tagProperty, tag, tagOwner.getCommunity()).size() == 0) {
                    KnowledgeRepository newKnowledge = addRecordRepositoryOfKnowledge(tag, sourceOrigin, source, tag.getCommunity());
                    newKnowledge.setTagOwner(tagOwner);
                    newKnowledge.setAttribute(tagProperty);
                    newKnowledge.setTag(tag);

                    knowledgeRepositoryDao.update(newKnowledge);
                }
			}

            // Поскольку каждое свойство это тоже множество то для каждого свойства присвоим значение множество
            for(String property : propertiesSet) {
                tagProperty = thesaurusDao.getByEssence(property, community);

                Thesaurus serviceTagThis = getTagByEssence(ServiceTags.THIS, true, sourceOrigin, source, null);
                Thesaurus serviceTagQuestion = getTagByEssence(ServiceTags.QUESTION, true, sourceOrigin, source, null);
                Thesaurus serviceTagMany = getTagByEssence(ServiceTags.MANY, true, sourceOrigin, source, null);

                List<KnowledgeRepository> knowledgeList = knowledgeRepositoryDao.getByTags(tagProperty, serviceTagThis, serviceTagMany, tagProperty.getCommunity());
                if (knowledgeList.size() == 0) {
                    knowledgeList = knowledgeRepositoryDao.getByTags(tagProperty, serviceTagThis, serviceTagQuestion, tagProperty.getCommunity());

                    KnowledgeRepository propertyKnowledge;
                    if (knowledgeList.size() > 0) {
                        propertyKnowledge = knowledgeList.get(0);
                    } else {
                        propertyKnowledge = addRecordRepositoryOfKnowledge(tagProperty, sourceOrigin, source, tagProperty.getCommunity());
                    }

                    jsonObject = new JSONObject();
                    jsonObject.put("knowledge_rep_id", propertyKnowledge.getId());
                    jsonObject.put("thesaurus_tag", ServiceTags.MANY);
                    jsonObject.put("is_service_tag", ServiceTags.MANY);
                    answerTheQuestion(jsonObject.toString(), sourceOrigin, source);
                }
            }
		}

        knowledge.setShowInQuestions(false);
		knowledgeRepositoryDao.update(knowledge);

        addScoreSharer(ScoreObjects.KNOWLEDGE_REPOSITORY_ANSWER_QUESTION_MANY.getName(), CyberbrainObjects.KNOWLEDGE_REPOSITORY.getName(), knowledge.getId(), knowledge.getCommunity());

		return true;
	}

	/**
	 * Метод срабатывает при ответе на вопрос по свойству в хранилище знаний
	 */
	public boolean answerTheQuestionProperty(String jsonData, String sourceOrigin, Long source) {
		JSONObject jsonObject = new JSONObject(jsonData);

		Long knowledgeId = Long.valueOf(jsonObject.get("knowledge_rep_id").toString());
		Thesaurus tagProperty = thesaurusDao.getById(Long.valueOf(jsonObject.get("thesaurus_tag_property_id").toString()));
		String strTag = jsonObject.get("thesaurus_tag").toString().trim();
		String strMera = jsonObject.get("value_mera").toString().trim();

        if ((!strMera.equals("null") || !strTag.equals("null")) && (!strMera.equals("") || !strTag.equals(""))) {
            KnowledgeRepository knowledge = knowledgeRepositoryDao.getById(knowledgeId);

            Double mera = strMera.equals("null") || strMera.equals("") ? 0 : Double.valueOf(strMera);

            Thesaurus tag;
            if (strMera.equals("null") || strMera.equals("")) {
                tag = getTagByEssence(strTag, false, sourceOrigin, source, knowledge.getCommunity().getId());
            } else {
                tag = getTagByEssence(ServiceTags.MERA, true, sourceOrigin, source, null);
            }

            KnowledgeRepository newKnowledge = knowledge.clone();
            newKnowledge.setAttribute(tagProperty);
            newKnowledge.setTag(tag);
            newKnowledge.setMera(strMera.equals("null") || strMera.equals("") ? null : mera);
            newKnowledge.setNext(null);
            newKnowledge.setSourceOrigin(getCyberbrainObjectByName(sourceOrigin));
            newKnowledge.setSource(source);
            knowledgeRepositoryDao.save(newKnowledge);

            List<KnowledgeRepository> knowledgeList = knowledgeRepositoryDao.getByTags(newKnowledge.getTagOwner(), newKnowledge.getAttribute(), null, newKnowledge.getCommunity());

            for(KnowledgeRepository obj : knowledgeList) {
                if (obj.getNext() == null && !Objects.equals(obj.getId(), newKnowledge.getId())) {
                    obj.setNext(newKnowledge.getId());
                    obj.setIsTopical(false);
                    knowledgeRepositoryDao.update(obj);
                }
            }

            // Если пользователь ввел тег то мы сразу можем указать что это
            if (strMera.equals("null") || strMera.equals("")) {
                Thesaurus serviceTagThis = getTagByEssence(ServiceTags.THIS, true, sourceOrigin, source, null);
                Thesaurus serviceTagQuestion = getTagByEssence(ServiceTags.QUESTION, true, sourceOrigin, source, null);
                knowledgeList = knowledgeRepositoryDao.getByTags(tag, serviceTagThis, serviceTagQuestion, tag.getCommunity());

                for(KnowledgeRepository obj : knowledgeList) {
                    obj.setTag(tagProperty);
                    knowledgeRepositoryDao.update(obj);
                }
            }

            addScoreSharer(ScoreObjects.KNOWLEDGE_REPOSITORY_ANSWER_QUESTION_PROPERTY.getName(), CyberbrainObjects.KNOWLEDGE_REPOSITORY.getName(), newKnowledge.getId(), newKnowledge.getCommunity());
        }

		return true;
	}

    /**
     * Метод срабатывает при "сохранении" на вопрос по трекам объекта
     */
    public boolean saveTrack(String jsonData, String sourceOrigin, Long source) {
        JSONObject jsonObject = new JSONObject(jsonData);

        Long knowledgeRepId = Long.valueOf(jsonObject.get("knowledge_rep_id").toString());

        Thesaurus tagOwner = thesaurusDao.getById(Long.valueOf(jsonObject.get("thesaurus_tag_owner_id").toString()));
        Thesaurus serviceTagStatus = getTagByEssence(ServiceTags.STATUS, true, sourceOrigin, source, null);
        Thesaurus tagCustomStatus = getTagByEssence(jsonObject.get("custom_status").toString().trim(), false, sourceOrigin, source, tagOwner.getCommunity().getId());

        Date timeReady = DateUtils.stringToDate(jsonObject.get("time_ready").toString().replaceAll("T", " "), "yyyy-MM-dd HH:mm:ss");
        Integer lifecycleStatus = Integer.valueOf(jsonObject.get("lifecycle_status").toString());
        String isTrack = jsonObject.get("is_track").toString();

        if (isTrack.equals("0")) {
            KnowledgeRepository knowledge = addRecordRepositoryOfKnowledge(tagOwner, sourceOrigin, source, tagOwner.getCommunity());
            knowledge.setTimeReady(timeReady);
            knowledge.setAttribute(serviceTagStatus);
            knowledge.setTag(tagCustomStatus);
            knowledge.setLifecycleStatus(lifecycleStatus);
            knowledgeRepositoryDao.update(knowledge);
        } else {
            KnowledgeRepository knowledge = knowledgeRepositoryDao.getById(knowledgeRepId);
            knowledge.setTimeReady(timeReady);
            knowledge.setAttribute(serviceTagStatus);
            knowledge.setTag(tagCustomStatus);
            knowledge.setLifecycleStatus(lifecycleStatus);
            knowledgeRepositoryDao.update(knowledge);
        }

        return true;
    }

    /**
     * Добавление нового трека для объекта
     */
    public boolean addNewTrack(String jsonData, String sourceOrigin, Long source) {
        JSONObject jsonObject = new JSONObject(jsonData);

        Long knowledgeRepId = Long.valueOf(jsonObject.get("knowledge_rep_id").toString());

        Thesaurus tagOwner = thesaurusDao.getById(Long.valueOf(jsonObject.get("thesaurus_tag_owner_id").toString()));
        Thesaurus serviceTagStatus = getTagByEssence(ServiceTags.STATUS, true, sourceOrigin, source, null);
        Thesaurus tagCustomStatus = getTagByEssence(jsonObject.get("custom_status").toString().trim(), false, sourceOrigin, source, tagOwner.getCommunity().getId());

        Date timeReady = DateUtils.stringToDate(jsonObject.get("time_ready").toString().replaceAll("T", " "), "yyyy-MM-dd HH:mm:ss");
        Integer lifecycleStatus = Integer.valueOf(jsonObject.get("lifecycle_status").toString());
        String isTrack = jsonObject.get("is_track").toString();

        KnowledgeRepository knowledge = knowledgeRepositoryDao.getById(knowledgeRepId);

        Long next = knowledge.getNext();
        while (next != null) {
            knowledge = knowledgeRepositoryDao.getById(next);
            next = knowledge.getNext();
        }

        KnowledgeRepository newKnowledge = addRecordRepositoryOfKnowledge(tagOwner, sourceOrigin, source, tagOwner.getCommunity());
        newKnowledge.setTimeReady(timeReady);
        newKnowledge.setAttribute(serviceTagStatus);
        newKnowledge.setTag(tagCustomStatus);
        newKnowledge.setLifecycleStatus(lifecycleStatus);
        knowledgeRepositoryDao.update(newKnowledge);

        if (isTrack.equals("1")) {
            knowledge.setNext(newKnowledge.getId());
            knowledgeRepositoryDao.update(knowledge);
        }

        return true;
    }

    /**
     * Закончить редактирование треков объекта
     */
    public boolean editTracksFinish(String jsonData, String sourceOrigin, Long source) {
        JSONObject jsonObject = new JSONObject(jsonData);

        Thesaurus tagOwner = thesaurusDao.getById(Long.valueOf(jsonObject.get("thesaurus_tag_owner_id").toString()));
        Thesaurus serviceTagStatus = getTagByEssence(ServiceTags.STATUS, true, sourceOrigin, source, null);

        List<KnowledgeRepository> knowledgeList = knowledgeRepositoryDao.getByTags(tagOwner, serviceTagStatus, null, tagOwner.getCommunity());

        for (KnowledgeRepository obj : knowledgeList) {
            obj.setShowInQuestions(false);
            knowledgeRepositoryDao.update(obj);
        }

        // По завершению вычисляем трудозатраты в днях для состояний
        Collections.sort(knowledgeList, new Comparator<KnowledgeRepository>(){
            public int compare(KnowledgeRepository s1, KnowledgeRepository s2) {
                return s1.getTimeReady().compareTo(s2.getTimeReady());
            }
        });

        // выставляем маркер актуальности первому элементу трека
        KnowledgeRepository knowledge = knowledgeList.get(0);
        knowledge.setIsTopical(true);
        knowledgeRepositoryDao.update(knowledge);

        Date readyDate = null;
        double trackDays;

        for (KnowledgeRepository obj : knowledgeList) {
            if (obj.getLifecycleStatus() == 5) {
                readyDate = obj.getTimeReady();
            }
        }

        if (readyDate != null) {
            for (KnowledgeRepository obj : knowledgeList) {
                if (obj.getLifecycleStatus() < 5) {
                    trackDays = DateUtils.getDistanceDays(readyDate, obj.getTimeReady());
                    obj.setMera(trackDays);
                    knowledgeRepositoryDao.update(obj);
                } else if (obj.getLifecycleStatus() == 5) {
                    obj.setMera(0.0);
                    knowledgeRepositoryDao.update(obj);
                } else if (obj.getLifecycleStatus() > 5) {
                    trackDays = DateUtils.getDistanceDays(readyDate, obj.getTimeReady());
                    obj.setMera(trackDays);
                    knowledgeRepositoryDao.update(obj);
                }
            }
        }

        return true;
    }

    private void setQuestionAnswer(Thesaurus tagOwner, String strTag, boolean isServiceTag, String sourceOrigin, Long source) {
        Thesaurus serviceTagThis = getTagByEssence(ServiceTags.THIS, true, sourceOrigin, source, null);
        Thesaurus serviceTagQuestion = getTagByEssence(ServiceTags.QUESTION, true, sourceOrigin, source, null);

        List<KnowledgeRepository> knowledgeList = knowledgeRepositoryDao.getByTags(tagOwner, serviceTagThis, serviceTagQuestion, tagOwner.getCommunity());
        if (knowledgeList.size() > 0) {
            JSONObject jsonObject;

            jsonObject = new JSONObject();
            jsonObject.put("knowledge_rep_id", knowledgeList.get(0).getId());
            jsonObject.put("thesaurus_tag", strTag);

            if (isServiceTag) {
                jsonObject.put("is_service_tag", strTag);
            }

            answerTheQuestion(jsonObject.toString(), sourceOrigin, source);
        }
    }

	/**
	 * Добавление новой задачи (классический вариант)
	 */
	@Transactional(rollbackFor = CyberbrainException.class)
	public boolean addUserTask(String jsonData, String sourceOrigin, Long source) {
		JSONObject jsonObject = new JSONObject(jsonData);
		JSONArray jsonArray = jsonObject.getJSONArray("subtask_ids");

        Long communityId = Long.valueOf(jsonObject.get("community_id").toString());
		UserEntity performer = sharerDao.getById(Long.valueOf(jsonObject.get("performer_id").toString()));
		UserEntity customer = sharerDao.getById(Long.valueOf(jsonObject.get("customer_id").toString()));
        KnowledgeRepository track = knowledgeRepositoryDao.getById(Long.valueOf(jsonObject.get("object_id").toString()));
		Thesaurus statusFrom = getTagByEssence(jsonObject.get("from_status_name").toString(), false, sourceOrigin, source, communityId);
		Thesaurus statusTo = getTagByEssence(jsonObject.get("to_status_name").toString(), false, sourceOrigin, source, communityId);

		Date dateExecution = DateUtils.stringToDate(jsonObject.get("date_execution").toString(), "yyyy/MM/dd HH:mm:ss");
		String description = jsonObject.get("description").toString().trim();
		Integer lifecycle = Integer.valueOf(jsonObject.get("lifecycle_id").toString());

		// Добавление задачи
		UserTask userTask = new UserTask();
		userTask.setPerformer(performer);
        userTask.setDateExecution(dateExecution);
        userTask.setDescription(description);
        userTask.setCustomer(customer);
        userTask.setLifecycle(lifecycle);
		userTask.setTrack(track);
		userTask.setStatusFrom(statusFrom);
		userTask.setStatusTo(statusTo);
        userTask.setCommunity(communityDao.getById(communityId));
        userTask.setSourceOrigin(getCyberbrainObjectByName(sourceOrigin));
        userTask.setSource(source);
		userTaskDao.save(userTask);

		// Добавление подзадач
		List<Long> list = new ArrayList<>();
		for (int i = 0; i < jsonArray.length(); i++) {
			list.add(jsonArray.getLong(i));
		}

		UserSubtask userSubtask;
		for (Long id : list) {
			userSubtask = new UserSubtask();
			userSubtask.setUserTask(userTask);
			userSubtask.setUserSubtask(userTaskDao.getById(id));

			userSubtaskDao.save(userSubtask);
		}

        addScoreSharer(ScoreObjects.CREATE_TASK.getName(), CyberbrainObjects.USER_TASK.getName(), userTask.getId(), userTask.getCommunity());

		return true;
	}

    /**
     * Редактирование задачи (классический вариант)
     */
    @Transactional(rollbackFor = CyberbrainException.class)
    public boolean updateUserTask(String jsonData, String sourceOrigin, Long source) {
        JSONObject jsonObject = new JSONObject(jsonData);
        JSONArray jsonArray = jsonObject.getJSONArray("subtask_ids");

        UserTask userTask = userTaskDao.getById(Long.valueOf(jsonObject.get("id").toString()));
        UserEntity performer = sharerDao.getById(Long.valueOf(jsonObject.get("performer_id").toString()));
        UserEntity customer = sharerDao.getById(Long.valueOf(jsonObject.get("customer_id").toString()));
        KnowledgeRepository track = knowledgeRepositoryDao.getById(Long.valueOf(jsonObject.get("object_id").toString()));
        Thesaurus statusFrom = getTagByEssence(jsonObject.get("from_status_name").toString(), false, sourceOrigin, source, userTask.getCommunity().getId());
        Thesaurus statusTo = getTagByEssence(jsonObject.get("to_status_name").toString(), false, sourceOrigin, source, userTask.getCommunity().getId());

        Date dateExecution = DateUtils.stringToDate(jsonObject.get("date_execution").toString(), "yyyy/MM/dd HH:mm:ss");
        String description = jsonObject.get("description").toString().trim();
        Integer lifecycle = Integer.valueOf(jsonObject.get("lifecycle_id").toString());

        userTask.setPerformer(performer);
        userTask.setDateExecution(dateExecution);
        userTask.setDescription(description);
        userTask.setCustomer(customer);

        if (!Objects.equals(userTask.getLifecycle(), lifecycle)) {
            userTask.setLifecycle(lifecycle);

            // если задача переведена в состояние "готово" (решена) тогда начислим баллы
            if (Objects.equals(lifecycle, UserTaskLifecycle.SOLVED.getIndex())) {
                addScoreSharer(ScoreObjects.USER_TASK_CHANGE_STATUS_READY.getName(), CyberbrainObjects.USER_TASK.getName(), userTask.getId(), userTask.getCommunity());
            }
        }

        userTask.setTrack(track);
        userTask.setStatusFrom(statusFrom);
        userTask.setStatusTo(statusTo);
        userTaskDao.update(userTask);

        // Определяем список подзадач которые нужно добавить
        List<Long> subtaskIds = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            subtaskIds.add(jsonArray.getLong(i));
        }

        // Определяем текущий список подзадач которые уже добавлены
        List<Long> currentSubtaskIds = new ArrayList<>();
        for (UserSubtask obj : userTask.getSubtasks()) {
            currentSubtaskIds.add(obj.getUserSubtask().getId());

            // тут же проверяем была ли удалена подзадача или нет
            if (!subtaskIds.contains(obj.getUserSubtask().getId())) {
                userSubtaskDao.delete(obj);
            }
        }

        // Добавление подзадач
        UserSubtask userSubtask;
        for (Long id : subtaskIds) {
            if (!currentSubtaskIds.contains(id)) {
                userSubtask = new UserSubtask();
                userSubtask.setUserTask(userTask);
                userSubtask.setUserSubtask(userTaskDao.getById(id));

                userSubtaskDao.save(userSubtask);
            }
        }

        if (Objects.equals(lifecycle, UserTaskLifecycle.CONFIRMED.getIndex())) {
            // Передвинуть маркер в трекере на новый ЖЦ и создать новую задачу согласно новым данным
            track.setIsTopical(false);
            knowledgeRepositoryDao.update(track);

            if (track.getNext() != null) {
                track = knowledgeRepositoryDao.getById(track.getNext());
                track.setIsTopical(true);

                // Создадим новую задачу (на основе предыдущей задачи)
                UserTask newUserTask = new UserTask();
                newUserTask.setPerformer(userTask.getPerformer());
                newUserTask.setDescription(userTask.getDescription());
                newUserTask.setCustomer(userTask.getCustomer());
                newUserTask.setLifecycle(UserTaskLifecycle.NEW.getIndex());
                newUserTask.setTrack(track);
                newUserTask.setStatusFrom(track.getTag());

                if (track.getNext() != null) {
                    track = knowledgeRepositoryDao.getById(track.getNext());
                    newUserTask.setStatusTo(track.getTag());
                    newUserTask.setDateExecution(track.getTimeReady());
                } else {
                    Thesaurus serviceTagQuestion = getTagByEssence(ServiceTags.QUESTION, true, sourceOrigin, source, null);
                    newUserTask.setStatusTo(serviceTagQuestion);
                    newUserTask.setDateExecution(userTask.getDateExecution());
                }

                newUserTask.setCommunity(userTask.getCommunity());
                newUserTask.setSourceOrigin(userTask.getSourceOrigin());
                newUserTask.setSource(userTask.getSource());
                userTaskDao.save(newUserTask);

                // Создадим подзадачи если таковые имеются (на основе предыдущей задачи)
                for (UserSubtask obj : userTask.getSubtasks()) {
                    userSubtask = new UserSubtask();
                    userSubtask.setUserTask(newUserTask);
                    userSubtask.setUserSubtask(obj.getUserSubtask());
                    userSubtaskDao.save(userSubtask);
                }
            }
        }

        return true;
    }

    /**
     * Добавление новой проблемы (классический вариант)
     */
    public boolean addUserProblem(String jsonData, String sourceOrigin, Long source) {
        JSONObject jsonObject = new JSONObject(jsonData);
        JSONArray jsonArray = jsonObject.getJSONArray("problems");

        Thesaurus serviceTagAffect = getTagByEssence(ServiceTags.AFFECT, true, sourceOrigin, source, null);

        Long communityId = Long.valueOf(jsonObject.get("community_id").toString());
        String description = jsonObject.get("description").toString().trim();
        Thesaurus tagObject = getTagByEssence(jsonObject.get("tag_object").toString(), false, sourceOrigin, source, communityId);

        String strTagMany = jsonObject.get("tag_many").toString();
        Thesaurus tagMany;

        if (strTagMany.equals("")) {
            tagMany = getTagByEssence(ServiceTags.QUESTION, true, sourceOrigin, source, null);
        } else {
            tagMany = getTagByEssence(strTagMany, false, sourceOrigin, source, communityId);
            setQuestionAnswer(tagMany, ServiceTags.MANY, true, sourceOrigin, source);
            setQuestionAnswer(tagObject, strTagMany, false, sourceOrigin, source);
        }

        UserProblem userProblem = new UserProblem();
        userProblem.setDescription(description);
        userProblem.setTagObject(tagObject);
        userProblem.setTagMany(tagMany);
        userProblem.setUserEntity(sharerDao.getById(SecurityUtils.getUser().getId()));
        userProblem.setCommunity(communityDao.getById(communityId));
        userProblem.setSourceOrigin(getCyberbrainObjectByName(sourceOrigin));
        userProblem.setSource(source);

        userProblemDao.save(userProblem);

        List<JSONObject> jsonProblemsList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            jsonProblemsList.add(new JSONObject(jsonArray.get(i).toString()));
        }

        for (JSONObject obj : jsonProblemsList) {
            tagObject = getTagByEssence(obj.get("tag_object").toString(), false, sourceOrigin, source, communityId);

            strTagMany = obj.get("tag_many").toString();

            if (strTagMany.equals("")) {
                tagMany = getTagByEssence(ServiceTags.QUESTION, true, sourceOrigin, source, null);
            } else {
                tagMany = getTagByEssence(strTagMany, false, sourceOrigin, source, communityId);
                setQuestionAnswer(tagMany, ServiceTags.MANY, true, sourceOrigin, source);
                setQuestionAnswer(tagObject, strTagMany, false, sourceOrigin, source);
            }

            UserEntity performer = sharerDao.getById(Long.valueOf(obj.get("performer_id").toString()));

            UserProblemPerformer userProblemPerformer = new UserProblemPerformer();
            userProblemPerformer.setUserProblem(userProblem);
            userProblemPerformer.setTagObject(tagObject);
            userProblemPerformer.setTagMany(tagMany);
            userProblemPerformer.setPerformer(performer);

            userProblemPerformerDao.save(userProblemPerformer);

            // Создавать объект Y в ХЗ и указывать субъектом человека H
            List<KnowledgeRepository> knowledgeList = knowledgeRepositoryDao.getByTags(tagObject, null, null, tagObject.getCommunity());
            for (KnowledgeRepository knowledge : knowledgeList) {
                knowledge.setOwner(performer);
                knowledgeRepositoryDao.update(knowledge);
            }

            // Создавать запись в ХЗ - Y Влияет X
            knowledgeList = knowledgeRepositoryDao.getByTags(tagObject, serviceTagAffect, userProblem.getTagObject(), userProblem.getCommunity());
            if (knowledgeList.size() == 0) {
                KnowledgeRepository newKnowledge = addRecordRepositoryOfKnowledge(tagObject, CyberbrainObjects.USER_PROBLEM.getName(), userProblem.getId(), userProblem.getCommunity());
                newKnowledge.setTagOwner(tagObject);
                newKnowledge.setAttribute(serviceTagAffect);
                newKnowledge.setTag(userProblem.getTagObject());

                knowledgeRepositoryDao.update(newKnowledge);
            }
        }

        addScoreSharer(ScoreObjects.INDICATION_OF_THE_PROBLEM.getName(), CyberbrainObjects.USER_PROBLEM.getName(), userProblem.getId(), userProblem.getCommunity());

        return true;
    }

    /**
     * Редактирование проблемы (классический вариант)
     */
    public boolean updateUserProblem(String jsonData, String sourceOrigin, Long source) {
        JSONObject jsonObject = new JSONObject(jsonData);
        JSONArray jsonArray = jsonObject.getJSONArray("problems");

        Thesaurus serviceTagAffect = getTagByEssence(ServiceTags.AFFECT, true, sourceOrigin, source, null);

        UserProblem userProblem = userProblemDao.getById(Long.valueOf(jsonObject.get("id").toString()));
        String description = jsonObject.get("description").toString().trim();
        Thesaurus tagObject = getTagByEssence(jsonObject.get("tag_object").toString(), false, sourceOrigin, source, userProblem.getCommunity().getId());

        String strTagMany = jsonObject.get("tag_many").toString();
        Thesaurus tagMany;

        if (strTagMany.equals("")) {
            tagMany = getTagByEssence(ServiceTags.QUESTION, true, sourceOrigin, source, null);
        } else {
            tagMany = getTagByEssence(strTagMany, false, sourceOrigin, source, userProblem.getCommunity().getId());
            setQuestionAnswer(tagMany, ServiceTags.MANY, true, sourceOrigin, source);
            setQuestionAnswer(tagObject, strTagMany, false, sourceOrigin, source);
        }

        userProblem.setDescription(description);
        userProblem.setTagObject(tagObject);
        userProblem.setTagMany(tagMany);

        userProblemDao.update(userProblem);

        List<UserProblemPerformer> userProblemPerformerList = userProblemPerformerDao.getByProblemList(userProblem);

        // Определяем список проблем
        List<JSONObject> jsonProblemsList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            jsonProblemsList.add(new JSONObject(jsonArray.get(i).toString()));
        }

        // Удалим убранные проблемы
        boolean isFound;
        for (UserProblemPerformer obj : userProblemPerformerList) {
            isFound = false;

            for (JSONObject jsonObj : jsonProblemsList) {
                tagObject = getTagByEssence(jsonObj.get("tag_object").toString(), false, sourceOrigin, source, obj.getUserProblem().getCommunity().getId());

                strTagMany = jsonObj.get("tag_many").toString();

                if (strTagMany.equals("")) {
                    tagMany = getTagByEssence(ServiceTags.QUESTION, true, sourceOrigin, source, null);
                } else {
                    tagMany = getTagByEssence(strTagMany, false, sourceOrigin, source, obj.getUserProblem().getCommunity().getId());
                }

                UserEntity performer = sharerDao.getById(Long.valueOf(jsonObj.get("performer_id").toString()));

                if (obj.getTagObject().getId().equals(tagObject.getId()) &&
                        obj.getTagMany().getId().equals(tagMany.getId()) &&
                        obj.getPerformer().getId().equals(performer.getId())) {
                    isFound = true;
                    break;
                }
            }

            if (!isFound) {
                userProblemPerformerDao.delete(obj.getId());
            }
        }

        // добавим новые проблемы
        for (JSONObject jsonObj : jsonProblemsList) {
            isFound = false;

            tagObject = getTagByEssence(jsonObj.get("tag_object").toString(), false, sourceOrigin, source, userProblem.getCommunity().getId());
            strTagMany = jsonObj.get("tag_many").toString();

            if (strTagMany.equals("")) {
                tagMany = getTagByEssence(ServiceTags.QUESTION, true, sourceOrigin, source, null);
            } else {
                tagMany = getTagByEssence(strTagMany, false, sourceOrigin, source, userProblem.getCommunity().getId());
                setQuestionAnswer(tagMany, ServiceTags.MANY, true, sourceOrigin, source);
                setQuestionAnswer(tagObject, strTagMany, false, sourceOrigin, source);
            }

            UserEntity performer = sharerDao.getById(Long.valueOf(jsonObj.get("performer_id").toString()));

            for (UserProblemPerformer obj : userProblemPerformerList) {
                if (obj.getTagObject().getId().equals(tagObject.getId()) &&
                        obj.getTagMany().getId().equals(tagMany.getId()) &&
                        obj.getPerformer().getId().equals(performer.getId())) {
                    isFound = true;
                    break;
                }
            }

            if (!isFound) {
                UserProblemPerformer userProblemPerformer = new UserProblemPerformer();
                userProblemPerformer.setUserProblem(userProblem);
                userProblemPerformer.setTagObject(tagObject);
                userProblemPerformer.setTagMany(tagMany);
                userProblemPerformer.setPerformer(performer);

                userProblemPerformerDao.save(userProblemPerformer);
            }

            // Создавать объект Y в ХЗ и указывать субъектом человека H
            List<KnowledgeRepository> knowledgeList = knowledgeRepositoryDao.getByTags(tagObject, null, null, tagObject.getCommunity());
            for(KnowledgeRepository knowledge : knowledgeList) {
                knowledge.setOwner(performer);
                knowledgeRepositoryDao.update(knowledge);
            }

            // Создавать запись в ХЗ - Y Влияет X
            knowledgeList = knowledgeRepositoryDao.getByTags(tagObject, serviceTagAffect, userProblem.getTagObject(), userProblem.getCommunity());

            if (knowledgeList.size() == 0) {
                KnowledgeRepository newKnowledge = addRecordRepositoryOfKnowledge(tagObject, sourceOrigin, source, tagObject.getCommunity());
                newKnowledge.setTagOwner(tagObject);
                newKnowledge.setAttribute(serviceTagAffect);
                newKnowledge.setTag(userProblem.getTagObject());

                knowledgeRepositoryDao.update(newKnowledge);
            }
        }

        return true;
    }

    /**
     * Раздел Цели и дела: Копирование объекта
     */
    public boolean copyObject(String jsonData, String sourceOrigin, Long source) {
        JSONObject jsonObject = new JSONObject(jsonData);
        CommunityEntity community = communityDao.getById(Long.valueOf(jsonObject.get("community_id").toString()));

        Thesaurus sourceObject = getTagByEssence(jsonObject.get("source_object").toString(), false, sourceOrigin, source, community.getId());
        Thesaurus targetObject = getTagByEssence(jsonObject.get("target_object").toString(), false, sourceOrigin, source, community.getId());
        Date timeReady = DateUtils.stringToDate(jsonObject.get("time_ready").toString(), "yyyy/MM/dd HH:mm:ss");
        UserEntity responsible = sharerDao.getById(Long.valueOf(jsonObject.get("responsible_id").toString()));

        targetObject.setEssenceOwner(responsible);
        thesaurusDao.update(targetObject);

        Thesaurus serviceTagThis = getTagByEssence(ServiceTags.THIS, true, sourceOrigin, source, null);
        Thesaurus serviceTagQuestion = getTagByEssence(ServiceTags.QUESTION, true, sourceOrigin, source, null);
        Thesaurus serviceTagCopy = getTagByEssence(ServiceTags.COPY, true, sourceOrigin, source, null);

        List<KnowledgeRepository> knowledgeList = knowledgeRepositoryDao.getByTags(targetObject, serviceTagThis, serviceTagQuestion, targetObject.getCommunity());

        if (knowledgeList.size() > 0) {
            KnowledgeRepository knowledge = knowledgeList.get(0);
            knowledge.setAttribute(serviceTagCopy);
            knowledge.setTag(sourceObject);
            knowledge.setTimeReady(timeReady);
            knowledgeRepositoryDao.update(knowledge);
        } else {
			if (!jsonObject.isNull("tag_many_id")) {
				Thesaurus tagMany = thesaurusDao.getById(Long.valueOf(jsonObject.get("tag_many_id").toString()));

				knowledgeList = knowledgeRepositoryDao.getByTags(sourceObject, serviceTagThis, tagMany, targetObject.getCommunity());
				if (knowledgeList.size() > 0) {
					KnowledgeRepository newKnowledge = addRecordRepositoryOfKnowledge(targetObject, sourceOrigin, source, targetObject.getCommunity());
					newKnowledge.setAttribute(serviceTagCopy);
					newKnowledge.setTag(sourceObject);
					newKnowledge.setTimeReady(timeReady);
					knowledgeRepositoryDao.update(newKnowledge);
				}
			}
		}

        return true;
    }

	/**
	 * Экспорт табличных данных в файл
	 */
	public String exportDataTable(Class<?> clazz, boolean compress, Long communityId) {
		AbstractDao abstractDao = null;
		int count = 0;
		int	pageSize = 1000;

		if (! CyberbrainExportData.class.isAssignableFrom(clazz)) {
			return null;
		}

        Conjunction conjunction = new Conjunction();
        conjunction.add(Restrictions.eq("community", communityDao.getById(communityId)));

		if (clazz == JournalAttention.class) {
			abstractDao = journalAttentionDao;
			count = abstractDao.count(conjunction);
		} else if (clazz == KnowledgeRepository.class) {
			abstractDao = knowledgeRepositoryDao;
			count = abstractDao.count(conjunction);
		} else if (clazz == Thesaurus.class) {
			abstractDao = thesaurusDao;
			count = abstractDao.count(conjunction);
		} else if (clazz == UserTask.class) {
			abstractDao = userTaskDao;
			count = abstractDao.count(conjunction);
		}

        try {
            RandomAccessFile raf;
            MappedByteBuffer out;
            FileChannel fc;

            String date = DateUtils.dateToString(new Date(System.currentTimeMillis()), DateUtils.Format.DATE).replaceAll("[\\. \\s  :]", "_");
            String userName = SecurityUtils.getUser().getFullName().toLowerCase();
            userName = userName.replaceAll("[\\. \\s  : @]", "_");
            userName = StringUtils.toTranslit(userName);

            String fileName = System.getProperty("java.io.tmpdir") + "/Cyberbrain_" + clazz.getSimpleName() + "_" + date + "_" + userName;

            String sourceFileName = fileName + ".txt";
            String targetFileName = fileName + ".zip";

		    if (count > 0) {
				raf = new RandomAccessFile(sourceFileName , "rw");
				fc = raf.getChannel();
				int position = 0;
				byte[] buffer;

				for (int i = 0; i < count; i += pageSize) {
					List list = abstractDao.find(i, pageSize, conjunction);

					for (Object obj : list) {
						CyberbrainExportData data = (CyberbrainExportData) obj;

						if (position == 0) {
							buffer = data.getExportData(true).getBytes();
						} else {
							buffer = data.getExportData(false).getBytes();
						}

						out = raf.getChannel().map(FileChannel.MapMode.READ_WRITE, position, buffer.length);
						out.put(buffer);

						Cleaner cleaner = ((DirectBuffer) out).cleaner();
						cleaner.clean();

						position += buffer.length;
					}
				}

				fc.close();
				raf.close();
            } else {
                File file = new File(sourceFileName);
                BufferedWriter output = new BufferedWriter(new FileWriter(file));
                output.write("Нет данных для выгрузки!");
                output.close();
            }

            if (compress) {
                // Сжимаем файл в zip архив
                zipProcessor.compress(sourceFileName, targetFileName, true);
            }

            return targetFileName;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
	}

	/**
	 * Загрузка знаний в хранилище знаний
	 */
	public boolean importDataKnowledgeRepository(Map<String, Object> dataMap, String sourceOrigin, Long source) {
        List<List<String>> list = (List<List<String>>) dataMap.get("data");
        CommunityEntity community = communityDao.getById((Long) dataMap.get("communityId"));

		KnowledgeRepository knowledge = null;
		List<KnowledgeRepository> knowledgeList;

		// для начала проверим есть ли в системе служебный тег "Это"
		Thesaurus serviceTagThis = getTagByEssence(ServiceTags.THIS, true, sourceOrigin, source, null);

		// затем проверим есть ли в системе служебный тег "Свойство"
		Thesaurus serviceTagProperty = getTagByEssence(ServiceTags.PROPERTY, true, sourceOrigin, source, null);

		// затем проверим есть ли в системе служебный тег "?"
		Thesaurus serviceTagQuestion = getTagByEssence(ServiceTags.QUESTION, true, sourceOrigin, source, null);

		// затем проверим есть ли в системе служебный тег "Множество"
		Thesaurus serviceTagMany = getTagByEssence(ServiceTags.MANY, true, sourceOrigin, source, null);

		// Первое слово (левое верхнее) - множество элементы которого загружаем
		String firstWord = list.get(0).get(0);
		Thesaurus tagFirstWord = thesaurusDao.getByEssence(firstWord, community);
		if (tagFirstWord == null) {
			tagFirstWord = new Thesaurus();
			tagFirstWord.setIsServiceTag(false);
			tagFirstWord.setEssence(firstWord);
			tagFirstWord.setSinonim("");
			tagFirstWord.setFixDateEssence(new Date(System.currentTimeMillis()));
			tagFirstWord.setEssenceOwner(sharerDao.getById(SecurityUtils.getUser().getId()));
			tagFirstWord.setAttentionFrequency(0);
			tagFirstWord.setFrequencyEssence(0);
            tagFirstWord.setIsPersonalData(false);
            tagFirstWord.setIsObject(false);
            tagFirstWord.setCommunity(community);
            tagFirstWord.setSourceOrigin(getCyberbrainObjectByName(sourceOrigin));
            tagFirstWord.setSource(source);
			thesaurusDao.save(tagFirstWord);

			knowledge = addRecordRepositoryOfKnowledge(tagFirstWord, sourceOrigin, source, tagFirstWord.getCommunity());

            addScoreSharer(ScoreObjects.THESAURUS_NEW_RECORD.getName(), CyberbrainObjects.THESAURUS.getName(), tagFirstWord.getId(), tagFirstWord.getCommunity());
		} else {
			knowledgeList = knowledgeRepositoryDao.getByTags(tagFirstWord, serviceTagThis, serviceTagQuestion, tagFirstWord.getCommunity());

            if (knowledgeList.size() == 0) {
                knowledgeList = knowledgeRepositoryDao.getByTags(tagFirstWord, serviceTagThis, null, tagFirstWord.getCommunity());
            }

			if (knowledgeList.size() > 0) {
				knowledge = knowledgeList.get(0);
			}
		}

		// Проверим указано ли уже у первого слова что это множество
		knowledgeList = knowledgeRepositoryDao.getByTags(tagFirstWord, serviceTagThis, serviceTagMany, tagFirstWord.getCommunity());
		if (knowledgeList.size() == 0) {
			if (knowledge != null) {
				JSONObject jsonObject;

				// Указываем тегу что это множество
				jsonObject = new JSONObject();
				jsonObject.put("knowledge_rep_id", knowledge.getId());
				jsonObject.put("thesaurus_tag", ServiceTags.MANY);
				jsonObject.put("is_service_tag", ServiceTags.MANY);
				answerTheQuestion(jsonObject.toString(), sourceOrigin, source);
			}
		}

		// Для начала сформируем список свойств
		String properties = "";
		List<String> propertiesList = new ArrayList<>();

		for (String str : list.get(0)) {
			if (str.equals(list.get(0).get(1))) {
				properties = str;
			} else if (! str.equals(firstWord)) {
				properties += "." + str;
			}

			if (! str.equals(firstWord)) {
				propertiesList.add(str);
			}
		}

		// Добавляем свойства для множества
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("knowledge_rep_id", knowledge.getId());
		jsonObject.put("thesaurus_tag", properties);
		answerTheQuestionMany(jsonObject.toString(), sourceOrigin, source);

		Thesaurus tagObject = null;
		for(List<String> row : list) {
			if (row != list.get(0)) {
				int index = 0;
				for (String col : row) {
					if (index == 0) {
						tagObject = thesaurusDao.getByEssence(col, tagFirstWord.getCommunity());
						knowledge = null;

						if (tagObject == null) {
							tagObject = new Thesaurus();
							tagObject.setIsServiceTag(false);
							tagObject.setEssence(col);
							tagObject.setSinonim("");
							tagObject.setFixDateEssence(new Date(System.currentTimeMillis()));
							tagObject.setEssenceOwner(sharerDao.getById(SecurityUtils.getUser().getId()));
							tagObject.setAttentionFrequency(0);
							tagObject.setFrequencyEssence(0);
							tagObject.setIsPersonalData(false);
                            tagObject.setIsObject(false);
                            tagObject.setCommunity(tagFirstWord.getCommunity());
                            tagObject.setSourceOrigin(getCyberbrainObjectByName(sourceOrigin));
                            tagObject.setSource(source);

							thesaurusDao.save(tagObject);
							knowledge = addRecordRepositoryOfKnowledge(tagObject, sourceOrigin, source, tagObject.getCommunity());

                            addScoreSharer(ScoreObjects.THESAURUS_NEW_RECORD.getName(), CyberbrainObjects.THESAURUS.getName(), tagObject.getId(), tagObject.getCommunity());
						} else {
							// находим запись в хранилище знаний
							knowledgeList = knowledgeRepositoryDao.getByTags(tagObject, serviceTagThis, serviceTagQuestion, tagObject.getCommunity());

                            if (knowledgeList.size() == 0) {
                                knowledgeList = knowledgeRepositoryDao.getByTags(tagObject, serviceTagThis, null, tagObject.getCommunity());
                            }

							if (knowledgeList.size() > 0) {
								knowledge = knowledgeList.get(0);
							} else {
								// находим запись в хранилище знаний
								knowledgeList = knowledgeRepositoryDao.getByTags(tagObject, serviceTagThis, tagFirstWord, tagObject.getCommunity());

								if (knowledgeList.size() == 0) {
									knowledge = addRecordRepositoryOfKnowledge(tagObject, sourceOrigin, source, tagObject.getCommunity());
								}
							}
						}

						if (knowledge != null) {
							jsonObject = new JSONObject();
							jsonObject.put("knowledge_rep_id", knowledge.getId());
							jsonObject.put("thesaurus_tag", firstWord);
							answerTheQuestion(jsonObject.toString(), sourceOrigin, source);
						}
					} else {
						knowledge = null;

						// Значение свойства это тоже тег и его надо добавить в тезаурусс
						Thesaurus tagValue = getTagByEssence(col, false, sourceOrigin, source, tagFirstWord.getCommunity().getId());
						Thesaurus tagProperty = thesaurusDao.getByEssence(list.get(0).get(index), tagFirstWord.getCommunity());

						// находим запись в хранилище знаний
						knowledgeList = knowledgeRepositoryDao.getByTags(tagObject, serviceTagThis, tagFirstWord, tagFirstWord.getCommunity());

						if (knowledgeList.size() > 0) {
							knowledge = knowledgeList.get(0);
						}

						// обновим свойство
						if (knowledge != null) {
                            if (knowledgeRepositoryDao.getByTags(tagObject, tagProperty, tagValue, tagObject.getCommunity()).size() == 0) {
                                jsonObject = new JSONObject();
                                jsonObject.put("knowledge_rep_id", knowledge.getId());
                                jsonObject.put("thesaurus_tag_property_id", tagProperty.getId());
                                jsonObject.put("thesaurus_tag", col);
                                jsonObject.put("value_mera", "");

                                answerTheQuestionProperty(jsonObject.toString(), sourceOrigin, source);
                            }
						}
					}

					index++;
				}
			}
		}

		return true;
	}

    /**
     * Добавить пользователю определенное количество баллов
     * @param object - объект КиберМозга, за конкретный объект можно получить определенное кол-во баллов
     */
    public void addScoreSharer(String object, String sourceOrigin, Long source, CommunityEntity community) {
        ScoreObject scoreObject = scoreObjectDao.getByName(object);
        ScoreInfo scoreInfo = scoreInfoDao.getByObject(scoreObject, null);

        ScoreSharer scoreSharer = new ScoreSharer();
        scoreSharer.setCreationDate(new Date(System.currentTimeMillis()));
        scoreSharer.setUserEntity(sharerDao.getById(SecurityUtils.getUser().getId()));
        scoreSharer.setScoreObject(scoreObject);
        scoreSharer.setScore(scoreInfo.getScore());
        scoreSharer.setCommunity(community);
        scoreSharer.setSourceOrigin(getCyberbrainObjectByName(sourceOrigin));
        scoreSharer.setSource(source);

        scoreSharerDao.save(scoreSharer);
    }

    /**
     * Ответ на вопрос который находится рядом с разделами кибермозга
     * Самый приоритетный вопрос по рейтингу (объем внимания * частота)
     * @param jsonData
     * @return
     */
    public boolean answerTheQuestionByPriority(String jsonData, String sourceOrigin, Long source) {
        JSONObject jsonObject = new JSONObject(jsonData);

        String type = jsonObject.get("type").toString().trim();
        String knowledge_rep_id = jsonObject.get("knowledge_rep_id").toString().trim();
        String thesaurus_tag_property_id = jsonObject.get("thesaurus_tag_property_id").toString().trim();
        String answer = jsonObject.get("answer").toString().trim();

        jsonObject = new JSONObject();

        if (type.equals("this")) {
            jsonObject.put("knowledge_rep_id", knowledge_rep_id);
            jsonObject.put("thesaurus_tag", answer);

            answerTheQuestion(jsonObject.toString(), sourceOrigin, source);
        } else if (type.equals("many")) {
            jsonObject.put("knowledge_rep_id", knowledge_rep_id);
            jsonObject.put("thesaurus_tag", answer);

            answerTheQuestionMany(jsonObject.toString(), sourceOrigin, source);
        } else if (type.equals("property")) {
            jsonObject.put("knowledge_rep_id", knowledge_rep_id);
            jsonObject.put("thesaurus_tag_property_id", thesaurus_tag_property_id);
            jsonObject.put("thesaurus_tag", answer);
            jsonObject.put("value_mera", "");
            answerTheQuestionProperty(jsonObject.toString(), sourceOrigin, source);
        }

        return true;
    }

    /**
     * Наследование свойств множества
     * Если при ответе на вопрос "что это" введенный тег это какоето множество
     * тогда указать текущему тегу что это множество и занаследовать все свойства введенного множества
     * @return true если введенный тег это множество
     */
    private boolean inheritancePropertiesMany(Thesaurus tagOwnew, Thesaurus tag, String sourceOrigin, Long source) {
        Thesaurus serviceTagThis = getTagByEssence(ServiceTags.THIS, true, sourceOrigin, source, null);
        Thesaurus serviceTagMany = getTagByEssence(ServiceTags.MANY, true, sourceOrigin, source, null);
        Thesaurus serviceTagProperty = getTagByEssence(ServiceTags.PROPERTY, true, sourceOrigin, source, null);

        List<KnowledgeRepository> knowledgeList = knowledgeRepositoryDao.getByTags(tagOwnew, serviceTagThis, serviceTagMany, tagOwnew.getCommunity());
        if (knowledgeList.size() > 0) {
            KnowledgeRepository knowledge = knowledgeList.get(0);

            // теперь занаследуем свойства
            // для начала найдем все свойства нужного нам множества
            knowledgeList = knowledgeRepositoryDao.getByTags(tag, serviceTagProperty, null, tag.getCommunity());
            String properties = "";

            for (KnowledgeRepository obj : knowledgeList) {
                if (properties.equals("")) {
                    properties = obj.getTag().getEssence();
                } else {
                    properties += "." + obj.getTag().getEssence();
                }
            }

            // если есть какие либо свойства то занаследуем их
            if (!properties.equals("")) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("knowledge_rep_id", knowledge.getId());
                jsonObject.put("thesaurus_tag", properties);
                answerTheQuestionMany(jsonObject.toString(), sourceOrigin, source);
            }

            return true;
        }

        return false;
    }

    public boolean startReplication(String jsonData, String sourceOrigin, Long source) {
        JSONObject jsonObject = new JSONObject(jsonData);

        Long communityId = Long.valueOf(jsonObject.get("community_id").toString());
        CommunityEntity community = communityDao.getById(communityId);

        Thesaurus serviceTagThis = getTagByEssence(ServiceTags.THIS, true, sourceOrigin, source, null);
        Thesaurus serviceTagCopy = getTagByEssence(ServiceTags.COPY, true, sourceOrigin, source, null);

        List<KnowledgeRepository> knowledgeList = knowledgeRepositoryDao.getObjectsToBeCopied(serviceTagCopy, community);
        for (KnowledgeRepository knowledge : knowledgeList) {
			replicationCopyStatus(knowledge, community, sourceOrigin, source);
			replicationCopyAffect(knowledge, community, sourceOrigin, source);

			// Присваиваем У к тому же множеству к которому присвоен Х
			List<KnowledgeRepository> list = knowledgeRepositoryDao.getByTags(knowledge.getTag(), serviceTagThis, null, community);
			if (list.size() > 0) {
				KnowledgeRepository obj = list.get(0);
				list = knowledgeRepositoryDao.getByTags(knowledge.getTagOwner(), serviceTagThis, obj.getTag(), community);

				if (list.size() == 0) {
					KnowledgeRepository newKnowledge = addRecordRepositoryOfKnowledge(knowledge.getTagOwner(), sourceOrigin, source, community);

					// Указываем тегу что это множество
					JSONObject newJsonData = new JSONObject();
					newJsonData.put("knowledge_rep_id", newKnowledge.getId());
					newJsonData.put("thesaurus_tag", obj.getTag().getEssence());
					answerTheQuestion(newJsonData.toString(), sourceOrigin, source);
				}
			}
        }

		// После обработки всех действий еще раз проверим на наличие вновь созданных копий объектов (рекурсия)
		knowledgeList = knowledgeRepositoryDao.getObjectsToBeCopied(serviceTagCopy, community);
		if (knowledgeList.size() > 0) {
			startReplication(jsonData, sourceOrigin, source);
		}

        return true;
    }

    /**
     * Копирование объекта Х в ХЗ где атрибут = служебный тег = "Состояние"
     */
    private void replicationCopyStatus(KnowledgeRepository knowledge, CommunityEntity community, String sourceOrigin, Long source) {
        Thesaurus serviceTagStatus = getTagByEssence(ServiceTags.STATUS, true, sourceOrigin, source, null);
		Thesaurus serviceTagAffect = getTagByEssence(ServiceTags.AFFECT, true, sourceOrigin, source, null);

        // получение списка объектов для копирования
        List<KnowledgeRepository> listForCopy = knowledgeRepositoryDao.getByTags(knowledge.getTag(), serviceTagStatus, null, community);

        List<KnowledgeRepository> list = new ArrayList<>();
        for (KnowledgeRepository obj : listForCopy) {
            KnowledgeRepository newKnowledge = obj.clone();
            newKnowledge.setOwner(knowledge.getTag().getEssenceOwner());
            newKnowledge.setTagOwner(knowledge.getTagOwner());
            knowledgeRepositoryDao.save(newKnowledge);
            list.add(newKnowledge);
        }

        knowledge.setShowInQuestions(false);
        knowledgeRepositoryDao.update(knowledge);

        if (list.size() > 0) {
            Collections.sort(list, new Comparator<KnowledgeRepository>() {
                public int compare(KnowledgeRepository obj1, KnowledgeRepository obj2) {
                    return ComparisonChain.start()
							.compare(obj2.getLifecycleStatus(), obj1.getLifecycleStatus())
							.compare(obj2.getFixTimeChange(), obj1.getFixTimeChange())
							.result();
                }
            });

            // заполнение поля next
            Long next = null;
            for (KnowledgeRepository obj : list) {
                obj.setNext(next);
				obj.setShowInQuestions(true);
				obj.setIsTopical(null);

				//проверим заполненно ли поле change_if
				List<KnowledgeRepository> tmpList = knowledgeRepositoryDao.getByTags(knowledge.getTagOwner(), serviceTagAffect, null, community);
				if (tmpList.size() > 0) {
					if (obj.getChangeIf() != null) {
						KnowledgeRepository tmpKnowledge = knowledgeRepositoryDao.getById(obj.getChangeIf());
						tmpList = knowledgeRepositoryDao.getByTags(tmpList.get(0).getTag(), serviceTagStatus, tmpKnowledge.getTag(), community);
						if (tmpList.size() > 0) {
							obj.setChangeIf(tmpList.get(0).getId());
						} else {
							obj.setChangeIf(null);
						}
					}
				} else {
					obj.setChangeIf(null);
				}

				knowledgeRepositoryDao.update(obj);
                next = obj.getId();
            }
			list.get(list.size() - 1).setIsTopical(true);

            // заполнение поля даты готовности
            // предварительное заполнение поля меры
            Date readyDate = null;
            double trackDays;

            for (KnowledgeRepository obj : list) {
                if (obj.getLifecycleStatus() == 5) {
                    readyDate = obj.getTimeReady();
                }
            }

            if (readyDate != null) {
                for (KnowledgeRepository obj : list) {
                    if (obj.getLifecycleStatus() < 5) {
                        trackDays = DateUtils.getDistanceDays(readyDate, obj.getTimeReady());
                        obj.setMera(trackDays);
                        knowledgeRepositoryDao.update(obj);
                    } else if (obj.getLifecycleStatus() == 5) {
                        obj.setMera(0.0);
                        knowledgeRepositoryDao.update(obj);
                    } else if (obj.getLifecycleStatus() > 5) {
                        trackDays = DateUtils.getDistanceDays(readyDate, obj.getTimeReady());
                        obj.setMera(trackDays);
                        knowledgeRepositoryDao.update(obj);
                    }
                }

                readyDate = knowledge.getTimeReady();
                if (readyDate != null) {
					Date newTimeReady;
					for (KnowledgeRepository obj : list) {
						newTimeReady = DateUtils.add(readyDate, Calendar.DAY_OF_YEAR, obj.getMera().intValue());
						obj.setTimeReady(newTimeReady);
						knowledgeRepositoryDao.update(obj);
					}
				}
            } else {
                for (KnowledgeRepository obj : list) {
                    if (obj.getLifecycleStatus() != 5) {
                        obj.setTimeReady(null);
                        knowledgeRepositoryDao.update(obj);
                    }
                }
            }
        }
    }

    /**
     * Копирование Y "Влияет" на объект Х
     */
    private void replicationCopyAffect(KnowledgeRepository knowledge, CommunityEntity community, String sourceOrigin, Long source) {
        Thesaurus serviceTagAffect = getTagByEssence(ServiceTags.AFFECT, true, sourceOrigin, source, null);
        Thesaurus serviceTagThis = getTagByEssence(ServiceTags.THIS, true, sourceOrigin, source, null);
        Thesaurus serviceTagQuestion = getTagByEssence(ServiceTags.QUESTION, true, sourceOrigin, source, null);
        Thesaurus serviceTagMany = getTagByEssence(ServiceTags.MANY, true, sourceOrigin, source, null);
        Thesaurus serviceTagCopy = getTagByEssence(ServiceTags.COPY, true, sourceOrigin, source, null);

        // получение списка объектов для копирования
        List<KnowledgeRepository> listForCopy = knowledgeRepositoryDao.getByTags(null, serviceTagAffect, knowledge.getTag(), community);

        for (KnowledgeRepository obj : listForCopy) {
            // Если задано множество определим его
            int manyCount = 0;
            KnowledgeRepository knowledgeMany = null;
            List<KnowledgeRepository> knowledgeList = knowledgeRepositoryDao.getByTags(obj.getTagOwner(), serviceTagThis, null, knowledge.getCommunity());
            if (knowledgeList.size() > 0) {
                knowledgeList = knowledgeRepositoryDao.getByTags(knowledgeList.get(0).getTag(), serviceTagThis, serviceTagMany, knowledge.getCommunity());
                if (knowledgeList.size() > 0) {
                    knowledgeMany = knowledgeList.get(0);
                    manyCount = knowledgeMany.getMera().intValue() + 1;
                }
            }

            // Добавим новые теги влияния
            Thesaurus newTag = getTagByEssence(obj.getTagOwner().getEssence() + " " + String.valueOf(manyCount), false, sourceOrigin, source, community.getId());

            if (knowledgeMany != null) {
                knowledgeList = knowledgeRepositoryDao.getByTags(newTag, serviceTagThis, serviceTagQuestion, community);
                KnowledgeRepository newKnowledge = knowledgeList.get(0);

                // Указываем тегу что это множество
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("knowledge_rep_id", newKnowledge.getId());
                jsonObject.put("thesaurus_tag", knowledgeMany.getTagOwner().getEssence());
                answerTheQuestion(jsonObject.toString(), sourceOrigin, source);
            }

            KnowledgeRepository newKnowledge = addRecordRepositoryOfKnowledge(newTag, sourceOrigin, source, community);
            newKnowledge.setAttribute(serviceTagAffect);
            newKnowledge.setTag(knowledge.getTagOwner());
            newKnowledge.setOwner(knowledge.getTag().getEssenceOwner());
            knowledgeRepositoryDao.update(newKnowledge);

            newKnowledge = addRecordRepositoryOfKnowledge(newTag, sourceOrigin, source, community);
            newKnowledge.setAttribute(serviceTagCopy);
            newKnowledge.setTag(obj.getTagOwner());
            newKnowledge.setOwner(knowledge.getTag().getEssenceOwner());
            knowledgeRepositoryDao.update(newKnowledge);
        }
    }

    public String newObjectWizardForm(String jsonData, String sourceOrigin, Long source) {
        JSONObject jsonObject = new JSONObject(jsonData);

        Integer step = Integer.valueOf(jsonObject.get("step").toString());
        Long communityId = Long.valueOf(jsonObject.get("community_id").toString());
        String strNewManyId = jsonObject.get("new_many_id").toString();
        String strNewManyName = jsonObject.get("new_many_name").toString();
        Boolean newManyIsNumbered = Boolean.valueOf(jsonObject.get("new_many_is_numbered").toString());
        String strNewManyProperties = jsonObject.get("new_many_properties").toString();
        String strNewObjectName = jsonObject.get("new_object_name").toString();
		Boolean copyIsCreateCopy = Boolean.valueOf(jsonObject.get("copy_is_create_copy").toString());
        String strCopyObjectSourceName = jsonObject.get("copy_object_source_name").toString();
        String strCopyObjectTimeReady = jsonObject.get("copy_object_time_ready").toString();
        String strCopyObjectResponsibleId = jsonObject.get("copy_object_responsible_id").toString();

        Thesaurus serviceTagThis = getTagByEssence(ServiceTags.THIS, true, sourceOrigin, source, null);
        Thesaurus serviceTagQuestion = getTagByEssence(ServiceTags.QUESTION, true, sourceOrigin, source, null);
        Thesaurus serviceTagMany = getTagByEssence(ServiceTags.MANY, true, sourceOrigin, source, null);
        Thesaurus serviceTagStatus = getTagByEssence(ServiceTags.STATUS, true, sourceOrigin, source, null);
        Thesaurus serviceTagAffect = getTagByEssence(ServiceTags.AFFECT, true, sourceOrigin, source, null);

        if (step == 5) {
            //был пройден шаг 4 в визард форме теперь надо создать новый объект
            Thesaurus tagMany = getTagByEssence(strNewManyName, false, sourceOrigin, source, communityId);
            tagMany.setIsNumbered(newManyIsNumbered);
			tagMany.setIsObject(false);
            thesaurusDao.update(tagMany);

            if (strNewManyId.equals("-1") || strNewManyId.equals("")) {
                //шаг 1
                List<KnowledgeRepository> knowledgeList = knowledgeRepositoryDao.getByTags(tagMany, serviceTagThis, serviceTagMany, tagMany.getCommunity());
                if (knowledgeList.size() == 0) {
                    knowledgeList = knowledgeRepositoryDao.getByTags(tagMany, serviceTagThis, serviceTagQuestion, tagMany.getCommunity());
                    if (knowledgeList.size() > 0) {
                        // Указываем тегу что это множество
                        jsonObject = new JSONObject();
                        jsonObject.put("knowledge_rep_id", knowledgeList.get(0).getId());
                        jsonObject.put("thesaurus_tag", ServiceTags.MANY);
                        jsonObject.put("is_service_tag", ServiceTags.MANY);
                        answerTheQuestion(jsonObject.toString(), sourceOrigin, source);
                    }
                }

                //шаг 2
                knowledgeList = knowledgeRepositoryDao.getByTags(tagMany, serviceTagThis, serviceTagMany, tagMany.getCommunity());
                if (knowledgeList.size() > 0) {
                    // Добавляем свойства для множества
                    jsonObject = new JSONObject();
                    jsonObject.put("knowledge_rep_id", knowledgeList.get(0).getId());
                    jsonObject.put("thesaurus_tag", strNewManyProperties);
                    answerTheQuestionMany(jsonObject.toString(), sourceOrigin, source);
                }
            }

            //шаг 3
            Thesaurus tagManyNewObject = getTagByEssence(strNewObjectName, false, sourceOrigin, source, communityId);
            List<KnowledgeRepository> knowledgeList = knowledgeRepositoryDao.getByTags(tagManyNewObject, serviceTagThis, tagMany, tagManyNewObject.getCommunity());
            if (knowledgeList.size() == 0) {
                setQuestionAnswer(tagManyNewObject, tagMany.getEssence(), false, sourceOrigin, source);
            }

            //шаг 4 (копия)
            if (copyIsCreateCopy && !strCopyObjectSourceName.equals("") && !strCopyObjectTimeReady.equals("") && !strCopyObjectResponsibleId.equals("")) {
				jsonObject = new JSONObject();
                jsonObject.put("community_id", communityId.toString());
                jsonObject.put("source_object", strCopyObjectSourceName);
                jsonObject.put("target_object", strNewObjectName);
                jsonObject.put("time_ready", strCopyObjectTimeReady);
                jsonObject.put("responsible_id", strCopyObjectResponsibleId);
                jsonObject.put("tag_many_id", tagMany.getId());
                copyObject(jsonObject.toString(), sourceOrigin, source);

                jsonObject = new JSONObject();
                jsonObject.put("community_id", communityId.toString());
                startReplication(jsonObject.toString(), sourceOrigin, source);
            }

            jsonObject = new JSONObject(jsonData);
            jsonObject.put("tagOwnerId", tagManyNewObject.getId().toString());
        } else if (step == 8) {
            //шаг 8
            JSONArray jsonArray = jsonObject.getJSONArray("affects");

            List<JSONObject> list = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(new JSONObject(jsonArray.get(i).toString()));
            }

            for(JSONObject object : list) {
                Long knowledgeId = Long.valueOf(object.get("knowledge_rep_id").toString());
                String manyNameChangeif = object.get("many_name_changeif").toString();
                String objectNameChangeif = object.get("object_name_changeif").toString();
				Boolean readOnly = Boolean.valueOf(object.get("read_only").toString());

                if (!manyNameChangeif.equals("") && !objectNameChangeif.equals("") && !readOnly) {
                    KnowledgeRepository knowledgeTrack = knowledgeRepositoryDao.getById(knowledgeId);

                    //создадим множество если требуется
                    Thesaurus tagMany = getTagByEssence(manyNameChangeif, false, sourceOrigin, source, communityId);
					tagMany.setIsObject(false);
					thesaurusDao.update(tagMany);

                    List<KnowledgeRepository> knowledgeList = knowledgeRepositoryDao.getByTags(tagMany, serviceTagThis, serviceTagMany, tagMany.getCommunity());
                    if (knowledgeList.size() == 0) {
                        knowledgeList = knowledgeRepositoryDao.getByTags(tagMany, serviceTagThis, serviceTagQuestion, tagMany.getCommunity());
                        if (knowledgeList.size() > 0) {
                            // Указываем тегу что это множество
                            jsonObject = new JSONObject();
                            jsonObject.put("knowledge_rep_id", knowledgeList.get(0).getId());
                            jsonObject.put("thesaurus_tag", ServiceTags.MANY);
                            jsonObject.put("is_service_tag", ServiceTags.MANY);
                            answerTheQuestion(jsonObject.toString(), sourceOrigin, source);
                        }
                    }

                    Thesaurus tagObject = getTagByEssence(objectNameChangeif, false, sourceOrigin, source, communityId);
                    knowledgeList = knowledgeRepositoryDao.getByTags(tagObject, serviceTagThis, tagMany, tagObject.getCommunity());
                    if (knowledgeList.size() == 0) {
                        setQuestionAnswer(tagObject, tagMany.getEssence(), false, sourceOrigin, source);
                    }

                    //создадим трек если требуется
                    KnowledgeRepository trackObject = null;
                    Thesaurus tagCustomStatus = getTagByEssence("автоматически сгенерированный трек", false, sourceOrigin, source, communityId);
                    knowledgeList = knowledgeRepositoryDao.getByTags(tagObject, serviceTagStatus, null, tagObject.getCommunity());
                    if (knowledgeList.size() == 0) {
                        trackObject = addRecordRepositoryOfKnowledge(tagObject, sourceOrigin, source, tagObject.getCommunity());
                        trackObject.setTimeReady(new Date(System.currentTimeMillis()));
						trackObject.setAttribute(serviceTagStatus);
                        trackObject.setTag(tagCustomStatus);
                        trackObject.setLifecycleStatus(5);
						trackObject.setIsTopical(true);
                        knowledgeRepositoryDao.update(trackObject);
                    } else {
                        for(KnowledgeRepository track : knowledgeList) {
                            if (track.getLifecycleStatus() == 5) {
                                trackObject = track;
                            }
                        }
                    }

                    if (trackObject != null) {
                        trackObject.setChangeIf(knowledgeTrack.getId());
                        knowledgeRepositoryDao.update(trackObject);

                        Thesaurus tagManyNewObject = getTagByEssence(strNewObjectName, false, sourceOrigin, source, communityId);

                        // Создавать запись в ХЗ - Y Влияет X
                        knowledgeList = knowledgeRepositoryDao.getByTags(tagObject, serviceTagAffect, tagManyNewObject, tagObject.getCommunity());
                        if (knowledgeList.size() == 0) {
                            KnowledgeRepository newKnowledge = addRecordRepositoryOfKnowledge(tagObject, sourceOrigin, source, tagObject.getCommunity());
                            newKnowledge.setTagOwner(tagObject);
                            newKnowledge.setAttribute(serviceTagAffect);
                            newKnowledge.setTag(tagManyNewObject);
                            newKnowledge.setLifecycleStatus(knowledgeTrack.getLifecycleStatus());
                            knowledgeRepositoryDao.update(newKnowledge);
                        }
                    }
                }
            }
        }

        return jsonObject.toString();
    }

	/**
	 * Загрузка связей влияния в хранилище знаний
	 */
	public boolean importDataKnowledgeRepositoryCondition(Map<String, Object> dataMap, String sourceOrigin, Long source) {
		List<List<String>> list = (List<List<String>>) dataMap.get("data");
		CommunityEntity community = communityDao.getById((Long) dataMap.get("communityId"));

		Thesaurus serviceTagThis = getTagByEssence(ServiceTags.THIS, true, sourceOrigin, source, null);
		Thesaurus serviceTagQuestion = getTagByEssence(ServiceTags.QUESTION, true, sourceOrigin, source, null);
		Thesaurus serviceTagStatus = getTagByEssence(ServiceTags.STATUS, true, sourceOrigin, source, null);
		Thesaurus serviceTagAffect = getTagByEssence(ServiceTags.AFFECT, true, sourceOrigin, source, null);

		List<KnowledgeRepository> knowledgeList;

		for (List<String> row : list) {
			Thesaurus tagMany = getTagByEssence(row.get(0), false, sourceOrigin, source, community.getId());
			knowledgeList = knowledgeRepositoryDao.getByTags(tagMany, serviceTagThis, serviceTagQuestion, tagMany.getCommunity());
			if (knowledgeList.size() > 0) {
				// Указываем тегу что это множество
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("knowledge_rep_id", knowledgeList.get(0).getId());
				jsonObject.put("thesaurus_tag", ServiceTags.MANY);
				jsonObject.put("is_service_tag", ServiceTags.MANY);
				answerTheQuestion(jsonObject.toString(), sourceOrigin, source);
			}

			Thesaurus tagObject = getTagByEssence(row.get(1), false, sourceOrigin, source, community.getId());
			knowledgeList = knowledgeRepositoryDao.getByTags(tagObject, serviceTagThis, serviceTagQuestion, tagObject.getCommunity());
			if (knowledgeList.size() > 0) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("knowledge_rep_id", knowledgeList.get(0).getId());
				jsonObject.put("thesaurus_tag", tagMany.getEssence());
				answerTheQuestion(jsonObject.toString(), sourceOrigin, source);
			}

			//создадим трек если требуется
			KnowledgeRepository trackObject = null;
			Thesaurus tagCustomStatus = getTagByEssence(row.get(3), false, sourceOrigin, source, community.getId());
			knowledgeList = knowledgeRepositoryDao.getByTags(tagObject, serviceTagStatus, tagCustomStatus, tagObject.getCommunity());
			if (knowledgeList.size() == 0) {
				trackObject = addRecordRepositoryOfKnowledge(tagObject, sourceOrigin, source, tagObject.getCommunity());
				trackObject.setTimeReady(new Date(System.currentTimeMillis()));
				trackObject.setAttribute(serviceTagStatus);
				trackObject.setTag(tagCustomStatus);
				trackObject.setLifecycleStatus(Integer.valueOf(row.get(2)));
				trackObject.setIsTopical(true);
				if (Integer.valueOf(row.get(2)) == 5) {
					trackObject.setShowInQuestions(false);
				}
				knowledgeRepositoryDao.update(trackObject);
			} else {
				for (KnowledgeRepository track : knowledgeList) {
					if (track.getLifecycleStatus() == 5 && track.getChangeIf() == null) {
						trackObject = track;
						break;
					}
				}
				if (trackObject == null) {
					trackObject = addRecordRepositoryOfKnowledge(tagObject, sourceOrigin, source, tagObject.getCommunity());
					trackObject.setTimeReady(new Date(System.currentTimeMillis()));
					trackObject.setAttribute(serviceTagStatus);
					trackObject.setTag(tagCustomStatus);
					trackObject.setLifecycleStatus(Integer.valueOf(row.get(2)));
					trackObject.setIsTopical(true);
					if (Integer.valueOf(row.get(2)) == 5) {
						trackObject.setShowInQuestions(false);
					}
					knowledgeRepositoryDao.update(trackObject);
				}
			}

			//аналогичные действия для влияющего объекта
			if (!row.get(4).equals("") && !row.get(5).equals("") && !row.get(6).equals("") && !row.get(7).equals("")) {
				tagMany = getTagByEssence(row.get(4), false, sourceOrigin, source, community.getId());
				knowledgeList = knowledgeRepositoryDao.getByTags(tagMany, serviceTagThis, serviceTagQuestion, tagMany.getCommunity());
				if (knowledgeList.size() > 0) {
					// Указываем тегу что это множество
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("knowledge_rep_id", knowledgeList.get(0).getId());
					jsonObject.put("thesaurus_tag", ServiceTags.MANY);
					jsonObject.put("is_service_tag", ServiceTags.MANY);
					answerTheQuestion(jsonObject.toString(), sourceOrigin, source);
				}

				Thesaurus tagObjectCondition = getTagByEssence(row.get(5), false, sourceOrigin, source, community.getId());
				knowledgeList = knowledgeRepositoryDao.getByTags(tagObjectCondition, serviceTagThis, serviceTagQuestion, tagObjectCondition.getCommunity());
				if (knowledgeList.size() > 0) {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("knowledge_rep_id", knowledgeList.get(0).getId());
					jsonObject.put("thesaurus_tag", tagMany.getEssence());
					answerTheQuestion(jsonObject.toString(), sourceOrigin, source);
				}

				// Создавать запись в ХЗ - Y Влияет X
				knowledgeList = knowledgeRepositoryDao.getByTags(tagObjectCondition, serviceTagAffect, tagObject, tagObjectCondition.getCommunity());
				if (knowledgeList.size() == 0) {
					KnowledgeRepository newKnowledge = addRecordRepositoryOfKnowledge(tagObjectCondition, sourceOrigin, source, tagObjectCondition.getCommunity());
					newKnowledge.setAttribute(serviceTagAffect);
					newKnowledge.setTag(tagObject);
					knowledgeRepositoryDao.update(newKnowledge);
				}

				//создадим трек если требуется
				KnowledgeRepository trackObjectCondition;
				tagCustomStatus = getTagByEssence(row.get(7), false, sourceOrigin, source, community.getId());

				knowledgeList = knowledgeRepositoryDao.getByTags(tagObjectCondition, serviceTagStatus, tagCustomStatus, tagObject.getCommunity());
				if (knowledgeList.size() == 0) {
					trackObjectCondition = addRecordRepositoryOfKnowledge(tagObjectCondition, sourceOrigin, source, tagObjectCondition.getCommunity());
					trackObjectCondition.setTimeReady(new Date(System.currentTimeMillis()));
					trackObjectCondition.setAttribute(serviceTagStatus);
					trackObjectCondition.setTag(tagCustomStatus);
					trackObjectCondition.setLifecycleStatus(Integer.valueOf(row.get(6)));
					trackObjectCondition.setIsTopical(true);
					if (Integer.valueOf(row.get(6)) == 5) {
						trackObjectCondition.setShowInQuestions(false);
					}
					trackObjectCondition.setChangeIf(trackObject.getId());
					knowledgeRepositoryDao.update(trackObjectCondition);
				} else {
					boolean createTrack = true;
					for(KnowledgeRepository track : knowledgeList) {
						if (Objects.equals(trackObject.getId(), track.getChangeIf())) {
							createTrack = false;
							break;
						}
					}
					if (createTrack) {
						trackObjectCondition = addRecordRepositoryOfKnowledge(tagObjectCondition, sourceOrigin, source, tagObjectCondition.getCommunity());
						trackObjectCondition.setTimeReady(new Date(System.currentTimeMillis()));
						trackObjectCondition.setAttribute(serviceTagStatus);
						trackObjectCondition.setTag(tagCustomStatus);
						trackObjectCondition.setLifecycleStatus(Integer.valueOf(row.get(6)));
						trackObjectCondition.setIsTopical(true);
						if (Integer.valueOf(row.get(6)) == 5) {
							trackObjectCondition.setShowInQuestions(false);
						}
						trackObjectCondition.setChangeIf(trackObject.getId());
						knowledgeRepositoryDao.update(trackObjectCondition);
					}
				}
			}
		}

		return true;
	}

	/**
	 * Переодическое обновление поля "мера" для треков.
	 * В поле "мера" должно записываться количество влияний (влияющих треков) для трека.
	 */
	public void updateAffectsForTracks() {
		logger.info("Запуск обновления кол-ва влияний для треков.");

		Thesaurus serviceTagStatus = getTagByEssence(ServiceTags.STATUS, true, null, null, null);

		//получим список всех треков
		List<KnowledgeRepository> knowledgeList = knowledgeRepositoryDao.getByTags(null, serviceTagStatus, null);
		for (KnowledgeRepository knowledge : knowledgeList) {
			//получим список влияющих треков
			List<KnowledgeRepository> affectsList = knowledgeRepositoryDao.getAffectList(knowledge, knowledge.getTagOwner().getCommunity());
			knowledge.setMera((double) affectsList.size());
			knowledgeRepositoryDao.update(knowledge);
		}

		logger.info("Обновления кол-ва влияний для треков завершено.");
	}
}