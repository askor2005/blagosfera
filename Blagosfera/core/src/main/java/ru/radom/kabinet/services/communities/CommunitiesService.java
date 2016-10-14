package ru.radom.kabinet.services.communities;

import org.springframework.amqp.core.Message;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import ru.askor.blagosfera.domain.account.Account;
import ru.askor.blagosfera.domain.community.*;
import ru.askor.blagosfera.domain.user.UserDetailsImpl;
import ru.askor.blagosfera.domain.user.User;
import ru.askor.voting.domain.BatchVoting;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.communities.postappointbehavior.impl.PostAppointData;
import ru.radom.kabinet.web.utils.BreadcrumbItem;

import java.util.List;
import java.util.Map;

public interface CommunitiesService extends ApplicationContextAware {

	/**
	 * Пригласить пользователя на должность в сообщество
	 * <ul>
	 *     <li><b>memberId</b> id участника</li>
	 *     <li><b>sharer</b>, в котором лежит id, ikp или объект у которого есть поле id</li>
	 *     <li><b>appointer</b>, в котором лежит id, ikp или объект у которого есть поле id</li>
	 *     <li><b>community</b>, id, seolink или объект у которого есть поле id</li>
	 *     <li><b>post</b>, id, name или объект у которого есть поле id</li>
	 * </ul>
	 */
	//void requestAppointWorker(Message message);

	/**
	 * Назначить пользователя на должность в сообщество
	 * <ul>
	 *     <li><b>memberId</b> id участника</li>
	 *     <li><b>sharer</b>, в котором лежит id, ikp или объект у которого есть поле id</li>
	 *     <li><b>appointer</b>, в котором лежит id, ikp или объект у которого есть поле id</li>
	 *     <li><b>community</b>, id, seolink или объект у которого есть поле id</li>
	 *     <li><b>post</b>, id, name или объект у которого есть поле id</li>
	 * </ul>
	 */
	//void appointWorker(Message message);

	/**
	 * Пригласить пользователя на должность в сообщество
	 * <ul>
	 *     <li><b>memberId</b> id участника</li>
	 *     <li><b>sharer</b>, в котором лежит id, ikp или объект у которого есть поле id</li>
	 *     <li><b>disappointer</b>, в котором лежит id, ikp или объект у которого есть поле id</li>
	 *     <li><b>community</b>, id, seolink или объект у которого есть поле id</li>
	 *     <li><b>post</b>, id, name или объект у которого есть поле id</li>
	 * </ul>
	 */
	//void diappointWorker(Message message);

	/**
	 * Пытаемся получить объединение из пришедщих данных
	 * @param communityObj id, seolink или объект у которого есть поле id
	 */
	Community tryGetCommunity(Object communityObj);

	/**
	 * Пытаемся получить должность в объединении из пришедщих данных
	 * @param communityPostObj id, name или объект у которого есть поле id
	 */
	CommunityPost tryGetCommunityPost(Community community, Object communityPostObj);

	/**
	 * Создание объединения
	 * @param community
	 * @param creator
	 * @param users
     * @return
     */
	Community createCommunity(Community community, User creator, List<User> users);

	/**
	 * Создание объединения с опопвещением участников
	 * @param community
	 * @param creator
	 * @param users
	 * @param receiversNotification
     * @return
     */
	Community createCommunity(Community community, User creator, List<User> users, List<User> receiversNotification);

	/**
	 * Редактирование объединения
	 * @param community
	 * @param editor
     * @return
     */
	Community editCommunity(Community community, User editor);

	/**
	 * Поиск возможных участников объединения
	 * @param communityId
	 * @param firstResult
	 * @param maxResults
	 * @param query
	 * @param onlyVerifiedUsers
	 * @return
	 */
	List<CommunityMember> getPossibleMembers(Long communityId, int firstResult, int maxResults, String query, boolean onlyVerifiedUsers);

	@Deprecated
	boolean hasPermission(CommunityEntity community, Long userId, String permission);

    /**
     * Проверить является ли юзер членом объединения
     *
     * @param communityId
     * @param userId
     * @return
     */
    boolean isMember(Long communityId, Long userId);

	/**
	 * Проверить права доступа у пользователя в объединении
	 * @param communityId
	 * @param userId
	 * @param permission
	 * @return
	 */
	boolean hasPermission(Long communityId, Long userId, String permission);

	void onApplicationEvent(ApplicationEvent event);

	@Override
	void setApplicationContext(ApplicationContext applicationContext) throws BeansException;

	void checkPermission(Long communityId, User sharer, String permission, String errorMessage);

	/**
	 * Сохранить должность
	 * @param user Пользователь
	 * @param post Должность
	 * @param communityId ИД объединения в которой будет сохранена должность. Параметр для проверки
	 * @return
	 */
	CommunityPost savePost(User user, CommunityPost post, Long communityId);

	/**
	 * Удалить должность
	 * @param user пользователь
	 * @param postId ИД должности
	 * @param communityId ИД объединения в которой будет удалена должность. Параметр для проверки
	 * @return
	 */
	CommunityPost deletePost(User user, Long postId, Long communityId);

	CommunityPost copyPost(User user, Long postId, Long communityId);

	CommunityPost getCeoPost(Community community);

	/**
	 * Принятие запроса на работу
	 * @param postRequestId
	 * @param userId
	 */
	PostAppointData approveAppointRequest(Long postRequestId, Long userId);

	/**
	 *
	 * @param postRequestId
	 * @param userId
     */
	void cancelPostAppoint(Long postRequestId, Long userId);

	/**
	 * Сделать запрос участнику объединения на назначение в должности
	 * @param appointer
	 * @param member
	 * @param post
	 */
	void requestToAppoint(User appointer, CommunityMember member, CommunityPost post);

	/**
	 * Назначение члена объединения на должность
	 * @param communityPostRequest
	 * @return
	 */
	CommunityMember appoint(CommunityPostRequest communityPostRequest);

	/**
	 * Назначение члена объединения на должность на основе собрания
	 * @param batchVoting
	 * @param member
	 * @param post
	 * @return
	 */
	CommunityMember appoint(BatchVoting batchVoting, CommunityMember member, CommunityPost post);

	/**
	 * Назначение члена объединения на должность
	 */
	CommunityMember appoint(User appointer, CommunityMember member, CommunityPost post);

	/**
	 * Снятие с должности
	 * @param appointer
	 * @param member
	 * @param post
	 */
	void disapoint(User appointer, CommunityMember member, CommunityPost post);

	Map<Community, Integer> getChildMap(Community community);

	Community deleteCommunity(Long communityId, String comment, User user);

	Community restoreCommunity(Long communityId);

	boolean canTransferMoneyCommunity(Community community, User transferer);

	boolean canDeleteCommunity(Community community, UserDetailsImpl userDetails);

	boolean canRestoreCommunity(Community community, UserDetailsImpl userDetails);

	/**
	 * Выполнить сертификацию объединения
	 * @param communityId
	 * @param registrator
	 */
	void verifiedCommunity(Long communityId, User registrator);

	//List<BreadcrumbItem> getBreadcrumbCommonItems();

	List<BreadcrumbItem> getBreadcrumbCommonItems(boolean isMember, boolean isCreator, Community community);

	/**
	 * Получить ИД директора объединения установленного в полях
	 * @param community
	 * @return
	 */
	Long getCommunityDirectorId(Community community);

	/**
	 * Получить ИД директора объединения установленного в полях
	 * @param community
	 * @return
	 */
	User getCommunityDirector(Community community);

	/**
	 * Получить членов совета ПО
	 * @param community
	 * @return
	 */
	List<User> getMembersSovietOfCooperative(Community community);

	/**
	 * Получить председателя собрания ПО
	 * @param community
	 * @return
	 */
	User getPresidentSovietOfCooperative(Community community);

	/**
	 *
	 * @param communityId
	 * @param userDetails
	 * @return
	 */
	CommunityNewsPageDomain getCommunityNewsPageDomain(Long communityId, UserDetailsImpl userDetails);

	/**
	 * Получить ИДы видимых разделов объединения для пользователя
	 * @param communityId
	 * @param currentUserId
	 * @param communitySections
	 * @return
	 */
	List<Long> getVisibleSectionsForUser(Long communityId, Long currentUserId, List<CommunitySectionDomain> communitySections);

	/**
	 * Получить список счетов объединения
	 * @param communityId
	 * @return
	 */
	List<Account> getCommunityAccounts(Long communityId);
}