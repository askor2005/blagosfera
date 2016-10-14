package ru.radom.kabinet.security.context;

/**
 * сервис облегчающий работу с данными в контексте сессии
 */
public interface SessionContext {

    /**
     * Позволяет установить сгенерированный системой пароль
     * @return
     */
    String getGeneratedPassword();

    /**
     * Позволяет получить сгенерированный системой пароль
     * @param generatedPassword
     */
    void setGeneratedPassword(String generatedPassword);

}
