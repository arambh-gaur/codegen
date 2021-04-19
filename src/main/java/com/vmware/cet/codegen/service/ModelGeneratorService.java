package com.vmware.cet.codegen.service;

import com.google.common.base.CaseFormat;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.vmware.cet.codegen.constant.DatabaseTypeConstant;
import com.vmware.cet.codegen.exception.BusinessLayerException;
import com.vmware.cet.codegen.model.EntityRequestDTO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ModelGeneratorService {

    @Value("${generated.code.model.pakage.name}")
    private String modelClassPackage;

    @Value("${generated.code.source.path}")
    private String generatedCodePath;

    public String generateModelClass(EntityRequestDTO entityRequestDTO) {
        String className = null;
        try {
            if(entityRequestDTO != null && !CollectionUtils.isEmpty(entityRequestDTO.getFields())) {

                className = entityRequestDTO.getTableName();
                // set the first letter of the classname to uppercase
                if(!StringUtils.isBlank(className)) {
                    className = className.substring(0, 1).toUpperCase() + className.substring(1);
                }


                List<FieldSpec> fields = new ArrayList<>();

                for(EntityRequestDTO.EntityFieldDTO entityFieldDTO : entityRequestDTO.getFields()) {
                    Type type = null;
                    // remove underscores from field names and replace with camel case
                    String fieldName = entityFieldDTO.getFieldName();
                    fieldName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, fieldName);

                    if(DatabaseTypeConstant.VARCHAR.getValue().equals(entityFieldDTO.getDataType())) {
                        type = String.class;
                    } else if(DatabaseTypeConstant.INT.getValue().equals(entityFieldDTO.getDataType())) {
                        type = Integer.class;
                    } else if (DatabaseTypeConstant.BOOLEAN.getValue().equals(entityFieldDTO.getDataType())) {
                        type = Boolean.class;
                    } else if (DatabaseTypeConstant.DATETIME.getValue().equals(entityFieldDTO.getDataType())) {
                        type = Date.class;
                    }

                    FieldSpec field = FieldSpec
                            .builder(type, fieldName)
                            .addModifiers(Modifier.PRIVATE)
                            .build();
                    fields.add(field);
                }

                TypeSpec modelClass = TypeSpec
                        .classBuilder(entityRequestDTO.getTableName())
                        .addAnnotation(Data.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addFields(fields)
                        .build();

                Path path = Paths.get(generatedCodePath);
                JavaFile.builder(modelClassPackage, modelClass).build().writeTo(path);

            }
        } catch (RuntimeException be) {
            log.error("BusinessLayerException occurred in generateModelClass, Exception :\n",be);
            throw be;
        } catch (IOException ie) {
            log.error("IOException occurred in generateModelClass, Exception :\n",ie);
            throw new BusinessLayerException(ie.getMessage());
        }
        return className;
    }

}
