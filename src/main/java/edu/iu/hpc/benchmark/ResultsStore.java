package edu.iu.hpc.benchmark;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ResultsStore {

    public static List<Long> latencies = new ArrayList<>();

    public static Map<String, Long> sendTimes = new ConcurrentHashMap<>();

    public synchronized static void markSent(String uuid) {
        sendTimes.put(uuid, System.nanoTime());
    }

    public synchronized static void markRecv(String uuid) {
        Long sentTime = sendTimes.remove(uuid);
        latencies.add(System.nanoTime() - sentTime);
    }

    public synchronized static void writeToFile(String fileName) throws IOException {
        BufferedWriter br = new BufferedWriter(new FileWriter(new File(fileName)));
        for (Long latency : latencies) {
            br.write(String.valueOf(latency));
            br.newLine();
        }
        br.close();
    }
}
