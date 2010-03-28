package prototype.avro;

import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificRecord;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.avro.util.Utf8;

@SuppressWarnings("all")
public class Message extends SpecificRecordBase implements SpecificRecord {

    public static final Schema SCHEMA$ = Schema.parse("{\"type\":\"record\",\"name\":\"Message\",\"namespace\":\"prototype.avro\",\"fields\":[{\"name\":\"to\",\"type\":\"string\"},{\"name\":\"from\",\"type\":\"string\"},{\"name\":\"body\",\"type\":\"string\"}]}");
    public org.apache.avro.util.Utf8 to;
    public org.apache.avro.util.Utf8 from;
    public org.apache.avro.util.Utf8 body;

    public Schema getSchema() {
        return SCHEMA$;
    }

    public java.lang.Object get(int field$) {
        switch (field$) {
            case 0:
                return to;
            case 1:
                return from;
            case 2:
                return body;
            default:
                throw new AvroRuntimeException("Bad index");
        }
    }

    @SuppressWarnings(value = "unchecked")
    public void put(int field$, java.lang.Object value$) {
        switch (field$) {
            case 0:
                to = (Utf8) value$;
                break;
            case 1:
                from = (Utf8) value$;
                break;
            case 2:
                body = (Utf8) value$;
                break;
            default:
                throw new AvroRuntimeException("Bad index");
        }
    }
}
