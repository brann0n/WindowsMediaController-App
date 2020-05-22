package com.brandon.windowsmediacontrollerapp.remote;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.brandon.windowsmediacontrollerapp.remote.models.DataBufferModel;
import com.brandon.windowsmediacontrollerapp.remote.models.TransferCommandsObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ServerConnection {
    private Client client;
    private List<RemoteServerCallback> listeners;
    private List<DataBufferModel> buffers;
    private ConnectionPipeLine pipeLine;
    public ServerConnection(){
        this.client = new Client();
        this.pipeLine = new ConnectionPipeLine();
        this.listeners = new ArrayList<>();
        this.buffers = new ArrayList<>();
    }
    /**
     * adds the event listeners
     * @param toAdd event listener object
     */
    public void addListener(RemoteServerCallback toAdd) {
        listeners.add(toAdd);
    }
    private void OnCommandReceived(TransferCommandsObject obj){
        for(RemoteServerCallback callbacks: listeners){
            callbacks.OnCommandReceived(obj);
        }
    }
    private void OnDataReceived(TransferCommandsObject obj){
        for(RemoteServerCallback callbacks: listeners){
            callbacks.OnDataReceived(obj);
        }
    }
    private void OnResponseReceived(TransferCommandsObject obj){
        for(RemoteServerCallback callbacks: listeners){
            callbacks.OnResponseReceived(obj);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void startReceiver(){
        while(client.isConnected()){
            try {
                byte[] array = client.read();
                int length = array[1];
                int series = array[2];
                byte[] guidBytes = ConnectionPipeLine.SubArray(array, 3, 19);
                UUID guid = getGuidFromByteArray(guidBytes);
                if(!guid.toString().equals("00000000-0000-0000-0000-000000000000")){
                    DataBufferModel buffer = buffers.stream().filter(n -> n.DataId == guid).findAny().orElse(null);
                    if(buffer != null){
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        buffer.BufferedData.put(series, ConnectionPipeLine.SubArray(array, 19, 2028));
                        buffer.LatestSeries = series;
                    }
                    else{
                        buffer = new DataBufferModel();
                        buffer.BufferedData.put(series, ConnectionPipeLine.SubArray(array, 19, 2028));
                        buffer.LatestSeries = series;
                        buffer.DataId = guid;
                        buffer.SeriesLength = length;
                        buffers.add(buffer);
                    }

                    if(buffer.BufferedData.size() == buffer.SeriesLength){
                        if(array[0] == 0x1A){
                            //response
                            System.out.println("Response: " + guid.toString());
                            OnResponseReceived(pipeLine.BufferDeserialize(buffer));
                        }
                        else if(array[0] == 0x1B){
                            //command
                            System.out.println("Command: " + guid.toString());
                            OnCommandReceived(pipeLine.BufferDeserialize(buffer));
                        }
                        else if(array[0] == 0x1C){
                            //data
                            System.out.println("Data: " + guid.toString());
                            OnDataReceived(pipeLine.BufferDeserialize(buffer));
                        }
                    }
                }
                else{
                    Disconnect();
                }
            } catch (IOException e) {
                //disconnect
                e.printStackTrace();
                Disconnect();
                break; //breaks out of the while loop
            }
        }
    }
    private TransferCommandsObject createTransferObject(String command, String value){
        TransferCommandsObject model = new TransferCommandsObject();
        model.Command = command;
        model.Value = value;
        return model;
    }
    public void sendResponse(String command, String value) {
        try {
            client.sendResponse(pipeLine.BufferSerialize(createTransferObject(command, value)));
        } catch (IOException e) {
            e.printStackTrace();
            Disconnect();
        }
    }
    public void sendCommand(String command, String value) {
        try {
            client.sendCommand(pipeLine.BufferSerialize(createTransferObject(command, value)));
        } catch (IOException e) {
            e.printStackTrace();
            Disconnect();
        }
    }
    public void sendData(String command, String value) {
        try {
            client.sendData(pipeLine.BufferSerialize(createTransferObject(command, value)));
        } catch (IOException e) {
            e.printStackTrace();
            Disconnect();
        }
    }
    public static UUID getGuidFromByteArray(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long high = bb.getLong();
        long low = bb.getLong();
        return new UUID(high, low);
    }
    public static byte[] getBytesFromUUID(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());

        return bb.array();
    }
    public void Disconnect() {
        try {
            client.stopConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void Connect(String ip, int port) throws IOException {
        client.startConnection(ip, port);
    }
}
