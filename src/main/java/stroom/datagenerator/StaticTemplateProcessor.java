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
