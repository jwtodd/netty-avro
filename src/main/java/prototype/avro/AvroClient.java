package prototype.avro;

import org.apache.avro.ipc.SocketTransceiver;
import org.apache.avro.specific.SpecificRequestor;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;

public class AvroClient {

    private SocketTransceiver client;
    private static final Logger logger = Logger.getLogger(AvroClient.class.getName());

    // todo: lifecycle (start, stop)
    public AvroClient(SocketAddress address) throws IOException {
        client = new SocketTransceiver(address);

        logger.log(Level.FINE, format("server address: %s", address));
    }

    // todo: generalize/templatize
    public String dispatch(Message message) throws IOException {
        logger.log(Level.FINE, format("dispatching message: %s", message));

        Mail proxy = (Mail) SpecificRequestor.getClient(Mail.class, client);
        String response = proxy.send(message).toString();

        logger.log(Level.FINE, format("response: %s", response));

        return response;
    }

    public void dispose() throws IOException {
        client.close();
    }
}