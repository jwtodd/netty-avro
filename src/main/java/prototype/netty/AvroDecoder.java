package prototype.netty;

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.Message;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

@ChannelPipelineCoverage("all")
public class AvroDecoder extends OneToOneDecoder {

    private final Message prototype;
    private final ExtensionRegistry extensionRegistry;

    public AvroDecoder(Message prototype) {
        this(prototype, null);
    }

    public AvroDecoder(Message prototype, ExtensionRegistry extensionRegistry) {
        if (prototype == null) {
            throw new NullPointerException("prototype");
        }

        this.prototype = prototype.getDefaultInstanceForType();
        this.extensionRegistry = extensionRegistry;
    }

    @Override
    protected Object decode(ChannelHandlerContext context, Channel channel, Object message) throws Exception {
        if (!(message instanceof ChannelBuffer)) {
            return message;
        }

        // todo: go from CBIS -> AVRO obj
        ChannelBufferInputStream stream = new ChannelBufferInputStream((ChannelBuffer) message);
//        List<ByteBuffer> bytes = toBytes(stream);

        return extensionRegistry == null ?
                prototype.newBuilderForType().mergeFrom(stream).build() :
                prototype.newBuilderForType().mergeFrom(stream, extensionRegistry).build();
    }

    private List<ByteBuffer> toBytes(InputStream stream) throws IOException {
        List<ByteBuffer> buffers = new ArrayList<ByteBuffer>();

        while (true) {
            int length = (stream.read() << 24) + (stream.read() << 16) + (stream.read() << 8) + stream.read();

            if (length == 0) { // end of buffers
                return buffers;
            }

            ByteBuffer buffer = ByteBuffer.allocate(length);

            while (buffer.hasRemaining()) {
                int p = buffer.position();
                int i = stream.read(buffer.array(), p, buffer.remaining());

                if (i < 0) {
                    throw new EOFException("Unexpected EOF");
                }

                buffer.position(p + i);
            }

            buffer.flip();
            buffers.add(buffer);
        }
    }
}
