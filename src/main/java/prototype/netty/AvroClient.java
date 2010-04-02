package prototype.netty;

import org.apache.avro.util.Utf8;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import prototype.avro.Message;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;

public class AvroClient {

    private static final Logger logger = Logger.getLogger(AvroClient.class.getName());

    public static void main(String[] args) throws Exception {
        if (args.length != 4) {
            System.out.println("Usage: <port> <to> <from> <body>");
            System.exit(1);
        }

        logger.log(Level.FINE, format("port: %s, to: %s, from: %s, body: %s", args[0], args[1], args[2], args[3]));

//        String host = args[0];
        int port = Integer.parseInt(args[0]);
        String to = args[1];
        String from = args[2];
        String body = args[3];

        NioClientSocketChannelFactory factory = new NioClientSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool());
        ClientBootstrap bootstrap = new ClientBootstrap(factory);

        bootstrap.setPipelineFactory(new AvroClientPipelineFactory());

        ChannelFuture connectFuture = bootstrap.connect(new InetSocketAddress(/*host,*/ port));

        connectFuture.awaitUninterruptibly();

        if (!connectFuture.isSuccess()) {
            //noinspection ThrowableResultOfMethodCallIgnored
            logger.log(Level.WARNING, format("unable to connect: %1s", connectFuture.getCause().getMessage()));
        }

        Channel channel = connectFuture.getChannel();
        AvroClientHandler handler = channel.getPipeline().get(AvroClientHandler.class);
        Message message = createMessage(to, from, body);
        String response = handler.dispatch(message);

        logger.log(Level.FINE, format("response: %s", response));

        System.out.println("response: " + response);

        channel.close().awaitUninterruptibly();
        bootstrap.releaseExternalResources();
    }

    private static Message createMessage(String to, String from, String body) {
        Message message = new Message();

        message.to = new Utf8(to);
        message.from = new Utf8(from);
        message.body = new Utf8(body);

        return message;
    }
}
