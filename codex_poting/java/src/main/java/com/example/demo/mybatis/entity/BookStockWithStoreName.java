package com.example.demo.mybatis.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookStockWithStoreName {
    private Long id;
    private Long bookStockStoreId;
    private String storeName;
    private Integer bookStockQuantity;
}
