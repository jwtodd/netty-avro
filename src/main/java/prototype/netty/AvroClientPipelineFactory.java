package prototype.netty;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;
import static org.jboss.netty.channel.Channels.pipeline;

public class AvroClientPipelineFactory implements ChannelPipelineFactory {

    private static final Logger logger = Logger.getLogger(AvroClientPipelineFactory.class.getName());

    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = pipeline();

        pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4));
        pipeline.addLast("avroDecoder", new AvroDecoder());
        pipeline.addLast("avroAccumulator", new AvroAccumulator());
        pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
        pipeline.addLast("avroEncoder", new AvroEncoder());
        pipeline.addLast("handler", new AvroClientHandler());

        if (logger.isLoggable(Level.FINE)) {
            for (Map.Entry<String, ChannelHandler> handler : pipeline.toMap().entrySet()) {
                logger.log(Level.FINE, format("pipeline handler: %s %s",
                        handler.getKey(), handler.getValue().getClass().getName()));
            }
        }

        return pipeline;
    }
}
