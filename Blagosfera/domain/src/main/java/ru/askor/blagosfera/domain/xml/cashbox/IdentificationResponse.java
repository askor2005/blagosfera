//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.12.29 at 12:53:11 PM MSK 
//


package ru.askor.blagosfera.domain.xml.cashbox;

import javax.annotation.Generated;
import javax.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="status" type="{}Status"/>
 *         &lt;element name="shareHolder" type="{}ShareHolder" minOccurs="0"/>
 *         &lt;element name="affiliationFee" type="{}Money" minOccurs="0"/>
 *         &lt;element name="shareAmountMin" type="{}Money" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {

})
@XmlRootElement(name = "identificationResponse")
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
public class IdentificationResponse {

    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected Status status;
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected ShareHolder shareHolder;
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected Money affiliationFee;
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected Money shareAmountMin;

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link Status }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public Status getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link Status }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setStatus(Status value) {
        this.status = value;
    }

    /**
     * Gets the value of the shareHolder property.
     * 
     * @return
     *     possible object is
     *     {@link ShareHolder }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public ShareHolder getShareHolder() {
        return shareHolder;
    }

    /**
     * Sets the value of the shareHolder property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShareHolder }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setShareHolder(ShareHolder value) {
        this.shareHolder = value;
    }

    /**
     * Gets the value of the affiliationFee property.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public Money getAffiliationFee() {
        return affiliationFee;
    }

    /**
     * Sets the value of the affiliationFee property.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setAffiliationFee(Money value) {
        this.affiliationFee = value;
    }

    /**
     * Gets the value of the shareAmountMin property.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public Money getShareAmountMin() {
        return shareAmountMin;
    }

    /**
     * Sets the value of the shareAmountMin property.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setShareAmountMin(Money value) {
        this.shareAmountMin = value;
    }

}
