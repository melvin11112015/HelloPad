package com.gki.managerment;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.gki.managerment.util.DateUtils;

import java.util.Timer;

public class MainMenu extends BaseActivity {
    private TextView tv_date,tv_time,tv_week;
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

        timer = new Timer(true);
        timer.schedule(
                new java.util.TimerTask() { public void run()
                {
                    new RefreshTimeTask().execute();
                }}, 0, 1000);
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
                default:
                    break;
            }
        }
    }
}