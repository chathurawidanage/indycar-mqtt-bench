# How to build

This application depends on IndyCar record streamer.

First build IndyCar record streamer from [here.](https://github.com/DSC-SPIDAL/IndyCar/tree/master/utils/record-streamer)

Then build this module with following command.

```mvn clean install```

# How to run

Once you run above command, you will find two jars inside ```/target```

Use ```target/benchmark-1.0-SNAPSHOT-jar-with-dependencies.jar```

This jar can be executed as follows.

```java -jar benchmark-1.0-SNAPSHOT-jar-with-dependencies.jar broker_host:broker_port broker_user broker_password broker_topic location_of_indy_car_log_file no_of_cars no_of_messages_to_use_for_benchmark```

