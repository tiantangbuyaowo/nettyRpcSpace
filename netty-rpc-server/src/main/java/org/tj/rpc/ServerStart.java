package org.tj.rpc;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 启动服务
 * @ClassName: ServerStart
 * @Description: TODO
 * @author: 唐靖
 * @date: 2017年3月10日 下午5:17:46
 */
public class ServerStart {
	public static void main(String[] args) {
		new ClassPathXmlApplicationContext("classpath:rpc-invoke-config.xml");
	}
}
