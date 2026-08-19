package com.example.demo.config;

import com.example.demo.api.request.BookCreateRequest;
import com.example.demo.api.request.BookSalesUnitPriceCreateRequest;
import com.example.demo.api.request.BookUpdateRequest;
import com.example.demo.api.request.LoginRequest;
import com.example.demo.api.request.PurchaseInvoiceCreateRequest;
import com.example.demo.api.request.PurchaseInvoiceDetailCreateRequest;
import com.example.demo.api.response.BookPageResponse;
import com.example.demo.api.response.BookResponse;
import com.example.demo.api.response.BookStockResponse;
import com.example.demo.api.response.LoginResponse;
import com.example.demo.api.response.OpenBdBookResponse;
import com.example.demo.api.response.PurchaseInvoiceDetailResponse;
import com.example.demo.api.response.PurchaseInvoiceResponse;
import com.example.demo.doma.entity.BookStockWithStoreName;
import com.example.demo.doma.entity.BookWithPublisherName;
import com.example.demo.doma.generator.entity.Book;
import com.example.demo.doma.generator.entity.BookGenre;
import com.example.demo.doma.generator.entity.BookSalesUnitPriceHistory;
import com.example.demo.doma.generator.entity.BookStock;
import com.example.demo.doma.generator.entity.BookStockMovement;
import com.example.demo.doma.generator.entity.Publisher;
import com.example.demo.doma.generator.entity.PurchaseInvoice;
import com.example.demo.doma.generator.entity.PurchaseInvoiceDetail;
import com.example.demo.doma.generator.entity.Store;
import com.example.demo.doma.generator.entity.Supplier;
import com.example.demo.openbd.generated.invoker.ApiException;
import com.example.demo.openbd.generated.model.AbstractOpenApiSchema;
import com.example.demo.openbd.generated.model.BookDto;
import com.example.demo.openbd.generated.model.ErrorResponseDto;
import com.example.demo.openbd.generated.model.HanmotoDto;
import com.example.demo.openbd.generated.model.JsonSchemaDraft04AdditionalPropertiesDto;
import com.example.demo.openbd.generated.model.JsonSchemaDraft04Dto;
import com.example.demo.openbd.generated.model.OnixDto;
import com.example.demo.openbd.generated.model.OnixProductIdentifierDto;
import com.example.demo.openbd.generated.model.SummaryDto;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

public class NativeRuntimeHints implements RuntimeHintsRegistrar {
    private static final MemberCategory[] REFLECTION_MEMBER_CATEGORIES = {
        MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
        MemberCategory.INVOKE_DECLARED_METHODS,
        MemberCategory.ACCESS_DECLARED_FIELDS
    };

    private static final Class<?>[] REFLECTION_TYPES = {
        BookCreateRequest.class,
        BookSalesUnitPriceCreateRequest.class,
        BookUpdateRequest.class,
        LoginRequest.class,
        PurchaseInvoiceCreateRequest.class,
        PurchaseInvoiceDetailCreateRequest.class,
        BookPageResponse.class,
        BookResponse.class,
        BookStockResponse.class,
        LoginResponse.class,
        OpenBdBookResponse.class,
        OpenBdBookResponse.OpenBdOnixResponse.class,
        OpenBdBookResponse.OpenBdOnixProductIdentifierResponse.class,
        OpenBdBookResponse.OpenBdHanmotoResponse.class,
        OpenBdBookResponse.OpenBdSummaryResponse.class,
        PurchaseInvoiceDetailResponse.class,
        PurchaseInvoiceResponse.class,
        BookStockWithStoreName.class,
        BookWithPublisherName.class,
        Book.class,
        BookGenre.class,
        BookSalesUnitPriceHistory.class,
        BookStock.class,
        BookStockMovement.class,
        Publisher.class,
        PurchaseInvoice.class,
        PurchaseInvoiceDetail.class,
        Store.class,
        Supplier.class,
        ApiException.class,
        AbstractOpenApiSchema.class,
        BookDto.class,
        ErrorResponseDto.class,
        HanmotoDto.class,
        JsonSchemaDraft04AdditionalPropertiesDto.class,
        JsonSchemaDraft04Dto.class,
        OnixDto.class,
        OnixProductIdentifierDto.class,
        SummaryDto.class
    };

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        for (final var type : REFLECTION_TYPES) {
            hints.reflection().registerType(type, REFLECTION_MEMBER_CATEGORIES);
        }
        hints.resources().registerPattern("META-INF/com/example/demo/doma/**/*.sql");
        hints.resources().registerPattern("generator-schema.sql");
    }
}
