package com.example.demo.mybatis.generator.entity;

import jakarta.annotation.Generated;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderDetailEntityExample {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order_detail")
    protected String orderByClause;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order_detail")
    protected boolean distinct;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order_detail")
    protected List<Criteria> oredCriteria;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order_detail")
    public PurchaseOrderDetailEntityExample() {
        oredCriteria = new ArrayList<>();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order_detail")
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order_detail")
    public String getOrderByClause() {
        return orderByClause;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order_detail")
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order_detail")
    public boolean isDistinct() {
        return distinct;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order_detail")
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order_detail")
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order_detail")
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order_detail")
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order_detail")
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order_detail")
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order_detail")
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

        public Criteria andPurchaseOrderIdIsNull() {
            addCriterion("purchase_order_id is null");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderIdIsNotNull() {
            addCriterion("purchase_order_id is not null");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderIdEqualTo(Long value) {
            addCriterion("purchase_order_id =", value, "purchaseOrderId");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderIdNotEqualTo(Long value) {
            addCriterion("purchase_order_id <>", value, "purchaseOrderId");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderIdGreaterThan(Long value) {
            addCriterion("purchase_order_id >", value, "purchaseOrderId");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderIdGreaterThanOrEqualTo(Long value) {
            addCriterion("purchase_order_id >=", value, "purchaseOrderId");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderIdLessThan(Long value) {
            addCriterion("purchase_order_id <", value, "purchaseOrderId");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderIdLessThanOrEqualTo(Long value) {
            addCriterion("purchase_order_id <=", value, "purchaseOrderId");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderIdIn(List<Long> values) {
            addCriterion("purchase_order_id in", values, "purchaseOrderId");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderIdNotIn(List<Long> values) {
            addCriterion("purchase_order_id not in", values, "purchaseOrderId");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderIdBetween(Long value1, Long value2) {
            addCriterion("purchase_order_id between", value1, value2, "purchaseOrderId");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderIdNotBetween(Long value1, Long value2) {
            addCriterion("purchase_order_id not between", value1, value2, "purchaseOrderId");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailBookIdIsNull() {
            addCriterion("purchase_order_detail_book_id is null");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailBookIdIsNotNull() {
            addCriterion("purchase_order_detail_book_id is not null");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailBookIdEqualTo(Long value) {
            addCriterion("purchase_order_detail_book_id =", value, "purchaseOrderDetailBookId");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailBookIdNotEqualTo(Long value) {
            addCriterion("purchase_order_detail_book_id <>", value, "purchaseOrderDetailBookId");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailBookIdGreaterThan(Long value) {
            addCriterion("purchase_order_detail_book_id >", value, "purchaseOrderDetailBookId");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailBookIdGreaterThanOrEqualTo(Long value) {
            addCriterion("purchase_order_detail_book_id >=", value, "purchaseOrderDetailBookId");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailBookIdLessThan(Long value) {
            addCriterion("purchase_order_detail_book_id <", value, "purchaseOrderDetailBookId");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailBookIdLessThanOrEqualTo(Long value) {
            addCriterion("purchase_order_detail_book_id <=", value, "purchaseOrderDetailBookId");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailBookIdIn(List<Long> values) {
            addCriterion("purchase_order_detail_book_id in", values, "purchaseOrderDetailBookId");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailBookIdNotIn(List<Long> values) {
            addCriterion("purchase_order_detail_book_id not in", values, "purchaseOrderDetailBookId");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailBookIdBetween(Long value1, Long value2) {
            addCriterion("purchase_order_detail_book_id between", value1, value2, "purchaseOrderDetailBookId");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailBookIdNotBetween(Long value1, Long value2) {
            addCriterion("purchase_order_detail_book_id not between", value1, value2, "purchaseOrderDetailBookId");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailUnitPriceIsNull() {
            addCriterion("purchase_order_detail_unit_price is null");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailUnitPriceIsNotNull() {
            addCriterion("purchase_order_detail_unit_price is not null");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailUnitPriceEqualTo(Integer value) {
            addCriterion("purchase_order_detail_unit_price =", value, "purchaseOrderDetailUnitPrice");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailUnitPriceNotEqualTo(Integer value) {
            addCriterion("purchase_order_detail_unit_price <>", value, "purchaseOrderDetailUnitPrice");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailUnitPriceGreaterThan(Integer value) {
            addCriterion("purchase_order_detail_unit_price >", value, "purchaseOrderDetailUnitPrice");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailUnitPriceGreaterThanOrEqualTo(Integer value) {
            addCriterion("purchase_order_detail_unit_price >=", value, "purchaseOrderDetailUnitPrice");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailUnitPriceLessThan(Integer value) {
            addCriterion("purchase_order_detail_unit_price <", value, "purchaseOrderDetailUnitPrice");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailUnitPriceLessThanOrEqualTo(Integer value) {
            addCriterion("purchase_order_detail_unit_price <=", value, "purchaseOrderDetailUnitPrice");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailUnitPriceIn(List<Integer> values) {
            addCriterion("purchase_order_detail_unit_price in", values, "purchaseOrderDetailUnitPrice");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailUnitPriceNotIn(List<Integer> values) {
            addCriterion("purchase_order_detail_unit_price not in", values, "purchaseOrderDetailUnitPrice");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailUnitPriceBetween(Integer value1, Integer value2) {
            addCriterion("purchase_order_detail_unit_price between", value1, value2, "purchaseOrderDetailUnitPrice");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailUnitPriceNotBetween(Integer value1, Integer value2) {
            addCriterion("purchase_order_detail_unit_price not between", value1, value2, "purchaseOrderDetailUnitPrice");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailQuantityIsNull() {
            addCriterion("purchase_order_detail_quantity is null");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailQuantityIsNotNull() {
            addCriterion("purchase_order_detail_quantity is not null");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailQuantityEqualTo(Integer value) {
            addCriterion("purchase_order_detail_quantity =", value, "purchaseOrderDetailQuantity");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailQuantityNotEqualTo(Integer value) {
            addCriterion("purchase_order_detail_quantity <>", value, "purchaseOrderDetailQuantity");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailQuantityGreaterThan(Integer value) {
            addCriterion("purchase_order_detail_quantity >", value, "purchaseOrderDetailQuantity");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailQuantityGreaterThanOrEqualTo(Integer value) {
            addCriterion("purchase_order_detail_quantity >=", value, "purchaseOrderDetailQuantity");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailQuantityLessThan(Integer value) {
            addCriterion("purchase_order_detail_quantity <", value, "purchaseOrderDetailQuantity");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailQuantityLessThanOrEqualTo(Integer value) {
            addCriterion("purchase_order_detail_quantity <=", value, "purchaseOrderDetailQuantity");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailQuantityIn(List<Integer> values) {
            addCriterion("purchase_order_detail_quantity in", values, "purchaseOrderDetailQuantity");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailQuantityNotIn(List<Integer> values) {
            addCriterion("purchase_order_detail_quantity not in", values, "purchaseOrderDetailQuantity");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailQuantityBetween(Integer value1, Integer value2) {
            addCriterion("purchase_order_detail_quantity between", value1, value2, "purchaseOrderDetailQuantity");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailQuantityNotBetween(Integer value1, Integer value2) {
            addCriterion("purchase_order_detail_quantity not between", value1, value2, "purchaseOrderDetailQuantity");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailAmountIsNull() {
            addCriterion("purchase_order_detail_amount is null");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailAmountIsNotNull() {
            addCriterion("purchase_order_detail_amount is not null");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailAmountEqualTo(Long value) {
            addCriterion("purchase_order_detail_amount =", value, "purchaseOrderDetailAmount");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailAmountNotEqualTo(Long value) {
            addCriterion("purchase_order_detail_amount <>", value, "purchaseOrderDetailAmount");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailAmountGreaterThan(Long value) {
            addCriterion("purchase_order_detail_amount >", value, "purchaseOrderDetailAmount");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailAmountGreaterThanOrEqualTo(Long value) {
            addCriterion("purchase_order_detail_amount >=", value, "purchaseOrderDetailAmount");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailAmountLessThan(Long value) {
            addCriterion("purchase_order_detail_amount <", value, "purchaseOrderDetailAmount");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailAmountLessThanOrEqualTo(Long value) {
            addCriterion("purchase_order_detail_amount <=", value, "purchaseOrderDetailAmount");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailAmountIn(List<Long> values) {
            addCriterion("purchase_order_detail_amount in", values, "purchaseOrderDetailAmount");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailAmountNotIn(List<Long> values) {
            addCriterion("purchase_order_detail_amount not in", values, "purchaseOrderDetailAmount");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailAmountBetween(Long value1, Long value2) {
            addCriterion("purchase_order_detail_amount between", value1, value2, "purchaseOrderDetailAmount");
            return (Criteria) this;
        }

        public Criteria andPurchaseOrderDetailAmountNotBetween(Long value1, Long value2) {
            addCriterion("purchase_order_detail_amount not between", value1, value2, "purchaseOrderDetailAmount");
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

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: purchase_order_detail")
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