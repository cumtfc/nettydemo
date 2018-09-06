import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author fengchu created on 2018/9/6-16:58
 */
public class Decoder extends ByteToMessageDecoder { 
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) { 
        if (in.readableBytes() < 4) {
            return; 
        }
        out.add(new UnixTime(in.readUnsignedInt()));
    }
}
