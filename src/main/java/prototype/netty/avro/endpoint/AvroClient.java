package prototype.netty.avro.endpoint;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import prototype.avro.Message;
import prototype.netty.avro.handler.AvroClientHandler;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;

public class AvroClient {

    private static final Logger logger = Logger.getLogger(AvroClient.class.getName());
    private ClientBootstrap bootstrap;
    private Channel channel;

    // todo: lifecycle (start, stop)
    public AvroClient(SocketAddress address) {
        NioClientSocketChannelFactory factory = new NioClientSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool());

        bootstrap = new ClientBootstrap(factory);

        bootstrap.setPipelineFactory(new AvroClientPipelineFactory());

        ChannelFuture connection = bootstrap.connect(address);

        connection.awaitUninterruptibly();

        if (!connection.isSuccess()) {
            //noinspection ThrowableResultOfMethodCallIgnored
            logger.log(Level.WARNING, format("unable to connect: %1s", connection.getCause().getMessage()));
        }

        channel = connection.getChannel();
    }

    // todo: generalize/templatize
    public String dispatch(Message message) throws IOException {
        // todo: determine requester
        AvroClientHandler handler = channel.getPipeline().get(AvroClientHandler.class);
        String response = handler.dispatch(message);

        logger.log(Level.FINE, format("response: %s", response));

        return response;
    }

    public void dispose() {
        channel.close().awaitUninterruptibly();
        bootstrap.releaseExternalResources();
    }
}
