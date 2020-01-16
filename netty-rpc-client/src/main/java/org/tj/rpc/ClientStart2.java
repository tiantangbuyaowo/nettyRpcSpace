package org.tj.rpc;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang3.time.StopWatch;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 网络爬虫测试
 * @author Administrator
 *
 */
public class ClientStart2 {
	public static void main(String[] args) throws InterruptedException {

		// 并行度10000
		int parallel = 10000;

		// 开始计时
		StopWatch sw = new StopWatch();
		sw.start();

		CountDownLatch signal = new CountDownLatch(1);
		CountDownLatch finish = new CountDownLatch(parallel);

		for (int index = 0; index < parallel; index++) {
			new Thread(new MyThread(signal, finish)).start();
		}

		// 10个并发线程瞬间发起请求操作
		signal.countDown();
		finish.await();
		sw.stop();
		String tip = String.format("5W并发总共耗时: [%s] 秒", sw.getTime() / 1000);
		System.out.println(tip);

	}
}

class MyThread implements Runnable {
	CountDownLatch signal;
	CountDownLatch finish;

	public MyThread(CountDownLatch signal, CountDownLatch finish) {
		super();
		this.signal = signal;
		this.finish = finish;
	}

	@Override
	public void run() {
		String url = "https://www.bestbeijia.com/";
		OkHttpClient okHttpClient = new OkHttpClient();
		Request request = new Request.Builder().url(url).build();
		Call call = okHttpClient.newCall(request);
		try {
			try {
				signal.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Response response = call.execute();
			System.out.println(response.body().string());
			finish.countDown();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}