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
package stroom.datagenerator.templateLanguageTesting;

import org.apache.commons.cli.*;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.io.UnicodeInputStream;
import org.apache.velocity.tools.generic.DateTool;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class App {
    private final String outputDirectory;
    private final String suffix;

    public App (String outputDirectory, String suffix){

        this.outputDirectory = outputDirectory;
        this.suffix = suffix;
        File dir = new File (outputDirectory);
        dir.mkdirs();
    }


    public String generate(String templateDirectory, String template, String outputDirectory ) {
        Velocity.init();

        VelocityContext context = new VelocityContext();

        context.put( "user","Stroom");

        context.put("date", new DateTool());

        String templateFilename = templateDirectory + "/" + template;


        try
        {
            UnicodeInputStream inputStream = new UnicodeInputStream(new FileInputStream(templateFilename));

            final String outputName = outputDirectory + "/" + template +
                    (suffix != null ? suffix : "");
            FileOutputStream fileOutputStream = new FileOutputStream(outputName);
            final OutputStreamWriter sw;
            final InputStreamReader reader;
            if (inputStream.getEncodingFromStream() != null) {
               final Charset charset = Charset.forName(inputStream.getEncodingFromStream());
               reader = new InputStreamReader(inputStream, charset.newDecoder());

               final byte[] bom;
               if ("UTF-8".equals(charset.name())){
                   bom = new byte []{(byte)239, (byte)187, (byte)191};
               } else if ("UTF-16BE".equals(charset.name())) {
                   bom = new byte[]{(byte)254, (byte)255};
               } else if ("UTF-16LE".equals(charset.name())) {
                   bom = new byte[]{(byte)255, (byte)254};
               } else if ("UTF-32BE".equals(charset.name())) {
                   bom = new byte[]{(byte)0, (byte)0, (byte)254, (byte)255};
               } else if ("UTF-32LE".equals(charset.name())) {
                   bom = new byte[]{(byte)0, (byte)0, (byte)255, (byte)254};
               } else {
                   System.err.println ("Unrecognised BOM charset " + charset.name());
                   bom = null;
               }

               if (bom != null){
                   fileOutputStream.write(bom);
                   fileOutputStream.flush();
               }

               sw = new OutputStreamWriter(fileOutputStream, charset.newEncoder());
            } else {
                reader = new InputStreamReader(inputStream, StandardCharsets.US_ASCII.newDecoder());
                sw = new OutputStreamWriter(new FileOutputStream(outputName),
                        StandardCharsets.US_ASCII.newEncoder());
            }

            System.out.println ("Template " + template + " encoding: " + inputStream.getEncodingFromStream());
            Velocity.evaluate(context, sw, template, reader);

            sw.flush();

            sw.close();
            return outputName + " written";
        } catch (FileNotFoundException fnfe){
            // couldn't find the template
            System.err.println("Cannot find " + templateFilename);
        }
        catch (IOException ioe){
            System.err.println("IOException thrown, possible encoding issue... " + ioe.getMessage());
            ioe.printStackTrace();
        }
        catch( ResourceNotFoundException rnfe )
        {
            // couldn't find the template
            System.err.println("Cannot find " + templateFilename);
        }
        catch( ParseErrorException pee )
        {
            // syntax error: problem parsing the template
            System.err.println("Parse Error in " + templateFilename
             + " " + pee.getMessage() + " Line: " + pee.getLineNumber() + " Col: " + pee.getColumnNumber());
        }
        catch( MethodInvocationException mie )
        {
            // something invoked in the template
            // threw an exception
            System.err.println("Exception thrown by invoked call from template " + templateFilename
                    + " " + mie.getMessage() + " Line: " + mie.getLineNumber() + " Col: " + mie.getColumnNumber()
                    + mie.getCause().getMessage());
        }
        catch( Exception e )
        {
            System.err.println ("Exception thrown during processing of template " + templateFilename + " Detail: "  + e.getMessage()
            + ((e.getCause() != null) ? e.getCause().getMessage() : ""));
        }

        return null;

    }

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption(new Option("o", "output", true, "Output directory"));
        options.addOption(new Option ("s","suffix", true, "File suffix, e.g. .out"));
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine commands = parser.parse(options, args);
            String outputDir = commands.getOptionValue("o");
            String extension = commands.getOptionValue("s");
            if (outputDir == null){
                outputDir = ".";
            }
            App app = new App(outputDir, extension);
            app.process(commands.getArgs());
        } catch (ParseException ex) {
            System.err.println ("Usage: java " + App.class.getName() + " [-o/--output <output directory>] " +
                    " [-s/--suffix <output file suffix e.g. .out>] " +
                    "<input dir 1> ... [input dir n]");
        }

    }

    public void process (String [] dirs){
        for (String dir : dirs){
            System.out.println("Processing all files in " + dir);
            File folder = new File (dir);
            File[]  templates = folder.listFiles();
            for (File template : templates){
                try{
                    String path = template.getCanonicalPath();

                    System.out.println("Processing template " + template.getName());
                    String result = generate(dir, template.getName(), outputDirectory);
                    System.out.println();
                    System.out.println(result);
                    System.out.println();
                    System.out.println("-------------------------");
                } catch (IOException ex) {
                    System.err.println ("IOException processing " + template.getName() + " " + ex.getMessage());
                }


            }
        }

    }
}
