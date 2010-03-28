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

        // TODO: go from CBIS -> AVRO obj
        ChannelBufferInputStream stream = new ChannelBufferInputStream((ChannelBuffer) message);
        return null;
    }
}
