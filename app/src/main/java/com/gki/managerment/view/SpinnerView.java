package com.gki.managerment.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.gki.managerment.R;
import com.gki.managerment.adapter.SpinnerAdapter;

import java.util.ArrayList;

public class SpinnerView extends RelativeLayout {
    private EditText etInput;
    private ImageView ivArrow;
    private ListView lvList;
    private PopupWindow mPopup;
    private String[] mTags;
    private ArrayList<String> mList;
    private RelativeLayout rl;


    private View rootView;// 组合自定义控件界面的根节点对象

    public SpinnerView(Context context) {
        super(context);
        init();
        lvList = new ListView(getContext());
        mTags = getContext().getResources().getStringArray(R.array.tag_names);//FIXME 从外网获取数据的地方
        for (int i = 0; i < 200; i++) {
            mList.add("adfasdf" + i);
        }
        ivArrow.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                showPopup();
            }
        });
        lvList.setAdapter(new SpinnerAdapter(mList, getContext()));
        lvList.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String content = mList.get(position);
                etInput.setText(content);

                mPopup.dismiss();// 消失弹窗
            }
        });

    }


    public SpinnerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();

    }


    public SpinnerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        rootView = View.inflate(getContext(), R.layout.spinner_view, this);
        ivArrow = (ImageView) rootView.findViewById(R.id.iv_arrow);
        etInput = (EditText) rootView.findViewById(R.id.et_input);
        rl = (RelativeLayout) rootView.findViewById(R.id.rl_spinner);
    }


    protected void showPopup() {
        if (mPopup == null) {
//			mPopup = new PopupWindow(lvList, etInput.getWidth(), 400, true);// 参1:布局对象;参2:宽度;参3:高度;参4:获取焦点
            mPopup = new PopupWindow(lvList, 300, 400, true);// 参1:布局对象;参2:宽度;参3:高度;参4:获取焦点
            mPopup.setBackgroundDrawable(new ColorDrawable());// 必须设置背景,
            // 点击返回键和空白处才会消失
        }

        mPopup.showAsDropDown(etInput);// 显示在某个控件正下方
    }
}
