package com.example.demo.jpa.typeconverter;

import com.example.demo.data.domain.PurchaseInvoiceType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PurchaseInvoiceTypeConverter implements AttributeConverter<PurchaseInvoiceType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(PurchaseInvoiceType attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public PurchaseInvoiceType convertToEntityAttribute(Integer dbData) {
        return dbData == null ? null : PurchaseInvoiceType.of(dbData);
    }
}
