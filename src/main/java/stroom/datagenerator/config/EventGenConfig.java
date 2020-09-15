package stroom.datagenerator.config;

import java.time.Duration;
import java.time.Instant;
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
    private String domain;
    private int userCount;
    private int defaultSubstreamCount;

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
        domain = null;
        userCount = 0;
        defaultSubstreamCount = 0;
    }

    public EventGenConfig(EventGenConfig baseConfig, Instant startTime, Duration runLength,
                          Duration batchDuration,
                          String templateRoot, String outputRoot, String domain, Integer userCount, Integer substreamCount){
        if (startTime != null)
            this.startTime = startTime;
        else
            this.startTime = baseConfig.startTime;

        if (runLength != null)
            this.runLength = runLength;
        else
            this.runLength = baseConfig.runLength;

        if (batchDuration != null)
            this.batchDuration = batchDuration;
        else
            this.batchDuration = baseConfig.batchDuration;

        if (templateRoot != null)
            this.templateRoot = templateRoot;
        else
            this.templateRoot = baseConfig.templateRoot;

        if (outputRoot != null)
            this.outputRoot = outputRoot;
        else
            this.outputRoot = baseConfig.outputRoot;

        if (userCount != null)
            this.userCount = userCount;
        else
            this.userCount = baseConfig.userCount;

        if (substreamCount != null)
            this.defaultSubstreamCount = substreamCount;
        else
            this.defaultSubstreamCount = baseConfig.defaultSubstreamCount;

        if (domain != null)
            this.domain = domain;
        else
            this.domain = baseConfig.domain;

        this.defaultTemplateFormat = baseConfig.defaultTemplateFormat;
        this.defaultTimeZoneId = baseConfig.defaultTimeZoneId;
        this.defaultLocaleId = baseConfig.defaultLocaleId;
        this.defaultFileEncoding = baseConfig.defaultFileEncoding;

        this.streams = baseConfig.streams;
    }

    public EventGenConfig(Instant startTime, Duration runLength, int seed, final String templateRoot,
                          final String outputRoot,
                          final String defaultTemplateFormat,
                          final Duration batchDuration,
                          final String defaultTimezone, final String defaultLocale,
                          final String defaultFileEncoding,
                          final String domain,
                          final int userCount,
                          final int defaultSubstreamCount,
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
        this.domain = domain;
        this.userCount = userCount;
        this.defaultSubstreamCount = defaultSubstreamCount;
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

    public Duration getBatchDuration() {
        return batchDuration;
    }

    public int getUserCount() {
        return userCount;
    }

    public int getDefaultSubstreamCount() {
        return defaultSubstreamCount;
    }

    public String getDomain() {
        return domain;
    }
}
