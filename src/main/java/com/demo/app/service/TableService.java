package com.demo.app.service;

import com.demo.app.dto.TableTransfer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class TableService {
    
    private final DataSource dataSource;

    public TableService(@Qualifier("appDataSource") DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<TableTransfer> fetchDataForTable(String tableName){
        String sql = "SELECT * FROM " + tableName;
        List<TableTransfer> listTransfer = new ArrayList<>();
        TableTransfer tableTransfer = new TableTransfer(new ArrayList<>());
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            ResultSetMetaData md = rs.getMetaData();
            int colCount = md.getColumnCount();

            for (int i = 0; i < colCount; i++) {
                String col_name = md.getColumnName(i + 1);
                String columnTypeName = md.getColumnTypeName(i + 1);
                log.info(columnTypeName);
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
}
