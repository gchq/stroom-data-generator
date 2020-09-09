package stroom.dataGenerator;

import stroom.dataGenerator.config.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class EventStreamProcessor {
    private final EventGenConfig appConfig;
    private final EventStreamConfig config;
    private TemplateProcessor headerProcessor = null;
    private StochasticContentProcessor contentProcessor;
    private TemplateProcessor footerProcessor = null;
    private final String outputDirectory;
    private final boolean createSubstreams;

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
        if  (config.getSubstreamCount() > 0){
            createSubstreams = true;
        } else {
            createSubstreams = false;
        }
    }

    public void process (String periodName, Instant startTimeInclusive, Instant endTimeExclusive, Random random) throws IOException, TemplateProcessingException {
        //Create output directory
        File dir = new File (outputDirectory);
        dir.mkdirs();

        String outputFilename;
        if (createSubstreams){
            //Create zip archive
            outputFilename = outputDirectory + "/" + periodName + config.getName().replace(' ', '_') +
                    ".zip";
        } else {
            //Create ordinary text file
            outputFilename = outputDirectory + "/" + periodName + config.getName().replace(' ', '_') +
            (config.getOutputSuffix() != null ? config.getOutputSuffix() : "");;
        }
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(outputFilename);
        } catch (FileNotFoundException ex){
            System.err.println("Error: Unable to create output file " + outputFilename);
            return;
        }

        ZipOutputStream zipOutputStream = null;
        if (createSubstreams){
            zipOutputStream = new ZipOutputStream(fileOutputStream);
        }

        int substream = 0;
        while (substream < config.getSubstreamCount()){
            substream++;

            final Writer writer;
            if (zipOutputStream != null){
                zipOutputStream.putNextEntry(new ZipEntry(String.format("%04d.dat", substream)));
                writer = createWriter(zipOutputStream);
            } else {
                writer = createWriter(fileOutputStream);
            }


            try {
                if (headerProcessor != null) {
                    headerProcessor.process(startTimeInclusive, writer);
                }
            } catch (TemplateProcessingException ex){
                System.err.println("Processing error during header creation " + config.getName());
                throw ex;
            }

            contentProcessor.process(startTimeInclusive, endTimeExclusive, writer, random);

            try {
                if (footerProcessor != null) {
                    footerProcessor.process(endTimeExclusive, writer);
                }
            } catch (TemplateProcessingException ex){
                System.err.println("Processing error during footer creation " + config.getName());
                throw ex;
            }

            if (zipOutputStream != null){
                zipOutputStream.closeEntry();
            }
        }

        if (zipOutputStream != null){
            zipOutputStream.close();
        }

        fileOutputStream.close();
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
            if ("UTF-8".equals(charset.name())){
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
