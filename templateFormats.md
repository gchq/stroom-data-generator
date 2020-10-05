# Supported Template Languages
Eventgen is capable of supporting a mixture of template langage formats within a single configuration.

The following template formats are currently supported:
* Static
* Simple
* Velocity

## Static
`Static` is the most primitive template "language" of all.  Copies the template directly to the output with minimal modification.

It is fast but extremely limited in capability.

## Simple
`Simple` is a very minimal template language that copies the input to the output and expands a number of special strings (variables).

It is relatively fast at processing.

### Variables that change every stream / substream
* `$user` - a randomly allocated user id, normally assumed to be using the device during the creation of the stream
* `$substream` - the one up number of the current substream within the zip
* `$hostip` - a IP address that is meant to belong to the device generating the stream.
* `$host` - a hostname that is meant to belong to the device generating the stream
* `$fqdn` - the fully qualified DNS name of `$host`

### Variables that change every event
* `$otherip` - a randomly generated IP address
* `$otheruser` - a randomly allocated user id from the set of all userids
* `$seq` - a one up number for the event. 
* `$date.format('SimpleDateFormatString')` - the timestamp of the event formatted with respect to the supplied format
string and the selected timezone and locale.  The format string is that expected by Java 12 SimpleDateFormat, and
is expected to be surrounded by quotes (either `'` or `"`)

## Velocity
`Velocity` is [Apache Velocity Format v2.2](https://velocity.apache.org/engine/2.2/user-guide.html) a powerful and well-established template language.

The `VelocityContext` persists for all templates relating to an event stream. This enables variables to be used
consistently even between templates.

In addition to any variable that may be defined within a template itself, a number of others are automatically created and made available.

### Variables that change every stream / substream
* `$user` - a randomly allocated user id, using the device during the creation of the stream
* `$substream` - the one up number of the current substream within the zip
* `$hostip` - a IP address that is meant to belong to the device generating the stream.
* `$host` - a hostname that is meant to belong to the device generating the stream
* `$fqdn` - the fully qualified DNS name of `$host`
* `$allusers` - a list containing all users
* `$math` - an instance of [Velocity's MathTool](https://velocity.apache.org/tools/3.0/tools-summary.html#MathTool).
* `$random` - an instance of Java 12's java.util.Random class, that is set to the same seed when 
regenerating a particular event. 

### Variables that change every event
* `$otherip` - a randomby generated IP address
* `$otheruser` - a randomly allocated user id from the set of all userids
* `$seq` - a one up number for the event. 
* `$date` - an instance of [Velocity's ComparisonDateTool](https://velocity.apache.org/tools/3.0/tools-summary.html#ComparisonDateTool) 
that defaults to the current event time rather than the actual current clock time.


