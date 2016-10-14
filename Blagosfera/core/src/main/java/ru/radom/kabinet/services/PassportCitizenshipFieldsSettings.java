package ru.radom.kabinet.services;

/**
 *
 * Created by ebelyaev on 25.09.2015.
 */
public class PassportCitizenshipFieldsSettings {

    private static final String DEFAULT_HOLDER = "_";

    private static final String DEFAULT_COUNTRY_COM_CODE = "default";

    // какие поля светить
    private static final boolean DEFAULT_SHOW_PERSON_INN = true;
    private static final boolean DEFAULT_SHOW_SNILS = true;
    private static final boolean DEFAULT_SHOW_BY_IDENTIFICATION_NUMBER = false;
    private static final boolean DEFAULT_SHOW_KZ_INDIVIDUAL_IDENTIFICATION_NUMBER = false;
    private static final boolean DEFAULT_SHOW_PASSPORT_SERIAL = true;
    private static final boolean DEFAULT_SHOW_PASSPORT_NUMBER = true;
    private static final boolean DEFAULT_SHOW_PASSPORT_DIVISION = true;
    private static final boolean DEFAULT_SHOW_PASSPORT_EXPIRED_DATE = false;
    private static final boolean DEFAULT_SHOW_PASSPORT_EXPIRATION_DATE = false;
    private static final boolean DEFAULT_SHOW_PASSPORT_DEALER = true;

    // формат/маски полей
    private static final String DEFAULT_MASK_PERSON_INN = "999999999999";
    private static final String DEFAULT_HOLDER_PERSON_INN = "____________";

    private static final String DEFAULT_MASK_SNILS = "999-999-999 99";
    private static final String DEFAULT_HOLDER_SNILS = "___-___-___ __";

    private static final String DEFAULT_MASK_BY_IDENTIFICATION_NUMBER = "9999999 r 999 rr 9";
    private static final String DEFAULT_HOLDER_BY_IDENTIFICATION_NUMBER = "_______ _ ___ __ _";

    private static final String DEFAULT_MASK_KZ_INDIVIDUAL_IDENTIFICATION_NUMBER = "999999999999";
    private static final String DEFAULT_HOLDER_KZ_INDIVIDUAL_IDENTIFICATION_NUMBER = "____________";

    private static final String DEFAULT_MASK_PASSPORT_SERIAL = "99 99";
    private static final String DEFAULT_HOLDER_PASSPORT_SERIAL = "__ __";

    private static final String DEFAULT_MASK_PASSPORT_NUMBER = "999999";
    private static final String DEFAULT_HOLDER_PASSPORT_NUMBER = "______";

    private static final String DEFAULT_MASK_PASSPORT_DIVISION = "999-999";
    private static final String DEFAULT_HOLDER_PASSPORT_DIVISION = "___-___";

    // -----------------------------------------------------------------------------------------------------------------

    // Для какой гражданства какой страны настройки
    private String countryComCode = DEFAULT_COUNTRY_COM_CODE;

    // какие поля светить
    private boolean showPersonInn = DEFAULT_SHOW_PERSON_INN;
    private boolean showSnils = DEFAULT_SHOW_SNILS;
    private boolean showByIdentificationNumber = DEFAULT_SHOW_BY_IDENTIFICATION_NUMBER;
    private boolean showKzIndividualIdentificationNumber = DEFAULT_SHOW_KZ_INDIVIDUAL_IDENTIFICATION_NUMBER;
    private boolean showPassportSerial = DEFAULT_SHOW_PASSPORT_SERIAL;
    private boolean showPassportNumber = DEFAULT_SHOW_PASSPORT_NUMBER;
    private boolean showPassportDivision = DEFAULT_SHOW_PASSPORT_DIVISION;
    private boolean showPassportExpiredDate = DEFAULT_SHOW_PASSPORT_EXPIRED_DATE;
    private boolean showPassportExpirationDate = DEFAULT_SHOW_PASSPORT_EXPIRATION_DATE;
    private boolean showPassportDealer = DEFAULT_SHOW_PASSPORT_DEALER;

    // формат/маски полей
    private String holder = DEFAULT_HOLDER;

    private String maskPersonInn = DEFAULT_MASK_PERSON_INN;
    private String holderPersonInn = DEFAULT_HOLDER_PERSON_INN;

    private String maskSnils = DEFAULT_MASK_SNILS;
    private String holderSnils = DEFAULT_HOLDER_SNILS;

    private String maskByIdentificationNumber = DEFAULT_MASK_BY_IDENTIFICATION_NUMBER;
    private String holderByIdentificationNumber = DEFAULT_HOLDER_BY_IDENTIFICATION_NUMBER;

    private String maskKzIndividualIdentificationNumber = DEFAULT_MASK_KZ_INDIVIDUAL_IDENTIFICATION_NUMBER;
    private String holderKzIndividualIdentificationNumber = DEFAULT_HOLDER_KZ_INDIVIDUAL_IDENTIFICATION_NUMBER;

    private String maskPassportSerial = DEFAULT_MASK_PASSPORT_SERIAL;
    private String holderPassportSerial = DEFAULT_HOLDER_PASSPORT_SERIAL;

    private String maskPassportNumber = DEFAULT_MASK_PASSPORT_NUMBER;
    private String holderPassportNumber = DEFAULT_HOLDER_PASSPORT_NUMBER;

    private String maskPassportDivision = DEFAULT_MASK_PASSPORT_DIVISION;
    private String holderPassportDivision = DEFAULT_HOLDER_PASSPORT_DIVISION;

    public String getCountryComCode() {
        return countryComCode;
    }

    public void setCountryComCode(String countryComCode) {
        this.countryComCode = countryComCode;
    }

    public boolean isShowPersonInn() {
        return showPersonInn;
    }

    public void setShowPersonInn(boolean showPersonInn) {
        this.showPersonInn = showPersonInn;
    }

    public boolean isShowSnils() {
        return showSnils;
    }

    public void setShowSnils(boolean showSnils) {
        this.showSnils = showSnils;
    }

    public boolean isShowPassportSerial() {
        return showPassportSerial;
    }

    public void setShowPassportSerial(boolean showPassportSerial) {
        this.showPassportSerial = showPassportSerial;
    }

    public boolean isShowPassportNumber() {
        return showPassportNumber;
    }

    public void setShowPassportNumber(boolean showPassportNumber) {
        this.showPassportNumber = showPassportNumber;
    }

    public boolean isShowPassportExpirationDate() {
        return showPassportExpirationDate;
    }

    public void setShowPassportExpirationDate(boolean showPassportExpirationDate) {
        this.showPassportExpirationDate = showPassportExpirationDate;
    }

    public boolean isShowPassportDealer() {
        return showPassportDealer;
    }

    public void setShowPassportDealer(boolean showPassportDealer) {
        this.showPassportDealer = showPassportDealer;
    }

    public boolean isShowPassportDivision() {
        return showPassportDivision;
    }

    public void setShowPassportDivision(boolean showPassportDivision) {
        this.showPassportDivision = showPassportDivision;
    }

    public String getHolder() {
        return holder;
    }

    public void setHolder(String holder) {
        this.holder = holder;
    }

    public String getMaskPersonInn() {
        return maskPersonInn;
    }

    public void setMaskPersonInn(String maskPersonInn) {
        this.maskPersonInn = maskPersonInn;
    }

    public String getHolderPersonInn() {
        return holderPersonInn;
    }

    public void setHolderPersonInn(String holderPersonInn) {
        this.holderPersonInn = holderPersonInn;
    }

    public String getMaskSnils() {
        return maskSnils;
    }

    public void setMaskSnils(String maskSnils) {
        this.maskSnils = maskSnils;
    }

    public String getHolderSnils() {
        return holderSnils;
    }

    public void setHolderSnils(String holderSnils) {
        this.holderSnils = holderSnils;
    }

    public String getMaskPassportSerial() {
        return maskPassportSerial;
    }

    public void setMaskPassportSerial(String maskPassportSerial) {
        this.maskPassportSerial = maskPassportSerial;
    }

    public String getHolderPassportSerial() {
        return holderPassportSerial;
    }

    public void setHolderPassportSerial(String holderPassportSerial) {
        this.holderPassportSerial = holderPassportSerial;
    }

    public String getMaskPassportNumber() {
        return maskPassportNumber;
    }

    public void setMaskPassportNumber(String maskPassportNumber) {
        this.maskPassportNumber = maskPassportNumber;
    }

    public String getHolderPassportNumber() {
        return holderPassportNumber;
    }

    public void setHolderPassportNumber(String holderPassportNumber) {
        this.holderPassportNumber = holderPassportNumber;
    }

    public String getMaskPassportDivision() {
        return maskPassportDivision;
    }

    public void setMaskPassportDivision(String maskPassportDivision) {
        this.maskPassportDivision = maskPassportDivision;
    }

    public String getHolderPassportDivision() {
        return holderPassportDivision;
    }

    public void setHolderPassportDivision(String holderPassportDivision) {
        this.holderPassportDivision = holderPassportDivision;
    }

    public boolean isShowByIdentificationNumber() {
        return showByIdentificationNumber;
    }

    public void setShowByIdentificationNumber(boolean showByIdentificationNumber) {
        this.showByIdentificationNumber = showByIdentificationNumber;
    }

    public boolean isShowKzIndividualIdentificationNumber() {
        return showKzIndividualIdentificationNumber;
    }

    public void setShowKzIndividualIdentificationNumber(boolean showKzIndividualIdentificationNumber) {
        this.showKzIndividualIdentificationNumber = showKzIndividualIdentificationNumber;
    }

    public String getMaskByIdentificationNumber() {
        return maskByIdentificationNumber;
    }

    public void setMaskByIdentificationNumber(String maskByIdentificationNumber) {
        this.maskByIdentificationNumber = maskByIdentificationNumber;
    }

    public String getHolderByIdentificationNumber() {
        return holderByIdentificationNumber;
    }

    public void setHolderByIdentificationNumber(String holderByIdentificationNumber) {
        this.holderByIdentificationNumber = holderByIdentificationNumber;
    }

    public String getMaskKzIndividualIdentificationNumber() {
        return maskKzIndividualIdentificationNumber;
    }

    public void setMaskKzIndividualIdentificationNumber(String maskKzIndividualIdentificationNumber) {
        this.maskKzIndividualIdentificationNumber = maskKzIndividualIdentificationNumber;
    }

    public String getHolderKzIndividualIdentificationNumber() {
        return holderKzIndividualIdentificationNumber;
    }

    public void setHolderKzIndividualIdentificationNumber(String holderKzIndividualIdentificationNumber) {
        this.holderKzIndividualIdentificationNumber = holderKzIndividualIdentificationNumber;
    }

    public boolean isShowPassportExpiredDate() {
        return showPassportExpiredDate;
    }

    public void setShowPassportExpiredDate(boolean showPassportExpiredDate) {
        this.showPassportExpiredDate = showPassportExpiredDate;
    }
}
