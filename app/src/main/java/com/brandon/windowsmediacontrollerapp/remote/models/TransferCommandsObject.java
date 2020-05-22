package com.brandon.windowsmediacontrollerapp.remote.models;

public class TransferCommandsObject {
    public String Command;
    public String Value;

    public TransferCommandsObject(){

    }

    public TransferCommandsObject(String Command, String Value){
        this.Command = Command;
        this.Value = Value;
    }

    @Override
    public String toString() {
        return "TranferCommandsObject{" +
                "Command='" + Command + '\'' +
                ", Value='" + Value + '\'' +
                '}';
    }
}
