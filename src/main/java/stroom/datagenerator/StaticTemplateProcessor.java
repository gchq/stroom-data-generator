package stroom.datagenerator;

import stroom.datagenerator.config.EventGenConfig;
import stroom.datagenerator.config.TemplateConfig;

import javax.annotation.processing.ProcessingEnvironment;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class StaticTemplateProcessor extends AbstractTemplateProcessor  {
    public static final String FORMAT_ID="Static";
    private static final int BUFFERSIZE = 1024;

    StaticTemplateProcessor(final EventGenConfig appConfig,
                      final String streamName, final TemplateConfig config) {
        super(appConfig, streamName, config);
    }

    @Override
    protected void processTemplate (final Reader input, final Writer output, final ProcessingContext context)
            throws IOException {
        char[] buffer = new char [BUFFERSIZE];
        int count;

        while ((count = input.read(buffer, 0, BUFFERSIZE)) > 0){
            output.write(buffer, 0, count);
        }
    }
}
