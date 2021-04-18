package com.vmware.cet.codegen.constant;

import lombok.Getter;

public enum CodegenConstant {

    COMMA_SEPERATOR(","),
    EMPTY_STRING(""),
    NEWLINE("\n");

    @Getter
    private String value;

    CodegenConstant(String value) {
        this.value = value;
    }
}
