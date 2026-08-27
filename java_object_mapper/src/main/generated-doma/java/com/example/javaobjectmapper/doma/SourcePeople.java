package com.example.javaobjectmapper.doma;

import java.time.LocalDateTime;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;

/**
 * 
 */
@Entity
@Table(name = "SOURCE_PEOPLE")
public class SourcePeople {

    /** */
    @Id
    @Column(name = "ID")
    Long id;

    /** */
    @Column(name = "FIRST_NAME")
    String firstName;

    /** */
    @Column(name = "LAST_NAME")
    String lastName;

    /** */
    @Column(name = "AGE")
    Integer age;

    /** */
    @Column(name = "EMAIL")
    String email;

    /** */
    @Column(name = "CITY")
    String city;

    /** */
    @Column(name = "STREET")
    String street;

    /** */
    @Column(name = "POSTAL_CODE")
    String postalCode;

    /** */
    @Column(name = "LOYALTY_POINTS")
    Integer loyaltyPoints;

    /** */
    @Column(name = "CREATED_AT")
    LocalDateTime createdAt;

    /** 
     * Returns the id.
     * 
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /** 
     * Sets the id.
     * 
     * @param id the id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /** 
     * Returns the firstName.
     * 
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /** 
     * Sets the firstName.
     * 
     * @param firstName the firstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /** 
     * Returns the lastName.
     * 
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /** 
     * Sets the lastName.
     * 
     * @param lastName the lastName
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /** 
     * Returns the age.
     * 
     * @return the age
     */
    public Integer getAge() {
        return age;
    }

    /** 
     * Sets the age.
     * 
     * @param age the age
     */
    public void setAge(Integer age) {
        this.age = age;
    }

    /** 
     * Returns the email.
     * 
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /** 
     * Sets the email.
     * 
     * @param email the email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /** 
     * Returns the city.
     * 
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /** 
     * Sets the city.
     * 
     * @param city the city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /** 
     * Returns the street.
     * 
     * @return the street
     */
    public String getStreet() {
        return street;
    }

    /** 
     * Sets the street.
     * 
     * @param street the street
     */
    public void setStreet(String street) {
        this.street = street;
    }

    /** 
     * Returns the postalCode.
     * 
     * @return the postalCode
     */
    public String getPostalCode() {
        return postalCode;
    }

    /** 
     * Sets the postalCode.
     * 
     * @param postalCode the postalCode
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /** 
     * Returns the loyaltyPoints.
     * 
     * @return the loyaltyPoints
     */
    public Integer getLoyaltyPoints() {
        return loyaltyPoints;
    }

    /** 
     * Sets the loyaltyPoints.
     * 
     * @param loyaltyPoints the loyaltyPoints
     */
    public void setLoyaltyPoints(Integer loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    /** 
     * Returns the createdAt.
     * 
     * @return the createdAt
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /** 
     * Sets the createdAt.
     * 
     * @param createdAt the createdAt
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}