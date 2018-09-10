package server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

/**
 * @author fengchu created on 2018/9/10-19:46
 */
public class ServerHeartbeatHandler extends ChannelInboundHandlerAdapter {

    /**
     * 心跳丢失计数器
     */
    private int counter;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        // 判断接收到的包类型
        if (msg instanceof TextWebSocketFrame) {
            TextWebSocketFrame in = (TextWebSocketFrame) msg;
            if (in.text().equals("HeartBeat")) {
                handleHeartbeat(ctx, msg);
            } else {
                ctx.fireChannelRead(msg);
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            // 空闲6s之后触发 (心跳包丢失)
            if (counter >= 3) {
                // 连续丢失3个心跳包 (断开连接)
                ctx.channel().close().sync();
                System.out.println("已与Client断开连接");
            } else {
                counter++;
                System.out.println("丢失了第 " + counter + " 个心跳包");
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }

    /**
     * 处理心跳包
     */
    private void handleHeartbeat(ChannelHandlerContext ctx, Object msg) {
        // 将心跳丢失计数器置为0
        counter = 0;
        System.out.println("收到心跳包");
        ReferenceCountUtil.release(msg);
    }
}
