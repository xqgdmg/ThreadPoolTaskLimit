package com.example.administrator.threadpooltasklimit;

/**
 * 创建者     Chris
 * 创建时间   2016/7/8 10:09
 * 描述	      创建两种类型的线程池代理(普通线程池代理,下载线程池代理)
 *
 */
public class ThreadPoolProxyFactory {
    static ThreadPoolProxy mNormalThreadPoolProxy;
    static ThreadPoolProxy mDownloadThreadPoolProxy;

    /**
     * 创建一个普通线程池的代理对象
     */
    public static ThreadPoolProxy createNormalThreadPoolProxy() {
        if (mNormalThreadPoolProxy == null) {
            synchronized (ThreadPoolProxyFactory.class) {
                if (mNormalThreadPoolProxy == null) {
                    mNormalThreadPoolProxy = new ThreadPoolProxy(3, 3);
                }
            }
        }
        return mNormalThreadPoolProxy;
    }

    /**
     * 创建一个下载线程池的代理对象
     */
    public static ThreadPoolProxy createDownloadThreadPoolProxy() {
        if (mDownloadThreadPoolProxy == null) {
            synchronized (ThreadPoolProxyFactory.class) {
                if (mDownloadThreadPoolProxy == null) {
                    mDownloadThreadPoolProxy = new ThreadPoolProxy(3, 3);
                }
            }
        }
        return mDownloadThreadPoolProxy;
    }

}
