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
@Table(name = "MAPPED_PEOPLE_LOMBOK_MODELMAPPER")
public class MappedPeopleLombokModelmapper {

    /** */
    @Id
    @Column(name = "ID")
    Long id;

    /** */
    @Column(name = "SOURCE_ID")
    Long sourceId;

    /** */
    @Column(name = "FULL_NAME")
    String fullName;

    /** */
    @Column(name = "AGE")
    Integer age;

    /** */
    @Column(name = "AGE_GROUP")
    String ageGroup;

    /** */
    @Column(name = "EMAIL")
    String email;

    /** */
    @Column(name = "ADDRESS_LINE")
    String addressLine;

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
     * Returns the sourceId.
     * 
     * @return the sourceId
     */
    public Long getSourceId() {
        return sourceId;
    }

    /** 
     * Sets the sourceId.
     * 
     * @param sourceId the sourceId
     */
    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    /** 
     * Returns the fullName.
     * 
     * @return the fullName
     */
    public String getFullName() {
        return fullName;
    }

    /** 
     * Sets the fullName.
     * 
     * @param fullName the fullName
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
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
     * Returns the ageGroup.
     * 
     * @return the ageGroup
     */
    public String getAgeGroup() {
        return ageGroup;
    }

    /** 
     * Sets the ageGroup.
     * 
     * @param ageGroup the ageGroup
     */
    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
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
     * Returns the addressLine.
     * 
     * @return the addressLine
     */
    public String getAddressLine() {
        return addressLine;
    }

    /** 
     * Sets the addressLine.
     * 
     * @param addressLine the addressLine
     */
    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
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