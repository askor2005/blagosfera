package ru.radom.kabinet.dao.cyberbrain;

import org.hibernate.SQLQuery;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.askor.blagosfera.domain.community.CommunityMemberStatus;
import ru.askor.blagosfera.domain.user.UserDetailsImpl;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dao.communities.CommunityDao;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.cyberbrain.KnowledgeRepository;
import ru.radom.kabinet.model.cyberbrain.Thesaurus;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.CyberbrainService;
import ru.radom.kabinet.utils.Roles;

import java.math.BigInteger;
import java.util.*;

@Repository("knowledgeRepositoryDao")
public class KnowledgeRepositoryDao extends Dao<KnowledgeRepository> {
    private static class Query {
        /**
         * Запрос на получение списока вопросов
         */
        public static final String GET_QUESTIONS_LIST = "SELECT " +
                "  knowledge_rep.id as knowledge_rep_id, " +
                "  thesaurus_tag_owner.id as thesaurus_tag_owner_id, " +
                "  thesaurus_tag_owner." + Thesaurus.Columns.ESSENCE + " as thesaurus_tag_owner, " +
                "  thesaurus_attribute.id as thesaurus_tag_attribute_id, " +
                "  thesaurus_attribute." + Thesaurus.Columns.ESSENCE + " as thesaurus_tag_attribute " +
                "FROM " + KnowledgeRepository.TABLE_NAME + " knowledge_rep " +
                "  INNER JOIN " + Thesaurus.TABLE_NAME + " thesaurus_tag on thesaurus_tag.id = knowledge_rep." + KnowledgeRepository.Columns.TAG +
                "  INNER JOIN " + Thesaurus.TABLE_NAME + " thesaurus_tag_owner on thesaurus_tag_owner.id = knowledge_rep." + KnowledgeRepository.Columns.TAG_OWNER +
                "  INNER JOIN " + Thesaurus.TABLE_NAME + " thesaurus_attribute on thesaurus_attribute.id = knowledge_rep." + KnowledgeRepository.Columns.ATTRIBUTE;

        /**
         * Ограничение на выбор данных для вопросов (что это)
         */
        public static String GET_QUESTIONS_LIST_RESTRICTION = " WHERE " +
                "knowledge_rep." + KnowledgeRepository.Columns.SHOW_IN_QUESTIONS + " = true and " +
                "thesaurus_tag." + Thesaurus.Columns.ESSENCE + " = '?' and " +
                "lower(thesaurus_attribute." + Thesaurus.Columns.ESSENCE + ") = 'это' and " +
                "knowledge_rep." + KnowledgeRepository.Columns.COMMUNITY + " in (:userCommunitiesIds)";

        /**
         * Ограничение на выбор данных для вопросов (множества)
         */
        public static String GET_QUESTIONS_LIST_RESTRICTION_MANY = " WHERE " +
                "knowledge_rep." + KnowledgeRepository.Columns.SHOW_IN_QUESTIONS + " = true and " +
                "lower(thesaurus_tag." + Thesaurus.Columns.ESSENCE + ") = 'множество' and " +
                "knowledge_rep." + KnowledgeRepository.Columns.COMMUNITY + " in (:userCommunitiesIds)";

        /**
         * Сортировка списка данных
         */
        public static String GET_QUESTIONS_LIST_ORDER_BY = " ORDER BY " +
                "thesaurus_tag_owner." + Thesaurus.Columns.FREQUENCY_ESSENCE + " * thesaurus_tag_owner." + Thesaurus.Columns.ATTENTION_FREQUENCY + " DESC, " +
                "thesaurus_tag_owner." + Thesaurus.Columns.ESSENCE;

        /**
         * Запрос на получение списока вопросов по свойствам
         */
        public static final String GET_QUESTIONS_PROPERTIES_LIST = "SELECT " +
                "  knowledge_rep.id as knowledge_rep_id, " +
                "  tbl.thesaurus_tag_many_id, " +
                "  tbl.thesaurus_tag_many, " +
                "  thesaurus_tag_owner.id as thesaurus_tag_owner_id, " +
                "  thesaurus_tag_owner." + Thesaurus.Columns.ESSENCE + " as thesaurus_tag_owner, " +
                "  tbl.thesaurus_tag_property_id, " +
                "  tbl.thesaurus_tag_property, " +
                "  thesaurus_attribute.id as thesaurus_attribut_id, " +
                "  thesaurus_attribute." + Thesaurus.Columns.ESSENCE + " as thesaurus_attribute " +
                "FROM " + KnowledgeRepository.TABLE_NAME + " knowledge_rep " +
                "  INNER JOIN " + Thesaurus.TABLE_NAME + " thesaurus_tag on thesaurus_tag.id = knowledge_rep." + KnowledgeRepository.Columns.TAG +
                "  INNER JOIN " + Thesaurus.TABLE_NAME + " thesaurus_tag_owner on thesaurus_tag_owner.id = knowledge_rep." + KnowledgeRepository.Columns.TAG_OWNER +
                "  INNER JOIN " + Thesaurus.TABLE_NAME + " thesaurus_attribute on thesaurus_attribute.id = knowledge_rep." + KnowledgeRepository.Columns.ATTRIBUTE  +
                "  INNER JOIN (SELECT " +
                "                 thesaurus_tag_owner.id as thesaurus_tag_many_id, " +
                "                 thesaurus_tag_owner." + Thesaurus.Columns.ESSENCE + " as thesaurus_tag_many, " +
                "                 thesaurus_tag.id as thesaurus_tag_property_id, " +
                "                 thesaurus_tag." + Thesaurus.Columns.ESSENCE + " as thesaurus_tag_property " +
                "              FROM " + KnowledgeRepository.TABLE_NAME + " knowledge_rep " +
                "                 INNER JOIN " + Thesaurus.TABLE_NAME + " thesaurus_tag on thesaurus_tag.id = knowledge_rep.tag " +
                "                 INNER JOIN " + Thesaurus.TABLE_NAME + " thesaurus_tag_owner on thesaurus_tag_owner.id = knowledge_rep." + KnowledgeRepository.Columns.TAG_OWNER +
                "                 INNER JOIN " + Thesaurus.TABLE_NAME + " thesaurus_attribute on thesaurus_attribute.id = knowledge_rep." + KnowledgeRepository.Columns.ATTRIBUTE +
                "              WHERE " +
                "                 lower(thesaurus_attribute." + Thesaurus.Columns.ESSENCE + ") = 'свойство' and " +
                "                 knowledge_rep." + KnowledgeRepository.Columns.SHOW_IN_QUESTIONS + " = true and " +
                "                 knowledge_rep." + KnowledgeRepository.Columns.COMMUNITY + " in (:userCommunitiesIds)) as tbl ON tbl.thesaurus_tag_many_id = knowledge_rep." + KnowledgeRepository.Columns.TAG +
                "  LEFT JOIN (SELECT knowledge_rep." + KnowledgeRepository.Columns.TAG_OWNER + ", knowledge_rep." + KnowledgeRepository.Columns.ATTRIBUTE +
                "             FROM " + KnowledgeRepository.TABLE_NAME + " knowledge_rep " +
                "                INNER JOIN " + Thesaurus.TABLE_NAME + " thesaurus_attribute on thesaurus_attribute.id = knowledge_rep." + KnowledgeRepository.Columns.ATTRIBUTE +
                "             WHERE " +
                "                lower(thesaurus_attribute." + Thesaurus.Columns.ESSENCE + ") not in ('свойство', 'это') and " +
                "                knowledge_rep." + KnowledgeRepository.Columns.COMMUNITY + " in (:userCommunitiesIds)) as tbl2 on tbl2." + KnowledgeRepository.Columns.TAG_OWNER + " = thesaurus_tag_owner.id and tbl2." + KnowledgeRepository.Columns.ATTRIBUTE + " = tbl.thesaurus_tag_property_id" +
                " WHERE " +
                "  tbl2." + KnowledgeRepository.Columns.TAG_OWNER + " is null and " +
                "  lower(thesaurus_attribute." + Thesaurus.Columns.ESSENCE + ") = 'это' and " +
                "  knowledge_rep." + KnowledgeRepository.Columns.COMMUNITY + " in (:userCommunitiesIds)";

        /**
         * Запрос для получения одного вопроса с учетом приоритетов
         */
        public static final String GET_PRIORITY_QUESTION = "SELECT " +
                "  'this' as type, " +
                "  knowledge_rep.id as knowledge_rep_id, " +
                "  -1 as thesaurus_tag_property_id, " +
                "  thesaurus_tag_owner.essence as tag, " +
                "  'Вопрос \"что это\":' as description, " +
                "  thesaurus_tag_owner.frequency_essence * thesaurus_tag_owner.attention_frequency as priority " +
                "FROM cyberbrain_knowledge_repository knowledge_rep  " +
                "  INNER JOIN cyberbrain_thesaurus thesaurus_tag on thesaurus_tag.id = knowledge_rep.tag " +
                "  INNER JOIN cyberbrain_thesaurus thesaurus_tag_owner on thesaurus_tag_owner.id = knowledge_rep.tag_owner " +
                "  INNER JOIN cyberbrain_thesaurus thesaurus_attribute on thesaurus_attribute.id = knowledge_rep.attribute " +
                "WHERE " +
                "  knowledge_rep.show_in_questions = true and " +
                "  thesaurus_tag.essence = '?' and " +
                "  lower(thesaurus_attribute.essence) = 'это' and " +
                "  knowledge_rep." + KnowledgeRepository.Columns.COMMUNITY + " in (:userCommunitiesIds) " +
                "union all " +
                "SELECT " +
                "  'many' as type, " +
                "  knowledge_rep.id as knowledge_rep_id, " +
                "  -1 as thesaurus_tag_property_id, " +
                "  thesaurus_tag_owner.essence as tag, " +
                "  'Вопрос по множеству:' as description, " +
                "  thesaurus_tag_owner.frequency_essence * thesaurus_tag_owner.attention_frequency as priority " +
                "FROM cyberbrain_knowledge_repository knowledge_rep " +
                "  INNER JOIN cyberbrain_thesaurus thesaurus_tag on thesaurus_tag.id = knowledge_rep.tag " +
                "  INNER JOIN cyberbrain_thesaurus thesaurus_tag_owner on thesaurus_tag_owner.id = knowledge_rep.tag_owner " +
                "  INNER JOIN cyberbrain_thesaurus thesaurus_attribute on thesaurus_attribute.id = knowledge_rep.attribute " +
                "WHERE " +
                "  knowledge_rep.show_in_questions = true and " +
                "  lower(thesaurus_tag.essence) = 'множество' and " +
                "  knowledge_rep." + KnowledgeRepository.Columns.COMMUNITY + " in (:userCommunitiesIds) " +
                "union all " +
                "SELECT " +
                "  'property' as type, " +
                "  knowledge_rep.id as knowledge_rep_id, " +
                "  tbl.thesaurus_tag_property_id, " +
                "  tbl.thesaurus_tag_property as tag, " +
                "  'Вопрос по свойству: ' || tbl.thesaurus_tag_many || ' - ' || thesaurus_tag_owner.essence || ' -' as description, " +
                "  thesaurus_tag_owner.frequency_essence * thesaurus_tag_owner.attention_frequency as priority " +
                "FROM cyberbrain_knowledge_repository knowledge_rep " +
                "  INNER JOIN cyberbrain_thesaurus thesaurus_tag on thesaurus_tag.id = knowledge_rep.tag " +
                "  INNER JOIN cyberbrain_thesaurus thesaurus_tag_owner on thesaurus_tag_owner.id = knowledge_rep.tag_owner " +
                "  INNER JOIN cyberbrain_thesaurus thesaurus_attribute on thesaurus_attribute.id = knowledge_rep.attribute " +
                "  INNER JOIN (SELECT " +
                "                thesaurus_tag_owner.id as thesaurus_tag_many_id, " +
                "                thesaurus_tag_owner.essence as thesaurus_tag_many, " +
                "                thesaurus_tag.id as thesaurus_tag_property_id, " +
                "                thesaurus_tag.essence as thesaurus_tag_property " +
                "              FROM cyberbrain_knowledge_repository knowledge_rep " +
                "                INNER JOIN cyberbrain_thesaurus thesaurus_tag on thesaurus_tag.id = knowledge_rep.tag " +
                "                INNER JOIN cyberbrain_thesaurus thesaurus_tag_owner on thesaurus_tag_owner.id = knowledge_rep.tag_owner " +
                "                INNER JOIN cyberbrain_thesaurus thesaurus_attribute on thesaurus_attribute.id = knowledge_rep.attribute " +
                "              WHERE " +
                "                lower(thesaurus_attribute.essence) = 'свойство' and knowledge_rep.show_in_questions = true and " +
                "                knowledge_rep." + KnowledgeRepository.Columns.COMMUNITY + " in (:userCommunitiesIds)) as tbl ON tbl.thesaurus_tag_many_id = knowledge_rep.tag " +
                "  LEFT JOIN (SELECT knowledge_rep.tag_owner, knowledge_rep.attribute " +
                "             FROM cyberbrain_knowledge_repository knowledge_rep " +
                "               INNER JOIN cyberbrain_thesaurus thesaurus_attribute on thesaurus_attribute.id = knowledge_rep.attribute " +
                "             WHERE " +
                "               lower(thesaurus_attribute.essence) not in ('свойство', 'это') and" +
                "               knowledge_rep." + KnowledgeRepository.Columns.COMMUNITY + " in (:userCommunitiesIds)) as tbl2 on tbl2.tag_owner = thesaurus_tag_owner.id and tbl2.attribute = tbl.thesaurus_tag_property_id " +
                "WHERE " +
                "  tbl2.tag_owner is null and " +
                "  lower(thesaurus_attribute.essence) = 'это' and " +
                "  knowledge_rep." + KnowledgeRepository.Columns.COMMUNITY + " in (:userCommunitiesIds) " +
                "ORDER BY priority DESC LIMIT 1";

        /**
         * Запрос на получение списока вопросов по трекам объекта
         */
        public static final String GET_QUESTIONS_TRACKS_LIST = "SELECT " +
                "  main.knowledge_rep_id as knowledge_rep_id, " +
                "  main.thesaurus_tag_many_id as thesaurus_tag_many_id, " +
                "  main.thesaurus_tag_many as thesaurus_tag_many, " +
                "  main.thesaurus_tag_owner_id as thesaurus_tag_owner_id, " +
                "  main.thesaurus_tag_owner as thesaurus_tag_owner, " +
                "  main.custom_status as custom_status, " +
                "  case when main.time_ready is not null then to_char(main.time_ready, 'yyyy/MM/dd HH:mm:ss') else null end as time_ready, " +
                "  main.lifecycle_status as lifecycle_status_index, " +
                "  CASE " +
                "    WHEN main.lifecycle_status = 1 THEN 'Идея' " +
                "    WHEN main.lifecycle_status = 2 THEN 'Декомпозиция' " +
                "    WHEN main.lifecycle_status = 3 THEN 'Контрактовано' " +
                "    WHEN main.lifecycle_status = 4 THEN 'Исполняется' " +
                "    WHEN main.lifecycle_status = 5 THEN 'Готово' " +
                "    WHEN main.lifecycle_status = 6 THEN 'Изношено' " +
                "    WHEN main.lifecycle_status = 7 THEN 'Убирается' " +
                "    WHEN main.lifecycle_status = 8 THEN 'Память' " +
                "  END as lifecycle_status, " +
                "  main.is_track " +
                "FROM (SELECT " +
                "        knowledge_rep.id as knowledge_rep_id, " +
                "        tbl.thesaurus_tag_many_id, " +
                "        tbl.thesaurus_tag_many, " +
                "        thesaurus_tag_owner.id as thesaurus_tag_owner_id, " +
                "        thesaurus_tag_owner.essence as thesaurus_tag_owner, " +
                "        null as custom_status, " +
                "        knowledge_rep.time_ready, " +
                "        knowledge_rep.lifecycle_status, " +
                "        thesaurus_tag_owner.frequency_essence * thesaurus_tag_owner.attention_frequency as priority, " +
                "        0 as is_track " +
                "      FROM cyberbrain_knowledge_repository knowledge_rep " +
                "        INNER JOIN cyberbrain_thesaurus thesaurus_tag on thesaurus_tag.id = knowledge_rep.tag " +
                "        INNER JOIN cyberbrain_thesaurus thesaurus_tag_owner on thesaurus_tag_owner.id = knowledge_rep.tag_owner " +
                "        INNER JOIN cyberbrain_thesaurus thesaurus_attribute on thesaurus_attribute.id = knowledge_rep.attribute " +
                "        INNER JOIN (SELECT " +
                "                      thesaurus_tag_owner.id as thesaurus_tag_many_id, " +
                "                      thesaurus_tag_owner.essence as thesaurus_tag_many " +
                "                    FROM cyberbrain_knowledge_repository knowledge_rep " +
                "                      INNER JOIN cyberbrain_thesaurus thesaurus_tag on thesaurus_tag.id = knowledge_rep.tag " +
                "                      INNER JOIN cyberbrain_thesaurus thesaurus_tag_owner on thesaurus_tag_owner.id = knowledge_rep.tag_owner " +
                "                    WHERE " +
                "                      lower(thesaurus_tag.essence) = 'множество' and " +
                "                      knowledge_rep." + KnowledgeRepository.Columns.COMMUNITY + " in (:userCommunitiesIds)) as tbl on tbl.thesaurus_tag_many_id = knowledge_rep.tag " +
                "        LEFT JOIN (SELECT " +
                "                     thesaurus_tag_owner.id as thesaurus_tag_owner_id, " +
                "                     thesaurus_tag_owner.essence as thesaurus_tag_owner " +
                "                   FROM cyberbrain_knowledge_repository knowledge_rep " +
                "                     INNER JOIN cyberbrain_thesaurus thesaurus_tag on thesaurus_tag.id = knowledge_rep.tag " +
                "                     INNER JOIN cyberbrain_thesaurus thesaurus_tag_owner on thesaurus_tag_owner.id = knowledge_rep.tag_owner " +
                "                     INNER JOIN cyberbrain_thesaurus thesaurus_attribute on thesaurus_attribute.id = knowledge_rep.attribute " +
                "                   WHERE " +
                "                     lower(thesaurus_attribute.essence) = 'состояние' and " +
                "                     knowledge_rep." + KnowledgeRepository.Columns.COMMUNITY + " in (:userCommunitiesIds)) as tbl2 on  tbl2.thesaurus_tag_owner_id = thesaurus_tag_owner.id " +
                "      WHERE tbl2.thesaurus_tag_owner_id is null and " +
                "            lower(thesaurus_attribute.essence) = 'это' and " +
                "            knowledge_rep." + KnowledgeRepository.Columns.COMMUNITY + " in (:userCommunitiesIds) " +
                "      UNION ALL " +
                "      SELECT " +
                "        knowledge_rep.id as knowledge_rep_id, " +
                "        tbl.thesaurus_tag_many_id, " +
                "        tbl.thesaurus_tag_many, " +
                "        thesaurus_tag_owner.id as thesaurus_tag_owner_id, " +
                "        thesaurus_tag_owner.essence as thesaurus_tag_owner, " +
                "        thesaurus_tag.essence as custom_status, " +
                "        knowledge_rep.time_ready, " +
                "        knowledge_rep.lifecycle_status, " +
                "        thesaurus_tag_owner.frequency_essence * thesaurus_tag_owner.attention_frequency as priority, " +
                "        1 as is_track " +
                "      FROM cyberbrain_knowledge_repository knowledge_rep " +
                "        INNER JOIN cyberbrain_thesaurus thesaurus_tag on thesaurus_tag.id = knowledge_rep.tag " +
                "        INNER JOIN cyberbrain_thesaurus thesaurus_tag_owner on thesaurus_tag_owner.id = knowledge_rep.tag_owner " +
                "        INNER JOIN cyberbrain_thesaurus thesaurus_attribute on thesaurus_attribute.id = knowledge_rep.attribute " +
                "        INNER JOIN (SELECT " +
                "                      thesaurus_tag.id as thesaurus_tag_many_id, " +
                "                      thesaurus_tag.essence as thesaurus_tag_many, " +
                "                      thesaurus_tag_owner.id as thesaurus_tag_owner_id, " +
                "                      thesaurus_tag_owner.essence as thesaurus_tag_owner " +
                "                    FROM cyberbrain_knowledge_repository knowledge_rep " +
                "                      INNER JOIN cyberbrain_thesaurus thesaurus_tag on thesaurus_tag.id = knowledge_rep.tag " +
                "                      INNER JOIN cyberbrain_thesaurus thesaurus_tag_owner on thesaurus_tag_owner.id = knowledge_rep.tag_owner " +
                "                      INNER JOIN cyberbrain_thesaurus thesaurus_attribute on thesaurus_attribute.id = knowledge_rep.attribute " +
                "                    WHERE " +
                "                      lower(thesaurus_attribute.essence) = 'это' and lower(thesaurus_tag.essence) <> 'множество' and " +
                "                      knowledge_rep." + KnowledgeRepository.Columns.COMMUNITY + " in (:userCommunitiesIds)) as tbl on tbl.thesaurus_tag_owner_id = knowledge_rep.tag_owner " +
                "      WHERE " +
                "        lower(thesaurus_attribute.essence) = 'состояние' and " +
                "        knowledge_rep.show_in_questions = true and " +
                "        knowledge_rep." + KnowledgeRepository.Columns.COMMUNITY + " in (:userCommunitiesIds)) as main";

        /**
         * Сортировка списка данных (вопросы по трекам объекта)
         */
        public static final String GET_QUESTIONS_TRACKS_LIST_ORDER_BY = " ORDER BY main.priority DESC, main.thesaurus_tag_many, main.thesaurus_tag_owner, main.time_ready";

        /**
         * Запрос на получение к какому множеству принадлежит трек
         */
        public static final String GET_MANY_BY_TRACK_ID = "SELECT tbl.thesaurus_tag_many_id " +
                "FROM cyberbrain_knowledge_repository knowledge_rep " +
                "  INNER JOIN cyberbrain_thesaurus thesaurus_tag on thesaurus_tag.id = knowledge_rep.tag " +
                "  INNER JOIN cyberbrain_thesaurus thesaurus_tag_owner on thesaurus_tag_owner.id = knowledge_rep.tag_owner " +
                "  INNER JOIN cyberbrain_thesaurus thesaurus_attribute on thesaurus_attribute.id = knowledge_rep.attribute " +
                "  INNER JOIN (SELECT " +
                "                thesaurus_tag.id as thesaurus_tag_many_id, " +
                "                thesaurus_tag.essence as thesaurus_tag_many, " +
                "                thesaurus_tag_owner.id as thesaurus_tag_owner_id, " +
                "                thesaurus_tag_owner.essence as thesaurus_tag_owner " +
                "              FROM cyberbrain_knowledge_repository knowledge_rep " +
                "                INNER JOIN cyberbrain_thesaurus thesaurus_tag on thesaurus_tag.id = knowledge_rep.tag " +
                "                INNER JOIN cyberbrain_thesaurus thesaurus_tag_owner on thesaurus_tag_owner.id = knowledge_rep.tag_owner " +
                "                INNER JOIN cyberbrain_thesaurus thesaurus_attribute on thesaurus_attribute.id = knowledge_rep.attribute " +
                "              WHERE " +
                "                lower(thesaurus_attribute.essence) = 'это' and lower(thesaurus_tag.essence) <> 'множество' and " +
                "                knowledge_rep.community_id in (:userCommunitiesIds)) as tbl on tbl.thesaurus_tag_owner_id = knowledge_rep.tag_owner " +
                "WHERE " +
                "  lower(thesaurus_attribute.essence) = 'состояние' and " +
                "  knowledge_rep.id = :knowledgeId";

        /**
         * Запрос на получение трека для конкретного объекта
         */
        public static final String NEW_OBJECT_WIZARD_FORM_GET_TRACKS_LIST_BY_OBJECT_ID = "SELECT " +
                "  knowledge_rep.id as knowledge_rep_id, " +
                "  thesaurus_tag_owner.essence as thesaurus_tag_new_object, " +
                "  thesaurus_tag_from.essence as thesaurus_tag_from_name, " +
                "  thesaurus_tag_to.essence as thesaurus_tag_to_name, " +
                "  coalesce(tbl1.thesaurus_tag_many_0, '') as thesaurus_tag_many, " +
                "  coalesce(tbl1.thesaurus_tag_owner_0, '') as thesaurus_tag_owner, " +
                "  CASE WHEN tbl1.thesaurus_tag_many_0 is null THEN 0 ELSE 1 END as read_only " +
                "FROM cyberbrain_knowledge_repository knowledge_rep " +
                "  INNER JOIN cyberbrain_thesaurus thesaurus_tag_from on thesaurus_tag_from.id = knowledge_rep.tag " +
                "  INNER JOIN cyberbrain_thesaurus thesaurus_tag_owner on thesaurus_tag_owner.id = knowledge_rep.tag_owner " +
                "  INNER JOIN cyberbrain_thesaurus thesaurus_attribute on thesaurus_attribute.id = knowledge_rep.attribute " +
                "  INNER JOIN cyberbrain_knowledge_repository knowledge_rep2 on knowledge_rep2.id = knowledge_rep.next " +
                "  INNER JOIN cyberbrain_thesaurus thesaurus_tag_to on thesaurus_tag_to.id = knowledge_rep2.tag " +
                "  LEFT JOIN (SELECT " +
                "               knowledge_rep_0.id as knowledge_rep_id_0, " +
                "               knowledge_rep2_0.id as knowledge_rep2_id_0, " +
                "               thesaurus_tag_owner_0.id as thesaurus_tag_owner_id_0, " +
                "               thesaurus_tag_owner_0.essence as thesaurus_tag_owner_0, " +
                "               tbl_0.thesaurus_tag_many_0 " +
                "             FROM cyberbrain_knowledge_repository knowledge_rep_0 " +
                "               INNER JOIN cyberbrain_knowledge_repository knowledge_rep2_0 on knowledge_rep2_0.change_if = knowledge_rep_0.id " +
                "               INNER JOIN cyberbrain_thesaurus thesaurus_tag_0 on thesaurus_tag_0.id = knowledge_rep2_0.tag " +
                "               INNER JOIN cyberbrain_thesaurus thesaurus_tag_owner_0 on thesaurus_tag_owner_0.id = knowledge_rep2_0.tag_owner " +
                "               INNER JOIN cyberbrain_thesaurus thesaurus_attribute_0 on thesaurus_attribute_0.id = knowledge_rep2_0.attribute " +
                "               INNER JOIN (SELECT tag_owner, thesaurus_tag_1.essence as thesaurus_tag_many_0 " +
                "                           FROM cyberbrain_knowledge_repository knowledge_rep_1 " +
                "                             INNER JOIN cyberbrain_thesaurus thesaurus_tag_1 on thesaurus_tag_1.id = knowledge_rep_1.tag " +
                "                           WHERE thesaurus_tag_1.is_object = false) as tbl_0 ON tbl_0.tag_owner = knowledge_rep2_0.tag_owner " +
                "             where " +
                "               lower(thesaurus_attribute_0.essence) = 'состояние' and knowledge_rep_0.tag_owner = :objectId) as tbl1 ON tbl1.knowledge_rep_id_0 = knowledge_rep.id " +
                "where " +
                "  lower(thesaurus_attribute.essence) = 'состояние' and thesaurus_tag_owner.id = :objectId " +
                "order by thesaurus_tag_from.essence";
    }

    @Autowired
    private ThesaurusDao thesaurusDao;

    @Autowired
    private CommunityDao communityDao;

    @Autowired
    private SharerDao sharerDao;

    /**
     * Получить список всех сообществ в которых состоит пользователь
     * @return List<CommunityEntity>
     */
    private List<CommunityEntity> getCurrentUserCommunitiesList() {
        UserDetailsImpl userDetails = SecurityUtils.getUserDetails();
        UserEntity userEntity = sharerDao.getByEmail(userDetails.getUsername());
        return communityDao.getList(userEntity.getId(), userDetails.hasRole(Roles.ROLE_ADMIN), Arrays.asList(CommunityMemberStatus.MEMBER, CommunityMemberStatus.REQUEST_TO_LEAVE), null, 0, 20, "", null, null, null, null, true, false, "name", true);
    }

    /**
     * Получить строку с идентификаторами сообществ в которых состоит пользователь
     * @return String
     */
    private List<Long> getCurrentUserCommunitiesIds() {
        List<CommunityEntity> communitiesList = getCurrentUserCommunitiesList();
        List<Long> list = communityDao.getIdsList(communitiesList);
        list.add(-1l);
        return list;
    }

    /**
     * Получить список вопросов "что это"
     * @return List
     */
    public List getQuestionsList(Map<String, String> filters, int firstResult, int maxResults) {
        String queryString = Query.GET_QUESTIONS_LIST + Query.GET_QUESTIONS_LIST_RESTRICTION;

        if (filters != null) {
            if (filters.get("tagFilter") != null) {
                if (!filters.get("tagFilter").equals("")) {
                    queryString += " and lower(thesaurus_tag_owner." + Thesaurus.Columns.ESSENCE + ") like lower('%" + filters.get("tagFilter") + "%')";
                }
            }
        }

        queryString += Query.GET_QUESTIONS_LIST_ORDER_BY;

        SQLQuery query = createSQLQuery(queryString);

        if (filters != null) {
            if (filters.get("communityId") != null) {
                if (!filters.get("communityId").equals("")) {
                    query.setParameter("userCommunitiesIds", Long.valueOf(filters.get("communityId")));
                } else {
                    query.setParameterList("userCommunitiesIds", getCurrentUserCommunitiesIds());
                }
            } else {
                query.setParameterList("userCommunitiesIds", getCurrentUserCommunitiesIds());
            }
        } else {
            query.setParameterList("userCommunitiesIds", getCurrentUserCommunitiesIds());
        }

        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

        return query.setFirstResult(firstResult).setMaxResults(maxResults).list();
    }

    /**
     * Получить количество вопросов "что это"
     * @return BigInteger
     */
    public BigInteger getQuestionsCount(Map<String, String> filters) {
        String queryString = Query.GET_QUESTIONS_LIST + Query.GET_QUESTIONS_LIST_RESTRICTION;

        if (filters != null) {
            if (filters.get("tagFilter") != null) {
                if (!filters.get("tagFilter").equals("")) {
                    queryString += " and lower(thesaurus_tag_owner." + Thesaurus.Columns.ESSENCE + ") like lower('%" + filters.get("tagFilter") + "%')";
                }
            }
        }

        int startIndex = queryString.indexOf("FROM");
        int endIndex = queryString.length();

        SQLQuery query = createSQLQuery("SELECT COUNT(*) " + queryString.substring(startIndex, endIndex));

        if (filters != null) {
            if (filters.get("communityId") != null) {
                if (!filters.get("communityId").equals("")) {
                    query.setParameter("userCommunitiesIds", Long.valueOf(filters.get("communityId")));
                } else {
                    query.setParameterList("userCommunitiesIds", getCurrentUserCommunitiesIds());
                }
            } else {
                query.setParameterList("userCommunitiesIds", getCurrentUserCommunitiesIds());
            }
        } else {
            query.setParameterList("userCommunitiesIds", getCurrentUserCommunitiesIds());
        }

        return (BigInteger) query.uniqueResult();
    }

    /**
     * Получить список вопросов (множество)
     * @return List
     */
    public List getQuestionsManyList(Map<String, String> filters, int firstResult, int maxResults) {
        String queryString = Query.GET_QUESTIONS_LIST + Query.GET_QUESTIONS_LIST_RESTRICTION_MANY;

        if (filters != null) {
            if (filters.get("manyFilter") != null) {
                if (!filters.get("manyFilter").equals("")) {
                    queryString += " and lower(thesaurus_tag_owner." + Thesaurus.Columns.ESSENCE + ") like lower('%" + filters.get("manyFilter") + "%')";
                }
            }
        }

        queryString += Query.GET_QUESTIONS_LIST_ORDER_BY;
        SQLQuery query = createSQLQuery(queryString);

        if (filters != null) {
            if (filters.get("communityId") != null) {
                if (!filters.get("communityId").equals("")) {
                    query.setParameter("userCommunitiesIds", Long.valueOf(filters.get("communityId")));
                } else {
                    query.setParameterList("userCommunitiesIds", getCurrentUserCommunitiesIds());
                }
            } else {
                query.setParameterList("userCommunitiesIds", getCurrentUserCommunitiesIds());
            }
        } else {
            query.setParameterList("userCommunitiesIds", getCurrentUserCommunitiesIds());
        }

        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

        return query.setFirstResult(firstResult).setMaxResults(maxResults).list();
    }

    /**
     * Получить количество вопросов (множество)
     * @return BigInteger
     */
    public BigInteger getQuestionsManyCount(Map<String, String> filters) {
        String queryString = Query.GET_QUESTIONS_LIST + Query.GET_QUESTIONS_LIST_RESTRICTION_MANY;

        if (filters != null) {
            if (filters.get("manyFilter") != null) {
                if (!filters.get("manyFilter").equals("")) {
                    queryString += " and lower(thesaurus_tag_owner." + Thesaurus.Columns.ESSENCE + ") like lower('%" + filters.get("manyFilter") + "%')";
                }
            }
        }

        int startIndex = queryString.indexOf("FROM");
        int endIndex = queryString.length();

        SQLQuery query = createSQLQuery("SELECT COUNT(*) " + queryString.substring(startIndex, endIndex));

        if (filters != null) {
            if (filters.get("communityId") != null) {
                if (!filters.get("communityId").equals("")) {
                    query.setParameter("userCommunitiesIds", Long.valueOf(filters.get("communityId")));
                } else {
                    query.setParameterList("userCommunitiesIds", getCurrentUserCommunitiesIds());
                }
            } else {
                query.setParameterList("userCommunitiesIds", getCurrentUserCommunitiesIds());
            }
        } else {
            query.setParameterList("userCommunitiesIds", getCurrentUserCommunitiesIds());
        }

        return (BigInteger) query.uniqueResult();
    }

    /**
     * Получить список вопросов (свойства)
     * @return List
     */
    public List getQuestionsPropertiesList(Map<String, String> filters, int firstResult, int maxResults) {
        String queryString = Query.GET_QUESTIONS_PROPERTIES_LIST;

        String propertiesMany = "";
        String propertiesTag = "";
        String propertiesProperty = "";

        if (filters != null) {
            if (filters.get("propertiesMany") != null) {
                propertiesMany = filters.get("propertiesMany");
            }

            if (filters.get("propertiesTag") != null) {
                propertiesTag = filters.get("propertiesTag");
            }

            if (filters.get("propertiesProperty") != null) {
                propertiesProperty = filters.get("propertiesProperty");
            }
        }

        if (! propertiesMany.equals("")) {
            queryString += " and lower(tbl.thesaurus_tag_many) like lower('%" + propertiesMany + "%')";
        }

        if (! propertiesTag.equals("")) {
            queryString += " and lower(thesaurus_tag_owner." + Thesaurus.Columns.ESSENCE + ") like lower('%" + propertiesTag + "%')";
        }

        if (! propertiesProperty.equals("")) {
            queryString += " and lower(tbl.thesaurus_tag_property) like lower('%" + propertiesProperty + "%')";
        }

        queryString += Query.GET_QUESTIONS_LIST_ORDER_BY;

        SQLQuery query = createSQLQuery(queryString);

        if (filters != null) {
            if (filters.get("communityId") != null) {
                if (!filters.get("communityId").equals("")) {
                    query.setParameter("userCommunitiesIds", Long.valueOf(filters.get("communityId")));
                } else {
                    query.setParameterList("userCommunitiesIds", getCurrentUserCommunitiesIds());
                }
            } else {
                query.setParameterList("userCommunitiesIds", getCurrentUserCommunitiesIds());
            }
        } else {
            query.setParameterList("userCommunitiesIds", getCurrentUserCommunitiesIds());
        }

        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

        return query.setFirstResult(firstResult).setMaxResults(maxResults).list();
    }

    /**
     * Получить количество вопросов (множество)
     * @return BigInteger
     */
    public BigInteger getQuestionsPropertiesCount(Map<String, String> filters) {
        String queryString = Query.GET_QUESTIONS_PROPERTIES_LIST;

        String propertiesMany = "";
        String propertiesTag = "";
        String propertiesProperty = "";

        if (filters != null) {
            if (filters.get("propertiesMany") != null) {
                propertiesMany = filters.get("propertiesMany");
            }

            if (filters.get("propertiesTag") != null) {
                propertiesTag = filters.get("propertiesTag");
            }

            if (filters.get("propertiesProperty") != null) {
                propertiesProperty = filters.get("propertiesProperty");
            }
        }

        if (! propertiesMany.equals("")) {
            queryString += " and lower(tbl.thesaurus_tag_many) like lower('%" + propertiesMany + "%')";
        }

        if (! propertiesTag.equals("")) {
            queryString += " and lower(thesaurus_tag_owner." + Thesaurus.Columns.ESSENCE + ") like lower('%" + propertiesTag + "%')";
        }

        if (! propertiesProperty.equals("")) {
            queryString += " and lower(tbl.thesaurus_tag_property) like lower('%" + propertiesProperty + "%')";
        }

        int startIndex = queryString.indexOf("FROM");
        int endIndex = queryString.length();

        SQLQuery query = createSQLQuery("SELECT COUNT(*) " + queryString.substring(startIndex, endIndex));

        if (filters != null) {
            if (filters.get("communityId") != null) {
                if (!filters.get("communityId").equals("")) {
                    query.setParameter("userCommunitiesIds", Long.valueOf(filters.get("communityId")));
                } else {
                    query.setParameterList("userCommunitiesIds", getCurrentUserCommunitiesIds());
                }
            } else {
                query.setParameterList("userCommunitiesIds", getCurrentUserCommunitiesIds());
            }
        } else {
            query.setParameterList("userCommunitiesIds", getCurrentUserCommunitiesIds());
        }

        return (BigInteger) query.uniqueResult();
    }

    /**
     * Получить список вопросов (Треки объектов)
     * @return List
     */
    public List getQuestionsTracksList(Map<String, String> filters, int firstResult, int maxResults) {
        String queryString = Query.GET_QUESTIONS_TRACKS_LIST;

        String propertiesMany = "";
        String propertiesTag = "";

        if (filters != null) {
            if (filters.get("tracksMany") != null) {
                propertiesMany = filters.get("tracksMany");
            }

            if (filters.get("tracksTag") != null) {
                propertiesTag = filters.get("tracksTag");
            }
        }

        if (! propertiesMany.equals("")) {
            queryString += " where lower(thesaurus_tag_many) like lower('%" + propertiesMany + "%')";
        }

        if (! propertiesTag.equals("")) {
            if (! propertiesMany.equals("")) {
                queryString += " and lower(thesaurus_tag_owner) like lower('%" + propertiesTag + "%')";
            } else {
                queryString += " where lower(thesaurus_tag_owner) like lower('%" + propertiesTag + "%')";
            }
        }

        queryString += Query.GET_QUESTIONS_TRACKS_LIST_ORDER_BY;

        SQLQuery query = createSQLQuery(queryString);

        if (filters != null) {
            if (filters.get("communityId") != null) {
                if (!filters.get("communityId").equals("")) {
                    query.setParameter("userCommunitiesIds", Long.valueOf(filters.get("communityId")));
                } else {
                    query.setParameterList("userCommunitiesIds", getCurrentUserCommunitiesIds());
                }
            } else {
                query.setParameterList("userCommunitiesIds", getCurrentUserCommunitiesIds());
            }
        } else {
            query.setParameterList("userCommunitiesIds", getCurrentUserCommunitiesIds());
        }

        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

        return query.setFirstResult(firstResult).setMaxResults(maxResults).list();
    }

    /**
     * Получить количество вопросов (Треки объектов)
     * @return BigInteger
     */
    public BigInteger getQuestionsTracksCount(Map<String, String> filters) {
        String queryString = Query.GET_QUESTIONS_TRACKS_LIST;

        String propertiesMany = "";
        String propertiesTag = "";

        if (filters != null) {
            if (filters.get("tracksMany") != null) {
                propertiesMany = filters.get("tracksMany");
            }

            if (filters.get("tracksTag") != null) {
                propertiesTag = filters.get("tracksTag");
            }
        }

        if (! propertiesMany.equals("")) {
            queryString += " where lower(thesaurus_tag_many) like lower('%" + propertiesMany + "%')";
        }

        if (! propertiesTag.equals("")) {
            if (! propertiesMany.equals("")) {
                queryString += " and lower(thesaurus_tag_owner) like lower('%" + propertiesTag + "%')";
            } else {
                queryString += " where lower(thesaurus_tag_owner) like lower('%" + propertiesTag + "%')";
            }
        }

        int startIndex = queryString.indexOf("FROM");
        int endIndex = queryString.length();

        SQLQuery query = createSQLQuery("SELECT COUNT(*) " + queryString.substring(startIndex, endIndex));

        if (filters != null) {
            if (filters.get("communityId") != null) {
                if (!filters.get("communityId").equals("")) {
                    query.setParameter("userCommunitiesIds", Long.valueOf(filters.get("communityId")));
                } else {
                    query.setParameterList("userCommunitiesIds", getCurrentUserCommunitiesIds());
                }
            } else {
                query.setParameterList("userCommunitiesIds", getCurrentUserCommunitiesIds());
            }
        } else {
            query.setParameterList("userCommunitiesIds", getCurrentUserCommunitiesIds());
        }

        return (BigInteger) query.uniqueResult();
    }

    public List<KnowledgeRepository> getByTags(Thesaurus tagOwner, Thesaurus attribute, Thesaurus tag, CommunityEntity community) {
        Conjunction conjunction = new Conjunction();

        if (community != null) {
            conjunction.add(Restrictions.eq("community", community));
        } else {
            conjunction.add(Restrictions.isNull("community"));
        }

        if (tagOwner != null) {
            conjunction.add(Restrictions.eq("tagOwner", tagOwner));
        }

        if (attribute != null) {
            conjunction.add(Restrictions.eq("attribute", attribute));
        }

        if (tag != null) {
            conjunction.add(Restrictions.eq("tag", tag));
        }

        return find(conjunction);
    }

    public List<KnowledgeRepository> getByTags(Thesaurus tagOwner, Thesaurus attribute, Thesaurus tag) {
        Conjunction conjunction = new Conjunction();

        if (tagOwner != null) {
            conjunction.add(Restrictions.eq("tagOwner", tagOwner));
        }

        if (attribute != null) {
            conjunction.add(Restrictions.eq("attribute", attribute));
        }

        if (tag != null) {
            conjunction.add(Restrictions.eq("tag", tag));
        }

        return find(conjunction);
    }

    /**
     * Получить список влияющих объектов для трека
     */
    public List<KnowledgeRepository> getAffectList(KnowledgeRepository knowledge, CommunityEntity community) {
        Conjunction conjunction = new Conjunction();

        if (community != null) {
            conjunction.add(Restrictions.eq("community", community));
        } else {
            conjunction.add(Restrictions.isNull("community"));
        }

        if (knowledge != null) {
            conjunction.add(Restrictions.eq("changeIf", knowledge.getId()));
        } else {
            return new ArrayList<>();
        }

        return find(conjunction);
    }

    public List<KnowledgeRepository> getMyKnowledge(String filterText, int firstResult, int maxResults) {
        List<KnowledgeRepository> list;

        if (filterText.equals("")) {
            list = new ArrayList<>();
        } else {
            Conjunction conjunction = new Conjunction();

            List<CommunityEntity> communitiesList = getCurrentUserCommunitiesList();
            if (communitiesList.size() > 0) {
                conjunction.add(Restrictions.in("community", communitiesList));
            } else {
                return new ArrayList<>();
            }

            filterText = "%" + filterText + "%";

            List<Thesaurus> tagOwnerList = thesaurusDao.getByEssenceList(filterText);
            List<Thesaurus> tagAttributeList = thesaurusDao.getByEssenceList(filterText);
            List<Thesaurus> tagList = thesaurusDao.getByEssenceList(filterText);

            Disjunction disjunction = Restrictions.disjunction();

            if (tagOwnerList.size() > 0) {
                disjunction.add(Restrictions.in("tagOwner", tagOwnerList));
            }

            if (tagAttributeList.size() > 0) {
                disjunction.add(Restrictions.in("attribute", tagAttributeList));
            }

            if (tagList.size() > 0) {
                disjunction.add(Restrictions.in("tag", tagList));
            }

            if (tagOwnerList.size() > 0 || tagAttributeList.size() > 0 || tagList.size() > 0) {
                conjunction.add(disjunction);
                list = find(firstResult, maxResults, conjunction);
            } else {
                list = new ArrayList<>();
            }
        }

        return list;
    }

    public Object getMyKnowledgeCount(String filterText) {
        int count = 0;

        if (!filterText.equals("")) {
            Conjunction conjunction = new Conjunction();

            List<CommunityEntity> communitiesList = getCurrentUserCommunitiesList();
            if (communitiesList.size() > 0) {
                conjunction.add(Restrictions.in("community", communitiesList));
            } else {
                return 0;
            }

            filterText = "%" + filterText + "%";

            List<Thesaurus> tagOwnerList = thesaurusDao.getByEssenceList(filterText);
            List<Thesaurus> tagAttributeList = thesaurusDao.getByEssenceList(filterText);
            List<Thesaurus> tagList = thesaurusDao.getByEssenceList(filterText);

            Disjunction disjunction = Restrictions.disjunction();

            if (tagOwnerList.size() > 0) {
                disjunction.add(Restrictions.in("tagOwner", tagOwnerList));
            }

            if (tagAttributeList.size() > 0) {
                disjunction.add(Restrictions.in("attribute", tagAttributeList));
            }

            if (tagList.size() > 0) {
                disjunction.add(Restrictions.in("tag", tagList));
            }

            if (tagOwnerList.size() > 0 || tagAttributeList.size() > 0 || tagList.size() > 0) {
                conjunction.add(disjunction);
                count = count(conjunction);
            } else {
                count = 0;
            }
        }

        return count;
    }

    public Object getPriorityQuestion() {
        String queryString = Query.GET_PRIORITY_QUESTION;

        SQLQuery query = createSQLQuery(queryString);
        query.setParameterList("userCommunitiesIds", getCurrentUserCommunitiesIds());
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

        return query.uniqueResult();
    }

    public List<KnowledgeRepository> getObjectsToBeCopied(Thesaurus attribute, CommunityEntity community) {
        Conjunction conjunction = new Conjunction();

        if (community != null) {
            conjunction.add(Restrictions.eq("community", community));
        } else {
            conjunction.add(Restrictions.isNull("community"));
        }

        conjunction.add(Restrictions.eq("attribute", attribute));
        conjunction.add(Restrictions.eq("showInQuestions", true));

        return find(conjunction);
    }

    public List<KnowledgeRepository> getUserTaskObjects(String query, int firstResult, int maxResults, Long communityId) {
        Conjunction conjunction = new Conjunction();

        CommunityEntity community = communityDao.getById(communityId);
        if (community != null) {
            conjunction.add(Restrictions.eq("community", community));
        } else {
            conjunction.add(Restrictions.isNull("community"));
        }

        List<Thesaurus> list = thesaurusDao.getByEssenceList("%" + query + "%", community);

        if (list.size() > 0) {
            conjunction.add(Restrictions.in("tagOwner", list));
        } else {
            return new ArrayList<>();
        }

        conjunction.add(Restrictions.eq("isTopical", true));

        return find(Order.asc("tagOwner"), firstResult, maxResults, conjunction);
    }

    public JSONObject getTrackInfoById(Long knowledgeId) {
        String queryString = Query.GET_MANY_BY_TRACK_ID;

        SQLQuery query = createSQLQuery(queryString);
        query.setParameterList("userCommunitiesIds", getCurrentUserCommunitiesIds());
        query.setLong("knowledgeId", knowledgeId);
        BigInteger tagManyId = (BigInteger) query.uniqueResult();

        JSONObject jsonObject = new JSONObject();

        if (tagManyId != null) {
            KnowledgeRepository knowledge = getById(knowledgeId);
            Thesaurus tagObject = knowledge.getTagOwner();
            Thesaurus tagProperty = thesaurusDao.getByEssence(CyberbrainService.ServiceTags.PROPERTY, null);
            Thesaurus tagMany = thesaurusDao.getById(tagManyId.longValue());

            List<KnowledgeRepository> list = getByTags(tagMany, tagProperty, null, tagMany.getCommunity());
            JSONArray jsonArray = new JSONArray();

            if (list.size() == 0) {
                jsonArray.put("{\"property\":\"" + tagMany.getEssence() + "\",\"property_value\":\"У данного множества нет свойств.\"}");
            } else {
                for (KnowledgeRepository obj : list) {
                    List<KnowledgeRepository> properties = getByTags(tagObject, obj.getTag(), null, tagMany.getCommunity());

                    if (properties.size() > 0) {
                        String propertyValue = properties.get(0).getTag().getEssence();
                        if (properties.get(0).getTag().getEssence().equals(CyberbrainService.ServiceTags.MERA)) {
                            propertyValue = properties.get(0).getMera().toString();
                        }

                        jsonArray.put("{\"property\":\"" +  obj.getTag().getEssence() + "\",\"property_value\":\"" + propertyValue + "\"}");
                    } else {
                        jsonArray.put("{\"property\":\"" +  obj.getTag().getEssence() + "\",\"property_value\":\"\"}");
                    }
                }
            }

            jsonObject.put("tag_many_id", tagMany.getId());
            jsonObject.put("tag_many_name", tagMany.getEssence());
            jsonObject.put("tag_many_properties", jsonArray);
        } else {
            jsonObject.put("tag_many_id", "");
            jsonObject.put("tag_many_name", "Нет данных по множеству!");
            jsonObject.put("tag_many_properties", "");
        }

        return jsonObject;
    }

    public JSONObject getConditionsInfoById(Long knowledgeId) {
        JSONObject jsonObject = new JSONObject();

        Conjunction conjunction = new Conjunction();
        conjunction.add(Restrictions.eq("changeIf", knowledgeId));

        List<KnowledgeRepository> list = find(conjunction);

        if (list.size() > 0) {
            JSONArray jsonArray = new JSONArray();

            for (KnowledgeRepository obj : list) {
                String queryString = Query.GET_MANY_BY_TRACK_ID;

                SQLQuery query = createSQLQuery(queryString);
                query.setParameterList("userCommunitiesIds", getCurrentUserCommunitiesIds());
                query.setLong("knowledgeId", obj.getId());
                BigInteger tagManyId = (BigInteger) query.uniqueResult();

                JSONObject newJsonObject = new JSONObject();

                if (tagManyId != null) {
                    Thesaurus tagMany = thesaurusDao.getById(tagManyId.longValue());
                    newJsonObject.put("tag_many_name", tagMany.getEssence());
                } else {
                    newJsonObject.put("tag_many_name", "Нет данных по множеству!");
                }

                newJsonObject.put("tag_object_name", obj.getTagOwner().getEssence());
                newJsonObject.put("custom_status_name", obj.getTag().getEssence());

                if (obj.getIsTopical() == null) {
                    newJsonObject.put("is_topical", "null");
                } else {
                    newJsonObject.put("is_topical", "");
                }

                jsonArray.put(newJsonObject);
            }

            jsonObject.put("conditions", jsonArray);
        } else {
            jsonObject.put("conditions", "");
        }

        return jsonObject;
    }

    public List newObjectWizardFormQuestionsTracksList(Map<String, String> filters, int firstResult, int maxResults) {
        String queryString = Query.GET_QUESTIONS_TRACKS_LIST;

        if (filters != null) {
            if (filters.get("tagOwnerId") != null && !filters.get("tagOwnerId").equals("")) {
                queryString += " where thesaurus_tag_owner_id = " + filters.get("tagOwnerId");
            } else {
                queryString += " where thesaurus_tag_owner_id = -1";
            }
        }

        queryString += Query.GET_QUESTIONS_TRACKS_LIST_ORDER_BY;

        SQLQuery query = createSQLQuery(queryString);
        query.setParameterList("userCommunitiesIds", getCurrentUserCommunitiesIds());
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

        return query.setFirstResult(firstResult).setMaxResults(maxResults).list();
    }

    public BigInteger newObjectWizardFormQuestionsTracksCount(Map<String, String> filters) {
        String queryString = Query.GET_QUESTIONS_TRACKS_LIST;

        if (filters != null) {
            if (filters.get("tagOwnerId") != null && !filters.get("tagOwnerId").equals("")) {
                queryString += " where thesaurus_tag_owner_id = " + filters.get("tagOwnerId");
            } else {
                return BigInteger.valueOf(0);
            }
        }

        int startIndex = queryString.indexOf("FROM");
        int endIndex = queryString.length();

        SQLQuery query = createSQLQuery("SELECT COUNT(*) " + queryString.substring(startIndex, endIndex));
        query.setParameterList("userCommunitiesIds", getCurrentUserCommunitiesIds());

        return (BigInteger) query.uniqueResult();
    }

    public List newObjectWizardFormQuestionsPropertiesList(Map<String, String> filters, int firstResult, int maxResults) {
        String queryString = Query.GET_QUESTIONS_PROPERTIES_LIST;

        if (filters != null) {
            if (filters.get("tagOwnerId") != null && !filters.get("tagOwnerId").equals("")) {
                queryString += " and thesaurus_tag_owner.id = " + filters.get("tagOwnerId");
            } else {
                queryString += " and thesaurus_tag_owner.id = -1";
            }
        }

        queryString += Query.GET_QUESTIONS_LIST_ORDER_BY;

        SQLQuery query = createSQLQuery(queryString);
        query.setParameterList("userCommunitiesIds", getCurrentUserCommunitiesIds());
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

        return query.setFirstResult(firstResult).setMaxResults(maxResults).list();
    }

    public BigInteger newObjectWizardFormQuestionsPropertiesCount(Map<String, String> filters) {
        String queryString = Query.GET_QUESTIONS_PROPERTIES_LIST;

        if (filters != null) {
            if (filters.get("tagOwnerId") != null && !filters.get("tagOwnerId").equals("")) {
                queryString += " and thesaurus_tag_owner.id = " + filters.get("tagOwnerId");
            } else {
                return BigInteger.valueOf(0);
            }
        }

        int startIndex = queryString.indexOf("FROM");
        int endIndex = queryString.length();

        SQLQuery query = createSQLQuery("SELECT COUNT(*) " + queryString.substring(startIndex, endIndex));
        query.setParameterList("userCommunitiesIds", getCurrentUserCommunitiesIds());

        return (BigInteger) query.uniqueResult();
    }

    /**
     *  Получить список множеств
     */
    public List<KnowledgeRepository> getManyList(HashMap<String, String> filters, int firstResult, int maxResults) {
        Conjunction conjunction = new Conjunction();
        conjunction.add(Restrictions.eq("isObject", false));

        if (filters != null) {
            if (filters.get("filterMany") != null && !filters.get("filterMany").equals("")) {
                conjunction.add(Restrictions.ilike("essence", "%" + filters.get("filterMany") + "%"));
            }

            if (filters.get("communityId") != null && !filters.get("communityId").equals("")) {
                CommunityEntity community = communityDao.getById(Long.valueOf(filters.get("communityId")));
                conjunction.add(Restrictions.eq("community", community));
            }
        }

        List<Thesaurus> list = thesaurusDao.find(firstResult, maxResults, conjunction);
        Thesaurus serviceTagThis =   thesaurusDao.getByEssence("Это", null);
        Thesaurus serviceTagMany = thesaurusDao.getByEssence("Множество", null);

        if (list.size() > 0) {
            conjunction = new Conjunction();
            conjunction.add(Restrictions.in("tagOwner", list));
            conjunction.add(Restrictions.eq("attribute", serviceTagThis));
            conjunction.add(Restrictions.eq("tag", serviceTagMany));
            return find(firstResult, maxResults, conjunction);
        }

        return new ArrayList<>();
    }

    public int getManyCount(HashMap<String, String> filters) {
        Conjunction conjunction = new Conjunction();
        conjunction.add(Restrictions.eq("isObject", false));

        if (filters != null) {
            if (filters.get("filterMany") != null && !filters.get("filterMany").equals("")) {
                conjunction.add(Restrictions.ilike("essence", "%" + filters.get("filterMany") + "%"));
            }

            if (filters.get("communityId") != null && !filters.get("communityId").equals("")) {
                CommunityEntity community = communityDao.getById(Long.valueOf(filters.get("communityId")));
                conjunction.add(Restrictions.eq("community", community));
            }
        }

        List<Thesaurus> list = thesaurusDao.find(conjunction);
        Thesaurus serviceTagThis =   thesaurusDao.getByEssence("Это", null);
        Thesaurus serviceTagMany = thesaurusDao.getByEssence("Множество", null);

        if (list.size() > 0) {
            conjunction = new Conjunction();
            conjunction.add(Restrictions.in("tagOwner", list));
            conjunction.add(Restrictions.eq("attribute", serviceTagThis));
            conjunction.add(Restrictions.eq("tag", serviceTagMany));
            return count(conjunction);
        }

        return 0;
    }

    public List newObjectWizardFormGetTracksListByObjectId(Long objectId) {
        String queryString = Query.NEW_OBJECT_WIZARD_FORM_GET_TRACKS_LIST_BY_OBJECT_ID;

        SQLQuery query = createSQLQuery(queryString);
        query.setParameter("objectId", objectId);
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

        return query.list();
    }
}