package ru.radom.kabinet.services.sharer;

import ru.askor.blagosfera.domain.registrator.RegistratorDomain;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.model.registration.RegistratorLevel;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * Created by vgusev on 17.03.2016.
 */
public interface UserDataService {

    /**
     * Загрузить объект с минимальным набором данных (без userData)
     * @param id
     * @return
     */
    User getByIdMinData(Long id);

    /**
     *
     * @param ids
     */
    List<User> getByIds(List<Long> ids);

    /**
     * Загрузить объект с максимальным набором данных (без userData)
     * @param id
     * @return
     */
    User getByIdFullData(Long id);

    /**
     *
     * @param ikpOrShortLink
     * @return
     */
    User getByIkpOrShortLink(String ikpOrShortLink);

    /**
     *
     * @param email
     * @return
     */
    User getByEmail(String email);

    /**
     *
     * @param user
     */
    void save(User user);

    void saveAvatar(User user);

    void saveSearchString(Long userId, String searchString);

    Date updateLogoutDate(Long userId);

    void setAllowMultipleSessions(Long userId, boolean allowMultipleSessions);

    /**
     * Создать нового пользователя
     * @param user
     * @return
     */
    User create(User user, String password);

    /**
     * Загрузить пользователей объединения
     * @param communityId
     * @return
     */
    List<User> getMembersOfCommunityFullData(Long communityId);

    /**
     *
     * @return
     */
    List<User> getNotDeletedMinData();

    /**
     *
     * @param registratorDomains
     * @return
     */
    List<User> getUsersFromRegistratorsMinData(List<RegistratorDomain> registratorDomains);

    /**
     * Поиск участников системы
     * @param query
     * @param firstResult
     * @param maxResults
     * @return
     */
    List<User> searchMinData(String query, int firstResult, int maxResults);

    List<User> searchMinDataVerified(String query, int firstResult, int maxResults);

    /**
     *
     * @param page
     * @param countInPage
     * @return
     */
    List<User> getNotDeletedByPage(int page, int countInPage);

    /**
     *
     * @param page
     * @param countInPage
     * @return
     */
    List<User> getNotDeletedManByPage(int page, int countInPage);

    /**
     *
     * @param page
     * @param countInPage
     * @return
     */
    List<User> getNotDeletedWomenByPage(int page, int countInPage);

    /**
     *
     * @return
     */
    int getTotalCount();

    /**
     *
     * @return
     */
    int getCountNotDeletedManByPage();

    /**
     *
     * @return
     */
    int getCountNotDeletedWomenByPage();

    /**
     *
     * @param ids
     * @param page
     * @param countInPage
     * @return
     */
    List<User> getByIdsAndPage(List<Long> ids, int page, int countInPage);

    Date getLastLogin(Long userId);

    RegistratorLevel getRegistratorLevel(Long userId);

    /**
     * Поиск email
     * @param email
     * @return
     */
    boolean existsEmail(String email);
    boolean existsEmail(String email,User excludeUser);

    BigDecimal getUserBalance(User user);

    Long getVerifiedUsersCount(Long userId);

    Long getVerifiedCommunitiesCount(Long userId);

    Long getVerifiedRegistratorsCount(Long userId);
}
