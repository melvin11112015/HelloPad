package com.gki.managerment.global;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gki.managerment.R;

import java.util.List;

/**
 * Created by lyc on 2016/2/27.
 */
public class ListItemAdapter extends BaseAdapter {
    private List<BaseListItem> data;
    private Context context;

    public ListItemAdapter(Context aContext, List<BaseListItem> lstData) {
        this.context = aContext;
        this.data = lstData;
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.spinner_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView.setText(data.get(position).ItemName);
        return convertView;
    }

    private class ViewHolder {
        TextView textView;

        public ViewHolder(View view) {
            textView = (TextView) view.findViewById(R.id.tv_spinner_item);
        }
    }
}
