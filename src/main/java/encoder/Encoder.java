package encoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import java.nio.charset.Charset;

/**
 * @author fengchu created on 2018/9/6-18:06
 */
public class Encoder extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        String s = (String) msg;
        byte[] bytes = s.getBytes(Charset.forName("utf-8"));
        final ByteBuf buf = ctx.alloc().buffer(bytes.length);
        buf.writeBytes(bytes);
        ctx.writeAndFlush(buf, promise);
    }
}
