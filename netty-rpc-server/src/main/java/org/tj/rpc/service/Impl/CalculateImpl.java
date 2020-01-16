/**
 * @filename:CalculateImpl.java
 *
 * Newland Co. Ltd. All rights reserved.
 *
 * @Description:计算器定义接口实现
 * @author tangjie
 * @version 1.0
 *
 */
package org.tj.rpc.service.Impl;

import org.tj.rpc.annotation.RpcService;
import org.tj.rpc.service.Calculate;

@RpcService(value = Calculate.class)
public class CalculateImpl implements Calculate {
	// 两数相加
	public int add(int a, int b) {
		return a + b;
	}

	@Override
	public int dev(int a, int b) {
		System.out.println("a=="+a+"b=="+b);
		System.out.println(a/b);
		return a/b;
	}
	
	
}
