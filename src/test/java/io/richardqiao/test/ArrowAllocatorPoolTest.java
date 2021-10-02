package io.richardqiao.test;

import io.richardqiao.practice.pool.ArrowAllocatorPool;
import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.RootAllocator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class ArrowAllocatorPoolTest {
    private static final Logger logger = LoggerFactory.getLogger(ArrowAllocatorPoolTest.class);
    private static ArrowAllocatorPool arrowAllocatorPool;
    @BeforeClass
    public static void setUp() throws NoSuchFieldException, IllegalAccessException {
        arrowAllocatorPool = new ArrowAllocatorPool();
        Field field = arrowAllocatorPool.getClass().getDeclaredField("poolCapacity");
        field.setAccessible(true);
        field.set(arrowAllocatorPool, 30);
        field = arrowAllocatorPool.getClass().getDeclaredField("arrowAllocatorSize");
        field.setAccessible(true);
        field.set(arrowAllocatorPool, 1_000_000);
    }

    /**
     * Test adding allocator to a full queue
     */
    @Test
    public void testAdddingToFull() throws Exception {
        int poolCapacity = 3;
        Field field = arrowAllocatorPool.getClass().getDeclaredField("poolCapacity");
        field.setAccessible(true);
        field.set(arrowAllocatorPool, poolCapacity);
        BufferAllocator bufferAllocator1 = arrowAllocatorPool.getAllocator();
        arrowAllocatorPool.returnAllocator(bufferAllocator1);
        try {
            BufferAllocator bufferAllocator2 = new RootAllocator(20);
            arrowAllocatorPool.returnAllocator(bufferAllocator2);
        }catch(Exception e){
            Assert.assertTrue(e.getMessage().startsWith("Pool is full when trying to return an allocator."));
        }
        arrowAllocatorPool.testClear();
    }

    /**
     * Test getting allocator from an empty pool
     */
    @Test
    public void testGettingFromEmpty() throws Exception {
        int poolCapacity = 2;
        Field field = arrowAllocatorPool.getClass().getDeclaredField("poolCapacity");
        field.setAccessible(true);
        field.set(arrowAllocatorPool, poolCapacity);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<BufferAllocator> fAllocator1 = executor.submit(() -> arrowAllocatorPool.getAllocator());
        Future<BufferAllocator> fAllocator2 = executor.submit(() -> arrowAllocatorPool.getAllocator());
        Future<BufferAllocator> fAllocator3 = executor.submit(() -> arrowAllocatorPool.getAllocator());
        while(!fAllocator1.isDone());
        while(!fAllocator2.isDone());
        Assert.assertTrue(fAllocator1.isDone());
        Assert.assertTrue(fAllocator2.isDone());
        // the 3rd pulling is still blocked.
        Assert.assertFalse(fAllocator3.isDone());
        // after returning an allocator, the 3rd pulling can get allocator
        arrowAllocatorPool.returnAllocator(fAllocator1.get());
        while(!fAllocator3.isDone());
        Assert.assertTrue(fAllocator3.isDone());
        arrowAllocatorPool.returnAllocator(fAllocator2.get());
        arrowAllocatorPool.returnAllocator(fAllocator3.get());
        arrowAllocatorPool.testClear();
    }

    /**
     * Test pool size greater than concurrency
     */
    @Test
    public void testpoolCapacity1Quick()
            throws ExecutionException, InterruptedException, NoSuchFieldException, IllegalAccessException {
        int tasks = 10;
        int concurrency = 3;
        int poolCapacity = 5;
        int runningMaxTimeMS = 1_000;

        Field field = arrowAllocatorPool.getClass().getDeclaredField("poolCapacity");
        field.setAccessible(true);
        field.set(arrowAllocatorPool, poolCapacity);

        Assert.assertEquals(arrowAllocatorPool.getNumAvailableAllocators(), poolCapacity);
        List<Future<Long>> results = new ArrayList<>();

        ExecutorService threadPool = Executors.newFixedThreadPool(concurrency);
        for(int i = 0; i < tasks; i++){
            AllocatorTask allocatorTask = new AllocatorTask(
                    i, new Random().nextInt(runningMaxTimeMS), arrowAllocatorPool);
            results.add(threadPool.submit(allocatorTask));
        }
        for(int i = 0; i < tasks; i++){
            logger.info("Finished task id=" + i + " timeMS=" + results.get(i).get());
        }
        logger.info("Allocator pool size=" + arrowAllocatorPool.getNumAvailableAllocators());
        logger.info("Total finished tasks=" + results.size());
        Assert.assertEquals(results.size(), tasks);
        Assert.assertEquals(arrowAllocatorPool.getNumAvailableAllocators(), poolCapacity);
        arrowAllocatorPool.testClear();
    }

    /**
     * Test pool size greater than concurrency
     */
    @Ignore
    @Test
    public void testpoolCapacity1()
            throws ExecutionException, InterruptedException, NoSuchFieldException, IllegalAccessException {
        int tasks = 50;
        int concurrency = 20;
        int poolCapacity = 30;
        int runningMaxTimeMS = 3_000;

        Field field = arrowAllocatorPool.getClass().getDeclaredField("poolCapacity");
        field.setAccessible(true);
        field.set(arrowAllocatorPool, poolCapacity);

        Assert.assertEquals(arrowAllocatorPool.getNumAvailableAllocators(), poolCapacity);
        List<Future<Long>> results = new ArrayList<>();

        ExecutorService threadPool = Executors.newFixedThreadPool(concurrency);
        for(int i = 0; i < tasks; i++){
            AllocatorTask allocatorTask = new AllocatorTask(
                    i, new Random().nextInt(runningMaxTimeMS), arrowAllocatorPool);
            results.add(threadPool.submit(allocatorTask));
        }
        for(int i = 0; i < tasks; i++){
            logger.info("Finished task id=" + i + " timeMS=" + results.get(i).get());
        }
        logger.info("Allocator pool size=" + arrowAllocatorPool.getNumAvailableAllocators());
        logger.info("Total finished tasks=" + results.size());
        Assert.assertEquals(results.size(), tasks);
        Assert.assertEquals(arrowAllocatorPool.getNumAvailableAllocators(), poolCapacity);
        arrowAllocatorPool.testClear();
    }

    /**
     * Test pool size less than concurrency
     */
    @Ignore
    @Test
    public void testpoolCapacity2()
            throws ExecutionException, InterruptedException, NoSuchFieldException, IllegalAccessException {
        int tasks = 100;
        int concurrency = 70;
        int poolCapacity = 30;
        int runningMaxTimeMS = 30_000;

        Field field = arrowAllocatorPool.getClass().getDeclaredField("poolCapacity");
        field.setAccessible(true);
        field.set(arrowAllocatorPool, poolCapacity);

        Assert.assertEquals(arrowAllocatorPool.getNumAvailableAllocators(), poolCapacity);
        List<Future<Long>> results = new ArrayList<>();

        ExecutorService threadPool = Executors.newFixedThreadPool(concurrency);
        for(int i = 0; i < tasks; i++){
            AllocatorTask allocatorTask = new AllocatorTask(
                    i, new Random().nextInt(runningMaxTimeMS), arrowAllocatorPool);
            results.add(threadPool.submit(allocatorTask));
        }
        for(int i = 0; i < tasks; i++){
            logger.info("Finished task id=" + i + " timeMS=" + results.get(i).get());
        }
        logger.info("Allocator pool size=" + arrowAllocatorPool.getNumAvailableAllocators());
        logger.info("Total finished tasks=" + results.size());
        Assert.assertEquals(results.size(), tasks);
        Assert.assertEquals(arrowAllocatorPool.getNumAvailableAllocators(), poolCapacity);
        arrowAllocatorPool.testClear();
    }

    /**
     * Test reactive concurrency
     * In this test case, it's verifying 10 concurrent tasks (in total 50) to get allocator from pool (size 3),
     * and running a random time period then return the allocator. The purpose is to make sure a small pool can
     * work in a much higher concurrency scenario, and won't cause deadlock, no allocator is lost, and not throw
     * any unexpected exceptions.
     *
     * Using Reactor (.parallel(concurrency).runOn(Schedulers.boundedElastic())) is a streaming way
     * to start multi threads which makes code conciser.
     */
    @Ignore
    @Test
    public void testReactiveConcurrency()
            throws IllegalAccessException, NoSuchFieldException {
        int tasks = 50;
        int concurrency = 10;
        int poolCapacity = 3;
        int runningMaxTimeMS = 5_000;
        Field field = arrowAllocatorPool.getClass().getDeclaredField("poolCapacity");
        field.setAccessible(true);
        field.set(arrowAllocatorPool, poolCapacity);

        Assert.assertEquals(arrowAllocatorPool.getNumAvailableAllocators(), poolCapacity);
        List<Long> results = Flux.range(0, tasks)
                .parallel(concurrency)
                .runOn(Schedulers.boundedElastic())
                .map(i -> {
                    Assert.assertTrue(arrowAllocatorPool.getNumAvailableAllocators() <= poolCapacity);
                    Long res = null;
                    try {
                        res = new AllocatorTask(i, new Random().nextInt(runningMaxTimeMS), arrowAllocatorPool).call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Assert.assertTrue(arrowAllocatorPool.getNumAvailableAllocators() <= poolCapacity);
                    return res;
                })
                .sequential()
                .collectList()
                .block()
                ;
        Assert.assertNotNull(results);
        logger.info("Allocator pool size=" + arrowAllocatorPool.getNumAvailableAllocators());
        logger.info("Total finished tasks=" + results.size());
        Assert.assertEquals(results.size(), tasks);
        Assert.assertEquals(arrowAllocatorPool.getNumAvailableAllocators(), poolCapacity);
        arrowAllocatorPool.testClear();
    }

    private static class AllocatorTask implements Callable<Long> {
        private final int id;
        private final long allocatorUsingTimeMS;
        private final ArrowAllocatorPool pool;
        public AllocatorTask(int id, long ms, ArrowAllocatorPool pool){
            this.id = id;
            this.allocatorUsingTimeMS = ms;
            this.pool = pool;
        }
        @Override
        public Long call() throws Exception {
            // Get allocator
            logger.info("Before fetching -- Allocator pool size=" + pool.getNumAvailableAllocators());
            final BufferAllocator allocator = pool.getAllocator();
            // using allocator
            try {
                logger.info("Running task id=" + id + ", timeMS=" + allocatorUsingTimeMS);
                Thread.sleep(allocatorUsingTimeMS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Return allocator
            pool.returnAllocator(allocator);
            logger.info("After returning -- Allocator pool size=" + pool.getNumAvailableAllocators());
            return allocatorUsingTimeMS;
        }
    }
}