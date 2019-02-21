package edu.iu.hpc.benchmark;

import org.eclipse.paho.client.mqttv3.*;

public class MQTTBenchPublisher extends AbstractMQTTBenchClient {

    public MQTTBenchPublisher(String url, String username, String password, String topic) {
        super(url, username, password, topic);
    }

    @Override
    public void onConnect() {
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
                //do nothing
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }

    public void publishMessage(String uuid, String message) throws MqttException {
        ResultsStore.markSent(uuid);

        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(message.getBytes());
        mqttMessage.setQos(0);
        this.client.publish(this.topic, mqttMessage);
    }
}
