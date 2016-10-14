package ru.askor.blagosfera.data.jpa.entities.account;

import org.hibernate.annotations.Any;
import org.hibernate.annotations.AnyMetaDef;
import org.hibernate.annotations.MetaValue;
import ru.askor.blagosfera.domain.account.Sharebook;
import ru.radom.kabinet.model.Discriminators;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Паевая книжка
 * <p/>
 * Created by ebelyaev on 21.08.2015.
 */
@Entity
@Table(name = "book_accounts")
public class SharebookEntity extends LongIdentifiable {

    @JoinColumn(name = "account_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.REMOVE)
    private AccountEntity account;

    @JoinColumn(name = "bonus_account_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.REMOVE)
    private AccountEntity bonusAccount;

    @Any(metaColumn = @Column(name = "owner_type", length = 50), fetch = FetchType.LAZY)
    @AnyMetaDef(idType = "long", metaType = "string", metaValues = {@MetaValue(targetEntity = CommunityEntity.class, value = Discriminators.COMMUNITY), @MetaValue(targetEntity = UserEntity.class, value = Discriminators.SHARER)})
    @JoinColumn(name = "owner_id")
    private Object sharebookOwner;

    @JoinColumn(name = "community_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private CommunityEntity community;

    public SharebookEntity() {
    }

    public AccountEntity getAccount() {
        return account;
    }

    public void setAccount(AccountEntity account) {
        this.account = account;
    }

    public AccountEntity getBonusAccount() {
        return bonusAccount;
    }

    public void setBonusAccount(AccountEntity bonusAccount) {
        this.bonusAccount = bonusAccount;
    }

    public Object getSharebookOwner() {
        return sharebookOwner;
    }

    public void setSharebookOwner(Object sharebookOwner) {
        this.sharebookOwner = sharebookOwner;
    }

    public CommunityEntity getCommunity() {
        return community;
    }

    public void setCommunity(CommunityEntity community) {
        this.community = community;
    }

    public BigDecimal getBalance() {
        return account.getBalance();
    }

    public BigDecimal getBonusBalance() {
        return bonusAccount.getBalance();
    }

    public Sharebook toDomain() {
        Sharebook result = new Sharebook();
        result.setAccount(getAccount() != null ? getAccount().toDomain() : null);
        result.setBonusAccount(getBonusAccount() != null ? getBonusAccount().toDomain() : null);
        result.setId(getId());

        if (sharebookOwner instanceof UserEntity) {
            result.setSharebookOwner(((UserEntity) sharebookOwner).toDomain());
            result.setSharebookOwnerType(Discriminators.SHARER);
        } else if (sharebookOwner instanceof CommunityEntity) {
            result.setSharebookOwner(((CommunityEntity) sharebookOwner).toDomain());
            result.setSharebookOwnerType(Discriminators.COMMUNITY);
        }

        return result;
    }
}
