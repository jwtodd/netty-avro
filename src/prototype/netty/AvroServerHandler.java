package prototype.netty;

import org.jboss.netty.channel.*;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Calendar.*;

import prototype.netty.LocalTimeProtocol.Continent;
import prototype.netty.LocalTimeProtocol.DayOfWeek;
import prototype.netty.LocalTimeProtocol.LocalTime;
import prototype.netty.LocalTimeProtocol.LocalTimes;
import prototype.netty.LocalTimeProtocol.Location;
import prototype.netty.LocalTimeProtocol.Locations;

@ChannelPipelineCoverage("all")
public class AvroServerHandler extends SimpleChannelUpstreamHandler {

    private static final Logger logger = Logger.getLogger(AvroServerHandler.class.getName());

    @Override
    public void handleUpstream(ChannelHandlerContext context, ChannelEvent event) throws Exception {
        if (event instanceof ChannelStateEvent) {
            logger.info(event.toString());
        }

        super.handleUpstream(context, event);
    }

    @Override
    public void messageReceived(ChannelHandlerContext context, MessageEvent event) {
        Locations locations = (Locations) event.getMessage();
        long currentTime = System.currentTimeMillis();
        LocalTimes.Builder builder = LocalTimes.newBuilder();

        for (Location location : locations.getLocationList()) {
            TimeZone tz = TimeZone.getTimeZone(toString(location.getContinent()) + '/' + location.getCity());
            Calendar calendar = Calendar.getInstance(tz);
            calendar.setTimeInMillis(currentTime);

            builder.addLocalTime(LocalTime.newBuilder().
                    setYear(calendar.get(YEAR)).
                    setMonth(calendar.get(MONTH) + 1).
                    setDayOfMonth(calendar.get(DAY_OF_MONTH)).
                    setDayOfWeek(DayOfWeek.valueOf(calendar.get(DAY_OF_WEEK))).
                    setHour(calendar.get(HOUR_OF_DAY)).
                    setMinute(calendar.get(MINUTE)).
                    setSecond(calendar.get(SECOND)).build());
        }

        event.getChannel().write(builder.build());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, ExceptionEvent event) {
        logger.log(Level.WARNING, "Unexpected exception from downstream.", event.getCause());

        event.getChannel().close();
    }

    private static String toString(Continent content) {
        return "" + content.name().charAt(0) + content.name().toLowerCase().substring(1);
    }

    @Override
    public void channelOpen(ChannelHandlerContext context, ChannelStateEvent event) {
        AvroServer.getChannelGroup().add(event.getChannel());
    }
}
