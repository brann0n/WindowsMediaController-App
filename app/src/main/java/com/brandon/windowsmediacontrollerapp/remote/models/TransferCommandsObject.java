package com.brandon.windowsmediacontrollerapp.remote.models;

public class TransferCommandsObject {
    public String Command;
    public String Value;

    @Override
    public String toString() {
        return "TranferCommandsObject{" +
                "Command='" + Command + '\'' +
                ", Value='" + Value + '\'' +
                '}';
    }
}
