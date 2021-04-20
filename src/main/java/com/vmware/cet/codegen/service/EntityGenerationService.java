package com.vmware.cet.codegen.service;

import com.google.common.base.CaseFormat;
import com.squareup.javapoet.AnnotationSpec;
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
import org.springframework.web.bind.annotation.RequestMapping;

import javax.lang.model.element.Modifier;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class EntityGenerationService {

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
                    className = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, className);
                    className = className.substring(0, 1).toUpperCase() + className.substring(1);
                }


                List<FieldSpec> fields = new ArrayList<>();

                for(EntityRequestDTO.EntityFieldDTO entityFieldDTO : entityRequestDTO.getFields()) {
                    Type type = null;
                    List<AnnotationSpec> fieldAnnotations = new ArrayList<>();
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

                    if(BooleanUtils.isTrue(entityFieldDTO.getIsPrimaryKey())) {
                        AnnotationSpec idAnnotation = AnnotationSpec
                                .builder(Id.class)
                                .build();

                        AnnotationSpec generationStrategyAnnotation = AnnotationSpec
                                .builder(GeneratedValue.class)
                                .addMember("strategy","$T.IDENTITY", GenerationType.class)
                                .build();
                        fieldAnnotations.add(idAnnotation);
                        fieldAnnotations.add(generationStrategyAnnotation);

                    } else {
                        AnnotationSpec columnAnnotation = AnnotationSpec
                                .builder(Column.class)
                                .addMember("name", "\""+entityFieldDTO.getFieldName()+"\"")
                                .build();
                        fieldAnnotations.add(columnAnnotation);
                    }

                    FieldSpec field = FieldSpec
                            .builder(type, fieldName)
                            .addModifiers(Modifier.PRIVATE)
                            .addAnnotations(fieldAnnotations)
                            .build();
                    fields.add(field);
                }

                List<AnnotationSpec> classAnnotations = new ArrayList<>();
                AnnotationSpec entityAnnotation = AnnotationSpec
                        .builder(Entity.class)
                        .build();
                classAnnotations.add(entityAnnotation);

                AnnotationSpec tableAnnotation = AnnotationSpec
                        .builder(Table.class)
                        .addMember("name", "\""+entityRequestDTO.getTableName()+"\"")
                        .build();
                classAnnotations.add(tableAnnotation);

                AnnotationSpec dataAnnotation = AnnotationSpec
                        .builder(Data.class)
                        .build();
                classAnnotations.add(dataAnnotation);

                TypeSpec modelClass = TypeSpec
                        .classBuilder(className)
                        .addAnnotations(classAnnotations)
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
