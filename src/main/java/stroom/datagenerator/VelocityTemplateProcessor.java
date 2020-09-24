/*
 * Copyright 2020 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package stroom.datagenerator;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.tools.generic.ComparisonDateTool;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.MathTool;
import org.apache.velocity.tools.generic.NumberTool;
import stroom.datagenerator.config.EventGenConfig;
import stroom.datagenerator.config.TemplateConfig;

import java.io.Reader;
import java.io.Writer;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class VelocityTemplateProcessor extends AbstractTemplateProcessor {
    public static final String FORMAT_ID="Velocity";

    public VelocityTemplateProcessor(final EventGenConfig appConfig,
                                     final String streamName, final TemplateConfig config){
        super(appConfig, streamName, config);
        Velocity.init();
    }


    @Override
    protected void processTemplate (final Reader input, final Writer output, final ProcessingContext context)
      throws TemplateProcessingException {
        VelocityContext velocityContext = initialiseContext (context);

        Velocity.evaluate(velocityContext, output, getStreamName(), input);
    }

    private VelocityContext initialiseContext (ProcessingContext context) throws TemplateProcessingException{
        final VelocityContext velocityContext;
        if (context.getLanguageNativeContext() != null){
            if (! (context.getLanguageNativeContext() instanceof VelocityContext)){
                throw new TemplateProcessingException(getStreamName(), getConfig().getPath(), "Currently unable to mix template languages");
            }
            velocityContext = (VelocityContext) context.getLanguageNativeContext();
        } else {
            velocityContext = new VelocityContext();

            //Set vals that don't change for entire file/substream
            velocityContext.put("user", context.getUserId());
            velocityContext.put("substream", context.getSubstreamNum());
            velocityContext.put("host", context.getHostId());
            velocityContext.put("fqdn", context.getHostFqdn());
            velocityContext.put("hostip", context.getIpAddress());

            velocityContext.put("allusers", context.getAllUsers());
            velocityContext.put("allhosts", context.getAllHosts());

            velocityContext.put("random", context.getRandom());

            //Add velocity tools
            velocityContext.put("math", new MathTool());
            velocityContext.put("number", new NumberTool());
        }

        //Set vals that change each event
        velocityContext.put("otherip", context.generateRandomIpAddress());
        velocityContext.put("seq", context.getSequenceNumber());
        if (context.getTimestamp() != null) {
            velocityContext.put("date", new SingleInstantDateTool(context.getTimestamp(),
                    getConfig().getTimeZone(getAppConfig()), getConfig().getLocale(getAppConfig())));
        }

        context.setLanguageNativeContext(velocityContext);

        return velocityContext;
    }

    private static class SingleInstantDateTool extends ComparisonDateTool {
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
