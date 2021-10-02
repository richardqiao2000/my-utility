package io.richardqiao.practice.reactive.java;

import io.richardqiao.practice.reactive.common.JobItem;
import io.richardqiao.practice.reactive.common.Stages;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

// Starting from Java 8, CompletableFuture is introduced.
public class ReactiveLevel3 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        int[] nums = new int[10];
        List<CompletableFuture<Integer>> completableFutures = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            int finalI = i;
            completableFutures.add(CompletableFuture
                    .supplyAsync(() -> Stages.stage1(new JobItem(nums, finalI)))
                    .thenApplyAsync(x -> Stages.stage2(new JobItem(nums, finalI)))
                    .thenApplyAsync(x -> Stages.stage3(new JobItem(nums, finalI))));
        }
        for(CompletableFuture<Integer> completableFuture: completableFutures)
            completableFuture.get();
    }

}
