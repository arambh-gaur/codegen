package com.vmware.cet.codegen.service;

import com.squareup.javapoet.FieldSpec;
import com.vmware.cet.codegen.exception.BusinessLayerException;
import com.vmware.cet.codegen.model.EntityRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.lang.model.element.Modifier;
import java.util.List;

@Service
@Slf4j
public class ModelGeneratorService {

    public String generateModelClass(EntityRequestDTO entityRequestDTO) {
        String className;
        try {
            if(entityRequestDTO != null && !CollectionUtils.isEmpty(entityRequestDTO.getFields())) {
                for(EntityRequestDTO.EntityFieldDTO entityFieldDTO : entityRequestDTO.getFields()) {
                    FieldSpec name = FieldSpec
                            .builder(String.class, "name")
                            .addModifiers(Modifier.PUBLIC)
                            .build();
                }
            }
        } catch (BusinessLayerException be) {
            log.error("BusinessLayerException occurred in generateModelClass, Exception :\n",be);
            throw be;
        }
        return "";
    }

}
