package com.cos.huanhuan.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cos.huanhuan.R;
import com.cos.huanhuan.utils.DensityUtils;

import java.util.LinkedList;

public class TitleSearchBarSecond extends ViewGroup implements View.OnClickListener {
    private static final int DEFAULT_MAIN_TEXT_SIZE = 17;
    private static final int DEFAULT_SUB_TEXT_SIZE = 12;
    private static final int DEFAULT_ACTION_TEXT_SIZE = 15;
    private static final int DEFAULT_TITLE_BAR_HEIGHT = 74;

    private static final String STATUS_BAR_HEIGHT_RES_NAME = "status_bar_height";

    private EditText et_search_title;
    private TextView tv_title_publish,tv_title_cancel;
    private TabLayout top_titleBar;
    //private TextView mLeftText;
//    private LinearLayout mRightLayout;
//    private LinearLayout mCenterLayout;
//    private TextView mCenterText;
//    private TextView mSubTitleText;
//    private View mCustomCenterView;
    private View mDividerView;

    private LinearLayout mLeftLayout;


    private boolean mImmersive;

    private int mScreenWidth;
    private int mStatusBarHeight;
    private int mActionPadding;
    private int mOutPadding;
    private int mActionTextColor;
    private int mHeight;

    public TitleSearchBarSecond(Context context) {
        super(context);
        init(context);
    }

    public TitleSearchBarSecond(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TitleSearchBarSecond(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        if (mImmersive) {
            mStatusBarHeight = getStatusBarHeight();
        }
        mActionPadding = dip2px(14);
        mOutPadding = dip2px(8);
        mHeight = dip2px(DEFAULT_TITLE_BAR_HEIGHT);
        initView(context);
    }

    private void initView(Context context) {
        View view = View.inflate(context, R.layout.search_titlebar_second, null);
        et_search_title = (EditText) view.findViewById(R.id.et_search_title);
        tv_title_publish = (TextView) view.findViewById(R.id.tv_title_publish);
        tv_title_cancel = (TextView) view.findViewById(R.id.tv_title_cancel);
        top_titleBar = (TabLayout) view.findViewById(R.id.top_titleBar);

        mLeftLayout = new LinearLayout(context);
        //mLeftText = new TextView(context);
//        mCenterLayout = new LinearLayout(context);
//        mRightLayout = new LinearLayout(context);
        mDividerView = new View(context);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);

        mLeftLayout.addView(view);

//        mCenterText = new TextView(context);
//        mSubTitleText = new TextView(context);
//        mCenterLayout.addView(mCenterText);
//        mCenterLayout.addView(mSubTitleText);
//
//        mCenterLayout.setGravity(Gravity.CENTER);
//        mCenterText.setTextSize(DEFAULT_MAIN_TEXT_SIZE);
//        mCenterText.setSingleLine();
//        mCenterText.setGravity(Gravity.CENTER);
//        mCenterText.setEllipsize(TextUtils.TruncateAt.END);
//        mCenterText.getPaint().setFakeBoldText(true);
//
//        mSubTitleText.setTextSize(DEFAULT_SUB_TEXT_SIZE);
//        mSubTitleText.setSingleLine();
//        mSubTitleText.setGravity(Gravity.CENTER);
//        mSubTitleText.setEllipsize(TextUtils.TruncateAt.END);
//
//        mRightLayout.setPadding(mOutPadding, 0, mOutPadding, 0);

        mLeftLayout.setPadding(mOutPadding + mActionPadding - 6, DensityUtils.dip2px(context,28), mOutPadding + mActionPadding - 6, 0);
//        mCenterLayout.setPadding(0, DensityUtils.dip2px(context,20),0,0);
//        mRightLayout.setPadding(0, DensityUtils.dip2px(context,20),0,0);

        addView(mLeftLayout);
//        addView(mCenterLayout);
//        addView(mRightLayout, layoutParams);
        addView(mDividerView, new LayoutParams(LayoutParams.MATCH_PARENT, 1));
    }

    public void setRightButtonClick(OnClickListener l){
        tv_title_publish.setOnClickListener(l);
    }

    public void setLeftButtonClick(OnClickListener l){tv_title_cancel.setOnClickListener(l);}

    public String getEtText(){
        return et_search_title.getText().toString();
    }

    public void setEtText(String resText){
        et_search_title.setText(resText);
    }

    public EditText getEtSearch(){
        return et_search_title;
    }

    public void setRightTextAndColor(CharSequence resText,int resTextColor){
        tv_title_publish.setText(resText);
        tv_title_publish.setTextColor(resTextColor);
    }

    public void setRightVisiable(int i){
        if(i == 0){
            tv_title_publish.setVisibility(GONE);
        }else{
            tv_title_publish.setVisibility(VISIBLE);
        }
    }

    public void etTextWatcherListener(TextWatcher textWatcher){
        et_search_title.addTextChangedListener(textWatcher);
    }

    public TabLayout getTabLayout(){
        return top_titleBar;
    }

    public void setBaseImmersive(boolean immersive) {
        mImmersive = immersive;
        if (mImmersive) {
            mStatusBarHeight = getStatusBarHeight();
        } else {
            mStatusBarHeight = 0;
        }
    }

    public void setHeight(int height) {
        mHeight = height;
        setMeasuredDimension(getMeasuredWidth(), mHeight);
    }



//    public void setTitle(CharSequence title) {
//        int index = title.toString().indexOf("\n");
//        if (index > 0) {
//            setTitle(title.subSequence(0, index), title.subSequence(index + 1, title.length()), LinearLayout.VERTICAL);
//        } else {
//            index = title.toString().indexOf("\t");
//            if (index > 0) {
//                setTitle(title.subSequence(0, index), "  " + title.subSequence(index + 1, title.length()), LinearLayout.HORIZONTAL);
//            } else {
//                mCenterText.setText(title);
//                mSubTitleText.setVisibility(View.GONE);
//            }
//        }
//    }

//    private void setTitle(CharSequence title, CharSequence subTitle, int orientation) {
//        mCenterLayout.setOrientation(orientation);
//        mCenterText.setText(title);
//
//        mSubTitleText.setText(subTitle);
//        mSubTitleText.setVisibility(View.VISIBLE);
//    }

//    public void setCenterClickListener(OnClickListener l) {
//        mCenterLayout.setOnClickListener(l);
//    }
//
//    public void setTitle(int resid) {
//        setTitle(getResources().getString(resid));
//    }
//
//    public void setTitleColor(int resid) {
//        mCenterText.setTextColor(resid);
//    }
//
//    public void setTitleSize(float size) {
//        mCenterText.setTextSize(size);
//    }
//
//    public void setTitleBackground(int resid) {
//        mCenterText.setBackgroundResource(resid);
//    }
//
//    public void setSubTitleColor(int resid) {
//        mSubTitleText.setTextColor(resid);
//    }
//
//    public void setSubTitleSize(float size) {
//        mSubTitleText.setTextSize(size);
//    }
//
//    public void setCustomTitle(View titleView) {
//        if (titleView == null) {
//            mCenterText.setVisibility(View.VISIBLE);
//            if (mCustomCenterView != null) {
//                mCenterLayout.removeView(mCustomCenterView);
//            }
//
//        } else {
//            if (mCustomCenterView != null) {
//                mCenterLayout.removeView(mCustomCenterView);
//            }
//            LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//            mCustomCenterView = titleView;
//            mCenterLayout.addView(titleView, layoutParams);
//            mCenterText.setVisibility(View.GONE);
//        }
//    }

    public void setDivider(Drawable drawable) {
        mDividerView.setBackgroundDrawable(drawable);
    }

    public void setDividerColor(int color) {
        mDividerView.setBackgroundColor(color);
    }

    public void setDividerHeight(int dividerHeight) {
        mDividerView.getLayoutParams().height = dividerHeight;
    }

    public void setActionTextColor(int colorResId) {
        mActionTextColor = colorResId;
    }

//    /**
//     * Function to set a click listener for Title TextView
//     *
//     * @param listener the onClickListener
//     */
//    public void setOnTitleClickListener(OnClickListener listener) {
//        mCenterText.setOnClickListener(listener);
//    }

    @Override
    public void onClick(View view) {
        final Object tag = view.getTag();
        if (tag instanceof TitleBar.Action) {
            final TitleBar.Action action = (TitleBar.Action) tag;
            action.performAction(view);
        }
    }

    /**
     * Adds a list of {@link TitleBar.Action}s.
     * @param actionList the actions to add
     */
//    public void addActions(TitleBar.ActionList actionList) {
//        int actions = actionList.size();
//        for (int i = 0; i < actions; i++) {
//            addAction(actionList.get(i));
//        }
//    }

//    /**
//     * Adds a new {@link TitleBar.Action}.
//     * @param action the action to add
//     */
//    public View addAction(TitleBar.Action action) {
//        final int index = mRightLayout.getChildCount();
//        return addAction(action, index);
//    }

//    /**
//     * Adds a new {@link TitleBar.Action} at the specified index.
//     * @param action the action to add
//     * @param index the position at which to add the action
//     */
//    public View addAction(TitleBar.Action action, int index) {
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
//                LayoutParams.MATCH_PARENT);
//        View view = inflateAction(action);
//        mRightLayout.addView(view, index, params);
//        return view;
//    }
//
//    /**
//     * Removes all action views from this action bar
//     */
//    public void removeAllActions() {
//        mRightLayout.removeAllViews();
//    }
//
//    /**
//     * Remove a action from the action bar.
//     * @param index position of action to remove
//     */
//    public void removeActionAt(int index) {
//        mRightLayout.removeViewAt(index);
//    }
//
//    /**
//     * Remove a action from the action bar.
//     * @param action The action to remove
//     */
//    public void removeAction(TitleBar.Action action) {
//        int childCount = mRightLayout.getChildCount();
//        for (int i = 0; i < childCount; i++) {
//            View view = mRightLayout.getChildAt(i);
//            if (view != null) {
//                final Object tag = view.getTag();
//                if (tag instanceof TitleBar.Action && tag.equals(action)) {
//                    mRightLayout.removeView(view);
//                }
//            }
//        }
//    }
//
//    /**
//     * Returns the number of actions currently registered with the action bar.
//     * @return action count
//     */
//    public int getActionCount() {
//        return mRightLayout.getChildCount();
//    }

    /**
     * Inflates a {@link View} with the given {@link TitleBar.Action}.
     * @param action the action to inflate
     * @return a view
     */
    private View inflateAction(TitleBar.Action action) {
        View view = null;
        if (TextUtils.isEmpty(action.getText())) {
            ImageView img = new ImageView(getContext());
            img.setImageResource(action.getDrawable());
            view = img;
        } else {
            TextView text = new TextView(getContext());
            text.setGravity(Gravity.CENTER);
            text.setText(action.getText());
            text.setTextSize(DEFAULT_ACTION_TEXT_SIZE);
            if (mActionTextColor != 0) {
                text.setTextColor(mActionTextColor);
            }
            view = text;
        }

        view.setPadding(mActionPadding, 0, mActionPadding, 0);
        view.setTag(action);
        view.setOnClickListener(this);
        return view;
    }

    public View getViewByAction(TitleBar.Action action) {
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
        //measureChild(mRightLayout, widthMeasureSpec, heightMeasureSpec);
//        if (mLeftLayout.getMeasuredWidth() > mRightLayout.getMeasuredWidth()) {
//            mCenterLayout.measure(
//                    MeasureSpec.makeMeasureSpec(mScreenWidth - 2 * mLeftLayout.getMeasuredWidth(), MeasureSpec.EXACTLY)
//                    , heightMeasureSpec);
//        } else {
//            mCenterLayout.measure(
//                    MeasureSpec.makeMeasureSpec(mScreenWidth - 2 * mRightLayout.getMeasuredWidth(), MeasureSpec.EXACTLY)
//                    , heightMeasureSpec);
//        }
        measureChild(mDividerView, widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mLeftLayout.layout(0, mStatusBarHeight, mLeftLayout.getMeasuredWidth(), mLeftLayout.getMeasuredHeight() + mStatusBarHeight);
//        mRightLayout.layout(mScreenWidth - mRightLayout.getMeasuredWidth(), mStatusBarHeight,
//                mScreenWidth, mRightLayout.getMeasuredHeight() + mStatusBarHeight);
//        if (mLeftLayout.getMeasuredWidth() > mRightLayout.getMeasuredWidth()) {
//            mCenterLayout.layout(mLeftLayout.getMeasuredWidth(), mStatusBarHeight,
//                    mScreenWidth - mLeftLayout.getMeasuredWidth(), getMeasuredHeight());
//        } else {
//            mCenterLayout.layout(mRightLayout.getMeasuredWidth(), mStatusBarHeight,
//                    mScreenWidth - mRightLayout.getMeasuredWidth(), getMeasuredHeight());
//        }
        mDividerView.layout(0, getMeasuredHeight() - mDividerView.getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight());
    }

    public static int dip2px(int dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 计算状态栏高度高度
     * getStatusBarHeight
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
     * A {@link LinkedList} that holds a list of {@link TitleBar.Action}s.
     */
    @SuppressWarnings("serial")
    public static class ActionList extends LinkedList<TitleBar.Action> {
    }

    /**
     * Definition of an action that could be performed, along with a icon to
     * show.
     */
    public interface Action {
        String getText();
        int getDrawable();
        void performAction(View view);
    }

    public static abstract class ImageAction implements TitleBar.Action {
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
    }

    public static abstract class TextAction implements TitleBar.Action {
        final private String mText;

        public TextAction(String text) {
            mText = text;
        }

        @Override
        public int getDrawable() {
            return 0;
        }

        @Override
        public String getText() {
            return mText;
        }
    }

}
