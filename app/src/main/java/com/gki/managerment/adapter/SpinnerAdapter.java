package com.gki.managerment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gki.managerment.R;

import java.util.ArrayList;

public class SpinnerAdapter extends BaseAdapter {
    private ArrayList<String> mList;
    private Context context;

    public SpinnerAdapter(ArrayList<String> mList, Context context) {
        this.context = context;
        this.mList = mList;
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
        final String SpinnerItemInfo = mList.get(position);
        HolderView holderView = null;
        if (convertView == null) {
            holderView = new HolderView();
            convertView = LayoutInflater.from(context).inflate(R.layout.spinner_list_item, parent, false);
            holderView.ivLogo = (ImageView) convertView.findViewById(R.id.iv_logo);
            holderView.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
            holderView.btnConfm = (Button) convertView.findViewById(R.id.btn_addConfm);

            convertView.setTag(holderView);
        } else {
            holderView = (HolderView) convertView.getTag();
        }
        holderView.tvContent.setText(mList.get(position));
        holderView.btnConfm.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                mList.remove(position);//FIXME 点击按钮把数据更新到UI
                // 刷新listview
                SpinnerAdapter.this.notifyDataSetChanged();
            }
        });

        return convertView;
    }


    private static class HolderView {
        ImageView ivLogo;
        TextView tvContent;
        EditText etInfo;
        Button btnConfm;
    }

}
