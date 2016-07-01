package com.example.asus.secondtest;

import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.asus.firsttest.R;
import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONArray;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements /*LocationListener,*/ MqttCallback {



    private Button bt_reg;
    private Button bt_dis;
    private Button bt_phone;
    private Button bt_ip;
    private Button bt_book;
    private Button bt_car;
    private Button bt_alert;
    TextView txt;
    EditText input;




    private LocationManager locationManager;
    double LAT = 0.0;
    double LONG = 0.0;

    String text;
    boolean match = false;
    boolean match_me;
    boolean check_FS;

    MqttClient myClient;
    MqttConnectOptions connOpt;
    MemoryPersistence persistence = new MemoryPersistence();
    String broker;       //= "tcp://140.116.177.119:1883";//broker IP
    String ip;
    String clientId     = "cellphone";

    String set_room;
    String room;
    String set_node;
    String phone;
    String set_NodeLBType = "DType";
    String set_NodeMAC = "AA-BB-CC-DD-EE-FF";
    String[] set_NodeFunctions = {"location", "hotel", "Alert"};
    String[] set_Functions = {"GPS", "Text"};
    String set_control;
    ArrayList<String> FS_mapping_success = new ArrayList<String>();
    String Rule_ID;
    String addRule_cmd;
    JSONArray array_rule = new JSONArray();
    String car;
    String car_node;
    String sub_car;

    class Reg_commond {
        private String Source = set_node;
        private String Node = set_node;
        private String[] Functions = set_Functions;
        private String NodeLBType = set_NodeLBType;
        private String Control = "NODE_REG";
        private String[] NodeFunctions = set_NodeFunctions;
        private String NodeMAC = set_NodeMAC;
    }

    class AddFS_commond {
        private String Control;
        private ArrayList FSPairs;
        private String Node;
        private String Source;
    }

    class requestTopicList_commond {
        private String Source = set_node;
        private String Node = set_node;
        private String Control = "M2M_REQTOPICLIST";
    }

    class replyTL_commond {
        private String Control;
        private String Gateway;
        private String Source;
        private ArrayList SubscribeTopics;
    }

    /*class addM2M_rule_commond {
        private String Control = "M2M_ADDRULD";
        private String Source = "AHotel@MD-11111111-1111-1111-1111-9999999999";
        //private ArrayList<String> Rules = array_rule;
        //private JSONArray Rules = array_rule;
        private rule_format Rules = new rule_format();
        //private String Rules = addRule_cmd;
        //JSONArray Rules = new JSONArray();
    }*/

    /*class rule_format {
        private String RuleID = Rule_ID;
        private String InputNode = set_node;
        private String InputIO = "RFID";
        private String OutputNode = room;
        private String OutputIO = "key";
        private String TargetValueOverride = phone;
    }*/



    /////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 100, this);*/

        findViews();
        setListeners();
    }

    private void findViews() {
        bt_phone = (Button) findViewById(R.id.button_phone);
        bt_ip= (Button) findViewById(R.id.button_ip);
        bt_reg = (Button) findViewById(R.id.button_reg);
        bt_dis = (Button) findViewById(R.id.button_dis);
        bt_book = (Button) findViewById(R.id.button_book);
        bt_car = (Button) findViewById(R.id.button_car);
        bt_alert = (Button) findViewById(R.id.button_alert);
        txt = (TextView) findViewById(R.id.textView2);
        input = (EditText) findViewById(R.id.edit);
    }

    private void setListeners() {
        bt_phone.setOnClickListener(PHONE);
        bt_ip.setOnClickListener(IP);
        bt_reg.setOnClickListener(register);
        bt_dis.setOnClickListener(disconnect);
        bt_book.setOnClickListener(BOOK);
        bt_car.setOnClickListener(M2M);
        bt_alert.setOnClickListener(Alert);
    }

    private Button.OnClickListener PHONE = new Button.OnClickListener() {
        public void onClick(View v) {
            try {
                phone = input.getText().toString();
                set_node = "CellPhone@NODE-66666666-5555-4444-3333-12" + phone;
                txt.setText(phone);
            } catch (Exception e) {
                System.out.println("gg");
            }
        }
    };

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

    private Button.OnClickListener register = new Button.OnClickListener() {
        public void onClick(View v) {
            try {
                //regClient();
                registration();
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
                car = input.getText().toString();
                car_node = car + "@NODE-77777777-5555-9999-1111-" + car + car;
                sub_car = car_node + "/GPS";
                match = false;
                m2mClient();
                txt.setText("Subscribe M2M Topic");
                String aa = "Make a noise!!!!!";
                do {
                    txt.setText("Subscribe M2M Topic");
                } while (!match);
                txt.setText(text);
            } catch (Exception e) {
                System.out.println("gg");
            }
        }
    };

    private Button.OnClickListener BOOK = new Button.OnClickListener() {
        public void onClick(View v) {
            try {
                //bookClient();
                set_room = input.getText().toString();
                Rule_ID = set_room.substring(1, set_room.length());
                room = set_room + "@NODE-8c987879-958f-44e4-b1ac-" + Rule_ID + Rule_ID;
                bookingRoomMD(room);
                txt.setText("book room " + set_room);
            } catch (Exception e) {
                System.out.println("gg");
            }
        }
    };

    private Button.OnClickListener Alert = new Button.OnClickListener() {
        public void onClick(View v) {
            try {
                carClient();
                txt.setText("let car make noise");
            } catch (Exception e) {
                System.out.println("gg");
            }
        }
    };



    AddFS_commond addfs_step = new AddFS_commond();
    Gson addfs_step_gson = new Gson();
    replyTL_commond replyTL_step = new replyTL_commond();
    Gson replyTL_step_gson = new Gson();
    //rule_format add_room_step;


    public void registration() {
        connOpt = new MqttConnectOptions();
        connOpt.setCleanSession(true);

        Reg_commond reg_step = new Reg_commond();
        Gson reg_step_gson = new Gson();
        String reg_message = reg_step_gson.toJson(reg_step);

        // Connect to Broker
        try {
            myClient = new MqttClient(broker, clientId, persistence);
            myClient.setCallback(this);
            myClient.connect();
            System.out.println("Connected");

            myClient.subscribe(set_node);
            System.out.println("sub: " + set_node);

            System.out.println("Publishing message: "+ reg_message);
            MqttMessage message = new MqttMessage();
            message.setPayload(reg_message.getBytes());// GOGO gps
            myClient.publish("IOTSV/REG", message);
            System.out.println("Register to IoT Server");
        } catch (MqttException e) {
            e.printStackTrace();
            System.out.println("qq");
            System.exit(-1);
        }
    }

    public void bookingRoomMD(String wanted_room) {
        // setup commond

        String room = set_node + "/NFC";
        /*boolean check_room = wanted_room.contains("A");
        if ( check_room )
            room = set_node +"/NFC";
        else
            room = set_node +"/RFID";*/

        String content_json = String.format("{\"TopicName\": \"%s\", \"Source\": \"%s\", \"Control\": \"M2M_SET\", \"Room\": \"%s\"}",
                room, set_node, set_room);
        // Connect to Broker
        try {
            myClient = new MqttClient(broker, clientId, persistence);
            myClient.setCallback(this);
            myClient.connect();
            System.out.println("Connected");

            MqttMessage message = new MqttMessage();
            message.setPayload(content_json.getBytes());// GOGO gps
            myClient.publish(room, message);
        } catch (MqttException e) {
            e.printStackTrace();
            System.out.println("qq");
            System.exit(-1);
        }
    }

    /*public void regClient() {
        // setup MQTT Client
        connOpt = new MqttConnectOptions();
        connOpt.setCleanSession(true);

        // setup commond

        //control = "NODE_REG";
        //String content_json = String.format("{\"Node\": \"%s\", \"NodeFunctions\": %s, \"Source\": \"%s\", \"Functions\": %s, \"Control\": \"%s\"}",
                //node, NodeFunctions, node, Fuctions, control);

        Reg_commond reg_step = new Reg_commond();
        Gson reg_step_gson = new Gson();
        String reg_message = reg_step_gson.toJson(reg_step);

        // Connect to Broker
        try {
            myClient = new MqttClient(broker, clientId, persistence);
            myClient.setCallback(this);
            myClient.connect();
            System.out.println("Connected");

            myClient.subscribe(set_node);
            System.out.println("sub myself: " + set_node);

            System.out.println("Publishing reg message: "+ reg_message);
            MqttMessage message = new MqttMessage();
            message.setPayload(reg_message.getBytes());// GOGO gps
            myClient.publish("IOTSV/REG", message);
            System.out.println("Register to IoT Server");
        } catch (MqttException e) {
            e.printStackTrace();
            System.out.println("qq");
            System.exit(-1);
        }
    }*/

    public void m2mClient() {
        MqttClient myClient;
        MqttConnectOptions connOpt;
        connOpt = new MqttConnectOptions();
        connOpt.setCleanSession(true);
        MemoryPersistence persistence = new MemoryPersistence();

        // setup commond


        // Connect to Broker
        try {
            myClient = new MqttClient(broker, clientId, persistence);
            myClient.setCallback(this);
            myClient.connect();
            System.out.println("Connected");

            myClient.subscribe(sub_car);
            System.out.println("sub: " + sub_car);
        } catch (MqttException e) {
            e.printStackTrace();
            System.out.println("qq");
            System.exit(-1);
        }
    }

    /*public void bookClient() {

        // setup commond
        String content_json = String.format("{\"TopicName\": \"%s/SW0\", \"Source\": \"%s\", \"Control\": \"M2M_SET\"}",
                set_node, set_node);
        String room = set_node +"/SW0";
        // Connect to Broker
        try {
            myClient = new MqttClient(broker, clientId, persistence);
            myClient.setCallback(this);
            myClient.connect();
            System.out.println("Connected");

            MqttMessage message = new MqttMessage();
            message.setPayload(content_json.getBytes());// GOGO gps
            myClient.publish(room, message);
        } catch (MqttException e) {
            e.printStackTrace();
            System.out.println("qq");
            System.exit(-1);
        }
    }*/

    public void disClient() {
        // setup MQTT Client
        connOpt = new MqttConnectOptions();
        connOpt.setCleanSession(true);

        // setup commond
        String Control = "LASTWILL";
        String content_json = String.format("{\"Node\": \"%s\", \"Source\": \"%s\", \"Control\": \"%s\"}",
                set_node, set_node, Control);

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
        String Control = "M2M_REQTOPICLIST";
        String content_json = String.format("{\"Node\": \"%s\", \"Source\": \"%s\", \"Control\": \"%s\"}",
                set_node, set_node, Control);

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
            myClient.publish("FS1", message);
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

    public void carClient() {
        connOpt = new MqttConnectOptions();
        connOpt.setCleanSession(true);

        // setup commond
        String car = set_node + "/Alert" ;

        // Connect to Broker
        try {
            myClient = new MqttClient(broker, clientId, persistence);
            myClient.setCallback(this);
            myClient.connect();
            System.out.println("Connected");

            String content = "screen";
            MqttMessage message = new MqttMessage();
            message.setPayload(content.getBytes());// GOGO gps
            myClient.publish(car, message);
        } catch (MqttException e) {
            e.printStackTrace();
            System.out.println("qq");
            System.exit(-1);
        }
    }

    //////////////////////////////////// for GPS
   /* @Override
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
        //Toast.makeText(getBaseContext(), "Gps is turned on!! ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String s) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
        //Toast.makeText(getBaseContext(), "Gps is turned off!! ", Toast.LENGTH_SHORT).show();
    }*/

    @Override
    public void connectionLost(Throwable throwable) {

    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        System.out.println("-------------------- Subscribe --------------------");
        System.out.println("Topic: " + s);
        System.out.println("Message: " + new String(mqttMessage.getPayload()));
        System.out.println("---------------------------------------------------");

        match_me = set_node.equals(s);
        System.out.println(match_me);
        //check_FS = FS_mapping_success.contains(s);
        match = sub_car.equals(s);
        Thread.sleep(500);
        if (match_me) {
            //System.out.println("ZZZ");
            addfs_step = addfs_step_gson.fromJson(new String(mqttMessage.getPayload()), AddFS_commond.class);
            //System.out.println(addfs_step.Control);
            //System.out.println(addfs_step.FSPairs);
            //JsonParser addfs_parser = new JsonParser();
            //JsonObject addfs = (JsonObject)addfs_parser.parse(new String(mqttMessage.getPayload()));
            //JsonArray mappingFS = addfs.getAsJsonArray("FSPairs");
            //Gson test = new Gson();
            //ArrayList test_fs = test.fromJson(mappingFS, ArrayList.class);
            int size = addfs_step.FSPairs.size();
            //System.out.println(size);
            for (int i = 0; i < addfs_step.FSPairs.size(); i++) {
                String go_split = addfs_step.FSPairs.get(i).toString();
                String set_split = go_split.substring(1,go_split.length()-1);
                //System.out.println(set_split);
                String[] FSpairs_split = set_split.split(",");
                //System.out.println(FSpairs_split[1]);

                FS_mapping_success.add(FSpairs_split[0]);
            }
            requestFStopicList();
            //String[] test = addfs_step_gson.fromJson(String.format(addfs_step.FSPairs), String[].class);
        }


        /*if (check_FS) {
            replyTL_step = replyTL_step_gson.fromJson(new String(mqttMessage.getPayload()), replyTL_commond.class);
            System.out.println(replyTL_step.Gateway);
            System.out.println(replyTL_step.SubscribeTopics);
        }*/

        else if ( match ) {
            System.out.println("Make a noise!!!!!!!");
            text = new String(mqttMessage.getPayload());
            //txt.setText(text);
        }

        else {
            System.out.println("nothing");
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }

    public void requestFStopicList() throws InterruptedException {
        connOpt = new MqttConnectOptions();
        connOpt.setCleanSession(true);

        requestTopicList_commond reqTL_step = new requestTopicList_commond();
        Gson reqTL_step_gson = new Gson();
        String reg_message = reqTL_step_gson.toJson(reqTL_step);

        // Connect to Broker
        for (String str : FS_mapping_success) {
            try {
                myClient = new MqttClient(broker, clientId, persistence);
                myClient.setCallback(this);
                myClient.connect();
                System.out.println("Connected");

                System.out.println("Publishing message: " + reg_message);
                MqttMessage message = new MqttMessage();
                message.setPayload(reg_message.getBytes());// GOGO gps
                myClient.publish(str, message);
                System.out.println("Request Topic List");
                myClient.subscribe(str);
                System.out.println("sub FS: " + str);
            } catch (MqttException e) {
                e.printStackTrace();
                System.out.println("qq");
                System.exit(-1);
            }

            Thread.sleep(3000);
        }
    }
}
