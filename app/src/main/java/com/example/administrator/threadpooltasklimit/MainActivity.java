package com.example.administrator.threadpooltasklimit;

import android.os.Parcel;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.concurrent.Future;

/**
 * 说明了 一个任务只会分配一个线程去执行，不会多个线程一起执行同一个任务
 */
public class MainActivity extends AppCompatActivity {

    private Task3 task3;
    private Task1 task1;
    private Task2 task2;
    private Task4 task4;
    private Task5 task5;
    private ThreadPoolProxy normalThreadPoolProxy;
    private Future<?> future;
    public boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        task1 = new Task1();
        normalThreadPoolProxy = ThreadPoolProxyFactory.createNormalThreadPoolProxy();
        future = normalThreadPoolProxy.submit(task1);

        task2 = new Task2();
        normalThreadPoolProxy.submit(task2);

        task3 = new Task3();
        normalThreadPoolProxy.submit(task3);

        task4 = new Task4();
        normalThreadPoolProxy.submit(task4);

        task5 = new Task5();
        normalThreadPoolProxy.submit(task5);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        移除任务
//        ThreadPoolProxyFactory.createNormalThreadPoolProxy().remove(task1);
    }

    // 任务为什么会有停顿的时间，log不是连续一直打印的
    // 为何这个任务不能被取消？？？死循环不能被取消，虽然提示取消成功了，然而，不是死循环也不能取消，这是一个bug？？？？
    class Task1 implements Runnable{
        @Override
        public void run() {
            while(flag) {
                Log.e("haha", "flag== " + flag);
                    Log.e("Task1", "Task1== ");
                    SystemClock.sleep(1000);// 睡眠1秒

                    // 用这种方式中断，也不能被取消！！！！！！！！！！！
               /* try {
                    Thread.sleep(1000);// 睡眠1秒
                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();//
                    e.printStackTrace();
                }*/
            }
            // 置空任务，让gc可以回收，避免内存泄漏
            task1 = null;
        }
    }

    // 让这个执行任务的线程 sleep ，同样他也是占据了这个线程
    class Task2 implements Runnable{
        @Override
        public void run() {
            while(true){
                Log.e("Task2", "Task2== ");
                SystemClock.sleep(1000*60*60);// 睡眠一个小时
            }
        }
    }

    // 如果这个是可以执行完成的任务，后面的任务会得到执行的机会
    class Task3 implements Runnable{
        @Override
        public void run() {
            while(true){
                Log.e("Task3", "Task3== ");
                SystemClock.sleep(1000 * 10 );// 睡眠10秒

                // run 方法后面不将任务置为空的情况，任务执行完成了，后面的任务得到执行的机会

                // run 方法后面将任务置为空的情况,任务执行完成了，后面的任务得到执行的机会
                task3 = null;

                // Task3 执行完之后 把任务1 从线程池中移除，结果发现 task1 还在执行，那么，怎么让 task1 停止对线程的占用呢？？
//                ThreadPoolProxyFactory.createNormalThreadPoolProxy().remove(task1);

//                normalThreadPoolProxy.remove(task1);// 这个只是从任务队列移除了，当执行了，就没有办法了

//                future.cancel(false);// 关闭任务的正确姿势？
                task1 = null;
                future.cancel(true);// 关闭任务的正确姿势？
                Log.e("haha", "Happy== " + future.isCancelled());// 确实取消了，但是，log还在打印，这个取消的意思是？？or could not be cancelled for some other reason.原因到底是什么？
                Log.e("haha", "Happy== " + future.isDone());// 确实取消了，但是，log还在打印，这个取消的意思是？？or could not be cancelled for some other reason.原因到底是什么？

                flag = false;// 只能使用这种原始的方式 &&&&&&&&&&&&&&&&&&&&&&&&&&&尼玛真是坑爹啊！！！！！！！！1&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
                Log.e("haha", "Task3 flag== " + flag);

                break;// 退出
            }

        }
    }

    // 只开三个工作线程 上面三个任务都是死循环，任务不可能执行完，导致 Task4 一直在任务队列等待，不能获得执行的机会
    //
    class Task4 implements Runnable{
        @Override
        public void run() {
            while(true){
                Log.e("Task4", "Task4== ");
                SystemClock.sleep(1000 * 10);// 睡眠10秒

                break;
            }
        }
    }

    // Task5 有空闲线程的时候会执行
    class Task5 implements Runnable{
        @Override
        public void run() {
            Log.e("Task5", "Task5== ");
        }
    }

}
