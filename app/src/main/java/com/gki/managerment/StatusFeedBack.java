package com.gki.managerment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.gki.managerment.bean.FaultPosition;
import com.gki.managerment.bean.ProdLineStatus;
import com.gki.managerment.global.BaseListItem;
import com.gki.managerment.global.BaseListItemParser;
import com.gki.managerment.http.Service.commitToService;
import com.gki.managerment.http.Service.getService;
import com.gki.managerment.util.DateUtils;
import com.gki.managerment.util.StringUtils;
import com.gki.managerment.util.ToastUtil;
import com.gki.managerment.util.UserTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class StatusFeedBack extends BaseActivity implements OnClickListener {
    private java.util.Timer timer;
    public int screenHeight = 0;
    public int screenWidth = 0;
    private String CURRENT_PROD_LINE;
    ProdLineStatus[] aryStatus = new ProdLineStatus[8];
    List<BaseListItem> lstProdLine;
    private Spinner sp_prodLine;
    private Button btnBack;
    private Button btnfeedback_queliaoTime;
    private Button btnfeedback_changeMo;
    private Button btnfeedback_tryDo;
    private Button btnfeedback_quality;
    private Button btnfeedback_regularStop;
    private Button btnfeedback_elctStop;
    private Button btnfeedback_otherStop;
    private Button btnfeedback_eqpmFaltTime;
    private TextView txttextView2, tv_username, tv_date, tv_time, tv_week, tv_feedback_queliaoStart, tv_feedback_queliaoStop, tv_feedback_queliaoTime;
    private TextView tv_feedback_eqpmFaltStart, tv_feedback_eqpmFaltStop, tv_feedback_eqpmFaltTime;
    private TextView tv_feedback_changeMoStart, tv_feedback_changeMoStop, tv_feedback_changeMoTime;
    private TextView tv_feedback_tryDoStart, tv_feedback_tryDoStop, tv_feedback_tryDoTime;
    private TextView tv_feedback_qualityStart, tv_feedback_qualityStop, tv_feedback_qualityTime;
    private TextView tv_feedback_regularStopStart, tv_feedback_regularStopStop, tv_feedback_regularStopTime;
    private TextView tv_feedback_elctStopStart, btn_feedback_elctStopStop, btn_feedback_elctStopTime;
    private TextView tv_feedback_otherStopStart, btn_feedback_otherStopStop, btn_feedback_otherStopTime;
    private GetProdLineState mline;
    private MyCommitTask mCommitTask;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status_feed_back);

        sp_prodLine = (Spinner) findViewById(R.id.sp_prodLine);
        btnBack = (Button) findViewById(R.id.btn_back);
        btnfeedback_queliaoTime = (Button) findViewById(R.id.btn_feedback_queliaoTime);
        btnfeedback_changeMo = (Button) findViewById(R.id.btn_feedback_changeMo);
        btnfeedback_tryDo = (Button) findViewById(R.id.btn_feedback_tryDo);
        btnfeedback_quality = (Button) findViewById(R.id.btn_feedback_quality);
        btnfeedback_regularStop = (Button) findViewById(R.id.btn_feedback_regularStop);
        btnfeedback_elctStop = (Button) findViewById(R.id.btn_feedback_elctStop);
        btnfeedback_otherStop = (Button) findViewById(R.id.btn_feedback_otherStop);
        btnfeedback_eqpmFaltTime = (Button) findViewById(R.id.btn_feedback_eqpmFaltTime);
        txttextView2 = (TextView) findViewById(R.id.tv_title);

        tv_feedback_queliaoStart = (TextView) findViewById(R.id.tv_feedback_queliaoStart);
        tv_feedback_queliaoStop = (TextView) findViewById(R.id.tv_feedback_queliaoStop);
        tv_feedback_queliaoTime = (TextView) findViewById(R.id.tv_feedback_queliaoTime);

        tv_feedback_eqpmFaltStart = (TextView) findViewById(R.id.tv_feedback_eqpmFaltStart);
        tv_feedback_eqpmFaltStop = (TextView) findViewById(R.id.tv_feedback_eqpmFaltStop);
        tv_feedback_eqpmFaltTime = (TextView) findViewById(R.id.tv_feedback_eqpmFaltTime);

        tv_feedback_changeMoStart = (TextView) findViewById(R.id.tv_feedback_changeMoStart);
        tv_feedback_changeMoStop = (TextView) findViewById(R.id.tv_feedback_changeMoStop);
        tv_feedback_changeMoTime = (TextView) findViewById(R.id.tv_feedback_changeMoTime);

        tv_feedback_tryDoStart = (TextView) findViewById(R.id.tv_feedback_tryDoStart);
        tv_feedback_tryDoStop = (TextView) findViewById(R.id.tv_feedback_tryDoStop);
        tv_feedback_tryDoTime = (TextView) findViewById(R.id.tv_feedback_tryDoTime);

        tv_feedback_qualityStart = (TextView) findViewById(R.id.tv_feedback_qualityStart);
        tv_feedback_qualityStop = (TextView) findViewById(R.id.tv_feedback_qualityStop);
        tv_feedback_qualityTime = (TextView) findViewById(R.id.tv_feedback_qualityTime);

        tv_feedback_regularStopStop = (TextView) findViewById(R.id.tv_feedback_regularStopStop);
        tv_feedback_regularStopStart = (TextView) findViewById(R.id.tv_feedback_regularStopStart);
        tv_feedback_regularStopTime = (TextView) findViewById(R.id.tv_feedback_regularStopTime);

        tv_feedback_elctStopStart = (TextView) findViewById(R.id.tv_feedback_elctStopStart);
        btn_feedback_elctStopStop = (TextView) findViewById(R.id.btn_feedback_elctStopStop);
        btn_feedback_elctStopTime = (TextView) findViewById(R.id.btn_feedback_elctStopTime);

        tv_feedback_otherStopStart = (TextView) findViewById(R.id.tv_feedback_otherStopStart);
        btn_feedback_otherStopStop = (TextView) findViewById(R.id.btn_feedback_otherStopStop);
        btn_feedback_otherStopTime = (TextView) findViewById(R.id.btn_feedback_otherStopTime);

        btnfeedback_queliaoTime.setBackgroundColor(Color.parseColor("#008800"));
        btnfeedback_eqpmFaltTime.setBackgroundColor(Color.parseColor("#008800"));
        btnfeedback_changeMo.setBackgroundColor(Color.parseColor("#008800"));
        btnfeedback_tryDo.setBackgroundColor(Color.parseColor("#008800"));
        btnfeedback_quality.setBackgroundColor(Color.parseColor("#008800"));
        btnfeedback_regularStop.setBackgroundColor(Color.parseColor("#008800"));
        btnfeedback_elctStop.setBackgroundColor(Color.parseColor("#008800"));
        btnfeedback_otherStop.setBackgroundColor(Color.parseColor("#008800"));//绿

        //设置为当前登录用户的生产线
        btnBack.setOnClickListener(new MyListener());
        btnfeedback_eqpmFaltTime.setOnClickListener(new MyListener());
        btnfeedback_queliaoTime.setOnClickListener(new MyListener());
        btnfeedback_changeMo.setOnClickListener(new MyListener());
        btnfeedback_tryDo.setOnClickListener(new MyListener());
        btnfeedback_quality.setOnClickListener(new MyListener());
        btnfeedback_regularStop.setOnClickListener(new MyListener());
        btnfeedback_elctStop.setOnClickListener(new MyListener());
        btnfeedback_otherStop.setOnClickListener(new MyListener());

        tv_username = (TextView) findViewById(R.id.tv_username);
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_week = (TextView) findViewById(R.id.tv_week);
        timer = new Timer(true);
        timer.schedule(
                new java.util.TimerTask() { public void run()
                {
                    new RefreshTimeTask().execute();
                }}, 0, 1000);

        tv_username.setText(LoginUser.getUser().User_Name);
        tv_date.setText(LoginUser.getUser().LoginTime.substring(0,10));
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        screenHeight = windowManager.getDefaultDisplay().getHeight();
        screenWidth = windowManager.getDefaultDisplay().getWidth();

        for (int i = 0; i < 8; i++) {
            aryStatus[i] = new ProdLineStatus();
        }
        CURRENT_PROD_LINE = LoginUser.getUser().Prod_Line;
        GetUserProdLine getUserProdLine = new GetUserProdLine();
        getUserProdLine.execute(LoginUser.getUser().User_ID);
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
                lstProdLine = new BaseListItemParser().getListFromJson(result, "Prod_Line", "Prod_Line_Name", false);
                UserProdLineAdapter adapter = new UserProdLineAdapter(lstProdLine);
                sp_prodLine.setAdapter(adapter);
                sp_prodLine.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        CURRENT_PROD_LINE = ((BaseListItem)adapterView.getSelectedItem()).getItemId();
                        RefreshState();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                for(int i=0; i< lstProdLine.size(); i++)
                {
                    if (lstProdLine.get(i).getItemId().toString().equals(CURRENT_PROD_LINE))
                    {
                        sp_prodLine.setSelection(i);
                        break;
                    }
                }
            }
        }
    }

    private void RefreshState()
    {
        if (StringUtils.isEmpty(CURRENT_PROD_LINE))
        {
            ShowTipMessage("请选择生产线！");
        }
        else {
            try {
                showLoadingDialog();
                mline = new GetProdLineState();
                mline.execute(CURRENT_PROD_LINE, "0");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public <T> void jump(Class<T> MyClass) {
        Intent intent = new Intent();
        intent.setClass(this, MyClass);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {

    }

    public class MyListener implements OnClickListener {
        public void onClick(View v) {
            try {
                switch (v.getId()) {
                    case (R.id.btn_back):
                        StatusFeedBack.this.finish();
                        break;
                    case (R.id.btn_feedback_queliaoTime):
                        SendStatus(1);
                        break;
                    case (R.id.btn_feedback_eqpmFaltTime):
                        try {
                            if (aryStatus[1] != null && (aryStatus[1].getStarting_Time().equals("") || (aryStatus[1].getStarting_Time() != "" && aryStatus[1].getEnding_Time() != ""))) {
                                showFaultPositionDialog();
                            } else {
                                SendStatus(2);
                            }
                        }
                        catch(Exception ex)
                        {
                            ToastUtil.show(context, ex.getMessage());
                        }
                        break;
                    case (R.id.btn_feedback_changeMo):
                        SendStatus(3);
                        break;
                    case (R.id.btn_feedback_tryDo):
                        SendStatus(4);
                        break;
                    case (R.id.btn_feedback_quality):
                        SendStatus(5);
                        break;
                    case (R.id.btn_feedback_regularStop):
                        SendStatus(6);
                        break;
                    case (R.id.btn_feedback_elctStop):
                        SendStatus(7);
                        break;
                    case (R.id.btn_feedback_otherStop):
                        SendStatus(8);
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                //txttextView2.setText(e.getMessage());
                txttextView2.setText(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void SendStatus(int iStopType)
    {
        if (aryStatus[iStopType-1].equals(null)) {
            return;
        }
        //开始
        if (aryStatus[iStopType-1].getStarting_Time().equals("") || (!aryStatus[iStopType-1].getStarting_Time().equals("") && !aryStatus[iStopType-1].getEnding_Time().equals(""))) {
            //aryStatus[iStopType - 1].setStop_Type(iStopType);
            //aryStatus[iStopType-1].setProd_Line(sp_prodLine.getInputData());
            aryStatus[iStopType-1].setStarting_Time(DateUtils.getDataString("yyyy-MM-dd HH:mm:ss"));
            aryStatus[iStopType-1].setEnding_Time("");
            aryStatus[iStopType-1].setMinutes(0);
        }
        else
        {
            //结束
            aryStatus[iStopType-1].setEnding_Time(DateUtils.getDataString("yyyy-MM-dd HH:mm:ss"));
            aryStatus[iStopType-1].setMinutes(aryStatus[iStopType-1].getMinutes());
        }
        aryStatus[iStopType-1].setProduction_Date(DateUtils.getDataString("yyyy-MM-dd"));
        aryStatus[iStopType-1].setSubmit_Datetime(DateUtils.getDataString("yyyy-MM-dd HH:mm:ss"));
        aryStatus[iStopType-1].setSubmit_User_ID(LoginUser.user.User_ID);
        aryStatus[iStopType-1].setSubmit_User_Name(LoginUser.user.User_Name);
        SetStopStyle(iStopType);
        Gson gson = new Gson();
        String strJson = gson.toJson(aryStatus[iStopType-1]);
        System.out.println("commit json:" + strJson);
        mCommitTask = new MyCommitTask();
        mCommitTask.execute(strJson);
        System.out.println("toJson====>" + strJson);
    }

    private class MyCommitTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            return commitToService.SendProdLineStateService(params[0]);
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(String result) {
            try {
                System.out.println("execute result:" + result);
                if (result.trim().equals("true")) {
                    ShowTipMessage("提交成功！");
                } else {
                    ShowTipMessage("提交失败！", result);
                }
            }
            catch(Exception ex)
            {
                System.out.println("execute result:" + ex.getMessage());
            }
        }
    }

    /**************************************
     ***            故障弹出层          ***
    **************************************/
    private void showFaultPositionDialog() {
        curFaultPosition = "";
        new UserTask<String, String, String>() {
            @Override
            protected void onTaskPrepare() {
                super.onTaskPrepare();
                showLoadingDialog();
            }

            @Override
            protected void onErrorHandle(Context context, Exception error) {
                ShowTipMessage("加载失败", error.getMessage());
                dismissLoadingDialog();
            }

            @Override
            protected void onTaskFinished(Context context, String s) {
                dismissLoadingDialog();
                final Dialog dialog = new Dialog(StatusFeedBack.this);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setTitle("请选择故障部位");
                View content = LayoutInflater.from(getBaseContext()).inflate(R.layout.feedback_fault_part, null);
                Button dismiss = (Button) content.findViewById(R.id.btn_dismiss);
                dismiss.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                content.findViewById(R.id.btn_confirm).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        aryStatus[1].setFault_Position_Code(curFaultPosition);
                        SendStatus(2);
                    }
                });
                if (!TextUtils.isEmpty(s)) {
                    lstFault.clear();
                    List<FaultPosition> responses = new Gson().fromJson(s, new TypeToken<List<FaultPosition>>() {
                    }.getType());
                    if (responses != null) {
                        lstFault.addAll(responses);
                    }
                }
                dialog.show();
                ListView lv = (ListView) content.findViewById(R.id.list);
                faultPositionAdapter = new FaultPositionAdapter();
                lv.setAdapter(faultPositionAdapter);
                dialog.setContentView(content);
                final WindowManager.LayoutParams attrs = dialog.getWindow().getAttributes();
                attrs.width = (int) (screenWidth * 0.4);
                attrs.height = (int) (screenHeight * 0.7);
                dialog.getWindow().setAttributes(attrs);
            }
        }.registerCallback(new UserTask.TaskCallback<String, String, String>() {
            @Override
            public String call(UserTask<String, String, String> task, String[] params) throws Exception {
                String strStatus = getService.GetFaultPart(aryStatus[1].getProd_Line());
                return strStatus;
            }
        }).execute("");
    }

    private class GetProdLineState extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = getService.GetProdLineStateService(params[0], Integer.parseInt(params[1]));
            return result;
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(String result) {
            System.out.println("result:" + result);
            if (!result.trim().equals("null") && !result.trim().equals("[]") && !result.trim().equals("{}") && !result.trim().equals("") && !result.trim().equals("anyType{}")) {
                Gson gson = new Gson();
                try {
                    List<ProdLineStatus> lstStatus = new Gson().fromJson(result, new TypeToken<List<ProdLineStatus>>() {
                    }.getType());
                    int iStopType = 0;
                    if (lstStatus.size() == 8) {
                        for (int i = 0; i < lstStatus.size(); i++) {
                            aryStatus[lstStatus.get(i).getStop_Type() - 1].setStarting_Time(lstStatus.get(i).getStarting_Time());
                            aryStatus[lstStatus.get(i).getStop_Type() - 1].setEnding_Time(lstStatus.get(i).getEnding_Time());
                            aryStatus[lstStatus.get(i).getStop_Type() - 1].setProd_Line(lstStatus.get(i).getProd_Line());
                            aryStatus[lstStatus.get(i).getStop_Type() - 1].setStop_Type(lstStatus.get(i).getStop_Type());
                            aryStatus[lstStatus.get(i).getStop_Type() - 1].setFault_Position_Code(lstStatus.get(i).getFault_Position_Code());
                        }
                    } else {
                        iStopType = lstStatus.get(0).getStop_Type();
                        aryStatus[iStopType - 1].setStarting_Time(lstStatus.get(0).getStarting_Time());
                        aryStatus[iStopType - 1].setEnding_Time(lstStatus.get(0).getEnding_Time());
                        aryStatus[iStopType - 1].setFault_Position_Code(lstStatus.get(0).getFault_Position_Code());
                    }
                    SetStopStyle(iStopType);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            dismissLoadingDialog();
        }
    }

    private void SetStopStyle(int iStopType) {
        String Starting_Time;
        String Ending_Time;
        if (iStopType == 1 || iStopType == 0) {
            Starting_Time = aryStatus[0].getStarting_Time();
            Ending_Time = aryStatus[0].getEnding_Time();
            if (Starting_Time.equals("")) {
                btnfeedback_queliaoTime.setBackgroundColor(Color.parseColor("#008800"));
            } else if (!Starting_Time.equals("") && Ending_Time.equals("")) {
                btnfeedback_queliaoTime.setBackgroundColor(Color.parseColor("#FF0000"));//红
            } else {
                btnfeedback_queliaoTime.setBackgroundColor(Color.parseColor("#008800"));
            }
            tv_feedback_queliaoStart.setText(Starting_Time == "" ? "开始时间" : Starting_Time);
            tv_feedback_queliaoStop.setText(Ending_Time == "" ? "结束时间" : Ending_Time);
            if (aryStatus[0].getMinutes() > 0)
                tv_feedback_queliaoTime.setText("持续:" + StringUtils.FormatNumber(aryStatus[0].getMinutes()) +"分钟");
            else
                tv_feedback_queliaoTime.setText("持续时间");
        }
        if (iStopType == 2 || iStopType == 0) {
            Starting_Time = aryStatus[1].getStarting_Time();
            Ending_Time = aryStatus[1].getEnding_Time();
            if (Starting_Time.equals(""))
                btnfeedback_eqpmFaltTime.setBackgroundColor(Color.parseColor("#008800"));
            else if (!Starting_Time.equals("") && Ending_Time.equals(""))
                btnfeedback_eqpmFaltTime.setBackgroundColor(Color.parseColor("#FF0000"));
            else
                btnfeedback_eqpmFaltTime.setBackgroundColor(Color.parseColor("#008800"));

            tv_feedback_eqpmFaltStart.setText(Starting_Time == "" ? "开始时间" : Starting_Time);
            tv_feedback_eqpmFaltStop.setText(Ending_Time == "" ? "结束时间" : Ending_Time);
            if (aryStatus[1].getMinutes() > 0)
                tv_feedback_eqpmFaltTime.setText("持续:" + StringUtils.FormatNumber(aryStatus[1].getMinutes()) +"分钟");
            else
                tv_feedback_eqpmFaltTime.setText("持续时间");
        }
        if (iStopType == 3 || iStopType == 0) {
            Starting_Time = aryStatus[2].getStarting_Time();
            Ending_Time = aryStatus[2].getEnding_Time();
            if (Starting_Time.equals(""))
                btnfeedback_changeMo.setBackgroundColor(Color.parseColor("#008800"));
            else if (!Starting_Time.equals("") && Ending_Time.equals(""))
                btnfeedback_changeMo.setBackgroundColor(Color.parseColor("#FF0000"));
            else
                btnfeedback_changeMo.setBackgroundColor(Color.parseColor("#008800"));

            tv_feedback_changeMoStart.setText(Starting_Time == "" ? "开始时间" : Starting_Time);
            tv_feedback_changeMoStop.setText(Ending_Time == "" ? "结束时间" : Ending_Time);
            if (aryStatus[0].getMinutes() > 0)
                tv_feedback_changeMoTime.setText("持续:" + StringUtils.FormatNumber(aryStatus[2].getMinutes()) +"分钟");
            else
                tv_feedback_changeMoTime.setText("持续时间");
        }
        if (iStopType == 4 || iStopType == 0) {
            Starting_Time = aryStatus[3].getStarting_Time();
            Ending_Time = aryStatus[3].getEnding_Time();
            if (Starting_Time.equals(""))
                btnfeedback_tryDo.setBackgroundColor(Color.parseColor("#008800"));
            else if (!Starting_Time.equals("") && Ending_Time.equals(""))
                btnfeedback_tryDo.setBackgroundColor(Color.parseColor("#FF0000"));
            else
                btnfeedback_tryDo.setBackgroundColor(Color.parseColor("#008800"));

            tv_feedback_tryDoStart.setText(Starting_Time == "" ? "开始时间" : Starting_Time);
            tv_feedback_tryDoStop.setText(Ending_Time == "" ? "结束时间" : Ending_Time);
            if (aryStatus[3].getMinutes() > 0)
                tv_feedback_tryDoTime.setText("持续:" + StringUtils.FormatNumber(aryStatus[3].getMinutes()) +"分钟");
            else
                tv_feedback_tryDoTime.setText("持续时间");
        }
        if (iStopType == 5 || iStopType == 0) {
            Starting_Time = aryStatus[4].getStarting_Time();
            Ending_Time = aryStatus[4].getEnding_Time();
            if (Starting_Time.equals(""))
                btnfeedback_quality.setBackgroundColor(Color.parseColor("#008800"));
            else if (!Starting_Time.equals("") && Ending_Time.equals(""))
                btnfeedback_quality.setBackgroundColor(Color.parseColor("#FF0000"));
            else
                btnfeedback_quality.setBackgroundColor(Color.parseColor("#008800"));

            tv_feedback_qualityStart.setText(Starting_Time == "" ? "开始时间" : Starting_Time);
            tv_feedback_qualityStop.setText(Ending_Time == "" ? "结束时间" : Ending_Time);
            if (aryStatus[4].getMinutes() > 0)
                tv_feedback_qualityTime.setText("持续:" + StringUtils.FormatNumber(aryStatus[4].getMinutes()) +"分钟");
            else
                tv_feedback_qualityTime.setText("持续时间");
        }
        if (iStopType == 6 || iStopType == 0) {
            Starting_Time = aryStatus[5].getStarting_Time();
            Ending_Time = aryStatus[5].getEnding_Time();
            if (Starting_Time.equals(""))
                btnfeedback_regularStop.setBackgroundColor(Color.parseColor("#008800"));
            else if (!Starting_Time.equals("") && Ending_Time.equals(""))
                btnfeedback_regularStop.setBackgroundColor(Color.parseColor("#FF0000"));
            else
                btnfeedback_regularStop.setBackgroundColor(Color.parseColor("#008800"));

            tv_feedback_regularStopStart.setText(Starting_Time == "" ? "开始时间" : Starting_Time);
            tv_feedback_regularStopStop.setText(Ending_Time == "" ? "结束时间" : Ending_Time);
            if (aryStatus[5].getMinutes() > 0)
                tv_feedback_regularStopTime.setText("持续:" + StringUtils.FormatNumber(aryStatus[5].getMinutes()) +"分钟");
            else
                tv_feedback_regularStopTime.setText("持续时间");
        }
        if (iStopType == 7 || iStopType == 0) {
            Starting_Time = aryStatus[6].getStarting_Time();
            Ending_Time = aryStatus[6].getEnding_Time();
            if (Starting_Time.equals(""))
                btnfeedback_elctStop.setBackgroundColor(Color.parseColor("#008800"));
            else if (!Starting_Time.equals("") && Ending_Time.equals(""))
                btnfeedback_elctStop.setBackgroundColor(Color.parseColor("#FF0000"));
            else
                btnfeedback_elctStop.setBackgroundColor(Color.parseColor("#008800"));

            tv_feedback_elctStopStart.setText(Starting_Time == "" ? "开始时间" : Starting_Time);
            btn_feedback_elctStopStop.setText(Ending_Time == "" ? "结束时间" : Ending_Time);
            if (aryStatus[6].getMinutes() > 0)
                btn_feedback_elctStopTime.setText("持续:" + StringUtils.FormatNumber(aryStatus[6].getMinutes()) +"分钟");
            else
                btn_feedback_elctStopTime.setText("持续时间");
        }
        if (iStopType == 8 || iStopType == 0) {
            Starting_Time = aryStatus[7].getStarting_Time();
            Ending_Time = aryStatus[7].getEnding_Time();
            if (Starting_Time.equals(""))
                btnfeedback_otherStop.setBackgroundColor(Color.parseColor("#008800"));
            else if (!Starting_Time.equals("") && Ending_Time.equals(""))
                btnfeedback_otherStop.setBackgroundColor(Color.parseColor("#FF0000"));
            else
                btnfeedback_otherStop.setBackgroundColor(Color.parseColor("#008800"));
            tv_feedback_otherStopStart.setText(Starting_Time == "" ? "开始时间" : Starting_Time);
            btn_feedback_otherStopStop.setText(Ending_Time == "" ? "结束时间" : Ending_Time);
            if (aryStatus[7].getMinutes() > 0)
                btn_feedback_otherStopTime.setText("持续:" + StringUtils.FormatNumber(aryStatus[7].getMinutes()) +"分钟");
            else
                btn_feedback_otherStopTime.setText("持续时间");
        }
    }

    String curFaultPosition;
    FaultPositionAdapter faultPositionAdapter;
    private List<FaultPosition> lstFault = new ArrayList<FaultPosition>();
    private class FaultPositionAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return lstFault.size();
        }

        @Override
        public Object getItem(int position) {
            return lstFault.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final AddNewViewHolder holder;

            if (convertView == null) {
                convertView = LayoutInflater.from(getBaseContext()).inflate(R.layout.fault_list_item, null);
                holder = new AddNewViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (AddNewViewHolder) convertView.getTag();
            }
            final FaultPosition response = lstFault.get(position);
            holder.rdb.setChecked(response.isChecked);
            holder.rdb.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean checked = holder.rdb.isChecked();
                    if (checked) {
                        for (FaultPosition aPosition : lstFault)
                        {
                            aPosition.isChecked = false;
                        }
                        response.isChecked = true;
                        curFaultPosition = response.Fault_Position_Code;
                    } else {
                        response.isChecked = false;
                    }
                    notifyDataSetChanged();
                }
            });
            holder.faultCode.setText(response.Fault_Position_Code);
            holder.faultName.setText(response.Fault_Position_Description);
            return convertView;
        }
    }

    private class AddNewViewHolder {
        RadioButton rdb;
        TextView faultName, faultCode;

        public AddNewViewHolder(View view) {
            rdb = (RadioButton) view.findViewById(R.id.rdb_check);
            faultName = (TextView) view.findViewById(R.id.faultName);
            faultCode = (TextView) view.findViewById(R.id.faultCode);
        }
    }

    private void loadFaultPosition(String productLine) {
        new UserTask<String, String, String>() {

            @Override
            protected void onErrorHandle(Context context, Exception error) {

            }

            @Override
            protected void onTaskFinished(Context context, String s) {
                if (!TextUtils.isEmpty(s)) {
                    lstFault.clear();
                    List<FaultPosition> responses = new Gson().fromJson(s, new TypeToken<List<FaultPosition>>() {
                    }.getType());
                    if (responses != null) {
                        lstFault.addAll(responses);
                    }
                }
            }
        }.registerCallback(new UserTask.TaskCallback<String, String, String>() {
            @Override
            public String call(UserTask<String, String, String> task, String[] params) throws Exception {
                return getService.GetFaultPart(params[0]);
            }
        }).execute(productLine);
    }

    private class RefreshTimeTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            return null;
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(String result) {
            tv_date.setText(DateUtils.getDataString(DateUtils.DateFormat.PART_DATE_FORMAT));
            tv_time.setText(DateUtils.getDataString(DateUtils.DateFormat.TIME_FORMAT));
            tv_week.setText(DateUtils.getWeekString(DateUtils.getDataString(DateUtils.DateFormat.FULL_DATE_FORMAT)));
        }
    }
}
