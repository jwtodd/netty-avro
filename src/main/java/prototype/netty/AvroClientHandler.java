package prototype.netty;

import org.jboss.netty.channel.*;
import prototype.avro.Message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

@ChannelPipelineCoverage("one")
public class AvroClientHandler extends SimpleChannelUpstreamHandler {

    private static final Logger logger = Logger.getLogger(AvroClientHandler.class.getName());

    private volatile Channel channel;

    @Override
    public void handleUpstream(ChannelHandlerContext context, ChannelEvent event) throws Exception {
        if (event instanceof ChannelStateEvent) {
            logger.info(event.toString());
        }

        super.handleUpstream(context, event);
    }

    @Override
    public void channelOpen(ChannelHandlerContext context, ChannelStateEvent event) throws Exception {
        channel = event.getChannel();

        super.channelOpen(context, event);
    }

    public String dispatch(Message message) {
        // todo: implement
        // todo: handle response
        // todo: netty/transceiver
        // todo: write message, wait for response
        return null;
    }

    @Override
    public void messageReceived(ChannelHandlerContext context, final MessageEvent event) {
        // todo: implement
        int i = 0;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, ExceptionEvent event) {
        logger.log(Level.WARNING, "Unexpected exception from downstream.", event.getCause());

        event.getChannel().close();
    }
}
