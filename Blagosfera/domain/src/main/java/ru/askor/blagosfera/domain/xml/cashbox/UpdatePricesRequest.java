//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.12.29 at 12:53:11 PM MSK 
//


package ru.askor.blagosfera.domain.xml.cashbox;

import javax.annotation.Generated;
import javax.xml.bind.annotation.*;
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
 *         &lt;element name="shop" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="products">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="product" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;all>
 *                             &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="finalPrice" type="{}Price"/>
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
@XmlRootElement(name = "updatePricesRequest")
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
public class UpdatePricesRequest {

    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected long shop;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected UpdatePricesRequest.Products products;

    /**
     * Gets the value of the shop property.
     * 
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public long getShop() {
        return shop;
    }

    /**
     * Sets the value of the shop property.
     * 
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setShop(long value) {
        this.shop = value;
    }

    /**
     * Gets the value of the products property.
     * 
     * @return
     *     possible object is
     *     {@link UpdatePricesRequest.Products }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public UpdatePricesRequest.Products getProducts() {
        return products;
    }

    /**
     * Sets the value of the products property.
     * 
     * @param value
     *     allowed object is
     *     {@link UpdatePricesRequest.Products }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setProducts(UpdatePricesRequest.Products value) {
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
     *                   &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="finalPrice" type="{}Price"/>
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
        protected List<UpdatePricesRequest.Products.Product> product;

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
         * {@link UpdatePricesRequest.Products.Product }
         * 
         * 
         */
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
        public List<UpdatePricesRequest.Products.Product> getProduct() {
            if (product == null) {
                product = new ArrayList<UpdatePricesRequest.Products.Product>();
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
         *         &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="finalPrice" type="{}Price"/>
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
            protected String code;
            @XmlElement(required = true)
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
            protected Price finalPrice;

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
             * Gets the value of the finalPrice property.
             * 
             * @return
             *     possible object is
             *     {@link Price }
             *     
             */
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
            public Price getFinalPrice() {
                return finalPrice;
            }

            /**
             * Sets the value of the finalPrice property.
             * 
             * @param value
             *     allowed object is
             *     {@link Price }
             *     
             */
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-12-29T12:53:11+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
            public void setFinalPrice(Price value) {
                this.finalPrice = value;
            }

        }

    }

}
