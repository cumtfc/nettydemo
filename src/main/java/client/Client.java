package client;

import decoder.Decoder;
import encoder.Encoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Scanner;

/**
 * @author fengchu created on 2018/9/5-20:20
 */
public class Client {

    public static void main(String[] args) throws Exception {

        String host = "127.0.0.1";
        int port = 8080;
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) {
                    ch.pipeline()
                        .addLast("decoder", new Decoder())
                        .addLast("encoder", new Encoder())
                        .addLast("handler", new ClientHandler());
                }
            });
            connect(host, port, b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    private static void connect(String host, int port, Bootstrap b) throws InterruptedException {
        try {
            // 启动客户端
            ChannelFuture f = b.connect(host, port).sync();

            try (Scanner in = new Scanner(System.in)) {
                while (in.hasNextLine()) {
                    String s = in.nextLine();
                    if (s.equals("exit")) {
                        f.channel().close();
                        break;
                    }
                    f.channel().writeAndFlush(s);
                }
            }

        f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
