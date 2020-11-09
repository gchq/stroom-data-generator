#!/bin/bash

echo "This should be run only once to send an initial seed set of reference data to Stroom, to avoid errors"
echo "You can provide optional arguments via command line paramaters: $0 [host count] [user count] [stream count] [install directory]"

DEFAULT_HOST_COUNT=10000
DEFAULT_USER_COUNT=5000
DEFAULT_STREAM_COUNT=10

if (( $# > 0 ));
then
  HOST_COUNT=$1
else
  HOST_COUNT=$DEFAULT_HOST_COUNT
fi

if (( $# > 1 ));
then
  USER_COUNT=$2
else
  USER_COUNT=$DEFAULT_USER_COUNT
fi

if (( $# > 2 ));
then
  STREAM_COUNT=$3
else
  STREAM_COUNT=$DEFAULT_STREAM_COUNT
fi

if (( $# > 3 ));
then
  INSTALL_DIR=$4
else
  INSTALL_DIR=$(dirname "$0")/../..
fi

OUTPUT_DIR=$INSTALL_DIR/output

cd "$INSTALL_DIR"

if (( $? != 0 ))
then
  echo "Fatal, unable to change to eventgen install directory"
  exit
fi

echo "Generating test data with $HOST_COUNT hosts, $USER_COUNT users, and $STREAM_COUNT event substreams per batch"
echo "Please allow this process up to one hour to complete..."
echo
mkdir -p "$OUTPUT_DIR"

echo "Creating reference data required for repeatable tests"
java -Xmx40M -jar bin/eventgen.jar config/reference/hosts.yml -p 2019-12-01T00:00:00.000Z -r P31D -h $HOST_COUNT -u $USER_COUNT
java -Xmx40M -jar bin/eventgen.jar config/reference/example.yml -p 2019-12-01T00:00:00.000Z -r P31D -h $HOST_COUNT -u $USER_COUNT

echo "Sending that data to Stroom"
bin/sendAllZipsToStroom "$OUTPUT_DIR"

echo "Generating event data for repeatable tests"

java -Xmx40M -jar bin/eventgen.jar config/events/example.yml -p 2019-12-01T00:00:00.000Z -r P31D -h $HOST_COUNT -u $USER_COUNT
sleep 300
bin/sendAllZipsToStroom "$OUTPUT_DIR"

echo "Providing some more recent reference data for use with event data created by invoke.sh"

bin/eventgen config/reference/hosts.yml hosts state DAY 2020-01-01 ROLLING -h $HOST_COUNT -u $USER_COUNT 
bin/eventgen config/reference/example.yml refexample state DAY 2020-01-01 ROLLING -h $HOST_COUNT -u $USER_COUNT
bin/eventgen config/reference/hosts.yml hosts state DAY 2020-01-01 ROLLING -h $HOST_COUNT -u $USER_COUNT 
bin/eventgen config/reference/example.yml refexample state DAY 2020-01-01 ROLLING -h $HOST_COUNT -u $USER_COUNT
bin/eventgen config/reference/hosts.yml hosts state DAY 2020-01-01 ROLLING -h $HOST_COUNT -u $USER_COUNT 
bin/eventgen config/reference/example.yml refexample state DAY 2020-01-01 ROLLING -h $HOST_COUNT -u $USER_COUNT
bin/eventgen config/reference/hosts.yml hosts state DAY 2020-01-01 ROLLING -h $HOST_COUNT -u $USER_COUNT 
bin/eventgen config/reference/example.yml refexample state DAY 2020-01-01 ROLLING -h $HOST_COUNT -u $USER_COUNT
bin/eventgen config/reference/hosts.yml hosts state DAY 2020-01-01 ROLLING -h $HOST_COUNT -u $USER_COUNT 
bin/eventgen config/reference/example.yml refexample state DAY 2020-01-01 ROLLING -h $HOST_COUNT -u $USER_COUNT
bin/eventgen config/reference/hosts.yml hosts state DAY 2020-01-01 ROLLING -h $HOST_COUNT -u $USER_COUNT 
bin/eventgen config/reference/example.yml refexample state DAY 2020-01-01 ROLLING -h $HOST_COUNT -u $USER_COUNT
bin/eventgen config/reference/hosts.yml hosts state DAY 2020-01-01 ROLLING -h $HOST_COUNT -u $USER_COUNT 
bin/eventgen config/reference/example.yml refexample state DAY 2020-01-01 ROLLING -h $HOST_COUNT -u $USER_COUNT
bin/eventgen config/reference/hosts.yml hosts state DAY 2020-01-01 ROLLING -h $HOST_COUNT -u $USER_COUNT 
bin/eventgen config/reference/example.yml refexample state DAY 2020-01-01 ROLLING -h $HOST_COUNT -u $USER_COUNT
bin/eventgen config/reference/hosts.yml hosts state DAY 2020-01-01 ROLLING -h $HOST_COUNT -u $USER_COUNT 
bin/eventgen config/reference/example.yml refexample state DAY 2020-01-01 ROLLING -h $HOST_COUNT -u $USER_COUNT
bin/eventgen config/reference/hosts.yml hosts state DAY 2020-01-01 ROLLING -h $HOST_COUNT -u $USER_COUNT 
bin/eventgen config/reference/example.yml refexample state DAY 2020-01-01 ROLLING -h $HOST_COUNT -u $USER_COUNT
bin/eventgen config/reference/hosts.yml hosts state DAY 2020-01-01 ROLLING -h $HOST_COUNT -u $USER_COUNT 
bin/eventgen config/reference/example.yml refexample state DAY 2020-01-01 ROLLING -h $HOST_COUNT -u $USER_COUNT
bin/eventgen config/reference/hosts.yml hosts state DAY 2020-01-01 ROLLING -h $HOST_COUNT -u $USER_COUNT 
bin/eventgen config/reference/example.yml refexample state DAY 2020-01-01 ROLLING -h $HOST_COUNT -u $USER_COUNT
bin/eventgen config/reference/hosts.yml hosts state DAY 2020-01-01 ROLLING -h $HOST_COUNT -u $USER_COUNT 
bin/eventgen config/reference/example.yml refexample state DAY 2020-01-01 ROLLING -h $HOST_COUNT -u $USER_COUNT
bin/eventgen config/reference/hosts.yml hosts state DAY 2020-01-01 ROLLING -h $HOST_COUNT -u $USER_COUNT 
bin/eventgen config/reference/example.yml refexample state DAY 2020-01-01 ROLLING -h $HOST_COUNT -u $USER_COUNT

bin/sendAllZipsToStroom "$OUTPUT_DIR"
echo "Sleeping to allow Stroom time to process this initial data..."
sleep 150
echo "Flushing index shards"
util/flushAllShards

echo "Done. "
echo

echo "To comfirm that processing has been successful, the following scripts may be run:"
echo "test/bin/runTestSearch.sh checks that the results of a search are as expected."

if (( $HOST_COUNT == $DEFAULT_HOST_COUNT && $USER_COUNT == $DEFAULT_USER_COUNT  ))
then
  echo "test/bin/findErrorStreams.sh checks for error streams associated with eventgen processing"
else
  echo "test/bin/runTestSearch.sh is also available but might not provide expected output due to custom number of hosts or users"
fi
echo
echo "You may now run invoke.sh, or install it to run regularly (e.g. every minute) in crontab"
echo "The command:"
echo "  test/bin/invoke.sh $HOST_COUNT $USER_COUNT <number of substreams per stream for ausearch data>"
echo "Will run invoke with consistent configuration to that used to initialise."

