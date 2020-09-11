#!/bin/bash

SUFFIX='.zip'

echo "This script expects to be run from the directory containing the .zip files you wish to send"

if (( $# != 1 ))
then
  echo "Usage sendAllZipsToStroom <FEED>"
  exit 1
fi
echo

FEED=$1

for FILE in `ls *$SUFFIX`; do
  RESPONSE=`curl -w '%{http_code}' -k --data-binary @${FILE} "http://localhost:8080/stroom/noauth/datafeed" -H "Feed:$FEED" -H"Compression:zip" -H"Content-Type:application/audit`

  echo Sent $FILE to Stroom feed $FEED with response code $RESPONSE
  echo
done


