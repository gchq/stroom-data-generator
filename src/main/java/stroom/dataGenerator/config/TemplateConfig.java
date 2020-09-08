package stroom.dataGenerator.config;

import java.sql.Time;
import java.util.Locale;
import java.util.TimeZone;

public class TemplateConfig {
    private final String path;
    private final String format;
    private final String localeId;
    private final String timeZoneId;

    public TemplateConfig(final String path, final String format, final String localeName, final String timeZoneName) {
        this.path = path;
        this.format = format;
        this.localeId = localeName;
        this.timeZoneId = timeZoneName;
    }

    public String getPath() {
        return path;
    }

    public String getFormat() {
        return format;
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
