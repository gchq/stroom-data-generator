#!/bin/bash

HOST_COUNT=10000
USER_COUNT=1000
STREAM_COUNT=$3

INSTALL_DIR=$(dirname $0)/../..

OUTPUT_DIR=$INSTALL_DIR/output

cd $INSTALL_DIR
bin/eventgen config/reference/hosts.yml hosts state DAY 2020-01-01 ROLLING -h 10000 -u 5000 &
bin/eventgen config/reference/example.yml refexample state DAY 2020-01-01 ROLLING -h 10000 -u 5000 &

bin/eventgen config/events/example.yml example state HOUR 2020-08-01 ROLLING -h 10000 -u 5000 &
bin/sendAllZipsToStroom $OUTPUT_DIR
bin/removeIncompleteOutput $OUTPUT_DIR
