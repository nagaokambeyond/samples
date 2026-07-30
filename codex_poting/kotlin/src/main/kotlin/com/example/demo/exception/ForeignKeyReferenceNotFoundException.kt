package com.example.demo.exception

import org.seasar.doma.Table

class ForeignKeyReferenceNotFoundException : RuntimeException {
    val tableName: String?
    val id: Long?
    val columnName: String?
    val value: String?

    constructor(tableName: String?, id: Long?) : super("参照先データが存在しません: " + tableName + "(id=" + id + ")") {
        this.tableName = tableName
        this.id = id
        this.columnName = "id"
        this.value = id.toString()
    }

    constructor(
        tableName: String?,
        columnName: String?,
        value: String?
    ) : super("参照先データが存在しません: " + tableName + "(" + columnName + "=" + value + ")") {
        this.tableName = tableName
        this.id = null
        this.columnName = columnName
        this.value = value
    }

    constructor(entityClass: Class<*>, id: Long?) : this(resolveTableName(entityClass), id)

    companion object {
        private fun resolveTableName(entityClass: Class<*>): String {
            val domaTable = entityClass.getAnnotation<Table?>(Table::class.java)
            if (domaTable != null && !domaTable.name.isBlank()) {
                return domaTable.name
            }
            // mybatis用
            val simpleName = entityClass.getSimpleName().replaceFirst("Entity$".toRegex(), "")
            return simpleName.replace("([a-z0-9])([A-Z])".toRegex(), "$1_$2").lowercase()
        }
    }
}
