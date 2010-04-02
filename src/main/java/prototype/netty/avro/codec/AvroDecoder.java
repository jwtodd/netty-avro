package prototype.netty.avro.codec;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;

@ChannelPipelineCoverage("all")
public class AvroDecoder extends OneToOneDecoder {

    private static final ByteBuffer NO_OP = ByteBuffer.allocate(0);
    private static final Logger logger = Logger.getLogger(AvroDecoder.class.getName());

    @Override
    protected Object decode(ChannelHandlerContext context, Channel channel, Object message) throws Exception {
        if (!(message instanceof ChannelBuffer)) {
            logger.log(Level.FINE, format("unexpected type: %s", message.getClass().getName()));

            return message;
        }

        ByteBuffer buffer = ((ChannelBuffer) message).toByteBuffer();

        logger.log(Level.FINE, format("buffer length: %s%s", buffer.limit(), buffer.limit() == 0 ? " [terminus]" : ""));

        return buffer.limit() > 0 ? buffer : NO_OP;
    }
}