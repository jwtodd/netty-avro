package prototype.netty.avro;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.net.SocketAddress;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;

public class AvroServer {

    //    private static final ChannelGroup channels = new DefaultChannelGroup("avro-server");
    private static final Logger logger = Logger.getLogger(AvroServer.class.getName());

    // todo: lifecycle (start, stop)
    public AvroServer(SocketAddress address) {

        logger.log(Level.FINE, format("instantiating with address: %s", address));

        NioServerSocketChannelFactory factory = new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool());
        ServerBootstrap bootstrap = new ServerBootstrap(factory);

        bootstrap.setPipelineFactory(new AvroServerPipelineFactory());

        bootstrap.bind(address);
//        Channel channel = bootstrap.bind(address);
//
//        channels.add(channel);
//
//         ChannelGroupFuture channelsFuture = channels.close();
//
//         channelsFuture.awaitUninterruptibly();
//         factory.releaseExternalResources();
    }

//    static ChannelGroup getChannelGroup() {
//        return channels;
//    }
}