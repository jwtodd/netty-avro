package prototype.netty.avro;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import prototype.netty.avro.codec.AvroAccumulator;
import prototype.netty.avro.codec.AvroDecoder;
import prototype.netty.avro.codec.AvroEncoder;
import prototype.netty.avro.handler.AvroServerHandler;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;
import static org.jboss.netty.channel.Channels.pipeline;

public class AvroServerPipelineFactory implements ChannelPipelineFactory {

    private static final Logger logger = Logger.getLogger(AvroServerPipelineFactory.class.getName());

    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = pipeline();

        pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4));
        pipeline.addLast("avroDecoder", new AvroDecoder());
        pipeline.addLast("avroAccumulator", new AvroAccumulator());
        pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
        pipeline.addLast("avroEncoder", new AvroEncoder());
        pipeline.addLast("handler", new AvroServerHandler());

        if (logger.isLoggable(Level.FINE)) {
            for (Map.Entry<String, ChannelHandler> handler : pipeline.toMap().entrySet()) {
                logger.log(Level.FINE, format("pipeline handler: %s %s",
                        handler.getKey(), handler.getValue().getClass().getName()));
            }
        }

        return pipeline;
    }
}
