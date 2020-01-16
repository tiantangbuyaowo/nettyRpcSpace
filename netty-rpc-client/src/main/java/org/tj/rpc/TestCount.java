package org.tj.rpc;

import java.util.ArrayList;
import java.util.List;

public class TestCount {

	public static void main(String[] args) {
		List<Integer> data = new ArrayList<Integer>();
		for (int i = 0; i < 10000000; i++) {
			data.add(i + 1);
		}
		CountObj obj = new CountObj(1, data);
		while (obj.list.size() > 1) {
			obj = exeCount(obj);

		}
		System.out.println("最后剩下的是" + obj.list.get(0));

	}

	public static CountObj exeCount(CountObj obj) {
		List<Integer> data = new ArrayList<Integer>();
		for (int i = obj.start; i < obj.list.size() + obj.start; i++) {
			if (i % 3 != 0) {
				data.add(obj.list.get(i - obj.start));
			}
		}
		obj = new CountObj(((obj.list.size() + obj.start) % 3), data);

		return obj;

	}

}

class CountObj {

	public CountObj(int start, List<Integer> list) {
		super();
		this.start = start;
		this.list = list;
	}

	int start;
	List<Integer> list = new ArrayList<Integer>();
}