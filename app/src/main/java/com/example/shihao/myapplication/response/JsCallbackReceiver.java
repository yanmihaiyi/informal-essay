package com.example.shihao.myapplication.response;

import android.content.Context;
import android.widget.Toast;

import com.example.shihao.myapplication.BaseWebActivity;

public class JsCallbackReceiver {
    public JsCallbackReceiver() {
    }

    public static void handleCallbackData(Context context, String data) {
        if (context.getClass().equals(BaseWebActivity.class)) {
            Toast.makeText(context, "JsCallbackReceiver-------" + data, Toast.LENGTH_LONG).show();
        }
    }
}
