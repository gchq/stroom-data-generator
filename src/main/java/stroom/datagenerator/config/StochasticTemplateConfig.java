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
package stroom.datagenerator.config;

public class StochasticTemplateConfig {
    private final TemplateConfig template;
    private final double averageCountPerSecond;

    StochasticTemplateConfig (){
        template = null;
        averageCountPerSecond = 0;
    }

    public StochasticTemplateConfig (TemplateConfig template, double averageCountPerSecond){
        this.template = template;
        this.averageCountPerSecond = averageCountPerSecond;
    }
    public TemplateConfig getTemplate() {
        return template;
    }

    public double getAverageCountPerSecond() {
        return averageCountPerSecond;
    }
}
