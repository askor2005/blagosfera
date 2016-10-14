package ru.radom.kabinet.utils;

/**
 * Ключи настроек системы
 * Created by vgusev on 18.05.2016.
 */
public interface SystemSettingsConstants {

    /**
     * Участники голосования\собрания должны быть сертифицированны
     * Значение true\false
     */
    String VOTING_VOTERS_NEED_BE_VERIFIED = "voting.voters.need.verified";

    /**
     * Участник документа, который должен его подписывать должен быть сертифицированн
     * Значение true\false
     */
    String DOCUMENT_SIGNER_NEED_BE_VERIFIED = "document.signer.need.verified";

    /**
     * json с настройками списка статических текстов для подсказок на странице конструктора собраний
     */
    String VOTING_TEMPLATE_HELP_TEXTS = "voting.template.help.texts";
}
