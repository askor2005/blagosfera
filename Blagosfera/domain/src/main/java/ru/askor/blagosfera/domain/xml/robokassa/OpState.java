//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.07.19 at 07:39:59 PM MSK 
//


package ru.askor.blagosfera.domain.xml.robokassa;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MerchantLogin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="InvoiceID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Signature" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "merchantLogin",
    "invoiceID",
    "signature"
})
@XmlRootElement(name = "OpState")
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-07-19T07:39:59+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
public class OpState {

    @XmlElement(name = "MerchantLogin")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-07-19T07:39:59+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected String merchantLogin;
    @XmlElement(name = "InvoiceID")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-07-19T07:39:59+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected int invoiceID;
    @XmlElement(name = "Signature")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-07-19T07:39:59+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected String signature;

    /**
     * Gets the value of the merchantLogin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-07-19T07:39:59+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public String getMerchantLogin() {
        return merchantLogin;
    }

    /**
     * Sets the value of the merchantLogin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-07-19T07:39:59+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setMerchantLogin(String value) {
        this.merchantLogin = value;
    }

    /**
     * Gets the value of the invoiceID property.
     * 
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-07-19T07:39:59+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public int getInvoiceID() {
        return invoiceID;
    }

    /**
     * Sets the value of the invoiceID property.
     * 
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-07-19T07:39:59+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setInvoiceID(int value) {
        this.invoiceID = value;
    }

    /**
     * Gets the value of the signature property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-07-19T07:39:59+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public String getSignature() {
        return signature;
    }

    /**
     * Sets the value of the signature property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-07-19T07:39:59+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setSignature(String value) {
        this.signature = value;
    }

}
