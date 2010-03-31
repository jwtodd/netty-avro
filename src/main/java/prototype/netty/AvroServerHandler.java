package prototype.netty;

import org.apache.avro.ipc.Responder;
import org.apache.avro.specific.SpecificResponder;
import org.jboss.netty.channel.*;
import prototype.avro.Mail;
import prototype.avro.Server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;

@ChannelPipelineCoverage("one")
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
    public void messageReceived(ChannelHandlerContext context, MessageEvent event) throws IOException {
        @SuppressWarnings({"unchecked"})
        List<ByteBuffer> request = (List<ByteBuffer>) event.getMessage();

        logBuffer("request", request);

        if (!request.isEmpty()) {
            // todo: responder factory
            Responder responder = new SpecificResponder(Mail.class, new Server.MailImpl());
            List<ByteBuffer> response = responder.respond(request);

            logBuffer("response", response);

            writeResponse(event.getChannel(), response);
        }
    }

    private void logBuffer(String label, List<ByteBuffer> buffer) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, format("%s buffers: %s", label, buffer.size()));

            for (int i = 0; i < buffer.size(); i++) {
                logger.log(Level.FINE, format("  buffer [%s] length: %s", i, buffer.get(i).limit()));
            }
        }
    }

    private void writeResponse(Channel channel, List<ByteBuffer> response) {
        for (ByteBuffer buffer : response) {
            channel.write(buffer);

            logger.log(Level.FINE, format("wrote: %s", buffer.limit()));
        }

        ByteBuffer terminus = ByteBuffer.allocate(0);

        terminus.flip();

        channel.write(terminus);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, ExceptionEvent event) {
        //noinspection ThrowableResultOfMethodCallIgnored
        logger.log(Level.WARNING, "Unexpected exception from downstream.", event.getCause());

        event.getChannel().close();
    }

//    @Override
//    public void channelOpen(ChannelHandlerContext context, ChannelStateEvent event) {
//        AvroServer.getChannelGroup().add(event.getChannel());
//    }
}
