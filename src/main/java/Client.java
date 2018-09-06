import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

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
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline()
                        .addLast(new Decoder())
                        .addLast(new ClientHandler());
                }
            });
            connect(host, port, b);
            Thread.sleep(5 * 60 * 10000);
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
            // 等待连接关闭
            f.channel().closeFuture()
                .addListener((ChannelFutureListener) future -> connect(host, port, b));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
