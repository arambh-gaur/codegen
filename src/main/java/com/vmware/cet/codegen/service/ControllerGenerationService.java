package com.vmware.cet.codegen.service;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.vmware.cet.codegen.constant.CodegenConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ControllerGenerationService {

    @Value("${generated.code.source.path}")
    private String generatedCodePath;

    @Value("${generated.code.controller.pakage.name}")
    private String controllerClassPackage;

    public void generateController(String packageName, String businessClassname) {

        try {

            AnnotationSpec requestMappingAnnotation = AnnotationSpec
                    .builder(RequestMapping.class)
                    .addMember("value", "\"/api/v1\"")
                    .build();

            /*Class<?> clazz = Class.forName(businessClassname);
            Type businessClassType = clazz;*/

            FieldSpec.Builder fsBuilder;
            try {
                ClassName clazz = ClassName.get(packageName, businessClassname);
                fsBuilder = FieldSpec.builder(clazz, CodegenConstant.BUSINESS_REFERENCE_VARIABLE.getValue())
                        .addModifiers(Modifier.PRIVATE)
                        .addAnnotation(Autowired.class);
            } catch (MirroredTypeException mte) {
                DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
                fsBuilder = FieldSpec.builder(TypeName.get(classTypeMirror), CodegenConstant.BUSINESS_REFERENCE_VARIABLE.getValue())
                        .addModifiers(Modifier.PRIVATE)
                        .addAnnotation(Autowired.class);
            }

            FieldSpec businessReferenceVariable = fsBuilder.build();

            List<FieldSpec> fields = new ArrayList<>();
            fields.add(businessReferenceVariable);

            TypeSpec controller = TypeSpec
                    .classBuilder(CodegenConstant.CONTROLLER_CLASSNAME.getValue())
                    .addAnnotation(RestController.class)
                    .addAnnotation(Slf4j.class)
                    .addAnnotation(requestMappingAnnotation)
                    .addModifiers(Modifier.PUBLIC)
                    .addFields(fields)
                    .build();

            log.info("Added class");
            Path path = Paths.get(generatedCodePath);
            JavaFile.builder(controllerClassPackage, controller).build().writeTo(path);
        } catch (Exception e) {
            log.error("Exception occurred in generateController, exception :\n",e);
        }

    }

    private void getFieldSpec() {
        FieldSpec.Builder fsBuilder;
        try {
            ClassName clazz = ClassName.get("package.to.services", "AService");
            fsBuilder = FieldSpec.builder(clazz, "aService")
                    .addModifiers(Modifier.PRIVATE)
                    .addAnnotation(Autowired.class);
        } catch (MirroredTypeException mte) {
            DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
            fsBuilder = FieldSpec.builder(TypeName.get(classTypeMirror), "aService")
                    .addModifiers(Modifier.PRIVATE)
                    .addAnnotation(Autowired.class);
        }
    }

}
