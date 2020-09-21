package stroom.datagenerator;

import stroom.datagenerator.config.TemplateConfig;

import java.io.Writer;

public interface TemplateProcessor {
    void process (ProcessingContext context, Writer output) throws TemplateProcessingException;
    TemplateConfig getConfig ();
}
