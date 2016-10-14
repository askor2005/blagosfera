package ru.radom.blagosferabp.activiti.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.radom.blagosferabp.activiti.model.StencilEntity;

/**
 * Created by Otts Alexey on 03.11.2015.<br/>
 * DAO для {@link StencilEntity}
 */
public interface StencilDAO extends JpaRepository<StencilEntity, String> {
}
