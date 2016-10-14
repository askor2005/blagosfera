package ru.radom.kabinet.voting.settings;

import lombok.Data;

/**
 *
 * Created by vgusev on 26.05.2016.
 */
@Data
public class ConstructorBatchVotingSettings {

    private static final String DEFAULT_VOTERS_PARTICIPANT_NAME = "Список участников собрания";

    private static final String DEFAULT_VOTER_WHO_SIGN_PROTOCOL_PARTICIPANT_NAME = "Подписанты протокола";

    private static final String DEFAULT_COMMUNITY_PARTICIPANT_NAME = "Объединение";

    private static final String DEFAULT_TEMPLATE_CODE_WITH_PRESIDENT_VOTING = "test_protocol_batch_voting";

    private static final String DEFAULT_TEMPLATE_CODE_WITH_PRESIDENT_VOTING_WITHOUT_USERS_SIGNER = "test_protocol_batch_voting_without_users_singer";

    private static final String DEFAULT_TEMPLATE_CODE_WITHOUT_PRESIDENT_VOTING = "test_protocol_batch_voting_without_president";

    private static final String DEFAULT_PROTOCOL_USER_FIELD_NAME = "Протокол";

    private static final String DEFAULT_PRESIDENT_VOTING_PARTICIPANT_NAME = "Председатель собрания";

    private static final String DEFAULT_SECRETARY_VOTING_PARTICIPANT_NAME = "Секретарь собрания";

    private static final String DEFAULT_CHAT_FIELD_USER_NAME = "Чат";

    private String templateCode;

    private String templateCodeWithoutUsersSigner;

    private String templateCodeWithoutPresidentVoting;

    private String communityParticipantName;

    private String voterWhoSignProtocolParticipantName;

    private String votersParticipantName;

    private String protocolUserFieldName;

    private String presidentVotingParticipantName;

    private String secretaryVotingParticipantName;

	private String chatUserFieldName;

    private static final ConstructorBatchVotingSettings defaultSettings = new ConstructorBatchVotingSettings(
            DEFAULT_TEMPLATE_CODE_WITH_PRESIDENT_VOTING,
            DEFAULT_TEMPLATE_CODE_WITH_PRESIDENT_VOTING_WITHOUT_USERS_SIGNER,
            DEFAULT_TEMPLATE_CODE_WITHOUT_PRESIDENT_VOTING,
            DEFAULT_COMMUNITY_PARTICIPANT_NAME,
            DEFAULT_VOTERS_PARTICIPANT_NAME,
            DEFAULT_VOTER_WHO_SIGN_PROTOCOL_PARTICIPANT_NAME,
            DEFAULT_PROTOCOL_USER_FIELD_NAME,
            DEFAULT_PRESIDENT_VOTING_PARTICIPANT_NAME,
            DEFAULT_SECRETARY_VOTING_PARTICIPANT_NAME,
            DEFAULT_CHAT_FIELD_USER_NAME);

    public ConstructorBatchVotingSettings() {}

    public ConstructorBatchVotingSettings(
            String templateCode, String templateCodeWithoutUsersSigner, String templateCodeWithoutPresidentVoting, String communityParticipantName,
            String votersParticipantName, String voterWhoSignProtocolParticipantName,
            String protocolUserFieldName, String presidentVotingParticipantName, String secretaryVotingParticipantName, String chatUserFieldName) {
        this.templateCode = templateCode;
        this.templateCodeWithoutUsersSigner = templateCodeWithoutUsersSigner;
        this.templateCodeWithoutPresidentVoting = templateCodeWithoutPresidentVoting;
        this.communityParticipantName = communityParticipantName;
        this.votersParticipantName = votersParticipantName;
        this.voterWhoSignProtocolParticipantName = voterWhoSignProtocolParticipantName;
        this.protocolUserFieldName = protocolUserFieldName;
        this.presidentVotingParticipantName = presidentVotingParticipantName;
        this.secretaryVotingParticipantName = secretaryVotingParticipantName;

        this.chatUserFieldName = chatUserFieldName;
    }

    public static ConstructorBatchVotingSettings getDefaultSettings(){
        return defaultSettings;
    }

}
