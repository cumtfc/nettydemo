package server;

import decoder.Decoder;
import encoder.Encoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * 丢弃任何进入的数据
 *
 * @author fengchu
 */
public class Server {

    private int port;

    public Server(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }
        new Server(port).run();
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
//                            .addLast("decoder", new Decoder())
//                            .addLast("encoder", new Encoder())
                            .addLast(new HttpServerCodec())
                            .addLast(new HttpObjectAggregator(64 * 1024))
                            .addLast(new ChunkedWriteHandler())
                            .addLast("httpHandler", new HttpRequestHandler("/ws"))
                            .addLast(new WebSocketServerProtocolHandler("/ws"))
//                            .addLast("wsHandler", new ServerHandler());
                            .addLast(new TextWebSocketFrameHandler());

                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

            // 绑定端口，开始接收进来的连接
            ChannelFuture f = serverBootstrap.bind(port).sync();

            // 等待服务器  socket 关闭 。
            // 在这个例子中，这不会发生，但你可以优雅地关闭你的服务器。
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}