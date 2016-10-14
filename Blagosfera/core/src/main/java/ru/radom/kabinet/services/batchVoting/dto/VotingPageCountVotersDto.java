package ru.radom.kabinet.services.batchVoting.dto;

import lombok.Data;

/**
 *
 * Created by vgusev on 22.12.2015.
 */
@Data
public class VotingPageCountVotersDto {
    /**
     * Количество зарегистрированных пользователей в собрании
     */
    private int countRegisteredVoters;

    /**
     * Количество не зарегистрированных пользователей в собрании
     */
    private int countNotRegisteredVoters;

    public VotingPageCountVotersDto() {
    }

    public VotingPageCountVotersDto(int countRegisteredVoters, int countNotRegisteredVoters) {
        this.countRegisteredVoters = countRegisteredVoters;
        this.countNotRegisteredVoters = countNotRegisteredVoters;
    }
}
