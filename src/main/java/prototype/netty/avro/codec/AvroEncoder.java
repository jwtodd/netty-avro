package prototype.netty.avro.codec;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;
import static org.jboss.netty.buffer.ChannelBuffers.wrappedBuffer;

@ChannelPipelineCoverage("all")
public class AvroEncoder extends OneToOneEncoder {

    private static final Logger logger = Logger.getLogger(AvroEncoder.class.getName());

    public AvroEncoder() {
        super();
    }

    @Override
    protected Object encode(ChannelHandlerContext context, Channel channel, Object message) throws Exception {
        if (!(message instanceof ByteBuffer)) {
            return message;
        }

        ByteBuffer buffer = (ByteBuffer) message;

        if (logger.isLoggable(Level.FINE)) {
            int length = buffer.limit();

            logger.log(Level.FINE, format("buffer length: %s %s", length, length == 0 ? "[terminus]" : ""));
        }

        return wrappedBuffer(buffer.array());
    }
}
