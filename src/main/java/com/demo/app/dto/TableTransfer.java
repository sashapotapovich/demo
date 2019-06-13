package com.demo.app.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TableTransfer {
    
    private List<String> table;
}
