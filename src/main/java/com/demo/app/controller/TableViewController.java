package com.demo.app.controller;

import com.demo.app.dto.TableTransfer;
import com.demo.app.service.TableService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/app")
public class TableViewController {

    private TableService tableService;

    @Autowired
    public TableViewController(TableService tableService) {
        this.tableService = tableService;
    }

    @GetMapping("/tables/{tableName}")
    public List<TableTransfer> fetchDataForTable(@PathVariable String tableName) {
        return tableService.fetchDataForTable(tableName);
    }
    
}
