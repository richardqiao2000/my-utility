package io.richardqiao.practice.reactive.java;

import io.richardqiao.practice.reactive.common.JobItem;
import io.richardqiao.practice.reactive.common.Stages;

// start from java 1
public class ReactiveLevel0 {
    public static void main(String[] args) throws InterruptedException {
        int[] nums = new int[10];
        Thread[] threads = new Thread[10];
        for(int i = 0; i < 10; i++) {
            int finalI = i;
            threads[i] = new Thread(() -> Stages.stage1(new JobItem(nums, finalI)));
        }
        for(Thread thread: threads){
            thread.start();
        }
        for(int i = 0; i < 10; i++){
            threads[i].join();
            int tmp = nums[i];
        }

        for(int i = 0; i < 10; i++) {
            int finalI = i;
            threads[i] = new Thread(() -> Stages.stage2(new JobItem(nums, finalI)));
        }
        for(Thread thread: threads){
            thread.start();
        }
        for(int i = 0; i < 10; i++){
            threads[i].join();
            int tmp = nums[i];
        }

        for(int i = 0; i < 10; i++) {
            int finalI = i;
            threads[i] = new Thread(() -> Stages.stage3(new JobItem(nums, finalI)));
        }
        for(Thread thread: threads){
            thread.start();
        }
        for(int i = 0; i < 10; i++){
            threads[i].join();
            int tmp = nums[i];
        }
    }
}
