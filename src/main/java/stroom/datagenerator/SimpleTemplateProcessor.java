package stroom.datagenerator;

import stroom.datagenerator.config.EventGenConfig;
import stroom.datagenerator.config.TemplateConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class SimpleTemplateProcessor extends AbstractTemplateProcessor {
    public static final String FORMAT_ID = "Simple";
    private static final int BUFFERSIZE = 1024;
    private final ZoneId zomeId;

    SimpleTemplateProcessor(final EventGenConfig appConfig,
                            final String streamName, final TemplateConfig config) {
        super(appConfig, streamName, config);

        zomeId = getConfig().getTimeZone(getAppConfig()).toZoneId();
    }

    @Override
    protected void processTemplate (final Reader input, final Writer output, final ProcessingContext context)
            throws IOException {
        BufferedReader bufferedReader = new BufferedReader(input);
        String line;
        while ((line = bufferedReader.readLine()) != null){
            String variablesChangedLine = line.replace("$user", context.getUserId())
                .replace("$substream", "" + context.getSubstreamNum())
                .replace("$hostip", context.getIpAddress())
                .replace("$host", context.getHostId())
                .replace("$fqdn", context.getHostFqdn())
                .replace("$otherip", context.generateRandomIpAddress())
                .replace("$seq", "" + context.getSequenceNumber());

            String changedDateLine = replaceDates (variablesChangedLine, context.getTimestamp());
            output.write(changedDateLine);
            output.write('\n');
        }
    }

    private String replaceDates (String input, Instant timestamp){
        StringBuilder stringBuilder = new StringBuilder(input.length());
        int currentIndex = 0;
        int startIndex;
        while ((currentIndex < input.length()) && (startIndex = input.indexOf("$date.get(", currentIndex)) >= 0){
            //Copy up to the date
            stringBuilder.append(input, currentIndex, startIndex);

            int formatStart = startIndex+11;
            int formatEnd = input.indexOf(')',formatStart) - 1;

            if (formatEnd <= 0) {
                throw new RuntimeException("Date format is not terminated " + input);
            }
            String format = input.substring(formatStart, formatEnd);

            //Add the date
            stringBuilder.append(DateTimeFormatter.ofPattern(format)
                            .withZone(zomeId)
                    .withLocale(getConfig().getLocale(getAppConfig())).format(timestamp));

            //Move along input for possibly more dates
            currentIndex = formatEnd + 2;
        }

        //Copy remainder of input
        if (currentIndex < input.length()) {
            stringBuilder.append(input, currentIndex, input.length() - 1);
        }

        String output = stringBuilder.toString();

        return output;
    }
}
