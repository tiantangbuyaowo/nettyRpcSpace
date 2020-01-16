package org.tj.rpc.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程工厂
 * @ClassName: NamedThreadFactory
 * @Description: TODO
 * @author: 唐靖
 * @date: 2017年3月10日 下午2:19:08
 */
public class NamedThreadFactory implements ThreadFactory {
	/**
	 * 一个并发操作的int，提供原子性操作，current包提供的类
	 */
	private static final AtomicInteger threadNumber = new AtomicInteger(1);
	/**
	 * 线程数量
	 */
	private final AtomicInteger mThreadNum = new AtomicInteger(1);
	private final String prefix;
	private final boolean daemoThread;
	private final ThreadGroup threadGroup;

	public NamedThreadFactory() {
		this("rpcserver-threadpool-" + threadNumber.getAndIncrement(), false);
	}
	public NamedThreadFactory(String prefix) {
		this(prefix, false);
	}
	public NamedThreadFactory(String prefix, boolean daemo) {
		this.prefix = prefix + "-thread-";
		daemoThread = daemo;
		SecurityManager s = System.getSecurityManager();
		threadGroup = (s == null) ? Thread.currentThread().getThreadGroup() : s.getThreadGroup();
	}
	@Override
	public Thread newThread(Runnable runnable) {
		String name = prefix + mThreadNum.getAndIncrement();
		Thread ret = new Thread(threadGroup, runnable, name, 0);
		ret.setDaemon(daemoThread);
		return ret;
	}
	public ThreadGroup getThreadGroup() {
		return threadGroup;
	}
}
