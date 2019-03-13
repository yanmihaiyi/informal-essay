package com.example.shihao.myapplication.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.example.shihao.myapplication.response.MoreItem;

import java.util.List;

import static com.example.shihao.myapplication.R.*;

public class PopWindow {
    public PopWindow() {
    }

    public static void showPopWindow(View view, Context mContext, List<MoreItem> itemList, AdapterView.OnItemClickListener listener) {
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(mContext).inflate(
                layout.pop_window, null);
        ListView listView = contentView.findViewById(id.pop_list);
        PopWindowAdapter adapter = new PopWindowAdapter(mContext, layout.pop_list_item, itemList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(listener);
        final PopupWindow popupWindow = new PopupWindow(contentView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setTouchable(true);

        popupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Log.i("mengdd", "onTouch : ");

                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        popupWindow.setBackgroundDrawable(mContext.getResources().getDrawable(color.white));

        // 设置好参数之后再show
        popupWindow.showAsDropDown(view);

    }

    static class PopWindowAdapter extends ArrayAdapter {
        private final int resourceId;

        public PopWindowAdapter(Context context, int textViewResourceId, List<MoreItem> objects) {
            super(context, textViewResourceId, objects);
            resourceId = textViewResourceId;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            MoreItem moreItem = (MoreItem) getItem(position);
            View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            ImageView imageView = view.findViewById(id.pop_list_item_image);
            TextView textView = view.findViewById(id.pop_list_item_name);
            imageView.setImageResource(moreItem.getImageId());
            textView.setText(moreItem.getName());
            return view;
        }
    }
}
