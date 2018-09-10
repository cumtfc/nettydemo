package server;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.net.SocketAddress;

/**
 * @author fengchu created on 2018/9/5-11:46
 */
public class ServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame text) throws Exception {
        channels.stream().parallel()
            .filter(context -> context != ctx)
            .forEach(channel -> channel.writeAndFlush(text.copy()));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        SocketAddress socketAddress = ctx.channel().remoteAddress();
        System.out.println("客户端接入：" + socketAddress);
        channels.add(ctx.channel());
        channels.stream().parallel()
            .forEach(channel -> channel.writeAndFlush(new TextWebSocketFrame(channel.remoteAddress() + " 上线")));
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        SocketAddress socketAddress = ctx.channel().remoteAddress();
        System.out.println("客户端断开：" + socketAddress);
        channels.remove(ctx);
        channels.stream().parallel()
            .forEach(channel -> channel.writeAndFlush(new TextWebSocketFrame(channel.remoteAddress() + " 下线")));
    }

}