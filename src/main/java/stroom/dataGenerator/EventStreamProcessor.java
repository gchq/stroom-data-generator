package stroom.dataGenerator;

import stroom.dataGenerator.config.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class EventStreamProcessor {
    private final EventGenConfig appConfig;
    private final EventStreamConfig config;
    private TemplateProcessor headerProcessor = null;
    private StochasticContentProcessor contentProcessor;
    private TemplateProcessor footerProcessor = null;

    public EventStreamProcessor (final EventGenConfig appConfig, final EventStreamConfig config) throws FileNotFoundException {
        this.appConfig = appConfig;
        this.config = config;
        TemplateProcessorFactory templateProcessorFactory = new TemplateProcessorFactory(appConfig);

        if (config.getHeader() != null) {
            headerProcessor = templateProcessorFactory.createProcessor(config.getHeader(), config.getName());
        }

        if (config.getContent() != null){
            contentProcessor = new StochasticContentProcessor(appConfig, config.getContent(), config.getName());
        }

        if (config.getFooter() != null) {
            footerProcessor = templateProcessorFactory.createProcessor(config.getFooter(), config.getName());
        }
    }




}
