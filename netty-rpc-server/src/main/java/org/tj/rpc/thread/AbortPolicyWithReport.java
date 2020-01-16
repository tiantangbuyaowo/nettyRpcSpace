package org.tj.rpc.thread;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ClassName: AbortPolicyWithReport
 * @Description: 线程池异常策略
 * @author: 唐靖
 * @date: 2017年3月10日 下午2:33:25
 */
public class AbortPolicyWithReport implements RejectedExecutionHandler {
	private final String threadName;

	public AbortPolicyWithReport(String threadName) {
		this.threadName = threadName;
	}
	public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
		String msg = String.format(
				"RpcServer["
						+ " Thread Name: %s, Pool Size: %d (active: %d, core: %d, max: %d, largest: %d), Task: %d (completed: %d),"
						+ " Executor status:(isShutdown:%s, isTerminated:%s, isTerminating:%s)]",
				threadName, e.getPoolSize(), e.getActiveCount(), e.getCorePoolSize(), e.getMaximumPoolSize(),
				e.getLargestPoolSize(), e.getTaskCount(), e.getCompletedTaskCount(), e.isShutdown(), e.isTerminated(),
				e.isTerminating());
		System.out.println(msg);
		throw new RejectedExecutionException(msg);
	}
}
