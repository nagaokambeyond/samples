package com.example.demo.jpa.typeconverter;

import com.example.demo.data.domain.PurchaseOrderType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PurchaseOrderTypeConverter implements AttributeConverter<PurchaseOrderType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(PurchaseOrderType attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public PurchaseOrderType convertToEntityAttribute(Integer dbData) {
        return dbData == null ? null : PurchaseOrderType.of(dbData);
    }
}
