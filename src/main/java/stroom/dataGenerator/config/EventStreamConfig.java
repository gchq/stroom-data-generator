package stroom.dataGenerator.config;

import java.util.List;

public class EventStreamConfig {
    private final String name;
    private final int substreamCount;
    private final TemplateConfig header;
    private final List<StochasticTemplateConfig> content;
    private final TemplateConfig footer;
    private final String fileEncoding;
    private final String completionCommand;

    public EventStreamConfig(String name, int substreamCount, TemplateConfig header,
                             List<StochasticTemplateConfig> content,
                             TemplateConfig footer, String fileEncoding, String completionCommand) {
        this.name = name;
        this.substreamCount = substreamCount;
        this.header = header;
        this.content = content;
        this.footer = footer;
        this.fileEncoding = fileEncoding;
        this.completionCommand = completionCommand;
    }

    public String getName() {
        return name;
    }

    public int getSubstreamCount() {
        return substreamCount;
    }

    public TemplateConfig getHeader() {
        return header;
    }

    public List<StochasticTemplateConfig> getContent() {
        return content;
    }

    public TemplateConfig getFooter() {
        return footer;
    }

    public String getFileEncoding() {
        return fileEncoding;
    }

    public String getCompletionCommand() {
        return completionCommand;
    }
}
