package edu.iu.hpc.benchmark;

import org.eclipse.paho.client.mqttv3.*;

public class MQTTBenchSubscriber extends AbstractMQTTBenchClient {

    public MQTTBenchSubscriber(String url, String username, String password, String topic) {
        super(url, username, password, topic);
    }

    public void onConnect() throws MqttException {
        client.subscribe(topic);
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {
                try {
                    client.reconnect();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                try {
                    String payload = new String(mqttMessage.getPayload());
                    String[] split = payload.split(",");
                    String uuid = split[0];
                    ResultsStore.markRecv(uuid);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("Error in processing received message");
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });

    }
}
