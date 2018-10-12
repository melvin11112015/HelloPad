package com.gki.v107.activity;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.gki.managerment.R;
import com.gki.v107.fragment.InspectConfirm1Fragment;

import java.util.ArrayList;
import java.util.List;

public class InspectionActivity extends AppCompatActivity  {

    private static class MyPagerAdapter extends FragmentStatePagerAdapter{

        private List<Fragment> mFragments;

        private static final String[] titles = new String[]{"检查确认表","检查报告书"};

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2_inspection);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout2_inspection);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager2_inspection);

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(InspectConfirm1Fragment.newInstance("0","0"));
        fragmentList.add(InspectConfirm1Fragment.newInstance("1","1"));

        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(),fragmentList);
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
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
