package com.example.demo.mybatis.generator.entity;

import jakarta.annotation.Generated;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderDetailEntityExample {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_invoice_detail")
    protected String orderByClause;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_invoice_detail")
    protected boolean distinct;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_invoice_detail")
    protected List<Criteria> oredCriteria;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_invoice_detail")
    public PurchaseOrderDetailEntityExample() {
        oredCriteria = new ArrayList<>();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_invoice_detail")
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_invoice_detail")
    public String getOrderByClause() {
        return orderByClause;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_invoice_detail")
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_invoice_detail")
    public boolean isDistinct() {
        return distinct;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_invoice_detail")
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_invoice_detail")
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_invoice_detail")
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_invoice_detail")
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_invoice_detail")
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_invoice_detail")
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_invoice_detail")
    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
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

        public Criteria andPurchaseInvoiceIdIsNull() {
            addCriterion("purchase_invoice_id is null");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceIdIsNotNull() {
            addCriterion("purchase_invoice_id is not null");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceIdEqualTo(Long value) {
            addCriterion("purchase_invoice_id =", value, "purchaseInvoiceId");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceIdNotEqualTo(Long value) {
            addCriterion("purchase_invoice_id <>", value, "purchaseInvoiceId");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceIdGreaterThan(Long value) {
            addCriterion("purchase_invoice_id >", value, "purchaseInvoiceId");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceIdGreaterThanOrEqualTo(Long value) {
            addCriterion("purchase_invoice_id >=", value, "purchaseInvoiceId");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceIdLessThan(Long value) {
            addCriterion("purchase_invoice_id <", value, "purchaseInvoiceId");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceIdLessThanOrEqualTo(Long value) {
            addCriterion("purchase_invoice_id <=", value, "purchaseInvoiceId");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceIdIn(List<Long> values) {
            addCriterion("purchase_invoice_id in", values, "purchaseInvoiceId");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceIdNotIn(List<Long> values) {
            addCriterion("purchase_invoice_id not in", values, "purchaseInvoiceId");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceIdBetween(Long value1, Long value2) {
            addCriterion("purchase_invoice_id between", value1, value2, "purchaseInvoiceId");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceIdNotBetween(Long value1, Long value2) {
            addCriterion("purchase_invoice_id not between", value1, value2, "purchaseInvoiceId");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailBookIdIsNull() {
            addCriterion("purchase_invoice_detail_book_id is null");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailBookIdIsNotNull() {
            addCriterion("purchase_invoice_detail_book_id is not null");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailBookIdEqualTo(Long value) {
            addCriterion("purchase_invoice_detail_book_id =", value, "purchaseInvoiceDetailBookId");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailBookIdNotEqualTo(Long value) {
            addCriterion("purchase_invoice_detail_book_id <>", value, "purchaseInvoiceDetailBookId");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailBookIdGreaterThan(Long value) {
            addCriterion("purchase_invoice_detail_book_id >", value, "purchaseInvoiceDetailBookId");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailBookIdGreaterThanOrEqualTo(Long value) {
            addCriterion("purchase_invoice_detail_book_id >=", value, "purchaseInvoiceDetailBookId");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailBookIdLessThan(Long value) {
            addCriterion("purchase_invoice_detail_book_id <", value, "purchaseInvoiceDetailBookId");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailBookIdLessThanOrEqualTo(Long value) {
            addCriterion("purchase_invoice_detail_book_id <=", value, "purchaseInvoiceDetailBookId");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailBookIdIn(List<Long> values) {
            addCriterion("purchase_invoice_detail_book_id in", values, "purchaseInvoiceDetailBookId");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailBookIdNotIn(List<Long> values) {
            addCriterion("purchase_invoice_detail_book_id not in", values, "purchaseInvoiceDetailBookId");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailBookIdBetween(Long value1, Long value2) {
            addCriterion("purchase_invoice_detail_book_id between", value1, value2, "purchaseInvoiceDetailBookId");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailBookIdNotBetween(Long value1, Long value2) {
            addCriterion("purchase_invoice_detail_book_id not between", value1, value2, "purchaseInvoiceDetailBookId");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailUnitPriceIsNull() {
            addCriterion("purchase_invoice_detail_unit_price is null");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailUnitPriceIsNotNull() {
            addCriterion("purchase_invoice_detail_unit_price is not null");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailUnitPriceEqualTo(Integer value) {
            addCriterion("purchase_invoice_detail_unit_price =", value, "purchaseInvoiceDetailUnitPrice");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailUnitPriceNotEqualTo(Integer value) {
            addCriterion("purchase_invoice_detail_unit_price <>", value, "purchaseInvoiceDetailUnitPrice");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailUnitPriceGreaterThan(Integer value) {
            addCriterion("purchase_invoice_detail_unit_price >", value, "purchaseInvoiceDetailUnitPrice");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailUnitPriceGreaterThanOrEqualTo(Integer value) {
            addCriterion("purchase_invoice_detail_unit_price >=", value, "purchaseInvoiceDetailUnitPrice");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailUnitPriceLessThan(Integer value) {
            addCriterion("purchase_invoice_detail_unit_price <", value, "purchaseInvoiceDetailUnitPrice");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailUnitPriceLessThanOrEqualTo(Integer value) {
            addCriterion("purchase_invoice_detail_unit_price <=", value, "purchaseInvoiceDetailUnitPrice");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailUnitPriceIn(List<Integer> values) {
            addCriterion("purchase_invoice_detail_unit_price in", values, "purchaseInvoiceDetailUnitPrice");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailUnitPriceNotIn(List<Integer> values) {
            addCriterion("purchase_invoice_detail_unit_price not in", values, "purchaseInvoiceDetailUnitPrice");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailUnitPriceBetween(Integer value1, Integer value2) {
            addCriterion("purchase_invoice_detail_unit_price between", value1, value2, "purchaseInvoiceDetailUnitPrice");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailUnitPriceNotBetween(Integer value1, Integer value2) {
            addCriterion("purchase_invoice_detail_unit_price not between", value1, value2, "purchaseInvoiceDetailUnitPrice");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailQuantityIsNull() {
            addCriterion("purchase_invoice_detail_quantity is null");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailQuantityIsNotNull() {
            addCriterion("purchase_invoice_detail_quantity is not null");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailQuantityEqualTo(Integer value) {
            addCriterion("purchase_invoice_detail_quantity =", value, "purchaseInvoiceDetailQuantity");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailQuantityNotEqualTo(Integer value) {
            addCriterion("purchase_invoice_detail_quantity <>", value, "purchaseInvoiceDetailQuantity");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailQuantityGreaterThan(Integer value) {
            addCriterion("purchase_invoice_detail_quantity >", value, "purchaseInvoiceDetailQuantity");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailQuantityGreaterThanOrEqualTo(Integer value) {
            addCriterion("purchase_invoice_detail_quantity >=", value, "purchaseInvoiceDetailQuantity");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailQuantityLessThan(Integer value) {
            addCriterion("purchase_invoice_detail_quantity <", value, "purchaseInvoiceDetailQuantity");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailQuantityLessThanOrEqualTo(Integer value) {
            addCriterion("purchase_invoice_detail_quantity <=", value, "purchaseInvoiceDetailQuantity");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailQuantityIn(List<Integer> values) {
            addCriterion("purchase_invoice_detail_quantity in", values, "purchaseInvoiceDetailQuantity");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailQuantityNotIn(List<Integer> values) {
            addCriterion("purchase_invoice_detail_quantity not in", values, "purchaseInvoiceDetailQuantity");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailQuantityBetween(Integer value1, Integer value2) {
            addCriterion("purchase_invoice_detail_quantity between", value1, value2, "purchaseInvoiceDetailQuantity");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailQuantityNotBetween(Integer value1, Integer value2) {
            addCriterion("purchase_invoice_detail_quantity not between", value1, value2, "purchaseInvoiceDetailQuantity");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailAmountIsNull() {
            addCriterion("purchase_invoice_detail_amount is null");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailAmountIsNotNull() {
            addCriterion("purchase_invoice_detail_amount is not null");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailAmountEqualTo(Long value) {
            addCriterion("purchase_invoice_detail_amount =", value, "purchaseInvoiceDetailAmount");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailAmountNotEqualTo(Long value) {
            addCriterion("purchase_invoice_detail_amount <>", value, "purchaseInvoiceDetailAmount");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailAmountGreaterThan(Long value) {
            addCriterion("purchase_invoice_detail_amount >", value, "purchaseInvoiceDetailAmount");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailAmountGreaterThanOrEqualTo(Long value) {
            addCriterion("purchase_invoice_detail_amount >=", value, "purchaseInvoiceDetailAmount");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailAmountLessThan(Long value) {
            addCriterion("purchase_invoice_detail_amount <", value, "purchaseInvoiceDetailAmount");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailAmountLessThanOrEqualTo(Long value) {
            addCriterion("purchase_invoice_detail_amount <=", value, "purchaseInvoiceDetailAmount");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailAmountIn(List<Long> values) {
            addCriterion("purchase_invoice_detail_amount in", values, "purchaseInvoiceDetailAmount");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailAmountNotIn(List<Long> values) {
            addCriterion("purchase_invoice_detail_amount not in", values, "purchaseInvoiceDetailAmount");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailAmountBetween(Long value1, Long value2) {
            addCriterion("purchase_invoice_detail_amount between", value1, value2, "purchaseInvoiceDetailAmount");
            return (Criteria) this;
        }

        public Criteria andPurchaseInvoiceDetailAmountNotBetween(Long value1, Long value2) {
            addCriterion("purchase_invoice_detail_amount not between", value1, value2, "purchaseInvoiceDetailAmount");
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

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_invoice_detail")
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