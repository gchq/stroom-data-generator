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

import java.util.Locale;
import java.util.TimeZone;

public class TemplateConfig {
    private final String path;
    private final String format;
    private final String type;
    private final String localeId;
    private final String timeZoneId;

    TemplateConfig(){
        path = null;
        format = null;
        type = null;
        localeId = null;
        timeZoneId = null;
    }

    public TemplateConfig(final String path, final String format, final String type, final String localeName, final String timeZoneName) {
        this.path = path;
        this.format = format;
        this.type = type;
        this.localeId = localeName;
        this.timeZoneId = timeZoneName;
    }

    public TemplateConfig(final TemplateConfig base, final String path){
        this.path = path;
        this.format = base.format;
        this.type = base.type;
        this.localeId = base.localeId;
        this.timeZoneId = base.timeZoneId;
    }

    public String getPath() {
        return path;
    }

    public String getFormat() {
        return format;
    }

    public String getType() {
        return  type;
    }

    public TimeZone getTimeZone(EventGenConfig appConfig){
        if (timeZoneId != null){
            return TimeZone.getTimeZone(timeZoneId);
        } else {
            return TimeZone.getTimeZone(appConfig.getDefaultTimeZoneId());
        }
    }

    public Locale getLocale(EventGenConfig appConfig){
        if (localeId != null){
            return Locale.forLanguageTag(localeId);
        } else if (appConfig.getDefaultLocaleId() != null){
            return Locale.forLanguageTag(appConfig.getDefaultLocaleId());
        } else {
            return Locale.getDefault();
        }
    }
}
