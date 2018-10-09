package com.gki.managerment.adapter;

import java.util.ArrayList;

import com.gki.managerment.R;
import com.gki.managerment.LoginUser;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PopupListAdapter extends BaseAdapter {

	private ArrayList<LoginUser> list = new ArrayList<LoginUser>(); 
    private Activity activity = null; 
	private Handler handler;
    
	/**
	 * 自定义构造方法
	 * @param activity
	 * @param handler
	 * @param list
	 */
    public PopupListAdapter(Activity activity,Handler handler,ArrayList<LoginUser> list){
    	this.activity = activity;
    	this.handler = handler;
    	this.list = list;
    }
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null; 
        if (convertView == null) { 
            holder = new ViewHolder(); 
            //下拉项布局
            convertView = LayoutInflater.from(activity).inflate(R.layout.popup_list_item, null); 
            holder.tv_userId = (TextView) convertView.findViewById(R.id.tv_userId); 
            holder.img_delete = (ImageView) convertView.findViewById(R.id.img_delete); 
            
            convertView.setTag(holder); 
        } else { 
            holder = (ViewHolder) convertView.getTag(); 
        } 
        
        holder.tv_userId.setText(list.get(position).getUserId());
        
        //为下拉框选项文字部分设置事件，最终效果是点击将其文字填充到文本框
        holder.tv_userId.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putString("userId", list.get(position).getUserId());
				data.putString("pswd", list.get(position).getPswd());
				msg.setData(data);
				msg.what = 901;
				handler.sendMessage(msg);
			}
		});
        
        //为下拉框选项删除图标部分设置事件，最终效果是点击将该选项删除
        holder.img_delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putInt("position", position);
				data.putString("userId", list.get(position).getUserId());
				msg.setData(data);
				msg.what = 902;
				handler.sendMessage(msg);
			}
		});
        return convertView; 
	}
}

class ViewHolder { 
    TextView tv_userId; 
    ImageView img_delete; 
}