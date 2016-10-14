package ru.radom.kabinet.model.cashbox;

import javax.persistence.*;

@Entity
@Table(name = "cashbox_products")
public class CashboxProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cashbox_products_id_generator")
    @SequenceGenerator(name = "cashbox_products_id_generator", sequenceName = "cashbox_products_id", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "shop", nullable = false)
    private Long shop;

    public CashboxProductEntity() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CashboxProductEntity)) return false;

        CashboxProductEntity that = (CashboxProductEntity) o;

        //return !(getId() != null ? !getId().equals(that.getId()) : that.getId() != null);
        return (getId() != null) && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getShop() {
        return shop;
    }

    public void setShop(Long shop) {
        this.shop = shop;
    }
}
