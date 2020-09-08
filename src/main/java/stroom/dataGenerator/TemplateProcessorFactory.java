package stroom.dataGenerator;

import stroom.dataGenerator.config.EventGenConfig;
import stroom.dataGenerator.config.TemplateConfig;

import java.io.FileNotFoundException;
import java.time.Duration;
import java.util.Map;

public class TemplateProcessorFactory {
    private final EventGenConfig appConfig;


    public TemplateProcessorFactory(EventGenConfig appConfig){
        this.appConfig = appConfig;
    }

    TemplateProcessor createProcessor (final TemplateConfig template, final String streamName) throws FileNotFoundException {
        String format = template.getFormat();

        if (format == null){
            format = appConfig.getDefaultTemplateFormat();
        }

        if (format == null) {
            throw new IllegalStateException("No template language specified");
        }

        if (VelocityTemplateProcessor.FORMAT_ID.equals(format)){
            return new VelocityTemplateProcessor(appConfig, streamName, template);
        }

        throw new FileNotFoundException("Template language not recognised: " + format);
    }
}
