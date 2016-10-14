package ru.radom.kabinet.dao;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.*;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.DateType;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import padeg.lib.Padeg;
import ru.askor.blagosfera.data.jpa.repositories.UserRepository;
import ru.askor.blagosfera.domain.Address;
import ru.radom.kabinet.dao.fields.FieldDao;
import ru.radom.kabinet.dao.fields.FieldValueDao;
import ru.radom.kabinet.dto.VerifiedSharerDto;
import ru.radom.kabinet.model.Discriminators;
import ru.askor.blagosfera.domain.user.SharerStatus;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.fields.FieldEntity;
import ru.radom.kabinet.model.fields.FieldValueEntity;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.utils.DateUtils;
import ru.radom.kabinet.utils.FieldConstants;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@Deprecated
@Repository("sharerDao")
public class SharerDao extends Dao<UserEntity> {
	private static final Logger logger = LoggerFactory.createLogger(SharerDao.class);
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private FieldDao fieldDao;

	@Autowired
	private FieldValueDao fieldValueDao;

	// Поля адреса фактического проживания
	private static final Map<String, String> FACT_ADDRESS_FIELDS = new HashMap<String, String>(){{
		put("country_field_name", FieldConstants.FACT_COUNTRY_SHARER);
		put("region_field_name", FieldConstants.FACT_REGION_SHARER);
		put("district_field_name", FieldConstants.FACT_DISTRICT_SHARER);
		put("city_field_name", FieldConstants.FACT_CITY_SHARER);
		put("street_field_name", FieldConstants.FACT_STREET_SHARER);
		put("building_field_name", FieldConstants.FACT_BUILDING_SHARER);
		put("position_field_name", FieldConstants.FACT_GEO_POSITION_SHARER);
		put("location_field_name", FieldConstants.FACT_GEO_LOCATION_SHARER);
		put("room_field_name", FieldConstants.FACT_ROOM_SHARER);
	}};

	// Поля регистрации участника
	private static final Map<String, String> REGISTRATION_ADDRESS_FIELDS = new HashMap<String, String>(){{
		put("country_field_name", FieldConstants.REGISTRATION_COUNTRY_SHARER);
		put("region_field_name", FieldConstants.REGISTRATION_REGION_SHARER);
		put("district_field_name", FieldConstants.REGISTRATION_DISTRICT_SHARER);
		put("city_field_name", FieldConstants.REGISTRATION_CITY_SHARER);
		put("street_field_name", FieldConstants.REGISTRATION_STREET_SHARER);
		put("building_field_name", FieldConstants.REGISTRATION_BUILDING_SHARER);
		put("position_field_name", FieldConstants.REGISTRATION_GEO_POSITION_SHARER);
		put("location_field_name", FieldConstants.REGISTRATION_GEO_LOCATION_SHARER);
		put("room_field_name", FieldConstants.REGISTRATION_ROOM_SHARER);
	}};

	// Поля офиса регистратора
	private static final Map<String, String> REGISTRATOR_OFFICE_FIELDS = new HashMap<String, String>(){{
		put("country_field_name", FieldConstants.REGISTRATOR_OFFICE_COUNTRY);
		put("region_field_name", FieldConstants.REGISTRATOR_OFFICE_REGION);
		put("district_field_name", FieldConstants.REGISTRATOR_OFFICE_DISTRICT);
		put("city_field_name", FieldConstants.REGISTRATOR_OFFICE_CITY);
		put("street_field_name", FieldConstants.REGISTRATOR_OFFICE_STREET);
		put("building_field_name", FieldConstants.REGISTRATOR_OFFICE_BUILDING);
		put("position_field_name", FieldConstants.REGISTRATOR_OFFICE_GEO_POSITION);
		put("location_field_name", FieldConstants.REGISTRATOR_OFFICE_GEO_LOCATION);
		put("room_field_name", FieldConstants.REGISTRATOR_OFFICE_ROOM);
	}};

	public UserEntity getByEmail(String email, Boolean deleted) {
		if (deleted == null) {
			return findFirst(Restrictions.eq("email", email));
		} else {
			return findFirst(Restrictions.eq("email", email), Restrictions.eq("deleted", deleted));
		}

	}

	public UserEntity getByEmail(String email) {
		return getByEmail(email, false);
	}

	public List<UserEntity> getByEmails(List<String> emails, Boolean deleted) {
		if (deleted == null) {
			return find(Restrictions.in("email", emails));
		} else {
			return find(Restrictions.in("email", emails), Restrictions.eq("deleted", deleted));
		}
	}

	public List<UserEntity> getByEmails(List<String> emails) {
		return getByEmails(emails, false);
	}

	public UserEntity getByIkp(String ikp, Boolean deleted) {
		if (deleted == null) {
			return findFirst(Restrictions.eq("ikp", ikp));
		} else {
			return findFirst(Restrictions.eq("ikp", ikp), Restrictions.eq("deleted", deleted));
		}
	}

	public UserEntity getByIkp(String ikp) {
		return getByIkp(ikp, false);
	}

	public UserEntity getByShortLink(String shortLink) {
		FieldEntity field = fieldDao.getByInternalName("SHARER_SHORT_LINK_NAME");
		List<FieldValueEntity> fieldValues = fieldValueDao.getList(field, shortLink);
		List<Long> ids = new ArrayList<>();
		for (FieldValueEntity fieldValue : fieldValues) {
			ids.add(fieldValue.getObject().getId());
		}

		UserEntity userEntity = null;
		if(ids.size() > 0) {
			// Важно чтобы совпадал айдишник и участник не был удалён,
			// т.к. в остальном результат должен быть уникальным.
			userEntity = findFirst(Restrictions.in("id", ids),Restrictions.eq("deleted", false));
		}
		return userEntity;
	}

	public UserEntity getByIkpOrShortLink(String ikpOrShortLink) {
		UserEntity userEntity = null;
		userEntity = getByShortLink(ikpOrShortLink);
		if (userEntity == null) {
			userEntity = getByIkp(ikpOrShortLink);
		}
		return userEntity;
	}

	public void setAllOffline() {
		getCurrentSession().createSQLQuery("update sharers set online = false").executeUpdate();
	}

	public boolean existsEmail(String email) {
		return (boolean) createSQLQuery("SELECT EXISTS(SELECT id FROM sharers WHERE email = :email)").setString("email", email.toLowerCase().trim()).uniqueResult();
	}

	public boolean existsEmail(String email, UserEntity userEntity) {
		return (boolean) createSQLQuery("SELECT EXISTS(SELECT id FROM sharers WHERE email = :email and id <> :id)").setString("email", email.toLowerCase().trim()).setLong("id", userEntity.getId()).uniqueResult();
	}

	public UserEntity getByPasswordRecoveryCode(String code) {
		return findFirst(Restrictions.eq("passwordRecoveryCode", code), Restrictions.eq("deleted", false));
	}

	public List<UserEntity> getByOverdueActivation(int daysForActivation) {
		return find(Restrictions.eq("status", SharerStatus.WAIT), Restrictions.eq("deleted", false), Restrictions.lt("activateCodeAt", DateUtils.add(new Date(), Calendar.DAY_OF_YEAR, -daysForActivation)));
	}

	public List<UserEntity> getByNotFilledProfiles() {
		return find(Restrictions.isNotNull("profileUnfilledAt"), Restrictions.eq("deleted", false));
	}

	public List<UserEntity> getByNotFilledProfilesByPage(int page, int countInPage) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("deleted", false));
		criteria.add(Restrictions.isNotNull("profileUnfilledAt"));
		criteria.setFirstResult(page * countInPage);
		criteria.setMaxResults(countInPage);
		return find(criteria);
	}

	public UserEntity getBySearchString(String name, Boolean deleted) {
		return deleted != null ? findFirst(Restrictions.eq("deleted", deleted), Restrictions.ilike("searchString", name, MatchMode.ANYWHERE)) : findFirst(Restrictions.ilike("searchString", name, MatchMode.ANYWHERE));
	}

	public UserEntity getBySearchString(String name) {
		return getBySearchString(name, false);
	}

	public UserEntity getByActivationCode(String code) {
		return findFirst(Restrictions.eq("status", SharerStatus.WAIT), Restrictions.eq("activateCode", code), Restrictions.eq("deleted", false));
	}

	public List<UserEntity> search(String query, int firstResult, int maxResults, boolean includeContextSharer, List<String> ikpList, Boolean ikpOnly) {
		Conjunction conjunction = new Conjunction();
		conjunction.add(Restrictions.ilike("searchString", query, MatchMode.ANYWHERE));
		if (!includeContextSharer) {
			conjunction.add(Restrictions.ne("id", SecurityUtils.getUser().getId()));
		}
		if (ikpList != null) {
			if (ikpList.size() == 0) {
				if (ikpOnly) {
					return new ArrayList<UserEntity>();
				}
			} else {
				Criterion restriction = Restrictions.in("ikp", ikpList);
				if (ikpOnly) {
					conjunction.add(restriction);
				} else {
					conjunction.add(Restrictions.not(restriction));
				}
			}
		}
		return find(Order.asc("searchString"), firstResult, maxResults, conjunction);
	}

    public List<UserEntity> search(String query, int firstResult, int maxResults, boolean includeContextSharer, String orderBy, boolean asc,Long ageFrom,Long ageTo,Boolean sex,String country,String city) {
        return search(query, firstResult, maxResults, includeContextSharer, orderBy, asc, false, false,ageFrom,ageTo,sex,country,city);
    }

	public List<UserEntity> search(String query, int firstResult, int maxResults, boolean includeContextSharer, String orderBy, boolean asc,
								   boolean waitingForCertification, boolean requestedForRegistrationsOnlyToMe,Long ageFrom,Long ageTo,Boolean sex,String country,String city) {
		Conjunction conditions = Restrictions.conjunction();

        Junction tokensConditions = Restrictions.disjunction();
        StringTokenizer queryTokens = new StringTokenizer(query, ",", false);
        while (queryTokens.hasMoreTokens()) {
            String token = queryTokens.nextToken();

            if (token.length() < 2) continue;

            Junction subconditions = Restrictions.conjunction();
            StringTokenizer subtokens = new StringTokenizer(token);

            while (subtokens.hasMoreTokens()) {
                String subtoken = subtokens.nextToken();
                if (subtoken.length() < 2) continue;

                if (subtoken.startsWith("\"") && subtoken.endsWith("\"")) {
                    subtoken = subtoken.replaceAll("\"%", "");
                    subtoken = subtoken.replaceAll("%\"", "");
                    subtoken = subtoken.replaceAll("\"", " ");
                } else {
                    //subtoken = " " + subtoken + " ";
                }

                subconditions.add(Restrictions.ilike("searchString", subtoken, MatchMode.ANYWHERE));
            }

            tokensConditions.add(subconditions);
        }

        conditions.add(tokensConditions);
        conditions.add(Restrictions.eq("deleted", false));

        if (!includeContextSharer) {
			conditions.add(Restrictions.ne("id", SecurityUtils.getUser().getId()));
		}

        Order order = asc ? Order.asc(orderBy) : Order.desc(orderBy);

        Criteria criteria = getCriteria();

        for (Criterion criterion : conditions.conditions()) {
			criteria.add(criterion);
		}

        if (waitingForCertification) {
			Long[] userIds = null;
			if (requestedForRegistrationsOnlyToMe) {
				userIds = userRepository.getUsersIdsWithRegistrationRequestsToRegistrator(SecurityUtils.getUser().getId());
			}
			else {
				userIds =  userRepository.getUsersIdsWithRegistrationRequests();
			}
			if (userIds.length > 0) {
				criteria.add(Restrictions.in("id", userIds));
			}
			else {
				criteria.add(Restrictions.in("id", new Long[]{-1l}));
			}
        }

		if (order != null) {
			criteria.addOrder(order);
		}
		if (!ru.radom.kabinet.utils.StringUtils.isEmpty(country)) {
			criteria.createAlias("fieldValuesCountry", "fieldsAlias", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("fieldsAlias.field", "fieldAlias", JoinType.INNER_JOIN);
			criteria.add(Restrictions.or(Restrictions.and(Restrictions.eq("fieldAlias.internalName",FieldConstants.FACT_COUNTRY_SHARER),
					Restrictions.eq("fieldsAlias.stringValue",country)),
					Restrictions.and(Restrictions.eq("fieldAlias.internalName",FieldConstants.REGISTRATION_COUNTRY_SHARER),
							Restrictions.eq("fieldsAlias.stringValue",country)),
					Restrictions.and(Restrictions.eq("fieldAlias.internalName",FieldConstants.REGISTRATOR_OFFICE_COUNTRY),
							Restrictions.eq("fieldsAlias.stringValue",country))));
		}
		if (!ru.radom.kabinet.utils.StringUtils.isEmpty(city)) {
			criteria.createAlias("fieldValuesCityAndRegion", "fieldsAliasCity", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("fieldsAliasCity.field", "fieldAliasCity", JoinType.INNER_JOIN);
			criteria.add(Restrictions.or(Restrictions.and(Restrictions.eq("fieldAliasCity.internalName",FieldConstants.REGISTRATOR_OFFICE_CITY),
							Restrictions.eq("fieldsAliasCity.stringValue",city)),
					Restrictions.and(Restrictions.eq("fieldAliasCity.internalName",FieldConstants.FACT_CITY_SHARER),
							Restrictions.eq("fieldsAliasCity.stringValue",city)),
					Restrictions.and(Restrictions.eq("fieldAliasCity.internalName",FieldConstants.REGISTRATION_CITY_SHARER),
							Restrictions.eq("fieldsAliasCity.stringValue",city)), Restrictions.and(Restrictions.eq("fieldAliasCity.internalName",FieldConstants.REGISTRATOR_OFFICE_REGION),
							Restrictions.eq("fieldsAliasCity.stringValue",city)),Restrictions.and(Restrictions.eq("fieldAliasCity.internalName",FieldConstants.FACT_REGION_SHARER),
							Restrictions.eq("fieldsAliasCity.stringValue",city)),Restrictions.and(Restrictions.eq("fieldAliasCity.internalName",FieldConstants.REGISTRATION_REGION_SHARER),
							Restrictions.eq("fieldsAliasCity.stringValue",city))));
		}
		if ((ageFrom != null) || (ageTo != null)){
			++ageTo;
			Criteria birthdateCriteria = criteria.createCriteria("fieldValuesBirthday", "fieldsAliasBirthday", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("fieldsAliasBirthday.field", "fieldAliasBirthday", JoinType.INNER_JOIN);
			if (ageFrom != null) {
				Date dateFrom = DateUtils.add(new Date(), Calendar.YEAR, -ageFrom.intValue());
				birthdateCriteria.add(Restrictions.and(Restrictions.eq("fieldAliasBirthday.internalName", FieldConstants.SHARER_BIRTHDAY),
						Restrictions.sqlRestriction("to_timestamp_safe({alias}.string_value,'dd.mm.yyyy') <= ?", dateFrom, StandardBasicTypes.DATE)));
			}
			if (ageTo != null) {
				Date dateTo = DateUtils.add(new Date(), Calendar.YEAR, -ageTo.intValue());
				birthdateCriteria.add(Restrictions.and(Restrictions.eq("fieldAliasBirthday.internalName", FieldConstants.SHARER_BIRTHDAY),
						Restrictions.sqlRestriction("to_timestamp_safe({alias}.string_value,'dd.mm.yyyy') >= ?", dateTo, StandardBasicTypes.DATE)));
			}
		}
		if (sex != null) {
			String gender = sex ? UserEntity.MALE_GENDER_STRING_VALUE : UserEntity.FEMALE_GENDER_STRING_VALUE;
			criteria.createAlias("fieldValuesGender", "fieldsAliasGender", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("fieldsAliasGender.field", "fieldAliasGender", JoinType.INNER_JOIN);
			criteria.add(Restrictions.or(Restrictions.and(Restrictions.eq("fieldAliasGender.internalName",FieldConstants.SHARER_GENDER),
							Restrictions.eq("fieldsAliasGender.stringValue",gender))
					));
		}
		criteria.setFirstResult(firstResult);
		criteria.setMaxResults(maxResults);

		List<UserEntity> userEntities = find(criteria);

        for (UserEntity userEntity : userEntities) {
			update(userEntity);
		}

        return userEntities;
	}

	public List<UserEntity> search(String query, int firstResult, int maxResults) {
		return search(query, firstResult, maxResults, false, "searchString", true, null, null, null, null, null);
	}

	public List<UserEntity> searchVerified(String query, int firstResult, int maxResults, boolean includeContextSharer, String orderBy, boolean asc) {
		Conjunction conjunction = new Conjunction();
		conjunction.add(Restrictions.ilike("searchString", query, MatchMode.ANYWHERE));
		conjunction.add(Restrictions.eq("deleted", false));
		conjunction.add(Restrictions.eq("verified", true));
		if (!includeContextSharer) {
			conjunction.add(Restrictions.ne("id", SecurityUtils.getUser().getId()));
		}
		List<UserEntity> userEntities = find(asc ? Order.asc(orderBy) : Order.desc(orderBy), firstResult, maxResults, conjunction);
		for (UserEntity userEntity : userEntities) {
			update(userEntity);
		}
		return userEntities;
	}

	/**
	 * Найти сертифицированныз пользователей системы.
	 * @param query - строка поиска в имени
	 * @param firstResult - индекс с которого начать поиск
	 * @param maxResults - максимальное количество
	 * @param excludeIds - ИД которые нужно иключить
	 * @return список пользователей
	 */
	public List<UserEntity> searchVerified(String query, int firstResult, int maxResults, List<Long> excludeIds) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.ilike("searchString", query, MatchMode.ANYWHERE));
		criteria.add(Restrictions.eq("deleted", false));
		criteria.add(Restrictions.eq("verified", true));
		if (excludeIds != null && excludeIds.size() > 0) {
			criteria.add(Restrictions.not(Restrictions.in("id", excludeIds)));
		}
		criteria.setFirstResult(firstResult);
		criteria.setMaxResults(maxResults);
		criteria.addOrder(Order.asc("searchString"));
		return find(criteria);
	}

	/**
	 * Найти пользователей системы.
	 * @param query - строка поиска в имени
	 * @param firstResult - индекс с которого начать поиск
	 * @param maxResults - максимальное количество
	 * @param excludeIds - ИД которые нужно иключить
	 * @return список пользователей
	 */
	public List<UserEntity> searchActive(String query, int firstResult, int maxResults, List<Long> excludeIds) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.ilike("searchString", query, MatchMode.ANYWHERE));
		criteria.add(Restrictions.eq("deleted", false));
		if (excludeIds != null && excludeIds.size() > 0) {
			criteria.add(Restrictions.not(Restrictions.in("id", excludeIds)));
		}
		criteria.setFirstResult(firstResult);
		criteria.setMaxResults(maxResults);
		criteria.addOrder(Order.asc("searchString"));
		return find(criteria);
	}

	public int getSearchCount(String query, boolean includeContextSharer){
		Conjunction conjunction = new Conjunction();
		conjunction.add(Restrictions.ilike("searchString", query, MatchMode.ANYWHERE));
		conjunction.add(Restrictions.eq("deleted", false));
		if (!includeContextSharer) {
			conjunction.add(Restrictions.ne("id", SecurityUtils.getUser().getId()));
		}
		return count(conjunction);
	}

	public void setOtherOffline(Set<Long> excludeIds) {
		if (excludeIds.size() > 0) {
			createSQLQuery("update sharers set online = false where id not in (:ids)").setParameterList("ids", excludeIds).executeUpdate();
		} else {
			setAllOffline();
		}
	}

	public int getTotalCount() {
		return count(Restrictions.eq("deleted", false));
	}

	public UserEntity getById(long id, boolean b) {
		UserEntity userEntity = getById(id);
		return !userEntity.isDeleted() ? userEntity : null;
	}

	public List<UserEntity> getNotDeleted() {
		return find(Restrictions.eq("deleted", false));
	}

	public List<UserEntity> getNotDeletedByPage(int page, int countInPage) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("deleted", false));
		criteria.setFirstResult(page * countInPage);
		criteria.setMaxResults(countInPage);
		criteria.addOrder(Order.asc("id"));
		return find(criteria);
	}


	public int getCountNotDeletedByGenderAndPage(boolean sex) {
		String genderStringValue = sex ? UserEntity.MALE_GENDER_STRING_VALUE : UserEntity.FEMALE_GENDER_STRING_VALUE;
		String query =
				"select count(sh.id) as cnt from sharers sh \n" +
						"	join field_values fv on sh.id = fv.object_id \n" +
						"	join fields f on f.id = fv.field_id \n" +
						"where \n" +
						"	f.internal_name = :gender_field_name and \n" +
						"	fv.string_value = :gender_field_value and \n" +
						"	sh.deleted = false";
		SQLQuery sqlQuery = createSQLQuery(query);

		sqlQuery.setString("gender_field_name", FieldConstants.SHARER_GENDER);
		sqlQuery.setString("gender_field_value", genderStringValue);

		sqlQuery.setResultTransformer(new ResultTransformer() {
			@Override
			public Object transformTuple(Object[] objects, String[] strings) {
				return ((BigInteger)objects[0]).intValue();
			}

			@Override
			public List transformList(List list) {
				return list;
			}
		});
		List<Integer> result = sqlQuery.list();
		return result.get(0);
	}


	public List<UserEntity> getNotDeletedByGenderAndPage(int page, int countInPage, boolean sex) {
		String genderStringValue = sex ? UserEntity.MALE_GENDER_STRING_VALUE : UserEntity.FEMALE_GENDER_STRING_VALUE;
		String query =
				"select sh.id as id from sharers sh \n" +
				"	join field_values fv on sh.id = fv.object_id \n" +
				"	join fields f on f.id = fv.field_id \n" +
				"where \n" +
				"	f.internal_name = :gender_field_name and \n" +
				"	fv.string_value = :gender_field_value and \n" +
				"	sh.deleted = false order by sh.id asc";
		SQLQuery sqlQuery = createSQLQuery(query);

		sqlQuery.setString("gender_field_name", FieldConstants.SHARER_GENDER);
		sqlQuery.setString("gender_field_value", genderStringValue);

		sqlQuery.setFirstResult(page * countInPage);
		sqlQuery.setMaxResults(countInPage);
		sqlQuery.setResultTransformer(new ResultTransformer() {
			@Override
			public Object transformTuple(Object[] objects, String[] strings) {
				return ((BigInteger)objects[0]).longValue();
			}

			@Override
			public List transformList(List list) {
				return list;
			}
		});
		List<Long> idList = sqlQuery.list();

		Criteria criteria = getCriteria();
		criteria.add(Restrictions.in("id", idList));
		criteria.addOrder(Order.asc("id"));
		return find(criteria);
	}

	public int getCountNotDeletedManByPage() {
		return getCountNotDeletedByGenderAndPage(true);
	}

	public int getCountNotDeletedWomenByPage() {
		return getCountNotDeletedByGenderAndPage(false);
	}

	public List<UserEntity> getNotDeletedManByPage(int page, int countInPage) {
		return getNotDeletedByGenderAndPage(page, countInPage, true);
	}

	public List<UserEntity> getNotDeletedWomenByPage(int page, int countInPage) {
		return getNotDeletedByGenderAndPage(page, countInPage, false);
	}

	public List<UserEntity> getAllVerified() {
		Criteria criteria = getCriteria().add(Restrictions.eq("deleted", false)).add(Restrictions.eq("verified", true));
		return find(criteria);
	}

	private static final String VERIFIED_SHARERS_BY_SHARER_COUNT_SQL = "SELECT COUNT(*)\n" +
			"FROM sharers S\n" +
			"WHERE S.verifier_id = :sharer_id and S.deleted=false AND S.verified=true;";

	/**
	 *Возвращает количество сертифицированных sharer'ом пользователей
	 * @return
	 */
	public Long getVerifiedSharersCount(Long userId) {
		Query query = getCurrentSession().createSQLQuery(VERIFIED_SHARERS_BY_SHARER_COUNT_SQL).setLong("sharer_id", userId);
		return ((BigInteger)query.uniqueResult()).longValue();
	}

	private VerifiedSharerDto createVerifiedSharerDtoFromRow(Object row) {
		Object[] r = (Object[]) row;

		Long id = ((BigInteger) r[0]).longValue();

		String email = (String) r[1];

		String firstName = (String) r[2];
		String lastName = (String) r[3];
		String secondName = (String) r[4];
		String fullName = (lastName != null ? lastName + " " : "") + (firstName != null ? firstName + " " : "") + (secondName != null ? secondName : "");

		Long inviterId = (r[6] != null ? ((BigInteger)r[6]).longValue() : null);

		Date registrationDate = (Date) r[10];
		String registrationDateString = (DateUtils.dateToString(registrationDate, DateUtils.Format.DATE_TIME_SHORT));


		Date verificationDate = (Date) r[5];
		String verificationDateString = (DateUtils.dateToString(verificationDate, DateUtils.Format.DATE_TIME_SHORT));

		String verificationType = "Полная идентификация";

		Integer memberWithOrganizationCount = (r[11] != null ? ((BigInteger)r[11]).intValue() : 0);
		Integer memberWithoutOrganizationCount = (r[12] != null ? ((BigInteger)r[12]).intValue() : 0);

		Integer creatorWithOrganizationCount = (r[13] != null ? ((BigInteger)r[13]).intValue() : 0);
		Integer creatorWithoutOrganizationCount = (r[14] != null ? ((BigInteger)r[14]).intValue() : 0);

		String avatarUrlSrc = (String) r[15];

		String gender = (String) r[16];
		boolean sex = StringUtils.equals(gender, "Мужской");

		String inviterFirstName = (String) r[7];
		String inviterLastName = (String) r[8];
		String inviterSecondName = (String) r[9];
		String inviterFullName = Padeg.getFIOPadeg(inviterLastName, inviterFirstName, inviterSecondName, sex, 5);

		VerifiedSharerDto verifiedSharerDto = new VerifiedSharerDto();

		verifiedSharerDto.setId(id);
		verifiedSharerDto.setAvatarUrlSrc(avatarUrlSrc);
		verifiedSharerDto.setEmail(email);
		verifiedSharerDto.setName(fullName);
		verifiedSharerDto.setInviterId(inviterId);
		verifiedSharerDto.setRegistrationDate(registrationDateString);
		verifiedSharerDto.setInviterName(inviterFullName);
		verifiedSharerDto.setVerificationDate(verificationDateString);
		verifiedSharerDto.setVerificationType(verificationType);
		verifiedSharerDto.setMemberWithOrganizationCount(memberWithOrganizationCount);
		verifiedSharerDto.setMemberWithoutOrganizationCount(memberWithoutOrganizationCount);
		verifiedSharerDto.setCreatorWithOrganizationCount(creatorWithOrganizationCount);
		verifiedSharerDto.setCreatorWithoutOrganizationCount(creatorWithoutOrganizationCount);

		return verifiedSharerDto;
	}

	private static final String SELECT_VERIFIED_SHARERS_FOR_SHARER_SQL = "SELECT \n" +
			"S.id, \n" +
			"S.email,\n" +
			"SFN.string_value AS sharer_first_name, SLN.string_value AS sharer_last_name, SSN.string_value AS sharer_second_name,\n" +
			"S.registered_at, \n" +
			"I.sharer_id AS inviter_id,\n" +
			"IFN.string_value AS inviter_first_name, ILN.string_value AS inviter_last_name,ISN.string_value AS inviter_second_name,\n" +
			"S.verification_date,\n" +
			"M1.count AS member_with_organization_count, M2.count AS member_without_organization_count,\n" +
			"C1.count AS creator_with_organization_count, C2.count AS creator_without_organization_count,\n" +
			"S.avatar_src, G.string_value AS gender\n" +
			"FROM sharers S\n" +
			"LEFT JOIN invites I ON S.id=I.invited_sharer_id\n" +
			"LEFT JOIN (\n" +
			"\t--(sharer_id,count) состоит в объединенях в рамках юр лиц(кол-во)\n" +
			"\tSELECT CM.sharer_id, COUNT(*)\n" +
			"\tFROM community_members CM\n" +
			"\tINNER JOIN communities C ON CM.community_id=C.id\n" +
			"\tINNER JOIN field_values FV ON FV.object_id=C.id\n" +
			"\tINNER JOIN fields F ON FV.field_id=F.id\n" +
			"\tWHERE C.deleted=false AND FV.object_type='COMMUNITY' AND F.internal_name='COMMUNITY_TYPE' AND FV.string_value='COMMUNITY_WITH_ORGANIZATION'\n" +
			"\tGROUP BY CM.sharer_id\n" +
			") AS M1 ON M1.sharer_id=S.id\n" +
			"LEFT JOIN (\n" +
			"\t--(sharer_id,count) состоит в объединенях вне рамках юр лиц(кол-во)\n" +
			"\tSELECT CM.sharer_id, COUNT(*)\n" +
			"\tFROM community_members CM\n" +
			"\tINNER JOIN communities C ON CM.community_id=C.id\n" +
			"\tINNER JOIN field_values FV ON FV.object_id=C.id\n" +
			"\tINNER JOIN fields F ON FV.field_id=F.id\n" +
			"\tWHERE C.deleted=false AND FV.object_type='COMMUNITY' AND F.internal_name='COMMUNITY_TYPE' AND FV.string_value='COMMUNITY_WITHOUT_ORGANIZATION'\n" +
			"\tGROUP BY CM.sharer_id\n" +
			") AS M2 ON M2.sharer_id=S.id\n" +
			"LEFT JOIN (\n" +
			"\t--(sharer_id,count) создал объединений в рамках юр лиц(кол-во)\n" +
			"\tSELECT C.creator_id,  COUNT(*)\n" +
			"\tFROM communities C\n" +
			"\tINNER JOIN field_values FV ON FV.object_id=C.id\n" +
			"\tINNER JOIN fields F ON FV.field_id=F.id\n" +
			"\tWHERE FV.object_type='COMMUNITY' AND F.internal_name='COMMUNITY_TYPE' AND C.deleted=false AND FV.string_value='COMMUNITY_WITH_ORGANIZATION'\n" +
			"\tGROUP BY C.creator_id\n" +
			") AS C1 ON C1.creator_id=S.id\n" +
			"LEFT JOIN (\n" +
			"\t--(sharer_id,count) создал объединений вне рамках юр лиц(кол-во)\n" +
			"\tSELECT C.creator_id,  COUNT(*)\n" +
			"\tFROM communities C\n" +
			"\tINNER JOIN field_values FV ON FV.object_id=C.id\n" +
			"\tINNER JOIN fields F ON FV.field_id=F.id\n" +
			"\tWHERE FV.object_type='COMMUNITY' AND F.internal_name='COMMUNITY_TYPE' AND C.deleted=false AND FV.string_value='COMMUNITY_WITHOUT_ORGANIZATION'\n" +
			"\tGROUP BY C.creator_id\n" +
			") AS C2 ON C2.creator_id=S.id\n" +
			"\n" +
			"LEFT JOIN (\n" +
			"\tSELECT FV.object_id, FV.string_value\n" +
			"\tFROM field_values FV\n" +
			"\tINNER JOIN fields F ON F.id=FV.field_id \n" +
			"\tWHERE FV.object_type='SHARER' AND (F.internal_name='FIRSTNAME')\n" +
			") AS SFN ON S.id=SFN.object_id\n" +
			"LEFT JOIN (\n" +
			"\tSELECT FV.object_id, FV.string_value\n" +
			"\tFROM field_values FV\n" +
			"\tINNER JOIN fields F ON F.id=FV.field_id \n" +
			"\tWHERE FV.object_type='SHARER' AND (F.internal_name='LASTNAME')\n" +
			") AS SLN ON S.id=SLN.object_id\n" +
			"LEFT JOIN (\n" +
			"\tSELECT FV.object_id, FV.string_value\n" +
			"\tFROM field_values FV\n" +
			"\tINNER JOIN fields F ON F.id=FV.field_id \n" +
			"\tWHERE FV.object_type='SHARER' AND (F.internal_name='SECONDNAME')\n" +
			") AS SSN ON S.id=SSN.object_id\n" +
			"\n" +
			"LEFT JOIN (\n" +
			"\tSELECT FV.object_id, FV.string_value\n" +
			"\tFROM field_values FV\n" +
			"\tINNER JOIN fields F ON F.id=FV.field_id \n" +
			"\tWHERE FV.object_type='SHARER' AND (F.internal_name='FIRSTNAME')\n" +
			") AS IFN ON I.sharer_id=IFN.object_id\n" +
			"LEFT JOIN (\n" +
			"\tSELECT FV.object_id, FV.string_value\n" +
			"\tFROM field_values FV\n" +
			"\tINNER JOIN fields F ON F.id=FV.field_id \n" +
			"\tWHERE FV.object_type='SHARER' AND (F.internal_name='LASTNAME')\n" +
			") AS ILN ON I.sharer_id=ILN.object_id\n" +
			"LEFT JOIN (\n" +
			"\tSELECT FV.object_id, FV.string_value\n" +
			"\tFROM field_values FV\n" +
			"\tINNER JOIN fields F ON F.id=FV.field_id \n" +
			"\tWHERE FV.object_type='SHARER' AND (F.internal_name='SECONDNAME')\n" +
			") AS ISN ON I.sharer_id=ISN.object_id\n" +
			"LEFT JOIN (\n" +
			"\tSELECT FV.object_id, FV.string_value\n" +
			"\tFROM field_values FV\n" +
			"\tINNER JOIN fields F ON F.id=FV.field_id \n" +
			"\tWHERE FV.object_type='SHARER' AND (F.internal_name='GENDER')\n" +
			") AS G ON I.sharer_id=G.object_id\n" +
			"\n" +
			"WHERE verified=true AND verifier_id = :verifier_id";

	// Возвращает список сертифицированных шарером пользователей(только нужную инфу)
	public List<VerifiedSharerDto> getVerifiedSharers(Long userId) {
		Query query = getCurrentSession().createSQLQuery(SELECT_VERIFIED_SHARERS_FOR_SHARER_SQL).setLong("verifier_id", userId);
		List rows = query.list();

		List<VerifiedSharerDto> result = new ArrayList<>(rows.size());

		for (Object row: rows) {
			result.add(createVerifiedSharerDtoFromRow(row));
		}

		return result;
	}

	private Address getAddressByFieldNames(Map<String, String> fieldNames, String roomLabel, Long sharerId) {
		String query =
		"select \n"+
		"( \n"+
		"case fd.internal_name \n"+
		"when :country_field_name then \n"+
		"coalesce( \n"+
		"		(select lei.text from list_editor_item lei \n"+
		"		where lei.id = (select cast((0 || fd.string_value) as integer))), '') \n"+
		"else coalesce(fd.string_value, '') \n"+
		"end \n"+
		") as value, \n"+
		"( \n"+
		"case fd.internal_name \n"+
		"when :country_field_name then 'country' \n"+
		"when :region_field_name then 'region' \n"+
		"when :district_field_name then 'district' \n"+
		"when :city_field_name then 'city' \n"+
		"when :street_field_name then 'street' \n"+
		"when :building_field_name then 'building' \n"+
		"when :position_field_name then 'position' \n"+
		"when :location_field_name then 'location' \n"+
		"when :room_field_name then 'room' \n"+
		"end \n"+
		") as field_name \n"+
		"from ( \n"+
		"		select fv.string_value, f.internal_name from field_values fv \n"+
		"		join fields f on fv.field_id = f.id \n"+
		"		where fv.object_type = 'SHARER' and fv.object_id = :sharer_id and f.internal_name in " +
				"(:country_field_name, :region_field_name, :district_field_name, :city_field_name, :street_field_name, " +
				":building_field_name, :position_field_name, :location_field_name, :room_field_name) "+
		") as fd";
		SQLQuery sqlQuery = createSQLQuery(query);
		for (String fieldNameKey : fieldNames.keySet()) {
			String fieldName = fieldNames.get(fieldNameKey);
			sqlQuery.setString(fieldNameKey, fieldName);
		}
		sqlQuery.setLong("sharer_id", sharerId);
		sqlQuery.setResultTransformer(Transformers.TO_LIST);
		List<List<Object>> results = sqlQuery.list();
		Address result = null;
		if (results != null) {
			String country = null;
			String region = null;
			String district = null;
			String city = null;
			String street = null;
			String building = null;
			String position = null;
			String location = null;
			String room = null;
			for(List<Object> row: results){
				String value = (String)row.get(0);
				String fieldName = (String)row.get(1);
				switch (fieldName) {
					case "country":
						country = value;
						break;
					case "region":
						region = value;
						break;
					case "district":
						district = value;
						break;
					case "city":
						city = value;
						break;
					case "street":
						street = value;
						break;
					case "building":
						building = value;
						break;
					case "position":
						position = value;
						break;
					case "location":
						location = value;
						break;
					case "room":
						room = value;
						break;
				}
			}
			result = new Address(country, region, district, city, street, building, position, location, room, roomLabel);
		}
		return result;
	}


	/**
	 * Фактический фдрес участника
	 * @param userId
	 * @return
	 */
	public Address getActualAddress(Long userId) {
        return getAddressByFieldNames(FACT_ADDRESS_FIELDS, "квартира", userId);
	}

    public String getFullActualAddress(Long userId) {
        FieldValueEntity fieldValue = fieldValueDao.get(userId, "SHARER", FieldConstants.FACT_GEO_LOCATION_SHARER);
        if (fieldValue != null) return fieldValue.getStringValue();
        return getActualAddress(userId).getFullAddress();
    }

    public String getFullRegistrationAddress(Long userId) {
        FieldValueEntity fieldValue = fieldValueDao.get(userId, "SHARER", FieldConstants.REGISTRATION_GEO_LOCATION_SHARER);
        if (fieldValue != null) return fieldValue.getStringValue();
        return getRegistrationAddress(userId).getFullAddress();
    }

	/**
	 * Адрес регистрации участника
	 * @param userId
	 * @return
	 */
	public Address getRegistrationAddress(Long userId) {
		return getAddressByFieldNames(REGISTRATION_ADDRESS_FIELDS, "квартира", userId);
	}

	/**
	 * Адрес офиса регистратора
	 * @param userEntity
	 * @return
	 */
	public Address getRegistratorOfficeAddress(UserEntity userEntity) {
		return getAddressByFieldNames(REGISTRATOR_OFFICE_FIELDS, "офис", userEntity.getId());
	}
	public Address getOfficeAddress(Long userId) {
		return getAddressByFieldNames(REGISTRATOR_OFFICE_FIELDS, "офис", userId);
	}
	/**
	 * Баланс пользователя
	 * @param userId
	 * @return
	 */
	public BigDecimal getBalance(Long userId) {
        String query =
                "select coalesce(" +
                        "(select sum(a.total_balance) from accounts a where a.owner_id = :sharer_id and a.owner_type = :discriminator " +
                        "), 0) as balance";
		SQLQuery sqlQuery = createSQLQuery(query);
		sqlQuery.setLong("sharer_id", userId);
		sqlQuery.setString("discriminator", Discriminators.SHARER);
		sqlQuery.addScalar("balance", BigDecimalType.INSTANCE);
		List<BigDecimal> results = sqlQuery.list();
		BigDecimal result = null;
		if (results != null && results.size() > 0) {
			result = results.get(0);
		}
		return result;
	}

	/**
	 * Загрузить пользоватлей по ИДым и по странице
	 * @param ids ИДы
	 * @param page номер страницы
	 * @param countInPage количество элементов на странице
	 * @return список пользователей
	 */
	public List<UserEntity> getByIdsAndPage(List<Long> ids, int page, int countInPage) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("deleted", false));
		criteria.add(Restrictions.in("id", ids));
		criteria.addOrder(Order.asc("id"));
		criteria.setFirstResult(page * countInPage);
		criteria.setMaxResults(countInPage);
		return find(criteria);
	}

	/**
	 * Загрузить всех участников объединения
	 * @param communityId объединение
	 * @return список пользователей
	 */
	public List<UserEntity> getSharersMembersOfCommunity(Long communityId) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("deleted", false));

		criteria.createAlias("members", "membersAlias");
		criteria.add(Restrictions.eq("membersAlias.community.id", communityId));
		return find(criteria);
	}
	public boolean isActualAddressNotHidden(Long userId) {
		boolean result = false;
		FieldValueEntity fieldValueEntity = null;
		for (String internalName : FACT_ADDRESS_FIELDS.values()) {
			fieldValueEntity = fieldValueDao.get(userId,"SHARER",internalName);
			if (fieldValueEntity != null) {
				result = result || (!fieldValueEntity.isHidden());
			}
		}
		return result;
	}
	public boolean isActualCountryHidden(Long userId) {
		FieldValueEntity fieldValueEntity  = fieldValueDao.get(userId,"SHARER",FieldConstants.FACT_COUNTRY_SHARER);
		return fieldValueEntity != null ? fieldValueEntity.isHidden() : true;
	}
	public boolean isActualCityHidden(Long userId) {
		FieldValueEntity fieldValueEntity  = fieldValueDao.get(userId,"SHARER",FieldConstants.FACT_CITY_SHARER);
		return fieldValueEntity != null ? fieldValueEntity.isHidden() : true;
	}
	public boolean isActualStreetHidden(Long userId) {
		FieldValueEntity fieldValueEntity  = fieldValueDao.get(userId,"SHARER",FieldConstants.FACT_STREET_SHARER);
		return fieldValueEntity != null ? fieldValueEntity.isHidden() : true;
	}
	public boolean isActualBuildingHidden(Long userId) {
		FieldValueEntity fieldValueEntity  = fieldValueDao.get(userId,"SHARER",FieldConstants.FACT_BUILDING_SHARER);
		return fieldValueEntity != null ? fieldValueEntity.isHidden() : true;
	}


}
