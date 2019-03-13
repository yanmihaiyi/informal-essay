package com.example.shihao.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shihao.myapplication.function.Aschool;
import com.example.shihao.myapplication.function.Bschool;
import com.example.shihao.myapplication.function.School;
import com.example.shihao.myapplication.receiver.BlueStateReceiver;
import com.example.shihao.myapplication.utils.StatusBarUtils;
import com.example.shihao.myapplication.view.ButtomDialogView;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends Activity {

    @BindView(R.id.aschool)
    Button Aschool;
    @BindView(R.id.bschool)
    Button Bschool;
    @BindView(R.id.turn)
    Button turn;
    @BindView(R.id.turnToNewActivity)
    Button turnToNewActivity;
    @BindView(R.id.showDialog)
    Button showDialog;
    @BindView(R.id.blue_connect)
    Button blueConnect;
    @BindView(R.id.change_status)
    Button changeStatus;
    @BindView(R.id.image)
    ImageView image;
    private boolean statusBarMode = false;
    private final int ALBUM_OK = 1, CAMERA_OK = 2, CUT_OK = 3;
    private Uri contentUri;
    private Unbinder unbinder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        Aschool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                School schoolA = new Aschool();
                schoolA.findTeacher().sex();
                schoolA.findStudent().sex();
                System.out.println("A---------------");
            }
        });
        Bschool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                School schoolB = new Bschool();
                schoolB.findTeacher().sex();
                schoolB.findStudent().sex();
                System.out.println("B---------------");
            }
        });
        turn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BaseWebActivity.class);
                startActivity(intent);
            }
        });
        turnToNewActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MyActivity.class);
                startActivity(intent);
            }
        });
        showDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        changeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleStatusBarMode();
            }
        });
        blueConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BlueMainActivity.class);
                startActivity(intent);
            }
        });

        // 定义拍照后存放图片的文件位置和名称，使用完毕后可以方便删除
        //注册蓝牙监听
        BlueStateReceiver.registerReceiver(MainActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private void showDialog() {
        View view = View.inflate(MainActivity.this, R.layout.dialog_view, null);
        final Dialog dialog = new ButtomDialogView(MainActivity.this, view, false, true);
        dialog.show();
        TextView textView1 = view.findViewById(R.id.text1);
        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
                dialog.dismiss();
            }
        });
        TextView textView2 = view.findViewById(R.id.text2);
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAlbum();
                dialog.dismiss();
            }
        });
        TextView textView3 = view.findViewById(R.id.text3);
        textView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TestActivity activity = TestActivity.getInstance();
                activity.show(MainActivity.this);
                dialog.dismiss();
            }
        });

    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                    123);
            Log.e("Album", "我没有权限啊");
        } else {
            Log.e("Album", "我有权限啊");
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/test/" + System.currentTimeMillis() + ".jpg");
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //改变Uri  com.xykj.customview.fileprovider注意和xml中的一致
        contentUri = FileProvider.getUriForFile(this, "com.example.shihao.myapplication.fileprovider", file);
        //添加权限
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        startActivityForResult(intent, CAMERA_OK);
    }

    private void openAlbum() {
        Intent albumIntent = new Intent(Intent.ACTION_PICK, null);
        /**
         * 下面这句话，与其它方式写是一样的效果，如果：
         * intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
         * intent.setType(""image/*");设置数据类型
         * 要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
         */
        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(albumIntent, ALBUM_OK);
    }

    /**
     * 切换状态栏模式
     */
    public void toggleStatusBarMode() {
        Window window = getWindow();
        if (window == null) return;
        if (statusBarMode) {
            statusBarMode = false;
            StatusBarUtils.setLightMode(window);
        } else {
            statusBarMode = true;
            StatusBarUtils.setDarkMode(window);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("requestCode = " + requestCode);
        switch (requestCode) {
            // 如果是直接从相册获取
            case ALBUM_OK:
                //从相册中获取到图片了，才执行裁剪动作
                if (data != null) {
                    Intent intent = new Intent(MainActivity.this, CropImageActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("uri", data.getData());
                    intent.putExtras(bundle);
                    startActivityForResult(intent, CUT_OK);
                }
                break;
            // 如果是调用相机拍照时
            case CAMERA_OK:
                // 当拍照到照片时进行裁减，否则不执行操作
                if (contentUri != null) {
                    Intent intent = new Intent(MainActivity.this, CropImageActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("uri", contentUri);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, CUT_OK);
                }
                break;
            case 3:
                image.setImageBitmap(resultBitmap);
                break;
            default:
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static Bitmap resultBitmap;
}
