package com.gki.v107.fragment;


import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gki.managerment.R;
import com.gki.managerment.util.ToastUtil;
import com.gki.v107.entity.ProdConfirmDetailsAddon;
import com.gki.v107.entity.ProdConfirmBomItemsAddon;
import com.gki.v107.entity.ProdConfirmItemsInfo;
import com.gki.v107.myinterface.FragmentInteractionInterface;
import com.gki.v107.net.ApiTool;
import com.gki.v107.net.BaseOdataCallback;
import com.gki.v107.net.GenericOdataCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InspectConfirm2Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InspectConfirm2Fragment extends Fragment implements FragmentInteractionInterface {


    public InspectConfirm2Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment InspectConfirm1Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InspectConfirm2Fragment newInstance(String param1, String param2) {
        InspectConfirm2Fragment fragment = new InspectConfirm2Fragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tv2_inspection1_time1:
                    showTimePickerDialog(getContext(),android.R.style.Theme_Holo_Light_Dialog,tvstarttime,Calendar.getInstance());
                    break;
                case R.id.tv2_inspection1_time2:
                    showTimePickerDialog(getContext(),android.R.style.Theme_Holo_Light_Dialog,tvendtime,Calendar.getInstance());
                    break;
            }
        }
    };


    private TextView tvstarttime,tvendtime,tvDate;

    public void acquireDatas(String orderno){

        if(orderno.isEmpty() || adapter == null)return;

        currentOrderNo = orderno;

        ApiTool.callProdConfirmItemsList(new GenericOdataCallback<ProdConfirmItemsInfo>() {
            @Override
            public void onDataAvailable(List<ProdConfirmItemsInfo> datas) {
                datalist.clear();
                datalist.addAll(datas);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onDataUnAvailable(String msg, int errorCode) {
                datalist.clear();
                adapter.notifyDataSetChanged();
                ToastUtil.show(getContext(),msg);
            }
        });
    }

    private int successCount = 0,totalCount = 0;
    private String currentOrderNo = "";

    public void submitDatas(){
        if(adapter == null || tvstarttime == null || tvendtime == null || tvDate == null) return;
        final List<ProdConfirmDetailsAddon> addons
                = adapter.createAddonList(tvstarttime.getText().toString(),tvendtime.getText().toString(),tvDate.getText().toString(),currentOrderNo);
        if(addons == null || addons.isEmpty())return;
        if(tvstarttime.getText().toString().compareTo(tvendtime.getText().toString())>0){
            ToastUtil.show(getContext(),"始业时不能大于终业时");
            return;
        }
        successCount = 0;
        totalCount = 0;
        final StringBuilder stringBuilder = new StringBuilder();
        for(ProdConfirmDetailsAddon addon:addons){
            ApiTool.addProdConfirmDetails(addon, new BaseOdataCallback<Map<String, Object>>() {
                @Override
                public void onDataAvailable(Map<String, Object> datas) {
                    successCount++;
                    totalCount++;
                    toastResult(stringBuilder,addons.size());
                }

                @Override
                public void onDataUnAvailable(String msg, int errorCode) {
                    stringBuilder.append(msg).append('\n');
                    totalCount++;
                    toastResult(stringBuilder,addons.size());
                }
            });
        }

    }

    private void toastResult(StringBuilder stringBuilder,int size){
        if(totalCount>=size) {
            stringBuilder.append("共").append(totalCount).append("条记录,").append("成功提交").append(successCount).append("条");
            ToastUtil.show(getContext(), stringBuilder.toString());
        }
    }

    private static class MyInspectionAdapter extends BaseQuickAdapter<ProdConfirmItemsInfo,BaseViewHolder>{

        public MyInspectionAdapter( @Nullable List<ProdConfirmItemsInfo> data) {
            super(R.layout.item2_inspection2, data);
            this.data = data;
        }

        private List<ProdConfirmItemsInfo> data;

        @Override
        protected void convert(BaseViewHolder helper, ProdConfirmItemsInfo item) {
            helper.setText(R.id.tv2_item_inspection2_no,String.valueOf(helper.getAdapterPosition()));
            helper.setText(R.id.tv2_item_inspection2_project,item.getItem_Name());
            helper.setText(R.id.tv2_item_inspection2_material,item.getRef_Description());
            helper.setText(R.id.tv2_item_inspection2_method,item.getMethod());
            helper.setOnCheckedChangeListener(R.id.checkbox2_item_inspection2_confirm, new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    buttonView.setText(isChecked?"已确认":"未确认");
                    buttonView.setTextColor(isChecked?0xFF00FF00:0xFFFF0000);
                }
            });
        }

        public List<ProdConfirmDetailsAddon> createAddonList(String starttime,String endtime,String date,String orderno){

            String startdatetime = date + 'T' + starttime + ":00";
            String enddatetime = date + 'T' + endtime + ":00";
            int lineno = (int)Math.abs(System.currentTimeMillis());
            List<ProdConfirmDetailsAddon> addonList = new ArrayList<>();
            for(int index = 0;index<data.size();index++) {
                int step = lineno + index;
                ProdConfirmItemsInfo info = data.get(index);
                int viewPosition = index + 1;
                CheckBox checkBox =((CheckBox)getViewByPosition(getRecyclerView(),viewPosition,R.id.checkbox2_item_inspection2_confirm));
                boolean isConfirmed =checkBox.isChecked();
                ProdConfirmDetailsAddon addon = new ProdConfirmDetailsAddon();
                addon.setEnd_Time(enddatetime);
                addon.setStrat_Time(startdatetime);
                addon.setItem_Name(info.getItem_Name());
                addon.setLine_No(step);
                addon.setMethod(info.getMethod());
                addon.setProd_Order_No(orderno);
                addon.setStep(step);
                addon.setValue(String.valueOf(isConfirmed));
                addonList.add(addon);
            }
            return addonList;
        }
    }

    private MyInspectionAdapter adapter;
    private List<ProdConfirmItemsInfo> datalist = new ArrayList<>();

    /**
     * 时间选择
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
                        tv.setText(String.format("%02d:%02d",hourOfDay,minute));
                    }
                }
                // 设置初始时间
                , calendar.get(Calendar.HOUR_OF_DAY)
                , calendar.get(Calendar.MINUTE)
                // true表示采用24小时制
                , true).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inspect_confirm1, container, false);


        tvstarttime  = (TextView) view.findViewById(R.id.tv2_inspection1_time1);
        tvendtime  = (TextView) view.findViewById(R.id.tv2_inspection1_time2);

        tvstarttime.setOnClickListener(onClickListener);
        tvendtime.setOnClickListener(onClickListener);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler2_inspection1);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MyInspectionAdapter(datalist);
        View headerView = inflater.inflate(R.layout.item2_inspection2_header,container,false);
        adapter.addHeaderView(headerView);
        adapter.bindToRecyclerView(recyclerView);
        Calendar calendar = Calendar.getInstance();
        tvDate = (TextView) view.findViewById(R.id.tv2_inspection1_date);

        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        tvDate.setText(sdf1.format(calendar.getTime()));
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
        tvstarttime.setText(sdf2.format(calendar.getTime()));
        tvendtime.setText(sdf2.format(calendar.getTime()));
        return view;
    }

}
