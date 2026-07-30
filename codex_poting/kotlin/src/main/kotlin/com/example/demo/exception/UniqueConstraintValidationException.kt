package com.example.demo.exception

class UniqueConstraintValidationException(tableName: String?, columnName: String?, value: String?) :
    RuntimeException("一意制約に違反しています: " + tableName + "(" + columnName + "=" + value + ")")
