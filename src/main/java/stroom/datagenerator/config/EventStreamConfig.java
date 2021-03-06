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

import java.util.List;

public class EventStreamConfig {
    private final String name;
    private final Integer substreamCount;
    private final TemplateConfig preEvents;
    private final List<StochasticTemplateConfig> events;
    private final TemplateConfig betweenEvents;
    private final TemplateConfig postEvents;
    private final List<TemplateConfig> include;
    private final String fileEncoding;
    private final String completionCommand;
    private final String outputDirectory;
    private final String outputSuffix;
    private final String feed;
    private final String subdomain;
    private final Double minimumSecondsBetweenEvents;

    EventStreamConfig(){
        betweenEvents = null;
        name = null;
        substreamCount = null;
        preEvents = null;
        events = null;
        postEvents = null;
        fileEncoding = null;
        completionCommand = null;
        outputDirectory = null;
        outputSuffix = null;
        feed = null;
        include = null;
        subdomain = null;
        minimumSecondsBetweenEvents = null;
    }

    public EventStreamConfig(String name, int substreamCount, TemplateConfig preEvents,
                             TemplateConfig betweenEvents, String outputPath,
                             String outputSuffix,
                             List<StochasticTemplateConfig> events,
                             List<TemplateConfig> include,
                             TemplateConfig postEvents, String fileEncoding, String completionCommand,
                             String feed, String subdomain, Double minimumSecondsBetweenEvents) {
        this.name = name;
        this.substreamCount = substreamCount;
        this.betweenEvents = betweenEvents;
        this.outputDirectory = outputPath;
        this.outputSuffix = outputSuffix;
        this.preEvents = preEvents;
        this.events = events;
        this.postEvents = postEvents;
        this.fileEncoding = fileEncoding;
        this.completionCommand = completionCommand;
        this.feed = feed;
        this.include = include;
        this.subdomain = subdomain;
        this.minimumSecondsBetweenEvents = minimumSecondsBetweenEvents;
    }

    public String getName() {
        return name;
    }

    public int getSubstreamCount(EventGenConfig appConfig) {
        if (substreamCount == null) {
            return appConfig.getDefaultSubstreamCount();
        } else {
            return substreamCount;
        }
    }

    public Integer getSubstreamCount(){
        return substreamCount;
    }

    public TemplateConfig getPreEvents() {
        return preEvents;
    }

    public List<StochasticTemplateConfig> getEvents() {
        return events;
    }

    public TemplateConfig getPostEvents() {
        return postEvents;
    }

    public String getFileEncoding() {
        return fileEncoding;
    }

    public String getCompletionCommand() {
        return completionCommand;
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public String getOutputSuffix() {
        return outputSuffix;
    }

    public TemplateConfig getBetweenEvents() {
        return betweenEvents;
    }

    public String getFeed() {
        return feed;
    }

    public List<TemplateConfig> getInclude() {
        return include;
    }

    public boolean isZipRequired (EventGenConfig appConfig){
        return getSubstreamCount(appConfig) > 0 || include != null;
    }

    public String getSubdomain() {
        return subdomain;
    }

    public Double getMinimumSecondsBetweenEvents() {
        return minimumSecondsBetweenEvents;
    }
}
