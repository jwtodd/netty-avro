package prototype.netty.avro;

import java.net.InetSocketAddress;
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
