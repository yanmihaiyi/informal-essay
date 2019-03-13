package com.example.shihao.myapplication.receiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class BlueStateReceiver {
    public static void registerReceiver(Context context) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable();
            }
            context.registerReceiver(mReceiver, makeFilter());
        }
    }

    private static IntentFilter makeFilter() {
        IntentFilter filter = new IntentFilter();
        //监听手机本身蓝牙状态的广播
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        //蓝牙设备配对和解除配对时发送
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        //监听蓝牙设备连接和连接断开的广播
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        return filter;
    }

    private static BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState) {
                        case BluetoothAdapter.STATE_TURNING_ON:
                            Log.d("aaa", "STATE_TURNING_ON 手机蓝牙正在开启");
                            break;
                        case BluetoothAdapter.STATE_ON:
                            Log.d("aaa", "STATE_ON 手机蓝牙开启");
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            Log.d("aaa", "STATE_TURNING_OFF 手机蓝牙正在关闭");
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            Log.d("aaa", "STATE_OFF 手机蓝牙关闭");
                            break;
                    }
                    break;
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String name = device.getName();
                    Log.d("aaa", "device name: " + name);
                    int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
                    switch (state) {
                        case BluetoothDevice.BOND_NONE:
                            Log.d("aaa", "BOND_NONE 删除配对");
                            break;
                        case BluetoothDevice.BOND_BONDING:
                            Log.d("aaa", "BOND_BONDING 正在配对");
                            break;
                        case BluetoothDevice.BOND_BONDED:
                            Log.d("aaa", "BOND_BONDED 配对成功");
                            break;
                    }
                    break;
                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    BluetoothDevice connectedDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Log.d("aaa", connectedDevice.getName() + " ACTION_ACL_CONNECTED 已连接");
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    BluetoothDevice disConnectDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Log.d("aaa", disConnectDevice.getName() + " ACTION_ACL_DISCONNECTED 断开连接");
                    break;
            }
        }
    };

}
