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

import stroom.datagenerator.config.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class EventStreamProcessor {
    private static final String ADDITIONAL_FILES_CHARSET = "US-ASCII";

    private static String META_LITERAL = "Meta";
    private static String CONTENXT_LITERAL = "Context";
    private static String tempSuffix = "-incomplete";
    private final EventGenConfig appConfig;
    private final EventStreamConfig config;
    private final TemplateProcessor headerProcessor;
    private final StochasticContentProcessor contentProcessor;
    private final TemplateProcessor footerProcessor;
    private final String outputDirectory;
    private final boolean createSubstreams;
    private final List<TemplateProcessor> additionalIncludes;
    private final List<String> allHosts;

    public EventStreamProcessor (final EventGenConfig appConfig, final EventStreamConfig config) throws FileNotFoundException {
        this.appConfig = appConfig;
        this.config = config;
        TemplateProcessorFactory templateProcessorFactory = new TemplateProcessorFactory(appConfig);

        if (config.getPreEvents() != null) {
            headerProcessor = templateProcessorFactory.createProcessor(config.getPreEvents(), config.getName());
        } else {
            headerProcessor = null;
        }

        if (config.getEvents() != null){
            contentProcessor = new StochasticContentProcessor(appConfig, config.getEvents(),
                    config.getBetweenEvents(), config.getName());
        } else {
            contentProcessor = null;
        }

        if (config.getPostEvents() != null) {
            footerProcessor = templateProcessorFactory.createProcessor(config.getPostEvents(), config.getName());
        } else {
            footerProcessor = null;
        }

        additionalIncludes = new ArrayList<>();

        if (config.getInclude() != null){
            for (TemplateConfig includedConfig : config.getInclude()){
               additionalIncludes.add(templateProcessorFactory.createProcessor(includedConfig, config.getName()));
            }
        }

        if (config.getSubstreamCount() != null && appConfig.getHostCount() < config.getSubstreamCount()){
            allHosts = IntStream.range(1,config.getSubstreamCount() + 1).mapToObj(u -> "host" + u).
                    collect(Collectors.toList());
        } else {
            allHosts = null;
        }


        if (config.getOutputDirectory() != null) {
            if (config.getOutputDirectory().startsWith("/")){
                outputDirectory = config.getOutputDirectory();
            } else if (appConfig.getOutputRoot() != null){
                outputDirectory = appConfig.getOutputRoot() + "/" + config.getOutputDirectory();
            } else {
                outputDirectory = "./" + config.getOutputDirectory();
            }
        } else if (appConfig.getOutputRoot() != null) {
            outputDirectory = appConfig.getOutputRoot();
        } else {
            outputDirectory = ".";
        }
        if  (config.isZipRequired(appConfig)){
            createSubstreams = true;
        } else {
            createSubstreams = false;
        }
    }

    public void process (String periodName, final Collection<String> allDefaultHosts, Collection<String> allUsers,
                         Instant startTimeInclusive, Instant endTimeExclusive) throws IOException, TemplateProcessingException {
        //Create output directory
        File dir = new File (outputDirectory);
        dir.mkdirs();

        String outputFilename;
        if (createSubstreams){
            //Create zip archive
            outputFilename = outputDirectory + "/" + ((config.getFeed()!=null)?config.getFeed() : "UNDEFINED-FEED") +
                    '.' + periodName + "-" + config.getName().replace(' ', '_') +
                    ".zip";
        } else {
            //Create ordinary text file
            outputFilename = outputDirectory + "/" + ((config.getFeed()!=null)?config.getFeed() : "UNDEFINED-FEED") +
                    '.' + periodName + "-" + config.getName().replace(' ', '_') +
                        (config.getOutputSuffix() != null ? config.getOutputSuffix() : "");
        }
        String tempOutputfilename = outputFilename + tempSuffix;

        File tempFile = new File (tempOutputfilename);
        if (tempFile.exists()){
            throw new IOException("Error: File with temporary name " + tempOutputfilename + " already exists.  " +
                    "This can be due to two EventGen processes colliding.  Either wait for other process to complete " +
                    "or delete this file.");
        }
        File outputFile = new File (outputFilename);

        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(tempOutputfilename);
        } catch (FileNotFoundException ex){
            throw new IOException("Error: Unable to create output file using temporary name " + tempOutputfilename, ex);
        }

        ZipOutputStream zipOutputStream = null;
        if (createSubstreams){
            zipOutputStream = new ZipOutputStream(fileOutputStream);
        }

        int substreamCount = config.getSubstreamCount(appConfig);

        int substream = 0;
        do {
            substream++;

            final Writer writer;
            if (zipOutputStream != null){
                zipOutputStream.putNextEntry(new ZipEntry(String.format("%04d", substream) +
                        findExtension (null)));
                writer = createWriter(zipOutputStream);
            } else {
                writer = createWriter(fileOutputStream);
            }

            ProcessingContext context =
                    new ProcessingContext(startTimeInclusive, allUsers,
                            allHosts != null ? allHosts : allDefaultHosts,
                            substream,
                            generateUserNumber(startTimeInclusive.toEpochMilli() + substream),
                            appConfig.getDomain());
            try {
                if (headerProcessor != null) {

                    headerProcessor.process(context, writer);
                }
            } catch (TemplateProcessingException ex){
                throw new TemplateProcessingException(config.getName(), config.getPreEvents().getPath(), "Processing error during header creation" , ex);
            }

            if (contentProcessor != null) {
                context = contentProcessor.process(context, endTimeExclusive, writer);
            }


            try {
                if (footerProcessor != null) {
                    footerProcessor.process(context, writer);
                }
            } catch (TemplateProcessingException ex){
                throw new TemplateProcessingException(config.getName(), config.getPostEvents().getPath(), "Processing error during footer creation" , ex);
            }

            writer.flush();
            if (zipOutputStream != null){
                zipOutputStream.closeEntry();
            }

            //Now add any additional files that must be generated with the events
            for (TemplateProcessor additionalProcessor : additionalIncludes){
                zipOutputStream.putNextEntry(new ZipEntry(String.format("%04d", substream) +
                        findExtension (additionalProcessor)));
                Writer additionalStreamTypeWriter =  new OutputStreamWriter(zipOutputStream,
                        Charset.forName(ADDITIONAL_FILES_CHARSET).newEncoder());
                try {
                    additionalProcessor.process(context, additionalStreamTypeWriter);

                    additionalStreamTypeWriter.flush();
                } catch (UnsupportedEncodingException ex) {
                    throw new TemplateProcessingException(config.getName(), additionalProcessor.getConfig().getPath(),
                            "Context/Meta files must be " + ADDITIONAL_FILES_CHARSET + " encoded", ex);
                }
                zipOutputStream.closeEntry();
            }

        } while (substream < substreamCount);

        if (zipOutputStream != null){
            zipOutputStream.close();
        }

        fileOutputStream.close();

        //Rename file back to what it should be called.
        if (!tempFile.renameTo(outputFile)){
            throw new IOException("Error: Unable to rename temp output file " + tempOutputfilename + " to " + outputFilename);

        }

        if (config.getCompletionCommand() != null){
            //todo implement
            System.out.println("Would exectute: " + config.getCompletionCommand() + " " + outputFilename);
        }
    }

    private int generateUserNumber(long seed) {
        //Simple way to get a unequal distribution of events per user
        return (int) (appConfig.getUserCount() - Math.sqrt(new Random(seed).nextDouble()) *
                appConfig.getUserCount());
    }

    private String findExtension(TemplateProcessor processor){
        if (processor == null || processor.getConfig() == null || processor.getConfig().getType() == null){
            return ".dat";
        } else if ("Meta".equals(processor.getConfig().getType())) {
            return ".meta";
        } else if ("Context".equals(processor.getConfig().getType())) {
            return ".ctx";
        } else {
            throw new IllegalArgumentException("Unknown additional stream type " + processor.getConfig().getType() +
                    " Must be Meta or Context");
        }
    }

    private Writer createWriter(OutputStream outputStream) throws IOException {
        final OutputStreamWriter writer;

        String fileEncoding = config.getFileEncoding();
        if (fileEncoding == null){
            fileEncoding = appConfig.getDefaultFileEncoding();
        }

        if (fileEncoding != null) {
            final Charset charset = Charset.forName(fileEncoding);

            final byte[] bom;
            if ("US-ASCII".equals(charset.name())){
                bom = new byte[0];
            }
            else if ("UTF-8".equals(charset.name())){
                bom = new byte []{(byte)239, (byte)187, (byte)191};
            } else if ("UTF-16BE".equals(charset.name())) {
                bom = new byte[]{(byte)254, (byte)255};
            } else if ("UTF-16LE".equals(charset.name())) {
                bom = new byte[]{(byte)255, (byte)254};
            } else if ("UTF-32BE".equals(charset.name())) {
                bom = new byte[]{(byte)0, (byte)0, (byte)254, (byte)255};
            } else if ("UTF-32LE".equals(charset.name())) {
                bom = new byte[]{(byte)0, (byte)0, (byte)255, (byte)254};
            } else {
                throw new IOException("Unrecognised BOM charset " + charset.name() + " specified in config for " +
                        config.getName());
            }

            if (bom != null){
                outputStream.write(bom);
                outputStream.flush();
            }

            writer = new OutputStreamWriter(outputStream, charset.newEncoder());
        } else {
            writer = new OutputStreamWriter(outputStream,
                    StandardCharsets.US_ASCII.newEncoder());
        }
        return writer;
    }

}
