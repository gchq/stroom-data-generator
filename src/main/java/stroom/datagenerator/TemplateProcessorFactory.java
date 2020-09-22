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

import stroom.datagenerator.config.EventGenConfig;
import stroom.datagenerator.config.TemplateConfig;

import java.io.FileNotFoundException;

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

        TemplateProcessor processor;

        if (StaticTemplateProcessor.FORMAT_ID.equals(format)){
            processor = new StaticTemplateProcessor(appConfig, streamName, template);
        } else if (SimpleTemplateProcessor.FORMAT_ID.equals(format)){
            processor = new SimpleTemplateProcessor(appConfig, streamName, template);
        } else if (VelocityTemplateProcessor.FORMAT_ID.equals(format)){
            processor = new VelocityTemplateProcessor(appConfig, streamName, template);
        } else {
            throw new FileNotFoundException("Template language not recognised: " + format);
        }

        return processor;
    }
}
