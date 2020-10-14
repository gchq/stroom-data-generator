DIR=$(dirname $0)
INSTALL_DIR=$DIR/../../

OUTPUT_DIR="${INSTALL_DIR}test/output"


mkdir -p $OUTPUT_DIR
OUTPUT_FILE="$OUTPUT_DIR/testid-$RANDOM.json"
$INSTALL_DIR/util/searchStroom "$INSTALL_DIR/test/searchTemplates/goodAndBadLunchReviews.json" > $OUTPUT_FILE

diff -s "$INSTALL_DIR/test/searchTemplates/goodAndBadLunchReviews-out.json" $OUTPUT_FILE

if (( $? == 0 ))
then
  echo "All good - test output as expected"
else
  echo "Problem - test output $OUTPUT_FILE not as expected"
fi
