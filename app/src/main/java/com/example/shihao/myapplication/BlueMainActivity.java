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
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.blakequ.bluetooth_manager_lib.BleManager;
import com.blakequ.bluetooth_manager_lib.connect.multiple.MultiConnectManager;
import com.blankj.utilcode.util.ToastUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @date: 2019/12/4
 * @author shihao
 */
public class BlueMainActivity extends Activity {
    private static final String TAG = "BlueMainActivity";
    private List<String> address;
    private List<String> name;
    private List<BluetoothDevice> needConnectDevices;
    private List<BluetoothDevice> bluetoothDeviceList;
    private BluetoothAdapter mBluetoothAdapter;
    private BroadcastReceiver mBroadcastReceiver;
    private ResultListAdapter mResultListAdapter;
    /**
     * 多设备连接
     */
    MultiConnectManager multiConnectManager;
    public static Map<Integer, Boolean> map;
    public static List<Integer> p;
    Button scan;
    private BluetoothA2dp mBluetoothA2dp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue);
        scan = findViewById(R.id.scan);
        final Button connect = findViewById(R.id.connect);
        ListView resultList = findViewById(R.id.result_list);

        address = new ArrayList<>();
        name = new ArrayList<>();
        needConnectDevices = new ArrayList<>();
        multiConnectManager = BleManager.getMultiConnectManager(this);
        bluetoothDeviceList = new ArrayList<>();
        map = new HashMap<>();
        p = new ArrayList<>();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mResultListAdapter = new ResultListAdapter(this, name);
        resultList.setAdapter(mResultListAdapter);


        resultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView text = view.findViewById(R.id.status);
                BluetoothDevice ads = bluetoothDeviceList.get(position);

                if (!p.contains(position)) {
                    text.setText("已选择");
                    needConnectDevices.add(ads);
                    p.add(position);
                    map.put(position, true);
                } else {
                    if (map.get(position)) {
                        text.setText("未连接");
                        needConnectDevices.remove(ads);
                        map.put(position, false);
                    } else {
                        text.setText("已选择");
                        needConnectDevices.add(ads);
                        map.put(position, true);
                    }
                }
            }
        });

        // 检查设备上是否支持蓝牙
        if (mBluetoothAdapter == null) {
            ToastUtils.showShort("设备不支持蓝牙");
            finish();
            return;
        }
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    Log.e(TAG, "have no bluetooth adapter.");
                    return;
                }

                if (!mBluetoothAdapter.isEnabled()) {
                    mBluetoothAdapter.enable();
                } else {
                    //开始搜索附近蓝牙
                    startDiscovery();
                    scan.setClickable(false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scan.setClickable(true);
                            mBluetoothAdapter.cancelDiscovery();
                            Log.e(TAG, "mBluetoothAdapter cancelDiscovery.");
                        }
                    }, 10 * 1000);
                }
            }
        });

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createBond();
            }
        });

        //绑定BluetoothA2DP，获得service
        getBluetoothA2DP();
        initParameters();
    }


    private void initParameters() {

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
                                Log.i(TAG, "device: " + device.getName() + " connecting");
                                break;
                            case BluetoothA2dp.STATE_CONNECTED:
                                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                                Log.i(TAG, "device: " + device.getName() + " connected");
                                //连接成功，开始播放
                                break;
                            case BluetoothA2dp.STATE_DISCONNECTING:
                                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                                Log.i(TAG, "device: " + device.getName() + " disconnecting");
                                break;
                            case BluetoothA2dp.STATE_DISCONNECTED:
                                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                                Log.i(TAG, "device: " + device.getName() + " disconnected");
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
                                || deviceClassType == BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES)) {
                            Log.i(TAG, "Found device:" + device.getName());
                            if (!address.contains(device.getAddress())) {
                                address.add(device.getAddress());
                                if (device.getName() != null) {
                                    name.add(device.getName());
                                } else {
                                    name.add(device.getAddress());
                                }
                                mResultListAdapter.notifyDataSetChanged();
                                bluetoothDeviceList.add(device);
                            }
                        }
                        //</editor-fold>
                        break;
                    case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                        //<editor-fold>
                        int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE);
                        device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        switch (bondState) {
                            case BluetoothDevice.BOND_BONDED:  //配对成功
                                Log.i(TAG, "Device:" + device.getName() + " bonded.");
                                mBluetoothAdapter.cancelDiscovery();  //取消搜索
                                connect();  //连接蓝牙设备
                                break;
                            case BluetoothDevice.BOND_BONDING:
                                Log.i(TAG, "Device:" + device.getName() + " bonding.");
                                break;
                            case BluetoothDevice.BOND_NONE:
                                Log.i(TAG, "Device:" + device.getName() + " not bonded.");
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

    private void startDiscovery() {
        Log.i(TAG, "mBluetoothAdapter startDiscovery.");
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled() && !mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.startDiscovery();
        }
    }

    private void getBluetoothA2DP() {
        Log.i(TAG, "getBluetoothA2DP");
        if (mBluetoothAdapter == null) {
            return;
        }

        if (mBluetoothA2dp != null) {
            return;
        }

        mBluetoothAdapter.getProfileProxy(this, new BluetoothProfile.ServiceListener() {
            @Override
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                if (profile == BluetoothProfile.A2DP) {
                    //Service连接成功，获得BluetoothA2DP
                    mBluetoothA2dp = (BluetoothA2dp) proxy;
                }
            }

            @Override
            public void onServiceDisconnected(int profile) {

            }
        }, BluetoothProfile.A2DP);
    }

    private void createBond() {
        new Thread() {
            @Override
            public void run() {
                if (needConnectDevices != null && needConnectDevices.size() > 0) {
                    for (int i = 0; i < needConnectDevices.size(); i++) {
                        needConnectDevices.get(i).createBond();
                        Log.i(TAG, needConnectDevices.get(i).getName() + "   createBond");
                    }
                }
            }
        }.start();
    }

    //connect和disconnect都是hide方法，普通应用只能通过反射机制来调用该方法
    private void connect() {
        Log.i(TAG, "connect");
        if (mBluetoothA2dp == null) {
            return;
        }
        if (needConnectDevices == null || needConnectDevices.size() < 1) {
            return;
        }

        try {
            for (int i = 0; i < needConnectDevices.size(); i++) {
                Method connect = mBluetoothA2dp.getClass().getDeclaredMethod("connect", BluetoothDevice.class);
                connect.setAccessible(true);
                connect.invoke(mBluetoothA2dp, needConnectDevices.get(i));
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            Log.e(TAG, "connect exception:" + e);
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnect();
        disableAdapter();
        unregisterReceiver(mBroadcastReceiver);
    }

    private void disconnect() {
        Log.i(TAG, "disconnect");
        if (mBluetoothA2dp == null) {
            return;
        }
        if (needConnectDevices == null || needConnectDevices.size() < 1) {
            return;
        }

        try {
            for (int i = 0; i < needConnectDevices.size(); i++) {
                Method disconnect = mBluetoothA2dp.getClass().getDeclaredMethod("disconnect", BluetoothDevice.class);
                disconnect.setAccessible(true);
                disconnect.invoke(mBluetoothA2dp, needConnectDevices.get(i));
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            Log.e(TAG, "connect exception:" + e);
            e.printStackTrace();
        }
    }

    //取消配对
    private void unPairAllDevices() {
        Log.i(TAG, "unPairAllDevices");
        for (BluetoothDevice device : mBluetoothAdapter.getBondedDevices()) {
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
    private void disableAdapter() {
        Log.i(TAG, "disableAdapter");
        if (mBluetoothAdapter == null) {
            return;
        }

        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }

        //关闭ProfileProxy，也就是断开service连接
        mBluetoothAdapter.closeProfileProxy(BluetoothProfile.A2DP, mBluetoothA2dp);
//        if (mBluetoothAdapter.isEnabled()) {
//            boolean ret = mBluetoothAdapter.disable();
//            Log.i(TAG, "disable adapter:" + ret);
//        }
    }


    static class ResultListAdapter extends BaseAdapter {
        private final List<String> name;
        private Context thisContext;

        public ResultListAdapter(Context context, List<String> objects) {
            thisContext = context;
            name = objects;
        }

        @Override
        public int getCount() {
            return name.size();
        }

        @Override
        public Object getItem(int position) {
            return null != name ? name.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) thisContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.result_item, null);
                viewHolder = new ViewHolder();
                viewHolder.blueName = convertView.findViewById(R.id.blueName);
                viewHolder.status = convertView.findViewById(R.id.status);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.blueName.setText(name.get(position));
            if (p.contains(position) && map.get(position)) {
                viewHolder.status.setText("已选择");
            } else {
                viewHolder.status.setText("未选择");
            }


            return convertView;
        }

        class ViewHolder {
            TextView blueName;
            TextView status;
        }
    }
}
