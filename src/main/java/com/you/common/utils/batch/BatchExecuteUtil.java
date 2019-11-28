package com.you.common.utils.batch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 多线程批量执行某一个方法
 */
public class BatchExecuteUtil {

    public static List batchExecute(BatchExecuteInterface bei, List list) throws Exception {

        ExecutorService pool = Executors.newFixedThreadPool(10);
        List<Future> futureList = new ArrayList<Future>();
        for (int i = 0; i < list.size(); i++) {
            Callable c = new BatchCallable(bei,list.get(i));
            Future f = pool.submit(c);
            futureList.add(f);
        }
        // 关闭线程池
        pool.shutdown();

        List resultList = new ArrayList<>();
        // 获取所有并发任务的运行结果
        for (Future f : futureList) {
            Object o = f.get();
            if (o instanceof Collection){
                resultList.addAll((Collection) o);
            } else {
                if (o != null){
                    resultList.add(o);
                }
            }
        }
        return resultList;
    }

    static class BatchCallable<R,P> implements Callable<Object> {

        private BatchExecuteInterface bei;

        private P p;

        BatchCallable(BatchExecuteInterface bei, P p) {
            this.bei = bei;
            this.p = p;
        }

        @Override
        public R call() throws Exception {
            return (R) bei.singleExecute(p);
        }
    }

    public interface BatchExecuteInterface<R,P> {
        R singleExecute(P p);
    }
}
