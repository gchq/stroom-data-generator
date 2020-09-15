package stroom.datagenerator;

import java.io.Writer;

public interface TemplateProcessor {
    void process (ProcessingContext context, Writer output) throws TemplateProcessingException;
}
