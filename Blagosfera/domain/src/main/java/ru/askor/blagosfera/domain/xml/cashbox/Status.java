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
 * <p>Java class for Status complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Status">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="code" type="{}StatusCode"/>
 *         &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Status", propOrder = {

})
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
public class Status {

    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected StatusCode code;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected String message;

    /**
     * Gets the value of the code property.
     * 
     * @return
     *     possible object is
     *     {@link StatusCode }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public StatusCode getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatusCode }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setCode(StatusCode value) {
        this.code = value;
    }

    /**
     * Gets the value of the message property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public String getMessage() {
        return message;
    }

    /**
     * Sets the value of the message property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setMessage(String value) {
        this.message = value;
    }

}
