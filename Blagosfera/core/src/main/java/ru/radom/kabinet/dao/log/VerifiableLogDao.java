package ru.radom.kabinet.dao.log;

import org.hibernate.criterion.Order;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.log.VerifiableLog;
import ru.radom.kabinet.utils.MurmurHash;

/**
 * Created by ebelyaev on 05.08.2015.
 */
public abstract class VerifiableLogDao<E extends VerifiableLog> extends Dao<E> {

    @Override
    public void saveOrUpdate(E entity) {
        if(entity != null && entity.getId() !=null) {
            throw new RuntimeException("Нельзя обновить сущность, т.к. все другие сущности завязанные на хэшэ этой сущности станут невалидными.");
        }

        E lastEntity = findFirst(Order.desc("id"));
        String lastHash = (lastEntity!= null) ? lastEntity.getHash() : "";

        super.saveOrUpdate(entity);

        String hash = String.valueOf(MurmurHash.hash64(entity.getStringFromFields()));
        String newHash = String.valueOf(MurmurHash.hash64(lastHash + hash));
        entity.setHash(newHash);

        super.saveOrUpdate(entity);
    }
}

