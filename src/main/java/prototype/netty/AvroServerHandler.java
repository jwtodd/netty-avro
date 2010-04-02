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
public class AvroServerHandler extends AbstractAvroHandler {

    private static final Logger logger = Logger.getLogger(AvroServerHandler.class.getName());

    @Override
    public void messageReceived(ChannelHandlerContext context, MessageEvent event) throws IOException {
        @SuppressWarnings({"unchecked"})
        List<ByteBuffer> request = (List<ByteBuffer>) event.getMessage();

        logBuffer("request", request);

        if (!request.isEmpty()) {
            logger.log(Level.FINE, format("processing request"));

            // todo: responder factory
            Responder responder = new SpecificResponder(Mail.class, new Server.MailImpl());
            List<ByteBuffer> response = responder.respond(request);

            logger.log(Level.FINE, format("return response"));
            logBuffer("response", response);

            writeResponse(event.getChannel(), response);
        }
    }

//    @Override
//    public void channelOpen(ChannelHandlerContext context, ChannelStateEvent event) {
//        AvroServer.getChannelGroup().add(event.getChannel());
//    }
}
