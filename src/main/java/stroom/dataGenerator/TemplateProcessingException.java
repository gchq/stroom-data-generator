package stroom.dataGenerator;

public class TemplateProcessingException extends Exception {
    public TemplateProcessingException (final String streamName, final String templatePath, final String errorMsg, Throwable cause){
        super("Error generating stream " + streamName + " while processing template: " + templatePath + " Error: " + errorMsg, cause);
    }

    public TemplateProcessingException (final String streamName, final String templatePath, final String errorMsg){
        super("Error generating stream " + streamName + " while processing template: " + templatePath + " Error: " + errorMsg);
    }
}
