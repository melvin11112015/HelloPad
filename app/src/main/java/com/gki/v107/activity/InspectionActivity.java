package com.gki.v107.activity;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.gki.managerment.R;
import com.gki.v107.fragment.InspectConfirm2Fragment;
import com.gki.v107.fragment.InspectConfirm3Fragment;
import com.gki.v107.myinterface.FragmentInteractionInterface;
import com.gki.v107.fragment.InspectConfirm1Fragment;

import java.util.ArrayList;
import java.util.List;

public class InspectionActivity extends AppCompatActivity implements View.OnClickListener {



    private static class MyPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> mFragments;

        private static final String[] titles = new String[]{"构成部件","确认项目","检查报告书"};

        public MyPagerAdapter(FragmentManager fm,List<Fragment> mFragments) {
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

    private EditText etOrderno;
    private RadioGroup radioGroup;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button2_inspection1_check:
                doCheck();
                break;
            case R.id.button2_inspection1_submit:
                etOrderno.requestFocus();
                doSubmit();
                break;
        }
    }

    List<Fragment> fragmentList = new ArrayList<>();

    private void doCheck(){
        for(Fragment f:fragmentList)
            if(f instanceof FragmentInteractionInterface)
                ((FragmentInteractionInterface)f).acquireDatas(etOrderno.getText().toString(),getStepCode());
    }

    private void doSubmit(){

        for(Fragment f:fragmentList)
            if(f instanceof FragmentInteractionInterface)
                ((FragmentInteractionInterface)f).submitDatas();
    }

    private int getStepCode(){
        int stepCode = 1;
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
        return stepCode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2_inspection);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        etOrderno = (EditText) findViewById(R.id.et2_inspection1_pno);
        radioGroup = (RadioGroup) findViewById(R.id.radionGroup2_inspection1_step);

        findViewById(R.id.button2_inspection1_check).setOnClickListener(this);
        findViewById(R.id.button2_inspection1_submit).setOnClickListener(this);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout2_inspection);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager2_inspection);

        fragmentList.add(InspectConfirm1Fragment.newInstance("0","0"));
        fragmentList.add(InspectConfirm2Fragment.newInstance("1","1"));
        fragmentList.add(InspectConfirm3Fragment.newInstance("2","2"));

        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(),fragmentList);
        viewPager.setOffscreenPageLimit(3);

        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

        etOrderno.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode==KeyEvent.KEYCODE_ENTER){
                    doCheck();
                    return true;
                }
                else return false;
            }
        });
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
