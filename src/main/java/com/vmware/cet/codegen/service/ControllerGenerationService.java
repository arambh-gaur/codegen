package com.vmware.cet.codegen.service;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.lang.model.element.Modifier;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class ControllerGenerationService {

    @Value("generated.code.source.path")
    private String generatedCodePath;

    public void generateController() {

        try {

            AnnotationSpec requestMappingAnnotation = AnnotationSpec
                    .builder(RequestMapping.class)
                    .addMember("value", "\"/api/v1\"")
                    .build();

            TypeSpec controller = TypeSpec
                    .classBuilder("CodegenController")
                    .addAnnotation(RestController.class)
                    .addAnnotation(requestMappingAnnotation)
                    .addModifiers(Modifier.PUBLIC)
                    .build();

            log.info("Added class");
            Path path = Paths.get(generatedCodePath);
            JavaFile.builder("com.vmware.cet.generated.controller", controller).build().writeTo(path);
        } catch (Exception e) {

        }

    }

}
