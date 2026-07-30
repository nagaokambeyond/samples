package com.example.demo.mybatis.entity

import java.time.LocalDate
import java.time.LocalDateTime

class BookWithPublisherName {
    var id: Long? = null
    var title: String? = null
    var author: String? = null
    var releaseDate: LocalDate? = null
    var publisherId: Long? = null
    var publisherName: String? = null
    var genreId: Long? = null
    var genreName: String? = null
    var isbn: String? = null
    var salesUnitPrice: Int? = null
    var updateAt: LocalDateTime? = null
    var version: Long? = null
    var bookStockList: MutableList<BookStockWithStoreName?>? = ArrayList<BookStockWithStoreName?>()
}
