package stroom.dataGenerator.config;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class EventGenConfig {
    private final Instant startTime;
    private final Duration runLength;
    private final int seed;
    private final String templateRoot;
    private final String defaultTemplateFormat;
    private final List<EventStreamConfig> streams;

    public EventGenConfig(Instant startTime, Duration runLength, int seed, String templateRoot,
                          String defaultTemplateFormat,
                          List<EventStreamConfig> streams) {
        this.startTime = startTime;
        this.runLength = runLength;
        this.seed = seed;
        this.templateRoot = templateRoot;
        this.defaultTemplateFormat = defaultTemplateFormat;
        this.streams = streams;
    }

    public Duration getRunLength() {
        return runLength;
    }

    public int getSeed() {
        return seed;
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
}
