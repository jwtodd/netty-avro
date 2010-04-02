package prototype.netty;

import org.jboss.netty.channel.*;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;

abstract class AbstractAvroHandler extends SimpleChannelUpstreamHandler {

    private static final Logger logger = Logger.getLogger(AbstractAvroHandler.class.getName());

    @Override
    public void handleUpstream(ChannelHandlerContext context, ChannelEvent event) throws Exception {
        if (event instanceof ChannelStateEvent) {
            logger.info(event.toString());
        }

        super.handleUpstream(context, event);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, ExceptionEvent event) {
        //noinspection ThrowableResultOfMethodCallIgnored
        logger.log(Level.WARNING, "Unexpected exception from downstream.", event.getCause());

        event.getChannel().close();
    }

    protected void write(Channel channel, List<ByteBuffer> buffers) {
        for (ByteBuffer buffer : buffers) {
            channel.write(buffer);

            logger.log(Level.FINE, format("wrote: %s", buffer.limit()));
        }

        // todo: ?can we avoid reallocation?
        ByteBuffer terminus = ByteBuffer.allocate(0);

        terminus.flip();
        channel.write(terminus);
    }

    protected void logBuffer(String label, List<ByteBuffer> buffer) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, format("%s buffers: %s", label, buffer.size()));

            for (int i = 0; i < buffer.size(); i++) {
                logger.log(Level.FINE, format("  buffer [%s] length: %s", i, buffer.get(i).limit()));
            }
        }
    }
}
