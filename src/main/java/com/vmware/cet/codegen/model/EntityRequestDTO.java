package com.vmware.cet.codegen.model;

import lombok.Data;

import javax.xml.datatype.DatatypeConstants;
import java.util.List;

@Data
public class EntityRequestDTO {

    private Integer id;
    private String tableName;
    private List<EntityFieldDTO> fields;

    @Data
    public static class EntityFieldDTO {

        private String fieldName;
        private String dataType;
        private Boolean isForeignKey;
        private Boolean isPrimaryKey;
        private Boolean isUnique;
        private Boolean isAutoIncrement;
        private ForeignKeyMappingDTO foreignKeyMapping;

    }

    @Data
    public static class ForeignKeyMappingDTO {
        private String tableName;
        private String fieldName;
    }

}
