package org.tj.rpc;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

public class LogTest {

	public static CountDownLatch lat = new CountDownLatch(4);

	public static final BlockingQueue<String> log = new LinkedBlockingQueue<String>(16);

	static {
		for (int i = 0; i < 16; i++) {
			log.add("这是第" + (i + 1) + "个日志");
		}
	}

	public static void main(String[] args) {
		long time = System.currentTimeMillis();
		for (int i = 0; i < 4; i++) {
			new Thread(new Print()).start();
		}
		try {
			lat.await();
			System.out.println("一共耗时" + (System.currentTimeMillis() - time) / 1000 + "s");
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
	}

	public static void printLog() {
		try {
			synchronized (log) {
				System.out.println(log.take().toString());
			}
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}

class Print implements Runnable {

	@Override
	public void run() {
		for (int i = 0; i < 4; i++) {
			LogTest.printLog();
		}
		LogTest.lat.countDown();
	}

}