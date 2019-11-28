package com.you.common.utils.export.demo;

import com.alibaba.fastjson.JSON;
import com.you.common.utils.export.ExportUtils;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ExportDemo {

    public static void main(String[] args){

        System.out.println(JSON.toJSONString(createDataList(100)));
        String dir = "C:\\Users\\user\\Desktop\\";
        String path = dir + "Export.xls";
        ExportUtils.export(dir, path, createDataList(100), DemoExport.class);//导出部分制定字段到XML中
    }

    private static List<DemoExport> createDataList(int count){
        List<DemoExport> list = new ArrayList<>();
        for (int i= 0; i < count; i ++){
            DemoExport export = new DemoExport();

            int no = i%10;
            export.setUserNo("10000" + no);
            export.setUserNoShow("10000" + no);
            export.setUserName("张三" + no);
            export.setOrderNo("T-1000000000000"+i);
            export.setPayTime(new Date());
            export.setDate(new Date());
            export.setAmt(BigDecimal.TEN.multiply(new BigDecimal(i)));
            export.setStatus(no+"");

            list.add(export);
        }
        list.sort(Comparator.comparing(DemoExport::getUserNo));
        return list;
    }

}
