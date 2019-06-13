package com.demo.app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ColumnDefinition {
    
    @JsonProperty("columnName")
    private String columnName;
    @JsonProperty("dataType")
    private String dataType;
    @JsonProperty("precision")
    private Integer precision;
    @JsonProperty("scale")
    private Integer scale;
    @JsonProperty("nullable")
    private Boolean nullable;
    
}
