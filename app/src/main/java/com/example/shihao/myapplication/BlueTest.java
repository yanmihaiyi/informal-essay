package com.example.shihao.myapplication;

import android.app.Activity;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class BlueTest extends Activity{
    private static final String TAG = "BluetoothA2DPTest";
    private BroadcastReceiver mBroadcastReceiver;
    private BluetoothA2dp mBluetoothA2dp;
    private BluetoothAdapter mBluetoothAdapter;
    private String DEVICE_NAME = "KUWO_K1";
    private BluetoothDevice mBluetoothDevice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue);
        initParameters();
    }

    private void initParameters(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null){
            Log.e(TAG,"have no bluetooth adapter.");
            return;
        }

        if(!mBluetoothAdapter.isEnabled()){
            mBluetoothAdapter.enable();
        }else{
            //开始搜索附近蓝牙
            startDiscovery();
            //绑定BluetoothA2DP，获得service
            getBluetoothA2DP();
        }

        //监听广播
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                BluetoothDevice device;
                switch (intent.getAction()) {
                    case BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED:
                        //<editor-fold>
                        switch (intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, -1)) {
                            case BluetoothA2dp.STATE_CONNECTING:
                                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                                Log.i(TAG, "device: " + device.getName() +" connecting");
                                break;
                            case BluetoothA2dp.STATE_CONNECTED:
                                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                                Log.i(TAG, "device: " + device.getName() +" connected");
                                //连接成功，开始播放
                                break;
                            case BluetoothA2dp.STATE_DISCONNECTING:
                                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                                Log.i(TAG, "device: " + device.getName() +" disconnecting");
                                break;
                            case BluetoothA2dp.STATE_DISCONNECTED:
                                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                                Log.i(TAG, "device: " + device.getName() +" disconnected");
//                                setResultPASS();
                                break;
                            default:
                                break;
                        }
                        //</editor-fold>
                        break;
                    case BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED:
                        //<editor-fold>
                        int state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, -1);
                        switch (state) {
                            case BluetoothA2dp.STATE_PLAYING:
                                Log.i(TAG, "state: playing.");
                                break;
                            case BluetoothA2dp.STATE_NOT_PLAYING:
                                Log.i(TAG, "state: not playing");
                                break;
                            default:
                                Log.i(TAG, "state: unkown");
                                break;
                        }
                        //</editor-fold>
                        break;
                    case BluetoothDevice.ACTION_FOUND:
                        //<editor-fold>
                        device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        int deviceClassType = device.getBluetoothClass().getDeviceClass();
                        //找到指定的蓝牙设备
                        if ((deviceClassType == BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET
                                || deviceClassType == BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES)
                                && device.getName().equals(DEVICE_NAME)) {
                            Log.i(TAG, "Found device:" + device.getName());
                            mBluetoothDevice = device;
                            //start bond，开始配对
                            createBond();
                        }
                        //</editor-fold>
                        break;
                    case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                        //<editor-fold>
                        int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE,BluetoothDevice.BOND_NONE);
                        device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        switch (bondState){
                            case BluetoothDevice.BOND_BONDED:  //配对成功
                                Log.i(TAG,"Device:"+device.getName()+" bonded.");
                                mBluetoothAdapter.cancelDiscovery();  //取消搜索
                                connect();  //连接蓝牙设备
                                break;
                            case BluetoothDevice.BOND_BONDING:
                                Log.i(TAG,"Device:"+device.getName()+" bonding.");
                                break;
                            case BluetoothDevice.BOND_NONE:
                                Log.i(TAG,"Device:"+device.getName()+" not bonded.");
                                //不知道是蓝牙耳机的关系还是什么原因，经常配对不成功
                                //配对不成功的话，重新尝试配对
                                createBond();
                                break;
                            default:
                                break;

                        }

                        //</editor-fold>
                        break;
                    case BluetoothAdapter.ACTION_STATE_CHANGED:
                        //<editor-fold>
                        state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                        switch (state) {
                            case BluetoothAdapter.STATE_TURNING_ON:
                                Log.i(TAG, "BluetoothAdapter is turning on.");
                                break;
                            case BluetoothAdapter.STATE_ON:
                                Log.i(TAG, "BluetoothAdapter is on.");
                                //蓝牙已打开，开始搜索并连接service
                                startDiscovery();
                                getBluetoothA2DP();
                                break;
                            case BluetoothAdapter.STATE_TURNING_OFF:
                                Log.i(TAG, "BluetoothAdapter is turning off.");
                                break;
                            case BluetoothAdapter.STATE_OFF:
                                Log.i(TAG, "BluetoothAdapter is off.");
                                break;
                        }
                        //</editor-fold>
                        break;
                    default:
                        break;
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver, filter);
    }

    private void startDiscovery(){
        Log.i(TAG,"mBluetoothAdapter startDiscovery.");
        if(mBluetoothAdapter!=null && mBluetoothAdapter.isEnabled() && !mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.startDiscovery();
        }
    }

    private void getBluetoothA2DP(){
        Log.i(TAG,"getBluetoothA2DP");
        if(mBluetoothAdapter == null){
            return;
        }

        if(mBluetoothA2dp != null){
            return;
        }

        mBluetoothAdapter.getProfileProxy(this, new BluetoothProfile.ServiceListener() {
            @Override
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                if(profile == BluetoothProfile.A2DP){
                    //Service连接成功，获得BluetoothA2DP
                    mBluetoothA2dp = (BluetoothA2dp)proxy;
                }
            }

            @Override
            public void onServiceDisconnected(int profile) {

            }
        },BluetoothProfile.A2DP);
    }

    private void createBond() {
        Log.i(TAG, "createBond");
        mBluetoothDevice.createBond();
    }

    //connect和disconnect都是hide方法，普通应用只能通过反射机制来调用该方法
    private void connect(){
        Log.i(TAG,"connect");
        if(mBluetoothA2dp == null){
            return;
        }
        if(mBluetoothDevice == null){
            return;
        }

        try {
            Method connect = mBluetoothA2dp.getClass().getDeclaredMethod("connect", BluetoothDevice.class);
            connect.setAccessible(true);
            connect.invoke(mBluetoothA2dp,mBluetoothDevice);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            Log.e(TAG,"connect exception:"+e);
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnect();
        disableAdapter();
    }
    private void disconnect(){
        Log.i(TAG,"disconnect");
        if(mBluetoothA2dp == null){
            return;
        }
        if(mBluetoothDevice == null){
            return;
        }

        try {
            Method disconnect = mBluetoothA2dp.getClass().getDeclaredMethod("disconnect", BluetoothDevice.class);
            disconnect.setAccessible(true);
            disconnect.invoke(mBluetoothA2dp,mBluetoothDevice);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            Log.e(TAG,"connect exception:"+e);
            e.printStackTrace();
        }
    }

    //取消配对
    private void unPairAllDevices(){
        Log.i(TAG,"unPairAllDevices");
        for(BluetoothDevice device:mBluetoothAdapter.getBondedDevices()){
            try {
                Method removeBond = device.getClass().getDeclaredMethod("removeBond");
                removeBond.invoke(device);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    //注意，在程序退出之前（OnDestroy），需要断开蓝牙相关的Service
    //否则，程序会报异常：service leaks
    private void disableAdapter(){
        Log.i(TAG,"disableAdapter");
        if(mBluetoothAdapter == null){
            return;
        }

        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
        }

        //关闭ProfileProxy，也就是断开service连接
        mBluetoothAdapter.closeProfileProxy(BluetoothProfile.A2DP,mBluetoothA2dp);
        if(mBluetoothAdapter.isEnabled()){
            boolean ret = mBluetoothAdapter.disable();
            Log.i(TAG,"disable adapter:"+ret);
        }
    }
}
