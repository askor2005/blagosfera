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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PaymentMethodsList complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PaymentMethodsList">
 *   &lt;complexContent>
 *     &lt;extension base="{http://merchant.roboxchange.com/WebService/}ResponseData">
 *       &lt;sequence>
 *         &lt;element name="Methods" type="{http://merchant.roboxchange.com/WebService/}ArrayOfMethod" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentMethodsList", propOrder = {
    "methods"
})
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-07-19T07:39:59+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
public class PaymentMethodsList
    extends ResponseData
{

    @XmlElement(name = "Methods")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-07-19T07:39:59+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected ArrayOfMethod methods;

    /**
     * Gets the value of the methods property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfMethod }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-07-19T07:39:59+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public ArrayOfMethod getMethods() {
        return methods;
    }

    /**
     * Sets the value of the methods property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfMethod }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-07-19T07:39:59+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setMethods(ArrayOfMethod value) {
        this.methods = value;
    }

}
