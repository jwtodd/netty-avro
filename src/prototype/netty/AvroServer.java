package prototype.netty;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class AvroServer {

    private static final ChannelGroup channels = new DefaultChannelGroup("avro-server");

    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.valueOf(args[0]) : 9091;
        NioServerSocketChannelFactory factory = new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool());
        ServerBootstrap bootstrap = new ServerBootstrap(factory);

        bootstrap.setPipelineFactory(new AvroServerPipelineFactory());

        Channel channel = bootstrap.bind(new InetSocketAddress(port));

        channels.add(channel);

        // todo: add relevant wait

//        ChannelGroupFuture channelsFuture = channels.close();
//
//        channelsFuture.awaitUninterruptibly();
//        factory.releaseExternalResources();
    }

    static ChannelGroup getChannelGroup() {
        return channels;
    }
}
