package com.gki.managerment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.gki.managerment.bean.ProdMandayListSV_QueryList_Response;
import com.gki.managerment.global.BaseListItem;
import com.gki.managerment.global.BaseListItemParser;
import com.gki.managerment.http.Service.getService;
import com.gki.managerment.util.UserTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

//日报查询窗口
public class DailyQuery extends BaseActivity {
    private TextView tvUserName;
    private TextView tvDate;
    private String dairyID;
    private EditText etDiaryID, etProductOrder, etDate;
    private Spinner sp_prodLine;
    private ListView list;
    private List<ProdMandayListSV_QueryList_Response> responseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daily_query);
        init();

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        list = (ListView) findViewById(R.id.lv_staffInput_add2list);

        etDiaryID = (EditText) findViewById(R.id.txt_Key);
        etProductOrder = (EditText) findViewById(R.id.txt_OrderNo);
        etDate = (EditText) findViewById(R.id.txt_Date);
        sp_prodLine = (Spinner) findViewById(R.id.sp_prodLine);

        dairyID = getIntent().getStringExtra("dairy_id");
        if (dairyID == null) {
            finish();
            return;
        }

        etDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    new SlideDateTimePicker.Builder(getSupportFragmentManager())
                            .setListener(new SlideDateTimeListener() {
                                @Override
                                public void onDateTimeSet(java.util.Date date) {
                                    etDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date));
                                }
                            })
                            .setInitialDate(new java.util.Date())
                            .build()
                            .show();
                }
            }
        });
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        etDate.setText(formatter.format(new Date(System.currentTimeMillis())));

        findViewById(R.id.btn_add_Routing).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (responseList != null) {
                    ProdMandayListSV_QueryList_Response response = responseList.get(position);
                    Intent intent = new Intent();
                    intent.putExtra("data", response);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });

        etDiaryID.setFocusable(true);
        etDiaryID.requestFocus();

        GetUserProdLine getUserProdLine = new GetUserProdLine();
        getUserProdLine.execute(LoginUser.user.getUser().User_ID);
    }

    private void init() {
        tvUserName = (TextView) findViewById(R.id.tv_username);
        tvDate = (TextView) findViewById(R.id.tv_date);
        tvUserName.setText(LoginUser.user.User_Name);
        tvDate.setText(LoginUser.user.LoginTime.substring(0,10));
    }
    private class GetUserProdLine extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = getService.GetUserProdLineService(params[0]);
            return result;
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(String result) {
            System.out.println("result:" + result);
            if (!result.trim().equals("null") && !result.trim().equals("[]") && !result.trim().equals("{}") && !result.trim().equals("") && !result.trim().equals("anyType{}")) {
                List<BaseListItem> lstProdLine = new BaseListItemParser().getListFromJson(result, "Prod_Line", "Prod_Line_Name", false);
                UserProdLineAdapter adapter = new UserProdLineAdapter(lstProdLine);
                BaseListItem aEmptyItem = new BaseListItem("","");
                lstProdLine.add(0, aEmptyItem);
                sp_prodLine.setAdapter(adapter);
                for(int i=1; i< lstProdLine.size(); i++)
                {
                    if (lstProdLine.get(i).getItemId().toString().equals(LoginUser.getUser().Prod_Line))
                    {
                        sp_prodLine.setSelection(i);
                        break;
                    }
                }
            }
        }
    }
    private class UserProdLineAdapter extends BaseAdapter {
        private List<BaseListItem> data;

        public UserProdLineAdapter(List<BaseListItem> data) {
            this.data = data;
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
                convertView = LayoutInflater.from(getBaseContext()).inflate(R.layout.spinner_item, null);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    private void search() {
        new UserTask<String, String, String>() {

            @Override
            protected void onTaskPrepare() {
                super.onTaskPrepare();
                showLoadingDialog();
            }

            @Override
            protected void onErrorHandle(Context context, Exception error) {
                dismissLoadingDialog();
            }

            @Override
            protected void onTaskFinished(Context context, String s) {
                if (TextUtils.isEmpty(s)) {
                    dismissLoadingDialog();
                    return;
                }
                responseList = new Gson().fromJson(s, new TypeToken<List<ProdMandayListSV_QueryList_Response>>() {
                }.getType());
                if (responseList == null) {
                    dismissLoadingDialog();
                    return;
                }
                list.setAdapter(new Adapter(responseList));
                dismissLoadingDialog();
            }
        }.registerCallback(new UserTask.TaskCallback<String, String, String>() {
            @Override
            public String call(UserTask<String, String, String> task, String[] params) throws Exception {
                return getService.ProdMandayListSV_QueryList(params[0], params[1], params[2], params[3], params[4]);
            }
        }).execute(((BaseListItem)sp_prodLine.getSelectedItem()).getItemId(), etDiaryID.getText().toString(), etProductOrder.getText().toString(), etDate.getText().toString(), LoginUser.getUser().User_ID);
    }

    private class Adapter extends BaseAdapter {

        private List<ProdMandayListSV_QueryList_Response> data;

        public Adapter(List<ProdMandayListSV_QueryList_Response> data) {
            this.data = data;
        }

        @Override
        public int getCount() {
            return data == null ? 0 : data.size();
        }

        @Override
        public Object getItem(int position) {
            return null == data ? null : data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(getBaseContext()).inflate(R.layout.dialy_query_list_item, null);
                holder = new Holder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            ProdMandayListSV_QueryList_Response r = data.get(position);
            holder.title_document_no.setText(r.Document_No);
            holder.title_prod_no.setText(r.Prod_Order_No);
            holder.title_prod_input.setText(r.inputtime.split("T")[1].substring(0,8).toString());
            if (r.Posting_Date.length() >= 10)
                holder.title_prod_date.setText(r.Posting_Date.substring(0,10));
            else
                holder.title_prod_date.setText(r.Posting_Date);
            holder.title_prod_line.setText(r.Pro_line_name);
            holder.title_component_no.setText(r.Item_No);
            holder.title_component_name.setText(r.Description);
            holder.title_car_type.setText(r.car_model);
            holder.title_master.setText(r.Worker_Group_Name);
            return convertView;
        }
    }

    private class Holder {
        public TextView title_document_no, title_prod_no, title_prod_input,title_prod_date, title_prod_line, title_component_no, title_component_name, title_car_type,
                title_master;

        public Holder(View view) {
            title_document_no = (TextView) view.findViewById(R.id.title_document_no);
            title_prod_no = (TextView) view.findViewById(R.id.title_prod_no);
            title_prod_input = (TextView) view.findViewById(R.id.title_prod_input);
            title_prod_date = (TextView) view.findViewById(R.id.title_prod_date);
            title_prod_line = (TextView) view.findViewById(R.id.title_prod_line);
            title_component_no = (TextView) view.findViewById(R.id.title_component_no);
            title_component_name = (TextView) view.findViewById(R.id.title_component_name);
            title_car_type = (TextView) view.findViewById(R.id.title_car_type);
            title_master = (TextView) view.findViewById(R.id.title_master);
        }
    }
}
