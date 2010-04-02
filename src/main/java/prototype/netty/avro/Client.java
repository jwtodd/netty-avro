package prototype.netty.avro;

import org.apache.avro.util.Utf8;
import prototype.avro.Message;
import prototype.netty.avro.endpoint.AvroClient;

import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;

public class Client {

    private static final Logger logger = Logger.getLogger(Client.class.getName());

    public static void main(String[] args) throws Exception {
        if (args.length != 4) {
            System.out.println("Usage: <port> <to> <from> <body>");
            System.exit(1);
        }

        logger.log(Level.FINE, format("port: %s, to: %s, from: %s, body: %s", args[0], args[1], args[2], args[3]));

        int port = Integer.parseInt(args[0]);
        String to = args[1];
        String from = args[2];
        String body = args[3];
        InetSocketAddress address = new InetSocketAddress(port);
        AvroClient client = new AvroClient(address);
        Message message = createMessage(to, from, body);
        String response = client.dispatch(message);

        logger.log(Level.FINE, format("response: %s", response));

        System.out.println("response: " + response);

        client.dispose();
    }

    private static Message createMessage(String to, String from, String body) {
        Message message = new Message();

        message.to = new Utf8(to);
        message.from = new Utf8(from);
        message.body = new Utf8(body);

        return message;
    }
}