package com.you.common.utils.export;

import com.alibaba.fastjson.JSON;
import com.you.common.utils.export.annotation.Export;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import jxl.Workbook;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * Excel导出
 */
public class ExportUtils {
    private static final Logger logger = LoggerFactory.getLogger(ExportUtils.class);
    /**
     * 导出
     *
     * @param pramPath
     * @param filePath
     * @param c
     */
    public static void export(String pramPath, String filePath, Collection<?> c, Class clazz) {
        logger.info("pramPath : {} ;  filePath : {}", pramPath, filePath);
        try {
            //获取类注解
            Export classAnnotation = (Export) clazz.getAnnotation(Export.class);
            //获取方法注解
            Field[] fields = clazz.getDeclaredFields();

            List<String> titleList = new ArrayList();
            for (Field f : fields) {
                //获取字段中包含Export的注解
                Export fieldAnnotation = f.getAnnotation(Export.class);
                if (fieldAnnotation != null) {
                    titleList.add(fieldAnnotation.desc());
                }
            }
            String[] title = titleList.toArray(new String[titleList.size()]);

            File fileNew = new File(pramPath);
            if (!fileNew.exists()) {
                boolean flag = fileNew.mkdirs();
                if (flag){
                    logger.info("创建文件夹成功");
                } else {
                    logger.info("创建文件夹失败");
                }
            }

            File file = new File(filePath);
            boolean flag = file.createNewFile();
            if (flag){
                logger.info("创建Excel文件成功");
            } else {
                logger.info("创建Excel文件失败");
            }
            OutputStream os = new FileOutputStream(file.getPath());
            WritableWorkbook wwb = Workbook.createWorkbook(os);
            WritableSheet sheet = wwb.createSheet(classAnnotation.sheetName(), 0);
            Label label = null;

            int num;
            for (num = 0; num < title.length; ++num) {
                label = new Label(num, 0, title[num], getTitleCell());
                sheet.addCell(label);
            }

            num = 0;

            if (!CollectionUtils.isEmpty(c)) {

                List<ExportMergeRow> mergeRowList = new ArrayList<>();
                Integer startColumn = null;
                Integer endColumn = null;
                //合并列index
                Integer removeColumnIndex = null;

                Iterator iterator = c.iterator();
                while (iterator.hasNext()) {
                    Object object = JSON.parseObject(JSON.toJSONString(iterator.next()), clazz);
                    ++num;

                    for (int j = 0; j < fields.length; j++) {
                        fields[j].setAccessible(true);
                        // 字段值
                        Object value = fields[j].get(object);
                        if (value == null){
                            value = "";
                        }
                        String valueStr = "";
                        //获取字段上注解
                        Export fieldAnnotation = fields[j].getAnnotation(Export.class);
                        if (value instanceof Date) {
                            if (!StringUtils.isEmpty(fieldAnnotation.format()) && !StringUtils.isEmpty(value)) {
                                valueStr = dateToStr((Date) value, fieldAnnotation.format());
                            }
                        } else {
                            if (!StringUtils.isEmpty(fieldAnnotation.mapping())) {
                                Map map = JSON.parseObject(fieldAnnotation.mapping(), Map.class);
                                if (!CollectionUtils.isEmpty(map)) {
                                    valueStr = map.get(value.toString()) == null ? "" : map.get(value.toString()).toString();
                                }
                            } else if (!StringUtils.isEmpty(fieldAnnotation.merge())){
                                removeColumnIndex = j;
                                Map mergeMap = JSON.parseObject(fieldAnnotation.merge(), Map.class);
                                if (!CollectionUtils.isEmpty(mergeMap)) {
                                    startColumn = Integer.parseInt(mergeMap.get(Export.MERGE_START_COLUMN).toString());
                                    endColumn = Integer.parseInt(mergeMap.get(Export.MERGE_END_COLUMN).toString());
                                    //记录startRow
                                    if (CollectionUtils.isEmpty(mergeRowList)){
                                        ExportMergeRow mergeRow = new ExportMergeRow();
                                        mergeRow.setStartRow(num);
                                        mergeRow.setValue(value.toString());
                                        mergeRowList.add(mergeRow);
                                    } else {
                                        //获取最后一个
                                        ExportMergeRow mergeRow = mergeRowList.get(mergeRowList.size()-1);
                                        //字段出现变化
                                        if (mergeRow.getEndRow() == null){
                                            if (!mergeRow.getValue().equals(value.toString())){
                                                mergeRow.setEndRow(num - 1);

                                                //追加新的节点
                                                ExportMergeRow newMergeRow = new ExportMergeRow();
                                                newMergeRow.setStartRow(num);
                                                newMergeRow.setValue(value.toString());
                                                mergeRowList.add(newMergeRow);
                                            } else if (num == c.size()){
                                                mergeRow.setEndRow(num);
                                            }
                                        }
                                    }
                                }
                            } else {
                                valueStr = value.toString();
                            }
                        }

                        Label labelD = new Label(j, num, value == null ? "" : valueStr, getNormolCell());
                        sheet.addCell(labelD);
                    }
                }

                //删除merge标记列
                if (null != removeColumnIndex) {
                    sheet.removeColumn(removeColumnIndex);
                }
                //合并单元格
                if (startColumn != null && endColumn != null && !CollectionUtils.isEmpty(mergeRowList)) {
                    for (int column = startColumn; column <= endColumn; column++){
                        for (ExportMergeRow mergeRow : mergeRowList){
                            sheet.mergeCells(column, mergeRow.getStartRow(), column,
                                    mergeRow.getEndRow() == null ? mergeRow.getStartRow() : mergeRow.getEndRow());
                        }
                    }
                }

            }

            wwb.write();
            wwb.close();
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 设置单元格样式
     *
     * @return
     */
    private static WritableCellFormat getNormolCell() {
        WritableCellFormat format = new WritableCellFormat();
        try {
            format.setAlignment(jxl.format.Alignment.CENTRE);
            format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return format;
    }

    /**
     * 设置列头样式
     *
     * @return
     */
    private static WritableCellFormat getTitleCell() {
        WritableCellFormat format = new WritableCellFormat();
        try {
            format.setAlignment(jxl.format.Alignment.CENTRE);
            format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
            format.setBackground(Colour.GREY_40_PERCENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return format;
    }

    private static String dateToStr(Date date, String pattern) {
        if (date != null && pattern != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.format(date);
        } else {
            return null;
        }
    }
}
