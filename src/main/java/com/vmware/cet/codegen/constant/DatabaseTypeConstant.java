package com.vmware.cet.codegen.constant;

import lombok.Getter;

public enum DatabaseTypeConstant {

    VARCHAR("varchar"),
    INT("int"),
    DATETIME("datetime"),
    BOOLEAN("boolean");

    @Getter
    private String value;

    DatabaseTypeConstant(String value) {
        this.value = value;
    }
}
