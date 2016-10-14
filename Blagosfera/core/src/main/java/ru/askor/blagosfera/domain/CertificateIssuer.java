package ru.askor.blagosfera.domain;

public class CertificateIssuer {

    private String commonName;
    private String organizationUnit;
    private String organizationName;
    private String localityName;
    private String stateName;
    private String countryCode;
    private String email;

    public CertificateIssuer(String commonName, String organizationUnit, String organizationName, String localityName, String stateName, String countryCode, String email) {
        this.commonName = commonName;
        this.organizationUnit = organizationUnit;
        this.organizationName = organizationName;
        this.localityName = localityName;
        this.stateName = stateName;
        this.countryCode = countryCode;
        this.email = email;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getOrganizationUnit() {
        return organizationUnit;
    }

    public void setOrganizationUnit(String organizationUnit) {
        this.organizationUnit = organizationUnit;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getLocalityName() {
        return localityName;
    }

    public void setLocalityName(String localityName) {
        this.localityName = localityName;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String toString() {
        return String.format("CN=%s,OU=%s,O=%s,L=%s,ST=%s,C=%s,EMAILADDRESS=%s",
                getCommonName(), getOrganizationUnit(), getOrganizationName(), getLocalityName(), getStateName(), getCountryCode(), getEmail());
    }
}
