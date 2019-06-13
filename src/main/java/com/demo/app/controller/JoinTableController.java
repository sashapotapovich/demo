package com.demo.app.controller;

import com.demo.app.dto.TableTransfer;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/app")
public class JoinTableController {

    @Autowired
    @Qualifier("appDataSource")
    private DataSource dataSource;
    
    @GetMapping(value = "/fetchColumnNames/{tableName}")
    public List<String> fetchColumnNamesFromTable(@PathVariable String tableName) {
        String sql = "SELECT * FROM " + tableName;
        List<String> columnNames = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            ResultSetMetaData md = rs.getMetaData();
            int colCount = md.getColumnCount();
            for (int i = 0; i < colCount; i++) {
                String col_name = md.getColumnName(i + 1);
                columnNames.add(col_name);
            }
        } catch (SQLException e) {
            log.error(e.getLocalizedMessage());
        }
        return columnNames;
    }
    
    @GetMapping(value = "/fetchDataFromJoinedTables")
    public List<TableTransfer> fetchDataFromJoinedTables(@RequestBody JoinTableTransfer joinTableTransfer) {
        String sql = "SELECT * FROM " + joinTableTransfer.firstTableName + " LEFT JOIN " + joinTableTransfer.secondTableName +
                " ON " + joinTableTransfer.firstTableName + "." + joinTableTransfer.firstJoinColumn + " = "
                + joinTableTransfer.secondTableName + "." + joinTableTransfer.secondJoinColumn;
        List<TableTransfer> listTransfer = new ArrayList<>();
        TableTransfer tableTransfer = new TableTransfer(new ArrayList<>());
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            ResultSetMetaData md = rs.getMetaData();
            int colCount = md.getColumnCount();

            for (int i = 0; i < colCount; i++) {
                String col_name = md.getColumnName(i + 1);
                tableTransfer.getTable().add(col_name);
                log.info("Column Name - " + col_name);
            }
            listTransfer.add(tableTransfer);
            while (rs.next()) {
                TableTransfer tableTransfer2 = new TableTransfer(new ArrayList<>());
                for (int i = 0; i < colCount; i++) {
                    String data = rs.getString(i + 1);
                    tableTransfer2.getTable().add(data);
                }
                listTransfer.add(tableTransfer2);
            }

        } catch (SQLException e) {
            log.error(e.getLocalizedMessage());
        }
        return listTransfer;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    private static class JoinTableTransfer {
        @JsonProperty("firstTableName")
        String firstTableName;
        @JsonProperty("secondTableName")
        String secondTableName;
        @JsonProperty("firstJoinColumn")
        String firstJoinColumn;
        @JsonProperty("secondJoinColumn")
        String secondJoinColumn;
    }
}
