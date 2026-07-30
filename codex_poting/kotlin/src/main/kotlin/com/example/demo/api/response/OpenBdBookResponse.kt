package com.example.demo.api.response

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import java.net.URI

data class OpenBdBookResponse(
    @field:Schema(description = "JPRO-onix準拠項目")
    var onix: OpenBdOnixResponse? = null,

    @field:Schema(description = "版元ドットコム独自書誌項目")
    var hanmoto: OpenBdHanmotoResponse? = null,

    @field:Schema(description = "書誌の概要")
    var summary: OpenBdSummaryResponse? = null,
) {
    data class OpenBdOnixResponse(
        @field:JsonProperty("RecordReference")
        @field:Schema(description = "ISBNコード")
        var recordReference: String? = null,

        @field:JsonProperty("NotificationType")
        @field:Schema(description = "通知種別・削除フラグ等")
        var notificationType: String? = null,

        @field:JsonProperty("ProductIdentifier")
        @field:Schema(description = "商品識別子")
        var productIdentifier: OpenBdOnixProductIdentifierResponse? = null,

        @field:JsonProperty("DescriptiveDetail")
        @field:Schema(description = "商品情報")
        var descriptiveDetail: MutableMap<String?, Any?>? = null,

        @field:JsonProperty("CollateralDetail")
        @field:Schema(description = "販促情報")
        var collateralDetail: MutableMap<String?, Any?>? = null,

        @field:JsonProperty("PublishingDetail")
        @field:Schema(description = "出版情報")
        var publishingDetail: MutableMap<String?, Any?>? = null,

        @field:JsonProperty("ProductSupply")
        @field:Schema(description = "供給情報")
        var productSupply: MutableMap<String?, Any?>? = null,
    )

    data class OpenBdOnixProductIdentifierResponse(
        @field:JsonProperty("ProductIDType")
        @field:Schema(description = "IDの種類")
        var productIdType: String? = null,

        @field:JsonProperty("IDValue")
        @field:Schema(description = "ISBN")
        var idValue: String? = null,
    )

    data class OpenBdHanmotoResponse(
        @field:Schema(description = "公開日")
        var datekoukai: String? = null,

        @field:Schema(description = "情報更新日時")
        var datemodified: String? = null,

        @field:Schema(description = "情報作成日時")
        var datecreated: String? = null,

        @field:Schema(description = "出版年月日")
        var dateshuppan: String? = null,

        @field:Schema(description = "レビュー情報")
        var reviews: MutableList<MutableMap<String?, Any?>?>? = null,

        @field:Schema(description = "版元ドットコム独自項目")
        var hanmotoinfo: MutableMap<String?, Any?>? = null,
    )

    data class OpenBdSummaryResponse(
        @field:Schema(description = "ISBN")
        var isbn: String? = null,

        @field:Schema(description = "書名")
        var title: String? = null,

        @field:Schema(description = "巻号")
        var volume: String? = null,

        @field:Schema(description = "シリーズ名")
        var series: String? = null,

        @field:Schema(description = "出版者")
        var publisher: String? = null,

        @field:Schema(description = "出版年月日または出版年月")
        var pubdate: String? = null,

        @field:Schema(description = "書影URL")
        var cover: URI? = null,

        @field:Schema(description = "著者名")
        var author: String? = null,
    )
}
