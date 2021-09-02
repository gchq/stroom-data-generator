bin/eventgen config/events/testfloorplan.yml FloorplanTest state DAY `date -d '-7 day' +'%Y-%m-%d'` `date +'%Y-%m-%d'`
bin/sendAllZipsToStroom output
