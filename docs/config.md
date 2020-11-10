# Eventgen Configuration
Eventgen is principally controlled via a YAML configuration file, as described below.

Many properties are optional, some can have "global" defaults that can be overridden at an individual stream level.

The template processing language, is always specified using a literal, that relates to a [supported template format](templateFormats.md)

## Format 
```yaml
startTime: timestamp  # Timestamp of first possible event in ISO 8601 format.  E.g. 2020-01-01T00:00:00.000Z
runLength: duration # How long after the startTime should the last possible event be.  ISO 8601 format, e.g. P1D
batchDuration: duration # How long each batch of events to generate should last.  ISO 8601 format, e.g. PT10M
templateRoot: path # Path of the root of all templates, other template paths are relative to this unless they start with /
outputRoot: path # Path of the root for all output.  This directory and subdirectories will be created by the application if required
defaultTemplateFormat: [ Static | Simple | Velocity ] # Identifier of a supported template format
defaultTimeZoneId: timezone # Timezone used for outputted dates and times.  Expects an id as per Java 12 Timezone.getTimeZone(String) method
defaultLocaleId: locale # Locale used for calculated dates and times.  Expects an id as per Java 12 Locale.forLanguageTag(localeId) method
defaultFileEncoding: [ us-ascii | utf-8 | utf-16le | utf-16be | utf-32le | utf-32be ] # encoding used for generated files
domain: string # A DNS domain name that the devices will use within their fully qualified domain names (can be imaginary, does not need to resolve)
userCount: integer # The number of users to simulate via randomly assigning to $user variable during processing
defaultStreamCount: integer # The number of substreams to create within each zip for a particular time period (equates to number of simulated hosts)
streams:
  - # List of Stream configurations (see below)
```

### Stream Configuration
Each stream defined within the `streams` YAML property must comply with the following format.
```yaml
name: string # An identifier for this stream of event generation used in filename creation.
substreamCount: integer # The number of substreams to create within each zip for a particular time period (equates to number of simulated hosts)
fileEncoding: [ us-ascii | utf-8 | utf-16le | utf-16be | utf-32le | utf-32be ] # encoding used for generated files
completionCommand: string # not currently used
outputDirectory: # Subdirectory in which to create the files relating to this stream of events.
outputSuffix: string # Output file suffix, used only if substreamCount is 0 so a zip is not created.
feed: # The name of the Stroom feed associated with this stream of events, used as a prefix for the output filename.
preEvents: # A Template configuration (see below).  This template is processed before any events.
betweenEvents: # A Template configuration (see below).  This template is processed between all events.
postEvents: # A Template configuration (see below).  This template is processed after all events.
include:
  - # List of Template configurations (see below).  This is used in order to create Context and Meta files associated with the event stream.
events:
  - # List of Stochastic Template Configurations (see bottom of section) used to generate the individual events
subdomain: string # A subdomain within the overall domain used by hosts referenced in this stream
minimumSecondsBetweenEvents: real # A minimum number of seconds that must elapse between events on this stream
```


### Template Configuration
The following YAML format defines the settings for an individual template or set of templates.

```yaml
path: path # Path to either an individual template file or directory containing template files.  Relative to the template root, unless starts with /
format: [ Static | Simple | Velocity ] # Identifier of a supported template format
timeZoneId: timezone # Timezone used for outputted dates and times.  Expects an id as per Java 12 Timezone.getTimeZone(String) method
localeId: locale # Locale used for calculated dates and times.  Expects an id as per Java 12 Locale.forLanguageTag(localeId) method
type: [Meta | Context] # Should be set only where template is used to create a .meta or .ctx file associated with each event substream
```

### Stochastic Template Configuration
The following YAML format is used to define the various types of event associated with a stream.
```yaml
averageCountPerSecond: real # The required average event rate of this template (or set of templates). 
template: Template Configuration # The template definition (see above).
``` 
    
## Examples

Example configuration files are located [here](src/main/resources).