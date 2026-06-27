package com.example.demo.mybatis.converter;

import com.example.demo.api.request.PurchaseInvoiceCreateRequest;
import com.example.demo.api.response.PurchaseInvoiceDetailResponse;
import com.example.demo.api.response.PurchaseInvoiceResponse;
import com.example.demo.data.domain.PurchaseInvoiceType;
import com.example.demo.mybatis.generator.entity.BookStockEntity;
import com.example.demo.mybatis.generator.entity.PurchaseOrderDetailEntity;
import com.example.demo.mybatis.generator.entity.PurchaseOrderEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Profile("mybatis")
@RequiredArgsConstructor
public class PurchaseOperationConverterMybatis {
    private final ModelMapper modelMapper;

    public List<PurchaseOrderDetailEntity> toPurchaseInvoiceDetails(
        PurchaseInvoiceCreateRequest request,
        LocalDateTime now
    ) {
        return request.getDetails().stream().map(purchaseInvoiceDetail -> {
            final var row = modelMapper.map(purchaseInvoiceDetail, PurchaseOrderDetailEntity.class);
            final var amount = (long) row.getPurchaseInvoiceDetailUnitPrice() * row.getPurchaseInvoiceDetailQuantity();
            row.setPurchaseInvoiceDetailAmount(amount);
            row.setCreateAt(now);
            row.setUpdateAt(now);
            row.setVersion(1L);

            return row;
        }).toList();
    }

    public PurchaseOrderEntity toPurchaseInvoice(PurchaseInvoiceCreateRequest request, long amount, LocalDateTime now) {
        final var result = modelMapper.map(request, PurchaseOrderEntity.class);
        result.setPurchaseInvoiceType(PurchaseInvoiceType.PURCHASE);
        result.setPurchaseInvoiceAmount(amount);
        result.setCreateAt(now);
        result.setUpdateAt(now);
        result.setVersion(1L);
        return result;
    }

    public BookStockEntity toBookStock(Long storeId, PurchaseOrderDetailEntity purchaseInvoiceDetail, LocalDateTime now) {
        final var result = new BookStockEntity();
        result.setBookStockStoreId(storeId);
        result.setBookStockBookId(purchaseInvoiceDetail.getPurchaseInvoiceDetailBookId());
        result.setBookStockQuantity(purchaseInvoiceDetail.getPurchaseInvoiceDetailQuantity());
        result.setCreateAt(now);
        result.setUpdateAt(now);
        result.setVersion(1L);

        return result;
    }

    public PurchaseInvoiceResponse toResponse(PurchaseOrderEntity purchaseInvoice, List<PurchaseOrderDetailEntity> details) {
        final var list = details.stream()
            .map(row -> modelMapper.map(row, PurchaseInvoiceDetailResponse.class))
            .toList();

        final var response = modelMapper.map(purchaseInvoice, PurchaseInvoiceResponse.class);
        response.setDetail(list);
        return response;
    }
}
