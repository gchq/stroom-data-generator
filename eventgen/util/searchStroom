#!/bin/bash

if [ -z "$STROOM_API_KEY" ];
then
  echo "You must set the STROOM_API_KEY environmental variable before running this script"
  exit 1
fi

if (( $# < 1 ));
then
  echo "Usage: $0 <query json> [search API url]"
  exit 1
fi

if (( $# < 2 ));
then
  URL="https://localhost/api/stroom-index/v2/search"
else
  URL=$2
fi

export SEARCHID="Test$RANDOM"

cat $1 | jq --arg UUID "$SEARCHID" '.key.uuid = $UUID' |  http --verify=no POST $URL "Authorization:Bearer $STROOM_API_KEY" 

