package ru.askor.blagosfera.data.jpa.entities.account;

import org.hibernate.annotations.Any;
import org.hibernate.annotations.AnyMetaDef;
import org.hibernate.annotations.MetaValue;
import ru.askor.blagosfera.domain.account.Account;
import ru.radom.kabinet.model.Discriminators;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.notifications.SystemAccountEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "accounts")
public class AccountEntity extends LongIdentifiable {

    @JoinColumn(name = "type_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private AccountTypeEntity type;

    @Any(metaColumn = @Column(name = "owner_type", length = 50), fetch = FetchType.EAGER)
    @AnyMetaDef(idType = "long", metaType = "string", metaValues = {
            @MetaValue(targetEntity = CommunityEntity.class, value = Discriminators.COMMUNITY),
            @MetaValue(targetEntity = UserEntity.class, value = Discriminators.SHARER),
            @MetaValue(targetEntity = SharebookEntity.class, value = Discriminators.SHARER_BOOK),
            @MetaValue(targetEntity = SystemAccountEntity.class, value = Discriminators.SYSTEM_ACCOUNT)})
    @JoinColumn(name = "owner_id")
    private Object owner;

    @Column(name = "owner_type", insertable = false, updatable = false)
    private String ownerType;

    @Column(name = "owner_id", insertable = false, updatable = false)
    private Long ownerId;

    @Column(name = "total_balance", nullable = false, columnDefinition = "numeric(19,2) default 0.00")
    private BigDecimal balance;

    @Column(name = "hold_balance", nullable = false, columnDefinition = "numeric(19,2) default 0.00")
    private BigDecimal holdBalance;

    public AccountTypeEntity getType() {
        return type;
    }

    public void setType(AccountTypeEntity type) {
        this.type = type;
    }

    public Object getOwner() {
        return owner;
    }

    public void setOwner(Object owner) {
        this.owner = owner;
    }

    public String getOwnerType() {
        return ownerType;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getHoldBalance() {
        return holdBalance;
    }

    public void setHoldBalance(BigDecimal holdBalance) {
        this.holdBalance = holdBalance;
    }

    public Account toDomain() {
        Account result = new Account();
        result.setId(getId());
        result.setBalance(getBalance());
        result.setType(getType() != null ? getType().toDomain() : null);
        if (owner instanceof UserEntity) {
            result.setOwner(((UserEntity) owner).toDomain());
            result.setOwnerType(Discriminators.SHARER);
        } else if (owner instanceof CommunityEntity) {
            result.setOwner(((CommunityEntity) owner).toDomain());
            result.setOwnerType(Discriminators.COMMUNITY);
        } else if (owner instanceof SharebookEntity) {
            result.setOwner(((SharebookEntity) owner).toDomain());
            result.setOwnerType(Discriminators.SHARER_BOOK);
        } else if (owner instanceof SystemAccountEntity) {
            result.setOwner(((SystemAccountEntity) owner).toDomain());
            result.setOwnerType(Discriminators.SYSTEM_ACCOUNT);
        }
        return result;
    }

    public static List<Account> toDomainList(List<AccountEntity> accounts) {
        List<Account> result = new ArrayList<>();
        if (accounts != null) {
            for (AccountEntity account : accounts) {
                result.add(account.toDomain());
            }
        }
        return result;
    }
}
