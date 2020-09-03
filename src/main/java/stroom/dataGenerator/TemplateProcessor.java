package stroom.dataGenerator;

import java.io.Reader;
import java.io.Writer;
import java.time.Duration;
import java.time.Instant;
import java.util.Random;

public interface TemplateProcessor {
    void prologue (Writer output) throws TemplateProcessingException;
    void process (Instant timestamp, Writer output) throws TemplateProcessingException;
    void epilogue (Writer output) throws TemplateProcessingException;
}
