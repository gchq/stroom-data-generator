/*
 * Copyright 2020 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package stroom.datagenerator;

import java.time.Instant;
import java.util.Collection;
import java.util.Random;

public class ProcessingContext {
    private final Random random;
    private Object languageNativeContext;
    private final String fqdn;
    private final int substreamNum;
    private final Instant timestamp;
    private final String userId;
    private final String hostId;
    private final long sequenceNumber;
    private final Collection<String> allHosts;
    private final Collection<String> allUsers;

    public ProcessingContext(Instant timestamp, final Collection<String> allUsers, final Collection<String> allHosts, int substreamNum, int userNum, String domain){
        this.substreamNum = substreamNum;
        this.timestamp = timestamp;
        this.allHosts = allHosts;
        this.allUsers = allUsers;
        this.userId = "user" + userNum;
        this.hostId = "host" + substreamNum;
        this.fqdn = domain + "." + this.hostId;
        this.sequenceNumber = 1;
        languageNativeContext = null;
        random = new Random(timestamp.toEpochMilli() + substreamNum);
    }

    public ProcessingContext(ProcessingContext initialContext, Instant currentTime) {
        this.substreamNum = initialContext.substreamNum;
        this.timestamp = currentTime;
        this.allUsers = initialContext.allUsers;
        this.allHosts = initialContext.allHosts;
        this.userId = initialContext.userId;
        this.hostId = initialContext.hostId;
        this.fqdn = initialContext.fqdn;
        this.sequenceNumber = initialContext.sequenceNumber + 1;
        this.languageNativeContext = initialContext.languageNativeContext;
        this.random = initialContext.random;
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
//        Random random = new Random();
        return random.nextInt(255)+"." +
                random.nextInt(255)+"." +
                random.nextInt(255)+"." +
                random.nextInt(255);
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public Object getLanguageNativeContext() {
        return languageNativeContext;
    }

    public void setLanguageNativeContext(Object languageNativeContext) {
        this.languageNativeContext = languageNativeContext;
    }

    public Collection<String> getAllHosts() {
        return allHosts;
    }

    public Collection<String> getAllUsers() {
        return allUsers;
    }

    public Random getRandom() {
        return random;
    }
}
