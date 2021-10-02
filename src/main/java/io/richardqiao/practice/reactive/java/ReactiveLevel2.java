package io.richardqiao.practice.reactive.java;

import io.richardqiao.practice.reactive.common.JobItem;
import io.richardqiao.practice.reactive.common.Stages;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

// Starting from Java 7, ForJointPool is introduced
// ForJointPool understands thread dependencies, avoids changing threads,
// and preventing cache corruption as more as possible.
// worker-stealing algorithm
public class ReactiveLevel2 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService fjPool = ForkJoinPool.commonPool();
        int[] nums = new int[10];
        List<Future<Integer>> futures = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            int finalI = i;
            futures.add(fjPool.submit(() -> Stages.stage1(new JobItem(nums, finalI))));
        }
        for(Future<Integer> future: futures) {
            future.get();  // Blocking
        }

        futures.clear();
        for(int i = 0; i < 10; i++) {
            int finalI = i;
            futures.add(fjPool.submit(() -> Stages.stage2(new JobItem(nums, finalI))));
        }
        for(Future<Integer> future: futures) {
            future.get();  // Blocking
        }

        futures.clear();
        for(int i = 0; i < 10; i++) {
            int finalI = i;
            futures.add(fjPool.submit(() -> Stages.stage3(new JobItem(nums, finalI))));
        }
        for(Future<Integer> future: futures) {
            future.get();  // Blocking
        }

        fjPool.shutdown();
    }
}
