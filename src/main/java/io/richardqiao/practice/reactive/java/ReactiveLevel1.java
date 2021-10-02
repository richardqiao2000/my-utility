package io.richardqiao.practice.reactive.java;


import io.richardqiao.practice.reactive.common.JobItem;
import io.richardqiao.practice.reactive.common.Stages;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

// Starting from java 5
// ExcutorService/Callable/Runnalbe/Future is introduced
// But it needs to block the thread.
// We need to avoid blocking threads in next Level.
public class ReactiveLevel1 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(10);
        int[] nums = new int[10];
        List<Future<Integer>> futures = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            int finalI = i;
            futures.add(es.submit(() -> Stages.stage1(new JobItem(nums, finalI))));
        }
        for(Future<Integer> future: futures) {
            future.get();  // Blocking
        }

        futures.clear();
        for(int i = 0; i < 10; i++) {
            int finalI = i;
            futures.add(es.submit(() -> Stages.stage2(new JobItem(nums, finalI))));
        }
        for(Future<Integer> future: futures) {
            future.get();  // Blocking
        }

        futures.clear();
        for(int i = 0; i < 10; i++) {
            int finalI = i;
            futures.add(es.submit(() -> Stages.stage3(new JobItem(nums, finalI))));
        }
        for(Future<Integer> future: futures) {
            future.get();  // Blocking
        }

        es.shutdown();
    }
}
