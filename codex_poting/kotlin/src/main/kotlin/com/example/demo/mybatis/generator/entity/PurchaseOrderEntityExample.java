package com.example.demo.mybatis.generator.entity;

import com.example.demo.data.domain.PurchaseInvoiceType;
import jakarta.annotation.Generated;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderEntityExample {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_invoice")
    protected String orderByClause;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_invoice")
    protected boolean distinct;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_invoice")
    protected List<Criteria> oredCriteria;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_invoice")
    public PurchaseOrderEntityExample() {
        oredCriteria = new ArrayList<>();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_invoice")
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_invoice")
    public String getOrderByClause() {
        return orderByClause;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_invoice")
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_invoice")
    public boolean isDistinct() {
        return distinct;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_invoice")
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_invoice")
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_invoice")
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_invoice")
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_invoice")
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_invoice")
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_invoice")
    protected abstract static class GeneratedCriteria {
        protected List<Criterion> purchaseInvoiceTypeCriteria;

        protected List<Criterion> allCriteria;

        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<>();
            purchaseInvoiceTypeCriteria = new ArrayList<>();
        }

        public List<Criterion> getPurchaseInvoiceTypeCriteria() {
            return purchaseInvoiceTypeCriteria;
        }

        protected void addPurchaseInvoiceTypeCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            purchaseInvoiceTypeCriteria.add(new Criterion(condition, value, "com.example.demo.mybatis.typehandler.PurchaseInvoiceTypeHandler"));
            allCriteria = null;
        }

        protected void addPurchaseInvoiceTypeCriterion(String condition, PurchaseInvoiceType value1, PurchaseInvoiceType value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            purchaseInvoiceTypeCriteria.add(new Criterion(condition, value1, value2, "com.example.demo.mybatis.typehandler.PurchaseInvoiceTypeHandler"));
            allCriteria = null;
        }

        public boolean isValid() {
            return criteria.size() > 0
                || purchaseInvoiceTypeCriteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            if (allCriteria == null) {
                allCriteria = new ArrayList<>();
                allCriteria.addAll(criteria);
                allCriteria.addAll(purchaseInvoiceTypeCriteria);
            }
            return allCriteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
            allCriteria = null;
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
            allCriteria = null;
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
            allCriteria = null;
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceTypeIsNull() {
            addCriterion("purchase_invoice_type is null");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceTypeIsNotNull() {
            addCriterion("purchase_invoice_type is not null");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceTypeEqualTo(PurchaseInvoiceType value) {
            addPurchaseInvoiceTypeCriterion("purchase_invoice_type =", value, "purchaseInvoiceType");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceTypeNotEqualTo(PurchaseInvoiceType value) {
            addPurchaseInvoiceTypeCriterion("purchase_invoice_type <>", value, "purchaseInvoiceType");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceTypeGreaterThan(PurchaseInvoiceType value) {
            addPurchaseInvoiceTypeCriterion("purchase_invoice_type >", value, "purchaseInvoiceType");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceTypeGreaterThanOrEqualTo(PurchaseInvoiceType value) {
            addPurchaseInvoiceTypeCriterion("purchase_invoice_type >=", value, "purchaseInvoiceType");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceTypeLessThan(PurchaseInvoiceType value) {
            addPurchaseInvoiceTypeCriterion("purchase_invoice_type <", value, "purchaseInvoiceType");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceTypeLessThanOrEqualTo(PurchaseInvoiceType value) {
            addPurchaseInvoiceTypeCriterion("purchase_invoice_type <=", value, "purchaseInvoiceType");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceTypeIn(List<PurchaseInvoiceType> values) {
            addPurchaseInvoiceTypeCriterion("purchase_invoice_type in", values, "purchaseInvoiceType");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceTypeNotIn(List<PurchaseInvoiceType> values) {
            addPurchaseInvoiceTypeCriterion("purchase_invoice_type not in", values, "purchaseInvoiceType");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceTypeBetween(PurchaseInvoiceType value1, PurchaseInvoiceType value2) {
            addPurchaseInvoiceTypeCriterion("purchase_invoice_type between", value1, value2, "purchaseInvoiceType");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceTypeNotBetween(PurchaseInvoiceType value1, PurchaseInvoiceType value2) {
            addPurchaseInvoiceTypeCriterion("purchase_invoice_type not between", value1, value2, "purchaseInvoiceType");
            return (Criteria) this;
        }

        public Criteria andReturnPurchaseInvoiceIdIsNull() {
            addCriterion("return_purchase_invoice_id is null");
            return (Criteria) this;
        }

        public Criteria andReturnPurchaseInvoiceIdIsNotNull() {
            addCriterion("return_purchase_invoice_id is not null");
            return (Criteria) this;
        }

        public Criteria andReturnPurchaseInvoiceIdEqualTo(Long value) {
            addCriterion("return_purchase_invoice_id =", value, "returnPurchaseInvoiceId");
            return (Criteria) this;
        }

        public Criteria andReturnPurchaseInvoiceIdNotEqualTo(Long value) {
            addCriterion("return_purchase_invoice_id <>", value, "returnPurchaseInvoiceId");
            return (Criteria) this;
        }

        public Criteria andReturnPurchaseInvoiceIdGreaterThan(Long value) {
            addCriterion("return_purchase_invoice_id >", value, "returnPurchaseInvoiceId");
            return (Criteria) this;
        }

        public Criteria andReturnPurchaseInvoiceIdGreaterThanOrEqualTo(Long value) {
            addCriterion("return_purchase_invoice_id >=", value, "returnPurchaseInvoiceId");
            return (Criteria) this;
        }

        public Criteria andReturnPurchaseInvoiceIdLessThan(Long value) {
            addCriterion("return_purchase_invoice_id <", value, "returnPurchaseInvoiceId");
            return (Criteria) this;
        }

        public Criteria andReturnPurchaseInvoiceIdLessThanOrEqualTo(Long value) {
            addCriterion("return_purchase_invoice_id <=", value, "returnPurchaseInvoiceId");
            return (Criteria) this;
        }

        public Criteria andReturnPurchaseInvoiceIdIn(List<Long> values) {
            addCriterion("return_purchase_invoice_id in", values, "returnPurchaseInvoiceId");
            return (Criteria) this;
        }

        public Criteria andReturnPurchaseInvoiceIdNotIn(List<Long> values) {
            addCriterion("return_purchase_invoice_id not in", values, "returnPurchaseInvoiceId");
            return (Criteria) this;
        }

        public Criteria andReturnPurchaseInvoiceIdBetween(Long value1, Long value2) {
            addCriterion("return_purchase_invoice_id between", value1, value2, "returnPurchaseInvoiceId");
            return (Criteria) this;
        }

        public Criteria andReturnPurchaseInvoiceIdNotBetween(Long value1, Long value2) {
            addCriterion("return_purchase_invoice_id not between", value1, value2, "returnPurchaseInvoiceId");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDateIsNull() {
            addCriterion("purchase_invoice_date is null");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDateIsNotNull() {
            addCriterion("purchase_invoice_date is not null");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDateEqualTo(LocalDate value) {
            addCriterion("purchase_invoice_date =", value, "purchaseInvoiceDate");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDateNotEqualTo(LocalDate value) {
            addCriterion("purchase_invoice_date <>", value, "purchaseInvoiceDate");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDateGreaterThan(LocalDate value) {
            addCriterion("purchase_invoice_date >", value, "purchaseInvoiceDate");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDateGreaterThanOrEqualTo(LocalDate value) {
            addCriterion("purchase_invoice_date >=", value, "purchaseInvoiceDate");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDateLessThan(LocalDate value) {
            addCriterion("purchase_invoice_date <", value, "purchaseInvoiceDate");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDateLessThanOrEqualTo(LocalDate value) {
            addCriterion("purchase_invoice_date <=", value, "purchaseInvoiceDate");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDateIn(List<LocalDate> values) {
            addCriterion("purchase_invoice_date in", values, "purchaseInvoiceDate");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDateNotIn(List<LocalDate> values) {
            addCriterion("purchase_invoice_date not in", values, "purchaseInvoiceDate");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDateBetween(LocalDate value1, LocalDate value2) {
            addCriterion("purchase_invoice_date between", value1, value2, "purchaseInvoiceDate");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDateNotBetween(LocalDate value1, LocalDate value2) {
            addCriterion("purchase_invoice_date not between", value1, value2, "purchaseInvoiceDate");
            return (Criteria) this;
        }

        public Criteria andSupplierIdIsNull() {
            addCriterion("supplier_id is null");
            return (Criteria) this;
        }

        public Criteria andSupplierIdIsNotNull() {
            addCriterion("supplier_id is not null");
            return (Criteria) this;
        }

        public Criteria andSupplierIdEqualTo(Long value) {
            addCriterion("supplier_id =", value, "supplierId");
            return (Criteria) this;
        }

        public Criteria andSupplierIdNotEqualTo(Long value) {
            addCriterion("supplier_id <>", value, "supplierId");
            return (Criteria) this;
        }

        public Criteria andSupplierIdGreaterThan(Long value) {
            addCriterion("supplier_id >", value, "supplierId");
            return (Criteria) this;
        }

        public Criteria andSupplierIdGreaterThanOrEqualTo(Long value) {
            addCriterion("supplier_id >=", value, "supplierId");
            return (Criteria) this;
        }

        public Criteria andSupplierIdLessThan(Long value) {
            addCriterion("supplier_id <", value, "supplierId");
            return (Criteria) this;
        }

        public Criteria andSupplierIdLessThanOrEqualTo(Long value) {
            addCriterion("supplier_id <=", value, "supplierId");
            return (Criteria) this;
        }

        public Criteria andSupplierIdIn(List<Long> values) {
            addCriterion("supplier_id in", values, "supplierId");
            return (Criteria) this;
        }

        public Criteria andSupplierIdNotIn(List<Long> values) {
            addCriterion("supplier_id not in", values, "supplierId");
            return (Criteria) this;
        }

        public Criteria andSupplierIdBetween(Long value1, Long value2) {
            addCriterion("supplier_id between", value1, value2, "supplierId");
            return (Criteria) this;
        }

        public Criteria andSupplierIdNotBetween(Long value1, Long value2) {
            addCriterion("supplier_id not between", value1, value2, "supplierId");
            return (Criteria) this;
        }

        public Criteria andReceivingStoreIdIsNull() {
            addCriterion("receiving_store_id is null");
            return (Criteria) this;
        }

        public Criteria andReceivingStoreIdIsNotNull() {
            addCriterion("receiving_store_id is not null");
            return (Criteria) this;
        }

        public Criteria andReceivingStoreIdEqualTo(Long value) {
            addCriterion("receiving_store_id =", value, "receivingStoreId");
            return (Criteria) this;
        }

        public Criteria andReceivingStoreIdNotEqualTo(Long value) {
            addCriterion("receiving_store_id <>", value, "receivingStoreId");
            return (Criteria) this;
        }

        public Criteria andReceivingStoreIdGreaterThan(Long value) {
            addCriterion("receiving_store_id >", value, "receivingStoreId");
            return (Criteria) this;
        }

        public Criteria andReceivingStoreIdGreaterThanOrEqualTo(Long value) {
            addCriterion("receiving_store_id >=", value, "receivingStoreId");
            return (Criteria) this;
        }

        public Criteria andReceivingStoreIdLessThan(Long value) {
            addCriterion("receiving_store_id <", value, "receivingStoreId");
            return (Criteria) this;
        }

        public Criteria andReceivingStoreIdLessThanOrEqualTo(Long value) {
            addCriterion("receiving_store_id <=", value, "receivingStoreId");
            return (Criteria) this;
        }

        public Criteria andReceivingStoreIdIn(List<Long> values) {
            addCriterion("receiving_store_id in", values, "receivingStoreId");
            return (Criteria) this;
        }

        public Criteria andReceivingStoreIdNotIn(List<Long> values) {
            addCriterion("receiving_store_id not in", values, "receivingStoreId");
            return (Criteria) this;
        }

        public Criteria andReceivingStoreIdBetween(Long value1, Long value2) {
            addCriterion("receiving_store_id between", value1, value2, "receivingStoreId");
            return (Criteria) this;
        }

        public Criteria andReceivingStoreIdNotBetween(Long value1, Long value2) {
            addCriterion("receiving_store_id not between", value1, value2, "receivingStoreId");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceAmountIsNull() {
            addCriterion("purchase_invoice_amount is null");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceAmountIsNotNull() {
            addCriterion("purchase_invoice_amount is not null");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceAmountEqualTo(Long value) {
            addCriterion("purchase_invoice_amount =", value, "purchaseInvoiceAmount");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceAmountNotEqualTo(Long value) {
            addCriterion("purchase_invoice_amount <>", value, "purchaseInvoiceAmount");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceAmountGreaterThan(Long value) {
            addCriterion("purchase_invoice_amount >", value, "purchaseInvoiceAmount");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceAmountGreaterThanOrEqualTo(Long value) {
            addCriterion("purchase_invoice_amount >=", value, "purchaseInvoiceAmount");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceAmountLessThan(Long value) {
            addCriterion("purchase_invoice_amount <", value, "purchaseInvoiceAmount");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceAmountLessThanOrEqualTo(Long value) {
            addCriterion("purchase_invoice_amount <=", value, "purchaseInvoiceAmount");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceAmountIn(List<Long> values) {
            addCriterion("purchase_invoice_amount in", values, "purchaseInvoiceAmount");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceAmountNotIn(List<Long> values) {
            addCriterion("purchase_invoice_amount not in", values, "purchaseInvoiceAmount");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceAmountBetween(Long value1, Long value2) {
            addCriterion("purchase_invoice_amount between", value1, value2, "purchaseInvoiceAmount");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceAmountNotBetween(Long value1, Long value2) {
            addCriterion("purchase_invoice_amount not between", value1, value2, "purchaseInvoiceAmount");
            return (Criteria) this;
        }

        public Criteria andCreateAtIsNull() {
            addCriterion("create_at is null");
            return (Criteria) this;
        }

        public Criteria andCreateAtIsNotNull() {
            addCriterion("create_at is not null");
            return (Criteria) this;
        }

        public Criteria andCreateAtEqualTo(LocalDateTime value) {
            addCriterion("create_at =", value, "createAt");
            return (Criteria) this;
        }

        public Criteria andCreateAtNotEqualTo(LocalDateTime value) {
            addCriterion("create_at <>", value, "createAt");
            return (Criteria) this;
        }

        public Criteria andCreateAtGreaterThan(LocalDateTime value) {
            addCriterion("create_at >", value, "createAt");
            return (Criteria) this;
        }

        public Criteria andCreateAtGreaterThanOrEqualTo(LocalDateTime value) {
            addCriterion("create_at >=", value, "createAt");
            return (Criteria) this;
        }

        public Criteria andCreateAtLessThan(LocalDateTime value) {
            addCriterion("create_at <", value, "createAt");
            return (Criteria) this;
        }

        public Criteria andCreateAtLessThanOrEqualTo(LocalDateTime value) {
            addCriterion("create_at <=", value, "createAt");
            return (Criteria) this;
        }

        public Criteria andCreateAtIn(List<LocalDateTime> values) {
            addCriterion("create_at in", values, "createAt");
            return (Criteria) this;
        }

        public Criteria andCreateAtNotIn(List<LocalDateTime> values) {
            addCriterion("create_at not in", values, "createAt");
            return (Criteria) this;
        }

        public Criteria andCreateAtBetween(LocalDateTime value1, LocalDateTime value2) {
            addCriterion("create_at between", value1, value2, "createAt");
            return (Criteria) this;
        }

        public Criteria andCreateAtNotBetween(LocalDateTime value1, LocalDateTime value2) {
            addCriterion("create_at not between", value1, value2, "createAt");
            return (Criteria) this;
        }

        public Criteria andUpdateAtIsNull() {
            addCriterion("update_at is null");
            return (Criteria) this;
        }

        public Criteria andUpdateAtIsNotNull() {
            addCriterion("update_at is not null");
            return (Criteria) this;
        }

        public Criteria andUpdateAtEqualTo(LocalDateTime value) {
            addCriterion("update_at =", value, "updateAt");
            return (Criteria) this;
        }

        public Criteria andUpdateAtNotEqualTo(LocalDateTime value) {
            addCriterion("update_at <>", value, "updateAt");
            return (Criteria) this;
        }

        public Criteria andUpdateAtGreaterThan(LocalDateTime value) {
            addCriterion("update_at >", value, "updateAt");
            return (Criteria) this;
        }

        public Criteria andUpdateAtGreaterThanOrEqualTo(LocalDateTime value) {
            addCriterion("update_at >=", value, "updateAt");
            return (Criteria) this;
        }

        public Criteria andUpdateAtLessThan(LocalDateTime value) {
            addCriterion("update_at <", value, "updateAt");
            return (Criteria) this;
        }

        public Criteria andUpdateAtLessThanOrEqualTo(LocalDateTime value) {
            addCriterion("update_at <=", value, "updateAt");
            return (Criteria) this;
        }

        public Criteria andUpdateAtIn(List<LocalDateTime> values) {
            addCriterion("update_at in", values, "updateAt");
            return (Criteria) this;
        }

        public Criteria andUpdateAtNotIn(List<LocalDateTime> values) {
            addCriterion("update_at not in", values, "updateAt");
            return (Criteria) this;
        }

        public Criteria andUpdateAtBetween(LocalDateTime value1, LocalDateTime value2) {
            addCriterion("update_at between", value1, value2, "updateAt");
            return (Criteria) this;
        }

        public Criteria andUpdateAtNotBetween(LocalDateTime value1, LocalDateTime value2) {
            addCriterion("update_at not between", value1, value2, "updateAt");
            return (Criteria) this;
        }

        public Criteria andVersionIsNull() {
            addCriterion("version is null");
            return (Criteria) this;
        }

        public Criteria andVersionIsNotNull() {
            addCriterion("version is not null");
            return (Criteria) this;
        }

        public Criteria andVersionEqualTo(Long value) {
            addCriterion("version =", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionNotEqualTo(Long value) {
            addCriterion("version <>", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionGreaterThan(Long value) {
            addCriterion("version >", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionGreaterThanOrEqualTo(Long value) {
            addCriterion("version >=", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionLessThan(Long value) {
            addCriterion("version <", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionLessThanOrEqualTo(Long value) {
            addCriterion("version <=", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionIn(List<Long> values) {
            addCriterion("version in", values, "version");
            return (Criteria) this;
        }

        public Criteria andVersionNotIn(List<Long> values) {
            addCriterion("version not in", values, "version");
            return (Criteria) this;
        }

        public Criteria andVersionBetween(Long value1, Long value2) {
            addCriterion("version between", value1, value2, "version");
            return (Criteria) this;
        }

        public Criteria andVersionNotBetween(Long value1, Long value2) {
            addCriterion("version not between", value1, value2, "version");
            return (Criteria) this;
        }
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="do_not_delete_during_merge")
    public static class Criteria extends GeneratedCriteria {
        protected Criteria() {
            super();
        }
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_invoice")
    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}