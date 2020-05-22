package com.brandon.windowsmediacontrollerapp.remote;

import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.brandon.windowsmediacontrollerapp.remote.models.TransferCommandsObject;

import java.io.IOException;
import java.util.ArrayList;

public class Remote extends Thread {

    public ServerConnection server;
    private Context c;
    private ArrayList<String> mMessages = new ArrayList<>();
    private boolean mRun = true;

    public Remote(Context context)  {
        c = context;

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void run(){
        while(mRun){
            connect();
            server = new ServerConnection();
            server.addListener(callback);
        }
    }

    public void close() {
        mRun = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void connect() {
        try {
            server.Connect("192.168.2.61", 1222); //pc ip
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.startReceiver();
    }

    public void disconnect(){
        server.Disconnect();
    }


    public RemoteServerCallback callback = new RemoteServerCallback() {
        @Override
        public void OnDataReceived(TransferCommandsObject object) {
            System.out.println("Data: " + object);
        }

        @Override
        public void OnCommandReceived(TransferCommandsObject object) {
            System.out.println("Command: " + object);
            if(object.Command.equals("Login")){
                if(object.Value.equals("x_891$UI.()")){
                    server.sendResponse("Login", "Accepted");
                }
            }
            if(object.Command.equals("Debug")){
                if(object.Value.equals("TestVolume")){
                    server.sendCommand("VolumeUp", "100");
                    //Toast.makeText(c, "Sending Volume Up", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void OnResponseReceived(TransferCommandsObject object) {
            System.out.println("Response: " + object);
        }
    };
}
