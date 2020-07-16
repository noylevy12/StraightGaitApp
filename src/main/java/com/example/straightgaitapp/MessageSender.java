package com.example.straightgaitapp;

import android.os.AsyncTask;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class MessageSender extends AsyncTask<String, Void, Void> {
    Socket socket;
    DataOutputStream dataOutputStream;
    PrintWriter printWriter;

    @Override
    protected Void doInBackground(String... voids) {
        //get the ip of moblie
        String ipAddress = voids[0];
    try {
//        socket = new Socket("192.168.254.1", 7800);
        socket = new Socket("10.100.102.20", 7800);
        printWriter = new PrintWriter(socket.getOutputStream());
        printWriter.write(ipAddress);
        printWriter.close();
        socket.close();

    } catch (UnknownHostException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }
        return null;
    }
}
