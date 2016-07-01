package com.example.asus.thirdtest;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Created by asus on 2016-06-25.
 */
public class SampleClient implements MqttCallback {

    MqttClient myClient;
    MqttConnectOptions connOpt;
    MemoryPersistence persistence = new MemoryPersistence();

    String topic        = "mimi";
    String broker       = "tcp://140.116.177.119:1883";//broker IP
    String clientId     = "JavaSample";

    String car = "ABC-5678";
    String phone = "0987654321";


    @Override
    public void connectionLost(Throwable throwable) {
        System.out.println("Connection lost!");
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        System.out.println("-------------------- Subscribe --------------------");
        System.out.println("Topic: " + s);
        System.out.println("Message: " + new String(mqttMessage.getPayload()));
        System.out.println("---------------------------------------------------");
        String check = "NODE-06/SW2";

        boolean matck = check.equals(s);
        if ( matck ) {
            System.out.println("Make a noise!!!!!!!");
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        System.out.println("----------------- Publish success -----------------");
    }

    public static void main(double lat, double lon) {
        SampleClient sc = new SampleClient();
        System.out.println("run");

        if (lat == 1000.0 || lon == 1000.0) {
            //sc.regClient();
        } else if (lat == -1000.0 || lon == -1000.0) {
            sc.disClient();
        } else if (lat == -100.0 || lon == -100.0) {
            sc.getClient();
        } else {
            //sc.runClient(String.valueOf(lat), String.valueOf(lon));
        }

    }

    /*public void runClient(String lat, String lon) {
        // setup MQTT Client
        connOpt = new MqttConnectOptions();
        connOpt.setCleanSession(true);

        //String content = lat + "\t" + lon;
        String content = "{\"car\": "+ car + ", \"phone\": " + phone + ", \"lat\": " + lat + ", \"long\": " + lon + "}";// for JSON format

        // Connect to Broker
        try {
            System.out.println("try");
            myClient = new MqttClient(broker, clientId, persistence);
            myClient.setCallback(this);
            myClient.connect();
            System.out.println("Connected");
            System.out.println("Publishing message: "+ content);
            MqttMessage message = new MqttMessage();
            message.setPayload(content.getBytes());// GOGO gps
            myClient.publish("NODE-02/SW2", message);
            System.out.println("Message published");
        } catch (MqttException e) {
            e.printStackTrace();
            System.out.println("qq");
            System.exit(-1);
        }
    }*/

    public void regClient() {
        // setup MQTT Client
        connOpt = new MqttConnectOptions();
        connOpt.setCleanSession(true);

        // setup commond
        String node = "NODE-05";
        String NodeFunctions ="[\"location\", \"IOs\"]";
        String Source = "NODE-05";
        String Fuctions = "[\"GPS\", \"SOUND\", \"SW1\"]";
        String Control = "NODE_REG";
        String content_json = String.format("{\"Node\": \"%s\", \"NodeFunctions\": %s, \"Source\": \"%s\", \"Functions\": %s, \"Control\": \"%s\"}",
                node, NodeFunctions, Source, Fuctions, Control);

        // Connect to Broker
        try {
            myClient = new MqttClient(broker, clientId, persistence);
            myClient.setCallback(this);
            myClient.connect();
            System.out.println("Connected");

            myClient.subscribe(node);
            System.out.println("sub: " + node);

            System.out.println("Publishing message: "+ content_json);
            MqttMessage message = new MqttMessage();
            message.setPayload(content_json.getBytes());// GOGO gps
            myClient.publish("IOTSV/REG", message);
            System.out.println("Register to IoT Server");
        } catch (MqttException e) {
            e.printStackTrace();
            System.out.println("qq");
            System.exit(-1);
        }
    }

    public void disClient() {
        // setup MQTT Client
        connOpt = new MqttConnectOptions();
        connOpt.setCleanSession(true);

        // setup commond
        String node = "NODE-05";
        String Source = "NODE-05";
        String Control = "LASTWILL";
        String content_json = String.format("{\"Node\": \"%s\", \"Source\": \"%s\", \"Control\": \"%s\"}",
                node, Source, Control);

        // Connect to Broker
        try {
            myClient = new MqttClient(broker, clientId, persistence);
            myClient.setCallback(this);
            myClient.connect();
            System.out.println("Publishing message: "+ content_json);
            MqttMessage message = new MqttMessage();
            message.setPayload(content_json.getBytes());// GOGO gps
            myClient.publish("IOTSV/REG", message);
            myClient.disconnect();
            System.out.println("Disconnected");
            myClient.close();
        } catch (MqttException e) {
            e.printStackTrace();
            System.out.println("qq");
            System.exit(-1);
        }
    }

    public void getClient() {
        // setup MQTT Client
        connOpt = new MqttConnectOptions();
        connOpt.setCleanSession(true);

        // setup commond
        String FS = "FS3";
        String M2M = "NODE-06/SW2";
        String node = "NODE-05";
        String Source = "NODE-05";
        String Control = "M2M_REQTOPICLIST";
        String content_json = String.format("{\"Node\": \"%s\", \"Source\": \"%s\", \"Control\": \"%s\"}",
                node, Source, Control);

        // Connect to Broker
        try {
            myClient = new MqttClient(broker, clientId, persistence);
            myClient.setCallback(this);
            myClient.connect();
            System.out.println("Connected");

            System.out.println("Publishing message: "+ content_json);
            MqttMessage message = new MqttMessage();
            message.setPayload(content_json.getBytes());// GOGO gps
            myClient.publish(FS, message);
            System.out.println("NODE -> FS Request topic list info");
            myClient.subscribe(FS);
            System.out.println("sub: " + FS);

            myClient.subscribe(M2M);
            System.out.println("sub: " + M2M);
        } catch (MqttException e) {
            e.printStackTrace();
            System.out.println("qq");
            System.exit(-1);
        }
    }

}
