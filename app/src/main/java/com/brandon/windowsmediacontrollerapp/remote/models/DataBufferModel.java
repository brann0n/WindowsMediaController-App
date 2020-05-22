package com.brandon.windowsmediacontrollerapp.remote.models;

import java.util.HashMap;
import java.util.UUID;

public class DataBufferModel {
    public UUID DataId;
    public int SeriesLength;
    public int LatestSeries;
    public HashMap<Integer, byte[]> BufferedData;

    public DataBufferModel(){
        BufferedData = new HashMap<>();
    }
}
