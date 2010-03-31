package prototype.netty;

import org.apache.avro.util.Utf8;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import prototype.avro.Message;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AvroClient {

    private static final Logger logger = Logger.getLogger(AvroClient.class.getName());

    public static void main(String[] args) throws Exception {
        if (args.length != 4) {
            System.out.println("Usage: <port> <to> <from> <body>");
            System.exit(1);
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        Message message = new Message();

        message.to = new Utf8(args[2]);
        message.from = new Utf8(args[3]);
        message.body = new Utf8(args[4]);

        NioClientSocketChannelFactory factory = new NioClientSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool());
        ClientBootstrap bootstrap = new ClientBootstrap(factory);

        bootstrap.setPipelineFactory(new AvroClientPipelineFactory());

        ChannelFuture connectFuture = bootstrap.connect(new InetSocketAddress(host, port));

        connectFuture.awaitUninterruptibly();

        if (!connectFuture.isSuccess()) {
            //noinspection ThrowableResultOfMethodCallIgnored
            logger.log(Level.WARNING, String.format("unable to connect: %1s", connectFuture.getCause().getMessage()));
        }

        Channel channel = connectFuture.getChannel();
        // todo: handler factory, to vary request protocol/message
        AvroClientHandler handler = channel.getPipeline().get(AvroClientHandler.class);
        String response = handler.dispatch(message);

        // List<String> response = handler.getLocalTimes(cities);

        channel.close().awaitUninterruptibly();
        bootstrap.releaseExternalResources();

//        printResults(cities, response);
    }
}
