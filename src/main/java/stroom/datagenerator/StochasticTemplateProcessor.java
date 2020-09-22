/*
 * Copyright 2020 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package stroom.datagenerator;

import stroom.datagenerator.config.StochasticTemplateConfig;
import stroom.datagenerator.config.TemplateConfig;

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

    @Override
    public TemplateConfig getConfig() {
        return processor.getConfig();
    }
}
