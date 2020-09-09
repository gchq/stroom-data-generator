package stroom.dataGenerator;

import java.util.Random;

public final class EventGenUtil {
    private EventGenUtil(){
    }

    public static String generateTempDirectoryName(Random random){
        return "tmp" + random.nextInt(9999);
    }
}
