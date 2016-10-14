package ru.radom.kabinet.dao.account;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.askor.blagosfera.data.jpa.entities.account.AccountEntity;
import ru.askor.blagosfera.data.jpa.entities.account.AccountTypeEntity;
import ru.radom.kabinet.dao.Dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("accountDao")
public class AccountDao extends Dao<AccountEntity> {

    public List<AccountEntity> getList(Object owner) {
        return find(getCriteria().createAlias("type", "typeAlias").add(Restrictions.eq("owner", owner)).addOrder(Order.asc("typeAlias.position")));
    }

    public Map<AccountTypeEntity, AccountEntity> getAccountMap(Object owner) {
        Map<AccountTypeEntity, AccountEntity> map = new HashMap<>();

        for (AccountEntity account : getList(owner)) {
            map.put(account.getType(), account);
        }

        return map;
    }
}
