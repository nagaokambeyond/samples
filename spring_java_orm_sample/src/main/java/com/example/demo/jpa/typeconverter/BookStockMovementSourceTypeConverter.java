package com.example.demo.jpa.typeconverter;

import com.example.demo.data.domain.BookStockMovementSourceType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class BookStockMovementSourceTypeConverter implements AttributeConverter<BookStockMovementSourceType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(BookStockMovementSourceType attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public BookStockMovementSourceType convertToEntityAttribute(Integer dbData) {
        return dbData == null ? null : BookStockMovementSourceType.of(dbData);
    }
}
