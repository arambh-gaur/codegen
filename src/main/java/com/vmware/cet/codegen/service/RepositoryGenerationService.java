package com.vmware.cet.codegen.service;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.vmware.cet.codegen.constant.CodegenConstant;
import com.vmware.cet.codegen.constant.DatabaseTypeConstant;
import com.vmware.cet.codegen.exception.BusinessLayerException;
import com.vmware.cet.codegen.model.EntityRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class RepositoryGenerationService {

    @Value("${generated.code.repository.pakage.name}")
    private String repositoryClassPackage;

    @Value("${generated.code.source.path}")
    private String generatedCodePath;

    @Value("${generated.code.model.pakage.name}")
    private String modelClassPackage;

    public String generateRepositoryClass(String modelClassName, EntityRequestDTO entityRequestDTO) {
        String fullyQualifiedClassName;
        try {

            TypeSpec.Builder builder;
            Class primaryKeyClass = null;
            try {
                if(entityRequestDTO != null && !CollectionUtils.isEmpty(entityRequestDTO.getFields())) {
                    for(EntityRequestDTO.EntityFieldDTO entityFieldDTO : entityRequestDTO.getFields()) {
                        if(entityFieldDTO != null && BooleanUtils.isTrue(entityFieldDTO.getIsPrimaryKey())) {
                            if(DatabaseTypeConstant.INT.getValue().equals(entityFieldDTO.getDataType())) {
                                primaryKeyClass = Integer.class;
                            } else if(DatabaseTypeConstant.BIGINT.getValue().equals(entityFieldDTO.getDataType())) {
                                primaryKeyClass = Long.class;
                            }
                            break;
                        }
                    }
                }
                ClassName clazz = ClassName.get(modelClassPackage, modelClassName);
                builder = TypeSpec.interfaceBuilder(CodegenConstant.REPOSITORY_CLASSNAME.getValue())
                        .addAnnotation(Repository.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addSuperinterface(ParameterizedTypeName.get(ClassName.get(JpaRepository.class),
                                clazz,
                                ClassName.get(primaryKeyClass)));
            } catch (MirroredTypeException mte) {
                DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
                builder = TypeSpec.interfaceBuilder(CodegenConstant.REPOSITORY_CLASSNAME.getValue())
                        .addAnnotation(Repository.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addSuperinterface(ParameterizedTypeName.get(ClassName.get(JpaRepository.class),ClassName.get(classTypeMirror),ClassName.get(Long.class)));
            }

            TypeSpec repositoryClass = builder.build();

            Path path = Paths.get(generatedCodePath);
            JavaFile.builder(repositoryClassPackage, repositoryClass).build().writeTo(path);
            fullyQualifiedClassName = repositoryClassPackage+"."+CodegenConstant.REPOSITORY_CLASSNAME.getValue();
        } catch (IOException e) {
            log.error("IOException occurred in generateRepositoryClass, Exception :\n",e);
            throw new BusinessLayerException(e.getMessage());
        } catch (RuntimeException e) {
            log.error("RuntimeException occurred in generateRepositoryClass, Exception :\n",e);
            throw new BusinessLayerException(e.getMessage());
        }
        return fullyQualifiedClassName;
    }

}
