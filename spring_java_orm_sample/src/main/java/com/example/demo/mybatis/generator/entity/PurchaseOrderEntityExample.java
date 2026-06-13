package com.example.demo.mybatis.generator.entity;

import com.example.demo.data.domain.PurchaseOrderType;
import jakarta.annotation.Generated;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderEntityExample {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order")
    protected String orderByClause;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order")
    protected boolean distinct;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order")
    protected List<Criteria> oredCriteria;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order")
    public PurchaseOrderEntityExample() {
        oredCriteria = new ArrayList<>();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order")
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order")
    public String getOrderByClause() {
        return orderByClause;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order")
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order")
    public boolean isDistinct() {
        return distinct;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order")
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order")
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order")
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order")
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order")
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order")
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order")
    protected abstract static class GeneratedCriteria {
        protected List<Criterion> purchaseOrderTypeCriteria;

        protected List<Criterion> allCriteria;

        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<>();
            purchaseOrderTypeCriteria = new ArrayList<>();
        }

        public List<Criterion> getPurchaseOrderTypeCriteria() {
            return purchaseOrderTypeCriteria;
        }

        protected void addPurchaseOrderTypeCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            purchaseOrderTypeCriteria.add(new Criterion(condition, value, "com.example.demo.mybatis.typehandler.PurchaseOrderTypeHandler"));
            allCriteria = null;
        }

        protected void addPurchaseOrderTypeCriterion(String condition, PurchaseOrderType value1, PurchaseOrderType value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            purchaseOrderTypeCriteria.add(new Criterion(condition, value1, value2, "com.example.demo.mybatis.typehandler.PurchaseOrderTypeHandler"));
            allCriteria = null;
        }

        public boolean isValid() {
            return criteria.size() > 0
                || purchaseOrderTypeCriteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            if (allCriteria == null) {
                allCriteria = new ArrayList<>();
                allCriteria.addAll(criteria);
                allCriteria.addAll(purchaseOrderTypeCriteria);
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

        public Criteria andPurchaseOrderTypeIsNull() {
            addCriterion("purchase_order_type is null");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderTypeIsNotNull() {
            addCriterion("purchase_order_type is not null");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderTypeEqualTo(PurchaseOrderType value) {
            addPurchaseOrderTypeCriterion("purchase_order_type =", value, "purchaseOrderType");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderTypeNotEqualTo(PurchaseOrderType value) {
            addPurchaseOrderTypeCriterion("purchase_order_type <>", value, "purchaseOrderType");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderTypeGreaterThan(PurchaseOrderType value) {
            addPurchaseOrderTypeCriterion("purchase_order_type >", value, "purchaseOrderType");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderTypeGreaterThanOrEqualTo(PurchaseOrderType value) {
            addPurchaseOrderTypeCriterion("purchase_order_type >=", value, "purchaseOrderType");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderTypeLessThan(PurchaseOrderType value) {
            addPurchaseOrderTypeCriterion("purchase_order_type <", value, "purchaseOrderType");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderTypeLessThanOrEqualTo(PurchaseOrderType value) {
            addPurchaseOrderTypeCriterion("purchase_order_type <=", value, "purchaseOrderType");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderTypeIn(List<PurchaseOrderType> values) {
            addPurchaseOrderTypeCriterion("purchase_order_type in", values, "purchaseOrderType");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderTypeNotIn(List<PurchaseOrderType> values) {
            addPurchaseOrderTypeCriterion("purchase_order_type not in", values, "purchaseOrderType");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderTypeBetween(PurchaseOrderType value1, PurchaseOrderType value2) {
            addPurchaseOrderTypeCriterion("purchase_order_type between", value1, value2, "purchaseOrderType");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderTypeNotBetween(PurchaseOrderType value1, PurchaseOrderType value2) {
            addPurchaseOrderTypeCriterion("purchase_order_type not between", value1, value2, "purchaseOrderType");
            return (Criteria) this;
        }

        public Criteria andReturnPurchaseOrderIdIsNull() {
            addCriterion("return_purchase_order_id is null");
            return (Criteria) this;
        }

        public Criteria andReturnPurchaseOrderIdIsNotNull() {
            addCriterion("return_purchase_order_id is not null");
            return (Criteria) this;
        }

        public Criteria andReturnPurchaseOrderIdEqualTo(Long value) {
            addCriterion("return_purchase_order_id =", value, "returnPurchaseOrderId");
            return (Criteria) this;
        }

        public Criteria andReturnPurchaseOrderIdNotEqualTo(Long value) {
            addCriterion("return_purchase_order_id <>", value, "returnPurchaseOrderId");
            return (Criteria) this;
        }

        public Criteria andReturnPurchaseOrderIdGreaterThan(Long value) {
            addCriterion("return_purchase_order_id >", value, "returnPurchaseOrderId");
            return (Criteria) this;
        }

        public Criteria andReturnPurchaseOrderIdGreaterThanOrEqualTo(Long value) {
            addCriterion("return_purchase_order_id >=", value, "returnPurchaseOrderId");
            return (Criteria) this;
        }

        public Criteria andReturnPurchaseOrderIdLessThan(Long value) {
            addCriterion("return_purchase_order_id <", value, "returnPurchaseOrderId");
            return (Criteria) this;
        }

        public Criteria andReturnPurchaseOrderIdLessThanOrEqualTo(Long value) {
            addCriterion("return_purchase_order_id <=", value, "returnPurchaseOrderId");
            return (Criteria) this;
        }

        public Criteria andReturnPurchaseOrderIdIn(List<Long> values) {
            addCriterion("return_purchase_order_id in", values, "returnPurchaseOrderId");
            return (Criteria) this;
        }

        public Criteria andReturnPurchaseOrderIdNotIn(List<Long> values) {
            addCriterion("return_purchase_order_id not in", values, "returnPurchaseOrderId");
            return (Criteria) this;
        }

        public Criteria andReturnPurchaseOrderIdBetween(Long value1, Long value2) {
            addCriterion("return_purchase_order_id between", value1, value2, "returnPurchaseOrderId");
            return (Criteria) this;
        }

        public Criteria andReturnPurchaseOrderIdNotBetween(Long value1, Long value2) {
            addCriterion("return_purchase_order_id not between", value1, value2, "returnPurchaseOrderId");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDateIsNull() {
            addCriterion("purchase_order_date is null");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDateIsNotNull() {
            addCriterion("purchase_order_date is not null");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDateEqualTo(LocalDate value) {
            addCriterion("purchase_order_date =", value, "purchaseOrderDate");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDateNotEqualTo(LocalDate value) {
            addCriterion("purchase_order_date <>", value, "purchaseOrderDate");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDateGreaterThan(LocalDate value) {
            addCriterion("purchase_order_date >", value, "purchaseOrderDate");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDateGreaterThanOrEqualTo(LocalDate value) {
            addCriterion("purchase_order_date >=", value, "purchaseOrderDate");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDateLessThan(LocalDate value) {
            addCriterion("purchase_order_date <", value, "purchaseOrderDate");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDateLessThanOrEqualTo(LocalDate value) {
            addCriterion("purchase_order_date <=", value, "purchaseOrderDate");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDateIn(List<LocalDate> values) {
            addCriterion("purchase_order_date in", values, "purchaseOrderDate");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDateNotIn(List<LocalDate> values) {
            addCriterion("purchase_order_date not in", values, "purchaseOrderDate");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDateBetween(LocalDate value1, LocalDate value2) {
            addCriterion("purchase_order_date between", value1, value2, "purchaseOrderDate");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDateNotBetween(LocalDate value1, LocalDate value2) {
            addCriterion("purchase_order_date not between", value1, value2, "purchaseOrderDate");
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

        public Criteria andPurchaseOrderAmountIsNull() {
            addCriterion("purchase_order_amount is null");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderAmountIsNotNull() {
            addCriterion("purchase_order_amount is not null");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderAmountEqualTo(Long value) {
            addCriterion("purchase_order_amount =", value, "purchaseOrderAmount");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderAmountNotEqualTo(Long value) {
            addCriterion("purchase_order_amount <>", value, "purchaseOrderAmount");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderAmountGreaterThan(Long value) {
            addCriterion("purchase_order_amount >", value, "purchaseOrderAmount");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderAmountGreaterThanOrEqualTo(Long value) {
            addCriterion("purchase_order_amount >=", value, "purchaseOrderAmount");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderAmountLessThan(Long value) {
            addCriterion("purchase_order_amount <", value, "purchaseOrderAmount");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderAmountLessThanOrEqualTo(Long value) {
            addCriterion("purchase_order_amount <=", value, "purchaseOrderAmount");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderAmountIn(List<Long> values) {
            addCriterion("purchase_order_amount in", values, "purchaseOrderAmount");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderAmountNotIn(List<Long> values) {
            addCriterion("purchase_order_amount not in", values, "purchaseOrderAmount");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderAmountBetween(Long value1, Long value2) {
            addCriterion("purchase_order_amount between", value1, value2, "purchaseOrderAmount");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderAmountNotBetween(Long value1, Long value2) {
            addCriterion("purchase_order_amount not between", value1, value2, "purchaseOrderAmount");
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

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order")
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