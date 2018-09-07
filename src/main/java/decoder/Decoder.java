package decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;

/**
 * @author fengchu created on 2018/9/6-16:58
 */
public class Decoder extends ByteToMessageDecoder { 
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        ByteBuf copy = in.copy();
        in.clear();
        out.add(copy.toString(CharsetUtil.UTF_8));
    }
}
