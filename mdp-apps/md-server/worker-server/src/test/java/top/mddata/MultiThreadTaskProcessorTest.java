package top.mddata;

import org.junit.jupiter.api.Test;
import top.mddata.open.service.admin.processor.MultiThreadTaskProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 六如
 */
public class MultiThreadTaskProcessorTest {


    @Test
    public void testTask() {
        MultiThreadTaskProcessor processor = new MultiThreadTaskProcessor();

        // 准备数据：创建N*M个任务列表
        List<List<MultiThreadTaskProcessor.Task>> allTasks = new ArrayList<>();

        // 假设我们要创建 P 个list，每个list有 Q 个任务
        int totalLists = 12; // 总共12个list
        int tasksPerList = 5; // 每个list有5个任务
        int numberOfThreads = 2; // 使用3个线程

        // 初始化任务数据
        for (int i = 0; i < totalLists; i++) {
            List<MultiThreadTaskProcessor.Task> list = new ArrayList<>();
            for (int j = 0; j < tasksPerList; j++) {
                final int taskId = i * tasksPerList + j;
                list.add(() -> {
                    // 模拟任务执行
                    System.out.println("执行任务: " + taskId +
                                       " by thread: " + Thread.currentThread().getName());
                    try {
                        Thread.sleep(1100); // 模拟处理时间
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
            allTasks.add(list);
        }

        // 执行多线程处理
        processor.processTasksAsync(allTasks, numberOfThreads);
//        processor.shutdownGracefully();
        System.err.println("任务执行完成");


        try {
            Thread.sleep(11100); // 模拟处理时间
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


}
