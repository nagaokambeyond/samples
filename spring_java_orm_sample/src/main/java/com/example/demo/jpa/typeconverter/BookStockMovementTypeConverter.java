package com.example.demo.jpa.typeconverter;

import com.example.demo.data.domain.BookStockMovementType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class BookStockMovementTypeConverter implements AttributeConverter<BookStockMovementType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(BookStockMovementType attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public BookStockMovementType convertToEntityAttribute(Integer dbData) {
        return dbData == null ? null : BookStockMovementType.of(dbData);
    }
}
