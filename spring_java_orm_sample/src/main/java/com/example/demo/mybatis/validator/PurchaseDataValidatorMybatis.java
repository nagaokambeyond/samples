package com.example.demo.mybatis.validator;

import com.example.demo.api.request.PurchaseInvoiceCreateRequest;
import com.example.demo.api.request.PurchaseInvoiceDetailCreateRequest;
import com.example.demo.exception.ForeignKeyReferenceNotFoundException;
import com.example.demo.mybatis.generator.entity.BookEntityExample;
import com.example.demo.mybatis.generator.entity.StoreEntity;
import com.example.demo.mybatis.generator.entity.SupplierEntity;
import com.example.demo.mybatis.generator.mapper.BookMapper;
import com.example.demo.mybatis.generator.mapper.StoreMapper;
import com.example.demo.mybatis.generator.mapper.SupplierMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@Profile("mybatis")
@RequiredArgsConstructor
public class PurchaseDataValidatorMybatis {
    private final BookMapper bookMapper;
    private final SupplierMapper supplierMapper;
    private final StoreMapper storeMapper;

    public Map<String, Long> foreignKeyValidate(PurchaseInvoiceCreateRequest request) {
        final var supplier = supplierMapper.selectByPrimaryKey(request.getSupplierId());
        if (Objects.isNull(supplier)) {
            throw new ForeignKeyReferenceNotFoundException(SupplierEntity.class, request.getSupplierId());
        }

        final var store = storeMapper.selectByPrimaryKey(request.getReceivingStoreId());
        if (Objects.isNull(store)) {
            throw new ForeignKeyReferenceNotFoundException(StoreEntity.class, request.getReceivingStoreId());
        }

        return validateBooks(request.getDetails());
    }

    private Map<String, Long> validateBooks(List<PurchaseInvoiceDetailCreateRequest> details) {
        final var bookIdsByIsbn = new LinkedHashMap<String, Long>();
        details.forEach(detail -> {
            final var isbn = detail.getPurchaseInvoiceDetailIsbn();
            final var example = new BookEntityExample();
            example.createCriteria().andIsbnEqualTo(isbn);
            final var books = bookMapper.selectByExample(example);
            if (books.isEmpty()) {
                throw new ForeignKeyReferenceNotFoundException("book", "isbn", isbn);
            }
            bookIdsByIsbn.put(isbn, books.getFirst().getId());
        });
        return bookIdsByIsbn;
    }
}
