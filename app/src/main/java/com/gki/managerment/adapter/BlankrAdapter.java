package com.gki.managerment.adapter;

import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.gki.managerment.R;
import com.gki.managerment.parser.Blankentity;

import java.util.List;

public class BlankrAdapter extends BaseAdapter {
    private List<Blankentity> mList;
    private Context context;
    private LayoutInflater mInflater; //得到一个LayoutInfalter对象用来导入布局

    public BlankrAdapter(List<Blankentity> mList, Context context) {
        this.context = context;
        this.mList = mList;
        this.mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    public Object getItem(int position) {
        return mList == null ? null : mList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        // 1. 设置当前列表项   2. 装配数据
        final ViewHolder holder;
        if (convertView == null) {
            convertView = this.mInflater.inflate(R.layout.blank_listitem, null);
            holder = new ViewHolder();
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            holder.et_input = (EditText) convertView.findViewById(R.id.et_input);
            convertView.setTag(holder); //绑定ViewHolder对象
        } else {
            holder = (ViewHolder) convertView.getTag(); //取出ViewHolder对象
        }
        /*设置TextView显示的内容，即我们存放在动态数组中的数据*/
        holder.tv_content.setText(mList.get(position).getDescription());
        if (!mList.get(position).getQuantity_Time().equals("") && Float.parseFloat(mList.get(position).getQuantity_Time()) == 0)
        {
            holder.et_input.setText("");
        }
        else
        {
            holder.et_input.setText(mList.get(position).getQuantity_Time());
        }
        holder.et_input.setSelectAllOnFocus(true);
        holder.et_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mList.get(position).setQuantity_Time(holder.et_input.getText().toString());
                }
            }
        });
        holder.et_input.setInputType(InputType.TYPE_CLASS_NUMBER);
        /*
        holder.et_input.setOnKeyListener(
                new EditText.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        mList.get(position).setQuantity_Time(holder.et_input.getText().toString());
                        return false;
                    }
                });
        */
/*
        //我们要拿到里面的值根据position拿值
        holder.et_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        */
        return convertView;
    }

    private static class ViewHolder {
        public TextView tv_content;
        public EditText et_input;
    }
}
