# stroom-data-generator
Utility for generating data suitable for testing Stroom

## Overview
This repo contains the source code for **eventgen** a utility that is capable of creating pseudorandom data
in arbitrary text formats.  It has been developed with the purpose of Stroom testing, but it is possible
that it might be useful in other contexts too.

## Building
Build is controlled via gradle.  

Having cloned this repo, open a shell in the root directory of the repo and type:
```shell script
./gradlew jar eventgenDistZip
```

## Eventgen
Eventgen is a java application, compatible with Java 12 and above.  
The classname of the executable class is `stroom.datagenerator.EventGen`.
It can be invoked via the command:
```
java -jar build/libs/eventgen <config yml> [args...]
```
### Command line arguments
The application requires a single command line argument, this should be the name of an eventgen configuration file.

Additional, optional arguments can be provided to the application in order to override some of the configuration in the YAML file.
In order to view these arguments, use the command:
```
java -jar build/bibs/eventgen --help
```

### Configuration file
Eventgen configuration is specified in YAML.  This format is described in detail [here.](docs/config.md)

### Templates
The configuration YAML is expected to specify a number of templates that are processed using a template language processor 
in order to generate the required output.

### Output
The configuration YAML is expected to specify a directory for the root of all generated output.  
Output files are named automatically based on various properties defined within the configuration, e.g. the feed name
and time period of the events generated.

The files are created with an `-incomplete` suffix, and then renamed (moved) to their final filename when writing to
the file has completed. 

Eventgen will automatically overwrite previous output files, however it will fail if there is already a `-incomplete` 
file for the batch being created.

# Deployment
The archive `eventgen.zip` contains all software and example configuration (YAML and templates) 
within a simple directory structure.

In order to explore what this archive contains, you can navigate to the directory `eventgen`

A number of scripts can be found in `bin`.  These enable eventgen to be more easily deployed.

[More information...](docs/deployment.md)