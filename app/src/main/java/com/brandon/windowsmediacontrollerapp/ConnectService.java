package com.brandon.windowsmediacontrollerapp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.brandon.windowsmediacontrollerapp.remote.Remote;
import com.brandon.windowsmediacontrollerapp.remote.RemoteServerCallback;
import com.brandon.windowsmediacontrollerapp.remote.ServerConnection;
import com.brandon.windowsmediacontrollerapp.remote.models.TransferCommandsObject;

public class ConnectService extends Service {

    public final static String APP_COMMAND_CONNECT = "com.brandon.windowsmediacontrollerapp.APP_COMMAND_CONNECT";
    public final static String APP_COMMAND_DISCONNECT = "com.brandon.windowsmediacontrollerapp.APP_COMMAND_DISCONNECT";
    public final static String APP_COMMAND_PAUSE = "com.brandon.windowsmediacontrollerapp.APP_COMMAND_PAUSE";
    public final static String APP_COMMAND_NEXT = "com.brandon.windowsmediacontrollerapp.APP_COMMAND_NEXT";
    public final static String APP_COMMAND_PREV = "com.brandon.windowsmediacontrollerapp.APP_COMMAND_PREV";
    public final static String APP_COMMAND_MUTE = "com.brandon.windowsmediacontrollerapp.APP_COMMAND_MUTE";
    public final static String APP_COMMAND_VOLDOWN = "com.brandon.windowsmediacontrollerapp.APP_COMMAND_VOLDOWN";
    public final static String APP_COMMAND_VOLUP = "com.brandon.windowsmediacontrollerapp.APP_COMMAND_VOLUP";

    private ServerConnection remote;

    public ConnectService() {
        remote = new ServerConnection();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate() {
        super.onCreate();
        startIntentReceiver();
        remote.addListener(callback);
        remote.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
       return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    public void startIntentReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(APP_COMMAND_PAUSE);
        filter.addAction(APP_COMMAND_NEXT);
        filter.addAction(APP_COMMAND_PREV);
        filter.addAction(APP_COMMAND_MUTE);
        filter.addAction(APP_COMMAND_VOLDOWN);
        filter.addAction(APP_COMMAND_VOLUP);
        registerReceiver(receiver, filter);
    }


    BroadcastReceiver receiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("ConnectService", action);
            if(action.equals(APP_COMMAND_PAUSE)){
                remote.send("Pause", "empty");
            } else if(action.equals(APP_COMMAND_NEXT)){
                remote.send("SkipNext", "empty");
            }else if(action.equals(APP_COMMAND_PREV)){
                remote.send("SkipPrev", "empty");
            }else if(action.equals(APP_COMMAND_VOLUP)){
                remote.send("VolumeUp", "5");
            }else if(action.equals(APP_COMMAND_VOLDOWN)){
                remote.send("VolumeDown", "5");
            }else if(action.equals(APP_COMMAND_MUTE)){
                remote.send("VolumeMute", "empty");
            }

        }
    };

    private String TAG = "ConnectService";
    public RemoteServerCallback callback = new RemoteServerCallback() {
        @Override
        public void OnDataReceived(TransferCommandsObject object) {
            Log.d(TAG, "OnDataReceived: " + object.toString());
            System.out.println("Data: " + object);
        }

        @Override
        public void OnCommandReceived(TransferCommandsObject object) {
            Log.d(TAG, "OnCommandReceived: " + object.toString());
            if(object.Command.equals("Login")){
                if(object.Value.equals("x_891$UI.()")){
                    remote.sendResponse("Login", "Accepted");
                }
            }
//            if(object.Command.equals("Debug")){
////                if(object.Value.equals("TestVolume")){
////                    remote.sendCommand("VolumeUp", "100");
////                }
////            }
        }

        @Override
        public void OnResponseReceived(TransferCommandsObject object) {
            Log.d(TAG, "OnResponseReceived: " + object.toString());
        }
    };
}
