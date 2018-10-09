package com.gki.managerment.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.gki.managerment.R;
import com.gki.managerment.util.ArrayContainsUtil;

/**
 * 自定义组合控件
 *
 * @author Administrator
 */
public class SettingView extends LinearLayout {
    // 把自定义组合控件的xml文件实现化为对象，并且添加到当前对象中 ，作为当前对象的子控件
    // 自定义方法 操纵 组合控件的子控件

    private TextView mTitleTv; // 标题文本控件
    private EditText mInputEt; // 输入文本控件
    private EditText mInputEt2; // 输入文本控件
    private Spinner mSp;
    private String title;
    private int textSize_tv;
    private int textSize_et;
    private int minLines_tv;
    private int ems;

    private String[] mTags;

    private View rootView;// 组合自定义控件界面的根节点对象


    private int defaultStyle = -1; //1. 显示spinner
    private int isShowDefault = -1; // 1 不显示默认值
    private String[] defaultTags;
    private boolean isEditable = true, isReadOnly = false;

    @SuppressLint("NewApi")
    public SettingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public SettingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        // mTags = UIUtils.getStringArray(R.array.tag_names);
        mTags = getContext().getResources().getStringArray(R.array.tag_names);
        /**
         * 通过命名空间和属性名来获取属性值 namespace:命名控件 name：属性名
         */
        title = attrs.getAttributeValue(
                "http://schemas.android.com/apk/res/com.gki.managerment",
                "setting_title");
        textSize_tv = attrs.getAttributeIntValue(
                "http://schemas.android.com/apk/res/com.gki.managerment",
                "textSize_tv", 17);
        textSize_et = attrs.getAttributeIntValue(
                "http://schemas.android.com/apk/res/com.gki.managerment",
                "textSize_et", 17);
        minLines_tv = attrs.getAttributeIntValue(
                "http://schemas.android.com/apk/res/com.gki.managerment",
                "minLines_tv", 1);
        ems = attrs.getAttributeIntValue(
                "http://schemas.android.com/apk/res/com.gki.managerment",
                "ems", 10);

        defaultStyle = attrs.getAttributeIntValue(
                "http://schemas.android.com/apk/res/com.gki.managerment",
                "style", -1);
        isShowDefault = attrs.getAttributeIntValue(
                "http://schemas.android.com/apk/res/com.gki.managerment",
                "show_default", -1);
        isEditable = attrs.getAttributeBooleanValue(
                "http://schemas.android.com/apk/res/com.gki.managerment",
                "is_editable", true);
        isReadOnly = attrs.getAttributeBooleanValue(
                "http://schemas.android.com/apk/res/com.gki.managerment",
                "is_ReadOnly", false);

        // 初始化 自定义控件的属性值
        mTitleTv.setText(title);
        //mTitleTv.setTextSize(textSize_tv);
        mTitleTv.setMinEms(minLines_tv);
        mInputEt.setEms(ems);
        //mInputEt.setTextSize(textSize_et);
        mInputEt2.setEms(ems);
        mInputEt2.setTextSize(textSize_et);

        if (defaultStyle == -1) {
            // 判断et和spinner显示哪个
            if (ArrayContainsUtil.ListContains(mTags, title.trim())) {// 调用ArrayContainsUtil工具类判断特定值是否存在于数组中
                mInputEt.setVisibility(View.GONE);
                mSp.setVisibility(View.VISIBLE);
                mInputEt2.setVisibility(View.VISIBLE);
            }
            //
        } else if (defaultStyle == 1) {
            mInputEt.setVisibility(View.GONE);
            mSp.setVisibility(View.VISIBLE);
            mInputEt2.setVisibility(View.GONE);
        }


        if (isShowDefault == -1) {

        } else if (isShowDefault == 1) {
            defaultTags = new String[0];
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_list_item, defaultTags);
            mSp.setAdapter(adapter);
        }

        mInputEt.setEnabled(isEditable);

        if (isReadOnly){
            mInputEt.setCursorVisible(false);           //设置输入框中的光标不可见
            mInputEt.setFocusable(false);               //无焦点
            mInputEt.setFocusableInTouchMode(false);    //触摸时也得不到焦点
        }
    }

    public SettingView(Context context) {
        super(context);
        init();
    }

    // 初始化自定义组合控件的界面
    private void init() {
        rootView = View.inflate(getContext(), R.layout.setting_view, this);
        mTitleTv = (TextView) rootView.findViewById(R.id.tv_title);
        mInputEt = (EditText) rootView.findViewById(R.id.et_settingView);
        mInputEt2 = (EditText) rootView.findViewById(R.id.et_info);
        mSp = (Spinner) rootView.findViewById(R.id.sp_settingView);
    }

    //

    public void setTextSize_tv(int textSize) {
        mTitleTv.setTextSize(textSize);

    }

    public void setTextSize_et(int textSize) {
        mInputEt.setTextSize(textSize);

    }

    public void setVisibility(String visibility) {
        mSp.setVisibility(visibility.equals("visible") ? View.VISIBLE : View.GONE);
    }

    public String getInputData() {
        if (mSp.getVisibility() == View.VISIBLE) {
            if (mSp.getSelectedItem() != null) {
                return mSp.getSelectedItem().toString() + '('
                        + mInputEt2.getText().toString().trim() + ')';
            } else {
                return mInputEt.getText().toString().trim();
            }
        } else {
            return mInputEt.getText().toString().trim();
        }
    }

    public EditText getEditText() {
        return mInputEt;
    }

    public Spinner getSpinner() {
        return mSp;
    }

    public void setValue(String value) {
        mInputEt.setText(value == null ? "" : value);
    }

    public String getTitle() {
        return mTitleTv.getText().toString();
    }

    // 自定义方法
    // 设置组合控件标题
    public void setTitle(String title) {
        mTitleTv.setText(title);
    }


}
