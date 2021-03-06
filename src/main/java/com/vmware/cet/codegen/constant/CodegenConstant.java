package com.vmware.cet.codegen.constant;

import lombok.Getter;

public enum CodegenConstant {

    COMMA_SEPERATOR(","),
    EMPTY_STRING(""),
    NEWLINE("\n"),
    BUSINESS_CLASSNAME("CodegenService"),
    BUSINESS_REFERENCE_VARIABLE("codegenService"),
    REPOSITORY_CLASSNAME("CodegenRepository"),
    REPOSITORY_REFERENCE_VARIABLE("codegenRepository"),
    CONTROLLER_CLASSNAME("CodegenController"),
    MAIN_CLASSNAME("CodegenApplication");

    @Getter
    private String value;

    CodegenConstant(String value) {
        this.value = value;
    }
}
