package ru.radom.kabinet.services.sharer;

import ru.askor.blagosfera.domain.user.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Интерфейс сервиса, заполняющего Model sharer'а
 */
@Deprecated
public interface SharerModelService {

    /**
     * Заполняет Map стандартной модели для sharer'а.
     * В него входят все данные, обрабатывающиеся и отображающиеся на каждой странице залогиненного sharer'a
     * @param user
     * @param request
     */
    Map<String, Object> fillMapForStandardModel(User user, HttpServletRequest request);

}
