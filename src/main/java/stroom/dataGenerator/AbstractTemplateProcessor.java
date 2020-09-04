package stroom.dataGenerator;

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
    private final ProcessorRole role;

    public AbstractTemplateProcessor(final ProcessorRole role, final String templateRoot, final String streamName, final TemplateConfig config){
        templatePath = templateRoot + "/" + config.getPath();
        this.streamName = streamName;
        this.role = role;
    }

    @Override
    public void prologue(Writer output) throws TemplateProcessingException {
        if (ProcessorRole.HEADER.equals(role)){
            final Reader input = readTemplate();
            processTemplate (input, output, null);
        }
    }

    @Override
    public void process(Instant timestamp, Writer output) throws TemplateProcessingException {
        if (ProcessorRole.CONTENT.equals(role)){
            final Reader input = readTemplate();
            processTemplate (input, output, timestamp);
        }
    }

    @Override
    public void epilogue(Writer output) throws TemplateProcessingException {
        if (ProcessorRole.FOOTER.equals(role)){
            final Reader input = readTemplate();
            processTemplate (input, output, null);
        }
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

    abstract protected void processTemplate(Reader input, Writer output, Instant timestamp);

}
