package prototype.netty;

import com.google.protobuf.Message;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import static org.jboss.netty.buffer.ChannelBuffers.wrappedBuffer;

@ChannelPipelineCoverage("all")
public class AvroEncoder extends OneToOneEncoder {

    public AvroEncoder() {
        super();
    }

    @Override
    protected Object encode(ChannelHandlerContext context, Channel channel, Object message) throws Exception {
        if (!(message instanceof Message)) {
            return message;
        }

        return wrappedBuffer(((Message) message).toByteArray());
    }
}
