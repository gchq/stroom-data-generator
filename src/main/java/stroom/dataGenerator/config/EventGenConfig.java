package stroom.dataGenerator.config;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.List;

public class EventGenConfig {
    private final Instant startTime;
    private final Duration runLength;
    private final String templateRoot;
    private final String outputRoot;
    private final String defaultTemplateFormat;
    private final String defaultTimeZoneId;
    private final String defaultLocaleId;
    private final String defaultFileEncoding;
    private final List<EventStreamConfig> streams;
    private final Duration batchDuration;

    public EventGenConfig(){
        startTime = null;
        runLength = null;
        templateRoot = null;
        outputRoot = null;
        defaultTemplateFormat = null;
        defaultTimeZoneId = null;
        defaultLocaleId = null;
        defaultFileEncoding = null;
        streams = null;
        batchDuration = null;
    }

    public EventGenConfig(Instant startTime, Duration runLength, int seed, final String templateRoot,
                          final String outputRoot,
                          final String defaultTemplateFormat,
                          final Duration batchDuration,
                          final String defaultTimezone, final String defaultLocale,
                          final String defaultFileEncoding,
                          List<EventStreamConfig> streams) {
        this.startTime = startTime;
        this.runLength = runLength;
        this.templateRoot = templateRoot;
        this.outputRoot = outputRoot;
        this.defaultTemplateFormat = defaultTemplateFormat;
        this.defaultFileEncoding = defaultFileEncoding;
        this.batchDuration = batchDuration;
        if (defaultTimezone != null) {
            this.defaultTimeZoneId = defaultTimezone;
        } else {
            this.defaultTimeZoneId = "UTC";
        }
        this.defaultLocaleId = defaultLocale;
        this.streams = streams;
    }

    public Duration getRunLength() {
        return runLength;
    }

    public List<EventStreamConfig> getStreams() {
        return streams;
    }

    public String getTemplateRoot() {
        return templateRoot;
    }

    public String getDefaultTemplateFormat() {
        return defaultTemplateFormat;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public String getDefaultTimeZoneId() {
        return defaultTimeZoneId;
    }

    public String getDefaultLocaleId() {
        return defaultLocaleId;
    }

    public String getOutputRoot() {
        return outputRoot;
    }

    public String getDefaultFileEncoding() {
        return defaultFileEncoding;
    }

    public TemporalAmount getBatchDuration() {
        return batchDuration;
    }
}
