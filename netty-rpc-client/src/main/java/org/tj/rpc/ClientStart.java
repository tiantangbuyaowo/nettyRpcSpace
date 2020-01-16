package org.tj.rpc;

import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang3.time.StopWatch;
import org.tj.rpc.core.ClientMessageSendExecutor;
import org.tj.rpc.thread.CalcParallelRequestThread;

public class ClientStart {
	public static void main(String[] args) throws InterruptedException {
		final ClientMessageSendExecutor executor = new ClientMessageSendExecutor("127.0.0.1:18888");
		// 并行度10000
		int parallel = 10000;

		// 开始计时
		StopWatch sw = new StopWatch();
		sw.start();

		CountDownLatch signal = new CountDownLatch(1);
		CountDownLatch finish = new CountDownLatch(parallel);

		for (int index = 0; index < parallel; index++) {
			CalcParallelRequestThread client = new CalcParallelRequestThread(executor, signal, finish, index);
			new Thread(client).start();
		}

		// 10个并发线程瞬间发起请求操作
		signal.countDown();
		finish.await();

		sw.stop();

		String tip = String.format("RPC调用总共耗时: [%s] 秒", sw.getTime()/1000);
		System.out.println(tip);

		executor.stop();
	}
}
