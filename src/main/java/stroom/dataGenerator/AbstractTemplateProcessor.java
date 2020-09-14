package stroom.dataGenerator;

import stroom.dataGenerator.config.EventGenConfig;
import stroom.dataGenerator.config.TemplateConfig;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

public abstract class AbstractTemplateProcessor implements TemplateProcessor{
    private final String templatePath;
    private String content;
    private final String streamName;
    private final EventGenConfig appConfig;
    private final TemplateConfig config;

    public AbstractTemplateProcessor(final EventGenConfig appConfig, final String streamName, final TemplateConfig config){
        this.appConfig = appConfig;
        this.config = config;
        templatePath = appConfig.getTemplateRoot() + "/" + config.getPath();
        this.streamName = streamName;
    }


    @Override
    public void process(ProcessingContext context, Writer output) throws TemplateProcessingException {
        final Reader input = readTemplate();
        processTemplate (input, output, context);
    }

    protected Reader readTemplate() throws TemplateProcessingException {
        if (content == null) {
            try {
                content = new String(Files.readAllBytes(Path.of(templatePath)));
            } catch (IOException e) {
                throw new TemplateProcessingException(streamName, templatePath, "Cannot find specified template", e);
            }
        }
        return new StringReader(content);
    }

    public String getStreamName() {
        return streamName;
    }

    public EventGenConfig getAppConfig() {
        return appConfig;
    }

    public TemplateConfig getConfig(){
        return config;
    }

    abstract protected void processTemplate(Reader input, Writer output, ProcessingContext context);

}