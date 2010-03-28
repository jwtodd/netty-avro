package prototype.avro;

import org.apache.avro.Protocol;
import org.apache.avro.ipc.AvroRemoteException;
import org.apache.avro.util.Utf8;

@SuppressWarnings("all")
public interface Mail {
    public static final Protocol PROTOCOL = Protocol.parse("{\"protocol\":\"Mail\",\"namespace\":\"prototype.avro\",\"types\":[{\"type\":\"record\",\"name\":\"Message\",\"fields\":[{\"name\":\"to\",\"type\":\"string\"},{\"name\":\"from\",\"type\":\"string\"},{\"name\":\"body\",\"type\":\"string\"}]}],\"messages\":{\"send\":{\"request\":[{\"name\":\"message\",\"type\":\"Message\"}],\"response\":\"string\"}}}");

    Utf8 send(Message message) throws AvroRemoteException;
}
