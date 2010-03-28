package prototype.netty;

import org.jboss.netty.channel.*;

import java.util.logging.Level;
import java.util.logging.Logger;

@ChannelPipelineCoverage("all")
public class AvroServerHandler extends SimpleChannelUpstreamHandler {

    private static final Logger logger = Logger.getLogger(AvroServerHandler.class.getName());

    @Override
    public void handleUpstream(ChannelHandlerContext context, ChannelEvent event) throws Exception {
        if (event instanceof ChannelStateEvent) {
            logger.info(event.toString());
        }

        super.handleUpstream(context, event);
    }

    @Override
    public void messageReceived(ChannelHandlerContext context, MessageEvent event) {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, ExceptionEvent event) {
        logger.log(Level.WARNING, "Unexpected exception from downstream.", event.getCause());

        event.getChannel().close();
    }

    @Override
    public void channelOpen(ChannelHandlerContext context, ChannelStateEvent event) {
        AvroServer.getChannelGroup().add(event.getChannel());
    }
}
