package prototype.netty;

import org.apache.avro.ipc.Transceiver;
import org.apache.avro.specific.SpecificRequestor;
import org.apache.avro.util.Utf8;
import org.jboss.netty.channel.*;
import prototype.avro.Mail;
import prototype.avro.Message;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

@ChannelPipelineCoverage("one")
public class AvroClientHandler extends AbstractAvroHandler {

    private static final Logger logger = Logger.getLogger(AvroClientHandler.class.getName());
    private volatile Channel channel;
    private final BlockingQueue<List<ByteBuffer>> queue = new LinkedBlockingQueue<List<ByteBuffer>>();
    private Transceiver transceiver = new Transceiver() {

        @Override
        public String getRemoteName() {
            return channel.getRemoteAddress().toString();
        }

        @Override
        public List<ByteBuffer> readBuffers() throws IOException {
            List<ByteBuffer> response = null;
            boolean interrupted = false;

            try {
                response = queue.take();
            } catch (InterruptedException e) {
                interrupted = true;
            }

            if (interrupted) {
                Thread.currentThread().interrupt();
            }

            return response;
        }

        @Override
        public void writeBuffers(List<ByteBuffer> buffers) throws IOException {
            WriterUtil.write(channel, buffers);
        }
    };

    @Override
    public void handleUpstream(ChannelHandlerContext context, ChannelEvent event) throws Exception {
        if (event instanceof ChannelStateEvent) {
            logger.log(Level.FINE, event.toString());
        }

        super.handleUpstream(context, event);
    }

    @Override
    public void channelOpen(ChannelHandlerContext context, ChannelStateEvent event) throws Exception {
        channel = event.getChannel();

        super.channelOpen(context, event);
    }

    public String dispatch(Message message) throws IOException {
        // todo: break this out to specialized class
        Mail proxy = (Mail) SpecificRequestor.getClient(Mail.class, transceiver);
        Utf8 response = proxy.send(message);

        return response.toString();
    }

    @Override
    public void messageReceived(ChannelHandlerContext context, final MessageEvent event) {
        @SuppressWarnings({"unchecked"})
        List<ByteBuffer> response = (List<ByteBuffer>) event.getMessage();

        logBuffer("response", response);

        if (!response.isEmpty()) {
            boolean offered = queue.offer(response);

            logger.log(Level.FINE, String.format("response offered: %s", offered));
        }
    }
}
