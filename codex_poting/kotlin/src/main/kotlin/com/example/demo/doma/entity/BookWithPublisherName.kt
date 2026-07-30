package com.example.demo.doma.entity

import org.seasar.doma.Association
import org.seasar.doma.Column
import org.seasar.doma.Entity
import org.seasar.doma.Id
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
class BookWithPublisherName {
    @Id
    @Column(name = "id")
    var id: Long? = null

    @Column(name = "title")
    var title: String? = null

    @Column(name = "author")
    var author: String? = null

    @Column(name = "release_date")
    var releaseDate: LocalDate? = null

    @Column(name = "publisher_id")
    var publisherId: Long? = null

    @Column(name = "publisher_name")
    var publisherName: String? = null

    @Column(name = "genre_id")
    var genreId: Long? = null

    @Column(name = "genre_name")
    var genreName: String? = null

    @Column(name = "isbn")
    var isbn: String? = null

    @Column(name = "sales_unit_price")
    var salesUnitPrice: Int? = null

    @Column(name = "update_at")
    var updateAt: LocalDateTime? = null

    @Column(name = "version")
    var version: Long? = null

    @Association
    var bookStockList: MutableList<BookStockWithStoreName?>? = ArrayList<BookStockWithStoreName?>()
}
