package com.vmware.cet.codegen.service;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.vmware.cet.codegen.model.EntityRequestDTO;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.lang.model.element.Modifier;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class ClassGeneratorService {

    @Autowired
    private ByteBuddy byteBuddy;

    @Value("generated.code.source.path")
    private String generatedCodePath;

    public void generateClassesByteBuddy(EntityRequestDTO entityRequestDTO) {

        try {
            Map<TypeDescription,File> fileMap = byteBuddy.subclass(Object.class)
                    .name("Foo")
                    .make()
                    .saveIn(new File("/Users/arambhg/Desktop/code-gen-project/codegen/temp"));
            List<File> filesToCompile = new ArrayList<>();
            for(TypeDescription typeDefinitions : fileMap.keySet()) {
                File generatedClassFile = fileMap.get(typeDefinitions);
                log.info("Got the file mate");
            }

            log.info("Finished generating Foo class");
        } catch (Exception e) {
            log.error("Exception occurred while generating dynamic class, Exception :\n",e);
        }
    }

    public void generateClassJavaPoet() {

        try {
            FieldSpec name = FieldSpec
                    .builder(String.class, "name")
                    .addModifiers(Modifier.PUBLIC)
                    .build();

            MethodSpec sumOfTen = MethodSpec
                    .methodBuilder("sumOfTen")
                    .addStatement("int sum = 0")
                    .beginControlFlow("for (int i = 0; i <= 10; i++)")
                    .addStatement("sum += i")
                    .endControlFlow()
                    .build();

            TypeSpec person = TypeSpec
                    .classBuilder("Man")
                    .addModifiers(Modifier.PUBLIC)
                    .addField(name)
                    .addMethod(MethodSpec
                            .methodBuilder("getName")
                            .addModifiers(Modifier.PUBLIC)
                            .returns(String.class)
                            .addStatement("return this.name")
                            .build())
                    .addMethod(MethodSpec
                            .methodBuilder("setName")
                            .addParameter(String.class, "name")
                            .addModifiers(Modifier.PUBLIC)
                            .returns(String.class)
                            .addStatement("this.name = name")
                            .build())
                    .addMethod(sumOfTen)
                    .build();

            log.info("Added class");
            Path path = Paths.get(generatedCodePath);
            JavaFile.builder("com.vmware.com", person).build().writeTo(path);
        } catch (Exception e) {

        }

    }

}
