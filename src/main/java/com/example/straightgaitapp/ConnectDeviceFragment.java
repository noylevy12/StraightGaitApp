package com.example.straightgaitapp;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Set;

import static android.app.Activity.RESULT_OK;
import static androidx.constraintlayout.widget.Constraints.TAG;
import static androidx.core.app.ActivityCompat.checkSelfPermission;

public class ConnectDeviceFragment extends Fragment {

    public static final int REQUEST_ENABLE_BT = 0;
    public static final int REQUEST_DISCOVER_BT = 0;

    Button  btnPairedDevices, btnDiscover;
    ImageButton btnOn, btnOff;
    TextView textViewStatusBlt, textViewPaired;
    ImageView imageViewBlt;
//    ListView listViewBlt, listViewScanBlt;
ListView listViewBlt;

    BluetoothAdapter bluetoothAdapter;
    ArrayList<String> stringArrayList = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapterPairedDevices, arrayAdapterScan;

    public ArrayList<BluetoothDevice> bluetoothDevices = new ArrayList<>();
    public DeviceListAdapter deviceListAdapter;
    ListView listViewScanBlt;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_connect_device, container, false);
//        View connectDeviceFragmentView = inflater.inflate(R.layout.fragment_connect_device, container, false);


        textViewStatusBlt = (TextView) rootView.findViewById(R.id.textViewStatusBlt);
//        textViewPaired= (TextView) rootView.findViewById(R.id.textViewPaired);
        btnOn = (ImageButton) rootView.findViewById((R.id.btnOn));
        btnOff = (ImageButton) rootView.findViewById((R.id.btnOff));
        btnPairedDevices = (Button) rootView.findViewById((R.id.btnPairedDevices));
        btnDiscover = (Button) rootView.findViewById((R.id.btnDiscover));
        imageViewBlt = (ImageView) rootView.findViewById(R.id.imageViewBlt);
        listViewBlt = (ListView) rootView.findViewById(R.id.listViewBlt);
        listViewScanBlt = (ListView) rootView.findViewById(R.id.listViewScanBlt);

        //adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();



        //check if bluetooth is available or not
        if(bluetoothAdapter == null){
            //device does not support Bluetooth tecnology.
            textViewStatusBlt.setText("Bluetooth is not available");
        }else {
            // set image according bluetooth status
            textViewStatusBlt.setText("Bluetooth is available");
        }
            if(bluetoothAdapter.isEnabled()){
                imageViewBlt.setImageResource(R.drawable.ic_action_on);


            }else{
                imageViewBlt.setImageResource(R.drawable.ic_action_off);
            }

        //on bluetooth button click
        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!bluetoothAdapter.isEnabled()){
                    showToast("Turning On Bluetooth..");
                    //intent to on bluetooth
                    Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BT);
                    imageViewBlt.setImageResource(R.drawable.ic_action_on);
                }else {
                    showToast("Bluetooth is already on");
                }
            }
        });



        //off bluetooth button click
        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listViewBlt.setAdapter(null);
                listViewScanBlt.setAdapter(null);
                if(bluetoothAdapter.isEnabled()){
                    bluetoothAdapter.disable();
                    showToast("Turning Bluetooth Off ");
                    imageViewBlt.setImageResource(R.drawable.ic_action_off);
                }else{
                    showToast("Bluetooth is already off");
                }
            }
        });

        // get paired devices button click
        btnPairedDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listViewScanBlt.setAdapter(null);
                if(bluetoothAdapter.isEnabled()){
//                    textViewPaired.setText("Paired Devices");
                    Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
                    String[] devicesNames = new String[devices.size()];
                    int index = 0;
                    if(devices.size() > 0){
                        for(BluetoothDevice device : devices){
//                            textViewPaired.append("\nDevice: " + device.getName()+ ","+device);
                            devicesNames[index] = device.getName();
                            index++;
                        }
                        arrayAdapterPairedDevices = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, devicesNames);
                        listViewBlt.setAdapter(arrayAdapterPairedDevices);
                    }

                }else{
                    //bluetooth is off can not get paired devices
                    showToast("Turn on bluetooth to paired devices");
                }
            }
        });

        //discover bluetooth button click
/*
        btnDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listViewBlt.setAdapter(null);
                if(bluetoothAdapter.isEnabled()){
                    if(!bluetoothAdapter.isDiscovering()){
                        showToast("Making Your Device Discoverable");
                        bluetoothAdapter.startDiscovery();
                    }

                }else{
                    //bluetooth is off can not get paired devices
                    showToast("Turn on bluetooth to scan for devices");
                }

            }
        });


        IntentFilter intentFilter = new IntentFilter((BluetoothDevice.ACTION_FOUND));
        getActivity().registerReceiver(bltReceiver, intentFilter);

        arrayAdapterScan = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, stringArrayList);
        listViewScanBlt.setAdapter(arrayAdapterScan);
*/
//        IntentFilter intentFilter = new IntentFilter((BluetoothDevice.ACTION_FOUND));
//        getActivity().registerReceiver(bltReceiver, intentFilter);
//        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, stringArrayList);
//        listViewScanBlt.setAdapter(arrayAdapter);
//        btnDiscover.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            listViewBlt.setAdapter(null);
//            if(!bluetoothAdapter.isDiscovering()){
//                showToast("Making Your Device Discoverable");
//                bluetoothAdapter.startDiscovery();
//            }
//        }
//        });

        btnDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listViewBlt.setAdapter(null);
                listViewScanBlt.setAdapter(null);
                Log.d(TAG, "btnDiscover: Looking for unpaired devices.");
                showToast("Looking for unpaired devices.");
                if(bluetoothAdapter.isDiscovering()){
                    bluetoothAdapter.cancelDiscovery();
                    checkBTPermissions();
                    bluetoothAdapter.startDiscovery();
                    IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    getActivity().registerReceiver(bltReceiver, discoverDevicesIntent);
                }

                if(!bluetoothAdapter.isDiscovering()){
                    checkBTPermissions();
                    bluetoothAdapter.startDiscovery();
                    IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    getActivity().registerReceiver(bltReceiver, discoverDevicesIntent);
                }
            }
        });

        return rootView;
    }

    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int premmisionCheck = checkSelfPermission(getContext(),"Manifest.permission.ACCESS_FINE_LOCATION");
            premmisionCheck+= checkSelfPermission(getContext(),"Manifest.permission.ACCESS_COARSE_LOCATION");
            if(premmisionCheck !=0){
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
            }else{
                Log.d(TAG, "checkBTPremissions: No need to check permission. SDK version < LOLLIPOP");
            }
        }
    }

    // Broadcast receiver for listing devices that not yet paired
    BroadcastReceiver bltReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND");
            if(action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                bluetoothDevices.add(device);
                Log.d(TAG, "onReceive: "+ device.getName()+": "+ device.getAddress());
                deviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, bluetoothDevices);
                listViewScanBlt.setAdapter(deviceListAdapter);
            }
        }
    };

    //toast message function
    private void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case REQUEST_ENABLE_BT:
                if(requestCode == RESULT_OK){
                    //bluetooth is on
                    imageViewBlt.setImageResource(R.drawable.ic_action_on);
                    showToast("Bluetooth is on");
                }else  {
                    // user denied to turn bluetooth on
//                    showToast("could't on bluetooth");
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



}
