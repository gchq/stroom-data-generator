package stroom.dataGenerator;

import java.io.Reader;
import java.io.Writer;
import java.time.Instant;

public class VelocityTemplateProcessor implements TemplateProcessor {
    public static final String FORMAT_ID="Velocity";

    private final TemplateProcessorFactory.ProcessorRole role;
    private final String path;

    public VelocityTemplateProcessor(TemplateProcessorFactory.ProcessorRole role, String path){
        this.role = role;
        this.path = path;
    }

    @Override
    public void prologue(Writer output) throws TemplateProcessingException {
        if (TemplateProcessorFactory.ProcessorRole.HEADER.equals(role)){
            processTemplate (output, null);
        }
    }

    @Override
    public void process(Instant timestamp, Writer output) throws TemplateProcessingException {
        if (TemplateProcessorFactory.ProcessorRole.CONTENT.equals(role)){
            processTemplate (output, timestamp);
        }
    }

    @Override
    public void epilogue(Writer output) throws TemplateProcessingException {
        if (TemplateProcessorFactory.ProcessorRole.FOOTER.equals(role)){
            processTemplate (output, null);
        }
    }

    private void processTemplate (Writer output, Instant timestamp){

    }
}
