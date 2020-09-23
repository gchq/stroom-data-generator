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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.cli.*;
import stroom.datagenerator.config.EventGenConfig;
import stroom.datagenerator.config.EventStreamConfig;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class EventGen {

    private final EventGenConfig config;

    public EventGen(final EventGenConfig config) throws IOException {
        this.config = config;
    }

    public static EventGenConfig readConfig (String pathToConfig) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        return mapper.readValue(new File(pathToConfig), EventGenConfig.class);
    }

    public void go(boolean quiet) throws IOException, TemplateProcessingException {

        List<EventStreamProcessor> streamProcessors = new ArrayList<>();
        for (EventStreamConfig streamConfig : config.getStreams()) {
            streamProcessors.add(new EventStreamProcessor(config, streamConfig));
        }

        //Calculate number of batches.  Allow additional one for partial periods.
        boolean exactFit = (config.getRunLength().getSeconds() % config.getBatchDuration().getSeconds() == 0 &&
                config.getRunLength().getNano() == 0 && config.getBatchDuration().getNano() == 0);
        long periodCount = config.getRunLength().dividedBy(config.getBatchDuration())
                + (exactFit ? 0 : 1);

        if (!quiet) {
            System.out.println("Generating " + periodCount + " periods of data");
        }

        final List<String> allUsers = IntStream.range(1,config.getUserCount() + 1).mapToObj(u -> "user" + u).
                collect(Collectors.toList());
        final List<String> allHosts = IntStream.range(1,config.getHostCount() + 1).mapToObj(u -> "host" + u).
                collect(Collectors.toList());

        Stream.iterate(config.getStartTime(), (periodStart) -> periodStart.plus(config.getBatchDuration())).parallel()
                .limit(periodCount).
                forEach((periodStart) -> {
                    try {
                        processTimePeriod(allHosts, allUsers, periodStart, streamProcessors);
                    } catch (TemplateProcessingException | IOException ex) {
                        if (!quiet){
                            ex.printStackTrace();
                        }
                        throw new RuntimeException("Failed to process", ex);
                    }

                });

    }

    private void processTimePeriod(final Collection<String> allHosts, final Collection<String> allUsers,
                                   Instant startTime, List<EventStreamProcessor> processors) throws
            IOException, TemplateProcessingException {
        final String periodName = DateTimeFormatter.ISO_INSTANT.format(startTime);
        for (EventStreamProcessor streamProcessor : processors) {
            streamProcessor.process(periodName, allHosts, allUsers, startTime, startTime.plus(config.getBatchDuration()));
        }

    }


    public static void main(String[] args) {
        Options options = new Options();
        options.addOption(new Option ("?","help", false, "Show usage instructions / this message, then exit"));
        options.addOption(new Option ("q","quiet", false, "Suppress console output"));
        options.addOption(new Option ("p", "period", true, "Start Time, e.g. 2020-01-01T00:00:00.000Z"));
        options.addOption(new Option ("r","run", true, "Run length (time period), e.g. P30D"));
        options.addOption(new Option ("b","batch", true, "Batch size (time period), e.g. PT10M"));
        options.addOption(new Option ("d","domain", true, "Domain to use for host FQDN e.g. org.mydomain"));
        options.addOption(new Option ("o","output", true, "Output directory"));
        options.addOption(new Option ("t","templates", true, "Template directory"));
        options.addOption(new Option ("h", "hosts", true, "Number of hosts"));
        options.addOption(new Option ("u","users", true, "Number of users"));
        options.addOption(new Option ("e","encoding", true, "Default output file encoding"));
        options.addOption(new Option ("s","substreams", true, "Default number of substreams per batch"));
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine commands = parser.parse(options, args);
            boolean quietMode = commands.hasOption('q');
            String startTimeStr = commands.getOptionValue("p");
            Instant startTime = null;
            String runPeriodStr = commands.getOptionValue("r");
            Duration runLength = null;
            String batchPeriodStr = commands.getOptionValue("b");
            Duration batchDuration = null;
            String outputDir = commands.getOptionValue("o");
            String domain = commands.getOptionValue("d");
            String templateDir = commands.getOptionValue("t");
            String userCountStr = commands.getOptionValue("u");
            Integer userCount = null;
            String substreamCountStr = commands.getOptionValue("s");
            Integer substreamCount = null;
            String hostCountStr = commands.getOptionValue("h");
            Integer hostCount = null;
            if (commands.hasOption('?')){
                System.err.println ("Help currently limited to usage information...");
                new HelpFormatter().printHelp("java " + EventGen.class.getName() + " <config yaml file>", options );
                System.exit(0);
            }
            if (userCountStr != null){
                try {
                    userCount = Integer.parseInt(userCountStr);
                } catch (NumberFormatException ex){
                    System.err.println("User parameter requires a numeric value (number of users). Got " + userCountStr);
                    new HelpFormatter().printHelp("java " + EventGen.class.getName() + " <config yaml file>", options );
                    System.exit(1);
                }
            }
            if (hostCountStr != null){
                try {
                    hostCount = Integer.parseInt(hostCountStr);
                } catch (NumberFormatException ex){
                    System.err.println("Host parameter requires a numeric value (number of hosts). Got " + hostCountStr);
                    new HelpFormatter().printHelp("java " + EventGen.class.getName() + " <config yaml file>", options );
                    System.exit(1);
                }
            }
            if (substreamCountStr != null){
                try {
                    substreamCount = Integer.parseInt(substreamCountStr);
                } catch (NumberFormatException ex){
                    System.err.println("Substream count parameter requires a numeric value (number of substreams). Got " + substreamCountStr);
                    new HelpFormatter().printHelp("java " + EventGen.class.getName() + " <config yaml file>", options );
                    System.exit(1);
                }
            }
            if (startTimeStr != null){
                try {
                    startTime = Instant.parse(startTimeStr);
                } catch (DateTimeParseException ex){
                    System.err.println("Start time parameter requires a date time value. Got " + startTimeStr);
                    new HelpFormatter().printHelp("java " + EventGen.class.getName() + " <config yaml file>", options );
                    System.exit(1);
                }
            }
            if (runPeriodStr != null){
                try {
                    runLength = Duration.parse(runPeriodStr);
                } catch (DateTimeParseException ex){
                    System.err.println("Run length parameter requires a duration value. Got " + runPeriodStr );
                    new HelpFormatter().printHelp("java " + EventGen.class.getName() + " <config yaml file>", options );
                    System.exit(1);
                }
            }
            if (batchPeriodStr != null){
                try {
                    batchDuration = Duration.parse(batchPeriodStr);
                } catch (DateTimeParseException ex){
                    System.err.println("Batch size parameter requires a duration value");
                    System.exit(1);
                }
            }

            if (commands.getArgs().length == 1){
                if(!quietMode){
                    System.out.println("Initialising from " + commands.getArgs()[0]);
                }
                EventGenConfig ymlconfig = readConfig(commands.getArgs()[0]);

                EventGenConfig config = new EventGenConfig(ymlconfig, startTime, runLength, batchDuration,
                        templateDir, outputDir, domain, userCount, hostCount, substreamCount);

                EventGen app = new EventGen(config);
                if (!quietMode) {
                    System.out.println("Starting event generation... ");
                }
                app.go(quietMode);
            } else {
                new HelpFormatter().printHelp("java " + EventGen.class.getName() + " <config yaml file>", options );
                System.err.println("Please provide path to config YAML as application parameter");
                System.exit(1);
            }

        } catch (ParseException ex) {
            new HelpFormatter().printHelp("java " + EventGen.class.getName() + " <config yaml file>", options );
            System.exit(1);
        } catch (Exception ex) {
            System.err.println("Fatal Error due to " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }

    }
}
