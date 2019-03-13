package com.example.shihao.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shihao.myapplication.function.NetSpeed;
import com.example.shihao.myapplication.function.NetSpeedTimer;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;

public class MyActivity extends Activity implements Handler.Callback {
    private TextView text, text1, memoryText, batteryText;
    private Button memoryButton, pickPhoto;
    private NetSpeedTimer netSpeedTimer;
    private ImageView photoImage;
    private static final int IMAGE_REQUEST_CODE = 1001;
    private static final int CROP_REQUEST_CODE = 1002;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MyActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        text = findViewById(R.id.text);
        text1 = findViewById(R.id.text1);
        memoryText = findViewById(R.id.memory_text);
        memoryButton = findViewById(R.id.memory_button);
        batteryText = findViewById(R.id.battery_text);
        photoImage = findViewById(R.id.photo_image);
        pickPhoto = findViewById(R.id.pick_photo);
        pickPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //在这里跳转到手机系统相册里面
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE_REQUEST_CODE);
            }
        });
        Handler handler = new Handler(this);
        netSpeedTimer = new NetSpeedTimer(this, new NetSpeed(), handler).setDelayTime(500).setPeriodTime(1000);
        netSpeedTimer.startSpeedTimer();
        memoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMemory();
                setback();
            }
        });
        registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (getLocation() != null) {
            text1.setText("经度：" + getLocation().getLongitude() + " 纬度：" + getLocation().getLongitude());
        } else {
            text1.setText("经度：" + 0 + " 纬度：" + 0);
            getLocation();
        }
    }

    public void setback() {
        TestActivity testActivity = TestActivity.getInstance();
        Intent intent = new Intent();
        intent.putExtra("data",1111111);
        testActivity.onResult(1000,intent);
        finish();
    }

    @Nullable
    private Location getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providerList = locationManager.getProviders(true);
        String provider;
        if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
            text1.setText("无可用定位工具");
            return null;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        return location;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IMAGE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {//resultcode是setResult里面设置的code值
                    try {
                        Uri selectedImage = data.getData(); //获取系统返回的照片的Uri
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);//从系统表中查询指定Uri对应的照片
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String path = cursor.getString(columnIndex);  //获取照片路径
                        cursor.close();
                        Bitmap bitmap = BitmapFactory.decodeFile(path);
                        SharedPreferences sharedPreferences = getSharedPreferences("bitmap", 0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("bitmap", bitmapToString(bitmap));
                        editor.commit();
                        Intent intent = new Intent(MyActivity.this, CropPhotoActivity.class);
                        startActivityForResult(intent, CROP_REQUEST_CODE);
                    } catch (Exception e) {
                        // TODO Auto-generatedcatch block
                        e.printStackTrace();
                    }
                }
                break;
            case CROP_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    SharedPreferences sharedPreferences = getSharedPreferences("bitmap", 0);
                    String bitmap = sharedPreferences.getString("bitmap1", " ");
                    photoImage.setImageBitmap(stringToBitmap(bitmap));
                }
                break;
        }
    }


    public String bitmapToString(Bitmap bitmap) {
        //将Bitmap转换成字符串
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return string;

    }

    public Bitmap stringToBitmap(String string) {
        // 将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
                    bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case NetSpeedTimer.NET_SPEED_TIMER_DEFAULT:
                String speed = (String) message.obj;
                text.setText(speed);
                break;

            default:
                break;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        if (null != netSpeedTimer) {
            netSpeedTimer.stopSpeedTimer();
        }
        super.onDestroy();
    }

    private void getMemory() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        //最大分配内存获取方法2
        float maxMemory = (float) (memoryInfo.totalMem * 1.0 / (1024 * 1024 * 1024));
        //剩余内存
        float totalMemory = (float) (memoryInfo.availMem * 1.0 / (1024 * 1024 * 1024));
        //已用内存
        float freeMemory = (float) ((memoryInfo.totalMem - memoryInfo.availMem) * 1.0 / (1024 * 1024 * 1024));
        memoryText.setText("最大分配：" + String.valueOf(getBigDecimal(maxMemory)) + " G" + "当前剩余：" + String.valueOf(getBigDecimal(totalMemory)) + " G");
    }

    private float getBigDecimal(float f) {
        BigDecimal b = new BigDecimal(f);
        float f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        return f1;
    }

    /* 创建广播接收器 */
    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        private int BatteryN;        //目前电量
        private int BatteryV;        //电池电压
        private double BatteryT;        //电池温度
        private String BatteryStatus;    //电池状态
        private String BatteryTemp;        //电池使用情况
        private String result;

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            /*
             * 如果捕捉到的action是ACTION_BATTERY_CHANGED， 就运行onBatteryInfoReceiver()
             */
            if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                BatteryN = intent.getIntExtra("level", 0);      //目前电量
                BatteryV = intent.getIntExtra("voltage", 0);  //电池电压
                BatteryT = intent.getIntExtra("temperature", 0);  //电池温度

                switch (intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN)) {
                    case BatteryManager.BATTERY_STATUS_CHARGING:
                        BatteryStatus = "充电状态";
                        break;
                    case BatteryManager.BATTERY_STATUS_DISCHARGING:
                        BatteryStatus = "放电状态";
                        break;
                    case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                        BatteryStatus = "未充电";
                        break;
                    case BatteryManager.BATTERY_STATUS_FULL:
                        BatteryStatus = "充满电";
                        break;
                    case BatteryManager.BATTERY_STATUS_UNKNOWN:
                        BatteryStatus = "未知道状态";
                        break;
                }

                switch (intent.getIntExtra("health", BatteryManager.BATTERY_HEALTH_UNKNOWN)) {
                    case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                        BatteryTemp = "未知错误";
                        break;
                    case BatteryManager.BATTERY_HEALTH_GOOD:
                        BatteryTemp = "状态良好";
                        break;
                    case BatteryManager.BATTERY_HEALTH_DEAD:
                        BatteryTemp = "电池没有电";
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                        BatteryTemp = "电池电压过高";
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                        BatteryTemp = "电池过热";
                        break;
                }
                batteryText.setText("目前电量为" + BatteryN + "% --- " + BatteryStatus + "\n" + "电压为" + BatteryV + "mV --- " + BatteryTemp + "\n" + "电池温度为" + (BatteryT * 0.1) + "℃");

            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MyActivity.this, "已授权", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MyActivity.this, "拒绝授权", Toast.LENGTH_SHORT).show();
        }
    }
}
