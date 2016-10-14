package ru.radom.kabinet.voting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.askor.voting.business.services.BatchVotingService;
import ru.askor.voting.business.util.BatchVotingBehavior;
import ru.askor.voting.domain.*;
import ru.askor.voting.domain.exception.VotingSystemException;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.utils.VarUtils;

import javax.annotation.PostConstruct;

/**
 * Класс - поведение собрания по созданию КУч - второй этап.
 * Этап содержит:
 * - голосование за председателя собрания
 * - голосование за секретаря собрания
 * - голосование за повестку дня
 * - голосование за председателя КУч
 * - голосование за ревизора КУч
 * Created by vgusev on 24.08.2015.
 */
@Component("cooperativeSecondPlotBatchVoting")
public class CooperativeSecondPlotBatchVoting implements BatchVotingBehavior {

    public static final String NAME = "cooperativeSecondPlotBatchVoting";

    @Autowired
    BatchVotingService batchVotingServiceInstance;

    @Autowired
    SharerDao sharerDaoInstance;

    private static BatchVotingService batchVotingService;

    private static SharerDao sharerDao;

    // Индекс голосованиея за выбор председателя собрания
    public static final int VOTING_FOR_PRESIDENT_OF_MEETING_INDEX = 0;

    // Индекс голосованиея за выбор секретаря собрания
    public static final int VOTING_FOR_SECRETARY_OF_MEETING_INDEX = 1;

    // Индекс голосованиея за повестку дня собрания
    public static final int VOTING_FOR_AGENDA_OF_MEETING_INDEX = 2;

    // Индекс голосованиея за выбор председателя КУЧ
    public static final int VOTING_FOR_PRESIDENT_OF_SOCIAL_COMMUNITY_INDEX = 3;

    // Индекс голосованиея за выбор ревизора КУЧ
    public static final int VOTING_FOR_REVISOR_OF_SOCIAL_COMMUNITY_INDEX = 4;

    public CooperativeSecondPlotBatchVoting() {}

    @PostConstruct
    public void init() {
        batchVotingServiceInstance.registerBatchVotingBehavior(NAME, this);
        if (batchVotingService == null && batchVotingServiceInstance != null) {
            batchVotingService = batchVotingServiceInstance;
        }
        if (sharerDao == null && sharerDaoInstance != null) {
            sharerDao = sharerDaoInstance;
        }
    }

    @Override
    public boolean shouldFinishBatchVoting(Voting voting) {
        boolean valid = voting.getResult().getResultType() == VotingResultType.VALID;
        boolean deadHeat = voting.getResult().getResultType() == VotingResultType.INVALID_DEAD_HEAT;
        boolean finishedManually = voting.getAdditionalData().get(AdditionalDataConstants.FINISHED_MANUALLY_KEY) != null;
        boolean isContra = false;

        if (valid
                && (voting.getParameters().getVotingType() == VotingType.PRO_CONTRA)
                && (voting.getVotingItems().get(0).getValue().equals("CONTRA"))){
            isContra = true;
        }

        return (valid && isContra) || (!valid && (!deadHeat || finishedManually));
    }

    @Override
    public boolean shouldFinishBatchVotingIfCanNotRestartVoting(Voting voting) {
        return true;
    }

    @Override
    public boolean shouldUpdateVoting(Voting voting) throws VotingSystemException {
        return false;
    }

    @Override
    public boolean notifyAboutFinishedVotings(Voting voting) {
        return voting.getResult().getResultType() == VotingResultType.VALID;
    }

    @Override
    public boolean shouldUpdateBatchVoting(BatchVoting batchVoting, Voting voting) throws VotingSystemException {
        boolean batchVotingUpdated = false;

        switch (voting.getIndex().intValue()) {

            case VOTING_FOR_PRESIDENT_OF_MEETING_INDEX: { // Голосование за председателя собрания
                try {
                    removeCandidate(batchVoting.getVotings().get(VOTING_FOR_SECRETARY_OF_MEETING_INDEX),
                            batchVoting.getVotings().get(VOTING_FOR_PRESIDENT_OF_MEETING_INDEX).getVotingItems().get(0).getValue());

                    // Обновляем голосование за повестку дня, потому как её оглашает выбранный председатель собрания
                    String presidentOfMeetingIdStr = voting.getVotingItems().get(0).getValue();
                    Long presidentOfMeetingId = VarUtils.getLong(presidentOfMeetingIdStr, -1l);
                    UserEntity presidentOfMeeting = sharerDao.getById(presidentOfMeetingId);

                    Voting votingForAgenta = batchVoting.getVotings().get(VOTING_FOR_AGENDA_OF_MEETING_INDEX);
                    votingForAgenta.getAdditionalData().put(BatchVotingConstants.COOPERATIVE_AGENTA_CREATOR, presidentOfMeetingIdStr);

                    String genderString = presidentOfMeeting.getSex() ? "предложил" : "предложила";

                    String description = votingForAgenta.getAdditionalData().get(BatchVotingConstants.VOTING_DESCRIPTION);
                    description = description.replaceAll(BatchVotingConstants.PRESIDENT_OF_MEETING_TEMPLATE, presidentOfMeeting.getFullName() + " " + genderString);
                    votingForAgenta.getAdditionalData().put(BatchVotingConstants.VOTING_DESCRIPTION, description);

                    batchVotingUpdated = true;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    throw new VotingSystemException("candidate.id.incorrect");
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new VotingSystemException(e.getMessage());
                }
                break;
            }
            case VOTING_FOR_AGENDA_OF_MEETING_INDEX: { // Голосование за повестку дня
                // Выставляем параметры видимости для голосований за председателя КУч и ревизора КУч
                Voting votingToUpdate = batchVoting.getVotings().get(VOTING_FOR_PRESIDENT_OF_SOCIAL_COMMUNITY_INDEX);
                votingToUpdate.setVisible(true);

                votingToUpdate = batchVoting.getVotings().get(VOTING_FOR_REVISOR_OF_SOCIAL_COMMUNITY_INDEX);
                votingToUpdate.setVisible(true);

                batchVotingUpdated = true;
                break;
            }
            case VOTING_FOR_PRESIDENT_OF_SOCIAL_COMMUNITY_INDEX: {
                try {
                    removeCandidate(batchVoting.getVotings().get(VOTING_FOR_REVISOR_OF_SOCIAL_COMMUNITY_INDEX),
                            batchVoting.getVotings().get(VOTING_FOR_PRESIDENT_OF_SOCIAL_COMMUNITY_INDEX).getVotingItems().get(0).getValue());
                    batchVotingUpdated = true;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    throw new VotingSystemException("candidate.id.incorrect");
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new VotingSystemException(e.getMessage());
                }
                break;
            }
        }

        return batchVotingUpdated;
    }

    @Override
    public boolean shouldRestartVoting(Voting voting) {
        boolean deadHeat = voting.getResult().getResultType() == VotingResultType.INVALID_DEAD_HEAT;
        boolean finishedManually = voting.getAdditionalData().get(AdditionalDataConstants.FINISHED_MANUALLY_KEY) != null;
        return deadHeat && !finishedManually;
    }

    @Override
    public VotingState restartInState(Voting voting) {
        return VotingState.PAUSED;
    }

    private void removeCandidate(Voting voting, String candidateId) {
        voting.getParameters().getCandidatesAllowed().remove(Long.valueOf(candidateId));
        int count = voting.getVotingItems().size();

        for (int i = count - 1; i > -1; i--) {
            if (voting.getVotingItems().get(i).getValue().equals(candidateId)) {
                voting.getVotingItems().remove(i);
            }
        }
    }
}
