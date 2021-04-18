package com.vmware.cet.codegen.constant;

import lombok.Getter;

public enum SchemaSyntaxConstant {

    CREATE_TABLE("create table :tableName (:fields);"),
    CREATE_FIELD(":fieldName :fieldType :primaryKey :autoIncrement");

    @Getter
    private String statement;

    SchemaSyntaxConstant(String statement) {
        this.statement = statement;
    }
}
