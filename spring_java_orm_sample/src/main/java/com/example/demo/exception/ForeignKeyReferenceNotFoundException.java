package com.example.demo.exception;

import lombok.Getter;

import java.util.Locale;

@Getter
public class ForeignKeyReferenceNotFoundException extends RuntimeException {
    private final String tableName;
    private final Long id;
    private final String columnName;
    private final String value;

    public ForeignKeyReferenceNotFoundException(String tableName, Long id) {
        super("参照先データが存在しません: " + tableName + "(id=" + id + ")");
        this.tableName = tableName;
        this.id = id;
        this.columnName = "id";
        this.value = String.valueOf(id);
    }

    public ForeignKeyReferenceNotFoundException(String tableName, String columnName, String value) {
        super("参照先データが存在しません: " + tableName + "(" + columnName + "=" + value + ")");
        this.tableName = tableName;
        this.id = null;
        this.columnName = columnName;
        this.value = value;
    }

    public ForeignKeyReferenceNotFoundException(Class<?> entityClass, Long id) {
        this(resolveTableName(entityClass), id);
    }

    private static String resolveTableName(Class<?> entityClass) {
        final var jpaTable = entityClass.getAnnotation(jakarta.persistence.Table.class);
        if (jpaTable != null && !jpaTable.name().isBlank()) {
            return jpaTable.name();
        }

        final var domaTable = entityClass.getAnnotation(org.seasar.doma.Table.class);
        if (domaTable != null && !domaTable.name().isBlank()) {
            return domaTable.name();
        }

        // mybatis用
        final var simpleName = entityClass.getSimpleName().replaceFirst("Entity$", "");
        return simpleName
            .replaceAll("([a-z0-9])([A-Z])", "$1_$2")
            .toLowerCase(Locale.ROOT);
    }
}
