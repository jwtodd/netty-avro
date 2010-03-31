package prototype.netty;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;

public class AvroServer {

    private static final ChannelGroup channels = new DefaultChannelGroup("avro-server");
    private static final Logger logger = Logger.getLogger(AvroServer.class.getName());

    // todo: add shutdown hooks/etc

    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.valueOf(args[0]) : 9091;

        logger.log(Level.FINE, format("port: %s", port));

        NioServerSocketChannelFactory factory = new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool());
        ServerBootstrap bootstrap = new ServerBootstrap(factory);

        bootstrap.setPipelineFactory(new AvroServerPipelineFactory());

        Channel channel = bootstrap.bind(new InetSocketAddress(port));

        channels.add(channel);

//        ChannelGroupFuture channelsFuture = channels.close();
//
//        channelsFuture.awaitUninterruptibly();
//        factory.releaseExternalResources();
    }

//    static ChannelGroup getChannelGroup() {
//        return channels;
//    }
}
