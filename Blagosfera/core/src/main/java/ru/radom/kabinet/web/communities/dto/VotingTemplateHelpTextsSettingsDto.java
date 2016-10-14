package ru.radom.kabinet.web.communities.dto;

import lombok.Data;

/**
 *
 * Created by vgusev on 21.06.2016.
 */
@Data
public class VotingTemplateHelpTextsSettingsDto {

    private static final String DEFAULT_SENTENCE_HELP_TEXT_CODE = "voting.sentence.help";

    private static final String DEFAULT_SUCCESS_DECREE_HELP_TEXT_CODE = "voting.success.decree.help.text";

    private static final String DEFAULT_FAIL_DECREE_HELP_TEXT_CODE = "voting.fail.decree.help.text";

    private String votingSentenceHelpTextCode;

    private String votingSuccessDecreeHelpTextCode;

    private String votingFailDecreeHelpTextCode;

    public static final VotingTemplateHelpTextsSettingsDto DEFAULT_INSTANCE = new VotingTemplateHelpTextsSettingsDto(
            DEFAULT_SENTENCE_HELP_TEXT_CODE,
            DEFAULT_SUCCESS_DECREE_HELP_TEXT_CODE,
            DEFAULT_FAIL_DECREE_HELP_TEXT_CODE
    );

    public VotingTemplateHelpTextsSettingsDto() {}

    public VotingTemplateHelpTextsSettingsDto(String votingSentenceHelpTextCode, String votingSuccessDecreeHelpTextCode,
                                              String votingFailDecreeHelpTextCode) {
        this.votingSentenceHelpTextCode = votingSentenceHelpTextCode;
        this.votingSuccessDecreeHelpTextCode = votingSuccessDecreeHelpTextCode;
        this.votingFailDecreeHelpTextCode = votingFailDecreeHelpTextCode;
    }
}
