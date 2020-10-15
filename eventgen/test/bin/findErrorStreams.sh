#!/bin/bash

if [ -z "$STROOM_API_KEY" ];
then
  echo "You must set the STROOM_API_KEY environmental variable before running this script"
  exit 1
fi

if (( $# < 1 ));
then
  echo "No first parameter provided, using default URL"
  URL="https://localhost/api/meta/v1/find"
else
  URL=$1
fi

FEED_NAME='TEST-CANTEEN_FOOD_PURCHASE_JSON-EVENTS'

if (( $# < 2 ));
then
  echo "No second parameter provided, using default feed name $FEED_NAME"
  URL="https://localhost/api/meta/v1/find"
else
  URL=$1
fi


DIR=$(dirname $0)
INSTALL_DIR=$DIR/../../

OUTPUT_DIR="${INSTALL_DIR}test/output"

mkdir -p $OUTPUT_DIR
OUTPUT_FILE="$OUTPUT_DIR/errorsid-$RANDOM.json"

echo "Searching $URL for Error streams associated with feed $FEED_NAME"
echo

cat test/searchTemplates/findErrorStreams.json  | jq  --arg FEED "$FEED_NAME" '.expression.children[0].value=$FEED' \
	| http --verify=no POST $URL "Authorization:Bearer $STROOM_API_KEY"  > $OUTPUT_FILE

if [ ! -f $OUTPUT_FILE ]
then
  echo "Stroom did not send a result."
  exit 1  
fi

STREAM_COUNT=$(cat $OUTPUT_FILE | jq '.pageResponse.length')
EXACT=$(cat $OUTPUT_FILE | jq '.pageResponse.exact')

echo "Stroom API call output has been saved in $OUTPUT_FILE"
echo

if [[ -z "$EXACT" || $EXACT != 'true' ]]
then
  echo "Incomplete results received from stroom"
  exit 1
fi

if (( $STREAM_COUNT == 0 ))
then
  echo "No error streams at this time."
  exit 0
fi

echo "$STREAM_COUNT error streams for the $FEED_NAME feed"
echo
echo "Summary. Number of streams per pipeline are as follows:"
cat $OUTPUT_FILE | jq -r '.values[].meta.pipelineUuid' | sort | uniq -c \
	| sed 's/3f32873c-a366-4c18-9274-381727cb1e7c/Analytic Pipeline: Generate Restaurant Reviews/g' \
	| sed 's/007807a1-cd4c-41ad-9b83-458903e2f1a7/Translation Pipeline: TEST-CANTEEN_FOOD_PURCHASE_JSON-EVENTS/g' \
	| sed 's/dbdff9b6-534e-4706-bc67-fad501f2e616/Indexing Pipeline: Reviews Index/g' 

exit 1



