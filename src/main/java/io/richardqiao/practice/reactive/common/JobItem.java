package io.richardqiao.practice.reactive.common;

public class JobItem {
    public int[] nums;
    public int index;
    public JobItem(int[] nums, int i){
        this.nums = nums;
        index = i;
    }

    public JobItem callStage1(){
        Stages.stage1(this);
        return this;
    }

    public JobItem callStage2(){
        Stages.stage2(this);
        return this;
    }

    public JobItem callStage3(){
        Stages.stage3(this);
        return this;
    }

}
