package ru.askor.blagosfera.core.services.registrator;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.data.jpa.repositories.UserRepository;
import ru.askor.blagosfera.data.jpa.repositories.registrator.RegistrationRequestRepository;
import ru.askor.blagosfera.data.jpa.specifications.registrator.RegistratorSpecifications;
import ru.askor.blagosfera.domain.Address;
import ru.askor.blagosfera.domain.registrator.RegistratorDomain;
import ru.askor.blagosfera.domain.registrator.RegistratorSort;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dao.fields.FieldValueDao;
import ru.radom.kabinet.dao.registration.RegistratorDao;
import ru.radom.kabinet.dto.Timetable;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.fields.FieldValueEntity;
import ru.radom.kabinet.model.registration.RegistratorLevel;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.field.FieldsService;
import ru.radom.kabinet.services.sharer.UserDataService;
import ru.radom.kabinet.utils.FieldConstants;
import ru.radom.kabinet.utils.StringUtils;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by vtarasenko on 13.04.2016.
 */
@Service
@Transactional
public class RegistratorDataServiceImpl implements RegistratorDataService {
    private static final Logger logger = LoggerFactory.createLogger(RegistratorDataService.class);
    @Autowired
    private SharerDao sharerDao;
    @Autowired
    private RegistrationRequestRepository registrationRequestRepository;
    //TODO пока не используется тк при использовании paging при выброрке из бд сортировать часть выборки неправильно, нужно
    //сортировать по расстоянию средствами бд(например postgis)
    final static Comparator<RegistratorDomain> distanceComparator = new Comparator<RegistratorDomain>() {
        public int compare(RegistratorDomain s1, RegistratorDomain s2) {
            if (Objects.equals(s1.getDistance(), s2.getDistance())) return 0;
            if (s1.getDistance() == null) return -1;
            if (s2.getDistance() == null) return 1;
            return s1.getDistance().compareTo(s2.getDistance());
        }
    };
    @Autowired
    private FieldValueDao fieldValueDao;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserDataService userDataService;

    @Autowired
    private RegistratorDao registratorDao;

    private Specification<UserEntity> buildSearchSpecification(Long currentUserId, String nameTemplate,
                                                               final RegistratorLevel filterLevel,
                                                               List<String> excludeLevels, boolean includeRequestedForRegistration, boolean requestedForRegistrationsOnlyToMe) {
        Specifications<UserEntity> result = Specifications.where(RegistratorSpecifications.allRegistrators());

        if (currentUserId != null) {
            result = result.and(RegistratorSpecifications.userIdNotEquals(currentUserId));
        }

        if ((nameTemplate != null) && (!StringUtils.isEmpty(nameTemplate))) {
            result = result.and(RegistratorSpecifications.searchStringLike(nameTemplate));
        }

        Specifications filterLevelSpecifications = null;

        if (filterLevel == null) {
            filterLevelSpecifications = Specifications.where(RegistratorSpecifications.hasCommunityPostMnemoStartsWithLike(RegistratorLevel.PREFIX));
        } else {
            filterLevelSpecifications = Specifications.where(RegistratorSpecifications.hasCommunityPostMnemoEqual(filterLevel.getMnemo()));
        }

        if (excludeLevels != null) {
            for (String level : excludeLevels) {
                filterLevelSpecifications = filterLevelSpecifications.and(RegistratorSpecifications.hasNotCommunityPost(level));
            }
        }

        if (includeRequestedForRegistration) {
            if (requestedForRegistrationsOnlyToMe) {
                filterLevelSpecifications = filterLevelSpecifications.or(RegistratorSpecifications.idIn(userRepository.getUsersIdsWithRegistrationRequestsToRegistrator(currentUserId)));
            } else {
                filterLevelSpecifications = filterLevelSpecifications.or(RegistratorSpecifications.idIn(userRepository.getUsersIdsWithRegistrationRequests()));
            }
        }

        return result.and(filterLevelSpecifications);
    }

    @Override
    public List<RegistratorDomain> search(Long currentUserId, String nameTemplate, int page, int pageSize,
                                          final RegistratorLevel filterLevel, final Double latitude, final Double longitude,
                                          List<String> excludeLevels, boolean includeRequestedForRegistration,
                                          RegistratorSort registratorSort, boolean requestedForRegistrationsOnlyToMe) {
        Specification<UserEntity> specification = buildSearchSpecification(currentUserId, nameTemplate, filterLevel,
                excludeLevels, includeRequestedForRegistration, requestedForRegistrationsOnlyToMe);
        PageRequest pageRequest = registratorSort != null ?
                new PageRequest(page, pageSize, Sort.Direction.fromString(registratorSort.getDirection()), registratorSort.getProperty()) :
                new PageRequest(page, pageSize);
        List<UserEntity> userEntities = userRepository.findAll(specification, pageRequest).getContent();
        List<RegistratorDomain> result = transformToRegistrators(userEntities, latitude, longitude);
        return result;
    }

    @Override
    public List<RegistratorDomain> search(Long currentUserId, String nameTemplate,
                                          final RegistratorLevel filterLevel, final Double latitude, final Double longitude,
                                          List<String> excludeLevels, boolean includeRequestedForRegistration, boolean requestedForRegistrationsOnlyToMe) {
        Specification<UserEntity> specification = buildSearchSpecification(currentUserId, nameTemplate, filterLevel, excludeLevels, includeRequestedForRegistration, requestedForRegistrationsOnlyToMe);
        List<RegistratorDomain> result = transformToRegistrators(userRepository.findAll(specification), latitude, longitude);
        return result;
    }

    @Override
    public Long count(Long currentUserId, String nameTemplate,
                      final RegistratorLevel filterLevel,
                      List<String> excludeLevels) {
        Specification<UserEntity> specification = buildSearchSpecification(currentUserId, nameTemplate, filterLevel, excludeLevels, false, false);
        return userRepository.count(specification);
    }

    private List<RegistratorDomain> transformToRegistrators(List<UserEntity> users, final Double latitude, final Double longitude) {
        return users.stream().map(userEntity -> {
            User user = userDataService.getByIdFullData(userEntity.getId());
            // boolean existsRequestToMe = registrationRequestRepository.existsForUserAndRegistrator(user.getId(), SecurityUtils.getUser().getId());
            boolean existsRequest = registrationRequestRepository.existsForUser(user.getId());
            Double distance = null;
            Timetable timetable = null;
            Address officeAddress = sharerDao.getRegistratorOfficeAddress(userEntity);
            if ((existsRequest) && ((officeAddress == null) || (officeAddress.getLatitude() == null) || (officeAddress.getLongitude() == null))) {
                officeAddress = sharerDao.getActualAddress(user.getId());
                officeAddress.setRoomLabel(null);
                officeAddress.setRoom(null);
            }
            if ((existsRequest) && ((officeAddress == null) || (officeAddress.getLatitude() == null) || (officeAddress.getLongitude() == null))) {
                officeAddress = sharerDao.getRegistrationAddress(user.getId());
                officeAddress.setRoomLabel(null);
                officeAddress.setRoom(null);
            }
            user.setOfficeAddress(officeAddress);
            if ((officeAddress != null) && (longitude != null) && (latitude != null) && (officeAddress.getLongitude() != null) && (officeAddress.getLatitude() != null)) {
                distance = RegistratorDao.calculateDistance(latitude, officeAddress.getLatitude(), longitude, officeAddress.getLongitude(), 0d, 0d) / 1000;
            }
            FieldValueEntity timeTableFieldValue = fieldValueDao.get(userEntity, FieldConstants.SHARER_REGISTRATOR_OFFICE_TIMETABLE);
            if (timeTableFieldValue != null && !StringUtils.isEmpty(timeTableFieldValue.getStringValue())) {
                timetable = new Timetable(FieldsService.getFieldStringValue(timeTableFieldValue));
            }
            List<FieldValueEntity> additionalFieldValues = fieldValueDao.getByFieldList(userEntity, Arrays.asList(
                    FieldConstants.SHARER_REGISTRATOR_OFFICE_PHONE,
                    FieldConstants.SHARER_MOB_TEL,
                    FieldConstants.SHARER_REGISTRATOR_MOBILE_PHONE,
                    FieldConstants.SHARER_SKYPE,
                    FieldConstants.SHARER_HOME_TEL
            ));
            String skype = null;
            String registratorOfficePhone = null;
            String registratorPhone = null;
            String registratorMobilePhone = null;
            for (FieldValueEntity fieldValue : additionalFieldValues) {
                if (fieldValue.getField().getInternalName().equals(FieldConstants.SHARER_REGISTRATOR_OFFICE_PHONE)) {
                    registratorOfficePhone = FieldsService.getFieldStringValue(fieldValue);
                } else if (fieldValue.getField().getInternalName().equals(FieldConstants.SHARER_REGISTRATOR_MOBILE_PHONE)) {
                    registratorMobilePhone = FieldsService.getFieldStringValue(fieldValue);
                } else if (fieldValue.getField().getInternalName().equals(FieldConstants.SHARER_MOB_TEL)) {
                    registratorMobilePhone = FieldsService.getFieldStringValue(fieldValue);
                } else if (fieldValue.getField().getInternalName().equals(FieldConstants.SHARER_SKYPE)) {
                    skype = FieldsService.getFieldStringValue(fieldValue);
                } else if (fieldValue.getField().getInternalName().equals(FieldConstants.SHARER_HOME_TEL)) {
                    registratorPhone = FieldsService.getFieldStringValue(fieldValue);
                }

            }
            if ((registratorOfficePhone == null) && (existsRequest)) {
                registratorOfficePhone = registratorPhone;
            }
            if ((registratorOfficePhone == null) && (existsRequest)) {
                registratorOfficePhone = registratorMobilePhone;
            }
            return new RegistratorDomain(user, userDataService.getRegistratorLevel(user.getId()), distance, timetable, registratorOfficePhone, registratorMobilePhone, skype, registrationRequestRepository.existsForUserAndRegistrator(SecurityUtils.getUser().getId(), user.getId()), existsRequest);
        }).collect(Collectors.toList());
        //Collections.sort(result, distanceComparator);
    }

    @Override
    public List<User> getVerifiedRegistrators(Long userId) {
        List<User> result = null;
        if (userId != null) {
            List<UserEntity> userEntities = registratorDao.getVerifiedRegistrators(userRepository.findOne(userId));
            List<Long> ids = new ArrayList<>();
            for (UserEntity userEntity : userEntities) {
                ids.add(userEntity.getId());
            }
            result = userDataService.getByIds(ids);
        }
        return result;
    }

    @Override
    public User getByRegistratorId(Long registratorId) {
        User result = null;
        UserEntity userEntity = registratorDao.getById(registratorId);
        if (userEntity != null) {
            result = userDataService.getByIdFullData(userEntity.getId());
        }
        return result;
    }

    @Override
    public RegistratorDomain getRegistratorDtoById(Long id) {
        return registratorDao.getRegistratorById(id);
    }
    @Override
    public Integer getRegistratorLevelById(Long id) {
        RegistratorDomain registratorDomain = registratorDao.getRegistratorById(id);
        if ((registratorDomain != null) && (registratorDomain.getLevel() != null) && (registratorDomain.getLevel().getMnemo() != null)) {
            return getRegistratorLevel(registratorDomain.getLevel().getMnemo());
        }
        return null;
    }

    @Override
    public String getRegistratorLevel(Long registratorId) {
        return registratorDao.getRegistratorLevel(registratorId);
    }
    private Integer getRegistratorLevel(String registratorLevelMnemo) {
        Integer registratorLevel = null;
        if(registratorLevelMnemo != null) {
            switch(registratorLevelMnemo) {
                case "registrator.level0":
                    registratorLevel = 0;
                    break;
                case "registrator.level1":
                    registratorLevel = 1;
                    break;
                case "registrator.level2":
                    registratorLevel = 2;
                    break;
                case "registrator.level3":
                    registratorLevel = 3;
                    break;
                default:
                    registratorLevel = -1;
                    break;
            }
        }
        return registratorLevel;
    }
}
