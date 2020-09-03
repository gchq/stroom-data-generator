package stroom.dataGenerator.config;

import java.util.List;

public class EventStreamConfig {
    private String name;
    private int substreamCount;
    private TemplateConfig header;
    private List<StochasticTemplateConfig> content;
    private TemplateConfig footer;
    private String fileEncoding;
    private String completionCommand;
}
