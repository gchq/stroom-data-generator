package stroom.dataGenerator.config;

import stroom.dataGenerator.StochasticTemplateProcessor;
import stroom.dataGenerator.TemplateProcessingException;
import stroom.dataGenerator.TemplateProcessorFactory;

import java.io.FileNotFoundException;
import java.io.Writer;
import java.time.Instant;
import java.util.*;

public class StochasticContentProcessor {
    private List <StochasticTemplateProcessor> contentProcessors;
    public StochasticContentProcessor(EventGenConfig appConfig, List<StochasticTemplateConfig> contentConfig, String streamName) throws FileNotFoundException {
        TemplateProcessorFactory templateProcessorFactory = new TemplateProcessorFactory(appConfig);
        contentProcessors = new ArrayList<>();
        for (StochasticTemplateConfig config : contentConfig){
            contentProcessors.add ( new StochasticTemplateProcessor(templateProcessorFactory, streamName, config));
        }
    }

    public void process (Instant startTime, Instant endTime, Writer output, Random random) throws TemplateProcessingException {

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

            processor.process(currentTime, output);
        }

    }
}
