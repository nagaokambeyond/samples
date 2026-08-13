package com.example.demo.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Data
public class OpenBdBookResponse {
    @Schema(description = "JPRO-onix準拠項目")
    OpenBdOnixResponse onix;

    @Schema(description = "版元ドットコム独自書誌項目")
    OpenBdHanmotoResponse hanmoto;

    @Schema(description = "書誌の概要")
    OpenBdSummaryResponse summary;

    @Data
    public static class OpenBdOnixResponse {
        @JsonProperty("RecordReference")
        @Schema(description = "ISBNコード")
        String recordReference;

        @JsonProperty("NotificationType")
        @Schema(description = "通知種別・削除フラグ等")
        String notificationType;

        @JsonProperty("ProductIdentifier")
        @Schema(description = "商品識別子")
        OpenBdOnixProductIdentifierResponse productIdentifier;

        @JsonProperty("DescriptiveDetail")
        @Schema(description = "商品情報")
        Map<String, Object> descriptiveDetail;

        @JsonProperty("CollateralDetail")
        @Schema(description = "販促情報")
        Map<String, Object> collateralDetail;

        @JsonProperty("PublishingDetail")
        @Schema(description = "出版情報")
        Map<String, Object> publishingDetail;

        @JsonProperty("ProductSupply")
        @Schema(description = "供給情報")
        Map<String, Object> productSupply;
    }

    @Data
    public static class OpenBdOnixProductIdentifierResponse {
        @JsonProperty("ProductIDType")
        @Schema(description = "IDの種類")
        String productIdType;

        @JsonProperty("IDValue")
        @Schema(description = "ISBN")
        String idValue;
    }

    @Data
    public static class OpenBdHanmotoResponse {
        @Schema(description = "公開日")
        String datekoukai;

        @Schema(description = "情報更新日時")
        String datemodified;

        @Schema(description = "情報作成日時")
        String datecreated;

        @Schema(description = "出版年月日")
        String dateshuppan;

        @Schema(description = "レビュー情報")
        List<Map<String, Object>> reviews;

        @Schema(description = "版元ドットコム独自項目")
        Map<String, Object> hanmotoinfo;
    }

    @Data
    public static class OpenBdSummaryResponse {
        @Schema(description = "ISBN")
        String isbn;

        @Schema(description = "書名")
        String title;

        @Schema(description = "巻号")
        String volume;

        @Schema(description = "シリーズ名")
        String series;

        @Schema(description = "出版者")
        String publisher;

        @Schema(description = "出版年月日または出版年月")
        String pubdate;

        @Schema(description = "書影URL")
        URI cover;

        @Schema(description = "著者名")
        String author;
    }
}
