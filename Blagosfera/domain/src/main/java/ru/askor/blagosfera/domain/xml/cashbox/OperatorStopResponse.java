//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.12.29 at 12:53:11 PM MSK 
//


package ru.askor.blagosfera.domain.xml.cashbox;

import javax.annotation.Generated;
import javax.xml.bind.annotation.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


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
 *         &lt;element name="duration" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="exchangesTotal" type="{}Money" minOccurs="0"/>
 *         &lt;element name="exchangesCount" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="products" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="product" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;all>
 *                             &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="count" type="{}Amount"/>
 *                             &lt;element name="finalPrice" type="{}Money"/>
 *                           &lt;/all>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
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
@XmlRootElement(name = "operatorStopResponse")
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
public class OperatorStopResponse {

    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected Status status;
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected String duration;
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected Money exchangesTotal;
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected Integer exchangesCount;
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected OperatorStopResponse.Products products;

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
     * Gets the value of the duration property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public String getDuration() {
        return duration;
    }

    /**
     * Sets the value of the duration property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setDuration(String value) {
        this.duration = value;
    }

    /**
     * Gets the value of the exchangesTotal property.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public Money getExchangesTotal() {
        return exchangesTotal;
    }

    /**
     * Sets the value of the exchangesTotal property.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setExchangesTotal(Money value) {
        this.exchangesTotal = value;
    }

    /**
     * Gets the value of the exchangesCount property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public Integer getExchangesCount() {
        return exchangesCount;
    }

    /**
     * Sets the value of the exchangesCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setExchangesCount(Integer value) {
        this.exchangesCount = value;
    }

    /**
     * Gets the value of the products property.
     * 
     * @return
     *     possible object is
     *     {@link OperatorStopResponse.Products }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public OperatorStopResponse.Products getProducts() {
        return products;
    }

    /**
     * Sets the value of the products property.
     * 
     * @param value
     *     allowed object is
     *     {@link OperatorStopResponse.Products }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setProducts(OperatorStopResponse.Products value) {
        this.products = value;
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
     *       &lt;sequence>
     *         &lt;element name="product" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;all>
     *                   &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="count" type="{}Amount"/>
     *                   &lt;element name="finalPrice" type="{}Money"/>
     *                 &lt;/all>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
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
        "product"
    })
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public static class Products {

        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
        protected List<OperatorStopResponse.Products.Product> product;

        /**
         * Gets the value of the product property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the product property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getProduct().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link OperatorStopResponse.Products.Product }
         * 
         * 
         */
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
        public List<OperatorStopResponse.Products.Product> getProduct() {
            if (product == null) {
                product = new ArrayList<OperatorStopResponse.Products.Product>();
            }
            return this.product;
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
         *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="count" type="{}Amount"/>
         *         &lt;element name="finalPrice" type="{}Money"/>
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
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
        public static class Product {

            @XmlElement(required = true)
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
            protected String name;
            @XmlElement(required = true)
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
            protected String code;
            @XmlElement(required = true)
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
            protected BigDecimal count;
            @XmlElement(required = true)
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
            protected Money finalPrice;

            /**
             * Gets the value of the name property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
            public String getName() {
                return name;
            }

            /**
             * Sets the value of the name property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
            public void setName(String value) {
                this.name = value;
            }

            /**
             * Gets the value of the code property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
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
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
            public void setCode(String value) {
                this.code = value;
            }

            /**
             * Gets the value of the count property.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
            public BigDecimal getCount() {
                return count;
            }

            /**
             * Sets the value of the count property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
            public void setCount(BigDecimal value) {
                this.count = value;
            }

            /**
             * Gets the value of the finalPrice property.
             * 
             * @return
             *     possible object is
             *     {@link Money }
             *     
             */
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
            public Money getFinalPrice() {
                return finalPrice;
            }

            /**
             * Sets the value of the finalPrice property.
             * 
             * @param value
             *     allowed object is
             *     {@link Money }
             *     
             */
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
            public void setFinalPrice(Money value) {
                this.finalPrice = value;
            }

        }

    }

}
