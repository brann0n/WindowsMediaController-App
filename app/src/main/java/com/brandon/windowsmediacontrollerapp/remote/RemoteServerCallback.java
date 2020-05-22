package com.brandon.windowsmediacontrollerapp.remote;

import com.brandon.windowsmediacontrollerapp.remote.models.TransferCommandsObject;

public interface RemoteServerCallback {
    void OnDataReceived(TransferCommandsObject object);
    void OnCommandReceived(TransferCommandsObject object);
    void OnResponseReceived(TransferCommandsObject object);
}
