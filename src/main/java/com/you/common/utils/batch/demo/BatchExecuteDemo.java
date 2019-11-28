package com.you.common.utils.batch.demo;

import com.alibaba.fastjson.JSON;
import com.you.common.utils.batch.BatchExecuteUtil;
import java.util.Arrays;
import java.util.List;

public class BatchExecuteDemo {
    public static void main(String[] args){
        List<String> list = Arrays.asList("1","2","3","4","5","6","7","8","9","10","11","12");
        try {
            List resultList1 = BatchExecuteUtil.batchExecute(new BatchExecuteUtil.BatchExecuteInterface<List,String>() {
                @Override
                public List singleExecute(String o) {
                    return work(o);
                }
            },list);

            System.out.println(JSON.toJSONString(resultList1));

            //java 8 lambda
            List resultList2 = BatchExecuteUtil.batchExecute((o) -> work(o.toString()),list);
            System.out.println(JSON.toJSONString(resultList2));

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private static List work(String para){
        long time = Integer.parseInt(para)*1000L;
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(para);
    }
}
