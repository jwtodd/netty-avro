package prototype.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

@ChannelPipelineCoverage("all")
public class AvroDecoder extends OneToOneDecoder {

    @Override
    protected Object decode(ChannelHandlerContext context, Channel channel, Object message) throws Exception {
        if (!(message instanceof ChannelBuffer)) {
            return message;
        }

        ChannelBufferInputStream stream = new ChannelBufferInputStream((ChannelBuffer)message);
        int length = stream.available();

        if (length == 0) {
            return ByteBuffer.allocate(0);
        }

        byte[] bytes = new byte[length];

        stream.readFully(bytes);

        ByteBuffer buffer = ByteBuffer.allocate(length).put(bytes);

        buffer.flip();

        return buffer;
    }
}
