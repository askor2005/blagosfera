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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PaymentMethodGroup complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PaymentMethodGroup">
 *   &lt;complexContent>
 *     &lt;extension base="{http://merchant.roboxchange.com/WebService/}BaseData">
 *       &lt;sequence>
 *         &lt;element name="Items" type="{http://merchant.roboxchange.com/WebService/}ArrayOfCurrency" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="Code" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Description" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentMethodGroup", propOrder = {
    "items"
})
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-07-19T07:39:59+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
public class PaymentMethodGroup
    extends BaseData
{

    @XmlElement(name = "Items")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-07-19T07:39:59+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected ArrayOfCurrency items;
    @XmlAttribute(name = "Code")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-07-19T07:39:59+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected String code;
    @XmlAttribute(name = "Description")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-07-19T07:39:59+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected String description;

    /**
     * Gets the value of the items property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfCurrency }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-07-19T07:39:59+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public ArrayOfCurrency getItems() {
        return items;
    }

    /**
     * Sets the value of the items property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfCurrency }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-07-19T07:39:59+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setItems(ArrayOfCurrency value) {
        this.items = value;
    }

    /**
     * Gets the value of the code property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-07-19T07:39:59+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public String getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-07-19T07:39:59+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setCode(String value) {
        this.code = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-07-19T07:39:59+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-07-19T07:39:59+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setDescription(String value) {
        this.description = value;
    }

}