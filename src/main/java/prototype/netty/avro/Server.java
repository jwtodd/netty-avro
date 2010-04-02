package prototype.netty.avro;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import prototype.netty.avro.AvroServerPipelineFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;

public class Server {

    private static final Logger logger = Logger.getLogger(Server.class.getName());

    // todo: add shutdown hooks/etc

    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.valueOf(args[0]) : 9091;

        logger.log(Level.FINE, format("port: %s", port));

        InetSocketAddress address = new InetSocketAddress(port);
        // todo: lifecycle (start, stop)
        AvroServer server = new AvroServer(address);
    }
}
