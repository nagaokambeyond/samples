package com.example.demo.mybatis.generator.entity;

import jakarta.annotation.Generated;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookSalesUnitPriceHistoryEntityExample {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_sales_unit_price_history")
    protected String orderByClause;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_sales_unit_price_history")
    protected boolean distinct;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_sales_unit_price_history")
    protected List<Criteria> oredCriteria;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_sales_unit_price_history")
    public BookSalesUnitPriceHistoryEntityExample() {
        oredCriteria = new ArrayList<>();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_sales_unit_price_history")
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_sales_unit_price_history")
    public String getOrderByClause() {
        return orderByClause;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_sales_unit_price_history")
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_sales_unit_price_history")
    public boolean isDistinct() {
        return distinct;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_sales_unit_price_history")
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_sales_unit_price_history")
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_sales_unit_price_history")
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_sales_unit_price_history")
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_sales_unit_price_history")
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_sales_unit_price_history")
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_sales_unit_price_history")
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

        public Criteria andBookIdIsNull() {
            addCriterion("book_id is null");
            return (Criteria) this;
        }

        public Criteria andBookIdIsNotNull() {
            addCriterion("book_id is not null");
            return (Criteria) this;
        }

        public Criteria andBookIdEqualTo(Long value) {
            addCriterion("book_id =", value, "bookId");
            return (Criteria) this;
        }

        public Criteria andBookIdNotEqualTo(Long value) {
            addCriterion("book_id <>", value, "bookId");
            return (Criteria) this;
        }

        public Criteria andBookIdGreaterThan(Long value) {
            addCriterion("book_id >", value, "bookId");
            return (Criteria) this;
        }

        public Criteria andBookIdGreaterThanOrEqualTo(Long value) {
            addCriterion("book_id >=", value, "bookId");
            return (Criteria) this;
        }

        public Criteria andBookIdLessThan(Long value) {
            addCriterion("book_id <", value, "bookId");
            return (Criteria) this;
        }

        public Criteria andBookIdLessThanOrEqualTo(Long value) {
            addCriterion("book_id <=", value, "bookId");
            return (Criteria) this;
        }

        public Criteria andBookIdIn(List<Long> values) {
            addCriterion("book_id in", values, "bookId");
            return (Criteria) this;
        }

        public Criteria andBookIdNotIn(List<Long> values) {
            addCriterion("book_id not in", values, "bookId");
            return (Criteria) this;
        }

        public Criteria andBookIdBetween(Long value1, Long value2) {
            addCriterion("book_id between", value1, value2, "bookId");
            return (Criteria) this;
        }

        public Criteria andBookIdNotBetween(Long value1, Long value2) {
            addCriterion("book_id not between", value1, value2, "bookId");
            return (Criteria) this;
        }

        public Criteria andSalesUnitPriceIsNull() {
            addCriterion("sales_unit_price is null");
            return (Criteria) this;
        }

        public Criteria andSalesUnitPriceIsNotNull() {
            addCriterion("sales_unit_price is not null");
            return (Criteria) this;
        }

        public Criteria andSalesUnitPriceEqualTo(Integer value) {
            addCriterion("sales_unit_price =", value, "salesUnitPrice");
            return (Criteria) this;
        }

        public Criteria andSalesUnitPriceNotEqualTo(Integer value) {
            addCriterion("sales_unit_price <>", value, "salesUnitPrice");
            return (Criteria) this;
        }

        public Criteria andSalesUnitPriceGreaterThan(Integer value) {
            addCriterion("sales_unit_price >", value, "salesUnitPrice");
            return (Criteria) this;
        }

        public Criteria andSalesUnitPriceGreaterThanOrEqualTo(Integer value) {
            addCriterion("sales_unit_price >=", value, "salesUnitPrice");
            return (Criteria) this;
        }

        public Criteria andSalesUnitPriceLessThan(Integer value) {
            addCriterion("sales_unit_price <", value, "salesUnitPrice");
            return (Criteria) this;
        }

        public Criteria andSalesUnitPriceLessThanOrEqualTo(Integer value) {
            addCriterion("sales_unit_price <=", value, "salesUnitPrice");
            return (Criteria) this;
        }

        public Criteria andSalesUnitPriceIn(List<Integer> values) {
            addCriterion("sales_unit_price in", values, "salesUnitPrice");
            return (Criteria) this;
        }

        public Criteria andSalesUnitPriceNotIn(List<Integer> values) {
            addCriterion("sales_unit_price not in", values, "salesUnitPrice");
            return (Criteria) this;
        }

        public Criteria andSalesUnitPriceBetween(Integer value1, Integer value2) {
            addCriterion("sales_unit_price between", value1, value2, "salesUnitPrice");
            return (Criteria) this;
        }

        public Criteria andSalesUnitPriceNotBetween(Integer value1, Integer value2) {
            addCriterion("sales_unit_price not between", value1, value2, "salesUnitPrice");
            return (Criteria) this;
        }

        public Criteria andEffectiveFromIsNull() {
            addCriterion("effective_from is null");
            return (Criteria) this;
        }

        public Criteria andEffectiveFromIsNotNull() {
            addCriterion("effective_from is not null");
            return (Criteria) this;
        }

        public Criteria andEffectiveFromEqualTo(LocalDate value) {
            addCriterion("effective_from =", value, "effectiveFrom");
            return (Criteria) this;
        }

        public Criteria andEffectiveFromNotEqualTo(LocalDate value) {
            addCriterion("effective_from <>", value, "effectiveFrom");
            return (Criteria) this;
        }

        public Criteria andEffectiveFromGreaterThan(LocalDate value) {
            addCriterion("effective_from >", value, "effectiveFrom");
            return (Criteria) this;
        }

        public Criteria andEffectiveFromGreaterThanOrEqualTo(LocalDate value) {
            addCriterion("effective_from >=", value, "effectiveFrom");
            return (Criteria) this;
        }

        public Criteria andEffectiveFromLessThan(LocalDate value) {
            addCriterion("effective_from <", value, "effectiveFrom");
            return (Criteria) this;
        }

        public Criteria andEffectiveFromLessThanOrEqualTo(LocalDate value) {
            addCriterion("effective_from <=", value, "effectiveFrom");
            return (Criteria) this;
        }

        public Criteria andEffectiveFromIn(List<LocalDate> values) {
            addCriterion("effective_from in", values, "effectiveFrom");
            return (Criteria) this;
        }

        public Criteria andEffectiveFromNotIn(List<LocalDate> values) {
            addCriterion("effective_from not in", values, "effectiveFrom");
            return (Criteria) this;
        }

        public Criteria andEffectiveFromBetween(LocalDate value1, LocalDate value2) {
            addCriterion("effective_from between", value1, value2, "effectiveFrom");
            return (Criteria) this;
        }

        public Criteria andEffectiveFromNotBetween(LocalDate value1, LocalDate value2) {
            addCriterion("effective_from not between", value1, value2, "effectiveFrom");
            return (Criteria) this;
        }

        public Criteria andEffectiveToIsNull() {
            addCriterion("effective_to is null");
            return (Criteria) this;
        }

        public Criteria andEffectiveToIsNotNull() {
            addCriterion("effective_to is not null");
            return (Criteria) this;
        }

        public Criteria andEffectiveToEqualTo(LocalDate value) {
            addCriterion("effective_to =", value, "effectiveTo");
            return (Criteria) this;
        }

        public Criteria andEffectiveToNotEqualTo(LocalDate value) {
            addCriterion("effective_to <>", value, "effectiveTo");
            return (Criteria) this;
        }

        public Criteria andEffectiveToGreaterThan(LocalDate value) {
            addCriterion("effective_to >", value, "effectiveTo");
            return (Criteria) this;
        }

        public Criteria andEffectiveToGreaterThanOrEqualTo(LocalDate value) {
            addCriterion("effective_to >=", value, "effectiveTo");
            return (Criteria) this;
        }

        public Criteria andEffectiveToLessThan(LocalDate value) {
            addCriterion("effective_to <", value, "effectiveTo");
            return (Criteria) this;
        }

        public Criteria andEffectiveToLessThanOrEqualTo(LocalDate value) {
            addCriterion("effective_to <=", value, "effectiveTo");
            return (Criteria) this;
        }

        public Criteria andEffectiveToIn(List<LocalDate> values) {
            addCriterion("effective_to in", values, "effectiveTo");
            return (Criteria) this;
        }

        public Criteria andEffectiveToNotIn(List<LocalDate> values) {
            addCriterion("effective_to not in", values, "effectiveTo");
            return (Criteria) this;
        }

        public Criteria andEffectiveToBetween(LocalDate value1, LocalDate value2) {
            addCriterion("effective_to between", value1, value2, "effectiveTo");
            return (Criteria) this;
        }

        public Criteria andEffectiveToNotBetween(LocalDate value1, LocalDate value2) {
            addCriterion("effective_to not between", value1, value2, "effectiveTo");
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

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_sales_unit_price_history")
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