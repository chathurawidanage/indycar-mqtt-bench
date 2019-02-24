package edu.iu.hpc.benchmark;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

public abstract class AbstractMQTTBenchClient {

    protected String topic;
    protected String username;
    protected String password;
    protected String url;
    protected MqttClient client;

    public AbstractMQTTBenchClient(String url, String username, String password, String topic) {
        this.topic = topic;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public void start() throws MqttException {
        this.client = new MqttClient(
                url,
                MqttClient.generateClientId()
        );
        MqttConnectOptions connOpts = setUpConnectionOptions(
                this.username,
                this.password
        );
        client.connect(connOpts);
        this.onConnect();
    }

    public abstract void onConnect() throws MqttException;


    protected MqttConnectOptions setUpConnectionOptions(String username, String password) {
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setUserName(username);
        connOpts.setPassword(password.toCharArray());
        connOpts.setAutomaticReconnect(true);
        connOpts.setSocketFactory(new MQTTSocketFactory());
        connOpts.setMaxInflight(50000);
        return connOpts;
    }

}
