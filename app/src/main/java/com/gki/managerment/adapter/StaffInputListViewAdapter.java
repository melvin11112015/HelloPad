package com.gki.managerment.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.gki.managerment.R;

import java.util.ArrayList;
import java.util.HashMap;

public class StaffInputListViewAdapter extends BaseAdapter {

    public ArrayList<HashMap<String, Object>> list;
    Activity activity;

    public StaffInputListViewAdapter(Activity activity, ArrayList<HashMap<String, Object>> list) {
        super();
        this.activity = activity;
        this.list = list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();

        if (convertView == null) {
            System.out.println("marcus1");
            convertView = inflater.inflate(R.layout.staff_item, null);
            holder = new ViewHolder();
            holder.view_Routing = (TextView) convertView.findViewById(R.id.view_Routing);
            holder.view_Worker_ID = (TextView) convertView.findViewById(R.id.view_Worker_ID);
            holder.view_Worker_Name = (TextView) convertView.findViewById(R.id.view_Worker_Name);
            holder.view_Working_Time = (EditText) convertView.findViewById(R.id.view_Working_Time);
            holder.view_Over_Time = (EditText) convertView.findViewById(R.id.view_Over_Time);
            holder.view_Support_Time = (CheckBox) convertView.findViewById(R.id.view_Support_Time);
            holder.view_Over_Time_Request = (CheckBox) convertView.findViewById(R.id.view_Over_Time_Request);
            holder.view_Over_Time_Approval = (CheckBox) convertView.findViewById(R.id.view_Over_Time_Approval);
            holder.view_Over_Time_Day = (EditText) convertView.findViewById(R.id.view_Over_Time_Day);
            holder.view_Over_Time_Night = (EditText) convertView.findViewById(R.id.view_Over_Time_Night);
            holder.view_Over_Time_Day_Temp = (EditText) convertView.findViewById(R.id.view_Over_Time_Day_Temp);
            holder.view_Over_Time_Night_Temp = (EditText) convertView.findViewById(R.id.view_Over_Time_Night_Temp);
            holder.view_Over_Time_Day_Request = (CheckBox) convertView.findViewById(R.id.view_Over_Time_Day_Request);
            holder.view_Over_Time_Night_Request = (CheckBox) convertView.findViewById(R.id.view_Over_Time_Night_Request);
            holder.view_Over_Time_Day_Temp_Request = (CheckBox) convertView.findViewById(R.id.view_Over_Time_Day_Temp_Request);
            holder.view_Over_Time_Night_Temp_Request = (CheckBox) convertView.findViewById(R.id.view_Over_Time_Night_Temp_Request);
            holder.view_Leave_Category_Code = (TextView) convertView.findViewById(R.id.view_Leave_Category_Code);
            holder.view_Leave_Category_Description = (TextView) convertView.findViewById(R.id.view_Leave_Category_Description);
            holder.view_Leave_Hours = (EditText) convertView.findViewById(R.id.view_Leave_Hours);
            holder.view_Meal_Allowance_Day_Req = (CheckBox) convertView.findViewById(R.id.view_Meal_Allowance_Day_Req);
            holder.view_Meal_Allowance_Night_Req = (CheckBox) convertView.findViewById(R.id.view_Meal_Allowance_Night_Req);
            holder.view_Allowance_Day_Req = (CheckBox) convertView.findViewById(R.id.view_Allowance_Day_Req);
            holder.view_Allowance_Night_Req = (CheckBox) convertView.findViewById(R.id.view_Allowance_Night_Req);
            holder.chb = (CheckBox) convertView.findViewById(R.id.chb_select);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final HashMap<String, Object> map = list.get(position);
        holder.view_Routing.setText(String.valueOf(map.get("view_Routing")));
        holder.view_Worker_ID.setText(String.valueOf(map.get("view_Worker_ID")).equalsIgnoreCase("null") ? "" : String.valueOf(map.get("view_Worker_ID")));
        holder.view_Worker_Name.setText(String.valueOf(map.get("view_Worker_Name")).equalsIgnoreCase("null") ? "" : String.valueOf(map.get("view_Worker_Name")));
        holder.view_Working_Time.setText(String.valueOf(map.get("view_Working_Time")));
        holder.view_Over_Time.setText(String.valueOf(map.get("view_Over_Time")));
        holder.view_Support_Time.setChecked(String.valueOf(map.get("view_Support_Time")) == "false" ? false : true);
        holder.view_Over_Time_Request.setChecked(String.valueOf(map.get("view_Over_Time_Request")) == "false" ? false : true);
        holder.view_Over_Time_Approval.setChecked(String.valueOf(map.get("view_Over_Time_Approval")) == "false" ? false : true);
        holder.view_Over_Time_Day.setText(String.valueOf(map.get("view_Over_Time_Day")));
        holder.view_Over_Time_Night.setText(String.valueOf(map.get("view_Over_Time_Night")));
        holder.view_Over_Time_Day_Temp.setText(String.valueOf(map.get("view_Over_Time_Day_Temp")));
        holder.view_Over_Time_Night_Temp.setText(String.valueOf(map.get("view_Over_Time_Night_Temp")));
        holder.view_Over_Time_Day_Request.setChecked(String.valueOf(map.get("view_Over_Time_Day_Request")) == "false" ? false : true);
        holder.view_Over_Time_Night_Request.setChecked(String.valueOf(map.get("view_Over_Time_Night_Request")) == "false" ? false : true);
        holder.view_Over_Time_Day_Temp_Request.setChecked(String.valueOf(map.get("view_Over_Time_Day_Temp_Request")) == "false" ? false : true);
        holder.view_Over_Time_Night_Temp_Request.setChecked(String.valueOf(map.get("view_Over_Time_Night_Temp_Request")) == "false" ? false : true);
        holder.view_Leave_Category_Code.setText(String.valueOf(map.get("view_Leave_Category_Code")).equalsIgnoreCase("null") ? "" : String.valueOf(map.get("view_Leave_Category_Code")));
        holder.view_Leave_Category_Description.setText(String.valueOf(map.get("view_Leave_Category_Description")).equalsIgnoreCase("null") ? "" : String.valueOf(map.get("view_Leave_Category_Description")));
        holder.view_Leave_Hours.setText(String.valueOf(map.get("view_Leave_Hours")));
        holder.view_Meal_Allowance_Day_Req.setChecked(String.valueOf(map.get("view_Meal_Allowance_Day_Req")) == "false" ? false : true);
        holder.view_Meal_Allowance_Night_Req.setChecked(String.valueOf(map.get("view_Meal_Allowance_Night_Req")) == "false" ? false : true);
        holder.view_Allowance_Day_Req.setChecked(String.valueOf(map.get("view_Allowance_Day_Req")) == "false" ? false : true);
        holder.view_Allowance_Night_Req.setChecked(String.valueOf(map.get("view_Allowance_Night_Req")) == "false" ? false : true);
        holder.chb.setChecked((Boolean) map.get("is_selected"));
        holder.chb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.put("is_selected", (!(Boolean) map.get("is_selected")));
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    private class ViewHolder {
        TextView view_Routing;
        TextView view_Worker_ID;
        TextView view_Worker_Name;
        EditText view_Working_Time;
        EditText view_Over_Time;
        CheckBox view_Support_Time;
        CheckBox view_Over_Time_Request;
        CheckBox view_Over_Time_Approval;
        EditText view_Over_Time_Day;
        EditText view_Over_Time_Night;
        EditText view_Over_Time_Day_Temp;
        EditText view_Over_Time_Night_Temp;
        CheckBox view_Over_Time_Day_Request;
        CheckBox view_Over_Time_Night_Request;
        CheckBox view_Over_Time_Day_Temp_Request;
        CheckBox view_Over_Time_Night_Temp_Request;
        TextView view_Leave_Category_Code;
        TextView view_Leave_Category_Description;
        EditText view_Leave_Hours;
        CheckBox view_Meal_Allowance_Day_Req;
        CheckBox view_Meal_Allowance_Night_Req;
        CheckBox view_Allowance_Day_Req;
        CheckBox view_Allowance_Night_Req;

        CheckBox chb;

    }

}
