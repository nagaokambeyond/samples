package com.example.demo.doma.entity;

import lombok.Getter;
import lombok.Setter;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;

@Entity
@Getter
@Setter
public class BookStockWithStoreName {
    @Id
    @Column(name = "id")
    Long id;

    @Column(name = "book_stock_store_id")
    Long bookStockStoreId;

    @Column(name = "store_name")
    String storeName;

    @Column(name = "book_stock_quantity")
    Integer bookStockQuantity;
}
