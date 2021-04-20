package com.vmware.cet.codegen.constant;

import lombok.Getter;

public enum DatabaseTypeConstant {

    VARCHAR("varchar(500)"),
    INT("int"),
    BIGINT("bigint"),
    DATETIME("datetime"),
    BOOLEAN("boolean");

    @Getter
    private String value;

    DatabaseTypeConstant(String value) {
        this.value = value;
    }
}
