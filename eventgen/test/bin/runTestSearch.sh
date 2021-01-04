#!/bin/bash
DIR=$(dirname $0)
INSTALL_DIR=$DIR/../../

OUTPUT_DIR="${INSTALL_DIR}test/output"

mkdir -p "$OUTPUT_DIR"
OUTPUT_FILE="$OUTPUT_DIR/testid-$RANDOM.json"

if (( $# < 1 ));
then
  "$INSTALL_DIR"/util/searchStroom "$INSTALL_DIR/test/searchTemplates/goodAndBadLunchReviews.json" | jq '[.results[].rows[].values]' > "$OUTPUT_FILE" 2> /dev/null
  echo "No command line argument provided, using default URL for search API on localhost"
else
  "$INSTALL_DIR"/util/searchStroom "$INSTALL_DIR/test/searchTemplates/goodAndBadLunchReviews.json" "$1" | jq '[.results[].rows[].values]' > "$OUTPUT_FILE" 2> /dev/null
fi

if [ -s "$OUTPUT_FILE" ]
then
  diff -s "$INSTALL_DIR/test/searchTemplates/goodAndBadLunchReviews-out.json" "$OUTPUT_FILE"

  STATUS=$?
  if (( STATUS == 0 ))
  then
    echo "All good - test output as expected"
    echo "You may wish to run findErrorStreams.sh to confirm that all processing has been error free."
  else
    echo "Problem - test output $OUTPUT_FILE not as expected"
  fi

  exit $STATUS
else
  echo "Problem - no results returned from Stroom search!"
fi

exit 1
