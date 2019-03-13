package com.example.shihao.myapplication.function;

import android.graphics.RectF;
import android.support.annotation.NonNull;

import com.example.shihao.myapplication.function.Edge;

/**
 * 操控裁剪框的辅助类:操控裁剪框的缩放
 */
public class CropWindowScaleHelper {
    private Edge mHorizontalEdge;
    private Edge mVerticalEdge;


    CropWindowScaleHelper(Edge horizontalEdge, Edge verticalEdge) {
        mHorizontalEdge = horizontalEdge;
        mVerticalEdge = verticalEdge;
    }


    /**
     * 随着手指的移动而改变裁剪框的大小
     *
     * @param x         手指x方向的位置
     * @param y         手指y方向的位置
     * @param imageRect 用来表示图片边界的矩形
     */
    void updateCropWindow(float x,
                          float y,
                          @NonNull RectF imageRect) {

        if (mHorizontalEdge != null)
            mHorizontalEdge.updateCoordinate(x, y, imageRect);

        if (mVerticalEdge != null)
            mVerticalEdge.updateCoordinate(x, y, imageRect);
    }

}
