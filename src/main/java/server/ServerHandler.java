package server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fengchu created on 2018/9/5-11:46
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private static final List<ChannelHandlerContext> contexts = new ArrayList<>();

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        SocketAddress socketAddress = ctx.channel().remoteAddress();
        System.out.println("客户端接入：" + socketAddress);
        contexts.add(ctx);
        contexts.stream().parallel()
            .forEach(context -> context.writeAndFlush(context.channel().remoteAddress() + " 上线"));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        contexts.stream().parallel()
            .filter(context -> context != ctx)
            .forEach(context -> context.writeAndFlush(context.channel().remoteAddress() + ": " + msg));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        SocketAddress socketAddress = ctx.channel().remoteAddress();
        System.out.println("客户端断开：" + socketAddress);
        contexts.remove(ctx);
        contexts.stream().parallel()
            .forEach(context -> context.writeAndFlush(context.channel().remoteAddress() + " 下线"));
    }
}