package com.example.demo.mybatis.generator.entity;

import jakarta.annotation.Generated;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookStockEntityExample {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock")
    protected String orderByClause;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock")
    protected boolean distinct;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock")
    protected List<Criteria> oredCriteria;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock")
    public BookStockEntityExample() {
        oredCriteria = new ArrayList<>();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock")
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock")
    public String getOrderByClause() {
        return orderByClause;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock")
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock")
    public boolean isDistinct() {
        return distinct;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock")
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock")
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock")
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock")
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock")
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock")
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock")
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

        public Criteria andBookStockStoreIdIsNull() {
            addCriterion("book_stock_store_id is null");
            return (Criteria) this;
        }

        public Criteria andBookStockStoreIdIsNotNull() {
            addCriterion("book_stock_store_id is not null");
            return (Criteria) this;
        }

        public Criteria andBookStockStoreIdEqualTo(Long value) {
            addCriterion("book_stock_store_id =", value, "bookStockStoreId");
            return (Criteria) this;
        }

        public Criteria andBookStockStoreIdNotEqualTo(Long value) {
            addCriterion("book_stock_store_id <>", value, "bookStockStoreId");
            return (Criteria) this;
        }

        public Criteria andBookStockStoreIdGreaterThan(Long value) {
            addCriterion("book_stock_store_id >", value, "bookStockStoreId");
            return (Criteria) this;
        }

        public Criteria andBookStockStoreIdGreaterThanOrEqualTo(Long value) {
            addCriterion("book_stock_store_id >=", value, "bookStockStoreId");
            return (Criteria) this;
        }

        public Criteria andBookStockStoreIdLessThan(Long value) {
            addCriterion("book_stock_store_id <", value, "bookStockStoreId");
            return (Criteria) this;
        }

        public Criteria andBookStockStoreIdLessThanOrEqualTo(Long value) {
            addCriterion("book_stock_store_id <=", value, "bookStockStoreId");
            return (Criteria) this;
        }

        public Criteria andBookStockStoreIdIn(List<Long> values) {
            addCriterion("book_stock_store_id in", values, "bookStockStoreId");
            return (Criteria) this;
        }

        public Criteria andBookStockStoreIdNotIn(List<Long> values) {
            addCriterion("book_stock_store_id not in", values, "bookStockStoreId");
            return (Criteria) this;
        }

        public Criteria andBookStockStoreIdBetween(Long value1, Long value2) {
            addCriterion("book_stock_store_id between", value1, value2, "bookStockStoreId");
            return (Criteria) this;
        }

        public Criteria andBookStockStoreIdNotBetween(Long value1, Long value2) {
            addCriterion("book_stock_store_id not between", value1, value2, "bookStockStoreId");
            return (Criteria) this;
        }

        public Criteria andBookStockBookIdIsNull() {
            addCriterion("book_stock_book_id is null");
            return (Criteria) this;
        }

        public Criteria andBookStockBookIdIsNotNull() {
            addCriterion("book_stock_book_id is not null");
            return (Criteria) this;
        }

        public Criteria andBookStockBookIdEqualTo(Long value) {
            addCriterion("book_stock_book_id =", value, "bookStockBookId");
            return (Criteria) this;
        }

        public Criteria andBookStockBookIdNotEqualTo(Long value) {
            addCriterion("book_stock_book_id <>", value, "bookStockBookId");
            return (Criteria) this;
        }

        public Criteria andBookStockBookIdGreaterThan(Long value) {
            addCriterion("book_stock_book_id >", value, "bookStockBookId");
            return (Criteria) this;
        }

        public Criteria andBookStockBookIdGreaterThanOrEqualTo(Long value) {
            addCriterion("book_stock_book_id >=", value, "bookStockBookId");
            return (Criteria) this;
        }

        public Criteria andBookStockBookIdLessThan(Long value) {
            addCriterion("book_stock_book_id <", value, "bookStockBookId");
            return (Criteria) this;
        }

        public Criteria andBookStockBookIdLessThanOrEqualTo(Long value) {
            addCriterion("book_stock_book_id <=", value, "bookStockBookId");
            return (Criteria) this;
        }

        public Criteria andBookStockBookIdIn(List<Long> values) {
            addCriterion("book_stock_book_id in", values, "bookStockBookId");
            return (Criteria) this;
        }

        public Criteria andBookStockBookIdNotIn(List<Long> values) {
            addCriterion("book_stock_book_id not in", values, "bookStockBookId");
            return (Criteria) this;
        }

        public Criteria andBookStockBookIdBetween(Long value1, Long value2) {
            addCriterion("book_stock_book_id between", value1, value2, "bookStockBookId");
            return (Criteria) this;
        }

        public Criteria andBookStockBookIdNotBetween(Long value1, Long value2) {
            addCriterion("book_stock_book_id not between", value1, value2, "bookStockBookId");
            return (Criteria) this;
        }

        public Criteria andBookStockQuantityIsNull() {
            addCriterion("book_stock_quantity is null");
            return (Criteria) this;
        }

        public Criteria andBookStockQuantityIsNotNull() {
            addCriterion("book_stock_quantity is not null");
            return (Criteria) this;
        }

        public Criteria andBookStockQuantityEqualTo(Integer value) {
            addCriterion("book_stock_quantity =", value, "bookStockQuantity");
            return (Criteria) this;
        }

        public Criteria andBookStockQuantityNotEqualTo(Integer value) {
            addCriterion("book_stock_quantity <>", value, "bookStockQuantity");
            return (Criteria) this;
        }

        public Criteria andBookStockQuantityGreaterThan(Integer value) {
            addCriterion("book_stock_quantity >", value, "bookStockQuantity");
            return (Criteria) this;
        }

        public Criteria andBookStockQuantityGreaterThanOrEqualTo(Integer value) {
            addCriterion("book_stock_quantity >=", value, "bookStockQuantity");
            return (Criteria) this;
        }

        public Criteria andBookStockQuantityLessThan(Integer value) {
            addCriterion("book_stock_quantity <", value, "bookStockQuantity");
            return (Criteria) this;
        }

        public Criteria andBookStockQuantityLessThanOrEqualTo(Integer value) {
            addCriterion("book_stock_quantity <=", value, "bookStockQuantity");
            return (Criteria) this;
        }

        public Criteria andBookStockQuantityIn(List<Integer> values) {
            addCriterion("book_stock_quantity in", values, "bookStockQuantity");
            return (Criteria) this;
        }

        public Criteria andBookStockQuantityNotIn(List<Integer> values) {
            addCriterion("book_stock_quantity not in", values, "bookStockQuantity");
            return (Criteria) this;
        }

        public Criteria andBookStockQuantityBetween(Integer value1, Integer value2) {
            addCriterion("book_stock_quantity between", value1, value2, "bookStockQuantity");
            return (Criteria) this;
        }

        public Criteria andBookStockQuantityNotBetween(Integer value1, Integer value2) {
            addCriterion("book_stock_quantity not between", value1, value2, "bookStockQuantity");
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

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock")
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