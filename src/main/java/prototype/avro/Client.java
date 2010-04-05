package prototype.avro;

import org.apache.avro.util.Utf8;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;

public class Client {

    private static final Logger logger = Logger.getLogger(Client.class.getName());

    public static void main(String[] args) throws IOException {
        if (args.length != 4) {
            System.out.println("Usage: <port> <to> <from> <body>");
            System.exit(-1);
        }

        int port = Integer.valueOf(args[0]);
        InetSocketAddress address = new InetSocketAddress(port);
        AvroClient client = new AvroClient(address);
        Message message = createMessage(args[1], args[2], args[3]);
        String response = client.dispatch(message);

        logger.log(Level.FINE, format("response: %s", response));

        System.out.println("result: " + response);

        client.dispose();
    }

    private static Message createMessage(String to, String from, String body) {
        Message message = new Message();

        message.to = new Utf8(to);
        message.from = new Utf8(from);
        message.body = new Utf8(body);

        logger.log(Level.FINE, format("created message: %s", message.toString()));

        return message;
    }
}
