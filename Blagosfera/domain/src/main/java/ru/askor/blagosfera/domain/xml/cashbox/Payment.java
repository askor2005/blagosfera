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
 * <p>Java class for Payment complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Payment">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="cash">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;all>
 *                   &lt;element name="amount" type="{}Money"/>
 *                 &lt;/all>
 *                 &lt;attribute name="getChange" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="nocash">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Payment", propOrder = {
    "cash",
    "nocash"
})
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
public class Payment {

    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected Payment.Cash cash;
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected Payment.Nocash nocash;

    /**
     * Gets the value of the cash property.
     * 
     * @return
     *     possible object is
     *     {@link Payment.Cash }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public Payment.Cash getCash() {
        return cash;
    }

    /**
     * Sets the value of the cash property.
     * 
     * @param value
     *     allowed object is
     *     {@link Payment.Cash }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setCash(Payment.Cash value) {
        this.cash = value;
    }

    /**
     * Gets the value of the nocash property.
     * 
     * @return
     *     possible object is
     *     {@link Payment.Nocash }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public Payment.Nocash getNocash() {
        return nocash;
    }

    /**
     * Sets the value of the nocash property.
     * 
     * @param value
     *     allowed object is
     *     {@link Payment.Nocash }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setNocash(Payment.Nocash value) {
        this.nocash = value;
    }


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
     *         &lt;element name="amount" type="{}Money"/>
     *       &lt;/all>
     *       &lt;attribute name="getChange" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
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
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public static class Cash {

        @XmlElement(required = true)
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
        protected Money amount;
        @XmlAttribute(name = "getChange", required = true)
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
        protected boolean getChange;

        /**
         * Gets the value of the amount property.
         * 
         * @return
         *     possible object is
         *     {@link Money }
         *     
         */
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
        public Money getAmount() {
            return amount;
        }

        /**
         * Sets the value of the amount property.
         * 
         * @param value
         *     allowed object is
         *     {@link Money }
         *     
         */
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
        public void setAmount(Money value) {
            this.amount = value;
        }

        /**
         * Gets the value of the getChange property.
         * 
         */
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
        public boolean isGetChange() {
            return getChange;
        }

        /**
         * Sets the value of the getChange property.
         * 
         */
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
        public void setGetChange(boolean value) {
            this.getChange = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public static class Nocash {


    }

}