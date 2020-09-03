package stroom.dataGenerator;

import stroom.dataGenerator.config.EventGenConfig;
import stroom.dataGenerator.config.TemplateConfig;

import java.io.FileNotFoundException;
import java.time.Duration;

public class TemplateProcessorFactory {
    public enum ProcessorRole {
        HEADER,
        CONTENT,
        FOOTER
    }
    TemplateProcessor createProcessor (EventGenConfig appConfig, TemplateConfig template, ProcessorRole role) throws FileNotFoundException {
        String format = template.getFormat();

        if (template == null){
            format = appConfig.getDefaultTemplateFormat();
        }

        if (template == null) {
            throw new IllegalStateException("No template language specified");
        }

        if (VelocityTemplateProcessor.FORMAT_ID.equals(format)){
            return new VelocityTemplateProcessor(role, template.getPath());
        }

        throw new FileNotFoundException("Template language not recognised: " + format);
    }
}
