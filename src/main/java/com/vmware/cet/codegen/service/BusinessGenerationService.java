package com.vmware.cet.codegen.service;

import com.vmware.cet.codegen.exception.BusinessLayerException;
import com.vmware.cet.codegen.model.EntityRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class BusinessGenerationService {

    public void generateBusinessClass(EntityRequestDTO entityRequestDTO) {
        try {

        } catch (RuntimeException e) {
            log.error("RuntimeException occurred in generateBusinessClass");
            throw new BusinessLayerException(e.getMessage());
        }
    }

}
