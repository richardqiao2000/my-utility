package io.richardqiao.practice.reactive.java;

import io.richardqiao.practice.reactive.common.JobItem;
import io.richardqiao.practice.reactive.common.Stages;

import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.SubmissionPublisher;

// In Java 9, Flow is introduced
public class ReactiveLevel4 {

    public static void main(String[] args) throws InterruptedException {
        int[] nums = new int[10];
        JobSubscriber[] jobs = new JobSubscriber[10];
        for(int i = 0; i < 10; i++){
            jobs[i] = new JobSubscriber();
        }
        for(int i = 0; i < 10 ; i++) {
            SubmissionPublisher<JobItem> publisher = new SubmissionPublisher<>(ForkJoinPool.commonPool(), 2);
            publisher.subscribe(jobs[i]);    // Pipeline connected
            publisher.submit(new JobItem(nums, i));
            publisher.close();
        }
        for(int i = 0; i < 10; i++){
            while(!jobs[i].isCompleted){
                Thread.sleep(100);
            }
        }
    }

    static class JobSubscriber implements Subscriber<JobItem>{
        private Subscription sub;
        public boolean isCompleted = false;
        public void onSubscribe(final Subscription subscription) {
            sub = subscription;
            sub.request(1);
        }
        public void onNext(JobItem jt) {
            Stages.stage1(jt);
            Stages.stage2(jt);
            Stages.stage3(jt);
            sub.request(1);
        }
        public void onError(Throwable throwable){}
        public void onComplete() {
            isCompleted = true;
        }
    }
}
