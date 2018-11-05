package com.gki.managerment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gki.managerment.util.DateUtils;
import com.gki.managerment.util.ToastUtil;
import com.gki.v107.activity.BuildMessageActivity;
import com.gki.v107.activity.InspectionActivity;

import com.gki.v107.activity.PadMessageListActivity;
import com.gki.v107.entity.PadMessageInfo;
import com.gki.v107.net.ApiTool;
import com.gki.v107.net.GenericOdataCallback;

import java.util.List;
import java.util.Timer;



public class MainMenu extends BaseActivity {
    private TextView tv_date,tv_time,tv_week,tvMessage;
    private LinearLayout laMessage;
    private java.util.Timer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_week = (TextView) findViewById(R.id.tv_week);
        ((TextView)findViewById(R.id.tv_username)).setText(LoginUser.getUser().User_Name);
        ((TextView)findViewById(R.id.tv_date)).setText(LoginUser.getUser().LoginTime.substring(0,10));

        findViewById(R.id.btn_back).setOnClickListener(new MyListener());
        findViewById(R.id.tv_username).setOnClickListener(new MyListener());
        findViewById(R.id.btn_workDaily).setOnClickListener(new MyListener());
        findViewById(R.id.btn_staff).setOnClickListener(new MyListener());
        findViewById(R.id.btn_reworkingConfirmed).setOnClickListener(new MyListener());
        findViewById(R.id.btn_conduction).setOnClickListener(new MyListener());

        //melvin
        findViewById(R.id.btn_inspection).setOnClickListener(new MyListener());
        tvMessage = (TextView) findViewById(R.id.tv2_main_message);
        laMessage = (LinearLayout) findViewById(R.id.la2_main_message);
        laMessage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMenu.this,PadMessageListActivity.class));
            }
        });
        findViewById(R.id.button2_main_message).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMenu.this, BuildMessageActivity.class));
            }
        });

        timer = new Timer(true);
        timer.schedule(
                new java.util.TimerTask() { public void run()
                {
                    new RefreshTimeTask().execute();
                }}, 0, 1000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ApiTool.callPadMessageList(new GenericOdataCallback<PadMessageInfo>() {
            @Override
            public void onDataAvailable(List<PadMessageInfo> datas) {
                if(datas!=null && !datas.isEmpty()){
                    PadMessageInfo info = datas.get(0);
                    String showStr = info.getCreate_User()+" ("+info.getProdLineName()+"): "+info.getMsg();
                    tvMessage.setText(showStr);
                }else{
                    tvMessage.setText("");
                    tvMessage.setHint("没有任何消息");
                }
            }

            @Override
            public void onDataUnAvailable(String msg, int errorCode) {
                ToastUtil.show(MainMenu.this,"获取消息失败");
            }
        });
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

    public class MyListener implements OnClickListener {
        public void onClick(View v) {
            switch (v.getId()) {
                //点击修改密码
                case (R.id.tv_username):
                    OpenForm(ChangePassword.class);
                    break;
                case (R.id.btn_back):
                    LoginUser.setUser(null);
                    OpenForm(Main.class);
                    MainMenu.this.finish();
                    break;
                case (R.id.btn_workDaily):
                    OpenForm(DailyworkInput.class);
                    break;
                case (R.id.btn_staff):
                    OpenForm(StaffInput.class);
                    break;
                case (R.id.btn_reworkingConfirmed):
                    OpenForm(ConditionConfirm.class);
                    break;
                case (R.id.btn_conduction):
                    OpenForm(StatusFeedBack.class);
                    break;
                    //melvin
                case (R.id.btn_inspection):
                    OpenForm(InspectionActivity.class);
                    break;
                default:
                    break;
            }
        }
    }
}