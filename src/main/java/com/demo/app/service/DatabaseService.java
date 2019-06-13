package com.demo.app.service;

import com.demo.app.dto.ColumnDefinition;
import com.demo.app.dto.TableModel;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class DatabaseService {

    private final DataSource dataSource;

    public DatabaseService(@Qualifier("appDataSource") DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public TableModel createTableFromModel(TableModel tableModel) throws SQLException {
        String tableName = tableModel.getTableName();
        List<ColumnDefinition> columns = tableModel.getColumns();
        StringBuilder str = new StringBuilder();
        columns.forEach((ColumnDefinition columnDefinition) -> {
            str.append(columnDefinition.getColumnName()).append(" ")
               .append(columnDefinition.getDataType());
            Integer precision = columnDefinition.getPrecision();
            Integer scale = columnDefinition.getScale();
            if (precision != null && precision != 0) {
                if (scale != null && scale != 0) {
                    str.append("(").append(precision).append(",")
                       .append(scale).append(")");
                } else {
                    str.append("(").append(precision).append(")");
                }
            }
            if (columnDefinition.getNullable()) {
                str.append(" ").append("not null");
            }
            str.append(", ");
        });
        int i = str.toString().lastIndexOf(", ");
        String substring = str.toString().substring(0, i);
        Connection connection = dataSource.getConnection();

        String sql = "CREATE TABLE " + tableName + "(" + substring + ")";
        log.info("SQL --- " + sql);
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.executeUpdate();
        connection.commit();
        connection.close();
        return tableModel;
    }

    public Set<String> getAllTableNames() throws SQLException {
        String sql = "SELECT table_name FROM information_schema.tables WHERE table_schema='public' AND table_type='BASE TABLE'";
        Set<String> tableNames = new HashSet<>();
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            tableNames.add(resultSet.getString("table_name"));
        }
        return tableNames;
    }

    public TableModel getTable(String tableName) {
        String sql = "SELECT * FROM " + tableName;
        TableModel tableModel = new TableModel(tableName, new ArrayList<ColumnDefinition>());
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet rs = preparedStatement.executeQuery();
            ResultSetMetaData md = rs.getMetaData();
            int colCount = md.getColumnCount();
            for (int i = 1; i < colCount + 1; i++) {
                String columnName = md.getColumnName(i);
                String columnTypeName = md.getColumnTypeName(i);
                int precision = md.getPrecision(i);
                int scale = md.getScale(i);
                System.out.println("Column name - " + md.getColumnName(i) + ", Column Type name - " + md.getColumnTypeName(i));
                System.out.println("Precision - " + md.getPrecision(i) + ", Scale - " + md.getScale(i) + ", Class name - " +
                                           md.getColumnClassName(i));
                boolean nullable = md.isNullable(i) == ResultSetMetaData.columnNullable;
                tableModel.getColumns().add(new ColumnDefinition(columnName, columnTypeName, precision, scale, nullable));
            }

        } catch (SQLException e) {
            log.error(e.getLocalizedMessage());
        }
        return tableModel;
    }

    @Transactional
    public void droptable(String tableName) {
        String sqlChecktableName = "SELECT table_name FROM information_schema.tables WHERE table_schema='public' " +
                "AND table_type='BASE TABLE' and table_name like '%s'";
        String checkTableName = String.format(sqlChecktableName, tableName);
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(checkTableName);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                String tableName1 = rs.getString("table_name");
                if (tableName1 != null && !tableName.isEmpty()) {
                    String dropTable = "DROP TABLE " + tableName;
                    PreparedStatement drop = connection.prepareStatement(dropTable);
                    drop.executeUpdate();
                    connection.commit();
                    drop.close();
                    log.info("Table - {} was deleted", tableName.toUpperCase());
                } else {
                    log.error("Table - {} was not found", tableName.toUpperCase());
                }
            } else {
                log.error("Table - {} was not found", tableName.toUpperCase());
            }
            HashSet set = new HashSet<>(new ArrayList<>());
            preparedStatement.close();
        } catch (SQLException e) {
            log.error(e.getLocalizedMessage());
        }
    }
}
