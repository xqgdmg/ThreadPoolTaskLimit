package com.example.administrator.threadpooltasklimit;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 创建者     Chris
 * 创建时间   2016/7/8 09:45
 * 描述	      是线程池的代理类
 * 描述	      替线程池进行一些操作-->帮助ThreadPoolExecutor初始化
 * 描述	      会对线程池常见的操作进行暴露(提交任务,执行任务,移除任务)
 *
 */
public class ThreadPoolProxy {
    ThreadPoolExecutor mExecutor;//核心池大小
    private int mCorePoolSize;//线程池最大线程数
    private int mMaximumPoolSize;

    /**
     * 通过构造方法,传递创建线程池需要的两个核心参数
     *
     * @param corePoolSize
     * @param maximumPoolSize
     */
    public ThreadPoolProxy(int corePoolSize, int maximumPoolSize) {
        mCorePoolSize = corePoolSize;
        mMaximumPoolSize = maximumPoolSize;

        // 在构造方法中初始化，不然不能取消任务
        initThreadPoolExecutor();
    }

    /**
     * 创建线程池
     */
    private void initThreadPoolExecutor() {
        //双重检查加锁,只有在第一次实例化的时候才启用同步机制,提高了性能

        if (mExecutor == null || mExecutor.isShutdown() || mExecutor.isTerminated()) {
            synchronized (ThreadPoolProxy.class) {
                if (mExecutor == null || mExecutor.isShutdown() || mExecutor.isTerminated()) {
                    long keepAliveTime = 0;//保持时间
                    TimeUnit unit = TimeUnit.MILLISECONDS;//保持时间单位
                    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();//任务队列
                    ThreadFactory threadFactory = Executors.defaultThreadFactory();//线程工厂
                    RejectedExecutionHandler handler = new ThreadPoolExecutor.DiscardPolicy();//异常捕获器

                    mExecutor = new ThreadPoolExecutor(mCorePoolSize, mMaximumPoolSize, keepAliveTime,
                            unit, workQueue, threadFactory, handler);
                }
            }
        }
    }

    /*
    提交任务和执行任务的区别?
        是否有返回值
            submit-->有-->Future
            execute-->没有

    提交任务返回回来的Future对象是啥?
        1.接收任务执行之后的结果,还能检测任务是否执行完成,会等待任务完成,直到接收到结果
        2.我们可以通过get方法接收结果,如果任务执行完成了,get方法如果有必要的话会阻塞等待结果的完成
        3.cancel方法可以取消任务的执行
     */

    /**
     * 提交任务
     */
    public Future<?> submit(Runnable task) {
//        initThreadPoolExecutor();
        Future<?> result = mExecutor.submit(task);
        return result;
    }


    /**
     * 执行任务
     */
    public void execute(Runnable task) {
//        initThreadPoolExecutor();
        mExecutor.execute(task);
    }

    /**
     * 移除任务
     */
    public void remove(Runnable task) {
//        initThreadPoolExecutor();
        mExecutor.remove(task);
    }
}
