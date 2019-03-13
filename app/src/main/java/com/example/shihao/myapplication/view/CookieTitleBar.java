package com.example.shihao.myapplication.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.shihao.myapplication.R;
import com.example.shihao.myapplication.utils.PackageUtils;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.LinkedList;


/**
 * @author xiekun
 * @date 2018/07/05.
 * <p>
 * 自定义TitleBar
 */
public class CookieTitleBar extends ViewGroup implements View.OnClickListener {
    private static final String TAG = "CookieTitleBar";
    /**
     * 默认主标题字体大小
     */
    private static final int DEFAULT_MAIN_TEXT_SIZE = 18;
    /**
     * 默认副标题字体大小
     */
    private static final int DEFAULT_SUB_TEXT_SIZE = 12;
    /**
     * 默认标题字体颜色
     */
    private static final int DEFAULT_TITLE_TEXT_COLOR = Color.parseColor("#1a1a1a");
    /**
     * 默认两侧按钮字体大小
     */
    private static final int DEFAULT_ACTION_TEXT_SIZE = 12;
    /**
     * 默认两侧按钮字体颜色
     */
    private static final int DEFAULT_ACTION_TEXT_COLOR = Color.parseColor("#1a1a1a");
    /**
     * 默认标题栏高度
     */
    private static final int DEFAULT_TITLE_BAR_HEIGHT = 45;
    /**
     * 默认左侧图标大小
     */
    private static final int DEFAULT_ICON_SIZE = 36;
    /**
     * 默认action按钮内边距
     */
    private static final int DEFAULT_ACTION_PADDING = 10;
    /**
     * 默认外内边距
     */
    private static final int DEFAULT_OUT_PADDING = 0;
    /**
     * 默认标题栏背景色
     */
    private static final int DEFAULT_TITLE_BACKGROUND_COLOR = Color.parseColor("#ffda0a");
    /**
     * 状态栏资源标识
     */
    private static final String STATUS_BAR_HEIGHT_RES_NAME = "status_bar_height";

    private LinearLayout mLeftLayout;
    private ImageView mLeftImage;
    private ImageView mCenterDropImage;
    private ImageView mRightDropImage;
    private TextView mLeftText;
    private LinearLayout mRightLayout;
    private LinearLayout mCenterLayout;
    private TextView mCenterText;
    private TextView mSubTitleText;
    private View mCustomCenterView;
    private View mDividerView;

    private boolean mImmersive;

    private int mScreenWidth;
    private int mStatusBarHeight;
    private int mActionPadding;
    private int mOutPadding;
    private int mActionTextColor;
    private int mHeight;

    public Context mContext;

    public CookieTitleBar(Context context) {
        super(context);
        init(context);
    }

    public CookieTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CookieTitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        if (mImmersive) {
            mStatusBarHeight = getStatusBarHeight();
        }
        mActionPadding = px2dip(DEFAULT_ACTION_PADDING);
        mOutPadding = px2dip(DEFAULT_OUT_PADDING);
        mHeight = px2dip(DEFAULT_TITLE_BAR_HEIGHT);
        initView(context);
    }

    private void initView(Context context) {
        mLeftLayout = new LinearLayout(context);
        mCenterLayout = new LinearLayout(context);
        mRightLayout = new LinearLayout(context);
        mDividerView = new View(context);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);

        mLeftImage = new ImageView(context);
        mLeftText = new TextView(context);
        mLeftLayout.addView(mLeftImage, layoutParams);
        mLeftLayout.addView(mLeftText, layoutParams);

        mLeftText.setTextSize(DEFAULT_ACTION_TEXT_SIZE);
        mLeftText.setGravity(Gravity.CENTER);
        mLeftText.setSingleLine();
        mLeftText.setTextColor(DEFAULT_ACTION_TEXT_COLOR);

        LayoutParams imgLp = mLeftImage.getLayoutParams();
        imgLp.width = px2dip(DEFAULT_ICON_SIZE);
        imgLp.height = px2dip(DEFAULT_ICON_SIZE);
        mLeftImage.setLayoutParams(imgLp);
        mLeftImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        mLeftLayout.setPadding(mOutPadding, 0, mOutPadding, 0);
        mLeftLayout.setGravity(Gravity.CENTER_VERTICAL);

        mCenterDropImage = new ImageView(context);
        mCenterDropImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        mRightDropImage = new ImageView(context);
        mRightDropImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        mCenterText = new TextView(context);
        mSubTitleText = new TextView(context);
        mCenterLayout.addView(mCenterText);
        mCenterLayout.addView(mSubTitleText);

        mCenterLayout.setGravity(Gravity.CENTER);
        mCenterText.setTextSize(DEFAULT_MAIN_TEXT_SIZE);
        // 设置标题宽度默认最小为8个字的宽度
//        mCenterText.setMinimumWidth(px2dip(DEFAULT_MAIN_TEXT_SIZE * 8));
        mCenterText.setSingleLine();
        mCenterText.setGravity(Gravity.CENTER);
        mCenterText.setEllipsize(TextUtils.TruncateAt.END);
        mCenterText.setTextColor(DEFAULT_TITLE_TEXT_COLOR);

        mSubTitleText.setTextSize(DEFAULT_SUB_TEXT_SIZE);
        mSubTitleText.setSingleLine();
        mSubTitleText.setGravity(Gravity.CENTER);
        mSubTitleText.setEllipsize(TextUtils.TruncateAt.END);
        mSubTitleText.setTextColor(DEFAULT_TITLE_TEXT_COLOR);

        mRightLayout.setPadding(mOutPadding, 0, mOutPadding, 0);

        addView(mLeftLayout, layoutParams);
        addView(mCenterLayout);
        addView(mRightLayout, layoutParams);
        addView(mDividerView, new LayoutParams(LayoutParams.MATCH_PARENT, 1));
        // 默认标题栏背景色为应用主题色
        setBackgroundColor(getResources().getColor(R.color.theme));
    }

    /**
     * 设置沉浸式
     *
     * @param window
     */
    public void setImmersive(Window window) {
        boolean isImmersive = false;
        if (PackageUtils.hasKitKat() && !PackageUtils.hasLollipop()) {
            isImmersive = true;
            //透明状态栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
//            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        } else if (PackageUtils.hasLollipop()) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            isImmersive = true;
        }

        mImmersive = isImmersive;
        if (mImmersive) {
            mStatusBarHeight = getStatusBarHeight();
        } else {
            mStatusBarHeight = 0;
        }
    }

    /**
     * 设置沉浸式
     *
     * @param window
     * @param immersive
     */
    public void setImmersive(Window window, boolean immersive) {
        if (immersive) {
            boolean isImmersive = false;
            if (PackageUtils.hasKitKat() && !PackageUtils.hasLollipop()) {
                isImmersive = true;
                //透明状态栏
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                //透明导航栏
//            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            } else if (PackageUtils.hasLollipop()) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
                isImmersive = true;
            }

            mImmersive = isImmersive;
            if (mImmersive) {
                mStatusBarHeight = getStatusBarHeight();
            } else {
                mStatusBarHeight = 0;
            }
        } else {
            mStatusBarHeight = 0;
        }
    }

    /**
     * 设置高度
     *
     * @param height
     */
    public void setHeight(int height) {
        mHeight = height;
        setMeasuredDimension(getMeasuredWidth(), mHeight);
    }

    /**
     * 设置左侧按钮图片
     *
     * @param resId
     */
    public void setLeftImageResource(int resId) {
        Drawable icon = getResources().getDrawable(resId);
        Drawable tintIcon = DrawableCompat.wrap(icon);
//        DrawableCompat.setTintList(tintIcon, getResources().getColorStateList(R.color.white));
        mLeftImage.setImageDrawable(tintIcon);
    }

    /**
     * 设置左侧按钮点击监听
     *
     * @param l
     */
    public void setLeftClickListener(OnClickListener l) {
        mLeftLayout.setOnClickListener(l);
    }

    /**
     * 设置左侧按钮文字
     *
     * @param title
     */
    public void setLeftText(CharSequence title) {
        mLeftText.setText(title);
    }

    /**
     * 设置左侧按钮文字
     *
     * @param resid
     */
    public void setLeftText(int resid) {
        mLeftText.setText(resid);
    }

    /**
     * 设置左侧按钮文字大小
     *
     * @param size
     */
    public void setLeftTextSize(float size) {
        mLeftText.setTextSize(size);
    }

    /**
     * 设置左侧按钮文字颜色
     *
     * @param color
     */
    public void setLeftTextColor(int color) {
        mLeftText.setTextColor(color);
    }

    /**
     * 设置左侧按钮可见
     *
     * @param visible
     */
    public void setLeftVisible(boolean visible) {
        mLeftText.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置标题文字（仅主标题）
     *
     * @param title
     */
    public void setTitle(CharSequence title) {
        int index = title.toString().indexOf("\n");
        if (index > 0) {
            setTitle(title.subSequence(0, index), title.subSequence(index + 1, title.length()), LinearLayout.VERTICAL);
        } else {
            index = title.toString().indexOf("\t");
            if (index > 0) {
                setTitle(title.subSequence(0, index), "  " + title.subSequence(index + 1, title.length()), LinearLayout.HORIZONTAL);
            } else {
                mCenterText.setText(title);
                mSubTitleText.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 设置标题文字（主标题和副标题）
     *
     * @param title
     * @param subTitle
     * @param orientation
     */
    private void setTitle(CharSequence title, CharSequence subTitle, int orientation) {
        mCenterLayout.setOrientation(orientation);
        mCenterText.setText(title);

        mSubTitleText.setText(subTitle);
        mSubTitleText.setVisibility(View.VISIBLE);
    }

    /**
     * 首页标题栏标题旁添加下拉箭头
     *
     * @param l
     */
    public void addCenterDropDown(OnClickListener l) {
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        mCenterLayout.setOrientation(LinearLayout.HORIZONTAL);
        mCenterDropImage.setImageResource(R.mipmap.drop_big);
        mCenterDropImage.setPadding(20, 10, 0, 0);
        mCenterLayout.addView(mCenterDropImage, layoutParams);
        mCenterLayout.setOnClickListener(l);
    }

    /**
     * 首页标题栏时间旁添加下拉箭头
     */
    public void addRightDropDown() {
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        mRightLayout.setOrientation(LinearLayout.HORIZONTAL);
        mRightDropImage.setImageResource(R.mipmap.drop_samll);
        mRightDropImage.setPadding(0, 0, 50, 0);
        mRightLayout.addView(mRightDropImage, layoutParams);
    }

    /**
     * 设置中间（标题文字）点击监听
     *
     * @param l
     */
    public void setCenterClickListener(OnClickListener l) {
        mCenterLayout.setOnClickListener(l);
    }

    /**
     * 设置标题文字（仅主标题）
     *
     * @param resid
     */
    public void setTitle(int resid) {
        setTitle(getResources().getString(resid));
    }

    /**
     * 设置带透明度的背景颜色
     *
     * @param opacity
     * @param color
     */
    public void setOpacityBackgroundColor(int opacity, String color) {
        int a = opacity;
        String rStr = color.substring(0, 2);
        int r = Integer.parseInt(rStr, 16);
        String gStr = color.substring(2, 4);
        int g = Integer.parseInt(gStr, 16);
        String bStr = color.substring(4, 6);
        int b = Integer.parseInt(bStr, 16);
        setBackgroundColor(Color.argb(a, r, g, b));
    }

    /**
     * 设置标题栏透明
     */
    public void setTitleTransparent() {
        setBackgroundColor(Color.TRANSPARENT);
    }

    /**
     * 设置主标题文字颜色
     *
     * @param resid
     */
    public void setTitleColor(int resid) {
        mCenterText.setTextColor(resid);
    }

    /**
     * 设置主标题文字大小
     *
     * @param size
     */
    public void setTitleSize(float size) {
        mCenterText.setTextSize(size);
    }

    /**
     * 设置主标题文字背景
     *
     * @param resid
     */
    public void setTitleBackground(int resid) {
        mCenterText.setBackgroundResource(resid);
    }

    /**
     * 设置副标题文字颜色
     *
     * @param resid
     */
    public void setSubTitleColor(int resid) {
        mSubTitleText.setTextColor(resid);
    }

    /**
     * 设置副标题文字大小
     *
     * @param size
     */
    public void setSubTitleSize(float size) {
        mSubTitleText.setTextSize(size);
    }

    /**
     * 设置自定义Title
     *
     * @param titleView
     */
    public void setCustomTitle(View titleView) {
        if (titleView == null) {
            mCenterText.setVisibility(View.VISIBLE);
            if (mCustomCenterView != null) {
                mCenterLayout.removeView(mCustomCenterView);
            }

        } else {
            if (mCustomCenterView != null) {
                mCenterLayout.removeView(mCustomCenterView);
            }
            LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            mCustomCenterView = titleView;
            mCenterLayout.addView(titleView, layoutParams);
            mCenterText.setVisibility(View.GONE);
        }
    }

    /**
     * 设置标题栏下分割线
     *
     * @param drawable
     */
    public void setDivider(Drawable drawable) {
        mDividerView.setBackgroundDrawable(drawable);
    }

    /**
     * 设置标题栏下分割线颜色
     *
     * @param color
     */
    public void setDividerColor(int color) {
        mDividerView.setBackgroundColor(color);
    }

    /**
     * 设置标题栏下分割线高度
     *
     * @param dividerHeight
     */
    public void setDividerHeight(int dividerHeight) {
        mDividerView.getLayoutParams().height = dividerHeight;
    }

    /**
     * 设置右侧按钮文字颜色
     *
     * @param colorResId
     */
    public void setActionTextColor(int colorResId) {
        mActionTextColor = colorResId;
    }

    /**
     * Function to set a click listener for Title TextView
     *
     * @param listener the onClickListener
     */
    public void setOnTitleClickListener(OnClickListener listener) {
        mCenterText.setOnClickListener(listener);
    }

    @Override
    public void onClick(View view) {
        final Object tag = view.getTag();
        if (tag instanceof Action) {
            final Action action = (Action) tag;
            action.performAction(view);
        }
    }

    /**
     * Adds a list of {@link Action}s.
     *
     * @param actionList the actions to add
     */
    public void addActions(ActionList actionList) {
        int actions = actionList.size();
        for (int i = 0; i < actions; i++) {
            addAction(actionList.get(i));
        }
    }

    /**
     * Adds a new {@link Action}.
     *
     * @param action the action to add
     */
    public View addAction(Action action) {
        final int index = mRightLayout.getChildCount();
        return addAction(action, index);
    }

    /**
     * Adds a new {@link Action} at the specified index.
     *
     * @param action the action to add
     * @param index  the position at which to add the action
     */
    public View addAction(Action action, int index) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT);
        View view = inflateAction(action);
        mRightLayout.addView(view, index, params);
        return view;
    }

    /**
     * Removes all action views from this action bar
     */
    public void removeAllActions() {
        mRightLayout.removeAllViews();
    }

    /**
     * Remove a action from the action bar.
     *
     * @param index position of action to remove
     */
    public void removeActionAt(int index) {
        mRightLayout.removeViewAt(index);
    }

    /**
     * Remove a action from the action bar.
     *
     * @param action The action to remove
     */
    public void removeAction(Action action) {
        int childCount = mRightLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = mRightLayout.getChildAt(i);
            if (view != null) {
                final Object tag = view.getTag();
                if (tag instanceof Action && tag.equals(action)) {
                    mRightLayout.removeView(view);
                }
            }
        }
    }

    /**
     * Returns the number of actions currently registered with the action bar.
     *
     * @return action count
     */
    public int getActionCount() {
        return mRightLayout.getChildCount();
    }

    /**
     * Inflates a {@link View} with the given {@link Action}.
     *
     * @param action the action to inflate
     * @return a view
     */
    private View inflateAction(Action action) {
        View view = null;
        if (action instanceof ImageAction) {
            ImageView img = new ImageView(getContext());
            // 设置View的大小
            LayoutParams imgLp = new LayoutParams(px2dip(DEFAULT_ICON_SIZE), px2dip(DEFAULT_ICON_SIZE));
            img.setLayoutParams(imgLp);
            img.setImageResource(action.getDrawable());
            img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            view = img;
        } else if (action instanceof AssetsAction) {
            ImageView img = new ImageView(getContext());
            // 设置View的大小
            LayoutParams imgLp = new LayoutParams(px2dip(DEFAULT_ICON_SIZE), px2dip(DEFAULT_ICON_SIZE));
            img.setLayoutParams(imgLp);
            // 使用xUtils3加载assets里的图片
            ImageOptions imageOptions = new ImageOptions.Builder()
                    .setSize(px2dip(DEFAULT_ICON_SIZE), px2dip(DEFAULT_ICON_SIZE))
                    .setImageScaleType(ImageView.ScaleType.CENTER_INSIDE)
                    .build();
            x.image().bind(img, action.getAssets(), imageOptions);
            view = img;
        } else if (action instanceof TextAction) {
            TextView text = new TextView(getContext());
            text.setGravity(Gravity.CENTER);
            text.setText(action.getText());
            text.setTextSize(DEFAULT_ACTION_TEXT_SIZE);
            text.setTextColor(DEFAULT_ACTION_TEXT_COLOR);
            if (mActionTextColor != 0) {
                text.setTextColor(mActionTextColor);
            }
            view = text;
        }
        view.setPadding(mActionPadding, 0, 20, 0);
        view.setTag(action);
        view.setOnClickListener(this);
        return view;
    }

    /**
     * 通过Action获取View
     *
     * @param action
     * @return
     */
    public View getViewByAction(Action action) {
        View view = findViewWithTag(action);
        return view;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height;
        if (heightMode != MeasureSpec.EXACTLY) {
            height = mHeight + mStatusBarHeight;
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY);
        } else {
            height = MeasureSpec.getSize(heightMeasureSpec) + mStatusBarHeight;
        }
        mScreenWidth = MeasureSpec.getSize(widthMeasureSpec);
        measureChild(mLeftLayout, widthMeasureSpec, heightMeasureSpec);
        measureChild(mRightLayout, widthMeasureSpec, heightMeasureSpec);
        if (mLeftLayout.getMeasuredWidth() > mRightLayout.getMeasuredWidth()) {
            mCenterLayout.measure(
                    MeasureSpec.makeMeasureSpec(mScreenWidth - 2 * mLeftLayout.getMeasuredWidth(), MeasureSpec.EXACTLY)
                    , heightMeasureSpec);
        } else {
            mCenterLayout.measure(
                    MeasureSpec.makeMeasureSpec(mScreenWidth - 2 * mRightLayout.getMeasuredWidth(), MeasureSpec.EXACTLY)
                    , heightMeasureSpec);
        }
        measureChild(mDividerView, widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mLeftLayout.layout(0, mStatusBarHeight, mLeftLayout.getMeasuredWidth(), mLeftLayout.getMeasuredHeight() + mStatusBarHeight);
        mRightLayout.layout(mScreenWidth - mRightLayout.getMeasuredWidth(), mStatusBarHeight,
                mScreenWidth, mRightLayout.getMeasuredHeight() + mStatusBarHeight);
        if (mLeftLayout.getMeasuredWidth() > mRightLayout.getMeasuredWidth()) {
            mCenterLayout.layout(mLeftLayout.getMeasuredWidth(), mStatusBarHeight,
                    mScreenWidth - mLeftLayout.getMeasuredWidth(), getMeasuredHeight());
        } else {
            mCenterLayout.layout(mRightLayout.getMeasuredWidth(), mStatusBarHeight,
                    mScreenWidth - mRightLayout.getMeasuredWidth(), getMeasuredHeight());
        }
        mDividerView.layout(0, getMeasuredHeight() - mDividerView.getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight());
    }

    public int dip2px(int dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public int px2dip(int pxValue) {
        return getScreenWidth() * pxValue / 360;
    }

    public int getScreenWidth() {
        WindowManager wm = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 计算状态栏高度高度
     * getStatusBarHeight
     *
     * @return
     */
    public static int getStatusBarHeight() {
        return getInternalDimensionSize(Resources.getSystem(), STATUS_BAR_HEIGHT_RES_NAME);
    }

    private static int getInternalDimensionSize(Resources res, String key) {
        int result = 0;
        int resourceId = res.getIdentifier(key, "dimen", "android");
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId);
        }
        return result;
    }


    /**
     * A {@link LinkedList} that holds a list of {@link Action}s.
     */
    @SuppressWarnings("serial")
    public static class ActionList extends LinkedList<Action> {
    }

    /**
     * Definition of an action that could be performed, along with a icon to
     * show.
     */
    public interface Action {
        String getText();

        int getDrawable();

        String getAssets();

        void performAction(View view);
    }

    /**
     * 图片Action（传入图片的id）
     */
    public static abstract class ImageAction implements Action {
        private int mDrawable;

        public ImageAction(int drawable) {
            mDrawable = drawable;
        }

        @Override
        public int getDrawable() {
            return mDrawable;
        }

        @Override
        public String getText() {
            return null;
        }

        @Override
        public String getAssets() {
            return null;
        }
    }

    /**
     * 图片Action（传入Assets图片路径）
     */
    public static abstract class AssetsAction implements Action {
        private String mAssets;

        public AssetsAction(String assets) {
            mAssets = assets;
        }

        @Override
        public String getAssets() {
            return "assets://" + mAssets;
        }

        @Override
        public String getText() {
            return null;
        }

        @Override
        public int getDrawable() {
            return 0;
        }
    }

    /**
     * 文字Action
     */
    public static abstract class TextAction implements Action {
        final private String mText;

        public TextAction(String text) {
            mText = text;
        }

        @Override
        public String getText() {
            return mText;
        }

        @Override
        public int getDrawable() {
            return 0;
        }

        @Override
        public String getAssets() {
            return null;
        }
    }
}
