
package com.straightgait.straightgaitapp;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.WIFI_SERVICE;
import static com.straightgait.straightgaitapp.App.CHANNEL_1_ID;
import static java.lang.Integer.parseInt;

public class DeviceFragment extends Fragment implements DialogIP.DialogIPListener {

    public static final String TAG = "TAG";
    Button btnConnectDevice;
    TextView textViewLegStatus, textViewAngle, textViewLegTitle, textViewConnectingMessage;
    ImageView imageViewLeg;
    String SERVER_IP;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    boolean isExist;
    DocumentReference documentReference;
    Map<String, Object> details,legMove;
    Activity thisActivity;
    Context context;
    private NotificationManagerCompat notificationManager;
    private String computerIpAddress;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_device, container, false);


        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        try {
            SERVER_IP = getLocalIpAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        context = getContext();
        textViewLegStatus = (TextView) rootView.findViewById(R.id.textViewLegStatus);
        textViewConnectingMessage = (TextView) rootView.findViewById(R.id.textViewConnectingMessage);
        textViewAngle = (TextView) rootView.findViewById(R.id.textViewAngle);
        textViewLegTitle = (TextView) rootView.findViewById(R.id.textViewLegTitle);
        imageViewLeg = (ImageView) rootView.findViewById(R.id.imageViewLeg);
        btnConnectDevice = (Button) rootView.findViewById((R.id.btnConnectDevice));
        btnConnectDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MessageSender messageSender = new MessageSender();
//                messageSender.execute(SERVER_IP);
                openDialog();
//                textViewLegStatus.setText("waiting for sensor data");
//                btnConnectDevice.setText("reconnect");
//                textViewConnectingMessage.setText("Waiting for sensor data...");

            }
        });

        Thread thread = new Thread(new serverThread());
        thread.start();


        return rootView;

    }

    private void openDialog() {
        DialogIP dialogIP = new DialogIP(DeviceFragment.this);
        dialogIP.show(getFragmentManager(), "Connect to device");
    }

    @Override
    public void applyTextFromDialog(String ip) {
        computerIpAddress = ip;
        if(validIp(computerIpAddress)){
            Toast.makeText(thisActivity, "the IP: "+computerIpAddress+ "is valid", Toast.LENGTH_SHORT).show();
            MessageSender messageSender = new MessageSender(computerIpAddress);
            messageSender.execute(SERVER_IP);
            btnConnectDevice.setText("reconnect");
            textViewConnectingMessage.setText("Waiting for sensor data...");
        }else {
            Toast.makeText(thisActivity, "the IP: "+computerIpAddress+ "is not valid", Toast.LENGTH_SHORT).show();

        }


    }

    private boolean validIp(final String ip) {
        String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";

        return ip.matches(PATTERN);
    }

    private class serverThread implements Runnable{
        Socket clientSocket;
        ServerSocket serverSocket;
        InputStreamReader inputStreamReader;
        BufferedReader bufferedReader;
        Handler handler = new Handler();
        String data;


        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(7806);
                while (true) {
                    clientSocket = serverSocket.accept();
                    inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                    data = bufferedReader.readLine(); // reade data from device sensor

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            int retVal = 0;
                            if(data != null) {
                                    textViewAngle.setText("current foot angle: " + data);
                                textViewAngle.setPadding(30,10,30,10);

                                imageViewLeg.setRotation(parseInt(data));

                                if(parseInt(data) > 20){
                                    //The device should vibrate. The user should straighten the leg
                                    retVal = 1;
                                    textViewLegTitle.setText("Leg status:");
                                    textViewLegStatus.setText("Straighten your leg");
                                    textViewLegStatus.setTextColor(getResources().getColor(R.color.colorAccent));
                                    textViewLegStatus.setPadding(30,10,30,10);

                                    notificationManager = NotificationManagerCompat.from(getContext());
                                    sendNotification("Straighten your leg!", "Your leg is not in a straight line.");

                                }else {
                                    textViewLegTitle.setText("Leg status:");
                                    textViewLegStatus.setText("OK");
                                    textViewLegStatus.setTextColor(getResources().getColor(R.color.colorPrimary));
                                    textViewLegStatus.setPadding(20,10,20,10);
                                }

                                Date date = new Date();
                                String dStr = getDate(date.getTime()/1000);
                                Long timeStamp = date.getTime()/1000;


                                String userId = firebaseAuth.getCurrentUser().getUid();
                                Movement movement = new Movement(data, timeStamp, retVal);

                                documentReference = db.collection("LegMovements").document(userId);
//
                                details = new HashMap<>();
                                legMove = new HashMap<>();

                                details.put("angle", movement.getAngle());
                                details.put("date", movement.getDate());
                                details.put("status", movement.getStatus());
                                legMove.put("movements", Arrays.asList(details));
//                                documentReference.set(legMove);

                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                if (document.get("movements") != null) {
                                                    Log.d(TAG, "movements field exist");
                                                    isExist = true;
                                                    documentReference.update("movements", FieldValue.arrayUnion(details));
                                                } else {
                                                    Log.d(TAG, "movements field does not exist");
                                                    //Create the filed
                                                    isExist = false;
                                                    documentReference.set(legMove, SetOptions.merge());

                                                }
                                            }else {
                                                Log.d(TAG, "document created");
                                                documentReference.set(legMove, SetOptions.merge());
                                            }
                                        }
                                    }
                                });

                            }else{
                                textViewLegTitle.setText("");
                                textViewLegStatus.setText("Error read sensor data");
                                textViewLegStatus.setTextColor(Color.BLACK);
                                textViewLegStatus.setPadding(20,10,20,10);
                                Toast.makeText(thisActivity, "Error read sensor data", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });

                    inputStreamReader.close();
                    clientSocket.close();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("dd/MM/yyyy, HH:mm", cal).toString();
        return date;
    }

    private String getLocalIpAddress() throws UnknownHostException {
        WifiManager wifiManager = (WifiManager) thisActivity.getApplicationContext().getSystemService(WIFI_SERVICE);
        assert wifiManager != null;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipInt = wifiInfo.getIpAddress();
        return InetAddress.getByAddress(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(ipInt).array()).getHostAddress();
    }

    private void startServer(){

        AsyncHttpServer server = new AsyncHttpServer();
        server.get("/", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request,
                                  AsyncHttpServerResponse response) {
                response.send("Hello Noy!!!");
            }
        });
        server.listen(50000);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity){
            thisActivity =(Activity) context;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    public void sendNotification(String title, String message){
        Notification notification = new NotificationCompat.Builder(getContext(),CHANNEL_1_ID)
                .setSmallIcon(R.drawable.mini_logo)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .build();

        notificationManager.notify(1,notification);
    }
}

