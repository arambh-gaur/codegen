package com.vmware.cet.codegen.rest;

import com.vmware.cet.codegen.model.EntityRequestDTO;
import com.vmware.cet.codegen.service.SchemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/generate")
public class GenerateController {

    @Autowired
    private SchemaService schemaService;

    @PostMapping
    public void generateProject(@RequestBody List<EntityRequestDTO> entityRequestList) {
        int id = 1;
        if(!CollectionUtils.isEmpty(entityRequestList)) {
            for(EntityRequestDTO entityRequestDTO : entityRequestList) {
                entityRequestDTO.setId(id);
                ++id;
            }
            schemaService.generateSchema(entityRequestList);
        }
    }

}
