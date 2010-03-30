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

public class Client {

    private static SocketTransceiver client;

    public static class MailImpl implements Mail {

        public Utf8 send(Message message) {
            return new Utf8("(client)Sent message to " + message.to.toString()
                    + " from " + message.from.toString()
                    + " with body " + message.body.toString());
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 4) {
            System.out.println("Usage: <port> <to> <from> <body>");
        }

        final int port = Integer.valueOf(args[0]);

        Executors.newSingleThreadExecutor().submit(
                new Callable<Object>() {
                    public Object call() throws IOException {
                        client = new SocketTransceiver(new InetSocketAddress(port));

                        return null;
                    }
                }
        );

        while (client == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }

//        Mail proxy = (Mail) SpecificRequestor.getClient(Mail.class, client);
        Mail proxy = (Mail) SpecificRequestor.getClient(Mail.class, client);
        Message message = new Message();

        message.to = new Utf8(args[0]);
        message.from = new Utf8(args[1]);
        message.body = new Utf8(args[2]);

        System.out.println("Result: " + proxy.send(message));

        client.close();
    }
}
