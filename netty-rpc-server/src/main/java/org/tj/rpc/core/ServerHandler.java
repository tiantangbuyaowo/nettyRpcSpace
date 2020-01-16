package org.tj.rpc.core;

import java.util.Map;

import org.tj.rpc.model.MessageRequest;
import org.tj.rpc.model.MessageResponse;
import org.tj.rpc.thread.MessageRecvInitializeTask;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * @ClassName: ServerHandler
 * @Description: 服务器处理程序
 * @author: 唐靖
 * @date: 2017年3月10日 下午3:17:23
 */
public class ServerHandler extends ChannelHandlerAdapter {
	private final Map<String, Object> handlerMap;

	public ServerHandler(Map<String, Object> handlerMap) {
		this.handlerMap = handlerMap;
	}
	/**
	 * 读取
	 * @author 唐靖
	 * @date 2017年2月6日下午9:48:59
	 * @param ctx
	 * @param msg
	 * @throws Exception
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		MessageRequest request = (MessageRequest) msg;
		MessageResponse response = new MessageResponse();
		MessageRecvInitializeTask recvTask = new MessageRecvInitializeTask(request, response, handlerMap, ctx);
		// 不要阻塞nio线程，复杂的业务逻辑丢给专门的线程池
		MessageRecvExecutor.submit(recvTask);
	}
	/**
	 * 读完
	 * @author 唐靖
	 * @date 2017年2月6日下午9:49:12
	 * @param ctx
	 * @throws Exception
	 */
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		super.channelReadComplete(ctx);
		ctx.flush();
	}
	/**
	 * 出异常
	 * @author 唐靖
	 * @date 2017年2月6日下午9:49:24
	 * @param ctx
	 * @param cause
	 * @throws Exception
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// TODO Auto-generated method stub
		super.exceptionCaught(ctx, cause);
		ctx.close();
	}
}
