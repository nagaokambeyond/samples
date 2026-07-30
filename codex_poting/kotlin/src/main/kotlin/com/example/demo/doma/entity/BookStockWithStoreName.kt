package com.example.demo.doma.entity

import org.seasar.doma.Column
import org.seasar.doma.Entity
import org.seasar.doma.Id

@Entity
class BookStockWithStoreName {
    @Id
    @Column(name = "id")
    var id: Long? = null

    @Column(name = "book_stock_store_id")
    var bookStockStoreId: Long? = null

    @Column(name = "store_name")
    var storeName: String? = null

    @Column(name = "book_stock_quantity")
    var bookStockQuantity: Int? = null
}
