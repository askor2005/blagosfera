package ru.radom.kabinet.services.sharer.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.askor.blagosfera.core.services.user.UserService;
import ru.askor.blagosfera.data.jpa.repositories.UserRepository;
import ru.askor.blagosfera.data.jpa.repositories.community.CommunityPostRepository;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.field.Field;
import ru.askor.blagosfera.domain.registrator.RegistratorDomain;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.dao.RoleDao;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dao.SharersGroupDao;
import ru.radom.kabinet.dao.communities.CommunityDao;
import ru.radom.kabinet.dao.registration.RegistratorDao;
import ru.radom.kabinet.model.ImageType;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.registration.RegistratorLevel;
import ru.radom.kabinet.services.field.FieldValidatorBundle;
import ru.radom.kabinet.services.field.FieldsService;
import ru.radom.kabinet.services.sharer.UserDataService;
import ru.radom.kabinet.utils.IkpUtils;
import ru.radom.kabinet.utils.MurmurHash;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by vgusev on 17.03.2016.
 */
@Service
@Transactional
public class UserDataServiceImpl implements UserDataService {

    @Autowired
    private SharerDao sharerDao;
    @Autowired
    private CommunityDao communityDao;
    @Autowired
    private RegistratorDao registratorDao;
    @Autowired
    private CommunityPostRepository communityPostRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private SharersGroupDao sharersGroupDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private FieldsService fieldsService;

    private FieldValidatorBundle fieldValidatorBundle;

    @Autowired
    private void setFieldValidatorBundle(FieldValidatorBundle fieldValidatorBundle) {
        this.fieldValidatorBundle = fieldValidatorBundle;
    }

    /*@PostConstruct
    private void createSearchStrings() {
        List<UserEntity> entities = userRepository.findAllByDeletedFalse();

        for (UserEntity entity : entities) {
            User user = getByIdFullData(entity.getId());
            saveSearchString(entity.getId(), fieldsService.makeSearchString(user));
        }
    }*/

    @Override
    public User getByIdMinData(Long id) {
        if (id == null) return null;
        UserEntity userEntity = userRepository.findOne(id);
        return userEntity.toDomain();
    }

    private List<User> toDomainMinDataListSafe(Collection<UserEntity> userEntities) {
        List<User> result = new ArrayList<>();
        if (userEntities != null) {
            result.addAll(userEntities.stream().map(UserEntity::toDomain).collect(Collectors.toList()));
        }
        return result;
    }

    @Override
    public List<User> getByIds(List<Long> ids) {
        return toDomainMinDataListSafe(sharerDao.getByIds(ids));
    }

    @Override
    public User getByIdFullData(Long id) {
        return userService.getUserById(id);
    }

    @Override
    public User getByIkpOrShortLink(String ikpOrShortLink) {
        UserEntity userEntity = sharerDao.getByIkpOrShortLink(ikpOrShortLink);
        User result = null;
        if (userEntity != null) {
            result = userEntity.toDomain();
        }
        return result;
    }

    @Override
    public User getByEmail(String email) {
        UserEntity userEntity = sharerDao.getByEmail(email);
        User result = null;
        if (userEntity != null) {
            result = userEntity.toDomain();
        }
        return result;
    }

    @Override
    public void save(User user) {
        if (user.getId() == null) return;
        UserEntity userEntity = userRepository.findOne(user.getId());
        if (userEntity == null) return;

        userEntity.setAllowMultipleSessions(user.isAllowMultipleSessions());
        userEntity.setActivateCode(user.getActivateCode());
        userEntity.setDeleted(user.isDeleted());
        userEntity.setEmail(user.getEmail());
        userEntity.setAvatarSrc(user.getAvatar());
        userEntity.setAvatarPhotoSrc(user.getAvatarSrc());

        if (user.isVerified()) {
            userEntity.setVerificationDate(user.getVerificationDate());
            userEntity.setVerifier(userRepository.findOne(user.getVerifier()));
            userEntity.setVerified(true);
        }

        userRepository.save(userEntity);
    }
    @Override
    public void saveAvatar(User user) {
        if (user.getId() == null) return;
        UserEntity userEntity = userRepository.findOne(user.getId());
        if (userEntity == null) return;
        userEntity.setAvatarSrc(user.getAvatar());
        userEntity.setAvatarPhotoSrc(user.getAvatarSrc());
        userRepository.save(userEntity);
    }

    @Override
    public void saveSearchString(Long userId, String searchString) {
        UserEntity userEntity = userRepository.findOne(userId);
        if (userEntity == null) return;
        userEntity.setSearchString(searchString);
        userRepository.save(userEntity);
    }

    @Override
    public Date updateLogoutDate(Long userId) {
        Assert.notNull(userId);
        UserEntity userEntity = userRepository.findOne(userId);
        Assert.notNull(userEntity);

        Date date = new Date();
        userEntity.setLogoutDate(date);
        userRepository.save(userEntity);

        return date;
    }

    @Override
    public void setAllowMultipleSessions(Long userId, boolean allowMultipleSessions) {
        UserEntity userEntity = userRepository.findOne(userId);
        Assert.notNull(userEntity);

        userEntity.setAllowMultipleSessions(allowMultipleSessions);
        userRepository.save(userEntity);
    }

    @Override
    public User create(User user, String password) {
        if (user.getId() != null) return null;
        UserEntity userEntity = new UserEntity();

        Date now = new Date();
        userEntity.setSearchString(user.getFullName() + " " + user.getEmail());
        userEntity.setEmail(user.getEmail());
        userEntity.setSalt(user.getSalt());
        userEntity.setPassword(password);
        userEntity.setStatus(user.getStatus());
        userEntity.setIkp(user.getIkp());
        userEntity.setVerified(user.isVerified());
        userEntity.setGroup(sharersGroupDao.getById(12L));
        userEntity.setAvatarPhotoSrc(user.getAvatarSrc());
        userEntity.setAvatarSrc(user.getAvatar());
        userEntity.setProfileUnfilledAt(now);
        userEntity.setDeleted(false);
        userEntity.setArchived(false);
        userEntity.setAllowMultipleSessions(true); // Параллельные сессии разрешены по умолчанию
        userEntity.setRegisteredAt(now);
        if (user.getInviter() != null && user.getInviter().getId() != null) {
            userEntity.setInviter(userRepository.getOne(user.getInviter().getId()));
        }

        userEntity.getRoles().clear();
        userEntity.getRoles().add(roleDao.getByName("USER"));

        List<Field> fields = user.getFields();
        userEntity = userRepository.save(userEntity);
        user = userEntity.toDomain();
        fieldsService.saveFields(user, fields, fieldValidatorBundle);

        String ikp = IkpUtils.longToIkpHash(MurmurHash.hash64(userEntity.getId() + userEntity.getIkp()));
        userEntity.setIkp(ikp);
        userEntity.setActivateCode(null);
        userEntity.setActivateCodeAt(null);
        userEntity = userRepository.save(userEntity);

        user = userEntity.toDomain();

        return user;
    }

    @Override
    public List<User> getMembersOfCommunityFullData(Long communityId) {
        List<User> users = new ArrayList<>();
        List<UserEntity> userEntityEntities = sharerDao.getSharersMembersOfCommunity(communityId);
        for (UserEntity userEntity : userEntityEntities) {
            users.add(userService.getUserById(userEntity.getId()));
        }
        return users;
    }

    @Override
    public List<User> getNotDeletedMinData() {
        return toDomainMinDataListSafe(userRepository.findByDeletedOrderBySearchStringAsc(false));
    }

    @Override
    public List<User> getUsersFromRegistratorsMinData(List<RegistratorDomain> registratorDomains) {
        List<User> users = registratorDomains.stream().map(registrator -> registrator.getUser()).collect(Collectors.toList());
        return users;
    }

    @Override
    public List<User> searchMinData(String query, int firstResult, int maxResults) {
        return toDomainMinDataListSafe(sharerDao.search(query, firstResult, maxResults));
    }
    @Override
    public List<User> searchMinDataVerified(String query, int firstResult, int maxResults) {
        return toDomainMinDataListSafe(sharerDao.searchVerified(query, firstResult, maxResults, false, "searchString", true));
    }

    @Override
    public List<User> getNotDeletedByPage(int page, int countInPage) {
        return toDomainMinDataListSafe(sharerDao.getNotDeletedByPage(page, countInPage));
    }

    @Override
    public List<User> getNotDeletedManByPage(int page, int countInPage) {
        return toDomainMinDataListSafe(sharerDao.getNotDeletedManByPage(page, countInPage));
    }

    @Override
    public List<User> getNotDeletedWomenByPage(int page, int countInPage) {
        return toDomainMinDataListSafe(sharerDao.getNotDeletedWomenByPage(page, countInPage));
    }

    @Override
    public int getTotalCount() {
        return sharerDao.getTotalCount();
    }

    @Override
    public int getCountNotDeletedManByPage() {
        return sharerDao.getCountNotDeletedManByPage();
    }

    @Override
    public int getCountNotDeletedWomenByPage() {
        return sharerDao.getCountNotDeletedWomenByPage();
    }

    @Override
    public List<User> getByIdsAndPage(List<Long> ids, int page, int countInPage) {
        return toDomainMinDataListSafe(sharerDao.getByIdsAndPage(ids, page, countInPage));
    }

    @Override
    public Date getLastLogin(Long userId) {
        UserEntity userEntity = userRepository.findOne(userId);
        return userEntity.getLogoutDate();
    }

    @Override
    public RegistratorLevel getRegistratorLevel(Long userId) {
        List<String> levels = communityPostRepository.getMnemosForUserByPatternOrderDesc(userId, RegistratorLevel.PREFIX);
        for (String level : levels) {
            if (RegistratorLevel.getByMnemo(level) != null)
                return RegistratorLevel.getByMnemo(level);
        }
        return null;
    }

    @Override
    public boolean existsEmail(String email) {
        UserEntity userEntity = userRepository.findOneByEmail(email);
        return userEntity != null;
    }

    @Override
    public boolean existsEmail(String email, User excludeUser) {
        UserEntity userEntity = userRepository.findOneByEmailAndIdNot(email, excludeUser.getId());
        return userEntity != null;
    }

    @Override
    public BigDecimal getUserBalance(User user) {
        return userRepository.getUserBalance(user.getId());
    }

    @Override
    public Long getVerifiedUsersCount(Long userId) {
         return sharerDao.getVerifiedSharersCount(userId);
    }
    @Override
    public Long getVerifiedCommunitiesCount(Long userId) {
        return communityDao.getVerifiedCommunitiesCount(userId);
    }
    @Override
    public Long getVerifiedRegistratorsCount(Long userId) {
        return registratorDao.getVerifiedRegistratorsCount(userId);
    }
}
