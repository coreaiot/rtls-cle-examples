package com.coreaiot.cle.examples;

import java.io.*;
import java.util.zip.*;

import org.eclipse.paho.mqttv5.client.*;
import org.eclipse.paho.mqttv5.common.*;
import org.eclipse.paho.mqttv5.common.packet.*;
import org.eclipse.paho.mqttv5.client.persist.*;

public class App {

  public static void main(String[] args) {
    String serverURI = "tcp://localhost:1883";
    String clientId = "";
    String topicFilter = "/cle/mqtt";
    int qos = 0;
    try {
      MqttConnectionOptions connOpts = new MqttConnectionOptions();
      connOpts.setCleanStart(false);

      MqttClient mqttClient = new MqttClient(
          serverURI,
          clientId,
          new MemoryPersistence());

      mqttClient.setCallback(new MqttCallback() {
        @Override
        public void disconnected(MqttDisconnectResponse disconnectResponse) {
        }

        @Override
        public void mqttErrorOccurred(MqttException exception) {
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
          System.out.println(new String(unzip(message.getPayload())));
        }

        @Override
        public void deliveryComplete(IMqttToken token) {
        }

        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
          System.out.println("Connected");
        }

        @Override
        public void authPacketArrived(int reasonCode, MqttProperties properties) {
        }

      });

      mqttClient.connect(connOpts);
      mqttClient.subscribe(topicFilter, qos);
    } catch (MqttException me) {
      me.printStackTrace();
    }
  }

  public static byte[] unzip(byte[] data) throws IOException, DataFormatException {
    Inflater inf = new Inflater();
    inf.setInput(data);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    while (!inf.finished()) {
      int count = inf.inflate(buffer);
      baos.write(buffer, 0, count);
    }
    baos.close();
    return baos.toByteArray();
  }
}
