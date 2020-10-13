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
bin/eventgen config/reference/hosts.yml hosts state DAY 2020-01-01 ROLLING -h $HOST_COUNT -u $USER_COUNT &
bin/eventgen config/reference/example.yml refexample state DAY 2020-01-01 ROLLING -h $HOST_COUNT -u $USER_COUNT &
bin/eventgen config/events/ausearch.yml ausearch state HOUR 2020-08-01 ROLLING -h $HOST_COUNT -u $USER_COUNT -s $STREAM_COUNT &
bin/eventgen config/events/example.yml example state HOUR 2020-08-01 ROLLING -h $HOST_COUNT -u $USER_COUNT &
bin/sendAllZipsToStroom $OUTPUT_DIR
bin/removeIncompleteOutput $OUTPUT_DIR
