#!/bin/bash

echo "This should be run only once to send an initial seed set of reference data to Stroom, to avoid errors"
echo "usage: $0 [host count] [user count] [stream count] [install directory]"

if (( $# > 0 ));
then
  HOST_COUNT=$1
else
  HOST_COUNT=10000
fi

if (( $# > 1 ));
then
  USER_COUNT=$2
else
  USER_COUNT=5000
fi

if (( $# > 2 ));
then
  STREAM_COUNT=$3
else
  USER_COUNT=5
fi

if (( $# > 3 ));
then
  INSTALL_DIR=$4
else
  INSTALL_DIR=$(dirname "$0")/../..
fi


OUTPUT_DIR=$INSTALL_DIR/output

cd "$INSTALL_DIR"

echo "Generating test data with $HOST_COUNT hosts, $USER_COUNT users, and $STREAM_COUNT event substreams per batch"

echo "Please wait..."
mkdir -p $OUTPUT_DIR

echo "Creating reference data required for repeatable tests"
java -Xmx40M -jar bin/eventgen.jar config/reference/hosts.yml -p 2019-12-01T00:00:00.000Z -r P31D -h $HOST_COUNT -u $USER_COUNT
java -Xmx40M -jar bin/eventgen.jar config/reference/example.yml -p 2019-12-01T00:00:00.000Z -r P31D -h $HOST_COUNT -u $USER_COUNT

echo "Sending that data to Stroom"
bin/sendAllZipsToStroom $OUTPUT_DIR

echo "Generating event data for repeatable tests"
java -Xmx40M -jar bin/eventgen.jar config/events/example.yml -p 2019-12-01T00:00:00.000Z -r P31D -h $HOST_COUNT -u $USER_COUNT
sleep 300
bin/sendAllZipsToStroom $OUTPUT_DIR

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

bin/sendAllZipsToStroom $OUTPUT_DIR
echo "Sleeping to allow Stroom time to process this initial data..."
sleep 150
echo "Flushing index shards"
util/flushAllShards
echo "Done. "
echo "You can now run runTestSearch.sh to check that processing has been successful."
echo "You may also run invoke.sh, or install it in crontab"
