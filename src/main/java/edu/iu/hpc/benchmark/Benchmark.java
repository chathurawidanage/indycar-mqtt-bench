package edu.iu.hpc.benchmark;

import iu.edu.indycar.streamer.RecordStreamer;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Benchmark {

    public static void main(String[] args) throws MqttException {

        System.out.println("Using broker : " + args[0]);

        MQTTBenchPublisher mqttPublisher = new MQTTBenchPublisher(
                args[0],
                args[1],
                args[2],
                args[3]
        );

        MQTTBenchSubscriber mqttSubscriber = new MQTTBenchSubscriber(
                args[0],
                args[1],
                args[2],
                args[3]
        );

        System.out.println("Streaming records from : " + args[4]);

        RecordStreamer recordStreamer = new RecordStreamer(
                new File(args[4]),
                true,
                1,
                s -> "file_name"
        );

        final int noOfCars = Integer.valueOf(args[5]);
        final int messages = Integer.valueOf(args[6]);

        System.out.println("Running for " + noOfCars + " cars. Capturing records for " + messages + " messages.");

        final AtomicInteger count = new AtomicInteger(0);

        Map<String, Boolean> carsConsidered = new ConcurrentHashMap<>();

        recordStreamer.setTelemetryRecordListener(tRecord -> {

            if (carsConsidered.size() < noOfCars) {
                carsConsidered.put(tRecord.getCarNumber(), true);
            }

            if (carsConsidered.getOrDefault(tRecord.getCarNumber(), false)) {
                String uuid = UUID.randomUUID().toString();
                String record = String.format(
                        "%s,%s,%f,%f,%f",
                        uuid,
                        tRecord.getCarNumber(),
                        tRecord.getEngineSpeed(),
                        tRecord.getThrottle(),
                        tRecord.getVehicleSpeed()
                );
                try {
                    mqttPublisher.publishMessage(uuid, record);
                    if (count.incrementAndGet() == messages) {
                        ResultsStore.writeToFile("results_" + noOfCars + ".csv");
                        System.out.println("Results written to file.");
                        System.exit(0);
                    }

                    System.out.println(count.get());
                } catch (MqttException e) {
                    e.printStackTrace();
                    System.out.println("Error in sending message");
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Error in writing results to file");
                }
            }
        });

        mqttPublisher.start();
        mqttSubscriber.start();
        recordStreamer.start();
    }
}
