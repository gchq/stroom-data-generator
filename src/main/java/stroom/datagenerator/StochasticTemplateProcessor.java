package stroom.datagenerator;

import stroom.datagenerator.config.StochasticTemplateConfig;

import java.io.FileNotFoundException;
import java.io.Writer;

public class StochasticTemplateProcessor implements TemplateProcessor{
    private TemplateProcessor processor;
    private StochasticTemplateConfig config;
    public StochasticTemplateProcessor(TemplateProcessorFactory templateProcessorFactory, String streamName,
                                       StochasticTemplateConfig contentProcessorConfig) throws FileNotFoundException {
        processor = templateProcessorFactory.createProcessor(contentProcessorConfig.getTemplate(), streamName);
        config = contentProcessorConfig;
    }

    public long nextEventAfterMs(double randomVal){
        double arrivalRate = config.getAverageCountPerSecond() / 1000.0;
        return (long) ((Math.log(1.0-randomVal))/-arrivalRate);
    }

    @Override
    public void process(ProcessingContext context, Writer output) throws TemplateProcessingException {
        processor.process(context, output);
    }
}
