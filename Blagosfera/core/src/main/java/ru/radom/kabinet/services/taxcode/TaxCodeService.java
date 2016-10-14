package ru.radom.kabinet.services.taxcode;

import org.springframework.stereotype.Service;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.user.User;

/**
 * Сервис для получения кода налоговой по адресу
 * Created by vgusev on 16.02.2016.
 */
@Service
public interface TaxCodeService {

    /**
     * Получить код по строке адреса
     * @param region код региона
     * @param house номер дома
     * @param addressString строка адреса
     * @return код налоговой
     */
    String getCodeByAddress(String region, String house, String addressString);

    /**
     * Получить код по адресу регистрации пользователя
     * @param sharer пользователь
     * @return код налоговой
     */
    String getCodeBySharer(User sharer);

    /**
     * Получить код по адресу регистрации пользователя используя ИД
     * @param sharerId ИД пользователя
     * @return код налоговой
     */
    String getCodeBySharerId(Long sharerId);

    /**
     * Получить код по юр адресу юр лица
     * @param community юр лицо
     * @return код налоговой
     */
    String getCodeByCommunity(Community community);

    /**
     * Получить код по юр адресу юр лица используя ИД
     * @param communityId ИД юр лица
     * @return код налоговой
     */
    String getCodeByCommunityId(Long communityId);
}
