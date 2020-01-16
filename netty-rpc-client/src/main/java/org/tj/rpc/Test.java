package org.tj.rpc;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Test {
	public static void main(String[] args) {
		Lock lock = new ReentrantLock();
		Condition con = lock.newCondition();
		lock.lock();
		//for (int y = 0; y < 50; y++) {
			Thread t = new Thread(new RunThread(con, lock));
			t.start();
			try {
				con.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			for (int i = 0; i < 100; i++) {
				System.out.println("主线程执行" + i);
				if (i == 50) {
					con.signalAll();
					try {
						con.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
	//	}
		lock.unlock();
	}
}

class RunThread implements Runnable {

	private Condition con;
	private Lock lock;

	public RunThread(Condition con, Lock lock) {
		super();
		this.con = con;
		this.lock = lock;
	}

	@Override
	public void run() {
		lock.lock();
		for (int i = 0; i < 10; i++) {
			System.out.println("子线程运行" + i);
		}
		con.signalAll();

		try {
			con.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// lock.lock();
		for (int i = 0; i < 10; i++) {
			System.out.println("子线程运行" + i);
		}
		con.signalAll();
		lock.unlock();
	}

}
