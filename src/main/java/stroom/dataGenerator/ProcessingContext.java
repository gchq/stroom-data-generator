package stroom.dataGenerator;

import java.time.Instant;
import java.util.Random;

public class ProcessingContext {
    private final String fqdn;
    private final int substreamNum;
    private final Instant timestamp;
    private final String userId;
    private final String hostId;
    private final long sequenceNumber;

    public ProcessingContext(Instant timestamp, int substreamNum, int userNum, String domain){
        this.substreamNum = substreamNum;
        this.timestamp = timestamp;
        this.userId = "user" + userNum;
        this.hostId = "host" + substreamNum;
        this.fqdn = domain + "." + this.hostId;
        this.sequenceNumber = 1;
    }

    public ProcessingContext(ProcessingContext initialContext, Instant currentTime) {
        this.substreamNum = initialContext.substreamNum;
        this.timestamp = currentTime;
        this.userId = initialContext.userId;
        this.hostId = initialContext.hostId;
        this.fqdn = initialContext.fqdn;
        this.sequenceNumber = initialContext.sequenceNumber + 1;
    }

    public Instant getTimestamp(){
        return timestamp;
    }

    public int getSubstreamNum() {
        return substreamNum;
    }

    public String getUserId (){
        return userId;
    }

    public String getHostId (){
        return hostId;

    }

    public String getHostFqdn() {
        return fqdn;
    }

    public String getIpAddress (){
        return "192.168." + substreamNum / 256 + "." + substreamNum % 256;
    }

    public String generateRandomIpAddress () {
        Random random = new Random();
        return random.nextInt(255)+"." +
                random.nextInt(255)+"." +
                random.nextInt(255)+"." +
                random.nextInt(255);
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }
}
