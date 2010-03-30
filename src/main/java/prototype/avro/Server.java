package prototype.avro;

import org.apache.avro.ipc.SocketServer;
import org.apache.avro.ipc.SocketTransceiver;
import org.apache.avro.specific.SpecificRequestor;
import org.apache.avro.specific.SpecificResponder;
import org.apache.avro.util.Utf8;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static SocketServer server;
    private static final int DEFAULT_PORT = 9090;

    public static class MailImpl implements Mail {

        public Utf8 send(Message message) {
            return new Utf8("(server)Sent message to " + message.to.toString()
                    + " from " + message.from.toString()
                    + " with body " + message.body.toString());
        }
    }

    public static void main(String[] args) throws IOException {
        final int port = args.length >= 1 ? Integer.valueOf(args[0]) : DEFAULT_PORT;

        Executors.newSingleThreadExecutor().submit(
                new Callable<Object>() {
                    public Object call() throws IOException {
                        server = new SocketServer(new SpecificResponder(
                                Mail.class, new MailImpl()), new InetSocketAddress(port ));

                        return null;
                    }
                });

        while (server == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }

        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
