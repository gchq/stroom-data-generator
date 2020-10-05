/*
 * Copyright 2020 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package stroom.datagenerator;

import stroom.datagenerator.config.EventGenConfig;
import stroom.datagenerator.config.StochasticTemplateConfig;
import stroom.datagenerator.config.TemplateConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Writer;
import java.time.Instant;
import java.util.*;

public class StochasticContentProcessor {
    private List <StochasticTemplateProcessor> contentProcessors;
    private TemplateProcessor betweenEventProcessor;
    private final String streamName;
    private final long minMsBetweenEvents;

    public StochasticContentProcessor(EventGenConfig appConfig, List<StochasticTemplateConfig> contentConfig, TemplateConfig betweenEventConfig,
                                      String streamName, long minMsBetweenEvents) throws FileNotFoundException {
        this.streamName = streamName;
        TemplateProcessorFactory templateProcessorFactory = new TemplateProcessorFactory(appConfig);
        contentProcessors = new ArrayList<>();
        for (StochasticTemplateConfig config : contentConfig){
            String path = appConfig.getTemplateRoot() + "/"  + config.getTemplate().getPath();
            File templateFile = new File(path);
            if (!templateFile.exists()){
                throw new FileNotFoundException("Unable to locate template file/directory " + templateFile);
            }
            if (templateFile.isDirectory()){
                File[] templates = templateFile.listFiles();
                Arrays.sort(templates, Comparator.comparing(File::getName));
                int fileCount = templates.length;

                double averageRate = config.getAverageCountPerSecond() / fileCount;
                double minRate = averageRate / 10.0;

                if (fileCount == 0){
                    throw new FileNotFoundException("Template directory " + templateFile + " is empty - no templates found");
                }
                for (int i = 1; i <= fileCount; i++) {
                    //Create a processor for each file in the folder
                    File template = templates[i - 1];

                    if (!template.isDirectory()) {
                        double rate = minRate + 2 * ((averageRate - minRate) * i / (double) fileCount);

                        String thisTemplatePath = template.getPath().substring(appConfig.getTemplateRoot().length() + 1);
                        TemplateConfig templateConfig = new TemplateConfig(config.getTemplate(), thisTemplatePath);

                        StochasticTemplateConfig fileConfig = new StochasticTemplateConfig(templateConfig, rate);
                        contentProcessors.add(new StochasticTemplateProcessor(templateProcessorFactory, streamName, fileConfig));
                    }
                }
//                System.out.println ("Stream " + streamName + " will have a range of event rates totalling " + cummulativeRate + " events per second");
            } else {
                contentProcessors.add(new StochasticTemplateProcessor(templateProcessorFactory, streamName, config));
            }
        }
        if (betweenEventConfig != null){
            betweenEventProcessor = templateProcessorFactory.createProcessor(betweenEventConfig, streamName);
        }

        this.minMsBetweenEvents = minMsBetweenEvents;
    }

    public ProcessingContext process (ProcessingContext initialContext, Instant endTime, Writer output) {
        ProcessingContext context = initialContext;
        Instant currentTime = context.getTimestamp();
        boolean firstEvent = true;
        while (currentTime.isBefore(endTime)){
            Map<Long, StochasticTemplateProcessor> nextEventTimes = new HashMap<>();
            for (StochasticTemplateProcessor processor : contentProcessors){
                long msUntilMinTimeElapsed = minMsBetweenEvents;

                long msUntilNextEvent = 0;
                while (msUntilNextEvent <= msUntilMinTimeElapsed){
                    msUntilNextEvent += processor.nextEventAfterMs(initialContext.getRandom().nextDouble());
                }

                nextEventTimes.put(msUntilNextEvent, processor);
            }
            Long shortestInterval = nextEventTimes.keySet().iterator().next();
            for (Long delay : nextEventTimes.keySet()){
                if (delay < shortestInterval){
                    shortestInterval = delay;
                }
            }

            StochasticTemplateProcessor processor = nextEventTimes.get(shortestInterval);
            currentTime = Instant.ofEpochMilli(currentTime.toEpochMilli() + shortestInterval);
            context = new ProcessingContext(context, currentTime);
            try {
                if (!firstEvent && betweenEventProcessor != null){
                    betweenEventProcessor.process(context, output);
                }
                firstEvent = false;
                processor.process(context, output);
            } catch (TemplateProcessingException ex){
                System.err.println("Processing error encountered in " + streamName + " : " + ex.getMessage());
                ex.printStackTrace();
                System.err.println("Not using this template for further processing");
                contentProcessors.remove(processor);
            }
        }
        return context;
    }
}
