package prototype.netty;

import prototype.netty.LocalTimeProtocol.Continent;
import prototype.netty.LocalTimeProtocol.LocalTimes;
import prototype.netty.LocalTimeProtocol.Location;
import prototype.netty.LocalTimeProtocol.Locations;
import org.jboss.netty.channel.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

@ChannelPipelineCoverage("one")
public class AvroClientHandler extends SimpleChannelUpstreamHandler {

    private static final Logger logger = Logger.getLogger(AvroClientHandler.class.getName());

    private volatile Channel channel;
    private final BlockingQueue<LocalTimes> answer = new LinkedBlockingQueue<LocalTimes>();

    public List<String> getLocalTimes(Collection<String> cities) {
        Locations.Builder builder = Locations.newBuilder();

        for (String city : cities) {
            String[] components = city.split("/");
            Location location = Location.newBuilder().
                    setContinent(Continent.valueOf(components[0].toUpperCase())).
                    setCity(components[1]).build();

            builder.addLocation(location);
        }

        channel.write(builder.build());

        LocalTimeProtocol.LocalTimes localTimes;
        boolean interrupted = false;

        while (true) {
            try {
                localTimes = answer.take();
                break;
            } catch (InterruptedException e) {
                interrupted = true;
            }
        }

        if (interrupted) {
            Thread.currentThread().interrupt();
        }

        List<String> result = new ArrayList<String>();

        for (LocalTimeProtocol.LocalTime localTime : localTimes.getLocalTimeList()) {
            Formatter formatter = new Formatter().format(
                    "%4d-%02d-%02d %02d:%02d:%02d %s",
                    localTime.getYear(),
                    localTime.getMonth(),
                    localTime.getDayOfMonth(),
                    localTime.getHour(),
                    localTime.getMinute(),
                    localTime.getSecond(),
                    localTime.getDayOfWeek().name());

            result.add(formatter.toString());
        }

        return result;
    }

    @Override
    public void handleUpstream(ChannelHandlerContext context, ChannelEvent event) throws Exception {
        if (event instanceof ChannelStateEvent) {
            logger.info(event.toString());
        }

        super.handleUpstream(context, event);
    }

    @Override
    public void channelOpen(ChannelHandlerContext context, ChannelStateEvent event) throws Exception {
        channel = event.getChannel();

        super.channelOpen(context, event);
    }

    @Override
    public void messageReceived(ChannelHandlerContext context, final MessageEvent event) {
        boolean offered = answer.offer((LocalTimeProtocol.LocalTimes) event.getMessage());

        assert offered;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, ExceptionEvent event) {
        logger.log(Level.WARNING, "Unexpected exception from downstream.", event.getCause());

        event.getChannel().close();
    }
}
