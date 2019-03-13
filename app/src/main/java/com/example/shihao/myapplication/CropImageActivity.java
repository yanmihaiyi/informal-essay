package com.example.shihao.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.github.cropbitmap.LikeQQCropView;

public class CropImageActivity extends AppCompatActivity implements View.OnClickListener {
    private LikeQQCropView likeView;
    SeekBar sb;
    Button btHorizontalFlip;
    Button btVerticalFlip;
    Button btBoth;
    Button btReset;
    Button btClip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);
        initView();
    }

    private void initView() {
        likeView = findViewById(R.id.likeView);
        sb = findViewById(R.id.sb);

        btHorizontalFlip = findViewById(R.id.btHorizontalFlip);
        btHorizontalFlip.setOnClickListener(this);

        btVerticalFlip = findViewById(R.id.btVerticalFlip);
        btVerticalFlip.setOnClickListener(this);

        btBoth = findViewById(R.id.btBoth);
        btBoth.setOnClickListener(this);

        btReset = findViewById(R.id.btReset);
        btReset.setOnClickListener(this);

        btClip = findViewById(R.id.btClip);
        btClip.setOnClickListener(this);


        //压缩bitmap宽度至1080
//        Bitmap bitmap = LikeQQCropViewUtils.compressBitmapForWidth(this, R.drawable.bird, 1080);
        //如果你通过储存路径从手机相册直接获取图片(未压缩)，在保证bitmap不会oom的情况下，可以直接调用setBitmap方法
        //否则乖乖调用  以下方法(这些方法可以防止OOM)
        //setBitmap(多参)
        //setBitmapForHeight()
        //setBitmapForWidth()
        //setBitmapForScale()
        Uri uri = getIntent().getParcelableExtra("uri");
        likeView.setBitmapForWidth(uri,1920,CropImageActivity.this);
        /*如果手机相册的图片出现旋转的情况*/
        /* likeView.setBitmapForWidth(filePath,1080);
        int degree = likeView.readPictureDegree("filePath");
        Bitmap oldBitmap = likeView.getBitmap();
        Bitmap rotateBitmap = likeView.rotateBitmap(degree, oldBitmap);
        likeView.setBitmap(rotateBitmap);*/

        /*或者使用下面4种方法*/
        //likeView.setBitmapForHeightToRotate(filePath,height);
        //likeView.setBitmapForWidthToRotate(filePath,width);
        //likeView.setBitmapToRotate(filePath,width,height);
        //likeView.setBitmapForScaleToRotate(filePath,4);


        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //动态改变裁剪框圆角
                float cropWidth = likeView.getClipWidth() / 2;
                float newRadius = progress * 1f / sb.getMax() * cropWidth;
                likeView.setRadius(newRadius);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btHorizontalFlip:
                //水平翻转
                likeView.horizontalFlip();
                break;
            case R.id.btVerticalFlip:
                //垂直翻转
                likeView.verticalFlip();
                break;
            case R.id.btBoth:
                //垂直水平翻转
                likeView.verticalAndHorizontalFlip();
                break;
            case R.id.btReset:
                //重置图片位置
                likeView.reset();
                break;
            case R.id.btClip:
                //裁剪
                MainActivity.resultBitmap = likeView.clip();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }
}
