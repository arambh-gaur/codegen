package com.vmware.cet.codegen.repository;

import com.vmware.cet.codegen.exception.DataLayerException;
import com.vmware.cet.codegen.query.SchemaQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class SchemaRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void createSchemaTables(List<Integer> insertionSequence, Map<Integer,String> tableSchemaMap) {
        try {
            log.info("Creating tables for DB schema");
            if(!CollectionUtils.isEmpty(insertionSequence)) {
                for(Integer sequence : insertionSequence) {
                    String query = tableSchemaMap.get(sequence);
                    jdbcTemplate.update(query);
                }
            }
            log.info("Finished creating tables for DB schema");
        } catch (DataAccessException de) {
            log.error("DataAccessException occurred in createSchemaTables for ddl :{}",tableSchemaMap);
            throw new DataLayerException(de.getMessage());
        }
    }

}
