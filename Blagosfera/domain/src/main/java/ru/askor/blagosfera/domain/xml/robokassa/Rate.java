//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.07.19 at 07:39:59 PM MSK 
//


package ru.askor.blagosfera.domain.xml.robokassa;

import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Rate complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Rate">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="IncSum" use="required" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Rate")
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-07-19T07:39:59+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
public class Rate {

    @XmlAttribute(name = "IncSum", required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-07-19T07:39:59+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected BigDecimal incSum;

    /**
     * Gets the value of the incSum property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-07-19T07:39:59+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public BigDecimal getIncSum() {
        return incSum;
    }

    /**
     * Sets the value of the incSum property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-07-19T07:39:59+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setIncSum(BigDecimal value) {
        this.incSum = value;
    }

}
