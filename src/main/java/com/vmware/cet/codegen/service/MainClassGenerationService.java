package com.vmware.cet.codegen.service;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.vmware.cet.codegen.constant.CodegenConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import javax.lang.model.element.Modifier;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@Slf4j
public class MainClassGenerationService {

    @Value("${generated.code.source.path}")
    private String generatedCodePath;

    @Value("${generated.code.main.pakage.name}")
    private String mainClassPackage;

    public void generateMainClass() {

        try {
            MethodSpec mainMethod = MethodSpec
                    .methodBuilder("main")
                    .addModifiers(Modifier.PUBLIC,Modifier.STATIC)
                    .addStatement("$T.run("+CodegenConstant.MAIN_CLASSNAME.getValue()+".class, args)",SpringApplication.class)
                    .addParameter(String[].class,"args")
                    .build();

            TypeSpec mainClass = TypeSpec
                    .classBuilder(CodegenConstant.MAIN_CLASSNAME.getValue())
                    .addAnnotation(SpringBootApplication.class)
                    .addAnnotation(Slf4j.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(mainMethod)
                    .build();

            Path path = Paths.get(generatedCodePath);
            JavaFile.builder(mainClassPackage, mainClass).build().writeTo(path);
        } catch (Exception e) {
            log.error("Exception occurred in generateMainClass, exception :\n",e);
        }


    }

}
