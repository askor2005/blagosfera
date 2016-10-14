package ru.radom.kabinet.web.voting;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.askor.blagosfera.domain.user.User;
import ru.askor.voting.business.services.BatchVotingService;
import ru.askor.voting.business.services.VotingService;
import ru.askor.voting.domain.*;
import ru.askor.voting.domain.exception.VotingSystemException;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dto.CommonResponseDto;
import ru.radom.kabinet.dto.SuccessResponseDto;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.chat.DialogEntity;
import ru.radom.kabinet.model.votingtemplate.VotingAttributeTemplate;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.ChatService;
import ru.radom.kabinet.services.batchVoting.VotingPageService;
import ru.radom.kabinet.services.batchVoting.dto.VotingPageCountVotersDto;
import ru.radom.kabinet.services.sharer.UserDataService;
import ru.radom.kabinet.utils.CommonConstants;
import ru.radom.kabinet.utils.VarUtils;
import ru.radom.kabinet.voting.BatchVotingConstants;
import ru.radom.kabinet.voting.settings.RegistrationInBatchVotingSettings;
import ru.radom.kabinet.web.user.dto.UserDataDto;
import ru.radom.kabinet.web.voting.dto.VoteItemProtocolDto;
import ru.radom.kabinet.web.voting.dto.VoteItemsListDto;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис для работы со страницей голосования
 * Created by vgusev on 02.08.2015.
 */
@Controller("votingPageController")
public class VotingPageController {

    private final static Logger logger = LoggerFactory.getLogger(VotingPageController.class);

    @Autowired
    private VotingService votingService;

    @Autowired
    private BatchVotingService batchVotingService;

    @Autowired
    private SharerDao sharerDao;

    @Autowired
    private ChatService chatService;

    @Autowired
    private VotingPageService votingPageService;

    @Autowired
    private UserDataService userDataService;

    /**
     * Ссылка на собрание
     * Редиректим на первое голосование
     * @param model
     * @param batchVotingId
     * @return
     */
    @RequestMapping(value = "/votingsystem/batchVotingPage", method = RequestMethod.GET)
    public String getBatchVotingPage(Model model, @RequestParam(value = "batchVotingId", required = true) Long batchVotingId) {
        BatchVoting batchVoting = null;
        try {
            batchVoting = batchVotingService.getBatchVoting(batchVotingId, true, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String result;
        if (batchVoting == null || batchVoting.getVotings() == null || batchVoting.getVotings().isEmpty()) {
            result = "error404";
        } else {
            if (BatchVotingState.VOTERS_REGISTRATION.equals(batchVoting.getState())) {
                result = "redirect:/votingsystem/registrationInVoting.html?batchVotingId=" + batchVoting.getId();
            } else {
                result = "redirect:/votingsystem/votingPage.html?votingId=" + batchVoting.getVotings().get(0).getId();
            }
        }
        return result;
    }

    /**
     * Страница голосования
     * @param model модель
     * @param votingId ИД голосования
     * @return view
     */
    @RequestMapping(value = "/votingsystem/votingPage.html", method = RequestMethod.GET)
    public String getVotingPage(Model model, @RequestParam(value = "votingId") Long votingId) {
        Voting voting = null;
        try {
            voting = votingService.getVoting(votingId, true, true);
            model.addAttribute("isCurrentSharerCanRestartVoting", votingPageService.isCurrentSharerCanRestartVoting(SecurityUtils.getUser().getId(), voting));
        } catch (VotingSystemException e) {
            model.addAttribute("errorMessage", e.getMessage());
            logger.error(e.getMessage());
        }
        if (voting != null) {
            switch (voting.getState()) {
                case NEW:
                    model.addAttribute("errorMessage", "Голосование ещё не начато!");
                    break;
                case ACTIVE:
                    break;
                case FINISHED:
                    model.addAttribute("errorMessage", "Голосование уже закончено!");
                    break;
                case PAUSED:
                    // Голосование останавливается при ничьей
                    // В таком случае показываем протокол голосования и пробуем перезапустить его
                    //model.addAttribute("errorMessage", "Голосование приостановлено!");
                    break;
            }
            switch (voting.getParameters().getVotingType()) {
                case PRO_CONTRA:
                    break;
                case CANDIDATE:
                    List<Long> candidatesIds = new ArrayList<>();
                    for (VotingItem votingItem: voting.getVotingItems()) {
                        Long sharerId = VarUtils.getLong(votingItem.getValue(), -1l);
                        if (sharerId > -1) {
                            candidatesIds.add(sharerId);
                        }
                    }
                    setSharersInModel(candidatesIds, model, "candidates");
                    break;
                case INTERVIEW:
                    break;
                case MULTIPLE_SELECTION:
                    break;
                case SINGLE_SELECTION:
                    break;
            }
            // Участники голосования
            setSharersInModel(voting.getParameters().getVotersAllowed(), model, "voters");

            // Участники голосования, кто проголосовал
            List<Long> votersWhoVotes = new ArrayList<>();
            for (VotingItem votingItem : voting.getVotingItems()) {
                votersWhoVotes.addAll(votingItem.getVotes().stream().map(Vote::getOwnerId).collect(Collectors.toList()));
            }
            // Сдвиг времени от кринвича у сервера
            /*TimeZone timezone = TimeZone.getDefault();
            int timeZoneOffset = timezone.getOffset(new Date().getTime());*/

            model.addAttribute("votersWhoVotes", votersWhoVotes);
            model.addAttribute("votingId", votingId);
            model.addAttribute("votingStartDate", voting.getParameters().getStartDate().toString());
            model.addAttribute("votingEndDate", voting.getParameters().getEndDate().toString());
            //model.addAttribute("timeZoneOffset", timeZoneOffset);

            // Голосование закрытое
            boolean isClosedVoting = voting.getParameters().isSecretVoting();
            model.addAttribute("isClosedVoting", isClosedVoting);

            // Ишем пачку голосований по еденичному голосованию
            BatchVoting batchVoting;
            try {
                batchVoting = batchVotingService.getBatchVotingByVotingId(votingId, true, true);
                if (batchVoting != null) {
                    model.addAttribute("batchVotingId", batchVoting.getId());
                    model.addAttribute("batchVotingEndDate", batchVoting.getParameters().getEndDate().toString());
                    // Цели и задачи
                    String meetingTargets = batchVoting.getAdditionalData().get(BatchVotingConstants.BATCH_VOTING_TARGETS_ATTR_NAME);
                    model.addAttribute("meetingTargets", meetingTargets);
                    String additionalMeetingTargets = batchVoting.getAdditionalData().get(BatchVotingConstants.ADDITIONAL_MEETING_TARGETS_ATTR_NAME);
                    model.addAttribute("additionalMeetingTargets", additionalMeetingTargets);

                    // Наименование подробностей собрания
                    String batchVotingDescription = batchVoting.getAdditionalData().get(BatchVotingConstants.BATCH_VOTING_DESCRIPTION_ATTR_NAME);
                    model.addAttribute("batchVotingDescription", batchVotingDescription == null || batchVotingDescription.equals("") ? "Подробности собрания" : batchVotingDescription);

                    // Чат собрания
                    Long dialogId = VarUtils.getLong(batchVoting.getAdditionalData().get(BatchVotingConstants.BATCH_VOTING_DIALOG_ID_ATTR_NAME), -1l);
                    DialogEntity dialog = null;
                    if (dialogId > -1l) {
                        dialog = chatService.getDialog(dialogId, SecurityUtils.getUser().getId());
                    }
                    model.addAttribute("dialog", dialog);
                }
            } catch (Exception e) {
                model.addAttribute("errorMessage", e.getMessage());
                logger.error(e.getMessage());
            }
        }
        //model.addAttribute("nowDate", new Date().getTime());
        model.addAttribute("baseLink", "/votingsystem/votingPage.html?votingId=");

        Map<String, String> votingTypeNames = new HashMap<>();
        votingTypeNames.put("PRO_CONTRA", VotingType.PRO_CONTRA.name());
        votingTypeNames.put("CANDIDATE", VotingType.CANDIDATE.name());
        votingTypeNames.put("MULTIPLE_SELECTION", VotingType.MULTIPLE_SELECTION.name());
        votingTypeNames.put("SINGLE_SELECTION", VotingType.SINGLE_SELECTION.name());
        votingTypeNames.put("INTERVIEW", VotingType.INTERVIEW.name());

        model.addAttribute("votingTypes", votingTypeNames);

        model.addAttribute("useBiometricIdentification", voting.getAdditionalData().containsKey(VotingAttributeTemplate.USE_BIOMETRIC_IDENTIFICATION));
        model.addAttribute("skipResults", voting.getAdditionalData().containsKey(VotingAttributeTemplate.SKIP_RESULTS));

        // Проверяем может ли текущий пользователь перезпустить голосование или удалить собрание

        return "votingPage";
    }

    /**
     * Получить данные по участникам кто проголосовал
     * @param votingId ИД голосования
     * @return ИДы участников
     * @throws Exception
     */
    @RequestMapping(value = "/votingsystem/getVotersWhoVotes.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public Set<Long> getVotersWhoVotes(@RequestParam(value = "votingId", required = true) Long votingId) throws Exception {
        return votingPageService.getVotersWhoVotes(votingId);
    }

    /**
     * Установить список пользователей системы как параметр модели.
     * @param sharerIds ИДы участников
     * @param model модель
     * @param attrName наименование атрибута
     */
    private void setSharersInModel(Collection<Long> sharerIds, Model model, String attrName) {
        List<UserEntity> result = new ArrayList<>();
        for (Long sharerId : sharerIds) {
            result.add(sharerDao.getById(sharerId));
        }
        model.addAttribute(attrName, result);
    }

    /**
     * Страница регистрации в собрании
     * @param model модель
     * @param batchVotingId ИД собрания
     * @return view
     */
    @RequestMapping(value = "/votingsystem/registrationInVoting.html", method = RequestMethod.GET)
    public String registrationInBatchVoting(Model model, @RequestParam(value = "batchVotingId") Long batchVotingId){
        try {
            BatchVoting batchVoting = batchVotingService.getBatchVoting(batchVotingId, true, true);
            UserEntity userEntity = sharerDao.getById(batchVoting.getOwnerId());

            String description = batchVoting.getAdditionalData().get(BatchVotingConstants.MEETING_REGISTRATION_DESCRIPTION);
            model.addAttribute("description", description);

            // Цели и задачи собрания
            String meetingTargets = batchVoting.getAdditionalData().get(BatchVotingConstants.BATCH_VOTING_TARGETS_ATTR_NAME);
            model.addAttribute("meetingTargets", meetingTargets);
            String additionalMeetingTargets = batchVoting.getAdditionalData().get(BatchVotingConstants.ADDITIONAL_MEETING_TARGETS_ATTR_NAME);
            model.addAttribute("additionalMeetingTargets", additionalMeetingTargets);

            // Наименование подробностей собрания
            String batchVotingDescription = batchVoting.getAdditionalData().get(BatchVotingConstants.BATCH_VOTING_DESCRIPTION_ATTR_NAME);
            model.addAttribute("batchVotingDescription", batchVotingDescription == null || batchVotingDescription.equals("") ? "Подробности собрания" : batchVotingDescription);

            // Тема собрания
            model.addAttribute("subject", batchVoting.getSubject());

            // Чат собрания
            Long dialogId = VarUtils.getLong(batchVoting.getAdditionalData().get(BatchVotingConstants.BATCH_VOTING_DIALOG_ID_ATTR_NAME), -1l);
            DialogEntity dialog = null;
            if (dialogId > -1l) {
                dialog = chatService.getDialog(dialogId, SecurityUtils.getUser().getId());
            }
            model.addAttribute("dialog", dialog);

            int countRegisteredVoters = 0;
            List<UserEntity> registeredVoters = votingPageService.getRegisteredSharers(SecurityUtils.getUser().getId(), batchVotingId);
            if (registeredVoters != null) {
                countRegisteredVoters = registeredVoters.size();
            }
            int countNotRegisteredVoters = 0;
            List<UserEntity> notRegisteredVoters = votingPageService.getNotRegisteredSharers(SecurityUtils.getUser().getId(), batchVotingId);
            if (notRegisteredVoters != null) {
                countNotRegisteredVoters = notRegisteredVoters.size();
            }

            model.addAttribute("countRegisteredVoters", countRegisteredVoters);
            model.addAttribute("countNotRegisteredVoters", countNotRegisteredVoters);
            //model.addAttribute("nowDate", DateUtils.toDate(LocalDateTime.now()).getTime());
            model.addAttribute("votersRegistrationEndDate", batchVoting.getParameters().getVotersRegistrationEndDate().toString());

            model.addAttribute("owner", userEntity);
            model.addAttribute("currentSharer", SecurityUtils.getUser());
            model.addAttribute("batchVoting", batchVoting);

            Map<String, Object> templateData = new HashMap<>();
            templateData.put("batchVoting", batchVoting);
            String quorumInPercent = batchVoting.getAdditionalData().get(BatchVotingConstants.QUORUM_PERCENT_ATTR_NAME);
            templateData.put("quorumInPercent", quorumInPercent);

            // Формируем текст описания в регистрации собрания
            MustacheFactory mf = new DefaultMustacheFactory();
            Mustache mustache = mf.compile(new StringReader(RegistrationInBatchVotingSettings.getInstance().getRegistrationCommonDescription()), "registrationCommonDescription");
            Writer writer = new StringWriter();
            mustache.execute(writer, templateData);
            String registrationCommonDescription = writer.toString();

            model.addAttribute("registrationCommonDescription", registrationCommonDescription);
            model.addAttribute("registrationSharerText", RegistrationInBatchVotingSettings.getInstance().getRegistrationSharerText());

            model.addAttribute("useBiometricIdentificationInRegistration", batchVoting.getAdditionalData().containsKey(VotingAttributeTemplate.USE_BIOMETRIC_IDENTIFICATION + "_IN_REGISTRATION"));
        } catch (Exception e) {
            logger.error(e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "registrationInVoting";
    }

    /**
     * Список участников, которые уже зарегистрировались
     * @param batchVotingId ИД собрания
     * @return json
     */
    @RequestMapping(value = "/votingsystem/getRegisteredVoters.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public List<UserDataDto> getRegisteredVoters(@RequestParam(value = "batchVotingId", required = true) Long batchVotingId,
                                           @RequestParam(value = "firstIndex", required = true) Integer firstIndex,
                                           @RequestParam(value = "count", required = false) Integer count) throws Exception {
        List<UserEntity> registeredUserEntities = votingPageService.getRegisteredVoters(SecurityUtils.getUser().getId(), batchVotingId, firstIndex, count);
        List<User> registeredUser = UserEntity.toDomainList(registeredUserEntities);
        return UserDataDto.toDtoList(registeredUser);
    }

    /**
     * Список участников, которые ещё не зарегистрировались
     * @param batchVotingId ИД собрания
     * @return json
     */
    @RequestMapping(value = "/votingsystem/getNotRegisteredVoters.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public List<UserDataDto> getNotRegisteredVoters(@RequestParam(value = "batchVotingId", required = true) Long batchVotingId,
                                                       @RequestParam(value = "firstIndex", required = true) Integer firstIndex,
                                                       @RequestParam(value = "count", required = false) Integer count) throws Exception {
        List<UserEntity> notRegisteredUserEntities = votingPageService.getNotRegisteredVoters(SecurityUtils.getUser().getId(), batchVotingId, firstIndex, count);
        List<User> notRegisteredUser = UserEntity.toDomainList(notRegisteredUserEntities);
        return UserDataDto.toDtoList(notRegisteredUser);
    }


    /**
     * Получить количество участников
     * @param batchVotingId ИД собрания
     * @return json
     */
    @RequestMapping(value = "/votingsystem/getCountVoters.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public VotingPageCountVotersDto getCountVoters(@RequestParam(value = "batchVotingId", required = true) Long batchVotingId) throws Exception {
        return votingPageService.getCountVoters(SecurityUtils.getUser(), batchVotingId);
    }

    /**
     * Поторопить участника регистрации в собрании.
     * @param batchVotingId ИД собрания
     * @param voterId ИД участника
     * @return json
     */
    @RequestMapping(value = "/votingsystem/hurryVoter.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CommonResponseDto hurryVoter(@RequestParam(value = "batchVotingId", required = true) Long batchVotingId,
                                           @RequestParam(value = "voterId", required = true) Long voterId) throws Exception {
        votingPageService.hurryVoter(SecurityUtils.getUser(), batchVotingId, voterId);
        return SuccessResponseDto.get();
    }

    /**
     * Поторопить участника в голосовании
     * @param votingId ИД голосования
     * @param voterId ИД участника
     * @return json
     */
    @RequestMapping(value = "/votingsystem/hurryVoterInVoting.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CommonResponseDto hurryVoterInVoting(@RequestParam(value = "votingId", required = true) Long votingId,
                                           @RequestParam(value = "voterId", required = true) Long voterId) throws Exception {
        votingPageService.hurryVoterInVoting(SecurityUtils.getUser(), votingId, voterId);
        return SuccessResponseDto.get();
    }

    /**
     * Получить данные по голосованию
     * @param votingItemId ИД варианта голосования
     * @param votingId ИД голосования
     * @param page номер страницы
     * @param start индекс первого элемента
     * @param limit количество элементов
     * @return json
     */
    @RequestMapping(value = "/votingsystem/getVotingProtocol.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public VoteItemsListDto getVotingProtocol(@RequestParam(value = "votingItemId", required = true) Long votingItemId,
                                              @RequestParam(value = "votingId", required = true) Long votingId,
                                              @RequestParam(value = "page", required = false) int page,
                                              @RequestParam(value = "start", required = false) int start,
                                              @RequestParam(value = "limit", required = false) int limit) {
        VoteItemsListDto result;
        try {
            // Делаем поправку времени с учетом таймзоны
            //TimeZone timeZone = TimeZone.getDefault();
            //int timeZoneOffset = timeZone.getOffset(new Date().getTime()); // милисекунды

            int countVotes = 0;
            List<VoteItemProtocolDto> items = new ArrayList<>();

            Voting voting = votingService.getVoting(votingId, true, true);
            // Если голосование закрытое, то данные предоставлять не нужно
            if (voting != null && voting.getParameters().isSecretVoting()) {
                // do nothing
            } else {
                VotingItem votingItem = votingService.getVotingItem(votingItemId, true);

                for (int i=0; i<votingItem.getVotes().size(); i++) {
                    if (start > i) {
                        continue;
                    }
                    Vote vote = votingItem.getVotes().get(i);

                    VoteItemProtocolDto votingProtocolDto = new VoteItemProtocolDto(
                            vote,
                            userDataService.getByIdMinData(vote.getOwnerId()).getName()/*,
                            timeZoneOffset*/
                    );
                    items.add(votingProtocolDto);
                    if (items.size() == limit) {
                        break;
                    }
                }
                countVotes = votingItem.getVotes().size();
            }

            result = VoteItemsListDto.successDto(countVotes, items);
        } catch (Exception e) {
            logger.error(e.getMessage());
            result = VoteItemsListDto.errorDto();
        }
        return result;
    }

    /**
     * Перезапустить голосование
     * @param votingId ИД голосования
     * @return json
     */
    @RequestMapping(value = "/votingsystem/restartVoting.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CommonResponseDto restartVoting(@RequestParam(value = "votingId", required = true) Long votingId) throws Exception {
        votingPageService.restartVoting(SecurityUtils.getUser().getId(), votingId);
        return SuccessResponseDto.get();
    }

    /**
     * Завершить голосование и собрание
     * @param votingId ИД голосования
     * @return json
     */
    @RequestMapping(value = "/votingsystem/finishVoting.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public CommonResponseDto finishVoting(@RequestParam(value = "votingId", required = true) Long votingId) throws Exception {
        votingPageService.finishVoting(SecurityUtils.getUser().getId(), votingId);
        return SuccessResponseDto.get();
    }

    /**
     * Страница с результатами собрания
     * @param model
     * @param batchVotingId
     * @return
     */
    @RequestMapping(value = "/votingsystem/batchVotingResult.html", method = RequestMethod.GET)
    public String batchVotingResultPage(Model model, @RequestParam(value = "batchVotingId", required = true) Long batchVotingId){
        return "batchVotingResultPage";
    }

}