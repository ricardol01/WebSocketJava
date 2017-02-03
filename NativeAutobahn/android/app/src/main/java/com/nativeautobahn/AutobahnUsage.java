package com.nativeautobahn;

import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.Date;

import de.tavendo.autobahn.Autobahn;
import de.tavendo.autobahn.AutobahnConnection;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

import static android.content.ContentValues.TAG;

public class AutobahnUsage extends ReactContextBaseJavaModule {
    ReactApplicationContext mReactContext;
    public AutobahnUsage(ReactApplicationContext reactContext){
        super(reactContext);
        mReactContext=reactContext;
    }
    @Override
    public String getName() {
        return "AutobahnUsage";
    }

    private final WebSocketConnection mConnection = new WebSocketConnection();

//    public class  MSG{
//        private int number;
//        private  String string;
//    }
    private void start() {


        final String wsuri = "ws://echo.websocket.org/";

        try {
            mConnection.connect(wsuri, new WebSocketHandler() {

                @Override
                public void onOpen() {
                    Log.d(TAG, "Status: Connected to " + wsuri);
                    String string="Status: Connected to " + wsuri;
                    mReactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("Connect", string);
  //                  Toast.makeText(getReactApplicationContext(),"Connect to"+wsuri,Toast.LENGTH_LONG).show();
                    mConnection.sendTextMessage("Hello, world!");
                }

                @Override
                public void onTextMessage(final String payload) {
                    Log.d(TAG, "Got echo: " + payload);
                    MSG msg=new MSG();
                    msg.setNum(1);
                    msg.setContent("Got echo: " + payload);

                    msg.num=1;
                    msg.content="Got echo: " + payload;
                    String jsonStr= JSON.toJSONString(msg);
                    mReactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("GotData", jsonStr);
                    Toast.makeText(getReactApplicationContext(), jsonStr,Toast.LENGTH_LONG).show();
                }

                @Override
                public void onClose(int code, String reason) {
                    Log.d(TAG, "Connection lost.");
                    String string="Connection lost";
                    mReactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("ConnectionLost",string);

                }
            });
        } catch (WebSocketException e) {

            Log.d(TAG, e.toString());
        }

    }

    @ReactMethod
    public void Test()
    {
        start();
        try{
            Thread thread=Thread.currentThread();
            thread.sleep(3000);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        mConnection.disconnect();
        Toast.makeText(getReactApplicationContext(),"Lost Connection",Toast.LENGTH_LONG).show();
    }
}
