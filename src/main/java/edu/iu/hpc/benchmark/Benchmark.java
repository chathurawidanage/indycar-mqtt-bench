package edu.iu.hpc.benchmark;

import iu.edu.indycar.streamer.RecordStreamer;
import iu.edu.indycar.streamer.records.TelemetryRecord;
import iu.edu.indycar.streamer.records.policy.AbstractRecordAcceptPolicy;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.File;
import java.io.IOException;
import java.util.*;
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

        final List<String> goodCarsInrOrder = Arrays.asList(
                "22",
                "88",
                "66",
                "23",
                "24",
                "25",
                "26",
                "27",
                "28",
                "29",
                "98",
                "32",
                "12",
                "14",
                "59",
                "15",
                "17",
                "18",
                "19",
                "1",
                "3",
                "4",
                "6",
                "7",
                "9",
                "60",
                "64",
                "20",
                "21",
                "30",
                "10",
                "33",
                "13"
        );

        final Set<String> goodCars = new HashSet<>(goodCarsInrOrder.subList(0, Math.min(goodCarsInrOrder.size(), noOfCars)));

        System.out.println("Considering data for cars : " + goodCars);

        recordStreamer.setTelemetryRecordListener(tRecord -> {

            if (goodCars.contains(tRecord.getCarNumber())) {
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


        final HashMap<String, Boolean> foundFirstNonZero = new HashMap<>();

        recordStreamer.addRecordAcceptPolicy(
                TelemetryRecord.class,
                new AbstractRecordAcceptPolicy<TelemetryRecord>() {
                    @Override
                    public boolean evaluate(TelemetryRecord indycarRecord) {
                        if (foundFirstNonZero.containsKey(indycarRecord.getCarNumber())) {
                            return true;
                        } else if (indycarRecord.getLapDistance() != 0) {
                            foundFirstNonZero.put(indycarRecord.getCarNumber(), true);
                            return true;
                        }
                        return false;
                    }
                }
        );

        mqttPublisher.start();
        mqttSubscriber.start();
        recordStreamer.start();
    }
}
