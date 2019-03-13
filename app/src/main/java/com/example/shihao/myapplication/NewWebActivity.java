package com.example.shihao.myapplication;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnScrollChangeListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Toast;


import com.example.shihao.myapplication.view.CookieTitleBar;
import com.example.shihao.myapplication.response.MoreItem;
import com.example.shihao.myapplication.view.PopWindow;
import com.example.shihao.myapplication.view.ProgressWebView;

import java.util.ArrayList;
import java.util.List;

public class NewWebActivity extends BaseWebActivity {
    private ProgressWebView webView;
    private CookieTitleBar titleBar;
    private int navType = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_web);
        webView = findViewById(R.id.webView);
        titleBar = findViewById(R.id.title_bar);
        titleBar.setImmersive(getWindow());
        titleBar.setLeftImageResource(R.mipmap.ic_back);
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressedBack();
            }
        });
//        webView.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public void onReceivedTitle(WebView view, String title) {
//                super.onReceivedTitle(view, title);
//                titleBar.setTitle(title);
//            }
//        });
        if (navType == 1) {
            titleBar.setTitleTransparent();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                webView.setOnScrollChangeListener(new OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                        if (i1 > 0) {
                            titleBar.setBackgroundResource(R.color.theme);//标题栏颜色渐变
                        }
                        float f = (i1 + 0f) / 450;//滑动距离450px
                        Log.d("dddddd", f + "");
                        if (f > 1) {
                            titleBar.setLeftImageResource(R.mipmap.ic_back);
                            titleBar.setTitleColor(Color.parseColor("#ffffff"));
                        } else {
                            titleBar.setLeftImageResource(R.mipmap.ic_more);
                            titleBar.setTitleColor(Color.parseColor("#1a1a1a"));
                        }
                        if (f > 1) {
                            f = 1f;
                        }
                        if (f < 0) {
                            f = 0;
                        }
                        titleBar.setBackgroundColor(changeAlpha(ContextCompat.getColor(NewWebActivity.this, R.color.theme), (int) (f * 1 * 0xff)));
                    }
                });
            }
        }

        titleBar.addAction(new CookieTitleBar.ImageAction(R.mipmap.ic_more) {
            @Override
            public void performAction(View view) {
                final List<MoreItem> moreItemList = new ArrayList<MoreItem>();
                MoreItem moreItem = new MoreItem("aaaa", R.mipmap.ic_more);
                moreItemList.add(moreItem);
                MoreItem moreItem1 = new MoreItem("bbbb", R.mipmap.ic_more);
                moreItemList.add(moreItem1);
                AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (moreItemList.get(i).getName().equals("aaaa")) {
                            Toast.makeText(NewWebActivity.this, "aaaa click", Toast.LENGTH_SHORT).show();
                        }
                        if (moreItemList.get(i).getName().equals("bbbb")) {
                            Toast.makeText(NewWebActivity.this, "bbbb click", Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                PopWindow.showPopWindow(view, NewWebActivity.this, moreItemList, listener);
            }
        });

        webView.loadUrl("https://www.jianshu.com/p/28d7ffd9eda4");
    }

    private void pressedBack() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            finish();
        }

    }

    public static int changeAlpha(int color, int alpha) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        return Color.argb(alpha, red, green, blue);
    }

}
