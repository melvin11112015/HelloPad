package com.gki.v107.fragment;


import android.annotation.SuppressLint;
import android.os.AsyncTask;
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
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gki.managerment.LoginUser;
import com.gki.managerment.R;
import com.gki.managerment.bean.ProdMandayList;
import com.gki.managerment.http.Service.getService;
import com.gki.managerment.util.ToastUtil;
import com.gki.v107.entity.ItemVsSpecItemInfo;
import com.gki.v107.entity.ProdConfirmDetailsAddon;
import com.gki.v107.entity.ProdConfirmItemsInfo;
import com.gki.v107.entity.ProdSpecDetailsAddon;
import com.gki.v107.myinterface.FragmentInteractionInterface;
import com.gki.v107.net.ApiTool;
import com.gki.v107.net.BaseOdataCallback;
import com.gki.v107.net.GenericOdataCallback;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InspectConfirm3Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InspectConfirm3Fragment extends Fragment implements FragmentInteractionInterface {


    public InspectConfirm3Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment InspectConfirm1Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InspectConfirm3Fragment newInstance(String param1, String param2) {
        InspectConfirm3Fragment fragment = new InspectConfirm3Fragment();
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

    private TextView tvPartno;

    public void acquireDatas(String orderno){

        if(orderno.isEmpty() || adapter == null)return;

        currentOrderNo = orderno;
        new GetByDocumentNoTask().execute(currentOrderNo);

    }

    private class GetByDocumentNoTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            return getService.GetProdOrderService(params[0]);
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(String result) {
            if (!result.trim().equals("null")) {
                Gson gson = new Gson();
                ProdMandayList bean = gson.fromJson(result, ProdMandayList.class);

                if (!LoginUser.getUser().All_Prod_Line.contains(bean.Production_line + ","))
                {
                    ToastUtil.show(getContext(),"【生产单】不正确：该【生产单】不属于您所在【生产线】！");
                    tvPartno.setText("");
                    return;
                }

                tvPartno.setText(bean.Source_No);

                String filter ="Item_No eq '" + bean.Source_No + "'";

                ApiTool.callItemVsSpecItemList(filter,new GenericOdataCallback<ItemVsSpecItemInfo>() {
                    @Override
                    public void onDataAvailable(List<ItemVsSpecItemInfo> datas) {
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
            else
            {
                ToastUtil.show(getContext(),"找不到该订单，请确认订单是否正确！");
                tvPartno.setText("");
            }
        }
    }

    private int successCount = 0,totalCount = 0;
    private String currentOrderNo = "";

    public void submitDatas(){
        if(adapter == null || tvPartno == null) return;
        final List<ProdSpecDetailsAddon> addons
                = adapter.createAddonList(currentOrderNo);
        if(addons == null || addons.isEmpty())return;

        successCount = 0;
        totalCount = 0;
        final StringBuilder stringBuilder = new StringBuilder();
        for(ProdSpecDetailsAddon addon:addons){
            ApiTool.addProdSpecDetails(addon, new BaseOdataCallback<Map<String, Object>>() {
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

    private static class MyInspectionAdapter extends BaseQuickAdapter<ItemVsSpecItemInfo,BaseViewHolder>{

        public MyInspectionAdapter( @Nullable List<ItemVsSpecItemInfo> data) {
            super(R.layout.item2_inspection3, data);
            this.data = data;
        }

        private List<ItemVsSpecItemInfo> data;

        @Override
        protected void convert(BaseViewHolder helper, ItemVsSpecItemInfo item) {
            helper.setText(R.id.tv2_item_inspection3_no,String.valueOf(helper.getAdapterPosition()));
            helper.setText(R.id.tv2_item_inspection3_property,item.getSpec_Name());
            helper.setText(R.id.tv2_item_inspection3_specification,item.getSpec_Description());

            boolean isBoolType = item.getValue_Type().equals("Boolean");

            helper.setOnCheckedChangeListener(R.id.checkbox2_item_inspection3_confirm1, listener);
            helper.setOnCheckedChangeListener(R.id.checkbox2_item_inspection3_confirm2, listener);
            helper.setOnCheckedChangeListener(R.id.checkbox2_item_inspection3_confirm3, listener);
            helper.setGone(R.id.checkbox2_item_inspection3_confirm1,isBoolType);
            helper.setGone(R.id.checkbox2_item_inspection3_confirm2,isBoolType);
            helper.setGone(R.id.checkbox2_item_inspection3_confirm3,isBoolType);

            helper.setGone(R.id.et2_item_inspection3_value1,!isBoolType);
            helper.setGone(R.id.et2_item_inspection3_value2,!isBoolType);
            helper.setGone(R.id.et2_item_inspection3_value3,!isBoolType);

        }

        private CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setText(isChecked?"OK":"NotOK");
                buttonView.setTextColor(isChecked?0xFF00FF00:0xFFFF0000);
            }
        };

        public List<ProdSpecDetailsAddon> createAddonList(String orderno){

            int lineno = (int)Math.abs(System.currentTimeMillis());
            List<ProdSpecDetailsAddon> addonList = new ArrayList<>();
            for(int index = 0;index<data.size();index++) {
                int step = lineno + index;
                ItemVsSpecItemInfo info = data.get(index);
                ProdSpecDetailsAddon addon = new ProdSpecDetailsAddon();

                int viewPosition = index + 1;

                if(!info.getValue_Type().equals("Boolean")){
                    EditText editText1 =((EditText)getViewByPosition(getRecyclerView(),viewPosition,R.id.et2_item_inspection3_value1));
                    EditText editText2 =((EditText)getViewByPosition(getRecyclerView(),viewPosition,R.id.et2_item_inspection3_value2));
                    EditText editText3 =((EditText)getViewByPosition(getRecyclerView(),viewPosition,R.id.et2_item_inspection3_value3));

                    addon.setValue1(editText1.getText().toString());
                    addon.setValue2(editText2.getText().toString());
                    addon.setValue3(editText3.getText().toString());
                }else{
                    CheckBox checkBox1 =((CheckBox)getViewByPosition(getRecyclerView(),viewPosition,R.id.checkbox2_item_inspection3_confirm1));
                    CheckBox checkBox2 =((CheckBox)getViewByPosition(getRecyclerView(),viewPosition,R.id.checkbox2_item_inspection3_confirm2));
                    CheckBox checkBox3 =((CheckBox)getViewByPosition(getRecyclerView(),viewPosition,R.id.checkbox2_item_inspection3_confirm3));

                    addon.setValue1(String.valueOf(checkBox1.isChecked()));
                    addon.setValue2(String.valueOf(checkBox2.isChecked()));
                    addon.setValue3(String.valueOf(checkBox3.isChecked()));
                }

                addon.setLine_No(step);
                addon.setProd_Order_No(orderno);

                addonList.add(addon);
            }
            return addonList;
        }
    }

    private MyInspectionAdapter adapter;
    private List<ItemVsSpecItemInfo> datalist = new ArrayList<>();
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inspect_confirm3, container, false);


        recyclerView = (RecyclerView) view.findViewById(R.id.recycler2_inspection3);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MyInspectionAdapter(datalist);
        View headerView = inflater.inflate(R.layout.item2_inspection3_header,container,false);
        adapter.addHeaderView(headerView);
        adapter.bindToRecyclerView(recyclerView);

        tvPartno = (TextView) view.findViewById(R.id.tv2_inspection3_partno);

        return view;
    }

}
