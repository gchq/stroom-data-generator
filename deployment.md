# Scripts
## Wrapper script
A wrapper script for the Eventgen java application, also called `eventgen` is designed to incrementally
generate data, with a (configurable) number of batches created on each invocation of the application.
 
This script is capable of creating the data that relates to a fixed point in history, and can also be used to create
a rolling supply of data.

It is invoked in the following way:
```
bin/eventgen <config yaml> <task id> <state persistence directory> <earliest date to generate (inclusive)> [latest date to generate (exclusive)]
```

Parameters:
1. [Configuration YAML file](config.md) for Eventgen.
1. A string that you wish to assign to this event generation job.  This can be any literal, but must not change
between invocations of the application for this set of data.
1. The directory that will be used to store the state files that record the last run period/s.  
The directory may be shared by multiple event generation jobs (i.e. different task ids).
1. The earliest date to generate data for.  This should be a date in the past expressed in the format `yyyy-MM-dd`
1. (Optional) The latest date to generate data for, when continuous /  rolling mode is not required.
If not present, simulated near real-time data representing a recent period will be generated.

The script will not currently generate data representing periods of time in the future.

The following additional parameters may also be added if required:
1. -u num - the number of unique users to add for Reference data; has no effect for Event data.
1. -h num - the number of unique hosts to add for Reference data; has no effect for Event data.
1. -s num - the number of streams per Event data file.  Allows larger volumes of Event data to be generated; has no effect for Reference data.
 

## Upload script
'sendAllZipsToStroom' is a script that finds all zip files within the specified (output) directory and upload them to Stroom.
After successfully uploading files, they are deleted from the filesystem.

It is invoked in the following way:
```
bin/sendAllZipsToStroom <eventgen output directory root> [URL] [FEED]
```

Parameters:
1. Root of directory structure containing zip output created by eventgen
1. URL to the datafeed of the stroom instance to upload the zip files to.  If this is not provided, the default
URL `http://localhost:8080/stroom/noauth/datafeed` will be used.
1. Feed name to use during upload.  If this is not present, the part of the file name prior to the first `.` is used.
This mechanism is compatible with eventgen output and enables a single invocation of this script to upload data to
multiple different feeds.  

The specified output directory and all its subdirectories are searched for .zip files, and then sent to the specified URL.
If it receives a 200 response, the script deletes the successfully uploaded file.

**WARNING - this script deletes data!**  
You should be careful to specify a directory that was created by or for use by eventgen and only contains its output, 
to avoid inadvertently deleting .zip files that were not created by eventgen.

It is only possible to run a single instance of this script at a time.

## Cleanup script
'removeIncompleteOutput' is a script that finds all files that look like eventgen files that are still in the process
of being written to, and then checks whether a process is actually writing to these files.
Any incomplete files that are not apparently still being written to are deleted from the filesystem.
 
It is invoked in the following way:
```
bin/sendAllZipsToStroom <eventgen output directory root>
```

The root of the directory structure containing output created by eventgen is provided as a command line parameter.

**WARNING - this script deletes data!**  
You should be careful to specify a directory that was created by or for use by eventgen and only contains its output, 
to avoid inadvertently deleting .zip files that were not created by eventgen.

It is only possible to run a single instance of this script at a time.
