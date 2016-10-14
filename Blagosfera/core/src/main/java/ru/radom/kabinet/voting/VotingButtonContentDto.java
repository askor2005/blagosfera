package ru.radom.kabinet.voting;

import lombok.Data;

/**
 * Обёртка для данных кнопки при клике на которую отображается доп. информация на странице голосования
 * Created by vgusev on 07.01.2016.
 */
@Data
public class VotingButtonContentDto {

    /**
     * Текст на кнопке
     */
    private String buttonText;

    /**
     * Контент в модальном окне
     */
    private String content;
}
