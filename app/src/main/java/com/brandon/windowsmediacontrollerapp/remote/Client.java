package com.brandon.windowsmediacontrollerapp.remote;

import com.brandon.windowsmediacontrollerapp.remote.models.DataBufferModel;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    private Socket clientSocket;
    private BufferedOutputStream out;
    private BufferedInputStream in;

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new BufferedOutputStream(clientSocket.getOutputStream());
        in = new BufferedInputStream(clientSocket.getInputStream());
    }

    public boolean isConnected(){
        return clientSocket.isConnected() && !clientSocket.isClosed();
    }

    public void sendResponse(DataBufferModel msg) throws IOException {
        System.out.println("Sending Response with id: " + msg.DataId.toString());
        for(int i = 1; i <= msg.SeriesLength; i++){
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(new byte[] { 0x1A, (byte)msg.SeriesLength, (byte)i });
            outputStream.write(ServerConnection.getBytesFromUUID(msg.DataId));
            outputStream.write(msg.BufferedData.get(i));

            out.write(outputStream.toByteArray());
            out.flush();
        }
    }

    public void sendCommand(DataBufferModel msg) throws IOException {
        System.out.println("Sending Command with id: " + msg.DataId.toString());
        for(int i = 1; i <= msg.SeriesLength; i++){
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(new byte[] { 0x1B, (byte)msg.SeriesLength, (byte)i });
            outputStream.write(ServerConnection.getBytesFromUUID(msg.DataId));
            outputStream.write(msg.BufferedData.get(i));

            out.write(outputStream.toByteArray());
            out.flush();
        }
    }

    public void sendData(DataBufferModel msg) throws IOException {
        System.out.println("Sending Data with id: " + msg.DataId.toString());
        for(int i = 1; i <= msg.SeriesLength; i++){
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(new byte[] { 0x1C, (byte)msg.SeriesLength, (byte)i });
            outputStream.write(ServerConnection.getBytesFromUUID(msg.DataId));
            outputStream.write(msg.BufferedData.get(i));

            out.write(outputStream.toByteArray());
            out.flush();
        }
    }

    public byte[] read() throws IOException {
        byte[] charArray = new byte[2048];
        int read = in.read(charArray, 0, 2048);

        return charArray;
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }
}
