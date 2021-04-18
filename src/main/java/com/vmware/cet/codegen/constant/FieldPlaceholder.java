package com.vmware.cet.codegen.constant;

import lombok.Getter;

public enum FieldPlaceholder {

    TABLE_NAME(":tableName"),
    FIELD_NAME(":fieldName"),
    FIELD_TYPE(":fieldType"),
    PRIMARY_KEY(":primaryKey"),
    AUTO_INCREMENT(":autoIncrement"),
    COLUMN_NAME(":columnName"),
    FIELDS(":fields");

    @Getter
    private String placeHolder;

    FieldPlaceholder(String placeHolder) {
        this.placeHolder = placeHolder;
    }
}
