package com.example.demo.mybatis.validator;

import com.example.demo.api.request.PurchaseInvoiceCreateRequest;
import com.example.demo.api.request.PurchaseInvoiceDetailCreateRequest;
import com.example.demo.exception.ForeignKeyReferenceNotFoundException;
import com.example.demo.mybatis.generator.entity.BookEntity;
import com.example.demo.mybatis.generator.entity.StoreEntity;
import com.example.demo.mybatis.generator.entity.SupplierEntity;
import com.example.demo.mybatis.generator.mapper.BookMapper;
import com.example.demo.mybatis.generator.mapper.StoreMapper;
import com.example.demo.mybatis.generator.mapper.SupplierMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class PurchaseDataValidatorMybatis {
    private final BookMapper bookMapper;
    private final SupplierMapper supplierMapper;
    private final StoreMapper storeMapper;

    public void foreignKeyValidate(PurchaseInvoiceCreateRequest request) {
        final var supplier = supplierMapper.selectByPrimaryKey(request.getSupplierId());
        if (Objects.isNull(supplier)) {
            throw new ForeignKeyReferenceNotFoundException(SupplierEntity.class, request.getSupplierId());
        }

        final var store = storeMapper.selectByPrimaryKey(request.getReceivingStoreId());
        if (Objects.isNull(store)) {
            throw new ForeignKeyReferenceNotFoundException(StoreEntity.class, request.getReceivingStoreId());
        }

        validateBooks(request.getDetails());
    }

    private void validateBooks(List<PurchaseInvoiceDetailCreateRequest> details) {
        details.forEach(detail -> {
            final var bookId = detail.getPurchaseInvoiceDetailBookId();
            if (Objects.isNull(bookMapper.selectByPrimaryKey(bookId))) {
                throw new ForeignKeyReferenceNotFoundException(BookEntity.class, bookId);
            }
        });
    }
}
