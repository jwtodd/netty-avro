package prototype.netty;

import org.jboss.netty.channel.*;
import prototype.avro.Message;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

@ChannelPipelineCoverage("one")
public class AvroClientHandler extends SimpleChannelUpstreamHandler {

    private static final Logger logger = Logger.getLogger(AvroClientHandler.class.getName());
    private volatile Channel channel;
    // todo: generalize return type
    private final BlockingQueue<String> answer = new LinkedBlockingQueue<String>();

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

    // todo: break this out to specialized class
    public String dispatch(Message message) {
        // todo: message -> List<ByteBuffer>
        for (ByteBuffer buffer : new ArrayList<ByteBuffer>()) {
           channel.write(buffer);
        }

        String response;
        boolean interrupted = false;

        for (; ;) {
            try {
                response = answer.take();
                break;
            } catch (InterruptedException e) {
                interrupted = true;
            }
        }

        if (interrupted) {
            Thread.currentThread().interrupt();
        }

        return response;
    }

    @Override
         public void messageReceived(ChannelHandlerContext context, final MessageEvent event) {
             boolean offered = answer.offer((String) event.getMessage());
             assert offered;
         }

    @Override
         public void exceptionCaught(ChannelHandlerContext context, ExceptionEvent event) {
             logger.log(Level.WARNING, "Unexpected exception from downstream.", event.getCause());

             event.getChannel().close();
         }
}
