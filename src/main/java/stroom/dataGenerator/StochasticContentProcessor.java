package stroom.dataGenerator;

import stroom.dataGenerator.StochasticTemplateProcessor;
import stroom.dataGenerator.TemplateProcessingException;
import stroom.dataGenerator.TemplateProcessorFactory;
import stroom.dataGenerator.config.EventGenConfig;
import stroom.dataGenerator.config.StochasticTemplateConfig;

import java.io.FileNotFoundException;
import java.io.Writer;
import java.time.Instant;
import java.util.*;

public class StochasticContentProcessor {
    private List <StochasticTemplateProcessor> contentProcessors;
    private final String streamName;
    public StochasticContentProcessor(EventGenConfig appConfig, List<StochasticTemplateConfig> contentConfig, String streamName) throws FileNotFoundException {
        this.streamName = streamName;
        TemplateProcessorFactory templateProcessorFactory = new TemplateProcessorFactory(appConfig);
        contentProcessors = new ArrayList<>();
        for (StochasticTemplateConfig config : contentConfig){
            contentProcessors.add ( new StochasticTemplateProcessor(templateProcessorFactory, streamName, config));
        }
    }

    public void process (Instant startTime, Instant endTime, Writer output, Random random) {

        Instant currentTime = startTime;

        while (currentTime.isBefore(endTime)){
            Map<Long, StochasticTemplateProcessor> nextEventTimes = new HashMap<>();
            for (StochasticTemplateProcessor processor : contentProcessors){
                nextEventTimes.put(processor.nextEventAfterMs(random), processor);
            }
            Long shortestInterval = nextEventTimes.keySet().iterator().next();
            for (Long delay : nextEventTimes.keySet()){
                if (delay < shortestInterval){
                    shortestInterval = delay;
                }
            }

            StochasticTemplateProcessor processor = nextEventTimes.get(shortestInterval);
            currentTime = Instant.ofEpochMilli(currentTime.toEpochMilli() + shortestInterval);

            try {
                processor.process(currentTime, output);
            } catch (TemplateProcessingException ex){
                System.err.println("Processing error encountered in " + streamName + " : " + ex.getMessage());
                ex.printStackTrace();
                System.err.println("Not using this template for further processing");
                contentProcessors.remove(processor);
            }
        }

    }
}
