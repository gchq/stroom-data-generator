/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package stroom.datagenerator.templateLanguageTesting;

import org.junit.Test;

import java.io.File;
import java.net.URL;

public class AppTest {
    @Test public void testProcess() {

        URL url = Thread.currentThread().getContextClassLoader().getResource("templates/velocity");



        File dir = new File (url.getPath());

        App.main(new String[] {"-o",  "output", dir.getAbsolutePath()});

    }

}
