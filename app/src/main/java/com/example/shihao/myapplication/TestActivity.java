package com.example.shihao.myapplication;


import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class TestActivity {
    Context mContext;

    private static TestActivity testActivity;

    private TestActivity() {
    }

    public static TestActivity getInstance() {
        if (testActivity == null) {
            synchronized (TestActivity.class) {
                if (testActivity == null) {
                    testActivity = new TestActivity();
                }
            }
        }
        return testActivity;
    }

    public void show(Context context) {
        this.mContext = context;
        Toast.makeText(mContext, "TestActivity", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(mContext, MyActivity.class);
        mContext.startActivity(intent);
    }

    public void onResult(int resultCode, Intent data) {
        if (resultCode == 1000 && mContext != null) {
            Toast.makeText(mContext, "onActivityResult" + data.getIntExtra("data", 00), Toast.LENGTH_SHORT).show();
        }
    }
}
