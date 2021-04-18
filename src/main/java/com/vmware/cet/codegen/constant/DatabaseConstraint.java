package com.vmware.cet.codegen.constant;

import lombok.Getter;

public enum DatabaseConstraint {

    PRIMARY_KEY(" primary key"),
    AUTO_INCREMENT(" auto_increment"),
    FOREIGN_KEY(" foreign key (:fieldName) references :tableName(:columnName)");

    @Getter
    private String constraint;

    DatabaseConstraint(String constraint) {
        this.constraint = constraint;
    }
}
