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
 *         &lt;element name="OpStateResult" type="{http://merchant.roboxchange.com/WebService/}OperationStateResponse" minOccurs="0"/>
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
    "opStateResult"
})
@XmlRootElement(name = "OpStateResponse")
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-07-19T07:39:59+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
public class OpStateResponse {

    @XmlElement(name = "OpStateResult")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-07-19T07:39:59+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected OperationStateResponse opStateResult;

    /**
     * Gets the value of the opStateResult property.
     * 
     * @return
     *     possible object is
     *     {@link OperationStateResponse }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-07-19T07:39:59+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public OperationStateResponse getOpStateResult() {
        return opStateResult;
    }

    /**
     * Sets the value of the opStateResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link OperationStateResponse }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-07-19T07:39:59+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setOpStateResult(OperationStateResponse value) {
        this.opStateResult = value;
    }

}