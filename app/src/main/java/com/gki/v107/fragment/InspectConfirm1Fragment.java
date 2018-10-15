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
import com.gki.v107.myinterface.FragmentInteractionInterface;
import com.gki.v107.entity.ProdConfirmBomItemsAddon;
import com.gki.v107.entity.WebPordOrderCompInfo;
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
 * Use the {@link InspectConfirm1Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InspectConfirm1Fragment extends Fragment implements FragmentInteractionInterface {


    public InspectConfirm1Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment InspectConfirm1Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InspectConfirm1Fragment newInstance(String param1, String param2) {
        InspectConfirm1Fragment fragment = new InspectConfirm1Fragment();
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

        String filter = "Prod_Order_No eq '" + orderno +"' and Status eq 'Released'";
        ApiTool.callWebPordOrderComp(filter, new GenericOdataCallback<WebPordOrderCompInfo>() {
            @Override
            public void onDataAvailable(List<WebPordOrderCompInfo> datas) {
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

    public void submitDatas(){
        if(adapter == null || tvstarttime == null || tvendtime == null || tvDate == null) return;
        final List<ProdConfirmBomItemsAddon> addons
                = adapter.createAddonList(tvstarttime.getText().toString(),tvendtime.getText().toString(),tvDate.getText().toString());
        if(addons == null || addons.isEmpty()){
            ToastUtil.show(getContext(),"未获取数据");
            return;
        }
        if(tvstarttime.getText().toString().compareTo(tvendtime.getText().toString())>0){
            ToastUtil.show(getContext(),"始业时不能大于终业时");
            return;
        }
        successCount = 0;
        totalCount = 0;
        final StringBuilder stringBuilder = new StringBuilder();
        for(ProdConfirmBomItemsAddon addon:addons){
            ApiTool.addProdConfirmBomItems(addon, new BaseOdataCallback<Map<String, Object>>() {
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
            stringBuilder.append("(构成部件)共").append(totalCount).append("条记录,").append("成功提交").append(successCount).append("条");
            ToastUtil.show(getContext(), stringBuilder.toString());
        }
    }

    private static class MyInspectionAdapter extends BaseQuickAdapter<WebPordOrderCompInfo,BaseViewHolder>{

        public MyInspectionAdapter( @Nullable List<WebPordOrderCompInfo> data) {
            super(R.layout.item2_inspection1, data);
            this.data = data;
        }

        private List<WebPordOrderCompInfo> data;

        @Override
        protected void convert(BaseViewHolder helper, WebPordOrderCompInfo item) {
            helper.setText(R.id.tv2_item_inspection1_no,String.valueOf(helper.getAdapterPosition()));
            helper.setText(R.id.tv2_item_inspection1_code,item.getItem_No());
            helper.setText(R.id.tv2_item_inspection1_name,item.getDescription());
            helper.setText(R.id.tv2_item_inspection1_count,item.getQuantity_Base());
            helper.setOnCheckedChangeListener(R.id.checkbox2_item_inspection1_confirm, new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    buttonView.setText(isChecked?"已确认":"未确认");
                    buttonView.setTextColor(isChecked?0xFF00FF00:0xFFFF0000);
                }
            });
        }

        public List<ProdConfirmBomItemsAddon> createAddonList(String starttime,String endtime,String date){

            String startdatetime = date + 'T' + starttime + ":00";
            String enddatetime = date + 'T' + endtime + ":00";
            //int lineno = (int)Math.abs(System.currentTimeMillis());
            List<ProdConfirmBomItemsAddon> addonList = new ArrayList<>();
            for(int index = 0;index<data.size();index++) {
                //int step = lineno + index;
                WebPordOrderCompInfo info = data.get(index);
                int viewPosition = index + 1;
                CheckBox checkBox =((CheckBox)getViewByPosition(getRecyclerView(),viewPosition,R.id.checkbox2_item_inspection1_confirm));

                if(checkBox == null)return null;

                boolean isConfirmed =checkBox.isChecked();
                addonList.add(new ProdConfirmBomItemsAddon(info.getProd_Order_No(), info.getItem_No(), startdatetime, enddatetime/*, info.getDescription()*/, isConfirmed,info.getLine_No(),info.getLine_No()));
            }
            return addonList;
        }
    }

    private MyInspectionAdapter adapter;
    private List<WebPordOrderCompInfo> datalist = new ArrayList<>();

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
        View headerView = inflater.inflate(R.layout.item2_inspection1_header,container,false);
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
