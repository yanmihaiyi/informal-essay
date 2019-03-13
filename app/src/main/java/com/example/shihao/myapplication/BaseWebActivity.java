package com.example.shihao.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.shihao.myapplication.jsbridge.BridgeHandler;
import com.example.shihao.myapplication.jsbridge.BridgeWebView;
import com.example.shihao.myapplication.jsbridge.CallBackFunction;
import com.example.shihao.myapplication.response.JsCallbackReceiver;

public class BaseWebActivity extends Activity implements View.OnClickListener {
    private BridgeWebView webView;
    private Button javaToJsDefault,javaToJsSpec,turn,turn1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_web);
        webView = findViewById(R.id.webView);
        javaToJsDefault = findViewById(R.id.java_to_js_default);
        javaToJsDefault.setOnClickListener(this);
        javaToJsSpec = findViewById(R.id.java_to_js_spec);
        javaToJsSpec.setOnClickListener(this);
        turn = findViewById(R.id.turn);
        turn.setOnClickListener(this);
        turn1 = findViewById(R.id.turn1);
        turn1.setOnClickListener(this);


        //默认接收
        webView.setDefaultHandler(new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                String msg = "默认接收到js的数据：" + data;
                Toast.makeText(BaseWebActivity.this, msg, Toast.LENGTH_LONG).show();

                function.onCallBack("java默认接收完毕，并回传数据给js"); //回传数据给js
            }
        });
        //指定接收 submitFromWeb 与js保持一致
        webView.registerHandler("submitFromWeb", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                String msg = "指定接收到js的数据：" + data;
                Toast.makeText(BaseWebActivity.this, msg, Toast.LENGTH_LONG).show();

                function.onCallBack("java指定接收完毕，并回传数据给js"); //回传数据给js
            }
        });
        webView.registerHandler("newWeb", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                String msg = "指定接收到js的数据：" + data;
                Toast.makeText(BaseWebActivity.this, msg, Toast.LENGTH_LONG).show();

                function.onCallBack("java指定接收完毕，并回传数据给js"); //回传数据给js
                webView.loadUrl(data);
            }
        });
        webView.loadUrl("file:///android_asset/webtest.html");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.java_to_js_default:
                //默认接收
                webView.send("发送数据给js默认接收", new CallBackFunction() {
                    @Override
                    public void onCallBack(String data) { //处理js回传的数据
                        Toast.makeText(BaseWebActivity.this, data, Toast.LENGTH_LONG).show();
                        JsCallbackReceiver.handleCallbackData(BaseWebActivity.this,data);
                    }
                });
                break;
            case R.id.java_to_js_spec:
                //指定接收参数 functionInJs
                webView.callHandler("functionInJs", "发送数据给js指定接收", new CallBackFunction() {
                    @Override
                    public void onCallBack(String data) { //处理js回传的数据
                        Toast.makeText(BaseWebActivity.this, data, Toast.LENGTH_LONG).show();
                    }
                });
                break;
            case R.id.turn:
                Intent intent = new Intent(BaseWebActivity.this,NewWebActivity.class);
                startActivity(intent);
                break;
            case R.id.turn1:
                Intent intent1 = new Intent(BaseWebActivity.this,WebActivity.class);
                startActivity(intent1);
                break;
            default:
                break;
        }
    }
}
