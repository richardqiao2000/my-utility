package io.richardqiao.practice.pool;

import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.RootAllocator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingDeque;

/***
 * An Arrow Allocator Pool is used to provide reusable allocators for decoding arrow bas response during data fetching process.
 * <p>The pool is built with following input parameters:
 * <ul>
 * <li>pool capacity: the capacity of the pool
 * <li>each allocator size: The size is estimated to satisfy the maximum size of a single arrow record batch with 20_000 rows of data
 * </ul>
 * <p>
 *     The pool is working in a blocking way, which means when the pool is empty, a request to get the allocator will be blocked,
 * until other thread returns allocator to pool.
 * <p>
 *     When the pool is full, and a new element is to be added into the pool, a "Dequeue Full" exception will be thrown.
 * <p>
 *     Note: It's used only for data fetching and not for other purpose (async arrow response, bql arrow vector, etc)
 */
@Component
public class ArrowAllocatorPool {
    private static final Logger logger = LogManager.getLogger(ArrowAllocatorPool.class);
    private LinkedBlockingDeque<BufferAllocator> allocatorPool;
//    @Value("${bql.arrow.allocator.size:3000000}")
    private int arrowAllocatorSize = 3000000;
//    @Value("${bql.arrow.allocator.pool.size:120}")
    private int poolCapacity = 120;

    private static final String ERROR_MSG = "(BQL_ARROW_ERROR)Please contact G2672(arrow bas related developers) to solve it." +
            " The related FTAM is BQL_ENABLE_ARROW_DATA_RESPONSE. ";

    /**
     * To have an empty constructor to support spring framework better.
     */
    public ArrowAllocatorPool(){
        allocatorPool = new LinkedBlockingDeque<>(poolCapacity);
        while (allocatorPool.size() < poolCapacity) {
            allocatorPool.add(new RootAllocator(arrowAllocatorSize));
        }
        logger.info("Arrow allocator pool is initialized." +
                " poolCapacity=" + poolCapacity + " allocatorSize=" + arrowAllocatorSize);
    }

    /**
     * Get an arrow allocator from the pool
     * <p>
     * Throw InterruptedException if the current thread is interrupted, either because JVM down or running interrupt()
     * by another thread.
     *
     * @return A single arrow allocator
     */
    public BufferAllocator getAllocator() throws Exception {
        BufferAllocator allocator;
        try {
            allocator = allocatorPool.takeFirst();
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            loggingSizesWhenException();
            throw new Exception("Error when trying to get an allocator from the arrow allocator pool. "
                    + ERROR_MSG + ie);
        }
        return allocator;
    }

    /**
     * If an arrow alloator is finished usage of decoding arrow response, return the allocator to the pool.
     * <p>
     * There are 2 error handling:
     * <ul>
     * <li>1) allocator is null -- It should not happen, because during arrow decoding process, there is no way
     *    to set allocator null, which is proved by current impl without pool.
     * <li>2) pool is full when trying to return allocator -- This happens when an allocator is returned more than once,
     *    but this should not happen, because during process of getting allocator, takeFirst() method is thread safe
     *    and blocking.
     * </ul>
     * @param allocator arrow allocator after usage
     */
    public void returnAllocator(BufferAllocator allocator) throws Exception {
        if(allocator == null){
            loggingSizesWhenException();
            throw new Exception("Allocator should not be null when returning to pool. " + ERROR_MSG);
        }
        try {
            allocatorPool.addFirst(allocator);
        }catch(IllegalStateException ise){
            loggingSizesWhenException();
            throw new Exception("Pool is full when trying to return an allocator. " + ERROR_MSG + ise);
        }
    }

    /**
     * Get current usage amount of allocators
     * @return amount of allocators being used to decode
     */
    public int getNumUsedAllocators(){
        return poolCapacity - allocatorPool.size();
    }

    /***
     * Get current allocator amount in pool available to use
     * @return pool size
     */
    public int getNumAvailableAllocators(){
        return allocatorPool.size();
    }

    /**
     * Caution: The pool's life cycle is same as the JVM instance, in current impl it's not required to clear it.
     *          This method is used only for unit tests when switching to different test methods reconstructing the pool.
     */
    public void testClear(){
        for (BufferAllocator allocator : allocatorPool) {
            allocator.close();
        }
        allocatorPool.clear();
    }

    private void loggingSizesWhenException(){
        logger.error("Error when returning null allocator. allocatorInUse=" + getNumUsedAllocators() +
                " allocatorAvailable=" + getNumAvailableAllocators() +
                " poolCapacity=" + poolCapacity + " allocatorSize=" + arrowAllocatorSize);
    }
}
