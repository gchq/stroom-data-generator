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
