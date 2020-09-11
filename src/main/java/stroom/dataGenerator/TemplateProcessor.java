package stroom.dataGenerator;

import java.io.Reader;
import java.io.Writer;
import java.time.Duration;
import java.time.Instant;
import java.util.Random;

public interface TemplateProcessor {
    void process (ProcessingContext context, Writer output) throws TemplateProcessingException;
}
