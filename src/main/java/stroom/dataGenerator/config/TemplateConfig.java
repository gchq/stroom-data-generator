package stroom.dataGenerator.config;

public class TemplateConfig {
    private final String path;
    private final String format;

    public TemplateConfig(final String path, final String format) {
        this.path = path;
        this.format = format;
    }

    public String getPath() {
        return path;
    }

    public String getFormat() {
        return format;
    }
}
