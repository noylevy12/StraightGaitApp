package com.straightgait.straightgaitapp;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class MessageSender extends AsyncTask<String, Void, Void> {
    Socket socket;
    DataOutputStream dataOutputStream;
    PrintWriter printWriter;
    String hostIp;

    public MessageSender(String hostIp){
        this.hostIp = hostIp;
    }


    @Override
    protected Void doInBackground(String... voids) {
        //get the ip of moblie
        String ipAddress = voids[0];
    try {
        socket = new Socket(hostIp, 7800);
        printWriter = new PrintWriter(socket.getOutputStream());
        printWriter.write(ipAddress);
        printWriter.close();
        socket.close();

    } catch (UnknownHostException e) {
        e.printStackTrace();
        Log.d("MessageSender", "UnknownHostException");
    } catch (IOException e) {
        e.printStackTrace();
        Log.d("MessageSender", "IOException");
    }
        return null;
    }
}
