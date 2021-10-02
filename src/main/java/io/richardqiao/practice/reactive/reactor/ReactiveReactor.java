package io.richardqiao.practice.reactive.reactor;

import io.richardqiao.practice.reactive.common.JobItem;
import io.richardqiao.practice.reactive.common.Stages;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ForkJoinPool;

public class ReactiveReactor {
    public static void main(String[] args) throws InterruptedException {
        run2();
    }

    private static void run1(){
        int[] nums = new int[10];
        Flux.range(0, 10)
                .map(i -> new JobItem(nums, i))
                .parallel()
                .runOn(Schedulers.fromExecutor(ForkJoinPool.commonPool()))
                .map(jt -> {
                    Stages.stage1(jt);
                    Stages.stage2(jt);
                    Stages.stage3(jt);
                    return jt;
                })
                .sequential()
                .collectList()
                .block()
        ;
    }

    private static void run2() throws InterruptedException {
        int[] nums = new int[10];
        Flux.range(0, 10)
                .map(i -> new JobItem(nums, i))
                .parallel(12)
                .runOn(Schedulers.newParallel("hi", 11))
                .subscribe(Stages::stage1)
                .dispose()
        ;
//        Thread.sleep(10000);
    }
}
