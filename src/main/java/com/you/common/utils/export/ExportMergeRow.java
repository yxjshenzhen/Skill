package com.you.common.utils.export;

/**
 * 支持纵向合并Excel
 */
public class ExportMergeRow{

    private Integer startRow;
    private Integer endRow;
    private String value;

    public Integer getStartRow() {
        return startRow;
    }

    public void setStartRow(Integer startRow) {
        this.startRow = startRow;
    }

    public Integer getEndRow() {
        return endRow;
    }

    public void setEndRow(Integer endRow) {
        this.endRow = endRow;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
