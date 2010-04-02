package prototype.netty;

import org.jboss.netty.channel.Channel;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;


public class WriterUtil {

    private static final Logger logger = Logger.getLogger(WriterUtil.class.getName());
    
    public static void write(Channel channel, List<ByteBuffer> buffers) {
        for (ByteBuffer buffer : buffers) {
            channel.write(buffer);

            logger.log(Level.FINE, format("wrote: %s", buffer.limit()));
        }

        // todo: ?can we avoid reallocation?
        ByteBuffer terminus = ByteBuffer.allocate(0);

        terminus.flip();
        channel.write(terminus);
    }
}
