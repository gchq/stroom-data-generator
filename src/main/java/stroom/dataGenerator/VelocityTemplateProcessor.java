package stroom.dataGenerator;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.tools.generic.DateTool;
import stroom.dataGenerator.config.EventGenConfig;
import stroom.dataGenerator.config.TemplateConfig;

import java.io.Reader;
import java.io.Writer;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class VelocityTemplateProcessor extends AbstractTemplateProcessor {
    public static final String FORMAT_ID="Velocity";

    public VelocityTemplateProcessor(final EventGenConfig appConfig,
                                     final String streamName, final TemplateConfig config){
        super(appConfig, streamName, config);
    }


    @Override
    protected void processTemplate (final Reader input, final Writer output, final Instant timestamp){
        Velocity.init();

        VelocityContext context = new VelocityContext();

        context.put( "user","stroom");

        if (timestamp != null) {
            context.put("date", new SingleInstantDateTool(timestamp,
                    getConfig().getTimeZone(getAppConfig()), getConfig().getLocale(getAppConfig())));
        }
        Velocity.evaluate(context, output, getStreamName(), input);
    }

    private static class SingleInstantDateTool extends DateTool{
        private final Date date;
        SingleInstantDateTool (final Instant timestamp, TimeZone timeZone, Locale locale){
            this.date = Date.from(timestamp);
            setTimeZone(timeZone);
            setLocale(locale);
        }

        @Override
        public Date getDate() {
            return date;
        }
    }
}
