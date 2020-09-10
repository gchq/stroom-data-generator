/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package stroom.dataGenerator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import stroom.dataGenerator.config.EventGenConfig;
import stroom.dataGenerator.config.EventStreamConfig;

import javax.swing.text.DateFormatter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EventGen {

    private final EventGenConfig config;

    public EventGen(String pathToConfig) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        ;
        config = mapper.readValue(new File(pathToConfig), EventGenConfig.class);
    }

    public void go() throws IOException, TemplateProcessingException {

        List<EventStreamProcessor> streamProcessors = new ArrayList<>();
        for (EventStreamConfig streamConfig : config.getStreams()) {
            streamProcessors.add(new EventStreamProcessor(config, streamConfig));
        }

        //Calculate number of batches.  Allow additional one for partial periods.
        boolean exactFit = (config.getRunLength().getSeconds() % config.getBatchDuration().getSeconds() == 0 &&
                config.getRunLength().getNano() == 0 && config.getBatchDuration().getNano() == 0);
        long periodCount = config.getRunLength().dividedBy(config.getBatchDuration())
                + (exactFit ? 0 : 1);
        System.out.println ("Generating " + periodCount + " periods of data");
        Stream.iterate(config.getStartTime(), (periodStart) -> periodStart.plus(config.getBatchDuration())).parallel()
                .limit(periodCount).
                forEach((periodStart) -> {
                    try {
                        processTimePeriod(periodStart, streamProcessors);
                    } catch (TemplateProcessingException | IOException ex) {
                        ex.printStackTrace();
                        throw new RuntimeException("Failed to process", ex);
                    }

                });

    }

    public void processTimePeriod(Instant startTime, List<EventStreamProcessor> processors) throws
            IOException, TemplateProcessingException {
        final String periodName = DateTimeFormatter.ISO_INSTANT.format(startTime);
        for (EventStreamProcessor streamProcessor : processors) {
            streamProcessor.process(periodName, startTime, startTime.plus(config.getBatchDuration()));
        }

    }


    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Please provide path to config YAML as application parameter");
        } else {
            try {
                EventGen app = new EventGen(args[0]);
                app.go();
            } catch (Exception ex) {
                System.err.println("Fatal Error due to " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
}
