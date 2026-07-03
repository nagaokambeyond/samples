package com.example.demo.doma.converter;

import com.example.demo.api.request.PurchaseInvoiceCreateRequest;
import com.example.demo.api.response.PurchaseInvoiceDetailResponse;
import com.example.demo.api.response.PurchaseInvoiceResponse;
import com.example.demo.data.domain.PurchaseInvoiceType;
import com.example.demo.doma.generator.entity.BookStock;
import com.example.demo.doma.generator.entity.PurchaseInvoice;
import com.example.demo.doma.generator.entity.PurchaseInvoiceDetail;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@Profile("doma")
@RequiredArgsConstructor
public class PurchaseOperationConverterDoma {
    private final ModelMapper modelMapper;

    public List<PurchaseInvoiceDetail> toPurchaseInvoiceDetails(
        PurchaseInvoiceCreateRequest request,
        Map<String, Long> bookIdsByIsbn,
        LocalDateTime now
    ) {
        return request.getDetails().stream().map(purchaseInvoiceDetail -> {
            final var row = modelMapper.map(purchaseInvoiceDetail, PurchaseInvoiceDetail.class);
            row.setPurchaseInvoiceDetailBookId(bookIdsByIsbn.get(purchaseInvoiceDetail.getPurchaseInvoiceDetailIsbn()));
            final var amount = ((long) row.getPurchaseInvoiceDetailUnitPrice() * (long) row.getPurchaseInvoiceDetailQuantity());
            row.setPurchaseInvoiceDetailAmount(amount);
            row.setCreateAt(now);
            row.setUpdateAt(now);

            return row;
        }).toList();
    }

    public PurchaseInvoice toPurchaseInvoice(PurchaseInvoiceCreateRequest request, long amount, LocalDateTime now) {
        final var result = modelMapper.map(request, PurchaseInvoice.class);
        result.setPurchaseInvoiceType(PurchaseInvoiceType.PURCHASE);
        result.setPurchaseInvoiceAmount(amount);
        result.setCreateAt(now);
        result.setUpdateAt(now);
        return result;
    }

    public BookStock toBookStock(Long storeId, PurchaseInvoiceDetail purchaseInvoiceDetail, LocalDateTime now) {
        final var result = new BookStock();
        result.setBookStockStoreId(storeId);
        result.setBookStockBookId(purchaseInvoiceDetail.getPurchaseInvoiceDetailBookId());
        result.setBookStockQuantity(purchaseInvoiceDetail.getPurchaseInvoiceDetailQuantity());
        result.setCreateAt(now);
        result.setUpdateAt(now);

        return result;
    }

    public PurchaseInvoiceResponse toRespose(PurchaseInvoice purchaseInvoice, List<PurchaseInvoiceDetail> details){
        final var list = details.stream()
            .map(row -> modelMapper.map(row, PurchaseInvoiceDetailResponse.class))
            .toList();

        final var response = modelMapper.map(purchaseInvoice, PurchaseInvoiceResponse.class);
        response.setDetail(list);
        return response;
    }
}
