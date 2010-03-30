package prototype.netty;

import org.apache.avro.ipc.Responder;
import org.apache.avro.specific.SpecificResponder;
import org.jboss.netty.channel.*;
import prototype.avro.Mail;
import prototype.avro.Server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@ChannelPipelineCoverage("one")
public class AvroServerHandler extends SimpleChannelUpstreamHandler {

    private static final Logger logger = Logger.getLogger(AvroServerHandler.class.getName());
    private List<ByteBuffer> request = new ArrayList<ByteBuffer>();

    @Override
    public void handleUpstream(ChannelHandlerContext context, ChannelEvent event) throws Exception {
        if (event instanceof ChannelStateEvent) {
            logger.info(event.toString());
        }

        super.handleUpstream(context, event);
    }

    @Override
    public void messageReceived(ChannelHandlerContext context, MessageEvent event) throws IOException {
        ByteBuffer requestBuffer = (ByteBuffer) event.getMessage();

        if (requestBuffer.capacity() > 0) {
            this.request.add(requestBuffer);
        } else {
            // todo: response/content negotation: detect/instantiate respective responder
            Responder responder = new SpecificResponder(Mail.class, new Server.MailImpl());
            List<ByteBuffer> response = responder.respond(request);
            Channel channel = event.getChannel();

            for (ByteBuffer responseBuffer : response) {
                channel.write(responseBuffer);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, ExceptionEvent event) {
        logger.log(Level.WARNING, "Unexpected exception from downstream.", event.getCause());

        event.getChannel().close();
    }
//
//    @Override
//    public void channelOpen(ChannelHandlerContext context, ChannelStateEvent event) {
//        AvroServer.getChannelGroup().add(event.getChannel());
//    }
}
