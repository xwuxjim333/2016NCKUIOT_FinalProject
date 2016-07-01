package com.example.asus.thirdtest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MainActivity extends AppCompatActivity implements LocationListener, MqttCallback {

    String check = "CellPhone@NODE-66666666-5555-4444-3333-120987654321/Alert";

    private Button button_test;
    private Button button_reg;
    private Button button_dis;
    private Button button_topic;
    private Button button_m2m;
    private Button button_ip;
    TextView txt;
    EditText input;

    private LocationManager locationManager;
    double LAT = 0.0;
    double LONG = 0.0;

    String text;
    boolean match = false;

    MqttClient myClient;
    MqttConnectOptions connOpt = new MqttConnectOptions();;
    MemoryPersistence persistence = new MemoryPersistence();
    String broker;       //= "tcp://140.116.177.119:1883";//broker IP
    String clientId     = "car";
    String ip;
    String node = "AB3456@NODE-77777777-5555-9999-1111-AB3456AB3456";
    String phone_view = "CellPhone@NODE-66666666-5555-4444-3333-120987654321/text";

    String AHotel = "AHotel@FS-43211234-5432-4321-654321654321";
    String TrackL = "TrackLocation@FS-12344321-2345-1234-123456123456";

    /////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, this);

        findViews();
        setListeners();
    }



    private void findViews() {
        button_test = (Button) findViewById(R.id.button);
        button_reg = (Button) findViewById(R.id.button2);
        button_dis = (Button) findViewById(R.id.button3);
        button_topic = (Button) findViewById(R.id.button4);
        button_m2m = (Button) findViewById(R.id.button5);
        button_ip = (Button) findViewById(R.id.button8);
        txt = (TextView) findViewById(R.id.textView2);
        input = (EditText) findViewById(R.id.edit);
    }

    private void setListeners() {
        button_test.setOnClickListener(push);
        button_reg.setOnClickListener(register);
        button_dis.setOnClickListener(disconnect);
        button_topic.setOnClickListener(reqTopic);
        button_m2m.setOnClickListener(M2M);
        button_ip.setOnClickListener(IP);
    }

    private Button.OnClickListener IP = new Button.OnClickListener() {
        public void onClick(View v) {
            try {
                ip = input.getText().toString();
                broker = "tcp://" + ip;
                txt.setText(broker);
            } catch (Exception e) {
                System.out.println("gg");
            }
        }
    };

    private Button.OnClickListener push = new Button.OnClickListener() {
        public void onClick(View v) {
            try {
                runClient(String.valueOf(LAT), String.valueOf(LONG));
                txt.setText("Sent car GPS");
            } catch (Exception e) {
                System.out.println("gg");
            }
        }
    };

    private Button.OnClickListener register = new Button.OnClickListener() {
        public void onClick(View v) {
            try {
                regClient();
                txt.setText("Register");
            } catch (Exception e) {
                System.out.println("gg");
            }
        }
    };

    private Button.OnClickListener disconnect = new Button.OnClickListener() {
        public void onClick(View v) {
            try {
                disClient();
                txt.setText("Disconnect");
            } catch (Exception e) {
                System.out.println("gg");
            }
        }
    };

    private Button.OnClickListener reqTopic = new Button.OnClickListener() {
        public void onClick(View v) {
            try {
                getClient();
                txt.setText("Request Topic List");
            } catch (Exception e) {
                System.out.println("gg");
            }
        }
    };

    private Button.OnClickListener M2M = new Button.OnClickListener() {
        public void onClick(View v) {
            try {

                match = false;
                m2mClient();
                txt.setText("Subscribe M2M Topic");
                String aa = "Make a noise!!!!!";
                do {
                    txt.setText("Subscribe M2M Topic");
                } while (!match);
                txt.setText(aa);
            } catch (Exception e) {
                System.out.println("gg");
            }
        }
    };

    public void regClient() {
        // setup MQTT Client
        connOpt.setCleanSession(false);
        // setup commond
        String NodeFunctions ="[\"location\"]";
        String Fuctions = "[\"GPS\", \"SOUND\"]";
        String Control = "NODE_REG";
        String mac = "AA-BB-CC-DD-EE-FF";
        String D = "DType";
        String content_json = String.format("{\"Node\": \"%s\", \"NodeFunctions\": %s, \"Source\": \"%s\", \"Functions\": %s, \"Control\": \"%s\", \"NodeMAC\": \"%s\", \"NodeLBType\": \"%s\"}",
                node, NodeFunctions, node, Fuctions, Control, mac, D);

        // Connect to Broker
        try {
            myClient = new MqttClient(broker, clientId, persistence);
            myClient.setCallback(this);
            myClient.connect(connOpt);
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

    public void runClient(String lat, String lon) {
        // setup MQTT Client

        String content = "{\"lat\": " + lat + ", \"long\": " + lon + "}";// for JSON format
        String node_gps = node + "/GPS";
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
            myClient.publish(node_gps, message);
            System.out.println("Message published");
        } catch (MqttException e) {
            e.printStackTrace();
            System.out.println("qq");
            System.exit(-1);
        }
    }

    public void m2mClient() {

        connOpt.setCleanSession(false);

        // setup commond
        String FS = "FS3";
        String node = "NODE-05";
        String Source = "NODE-05";
        String Control = "M2M_REQTOPICLIST";
        String content_json = String.format("{\"Node\": \"%s\", \"Source\": \"%s\", \"Control\": \"%s\"}",
                node, Source, Control);

        // Connect to Broker
        try {
            myClient = new MqttClient(broker, clientId, persistence);
            myClient.setCallback(this);
            myClient.connect(connOpt);
            System.out.println("Connected");

            myClient.subscribe(check);
            System.out.println("sub: " + check);
        } catch (MqttException e) {
            e.printStackTrace();
            System.out.println("qq");
            System.exit(-1);
        }
    }

    public void disClient() {
        // setup MQTT Client


        // setup commond

        String Control = "LASTWILL";
        String content_json = String.format("{\"Node\": \"%s\", \"Source\": \"%s\", \"Control\": \"%s\"}",
                node, node, Control);

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


        // setup commond

        String M2M = "NODE-06/SW2";
        String Control = "M2M_REQTOPICLIST";
        String content_json = String.format("{\"Node\": \"%s\", \"Source\": \"%s\", \"Control\": \"%s\"}",
                node, node, Control);

        // Connect to Broker
        try {
            myClient = new MqttClient(broker, clientId, persistence);
            myClient.setCallback(this);
            myClient.connect();
            System.out.println("Connected");

            System.out.println("Publishing message: "+ content_json);
            MqttMessage message = new MqttMessage();
            message.setPayload(content_json.getBytes());// GOGO gps
            myClient.publish(TrackL, message);
            System.out.println("NODE -> FS Request topic list info");
            myClient.subscribe(TrackL);
            System.out.println("sub: " + TrackL);

            myClient.subscribe(check);
            System.out.println("sub: " + check);
        } catch (MqttException e) {
            e.printStackTrace();
            System.out.println("qq");
            System.exit(-1);
        }
    }

    //////////////////////////////////// for GPS
    @Override
    public void onLocationChanged(Location location) {
        String msg = "New Latitude: " + location.getLatitude()
                + "New Longitude: " + location.getLongitude();
        LAT = location.getLatitude();
        LONG = location.getLongitude();
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        Toast.makeText(getBaseContext(), "Gps is turned on!! ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String s) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
        Toast.makeText(getBaseContext(), "Gps is turned off!! ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void connectionLost(Throwable throwable) {

    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        System.out.println("-------------------- Subscribe --------------------");
        System.out.println("Topic: " + s);
        System.out.println("Message: " + new String(mqttMessage.getPayload()));
        System.out.println("---------------------------------------------------");

        match = check.equals(s);
        if ( match ) {
            System.out.println("Make a noise!!!!!!!");
            text = new String(mqttMessage.getPayload());
            //txt.setText(text);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
