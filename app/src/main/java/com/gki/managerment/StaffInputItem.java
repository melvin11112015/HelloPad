package com.gki.managerment;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.gki.managerment.bean.LeaveCategory;
import com.gki.managerment.bean.WorkDailyReport;
import com.gki.managerment.global.BaseListItem;
import com.gki.managerment.global.BaseListItemParser;
import com.gki.managerment.global.ListItemAdapter;
import com.gki.managerment.http.Service.getService;
import com.gki.managerment.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class StaffInputItem extends BaseActivity {

    private Button btnBack, btnSave;
    private Context context;
    private EditText et_Name, et_Working_Time,et_Over_Time;
    private CheckBox cb_Support_Time, cb_Over_Time_Request;
    //private CheckBox cb_Over_Time_Approval;
    private EditText et_Over_Time_Day, et_Over_Time_Night, et_Over_Time_Day_Temp,et_Over_Time_Night_Temp;
    private CheckBox cb_Over_Time_Day_Request,cb_Over_Time_Night_Request,cb_Over_Time_Day_Temp_Request,cb_Over_Time_Night_Temp_Request;
    //private EditText et_Leave_Category_Code;
    private EditText et_Leave_Category_Description,et_Leave_Hours,et_remark;
    private CheckBox cb_Meal_Allowance_Day_Req,cb_Meal_Allowance_Night_Req,cb_Allowance_Day_Req,cb_Allowance_Night_Req;
    private Spinner sp_Leave_Category, sp_Worker_Position;
    private WorkDailyReport currentPY;
    private WorkDailyReport DefaultSet;
    private SubmitTask submitTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_input_item);
        init();

        context = this;
        btnBack.setOnClickListener(new MyListener());
        btnSave.setOnClickListener(new MyListener());
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        currentPY = (WorkDailyReport) bundle.getSerializable("StaffInputItem");
        DefaultSet = (WorkDailyReport) bundle.getSerializable("DefaultSet");
        ShowItem(currentPY);
        /*cb_Over_Time_Request.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                et_Over_Time.setEnabled(cb_Over_Time_Request.isChecked());
                et_Over_Time.setText(cb_Over_Time_Request.isChecked() ? String.valueOf(DefaultSet.Default_Over_Time) : "");
            }
        });*/
        cb_Over_Time_Day_Request.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                et_Over_Time_Day.setEnabled(cb_Over_Time_Day_Request.isChecked());
                et_Over_Time_Day.setText(cb_Over_Time_Day_Request.isChecked() ? String.valueOf(DefaultSet.Def_Over_Time_Day) : "");
            }
        });
        cb_Over_Time_Night_Request.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                et_Over_Time_Night.setEnabled(cb_Over_Time_Night_Request.isChecked());
                et_Over_Time_Night.setText(cb_Over_Time_Night_Request.isChecked() ? String.valueOf(DefaultSet.Def_Over_Time_Night) : "");
            }
        });
        cb_Over_Time_Day_Temp_Request.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                et_Over_Time_Day_Temp.setEnabled(cb_Over_Time_Day_Temp_Request.isChecked());
                et_Over_Time_Day_Temp.setText(cb_Over_Time_Day_Temp_Request.isChecked() ? String.valueOf(DefaultSet.Def_Over_Time_Day_Temp) : "");
            }
        });
        cb_Over_Time_Night_Temp_Request.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                et_Over_Time_Night_Temp.setEnabled(cb_Over_Time_Night_Temp_Request.isChecked());
                et_Over_Time_Night_Temp.setText(cb_Over_Time_Night_Temp_Request.isChecked() ? String.valueOf(DefaultSet.Def_Over_Time_Night_Temp) : "");
            }
        });
        LeaveCategoryTask leaveCategoryTask = new LeaveCategoryTask();
        leaveCategoryTask.execute();

        WorkerPositionTask workerPositionTask = new WorkerPositionTask();
        workerPositionTask.execute();
    }

    private void init() {
        btnBack = (Button) findViewById(R.id.btn_back);
        btnSave = (Button) findViewById(R.id.btn_staffInput_save);
        et_Name = (EditText) findViewById(R.id.et_Name);
        sp_Worker_Position = (Spinner) findViewById(R.id.sp_Worker_Position);
        et_Working_Time = (EditText) findViewById(R.id.et_Working_Time);
        et_Over_Time = (EditText) findViewById(R.id.et_Over_Time);
        cb_Support_Time = (CheckBox) findViewById(R.id.cb_Support_Time);
        cb_Over_Time_Request = (CheckBox) findViewById(R.id.cb_Over_Time_Request);
        //cb_Over_Time_Approval = (CheckBox) findViewById(R.id.cb_Over_Time_Approval);
        et_Over_Time_Day = (EditText) findViewById(R.id.et_Over_Time_Day);
        et_Over_Time_Night = (EditText) findViewById(R.id.et_Over_Time_Night);
        et_Over_Time_Day_Temp = (EditText) findViewById(R.id.et_Over_Time_Day_Temp);
        et_Over_Time_Night_Temp = (EditText) findViewById(R.id.et_Over_Time_Night_Temp);
        cb_Over_Time_Day_Request = (CheckBox) findViewById(R.id.cb_Over_Time_Day_Request);
        cb_Over_Time_Night_Request = (CheckBox) findViewById(R.id.cb_Over_Time_Night_Request);
        cb_Over_Time_Day_Temp_Request = (CheckBox) findViewById(R.id.cb_Over_Time_Day_Temp_Request);
        cb_Over_Time_Night_Temp_Request = (CheckBox) findViewById(R.id.cb_Over_Time_Night_Temp_Request);
        //et_Leave_Category_Code = (EditText) findViewById(R.id.et_Leave_Category_Code);
        et_Leave_Category_Description = (EditText) findViewById(R.id.et_Leave_Category_Description);
        et_Leave_Hours = (EditText) findViewById(R.id.et_Leave_Hours);
        et_remark = (EditText) findViewById(R.id.et_remark);
        cb_Meal_Allowance_Day_Req = (CheckBox) findViewById(R.id.cb_Meal_Allowance_Day_Req);
        cb_Meal_Allowance_Night_Req = (CheckBox) findViewById(R.id.cb_Meal_Allowance_Night_Req);
        cb_Allowance_Day_Req = (CheckBox) findViewById(R.id.cb_Allowance_Day_Req);
        cb_Allowance_Night_Req = (CheckBox) findViewById(R.id.cb_Allowance_Night_Req);
        sp_Leave_Category = (Spinner) findViewById(R.id.sp_Leave_Category);

        et_Over_Time_Day.addTextChangedListener(new TxtChange());
        et_Over_Time_Day_Temp.addTextChangedListener(new TxtChange());
        et_Over_Time_Night.addTextChangedListener(new TxtChange());
        et_Over_Time_Night_Temp.addTextChangedListener(new TxtChange());
    }

    private class TxtChange implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable){
            float fOverTime = 0;
            if (!StringUtils.isEmpty(et_Over_Time_Day.getText().toString()))
                fOverTime += StringUtils.ToFloat(et_Over_Time_Day.getText().toString());
            if (!StringUtils.isEmpty(et_Over_Time_Day_Temp.getText().toString()))
                fOverTime += StringUtils.ToFloat(et_Over_Time_Day_Temp.getText().toString());
            if (!StringUtils.isEmpty(et_Over_Time_Night.getText().toString()))
                fOverTime += StringUtils.ToFloat(et_Over_Time_Night.getText().toString());
            if (!StringUtils.isEmpty(et_Over_Time_Night_Temp.getText().toString()))
                fOverTime += StringUtils.ToFloat(et_Over_Time_Night_Temp.getText().toString());
            et_Over_Time.setText(String.valueOf(fOverTime));
        }
    };

    private void ShowItem(WorkDailyReport currentPY) {
        if (currentPY != null) {
            et_Name.setText(currentPY.Worker_Name);
            //et_Routing.setText(currentPY.RoutingName);
            et_Working_Time.setText("" + currentPY.Working_Time);
            cb_Support_Time.setChecked(currentPY.Support_Time);
            cb_Over_Time_Request.setChecked(currentPY.Over_Time_Request);
            et_Over_Time.setText("" + currentPY.Over_Time);
            et_Over_Time.setEnabled(false);
            //cb_Over_Time_Approval.setChecked(currentPY.Over_Time_Approval);
            cb_Over_Time_Day_Request.setChecked(currentPY.Over_Time_Day_Request);
            et_Over_Time_Day.setEnabled(currentPY.Over_Time_Day_Request);
            et_Over_Time_Day.setText("" + currentPY.Over_Time_Day);

            cb_Over_Time_Night_Request.setChecked(currentPY.Over_Time_Night_Request);
            et_Over_Time_Night.setText("" + currentPY.Over_Time_Night);
            et_Over_Time_Night.setEnabled(currentPY.Over_Time_Night_Request);

            cb_Over_Time_Day_Temp_Request.setChecked(currentPY.Over_Time_Day_Temp_Request);
            et_Over_Time_Day_Temp.setText("" + currentPY.Over_Time_Day_Temp);
            et_Over_Time_Day_Temp.setEnabled(currentPY.Over_Time_Day_Temp_Request);

            cb_Over_Time_Night_Temp_Request.setChecked(currentPY.Over_Time_Night_Temp_Request);
            et_Over_Time_Night_Temp.setText("" + currentPY.Over_Time_Night_Temp);
            et_Over_Time_Night_Temp.setEnabled(currentPY.Over_Time_Night_Temp_Request);
            //et_Leave_Category_Code.setText(String.valueOf(staffInputItem
            //		.get("view_Leave_Category_Code")));
            //sp_Leave_Category.setsel
            et_Leave_Category_Description.setText(currentPY.Leave_Category_Description);
            et_Leave_Hours.setText("" + currentPY.Leave_Hours);

            cb_Meal_Allowance_Day_Req.setChecked(currentPY.Meal_Allowance_Day_Req);
            cb_Meal_Allowance_Night_Req.setChecked(currentPY.Meal_Allowance_Night_Req);
            cb_Allowance_Day_Req.setChecked(currentPY.Allowance_Day_Req);
            cb_Allowance_Night_Req.setChecked(currentPY.Allowance_Night_Req);
            et_remark.setText(currentPY.Remark);
        }
    }

    private void SubmitData() {
        if (currentPY != null) {
            if (et_Working_Time.getText().toString().trim() != "" && et_Working_Time.getText().toString().trim().length() != 0) {
                currentPY.Working_Time = Float.parseFloat(et_Working_Time.getText().toString());
            }
            if (et_Over_Time.getText().toString().trim() != "" && et_Over_Time.getText().toString().trim().length() != 0) {
                currentPY.Over_Time = Float.parseFloat(et_Over_Time.getText().toString());
            }
            currentPY.Support_Time = cb_Support_Time.isChecked();
            currentPY.Over_Time_Request = cb_Over_Time_Request.isChecked();
            currentPY.Worker_Position_Code = ((BaseListItem)sp_Worker_Position.getSelectedItem()).getItemId();
            currentPY.Worker_Position_Description = ((BaseListItem)sp_Worker_Position.getSelectedItem()).getItemName();
            //currentPY.Over_Time_Approval = cb_Over_Time_Approval.isChecked();
            if (et_Over_Time_Day.getText().toString().trim() != "" && et_Over_Time_Day.getText().toString().trim().length() != 0) {
                currentPY.Over_Time_Day = Float.parseFloat(et_Over_Time_Day.getText().toString());
            }
            if (et_Over_Time_Night.getText().toString().trim() != "" && et_Over_Time_Night.getText().toString().trim().length() != 0) {
                currentPY.Over_Time_Night = Float.parseFloat(et_Over_Time_Night.getText().toString());
            }
            if (et_Over_Time_Day_Temp.getText().toString().trim() != "" && et_Over_Time_Day_Temp.getText().toString().trim().length() != 0) {
                currentPY.Over_Time_Day_Temp = Float.parseFloat(et_Over_Time_Day_Temp.getText().toString());
            }
            if (et_Over_Time_Night_Temp.getText().toString().trim() != "" && et_Over_Time_Night_Temp.getText().toString().trim().length() != 0) {
                currentPY.Over_Time_Night_Temp = Float.parseFloat(et_Over_Time_Night_Temp.getText().toString());
            }
            currentPY.Over_Time_Day_Request = cb_Over_Time_Day_Request.isChecked();
            currentPY.Over_Time_Night_Request = cb_Over_Time_Night_Request.isChecked();
            currentPY.Over_Time_Day_Temp_Request = cb_Over_Time_Day_Temp_Request.isChecked();
            currentPY.Over_Time_Night_Temp_Request = cb_Over_Time_Night_Temp_Request.isChecked();
            String LeaveCategoryCode = ((LeaveCategoryItem) sp_Leave_Category.getSelectedItem()).GetCode();
            currentPY.Leave_Category_Code = LeaveCategoryCode;
            currentPY.Leave_Category_Description = et_Leave_Category_Description.getText().toString();
            if (et_Leave_Hours.getText().toString().trim() != "" && et_Leave_Hours.getText().toString().trim().length() != 0) {
                currentPY.Leave_Hours = Float.parseFloat(et_Leave_Hours.getText().toString());
            }
            currentPY.Meal_Allowance_Day_Req = cb_Meal_Allowance_Day_Req.isChecked();
            currentPY.Meal_Allowance_Night_Req = cb_Meal_Allowance_Night_Req.isChecked();
            currentPY.Allowance_Day_Req = cb_Allowance_Day_Req.isChecked();
            currentPY.Allowance_Night_Req = cb_Allowance_Night_Req.isChecked();
            currentPY.Remark = et_remark.getText().toString();
            Gson objJson = new Gson();
            String strJson = objJson.toJson(currentPY);
            submitTask = new SubmitTask();
            submitTask.execute(strJson);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.staff_input_item, menu);
        return true;
    }

      public class MyListener implements OnClickListener {
        public void onClick(View v) {
            switch (v.getId()) {
                case (R.id.btn_back):
                    StaffInputItem.this.finish();
                    break;
                case (R.id.btn_staffInput_save):
                    SubmitData();
                    break;
                default:
                    break;
            }
        }
    }

    private class SubmitTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            try
            {
                return getService.WorkDailyReportSV_Update(params[0]);
            }
            catch(Exception ex)
            {
                return null;
            }
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(String result) {
            if (result.equals("true")) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("result", "true");
                intent.putExtras(bundle);
                setResult(1000, intent);
                StaffInputItem.this.finish();
            } else {
                ShowTipMessage("提交失败！", result);
            }
        }
    }

    private class WorkerPositionTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = getService.GetWorkerPosition();
            return result;
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(String result) {
            System.out.println("result:" + result);
            if (!result.trim().equals("null") && !result.trim().equals("[]") && !result.trim().equals("{}") && !result.trim().equals("") && !result.trim().equals("anyType{}")) {
                List<BaseListItem> lstPosition_Code = new BaseListItemParser().getListFromJson(result, "Position_Code", "Position_Description", true);
                ListItemAdapter adapter = new ListItemAdapter(getBaseContext(), lstPosition_Code);
                sp_Worker_Position.setAdapter(adapter);
                for(int i=0; i< lstPosition_Code.size(); i++)
                {
                    if (lstPosition_Code.get(i).getItemId().equals(currentPY.Worker_Position_Code))
                    {
                        sp_Worker_Position.setSelection(i);
                        break;
                    }
                }
            }
        }
    }

    private class LeaveCategoryTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            return getService.GetLeaveCategoryService("");
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(String result) {
            if (!result.trim().equals("null") && !result.trim().equals("[]")) {
                Gson gson = new Gson();
                Type lt = new TypeToken<List<LeaveCategory>>() {
                }.getType();
                List<LeaveCategory> bean = gson.fromJson(result, lt);
                List<LeaveCategoryItem> lst = new ArrayList<LeaveCategoryItem>();
                int iIndex = -1, i = 0;
                for (LeaveCategory aItem : bean) {
                    lst.add(new LeaveCategoryItem(aItem.Code, aItem.Description));
                    if (aItem.Code.equals(currentPY.Leave_Category_Code))
                    {
                        iIndex = i;
                    }
                    i++;
                }
                LeaveCategoryItem aEmptyItem = new LeaveCategoryItem("","");
                lst.add(0, aEmptyItem);
                ArrayAdapter<LeaveCategoryItem> adapter = new ArrayAdapter<LeaveCategoryItem>(context,android.R.layout.simple_spinner_item, lst);
                sp_Leave_Category.setAdapter(adapter);
                sp_Leave_Category.setSelection(iIndex+1);
            }
        }
    }

    private class LeaveCategoryItem {
        private String Code;
        private String Value;

        public LeaveCategoryItem() {
            Code = "";
            Value = "";
        }

        public LeaveCategoryItem(String _Code, String _Value) {
            Code = _Code;
            Value = _Value;
        }

        @Override
        public String toString() {
            return Value;
        }

        public String GetCode() {
            return Code;
        }

        public String GetValue() {
            return Value;
        }
    }
}
