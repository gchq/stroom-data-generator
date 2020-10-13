#!/bin/bash

if (( $# < 3 ));
then
  echo "Usage: $0 <host count> <user count> <stream count> [install directory]" 
  exit 1
fi

if (( $# == 4 ));
then
  INSTALL_DIR=$4
else
  INSTALL_DIR=$(dirname $0)/../..
fi

HOST_COUNT=$1
USER_COUNT=$2
STREAM_COUNT=$3

OUTPUT_DIR=$INSTALL_DIR/output

cd $INSTALL_DIR
echo "This should be run only once to send an initial seed set of reference data to Stroom, to avoid errors"
echo "Please wait..."
mkdir -p $OUTPUT_DIR
bin/eventgen config/reference/hosts.yml seedhosts state DAY 2019-12-31 2020-01-01  -h $HOST_COUNT -u $USER_COUNT
bin/eventgen config/reference/example.yml seedRefexample state DAY 2019-12-31 2020-01-01  -h $HOST_COUNT -u $USER_COUNT
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
sleep 120
echo "Done. You can now run invoke.sh"
