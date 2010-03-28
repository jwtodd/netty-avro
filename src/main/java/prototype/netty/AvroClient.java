package prototype.netty;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

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
        if (args.length < 3) {
            printUsage();

            return;
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        Collection<String> cities = parseCities(args, 2);

        if (cities == null) {
            return;
        }

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
        AvroClientHandler handler = channel.getPipeline().get(AvroClientHandler.class);
        // List<String> response = handler.getLocalTimes(cities);

        channel.close().awaitUninterruptibly();
        bootstrap.releaseExternalResources();

        // printResults(cities, response);
    }

    private static void printUsage() {
        System.err.println("Usage: " + AvroClient.class.getSimpleName() +
                " <host> <port> <continent/city_name> ...");
        System.err.println("Example: " + AvroClient.class.getSimpleName() +
                " localhost 9091 America/New_York Asia/Seoul");
    }

    private static List<String> parseCities(String[] args, int offset) {
        List<String> cities = new ArrayList<String>();

        for (int i = offset; i < args.length; i++) {
            if (!args[i].matches("^[_A-Za-z]+/[_A-Za-z]+$")) {
                System.err.println("Syntax error: '" + args[i] + "'");
                printUsage();

                return null;
            }

            cities.add(args[i].trim());
        }

        return cities;
    }

    private static void printResults(Collection<String> cities, List<String> response) {
        Iterator<String> i1 = cities.iterator();
        Iterator<String> i2 = response.iterator();

        while (i1.hasNext()) {
            System.out.format("%28s: %s%n", i1.next(), i2.next());
        }
    }
}
