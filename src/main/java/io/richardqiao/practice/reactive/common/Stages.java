package io.richardqiao.practice.reactive.common;

import java.util.Random;

public class Stages {

    public static int stage1(JobItem jobItem) {
        try {
            Thread.sleep(new Random().nextInt(2000));
        }catch(InterruptedException ie){
            ie.printStackTrace();
        }
        System.out.println("Stage=1, Job=" + jobItem.index);
        jobItem.nums[jobItem.index] = new Random().nextInt(10000);
        return jobItem.nums[jobItem.index];
    }

    public static int stage2(JobItem jobItem) {
        try {
            Thread.sleep(new Random().nextInt(2000));
        }catch(InterruptedException ie){
            ie.printStackTrace();
        }
        System.out.println("Stage=2, Job=" + jobItem.index);
        jobItem.nums[jobItem.index] *= jobItem.nums[jobItem.index];
        return jobItem.nums[jobItem.index];
    }

    public static int stage3(JobItem jobItem) {
        try {
            Thread.sleep(new Random().nextInt(2000));
        }catch(InterruptedException ie){
            ie.printStackTrace();
        }
        System.out.println("Stage=3, Job=" + jobItem.index + ": " + jobItem.nums[jobItem.index]);
        return jobItem.nums[jobItem.index];
    }
}
