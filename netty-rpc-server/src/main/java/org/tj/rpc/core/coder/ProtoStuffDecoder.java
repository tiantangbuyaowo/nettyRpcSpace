package org.tj.rpc.core.coder;

import java.util.List;

import org.tj.rpc.util.SerializationUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

/**
 * @ClassName: ProtoStuffDecoder
 * @Description: 解码器
 * @author: 唐靖
 * @date: 2017年3月10日 下午3:19:31
 */
public class ProtoStuffDecoder extends MessageToMessageDecoder<ByteBuf> {
	private Class<?> clazz;

	public ProtoStuffDecoder(Class<?> clazz) {
		super();
		this.clazz = clazz;
	}
	@Override
	protected void decode(ChannelHandlerContext arg0, ByteBuf arg1, List<Object> arg2) throws Exception {
		int length = arg1.readableBytes(); // 读取byte长度
		byte[] data = new byte[length];
		arg1.getBytes(arg1.readerIndex(), data, 0, length);
		arg2.add(SerializationUtils.deserializer(data, clazz));
	}
}
