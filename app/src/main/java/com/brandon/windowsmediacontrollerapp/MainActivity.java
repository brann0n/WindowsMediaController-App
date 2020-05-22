package com.brandon.windowsmediacontrollerapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.brandon.windowsmediacontrollerapp.remote.Remote;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, ConnectService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void OnPause_Click(View e){
        sendBroadcast(new Intent(ConnectService.APP_COMMAND_PAUSE));
    }

    public void OnNext_Click(View e){
        sendBroadcast(new Intent(ConnectService.APP_COMMAND_NEXT));
    }

    public void OnPrev_Click(View e){
        sendBroadcast(new Intent(ConnectService.APP_COMMAND_PREV));
    }

    public void OnMute_Click(View e){
        sendBroadcast(new Intent(ConnectService.APP_COMMAND_MUTE));
    }

    public void OnVolumeUp_Click(View e){
        sendBroadcast(new Intent(ConnectService.APP_COMMAND_VOLUP));
    }

    public void OnVolumeDown_Click(View e){
        sendBroadcast(new Intent(ConnectService.APP_COMMAND_VOLDOWN));
    }
}
