package com.demo.app.controller;

import com.demo.app.dto.TableModel;
import com.demo.app.exceptions.NotFoundException;
import com.demo.app.service.DatabaseService;
import java.sql.SQLException;
import java.util.Set;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/app")
public class DatabaseController {

    private DatabaseService databaseService;

    @Autowired
    public DatabaseController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @GetMapping(value = "/database")
    public Set<String> getAllTableNames() throws SQLException {
        return databaseService.getAllTableNames();
    }

    @GetMapping(value = "/database/{tableName}")
    public TableModel getTableName(@PathVariable String tableName) {
        return databaseService.getTable(tableName);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/database")
    @ResponseStatus(HttpStatus.CREATED)
    public TableModel createTable(@RequestBody TableModel tableModel) throws SQLException {
        if (tableModel == null){
            throw new NotFoundException("Not found", 1, "a");
        } else if (tableModel.getTableName() == null || tableModel.getTableName().isEmpty()){
            throw new NotFoundException("Table Name not Found", 1, "b");
        }
        return databaseService.createTableFromModel(tableModel);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "database/{tableName}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteTable(@PathVariable String tableName){
        if (tableName == null || tableName.isEmpty()){
            throw new NotFoundException("tableName", 1, "tableName");
        }
        databaseService.droptable(tableName);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/database/{tableName}")
    @ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
    public ResponseEntity alterTable(@PathVariable String tableName, @RequestBody TableModel tableModel) {
        return new ResponseEntity<>("I'm a teapot", HttpStatus.I_AM_A_TEAPOT);
    }

}
