package ru.radom.kabinet.module.blagosfera.bp.service;

import ru.radom.kabinet.module.blagosfera.bp.model.BPModel;

/**
 * Created by Otts Alexey on 29.10.2015.<br/>
 * Сервис для работы с моделью бизнес процесса
 */
public interface BPModelService {

    /**
     * Создать модель с именем {@code name}
     * @param name    имя модели
     */
    BPModel create(String name);

    /**
     * Скопировать модель
     */
    BPModel copy(BPModel model);

    /**
     * Удалить модель
     * @param modelId    модель
     */
    void delete(Long modelId);

    /**
     * Обновить данные JSON модели
     */
    BPModel updateModelData(Long modelId, String data);
}
