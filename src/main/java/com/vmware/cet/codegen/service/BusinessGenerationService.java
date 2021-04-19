package com.vmware.cet.codegen.service;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.vmware.cet.codegen.constant.CodegenConstant;
import com.vmware.cet.codegen.exception.BusinessLayerException;
import com.vmware.cet.codegen.model.EntityRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class BusinessGenerationService {

    @Value("${generated.code.service.pakage.name}")
    private String businessClassPackage;

    @Value("${generated.code.source.path}")
    private String generatedCodePath;

    public String generateBusinessClass(EntityRequestDTO entityRequestDTO) {
        String fullyQualifiedClassName = null;
        try {

            MethodSpec sumOfTen = MethodSpec
                    .methodBuilder("sumOfTen")
                    .addStatement("int sum = 0")
                    .beginControlFlow("for (int i = 0; i <= 10; i++)")
                    .addStatement("sum += i")
                    .endControlFlow()
                    .returns(Integer.class)
                    .addStatement("return sum")
                    .build();

            TypeSpec controller = TypeSpec
                    .classBuilder(CodegenConstant.BUSINESS_CLASSNAME.getValue())
                    .addAnnotation(Service.class)
                    .addAnnotation(Slf4j.class)
                    .addMethod(sumOfTen)
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
