package com.gki.v107.activity;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.gki.managerment.LoginUser;
import com.gki.managerment.R;
import com.gki.managerment.util.ToastUtil;
import com.gki.v107.entity.WebProdOrderInfo;
import com.gki.v107.fragment.InspectConfirm1Fragment;
import com.gki.v107.fragment.InspectConfirm2Fragment;
import com.gki.v107.fragment.InspectConfirm3Fragment;
import com.gki.v107.myinterface.FragmentInteractionInterface;
import com.gki.v107.net.ApiTool;
import com.gki.v107.net.GenericOdataCallback;
import com.gki.v107.tool.DatetimeTool;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 检查确认
 */

public class InspectionActivity extends AppCompatActivity implements View.OnClickListener {


    private TextView tvstarttime, tvendtime, tvDate;

    private EditText etOrderno;
    //private RadioGroup radioGroup;
    private TextView tvPartNo;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Calendar c = Calendar.getInstance();
            switch (v.getId()) {
                case R.id.tv2_inspection1_time1:
                    showTimePickerDialog(InspectionActivity.this, android.R.style.Theme_Holo_Light_Dialog, tvstarttime, c);
                    break;
                case R.id.tv2_inspection1_time2:
                    showTimePickerDialog(InspectionActivity.this, android.R.style.Theme_Holo_Light_Dialog, tvendtime, c);
                    break;
                case R.id.tv2_inspection1_date:
                    new DatePickerDialog(InspectionActivity.this, android.R.style.Theme_Holo_Light_Dialog, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            tvDate.setText(String.format("%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth));
                        }
                    }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
                    break;
            }
        }
    };

    List<Fragment> fragmentList = new ArrayList<>();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button2_inspection1_check:
                doCheck();
                break;
            case R.id.button2_inspection1_submit:
                etOrderno.requestFocus();
                doSubmit();
                break;
            case R.id.button2_inspection1_back:
                finish();
                break;
        }
    }

    /**
     * 检查生产单号
     */
    private void doCheck() {

        if (etOrderno.getText().toString().trim().isEmpty()) {
            ToastUtil.show(this, "请输入生产单号");
            return;
        }

        String filter = "No eq '" + etOrderno.getText().toString().trim() + "' and Status eq 'Released'";

        ApiTool.callWebProdOrderList(filter, new GenericOdataCallback<WebProdOrderInfo>() {
            @Override
            public void onDataAvailable(List<WebProdOrderInfo> datas) {
                if (datas == null || datas.isEmpty()) {
                    ToastUtil.show(InspectionActivity.this, "找不到该订单，请确认订单是否正确！");
                    return;
                }

                WebProdOrderInfo bean = datas.get(0);

                if (bean.getProduction_line() == null || LoginUser.getUser().All_Prod_Line == null || !LoginUser.getUser().All_Prod_Line.contains(bean.getProduction_line())) {
                    ToastUtil.show(InspectionActivity.this, "【生产单】不正确：该【生产单】不属于您所在【生产线】！");
                    return;
                }

                tvPartNo.setText(bean.getSource_No());
                tvDate.setText(DatetimeTool.convertOdataTimezone(bean.getAcative_Starting_Date(), DatetimeTool.TYPE_DATE, bean.getAcative_Starting_Date().contains("Z")));

                for (Fragment f : fragmentList)
                    if (f instanceof FragmentInteractionInterface)
                        ((FragmentInteractionInterface) f).acquireDatas(etOrderno.getText().toString().trim(),
                                getStepCode(),
                                bean.getSource_No(), tvstarttime, tvendtime);
            }

            @Override
            public void onDataUnAvailable(String msg, int errorCode) {
                ToastUtil.show(InspectionActivity.this, "网络请求失败:" + msg);
            }
        });

        //new GetByDocumentNoTask().execute(etOrderno.getText().toString());
    }

    /**
     * 同时提交
     */
    private void doSubmit() {

        for (Fragment f : fragmentList)
            if (f instanceof FragmentInteractionInterface)
                ((FragmentInteractionInterface) f).submitDatas(tvDate, tvstarttime, tvendtime);
    }

    private int getStepCode() {
        int stepCode = 1;
        /*
        switch (radioGroup.getCheckedRadioButtonId()){
            case R.id.radioButton2_inspection1_step1:
                stepCode = 1;
                break;
            case R.id.radioButton2_inspection1_step2:
                stepCode = 2;
                break;
            case R.id.radioButton2_inspection1_step3:
                stepCode = 3;
                break;
        }
        */
        return stepCode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //去除title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去掉Activity上面的状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity2_inspection);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //隐藏actionbar
        getSupportActionBar().hide();

        tvstarttime = (TextView) findViewById(R.id.tv2_inspection1_time1);
        tvendtime = (TextView) findViewById(R.id.tv2_inspection1_time2);
        tvDate = (TextView) findViewById(R.id.tv2_inspection1_date);

        tvstarttime.setOnClickListener(onClickListener);
        tvendtime.setOnClickListener(onClickListener);
        tvDate.setOnClickListener(onClickListener);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        tvDate.setText(sdf1.format(calendar.getTime()));
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
        tvstarttime.setText(sdf2.format(calendar.getTime()));
        tvendtime.setText(sdf2.format(calendar.getTime()));

        etOrderno = (EditText) findViewById(R.id.et2_inspection1_pno);
        tvPartNo = (TextView) findViewById(R.id.tv2_inspection1_partno);
        //radioGroup = (RadioGroup) findViewById(R.id.radionGroup2_inspection1_step);

        findViewById(R.id.button2_inspection1_check).setOnClickListener(this);
        findViewById(R.id.button2_inspection1_submit).setOnClickListener(this);
        findViewById(R.id.button2_inspection1_back).setOnClickListener(this);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout2_inspection);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager2_inspection);

        fragmentList.add(InspectConfirm1Fragment.newInstance("0", "0"));
        fragmentList.add(InspectConfirm2Fragment.newInstance("1", "1"));
        fragmentList.add(InspectConfirm3Fragment.newInstance("2", "2"));

        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setOffscreenPageLimit(3);

        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

        etOrderno.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    doCheck();
                    return true;
                } else return false;
            }
        });
/*
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                doCheck();
            }
        });
        */
    }


    /**
     * 时间选择
     *
     * @param context
     * @param themeResId
     * @param tv
     * @param calendar
     */
    public static void showTimePickerDialog(Context context, int themeResId, final TextView tv, Calendar calendar) {
        // Calendar c = Calendar.getInstance();
        // 创建一个TimePickerDialog实例，并把它显示出来
        new TimePickerDialog(context, themeResId,
                // 绑定监听器
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        tv.setText(String.format("%02d:%02d", hourOfDay, minute));
                    }
                }
                // 设置初始时间
                , calendar.get(Calendar.HOUR_OF_DAY)
                , calendar.get(Calendar.MINUTE)
                // true表示采用24小时制
                , true).show();
    }

    private static class MyPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> mFragments;

        private static final String[] titles = new String[]{"初终回确认1", "初终回确认2", "检查报告书"};

        public MyPagerAdapter(FragmentManager fm, List<Fragment> mFragments) {
            super(fm);
            this.mFragments = mFragments;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
