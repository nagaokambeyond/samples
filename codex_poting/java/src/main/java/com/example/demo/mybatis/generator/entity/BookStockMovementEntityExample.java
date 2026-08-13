package com.example.demo.mybatis.generator.entity;

import com.example.demo.data.domain.BookStockMovementSourceType;
import com.example.demo.data.domain.BookStockMovementType;
import jakarta.annotation.Generated;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookStockMovementEntityExample {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock_movement")
    protected String orderByClause;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock_movement")
    protected boolean distinct;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock_movement")
    protected List<Criteria> oredCriteria;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock_movement")
    public BookStockMovementEntityExample() {
        oredCriteria = new ArrayList<>();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock_movement")
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock_movement")
    public String getOrderByClause() {
        return orderByClause;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock_movement")
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock_movement")
    public boolean isDistinct() {
        return distinct;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock_movement")
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock_movement")
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock_movement")
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock_movement")
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock_movement")
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock_movement")
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock_movement")
    protected abstract static class GeneratedCriteria {
        protected List<Criterion> movementTypeCriteria;

        protected List<Criterion> sourceTypeCriteria;

        protected List<Criterion> allCriteria;

        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<>();
            movementTypeCriteria = new ArrayList<>();
            sourceTypeCriteria = new ArrayList<>();
        }

        public List<Criterion> getMovementTypeCriteria() {
            return movementTypeCriteria;
        }

        protected void addMovementTypeCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            movementTypeCriteria.add(new Criterion(condition, value, "com.example.demo.mybatis.typehandler.BookStockMovementTypeHandler"));
            allCriteria = null;
        }

        protected void addMovementTypeCriterion(String condition, BookStockMovementType value1, BookStockMovementType value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            movementTypeCriteria.add(new Criterion(condition, value1, value2, "com.example.demo.mybatis.typehandler.BookStockMovementTypeHandler"));
            allCriteria = null;
        }

        public List<Criterion> getSourceTypeCriteria() {
            return sourceTypeCriteria;
        }

        protected void addSourceTypeCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            sourceTypeCriteria.add(new Criterion(condition, value, "com.example.demo.mybatis.typehandler.BookStockMovementSourceTypeHandler"));
            allCriteria = null;
        }

        protected void addSourceTypeCriterion(String condition, BookStockMovementSourceType value1, BookStockMovementSourceType value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            sourceTypeCriteria.add(new Criterion(condition, value1, value2, "com.example.demo.mybatis.typehandler.BookStockMovementSourceTypeHandler"));
            allCriteria = null;
        }

        public boolean isValid() {
            return criteria.size() > 0
                || movementTypeCriteria.size() > 0
                || sourceTypeCriteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            if (allCriteria == null) {
                allCriteria = new ArrayList<>();
                allCriteria.addAll(criteria);
                allCriteria.addAll(movementTypeCriteria);
                allCriteria.addAll(sourceTypeCriteria);
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

        public Criteria andStoreIdIsNull() {
            addCriterion("store_id is null");
            return (Criteria) this;
        }

        public Criteria andStoreIdIsNotNull() {
            addCriterion("store_id is not null");
            return (Criteria) this;
        }

        public Criteria andStoreIdEqualTo(Long value) {
            addCriterion("store_id =", value, "storeId");
            return (Criteria) this;
        }

        public Criteria andStoreIdNotEqualTo(Long value) {
            addCriterion("store_id <>", value, "storeId");
            return (Criteria) this;
        }

        public Criteria andStoreIdGreaterThan(Long value) {
            addCriterion("store_id >", value, "storeId");
            return (Criteria) this;
        }

        public Criteria andStoreIdGreaterThanOrEqualTo(Long value) {
            addCriterion("store_id >=", value, "storeId");
            return (Criteria) this;
        }

        public Criteria andStoreIdLessThan(Long value) {
            addCriterion("store_id <", value, "storeId");
            return (Criteria) this;
        }

        public Criteria andStoreIdLessThanOrEqualTo(Long value) {
            addCriterion("store_id <=", value, "storeId");
            return (Criteria) this;
        }

        public Criteria andStoreIdIn(List<Long> values) {
            addCriterion("store_id in", values, "storeId");
            return (Criteria) this;
        }

        public Criteria andStoreIdNotIn(List<Long> values) {
            addCriterion("store_id not in", values, "storeId");
            return (Criteria) this;
        }

        public Criteria andStoreIdBetween(Long value1, Long value2) {
            addCriterion("store_id between", value1, value2, "storeId");
            return (Criteria) this;
        }

        public Criteria andStoreIdNotBetween(Long value1, Long value2) {
            addCriterion("store_id not between", value1, value2, "storeId");
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

        public Criteria andMovementTypeIsNull() {
            addCriterion("movement_type is null");
            return (Criteria) this;
        }

        public Criteria andMovementTypeIsNotNull() {
            addCriterion("movement_type is not null");
            return (Criteria) this;
        }

        public Criteria andMovementTypeEqualTo(BookStockMovementType value) {
            addMovementTypeCriterion("movement_type =", value, "movementType");
            return (Criteria) this;
        }

        public Criteria andMovementTypeNotEqualTo(BookStockMovementType value) {
            addMovementTypeCriterion("movement_type <>", value, "movementType");
            return (Criteria) this;
        }

        public Criteria andMovementTypeGreaterThan(BookStockMovementType value) {
            addMovementTypeCriterion("movement_type >", value, "movementType");
            return (Criteria) this;
        }

        public Criteria andMovementTypeGreaterThanOrEqualTo(BookStockMovementType value) {
            addMovementTypeCriterion("movement_type >=", value, "movementType");
            return (Criteria) this;
        }

        public Criteria andMovementTypeLessThan(BookStockMovementType value) {
            addMovementTypeCriterion("movement_type <", value, "movementType");
            return (Criteria) this;
        }

        public Criteria andMovementTypeLessThanOrEqualTo(BookStockMovementType value) {
            addMovementTypeCriterion("movement_type <=", value, "movementType");
            return (Criteria) this;
        }

        public Criteria andMovementTypeIn(List<BookStockMovementType> values) {
            addMovementTypeCriterion("movement_type in", values, "movementType");
            return (Criteria) this;
        }

        public Criteria andMovementTypeNotIn(List<BookStockMovementType> values) {
            addMovementTypeCriterion("movement_type not in", values, "movementType");
            return (Criteria) this;
        }

        public Criteria andMovementTypeBetween(BookStockMovementType value1, BookStockMovementType value2) {
            addMovementTypeCriterion("movement_type between", value1, value2, "movementType");
            return (Criteria) this;
        }

        public Criteria andMovementTypeNotBetween(BookStockMovementType value1, BookStockMovementType value2) {
            addMovementTypeCriterion("movement_type not between", value1, value2, "movementType");
            return (Criteria) this;
        }

        public Criteria andQuantityDeltaIsNull() {
            addCriterion("quantity_delta is null");
            return (Criteria) this;
        }

        public Criteria andQuantityDeltaIsNotNull() {
            addCriterion("quantity_delta is not null");
            return (Criteria) this;
        }

        public Criteria andQuantityDeltaEqualTo(Integer value) {
            addCriterion("quantity_delta =", value, "quantityDelta");
            return (Criteria) this;
        }

        public Criteria andQuantityDeltaNotEqualTo(Integer value) {
            addCriterion("quantity_delta <>", value, "quantityDelta");
            return (Criteria) this;
        }

        public Criteria andQuantityDeltaGreaterThan(Integer value) {
            addCriterion("quantity_delta >", value, "quantityDelta");
            return (Criteria) this;
        }

        public Criteria andQuantityDeltaGreaterThanOrEqualTo(Integer value) {
            addCriterion("quantity_delta >=", value, "quantityDelta");
            return (Criteria) this;
        }

        public Criteria andQuantityDeltaLessThan(Integer value) {
            addCriterion("quantity_delta <", value, "quantityDelta");
            return (Criteria) this;
        }

        public Criteria andQuantityDeltaLessThanOrEqualTo(Integer value) {
            addCriterion("quantity_delta <=", value, "quantityDelta");
            return (Criteria) this;
        }

        public Criteria andQuantityDeltaIn(List<Integer> values) {
            addCriterion("quantity_delta in", values, "quantityDelta");
            return (Criteria) this;
        }

        public Criteria andQuantityDeltaNotIn(List<Integer> values) {
            addCriterion("quantity_delta not in", values, "quantityDelta");
            return (Criteria) this;
        }

        public Criteria andQuantityDeltaBetween(Integer value1, Integer value2) {
            addCriterion("quantity_delta between", value1, value2, "quantityDelta");
            return (Criteria) this;
        }

        public Criteria andQuantityDeltaNotBetween(Integer value1, Integer value2) {
            addCriterion("quantity_delta not between", value1, value2, "quantityDelta");
            return (Criteria) this;
        }

        public Criteria andSourceTypeIsNull() {
            addCriterion("source_type is null");
            return (Criteria) this;
        }

        public Criteria andSourceTypeIsNotNull() {
            addCriterion("source_type is not null");
            return (Criteria) this;
        }

        public Criteria andSourceTypeEqualTo(BookStockMovementSourceType value) {
            addSourceTypeCriterion("source_type =", value, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeNotEqualTo(BookStockMovementSourceType value) {
            addSourceTypeCriterion("source_type <>", value, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeGreaterThan(BookStockMovementSourceType value) {
            addSourceTypeCriterion("source_type >", value, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeGreaterThanOrEqualTo(BookStockMovementSourceType value) {
            addSourceTypeCriterion("source_type >=", value, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeLessThan(BookStockMovementSourceType value) {
            addSourceTypeCriterion("source_type <", value, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeLessThanOrEqualTo(BookStockMovementSourceType value) {
            addSourceTypeCriterion("source_type <=", value, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeIn(List<BookStockMovementSourceType> values) {
            addSourceTypeCriterion("source_type in", values, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeNotIn(List<BookStockMovementSourceType> values) {
            addSourceTypeCriterion("source_type not in", values, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeBetween(BookStockMovementSourceType value1, BookStockMovementSourceType value2) {
            addSourceTypeCriterion("source_type between", value1, value2, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeNotBetween(BookStockMovementSourceType value1, BookStockMovementSourceType value2) {
            addSourceTypeCriterion("source_type not between", value1, value2, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceIdIsNull() {
            addCriterion("source_id is null");
            return (Criteria) this;
        }

        public Criteria andSourceIdIsNotNull() {
            addCriterion("source_id is not null");
            return (Criteria) this;
        }

        public Criteria andSourceIdEqualTo(Long value) {
            addCriterion("source_id =", value, "sourceId");
            return (Criteria) this;
        }

        public Criteria andSourceIdNotEqualTo(Long value) {
            addCriterion("source_id <>", value, "sourceId");
            return (Criteria) this;
        }

        public Criteria andSourceIdGreaterThan(Long value) {
            addCriterion("source_id >", value, "sourceId");
            return (Criteria) this;
        }

        public Criteria andSourceIdGreaterThanOrEqualTo(Long value) {
            addCriterion("source_id >=", value, "sourceId");
            return (Criteria) this;
        }

        public Criteria andSourceIdLessThan(Long value) {
            addCriterion("source_id <", value, "sourceId");
            return (Criteria) this;
        }

        public Criteria andSourceIdLessThanOrEqualTo(Long value) {
            addCriterion("source_id <=", value, "sourceId");
            return (Criteria) this;
        }

        public Criteria andSourceIdIn(List<Long> values) {
            addCriterion("source_id in", values, "sourceId");
            return (Criteria) this;
        }

        public Criteria andSourceIdNotIn(List<Long> values) {
            addCriterion("source_id not in", values, "sourceId");
            return (Criteria) this;
        }

        public Criteria andSourceIdBetween(Long value1, Long value2) {
            addCriterion("source_id between", value1, value2, "sourceId");
            return (Criteria) this;
        }

        public Criteria andSourceIdNotBetween(Long value1, Long value2) {
            addCriterion("source_id not between", value1, value2, "sourceId");
            return (Criteria) this;
        }

        public Criteria andSourceDetailIdIsNull() {
            addCriterion("source_detail_id is null");
            return (Criteria) this;
        }

        public Criteria andSourceDetailIdIsNotNull() {
            addCriterion("source_detail_id is not null");
            return (Criteria) this;
        }

        public Criteria andSourceDetailIdEqualTo(Long value) {
            addCriterion("source_detail_id =", value, "sourceDetailId");
            return (Criteria) this;
        }

        public Criteria andSourceDetailIdNotEqualTo(Long value) {
            addCriterion("source_detail_id <>", value, "sourceDetailId");
            return (Criteria) this;
        }

        public Criteria andSourceDetailIdGreaterThan(Long value) {
            addCriterion("source_detail_id >", value, "sourceDetailId");
            return (Criteria) this;
        }

        public Criteria andSourceDetailIdGreaterThanOrEqualTo(Long value) {
            addCriterion("source_detail_id >=", value, "sourceDetailId");
            return (Criteria) this;
        }

        public Criteria andSourceDetailIdLessThan(Long value) {
            addCriterion("source_detail_id <", value, "sourceDetailId");
            return (Criteria) this;
        }

        public Criteria andSourceDetailIdLessThanOrEqualTo(Long value) {
            addCriterion("source_detail_id <=", value, "sourceDetailId");
            return (Criteria) this;
        }

        public Criteria andSourceDetailIdIn(List<Long> values) {
            addCriterion("source_detail_id in", values, "sourceDetailId");
            return (Criteria) this;
        }

        public Criteria andSourceDetailIdNotIn(List<Long> values) {
            addCriterion("source_detail_id not in", values, "sourceDetailId");
            return (Criteria) this;
        }

        public Criteria andSourceDetailIdBetween(Long value1, Long value2) {
            addCriterion("source_detail_id between", value1, value2, "sourceDetailId");
            return (Criteria) this;
        }

        public Criteria andSourceDetailIdNotBetween(Long value1, Long value2) {
            addCriterion("source_detail_id not between", value1, value2, "sourceDetailId");
            return (Criteria) this;
        }

        public Criteria andMovementDateIsNull() {
            addCriterion("movement_date is null");
            return (Criteria) this;
        }

        public Criteria andMovementDateIsNotNull() {
            addCriterion("movement_date is not null");
            return (Criteria) this;
        }

        public Criteria andMovementDateEqualTo(LocalDate value) {
            addCriterion("movement_date =", value, "movementDate");
            return (Criteria) this;
        }

        public Criteria andMovementDateNotEqualTo(LocalDate value) {
            addCriterion("movement_date <>", value, "movementDate");
            return (Criteria) this;
        }

        public Criteria andMovementDateGreaterThan(LocalDate value) {
            addCriterion("movement_date >", value, "movementDate");
            return (Criteria) this;
        }

        public Criteria andMovementDateGreaterThanOrEqualTo(LocalDate value) {
            addCriterion("movement_date >=", value, "movementDate");
            return (Criteria) this;
        }

        public Criteria andMovementDateLessThan(LocalDate value) {
            addCriterion("movement_date <", value, "movementDate");
            return (Criteria) this;
        }

        public Criteria andMovementDateLessThanOrEqualTo(LocalDate value) {
            addCriterion("movement_date <=", value, "movementDate");
            return (Criteria) this;
        }

        public Criteria andMovementDateIn(List<LocalDate> values) {
            addCriterion("movement_date in", values, "movementDate");
            return (Criteria) this;
        }

        public Criteria andMovementDateNotIn(List<LocalDate> values) {
            addCriterion("movement_date not in", values, "movementDate");
            return (Criteria) this;
        }

        public Criteria andMovementDateBetween(LocalDate value1, LocalDate value2) {
            addCriterion("movement_date between", value1, value2, "movementDate");
            return (Criteria) this;
        }

        public Criteria andMovementDateNotBetween(LocalDate value1, LocalDate value2) {
            addCriterion("movement_date not between", value1, value2, "movementDate");
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

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: book_stock_movement")
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