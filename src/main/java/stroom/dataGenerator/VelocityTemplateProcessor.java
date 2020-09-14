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
        Velocity.init();
    }


    @Override
    protected void processTemplate (final Reader input, final Writer output, final ProcessingContext context){
        VelocityContext velocityContext = initialiseContext (context);

        Velocity.evaluate(velocityContext, output, getStreamName(), input);
    }

    private VelocityContext initialiseContext (ProcessingContext context){
        final VelocityContext velocityContext;
        if (context.getLanguageNativeContext() != null){
            if (! (context.getLanguageNativeContext() instanceof VelocityContext)){
                throw new UnsupportedOperationException("Currently unable to mix template languages");
            }
            velocityContext = (VelocityContext) context.getLanguageNativeContext();
        } else {
            velocityContext = new VelocityContext();

            //Set vals that don't change for entire file/substrewam
            velocityContext.put("user", context.getUserId());
            velocityContext.put("substream", context.getSubstreamNum());
            velocityContext.put("host", context.getHostId());
            velocityContext.put("fqdn", context.getHostFqdn());
            velocityContext.put("hostip", context.getIpAddress());
            velocityContext.put("otherip", context.generateRandomIpAddress());
        }

        //Set vals that change each event
        velocityContext.put("seq", context.getSequenceNumber());
        if (context.getTimestamp() != null) {
            velocityContext.put("date", new SingleInstantDateTool(context.getTimestamp(),
                    getConfig().getTimeZone(getAppConfig()), getConfig().getLocale(getAppConfig())));
        }

        context.setLanguageNativeContext(velocityContext);

        return velocityContext;
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
