package com.vmware.cet.codegen.service;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.vmware.cet.codegen.constant.CodegenConstant;
import com.vmware.cet.codegen.exception.BusinessLayerException;
import com.vmware.cet.codegen.model.EntityRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class BusinessGenerationService {

    @Value("${generated.code.service.pakage.name}")
    private String businessClassPackage;

    @Value("${generated.code.source.path}")
    private String generatedCodePath;

    @Value("${generated.code.repository.pakage.name}")
    private String repositoryClassPackage;

    @Value("${generated.code.model.pakage.name}")
    private String modelClassPackage;

    public String generateBusinessClass(EntityRequestDTO entityRequestDTO, String modelClassname) {
        String fullyQualifiedClassName = null;
        try {

            List<MethodSpec> methods = new ArrayList<>();
            MethodSpec fetchProjects = MethodSpec
                    .methodBuilder("fetchProjects")
                    .returns(List.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("return "+CodegenConstant.REPOSITORY_REFERENCE_VARIABLE.getValue()+".findAll()")
                    .build();
            methods.add(fetchProjects);

            MethodSpec.Builder saveProjectBuilder;

            try {
                ClassName clazz = ClassName.get(modelClassPackage, modelClassname);
                saveProjectBuilder = MethodSpec
                        .methodBuilder("saveProject")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(clazz,"project")
                        .addStatement(CodegenConstant.REPOSITORY_REFERENCE_VARIABLE.getValue()+".save(project)");
            } catch (MirroredTypeException mte) {
                DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
                saveProjectBuilder = MethodSpec
                        .methodBuilder("saveProject")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(classTypeMirror.getClass(),"project")
                        .addStatement(CodegenConstant.REPOSITORY_REFERENCE_VARIABLE.getValue()+".save(project)");
            }

            MethodSpec saveProject = saveProjectBuilder.build();
            methods.add(saveProject);

            FieldSpec.Builder builder;
            try {
                ClassName clazz = ClassName.get(repositoryClassPackage, CodegenConstant.REPOSITORY_CLASSNAME.getValue());
                builder = FieldSpec.builder(clazz, CodegenConstant.REPOSITORY_REFERENCE_VARIABLE.getValue())
                        .addModifiers(Modifier.PRIVATE)
                        .addAnnotation(Autowired.class);
            } catch (MirroredTypeException mte) {
                DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
                builder = FieldSpec.builder(TypeName.get(classTypeMirror), CodegenConstant.REPOSITORY_CLASSNAME.getValue())
                        .addModifiers(Modifier.PRIVATE)
                        .addAnnotation(Autowired.class);
            }

            FieldSpec repositoryClassAutowiring = builder.build();

            TypeSpec controller = TypeSpec
                    .classBuilder(CodegenConstant.BUSINESS_CLASSNAME.getValue())
                    .addAnnotation(Service.class)
                    .addAnnotation(Slf4j.class)
                    .addField(repositoryClassAutowiring)
                    .addMethods(methods)
                    .addModifiers(Modifier.PUBLIC)
                    .build();

            Path path = Paths.get(generatedCodePath);
            JavaFile.builder(businessClassPackage, controller).build().writeTo(path);
            fullyQualifiedClassName = businessClassPackage+"."+CodegenConstant.BUSINESS_CLASSNAME.getValue();
        } catch (IOException e) {
            log.error("IOException occurred in generateBusinessClass, Exception :\n",e);
            throw new BusinessLayerException(e.getMessage());
        } catch (RuntimeException e) {
            log.error("RuntimeException occurred in generateBusinessClass, Exception :\n",e);
            throw new BusinessLayerException(e.getMessage());
        }
        return fullyQualifiedClassName;
    }

}
