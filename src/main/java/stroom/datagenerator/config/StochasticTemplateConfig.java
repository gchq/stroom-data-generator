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
