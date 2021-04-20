package com.vmware.cet.codegen.service;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.vmware.cet.codegen.constant.CodegenConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @Value("${generated.code.model.pakage.name}")
    private String modelClassPackage;

    public void generateController(String packageName, String businessClassname, String modelClassname) {

        try {
            List<FieldSpec> fields = new ArrayList<>();
            List<MethodSpec> methods = new ArrayList<>();
            AnnotationSpec requestMappingAnnotation = AnnotationSpec
                    .builder(RequestMapping.class)
                    .addMember("value", "\"/api/v1\"")
                    .build();

            AnnotationSpec getMappingAnnotation = AnnotationSpec
                    .builder(GetMapping.class)
                    .build();

            MethodSpec fetchMethod = MethodSpec
                    .methodBuilder("fetchEntity")
                    .returns(List.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(getMappingAnnotation)
                    .addStatement("return "+CodegenConstant.BUSINESS_REFERENCE_VARIABLE.getValue()+".fetchProjects()")
                    .build();

            AnnotationSpec requestBodyAnnotation = AnnotationSpec.builder(RequestBody.class).build();
            ParameterSpec.Builder paramSpecBuilder;
            try {
                ClassName clazz = ClassName.get(modelClassPackage, modelClassname);
                paramSpecBuilder = ParameterSpec.builder(clazz, "project", Modifier.FINAL);
            } catch (MirroredTypeException mte) {
                DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
                paramSpecBuilder = ParameterSpec.builder(classTypeMirror.getClass(), "project", Modifier.FINAL);
            }
            ParameterSpec requestBody = paramSpecBuilder.addAnnotation(requestBodyAnnotation).build();

            MethodSpec saveMethod = MethodSpec
                    .methodBuilder("saveEntity")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(PostMapping.class)
                    .addStatement("codegenService.saveProject(project)")
                    .addParameter(requestBody)
                    .returns(String.class)
                    .addStatement("return \"project details saved successfully\"")
                    .build();

            AnnotationSpec pathParamAnnotation = AnnotationSpec
                    .builder(PathVariable.class)
                    .addMember("value", "\"projectId\"")
                    .build();
            ParameterSpec projectId = ParameterSpec
                    .builder(Integer.class, "projectId", Modifier.FINAL)
                    .addAnnotation(pathParamAnnotation).build();

            MethodSpec updateMethod = MethodSpec
                    .methodBuilder("updateEntity")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(PutMapping.class)
                    .addStatement("codegenService.updateProject(project, projectId)")
                    .addParameter(requestBody)
                    .addParameter(projectId)
                    .returns(String.class)
                    .addStatement("return \"project details updated successfully\"")
                    .build();

            MethodSpec deleteMethod = MethodSpec
                    .methodBuilder("deleteEntity")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(DeleteMapping.class)
                    .addStatement("codegenService.deleteProject(projectId)")
                    .addParameter(projectId)
                    .returns(String.class)
                    .addStatement("return \"project details deleted successfully\"")
                    .build();

            methods.add(fetchMethod);
            methods.add(saveMethod);
            methods.add(updateMethod);
            methods.add(deleteMethod);

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

            fields.add(businessReferenceVariable);

            TypeSpec controller = TypeSpec
                    .classBuilder(CodegenConstant.CONTROLLER_CLASSNAME.getValue())
                    .addAnnotation(RestController.class)
                    .addAnnotation(Slf4j.class)
                    .addAnnotation(requestMappingAnnotation)
                    .addModifiers(Modifier.PUBLIC)
                    .addFields(fields)
                    .addMethods(methods)
                    .build();

            log.info("Added class");
            Path path = Paths.get(generatedCodePath);
            JavaFile.builder(controllerClassPackage, controller).build().writeTo(path);
        } catch (Exception e) {
            log.error("Exception occurred in generateController, exception :\n",e);
        }

    }

}
