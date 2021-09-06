# This script expects to be run from the eventgen diectory off the repo root.
# It creates data that is suitable for demonstrating Stroom visualisation "FloorMap"
#
# Run this script once in order to populate the FloorMap index.
#
# N.B. If you wish to recreate the "Test FloorMap" visualisation, you will need to clone the
#  stroom-visaulisations-dev git repository and run
#  ./createLocalFloorMapVis.sh ../data/example/example.json "Test FloorMap" \
#      cf9e90a6-28ae-4c3f-91d0-773a79de9700 a5670721-a138-4e4f-8637-005e33937f8b 4d3dbb25-07f7-4c04-8f69-864435cc7f92
#
#  After importing the newly generated visualisation, you might wish to move the items to the
#   "Test/Visualisations/Test Visualisations" folder within the Stroom explorer.


bin/eventgen config/events/testfloorplan.yml FloorplanTest state DAY `date -d '-7 day' +'%Y-%m-%d'` `date +'%Y-%m-%d'`
bin/sendAllZipsToStroom output
