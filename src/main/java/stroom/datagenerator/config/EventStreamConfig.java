package stroom.datagenerator.config;

import java.util.List;

public class EventStreamConfig {
    private final String name;
    private final Integer substreamCount;
    private final TemplateConfig preEvents;
    private final List<StochasticTemplateConfig> events;
    private final TemplateConfig betweenEvents;
    private final TemplateConfig postEvents;
    private final String fileEncoding;
    private final String completionCommand;
    private final String outputDirectory;
    private final String outputSuffix;
    private final String feed;

    EventStreamConfig(){
        betweenEvents = null;
        name = null;
        substreamCount = null;
        preEvents = null;
        events = null;
        postEvents = null;
        fileEncoding = null;
        completionCommand = null;
        outputDirectory = null;
        outputSuffix = null;
        feed = null;
    }

    public EventStreamConfig(String name, int substreamCount, TemplateConfig preEvents,
                             TemplateConfig betweenEvents, String outputPath,
                             String outputSuffix,
                             List<StochasticTemplateConfig> events,
                             TemplateConfig postEvents, String fileEncoding, String completionCommand,
                             String feed) {
        this.name = name;
        this.substreamCount = substreamCount;
        this.betweenEvents = betweenEvents;
        this.outputDirectory = outputPath;
        this.outputSuffix = outputSuffix;
        this.preEvents = preEvents;
        this.events = events;
        this.postEvents = postEvents;
        this.fileEncoding = fileEncoding;
        this.completionCommand = completionCommand;
        this.feed = feed;
    }

    public String getName() {
        return name;
    }

    public int getSubstreamCount(EventGenConfig appConfig) {
        if (substreamCount == null) {
            return appConfig.getDefaultSubstreamCount();
        } else {
            return substreamCount;
        }
    }

    public TemplateConfig getPreEvents() {
        return preEvents;
    }

    public List<StochasticTemplateConfig> getEvents() {
        return events;
    }

    public TemplateConfig getPostEvents() {
        return postEvents;
    }

    public String getFileEncoding() {
        return fileEncoding;
    }

    public String getCompletionCommand() {
        return completionCommand;
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public String getOutputSuffix() {
        return outputSuffix;
    }

    public TemplateConfig getBetweenEvents() {
        return betweenEvents;
    }

    public String getFeed() {
        return feed;
    }

    public boolean isZipRequired (EventGenConfig appConfig){
        return getSubstreamCount(appConfig) > 0;
    }
}
