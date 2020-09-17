package stroom.datagenerator;

import stroom.datagenerator.config.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class EventStreamProcessor {
    private static String tempSuffix = "-incomplete";
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

        if (config.getPreEvents() != null) {
            headerProcessor = templateProcessorFactory.createProcessor(config.getPreEvents(), config.getName());
        }

        if (config.getEvents() != null){
            contentProcessor = new StochasticContentProcessor(appConfig, config.getEvents(),
                    config.getBetweenEvents(), config.getName());
        }

        if (config.getPostEvents() != null) {
            footerProcessor = templateProcessorFactory.createProcessor(config.getPostEvents(), config.getName());
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
        if  (config.getSubstreamCount() > 0 || appConfig.getDefaultSubstreamCount() > 0){
            createSubstreams = true;
        } else {
            createSubstreams = false;
        }
    }

    public void process (String periodName, Instant startTimeInclusive, Instant endTimeExclusive) throws IOException, TemplateProcessingException {
        //Create output directory
        File dir = new File (outputDirectory);
        dir.mkdirs();

        String outputFilename;
        if (createSubstreams){
            //Create zip archive
            outputFilename = outputDirectory + "/" + periodName + "-" + config.getName().replace(' ', '_') +
                    ".zip";
        } else {
            //Create ordinary text file
            outputFilename = outputDirectory + "/" + periodName + "-" + config.getName().replace(' ', '_') +
            (config.getOutputSuffix() != null ? config.getOutputSuffix() : "");;
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

        int substreamCount = appConfig.getDefaultSubstreamCount();
        if (config.getSubstreamCount() > 0) {
            substreamCount = config.getSubstreamCount();
        }
        int substream = 0;
        do {
            substream++;

            final Writer writer;
            if (zipOutputStream != null){
                zipOutputStream.putNextEntry(new ZipEntry(String.format("%04d.dat", substream)));
                writer = createWriter(zipOutputStream);
            } else {
                writer = createWriter(fileOutputStream);
            }

            ProcessingContext context =
                    new ProcessingContext(startTimeInclusive, substream,
                            generateUserNumber(startTimeInclusive.toEpochMilli() + substream), appConfig.getDomain());
            try {
                if (headerProcessor != null) {

                    headerProcessor.process(context, writer);
                }
            } catch (TemplateProcessingException ex){
                System.err.println("Processing error during header creation " + config.getName());
                throw ex;
            }

            if (contentProcessor != null) {
                context = contentProcessor.process(context, endTimeExclusive, writer);
            }


            try {
                if (footerProcessor != null) {
                    footerProcessor.process(context, writer);
                }
            } catch (TemplateProcessingException ex){
                System.err.println("Processing error during footer creation " + config.getName());
                throw ex;
            }

            writer.flush();
            if (zipOutputStream != null){
                zipOutputStream.closeEntry();
            }
        } while (substream < substreamCount);

        if (zipOutputStream != null){
            zipOutputStream.close();
        }

        fileOutputStream.close();

        //Rename file back to what it should be called.
        if (!tempFile.renameTo(outputFile)){
            System.err.println("Error: Unable to rename temp output file " + tempOutputfilename + " to " + outputFilename);
            return;
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
