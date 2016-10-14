package ru.askor.blagosfera.domain.account;

/**
 * Created by max on 21.07.16.
 */
public class PaymentSystem {

    private Long id;
    private int position;
    private String name;
    private String beanName;
    private double systemIncomingComission;
    private double rameraIncomingComission;
    private double systemOutgoingComission;
    private double rameraOutgoingComission;
    private boolean active;

    public PaymentSystem() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public double getSystemIncomingComission() {
        return systemIncomingComission;
    }

    public void setSystemIncomingComission(double systemIncomingComission) {
        this.systemIncomingComission = systemIncomingComission;
    }

    public double getRameraIncomingComission() {
        return rameraIncomingComission;
    }

    public void setRameraIncomingComission(double rameraIncomingComission) {
        this.rameraIncomingComission = rameraIncomingComission;
    }

    public double getSystemOutgoingComission() {
        return systemOutgoingComission;
    }

    public void setSystemOutgoingComission(double systemOutgoingComission) {
        this.systemOutgoingComission = systemOutgoingComission;
    }

    public double getRameraOutgoingComission() {
        return rameraOutgoingComission;
    }

    public void setRameraOutgoingComission(double rameraOutgoingComission) {
        this.rameraOutgoingComission = rameraOutgoingComission;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
