package prototype.netty;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;

import static org.jboss.netty.channel.Channels.pipeline;

public class AvroClientPipelineFactory implements ChannelPipelineFactory {

    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = pipeline();

        pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4));
        pipeline.addLast("avroDecoder", new AvroDecoder());
        pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
        pipeline.addLast("avroEncoder", new AvroEncoder());
        pipeline.addLast("handler", new AvroClientHandler());

        return pipeline;
    }
}
