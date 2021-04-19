package com.vmware.cet.codegen.service;

import com.vmware.cet.codegen.constant.CodegenConstant;
import com.vmware.cet.codegen.constant.DatabaseConstraint;
import com.vmware.cet.codegen.constant.FieldPlaceholder;
import com.vmware.cet.codegen.constant.SchemaSyntaxConstant;
import com.vmware.cet.codegen.exception.BusinessLayerException;
import com.vmware.cet.codegen.model.EntityRequestDTO;
import com.vmware.cet.codegen.repository.SchemaRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.aspectj.apache.bcel.classfile.Code;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SchemaService {

    @Autowired
    private SchemaRepository schemaRepository;

    @Autowired
    private ClassGeneratorService classGeneratorService;

    @Autowired
    private ModelGeneratorService modelGeneratorService;

    @Autowired
    private BusinessGenerationService businessGenerationService;

    @Autowired
    private ControllerGenerationService controllerGenerationService;

    @Value("${generated.code.service.pakage.name}")
    private String businessClassPackage;

    public void generateSchema(List<EntityRequestDTO> requestList) {
        log.info("Generating schema with input :{}",requestList);
        try {
            LinkedList<Integer> tableCreationSequence = new LinkedList<>();
            Map<Integer, String> tableCreationStatements = new HashMap<>();

            if(!CollectionUtils.isEmpty(requestList)) {

                for(EntityRequestDTO entityRequestDTO : requestList) {
                    List<EntityRequestDTO.EntityFieldDTO> fields = entityRequestDTO.getFields();
                    Integer parentTableIndex = null;

                    String tableStatement = SchemaSyntaxConstant.CREATE_TABLE.getStatement().replace(FieldPlaceholder.TABLE_NAME.getPlaceHolder(),entityRequestDTO.getTableName());
                    List<String> fieldStatements = new ArrayList<>();

                    if(!CollectionUtils.isEmpty(fields)) {
                        for(EntityRequestDTO.EntityFieldDTO entityFieldDTO : fields) {

                            String fieldStatement = SchemaSyntaxConstant.CREATE_FIELD.getStatement().replace(FieldPlaceholder.FIELD_NAME.getPlaceHolder(),entityFieldDTO.getFieldName());
                            fieldStatement = fieldStatement.replace(FieldPlaceholder.FIELD_TYPE.getPlaceHolder(),entityFieldDTO.getDataType());

                            if(BooleanUtils.isTrue(entityFieldDTO.getIsPrimaryKey())) {
                                fieldStatement = fieldStatement.replace(FieldPlaceholder.PRIMARY_KEY.getPlaceHolder(), DatabaseConstraint.PRIMARY_KEY.getConstraint());
                            } else {
                                fieldStatement = fieldStatement.replace(FieldPlaceholder.PRIMARY_KEY.getPlaceHolder(), CodegenConstant.EMPTY_STRING.getValue());
                            }

                            if(BooleanUtils.isTrue(entityFieldDTO.getIsAutoIncrement())) {
                                fieldStatement = fieldStatement.replace(FieldPlaceholder.AUTO_INCREMENT.getPlaceHolder(), DatabaseConstraint.AUTO_INCREMENT.getConstraint());
                            } else {
                                fieldStatement = fieldStatement.replace(FieldPlaceholder.AUTO_INCREMENT.getPlaceHolder(), CodegenConstant.EMPTY_STRING.getValue());
                            }
                            fieldStatements.add(fieldStatement);
                            //check if any fields reference a column in another table as foreign key
                            if(BooleanUtils.isTrue(entityFieldDTO.getIsForeignKey())) {

                                EntityRequestDTO.ForeignKeyMappingDTO foreignKeyMapping = entityFieldDTO.getForeignKeyMapping();
                                String foreignKeyConstraint = DatabaseConstraint.FOREIGN_KEY.getConstraint().replace(FieldPlaceholder.FIELD_NAME.getPlaceHolder(),entityFieldDTO.getFieldName());
                                foreignKeyConstraint = foreignKeyConstraint.replace(FieldPlaceholder.TABLE_NAME.getPlaceHolder(),foreignKeyMapping.getTableName());
                                foreignKeyConstraint = foreignKeyConstraint.replace(FieldPlaceholder.COLUMN_NAME.getPlaceHolder(),foreignKeyMapping.getFieldName());
                                fieldStatements.add(foreignKeyConstraint);

                                /* if any field in this table is referencing a field in another table, check if parent table is
                                * already present in sequence then fetch the index, else add the parent table in the sequence and update index */
                                for(EntityRequestDTO foreignKeyTable : requestList) {
                                    if(foreignKeyTable.getTableName() != null && foreignKeyTable.getTableName().equals(foreignKeyMapping.getTableName())) {
                                        parentTableIndex = tableCreationSequence.indexOf(foreignKeyTable.getId());
                                        if(parentTableIndex == -1) {
                                            tableCreationSequence.add(foreignKeyTable.getId());
                                            parentTableIndex = tableCreationSequence.indexOf(foreignKeyTable.getId());
                                        }
                                    }
                                }
                            }
                        }
                    }
                    String createColumnStatement = String.join(CodegenConstant.COMMA_SEPERATOR.getValue(),fieldStatements);
                    tableStatement = tableStatement.replace(FieldPlaceholder.FIELDS.getPlaceHolder(),createColumnStatement);
                    tableCreationStatements.put(entityRequestDTO.getId(),tableStatement);
                    // if this table has a dependency on a parent table add this table in the sequence just after the parent table
                    if(parentTableIndex != null && parentTableIndex != -1) {
                        tableCreationSequence.add(parentTableIndex + 1,entityRequestDTO.getId());
                    } else {
                        tableCreationSequence.add(entityRequestDTO.getId());
                    }
                    log.info("Finished generating schema for input :{}",entityRequestDTO);

                    modelGeneratorService.generateModelClass(entityRequestDTO);
                    String businessClassname = businessGenerationService.generateBusinessClass(entityRequestDTO);
                    controllerGenerationService.generateController(businessClassPackage,CodegenConstant.BUSINESS_CLASSNAME.getValue());

                }

            }
            log.info("Compiled all create statements");
            //schemaRepository.createSchemaTables(tableCreationSequence,tableCreationStatements);


        } catch (BusinessLayerException be) {
            log.error("Exception occurred in generateSchema for input :{}, Exception :\n",requestList,be);
        }
    }

}
