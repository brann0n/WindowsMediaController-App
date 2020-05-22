package com.brandon.windowsmediacontrollerapp.remote;

import com.brandon.windowsmediacontrollerapp.remote.models.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

public class ConnectionPipeLine {

    public ConnectionPipeLine(){

    }

    public DataBufferModel BufferSerialize(TransferCommandsObject ObjectToSerialize) {
        String data = "{\"Command\":\"" + ObjectToSerialize.Command + "\",\"Value\":\"" + ObjectToSerialize.Value + "\"}";
        byte[] byteArray = data.getBytes();
        DataBufferModel buffer = new DataBufferModel();
        buffer.DataId = UUID.randomUUID();
        int count = 0;
        int bytesLeft = byteArray.length;
        int index = 0;
        int increment = 2029;
        while ((bytesLeft > 0)) {
            count++;
            byte[] subArray = SubArray(byteArray, index, increment);
            bytesLeft = (bytesLeft - increment);
            index = (index + increment);
            buffer.BufferedData.put(count, subArray);
        }

        buffer.SeriesLength = count;
        return buffer;
    }

    public TransferCommandsObject BufferDeserialize(DataBufferModel model) throws IOException {
        if(model.BufferedData.size() == model.SeriesLength){
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            for(int i = 1; i <= model.SeriesLength; i++){
                outputStream.write(model.BufferedData.get(i));
            }
            String data = new String(outputStream.toByteArray()).replaceAll("\0", "");
            TransferCommandsObject object = new TransferCommandsObject();
            String[] splitData = data.split("<SPLITPATTERN>", 2);
            object.Command = splitData[0];
            object.Value = splitData[1].trim();
            return object;
        }

        return null;
    }

    public static byte[] SubArray(byte[] data, int index, int end) {
        byte[] result;
        if(end > data.length - index)
        {
            result = Arrays.copyOfRange(data, index,data.length - index);
        }
        else
        {
            result = Arrays.copyOfRange(data, index, end);
        }

        return result;
    }
}
